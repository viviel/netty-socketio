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

import com.github.viviel.socketio.protocol.JacksonJsonSupport;
import com.github.viviel.socketio.protocol.Packet;
import com.github.viviel.socketio.protocol.PacketDecoder;
import com.github.viviel.socketio.protocol.PacketType;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

public class DecoderEventPacketTest extends DecoderBaseTest {

    @Test
    public void testDecode() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("5:::{\"name\":\"woot\"}", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.EVENT, packet.getType());
        Assertions.assertEquals("woot", packet.getName());
    }

    @Test
    public void testDecodeWithMessageIdAndAck() throws IOException {
        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("5:1+::{\"name\":\"tobi\"}", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.EVENT, packet.getType());
//        Assertions.assertEquals(1, (long)packet.getId());
//        Assertions.assertEquals(Packet.ACK_DATA, packet.getAck());
        Assertions.assertEquals("tobi", packet.getName());
    }

    @Test
    public void testDecodeWithData() throws IOException {
        JacksonJsonSupport jsonSupport = new JacksonJsonSupport();
        jsonSupport.addEventMapping("", "edwald", HashMap.class, Integer.class, String.class);
        PacketDecoder decoder = new PacketDecoder(jsonSupport, ackManager);

        Packet packet = decoder.decodePackets(Unpooled.copiedBuffer("5:::{\"name\":\"edwald\",\"args\":[{\"a\": \"b\"},2,\"3\"]}", CharsetUtil.UTF_8), null);
        Assertions.assertEquals(PacketType.EVENT, packet.getType());
        Assertions.assertEquals("edwald", packet.getName());
//        Assertions.assertEquals(3, packet.getArgs().size());
//        Map obj = (Map) packet.getArgs().get(0);
//        Assertions.assertEquals("b", obj.get("a"));
//        Assertions.assertEquals(2, packet.getArgs().get(1));
//        Assertions.assertEquals("3", packet.getArgs().get(2));
    }

}
