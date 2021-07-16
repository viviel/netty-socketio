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

import com.github.viviel.socketio.protocol.Packet;
import com.github.viviel.socketio.protocol.PacketType;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DecoderConnectionPacketTest extends DecoderBaseTest {

    @Test
    public void testDecodeHeartbeat() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("2:::", CharsetUtil.UTF_8), null);
//        Assertions.assertEquals(PacketType.HEARTBEAT, packet.getType());
    }

    @Test
    public void testDecode() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("1::/tobi", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.CONNECT, packet.getType());
        Assertions.assertEquals("/tobi", packet.getNsp());
    }

    @Test
    public void testDecodeWithQueryString() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("1::/test:?test=1", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.CONNECT, packet.getType());
        Assertions.assertEquals("/test", packet.getNsp());
//        Assertions.assertEquals("?test=1", packet.getQs());
    }

    @Test
    public void testDecodeDisconnection() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("0::/woot", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.DISCONNECT, packet.getType());
        Assertions.assertEquals("/woot", packet.getNsp());
    }

}
