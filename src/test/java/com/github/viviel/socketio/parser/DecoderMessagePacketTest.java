/*
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
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

public class DecoderMessagePacketTest extends DecoderBaseTest {

    @Test
    public void testDecodeId() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("3:1::asdfasdf", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.MESSAGE, packet.getType());
//        Assertions.assertEquals(1, (long)packet.getId());
//        Assertions.assertTrue(packet.getArgs().isEmpty());
//        Assertions.assertTrue(packet.getAck().equals(Boolean.TRUE));
    }

    @Test
    public void testDecode() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("3:::woot", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.MESSAGE, packet.getType());
        Assertions.assertEquals("woot", packet.getData());
    }

    @Test
    public void testDecodeWithIdAndEndpoint() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("3:5:/tobi", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.MESSAGE, packet.getType());
//        Assertions.assertEquals(5, (long)packet.getId());
//        Assertions.assertEquals(true, packet.getAck());
        Assertions.assertEquals("/tobi", packet.getNsp());
    }

}
