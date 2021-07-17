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
package com.github.viviel.socketio.broadcast.operations;

import com.github.viviel.socketio.SocketIOClient;
import com.github.viviel.socketio.misc.IterableCollection;
import com.github.viviel.socketio.protocol.Packet;
import com.github.viviel.socketio.protocol.PacketType;
import com.github.viviel.socketio.store.StoreFactory;
import com.github.viviel.socketio.store.pubsub.DispatchMessage;
import com.github.viviel.socketio.store.pubsub.PubSubType;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public class SingleRoomBroadcastOperations implements BroadcastOperations {

    private final String namespace;
    private final String room;
    private final Iterable<SocketIOClient> clients;
    private final StoreFactory storeFactory;
    private final ConcurrentMap<String, BroadcastAckCallback<Object>> broadcastAck;

    public SingleRoomBroadcastOperations(
            String namespace, String room, Iterable<SocketIOClient> clients, StoreFactory storeFactory,
            ConcurrentMap<String, BroadcastAckCallback<Object>> broadcastAck
    ) {
        super();
        this.namespace = namespace;
        this.room = room;
        this.clients = clients;
        this.storeFactory = storeFactory;
        this.broadcastAck = broadcastAck;
    }

    private void push(Packet packet) {
        this.storeFactory.pubSubStore().publish(
                PubSubType.DISPATCH,
                new DispatchMessage(this.room, packet, this.namespace));
    }

    @Override
    public Collection<SocketIOClient> getClients() {
        return new IterableCollection<>(clients);
    }

    @Override
    public void send(Packet packet) {
        dispatch(packet);
        push(packet);
    }

    @Override
    public void disconnect() {
        for (SocketIOClient client : clients) {
            client.disconnect();
        }
    }

    @Override
    public void send(String event, SocketIOClient exclude, Object... data) {
        Packet packet = new Packet(PacketType.MESSAGE);
        packet.setSubType(PacketType.EVENT);
        packet.setName(event);
        packet.setData(Arrays.asList(data));
        doDispatch(packet, exclude);
        push(packet);
    }

    @Override
    public void send(String event, Object... data) {
        Packet packet = new Packet(PacketType.MESSAGE);
        packet.setSubType(PacketType.EVENT);
        packet.setName(event);
        packet.setData(Arrays.asList(data));
        send(packet);
    }

    @Override
    public void dispatch(Packet packet) {
        doDispatch(packet, null);
    }

    private void doDispatch(Packet packet, SocketIOClient exclude) {
        String event = packet.getName();
        Object data = packet.getData();
        BroadcastAckCallback<Object> ack = broadcastAck.get(event);
        if (ack == null) {
            for (SocketIOClient client : clients) {
                if (exclude != null && client.getSessionId().equals(exclude.getSessionId())) {
                    continue;
                }
                client.send(packet);
            }
        } else {
            for (SocketIOClient client : clients) {
                if (exclude != null && client.getSessionId().equals(exclude.getSessionId())) {
                    continue;
                }
                client.send(packet, ack.createClientCallback(client, data));
            }
            ack.loopFinished();
        }
    }

    @Override
    public void dispatch(String event, Object... data) {
        Packet packet = new Packet(PacketType.MESSAGE);
        packet.setSubType(PacketType.EVENT);
        packet.setName(event);
        packet.setData(Arrays.asList(data));
        dispatch(packet);
    }
}