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
import com.github.viviel.socketio.protocol.Packet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MultiRoomBroadcastOperations implements BroadcastOperations {

    private final Collection<BroadcastOperations> broadcastOperations;

    public MultiRoomBroadcastOperations(Collection<BroadcastOperations> broadcastOperations) {
        this.broadcastOperations = broadcastOperations;
    }

    @Override
    public void disconnect() {
        for (BroadcastOperations b : this.broadcastOperations) {
            b.disconnect();
        }
    }

    @Override
    public void send(Packet packet) {
        for (BroadcastOperations b : this.broadcastOperations) {
            b.send(packet);
        }
    }

    @Override
    public void send(String event, Object... data) {
        for (BroadcastOperations b : this.broadcastOperations) {
            b.send(event, data);
        }
    }

    @Override
    public Collection<SocketIOClient> getClients() {
        Set<SocketIOClient> clients = new HashSet<>();
        for (BroadcastOperations b : this.broadcastOperations) {
            clients.addAll(b.getClients());
        }
        return clients;
    }

    @Override
    public void send(String event, String callback, Object... data) {
        for (BroadcastOperations b : this.broadcastOperations) {
            b.send(event, callback, data);
        }
    }

    @Override
    public void send(String event, SocketIOClient exclude, Object... data) {
        for (BroadcastOperations b : this.broadcastOperations) {
            b.send(event, exclude, data);
        }
    }

    @Override
    public void send(String event, String callback, SocketIOClient exclude, Object... data) {
        for (BroadcastOperations b : this.broadcastOperations) {
            b.send(event, callback, exclude, data);
        }
    }
}
