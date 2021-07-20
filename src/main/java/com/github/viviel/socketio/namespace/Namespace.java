/*
 * Copyright (c) 2012-2019 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.viviel.socketio.namespace;

import com.github.viviel.socketio.*;
import com.github.viviel.socketio.annotation.ScannerEngine;
import com.github.viviel.socketio.broadcast.operations.BroadcastAckCallback;
import com.github.viviel.socketio.broadcast.operations.BroadcastOperations;
import com.github.viviel.socketio.broadcast.operations.BroadcastOperationsFactory;
import com.github.viviel.socketio.function.Runnable;
import com.github.viviel.socketio.function.Supplier;
import com.github.viviel.socketio.interceptor.EventInterceptor;
import com.github.viviel.socketio.listener.*;
import com.github.viviel.socketio.protocol.JsonSupport;
import com.github.viviel.socketio.protocol.Packet;
import com.github.viviel.socketio.protocol.PacketType;
import com.github.viviel.socketio.store.StoreFactory;
import com.github.viviel.socketio.store.pubsub.DispatchMessage;
import com.github.viviel.socketio.store.pubsub.JoinLeaveMessage;
import com.github.viviel.socketio.store.pubsub.PubSubType;
import com.github.viviel.socketio.transport.NamespaceClient;
import io.netty.util.internal.PlatformDependent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Hub object for all clients in one namespace.
 * Namespace shares by different namespace-clients.
 *
 * @see NamespaceClient
 */
public class Namespace implements SocketIONamespace {

    public static final String DEFAULT_NAME = "";

    private final ScannerEngine engine = new ScannerEngine();
    private final ConcurrentMap<String, EventEntry<?>> eventListeners = PlatformDependent.newConcurrentHashMap();
    private final Queue<ConnectListener> connectListeners = new ConcurrentLinkedQueue<>();
    private final Queue<DisconnectListener> disconnectListeners = new ConcurrentLinkedQueue<>();
    private final Queue<PingListener> pingListeners = new ConcurrentLinkedQueue<>();
    private final Queue<EventInterceptor> eventInterceptors = new ConcurrentLinkedQueue<>();
    private final ConcurrentMap<String, BroadcastAckCallback<?>> broadcastCallback = PlatformDependent.newConcurrentHashMap();

    private final Map<UUID, SocketIOClient> allClients = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<String, Set<UUID>> roomClients = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<UUID, Set<String>> clientRooms = PlatformDependent.newConcurrentHashMap();

    private final String name;
    private final AckMode ackMode;
    private final JsonSupport jsonSupport;
    private final StoreFactory storeFactory;
    private final BroadcastOperationsFactory broadcastOperationsFactory;
    private final ExceptionListener exceptionListener;

    public Namespace(String name, Configuration configuration) {
        super();
        this.name = name;
        this.jsonSupport = configuration.getJsonSupport();
        this.storeFactory = configuration.getStoreFactory();
        this.broadcastOperationsFactory = configuration.getBroadcastOperationsFactory();
        this.exceptionListener = configuration.getExceptionListener();
        this.ackMode = configuration.getAckMode();
    }

