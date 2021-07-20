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
package com.github.viviel.socketio;

import com.github.viviel.socketio.transport.PollingTransport;
import com.github.viviel.socketio.transport.WebSocketTransport;

public enum TransportType {

    WEBSOCKET(WebSocketTransport.NAME),
    POLLING(PollingTransport.NAME);

    private final String value;

    TransportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransportType byName(String value) {
        for (TransportType t : TransportType.values()) {
            if (t.getValue().equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Can't find " + value + " transport");
    }
}
