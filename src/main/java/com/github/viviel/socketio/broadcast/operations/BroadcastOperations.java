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

import com.github.viviel.socketio.ClientOperations;
import com.github.viviel.socketio.SocketIOClient;
import com.github.viviel.socketio.protocol.Packet;

import java.util.Collection;

/**
 * broadcast interface
 */
public interface BroadcastOperations extends ClientOperations {

    Collection<SocketIOClient> getClients();

    void send(String event, String callback, Object... data);

    void send(String event, SocketIOClient exclude, Object... data);

    void send(String event, String callback, SocketIOClient exclude, Object... data);

    void dispatch(Packet packet);

    void dispatch(String event, Object... data);

    void dispatch(String event, String callback, Object... data);
}
