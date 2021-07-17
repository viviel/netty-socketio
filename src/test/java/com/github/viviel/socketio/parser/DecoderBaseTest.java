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
package com.github.viviel.socketio.parser;

import com.github.viviel.socketio.ack.AckManager;
import com.github.viviel.socketio.protocol.JacksonJsonSupport;
import com.github.viviel.socketio.protocol.PacketDecoder;
import mockit.Mocked;
import org.junit.jupiter.api.BeforeEach;


public class DecoderBaseTest {

    @Mocked
    protected AckManager ackManager;

    protected PacketDecoder decoder;

    @BeforeEach
    public void before() {
        decoder = new PacketDecoder(new JacksonJsonSupport(), ackManager);
    }
}