    public void addClient(SocketIOClient client) {
        allClients.put(client.getSessionId(), client);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addMultiTypeEventListener(String event, MultiTypeEventListener listener, Class<?>... dataClass) {
        EventEntry entry = getEventEntry(event);
        entry.addListener(listener);
        jsonSupport.addEventMapping(name, event, dataClass);
    }

    @Override
    public void removeAllListeners(String event) {
        EventEntry<?> entry = eventListeners.remove(event);
        if (entry != null) {
            jsonSupport.removeEventMapping(name, event);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void addEventListener(String event, Class<T> dataClass, DataListener<T> listener) {
        EventEntry entry = getEventEntry(event);
        entry.addListener(listener);
        jsonSupport.addEventMapping(name, event, dataClass);
    }

    private EventEntry<?> getEventEntry(String event) {
        EventEntry<?> entry = eventListeners.get(event);
        if (entry == null) {
            entry = new EventEntry<>();
            EventEntry<?> oldEntry = eventListeners.putIfAbsent(event, entry);
            if (oldEntry != null) {
                entry = oldEntry;
            }
        }
        return entry;
    }

    @Override
    public <T> void addGlobalEventListener(String event, Class<T> dataClass, GlobalDataListener listener) {
        EventEntry<?> entry = getEventEntry(event);
        entry.addGlobalListener(listener);
    }

    @Override
    public void addEventInterceptor(EventInterceptor interceptor) {
        eventInterceptors.add(interceptor);
    }

    @SuppressWarnings({"rawtypes"})
    public void onEvent(NamespaceClient client, String event, List<Object> args, AckRequest ackRequest) {
        EventEntry entry = eventListeners.get(event);
        if (entry == null) {
            return;
        }
        boolean keep = processInterceptors(client, event, args, ackRequest);
        if (!keep) {
            return;
        }
        processListeners(client, event, args, ackRequest);
        processGlobalListeners(event, args);
        sendAck(ackRequest);
    }

    private void withCatch(List<Object> args, Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            exceptionListener.onEventException(e, args);
        }
    }

    private <T> T withCatch(List<Object> args, Supplier<T> s) {
        try {
            return s.get();
        } catch (Exception e) {
            exceptionListener.onEventException(e, args);
            return null;
        }
    }

    private boolean processInterceptors(NamespaceClient client, String event,
                                        List<Object> args, AckRequest ackRequest) {
        for (EventInterceptor interceptor : eventInterceptors) {
            Boolean keep = withCatch(args, () -> interceptor.onEvent(client, event, args, ackRequest));
            if (keep == null || !keep) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void processListeners(NamespaceClient client, String event, List<Object> args, AckRequest ackRequest) {
        EventEntry entry = eventListeners.get(event);
        Queue<DataListener> listeners = entry.getListeners();
        for (DataListener listener : listeners) {
            Object data = getEventData(args, listener);
            withCatch(args, () -> listener.onData(client, data, ackRequest));
        }
    }

    public void processGlobalListeners(String event, List<Object> args) {
        EventEntry<?> eventEntry = eventListeners.get(event);
        Queue<GlobalDataListener> listeners = eventEntry.getGlobalListeners();
        if (listeners.isEmpty()) {
            return;
        }
        publish(event, args);
        for (GlobalDataListener l : listeners) {
            withCatch(args, () -> l.onData(event, args));
        }
    }

    private void publish(String event, List<Object> args) {
        Packet p = new Packet(PacketType.MESSAGE);
        p.setSubType(PacketType.EVENT);
        p.setName(event);
        p.setName(name);
        p.setData(args);
        publish(p);
    }

    private void publish(Packet packet) {
        this.storeFactory.pubSubStore().publish(PubSubType.DISPATCH, new DispatchMessage(packet));
    }

    private void sendAck(AckRequest ackRequest) {
        if (ackMode == AckMode.AUTO || ackMode == AckMode.AUTO_SUCCESS_ONLY) {
            // send ack response if it not executed
            // during {@link DataListener#onData} invocation
            ackRequest.sendAckData(Collections.emptyList());
        }
    }

    private Object getEventData(List<Object> args, DataListener<?> dataListener) {
        if (dataListener instanceof MultiTypeEventListener) {
            return new MultiTypeArgs(args);
        } else {
            if (!args.isEmpty()) {
                return args.get(0);
            }
        }
        return null;
    }

    @Override
    public void addDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    public void onDisconnect(SocketIOClient client) {
        Set<String> joinedRooms = client.getAllRooms();
        allClients.remove(client.getSessionId());

        // client must leave all rooms and publish the leave msg one by one on disconnect.
        for (String joinedRoom : joinedRooms) {
            leave(roomClients, joinedRoom, client.getSessionId());
            storeFactory.pubSubStore().publish(PubSubType.LEAVE, new JoinLeaveMessage(client.getSessionId(), joinedRoom, getName()));
        }
        clientRooms.remove(client.getSessionId());

        try {
            for (DisconnectListener listener : disconnectListeners) {
                listener.onDisconnect(client);
            }
        } catch (Exception e) {
            exceptionListener.onDisconnectException(e, client);
        }
    }

    @Override
    public void addConnectListener(ConnectListener listener) {
        connectListeners.add(listener);
    }

    public void onConnect(SocketIOClient client) {
        join(getName(), client.getSessionId());
        storeFactory.pubSubStore().publish(PubSubType.JOIN, new JoinLeaveMessage(client.getSessionId(), getName(), getName()));

        try {
            for (ConnectListener listener : connectListeners) {
                listener.onConnect(client);
            }
        } catch (Exception e) {
            exceptionListener.onConnectException(e, client);
        }
    }

    @Override
    public void addPingListener(PingListener listener) {
        pingListeners.add(listener);
    }

    public void onPing(SocketIOClient client) {
        try {
            for (PingListener listener : pingListeners) {
                listener.onPing(client);
            }
        } catch (Exception e) {
            exceptionListener.onPingException(e, client);
        }
    }

    @Override
    public BroadcastOperations getBroadcastOperations() {
        return broadcastOperationsFactory.getBroadcastOperations(
                getName(), getName(), allClients.values(), storeFactory, broadcastCallback
        );
    }

    @Override
    public BroadcastOperations getRoomOperations(String room) {
        return broadcastOperationsFactory.getBroadcastOperations(
                getName(), room, getRoomClients(room), storeFactory, broadcastCallback
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Namespace other = (Namespace) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public void addListeners(Object listener) {
        addListeners(listener, listener.getClass());
    }

    @Override
    public void addListeners(Object listener, Class<?> listenersClass) {
        engine.scan(this, listener, listenersClass);
    }

    public void joinRoom(String room, UUID sessionId) {
        join(room, sessionId);
        storeFactory.pubSubStore().publish(PubSubType.JOIN, new JoinLeaveMessage(sessionId, room, getName()));
    }

    private <K, V> void join(ConcurrentMap<K, Set<V>> map, K key, V value) {
        Set<V> clients = map.get(key);
        if (clients == null) {
            clients = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
            Set<V> oldClients = map.putIfAbsent(key, clients);
            if (oldClients != null) {
                clients = oldClients;
            }
        }
        clients.add(value);
        // object may be changed due to other concurrent call
        if (clients != map.get(key)) {
            // re-join if queue has been replaced
            join(map, key, value);
        }
    }

    public void join(String room, UUID sessionId) {
        join(roomClients, room, sessionId);
        join(clientRooms, sessionId, room);
    }

    public void leaveRoom(String room, UUID sessionId) {
        leave(room, sessionId);
        storeFactory.pubSubStore().publish(PubSubType.LEAVE, new JoinLeaveMessage(sessionId, room, getName()));
    }

    private <K, V> void leave(ConcurrentMap<K, Set<V>> map, K room, V sessionId) {
        Set<V> clients = map.get(room);
        if (clients == null) {
            return;
        }
        clients.remove(sessionId);

        if (clients.isEmpty()) {
            map.remove(room, Collections.emptySet());
        }
    }

    public void leave(String room, UUID sessionId) {
        leave(roomClients, room, sessionId);
        leave(clientRooms, sessionId, room);
    }

    public Set<String> getRooms(SocketIOClient client) {
        Set<String> res = clientRooms.get(client.getSessionId());
        if (res == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(res);
    }

    public Set<String> getRooms() {
        return roomClients.keySet();
    }

    public Iterable<SocketIOClient> getRoomClients(String room) {
        Set<UUID> sessionIds = roomClients.get(room);

        if (sessionIds == null) {
            return Collections.emptyList();
        }

        List<SocketIOClient> result = new ArrayList<>();
        for (UUID sessionId : sessionIds) {
            SocketIOClient client = allClients.get(sessionId);
            if (client != null) {
                result.add(client);
            }
        }
        return result;
    }

    @Override
    public Collection<SocketIOClient> getAllClients() {
        return Collections.unmodifiableCollection(allClients.values());
    }

    public JsonSupport getJsonSupport() {
        return jsonSupport;
    }

    @Override
    public SocketIOClient getClient(UUID uuid) {
        return allClients.get(uuid);
    }

    @Override
    public void addBroadcastAckCallback(String ackName, BroadcastAckCallback<Object> ack) {
        broadcastCallback.put(ackName, ack);
    }
}
