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
package com.github.viviel.socketio.listener;

import com.github.viviel.socketio.AckRequest;
import com.github.viviel.socketio.SocketIOClient;

public interface DataListener<T> {

    /**
     * Invokes when data object received from client
     *
     * @param client    - receiver
     * @param data      - received object
     * @param ack - ack request
     */
    void onData(SocketIOClient client, T data, AckRequest ack) throws Exception;
}
