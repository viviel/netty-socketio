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
package com.github.viviel.socketio.messages;

import com.github.viviel.socketio.TransportType;
import com.github.viviel.socketio.handler.ClientHead;
import io.netty.buffer.ByteBuf;

public class InPacketMessage {

    private final ClientHead client;
    private final ByteBuf content;
    private final TransportType transportType;

    public InPacketMessage(ClientHead client, ByteBuf content, TransportType transportType) {
        this.client = client;
        this.content = content;
        this.transportType = transportType;
    }

    public TransportType getTransport() {
        return transportType;
    }

    public ClientHead getClient() {
        return client;
    }

    public ByteBuf getContent() {
        return content;
    }
}
