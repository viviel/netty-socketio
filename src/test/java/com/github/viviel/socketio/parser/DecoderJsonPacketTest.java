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
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public class DecoderJsonPacketTest extends DecoderBaseTest {

    @Test
    public void testUTF8Decode() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("4:::\"Привет\"", CharsetUtil.UTF_8), null);
//        Assertions.assertEquals(PacketType.JSON, packet.getType());
        Assertions.assertEquals("Привет", packet.getData());
    }

    @Test
    public void testDecode() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("4:::\"2\"", CharsetUtil.UTF_8), null);
//        Assertions.assertEquals(PacketType.JSON, packet.getType());
        Assertions.assertEquals("2", packet.getData());
    }

    @Test
    public void testDecodeWithMessageIdAndAckData() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("4:1+::{\"a\":\"b\"}", CharsetUtil.UTF_8), null);
//        Assertions.assertEquals(PacketType.JSON, packet.getType());
//        Assertions.assertEquals(1, (long)packet.getId());
//        Assertions.assertEquals(Packet.ACK_DATA, packet.getAck());

        Map obj = (Map) packet.getData();
        Assertions.assertEquals("b", obj.get("a"));
        Assertions.assertEquals(1, obj.size());
    }

}
