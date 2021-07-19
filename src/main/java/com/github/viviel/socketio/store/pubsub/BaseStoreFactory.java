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
package com.github.viviel.socketio.store.pubsub;

import com.github.viviel.socketio.handler.AuthorizeHandler;
import com.github.viviel.socketio.handler.ClientHead;
import com.github.viviel.socketio.namespace.Namespace;
import com.github.viviel.socketio.namespace.NamespacesHub;
import com.github.viviel.socketio.protocol.JsonSupport;
import com.github.viviel.socketio.protocol.Packet;
import com.github.viviel.socketio.store.StoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class BaseStoreFactory implements StoreFactory {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Long nodeId = (long) (Math.random() * 1000000);

    protected Long getNodeId() {
        return nodeId;
    }

    @Override
    public void init(NamespacesHub namespacesHub, AuthorizeHandler authorizeHandler, JsonSupport jsonSupport) {
        PubSubStore store = pubSubStore();
        store.subscribe(PubSubType.CONNECT, msg -> initConnect(authorizeHandler, msg), ConnectMessage.class);
        store.subscribe(PubSubType.DISCONNECT, this::initDisconnect, DisconnectMessage.class);
        store.subscribe(PubSubType.DISPATCH, msg -> initDispatch(namespacesHub, msg), DispatchMessage.class);
        store.subscribe(PubSubType.JOIN, msg -> initJoin(namespacesHub, msg), JoinLeaveMessage.class);
        store.subscribe(PubSubType.LEAVE, msg -> initLeave(namespacesHub, msg), JoinLeaveMessage.class);
    }

    protected void initConnect(AuthorizeHandler authorizeHandler, ConnectMessage msg) {
        authorizeHandler.connect(msg.getSessionId());
        log.debug("{} sessionId: {}", PubSubType.CONNECT, msg.getSessionId());
    }

    protected void initDisconnect(DisconnectMessage msg) {
        UUID sessionId = msg.getSessionId();
        log.debug("{} sessionId: {}", PubSubType.DISCONNECT, sessionId);
    }

    protected void initDispatch(NamespacesHub namespacesHub, DispatchMessage msg) {
        String room = msg.getRoom();
        String namespaceName = msg.getNamespace();
        Packet packet = msg.getPacket();
        String event = packet.getName();
        Object data = packet.getData();
        Namespace namespace = namespacesHub.get(namespaceName);
        if (namespace == null) {
            log.error("{}, could not find namespace. package: {}", PubSubType.DISPATCH, packet);
            return;
        }
        namespace.dispatch(room, packet);
        namespace.processGlobalListeners(event, data);
        log.debug("{} packet: {}", PubSubType.DISPATCH, packet);
    }

    protected void initJoin(NamespacesHub namespacesHub, JoinLeaveMessage msg) {
        String room = msg.getRoom();
        String namespaceName = msg.getNamespace();
        Namespace namespace = namespacesHub.get(namespaceName);
        if (namespace == null) {
            log.error("{}, could not find namespace. sessionId: {}", PubSubType.JOIN, msg.getSessionId());
            return;
        }
        namespace.join(room, msg.getSessionId());
        log.debug("{} sessionId: {}", PubSubType.JOIN, msg.getSessionId());
    }

    protected void initLeave(NamespacesHub namespacesHub, JoinLeaveMessage msg) {
        String room = msg.getRoom();
        String namespaceName = msg.getNamespace();
        Namespace namespace = namespacesHub.get(namespaceName);
        if (namespace == null) {
            log.error("{}, could not find namespace. sessionId: {}", PubSubType.LEAVE, msg.getSessionId());
            return;
        }
        namespace.leave(room, msg.getSessionId());
        log.debug("{} sessionId: {}", PubSubType.LEAVE, msg.getSessionId());
    }

    @Override
    public abstract PubSubStore pubSubStore();

    @Override
    public void onDisconnect(ClientHead client) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (distributed session store, distributed publish/subscribe)";
    }
}
