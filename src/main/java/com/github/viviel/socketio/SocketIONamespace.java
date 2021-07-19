/**
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
package com.github.viviel.socketio;

import com.github.viviel.socketio.broadcast.operations.BroadcastAckCallback;
import com.github.viviel.socketio.broadcast.operations.BroadcastOperations;
import com.github.viviel.socketio.listener.EventListeners;

import java.util.Collection;
import java.util.UUID;

/**
 * Fully thread-safe.
 *
 */
public interface SocketIONamespace extends EventListeners {

    String getName();

    BroadcastOperations getBroadcastOperations();

    BroadcastOperations getRoomOperations(String room);

    /**
     * Get all clients connected to namespace
     *
     * @return collection of clients
     */
    Collection<SocketIOClient> getAllClients();

    /**
     * Get client by uuid connected to namespace
     *
     * @param uuid - id of client
     * @return client
     */
    SocketIOClient getClient(UUID uuid);

    void addBroadcastAckCallback(String ackName, BroadcastAckCallback<Object> ack);
}
