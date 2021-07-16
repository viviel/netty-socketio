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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class EncoderConnectionPacketTest extends EncoderBaseTest {

    @Test
    public void testEncodeHeartbeat() throws IOException {
//        Packet packet = new Packet(PacketType.HEARTBEAT);
//        ByteBuf result = Unpooled.buffer();
//        encoder.encodePacket(packet, result);
//        Assertions.assertEquals("2::", result.toString(CharsetUtil.UTF_8));
    }

    @Test
    public void testEncodeDisconnection() throws IOException {
        Packet packet = new Packet(PacketType.DISCONNECT);
        packet.setNsp("/woot");
        ByteBuf result = Unpooled.buffer();
//        encoder.encodePacket(packet, result);
        Assertions.assertEquals("0::/woot", result.toString(CharsetUtil.UTF_8));
    }

    @Test
    public void testEncode() throws IOException {
        Packet packet = new Packet(PacketType.CONNECT);
        packet.setNsp("/tobi");
        ByteBuf result = Unpooled.buffer();
//        encoder.encodePacket(packet, result);
        Assertions.assertEquals("1::/tobi", result.toString(CharsetUtil.UTF_8));
    }

    @Test
    public void testEncodePacketWithQueryString() throws IOException {
        Packet packet = new Packet(PacketType.CONNECT);
        packet.setNsp("/test");
//        packet.setQs("?test=1");
        ByteBuf result = Unpooled.buffer();
//        encoder.encodePacket(packet, result);
        Assertions.assertEquals("1::/test:?test=1", result.toString(CharsetUtil.UTF_8));
    }

}
