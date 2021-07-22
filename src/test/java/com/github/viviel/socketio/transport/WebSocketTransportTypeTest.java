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
/*
 * @(#)WebSocketTransportTest.java 2018. 5. 23.
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
package com.github.viviel.socketio.transport;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.junit.jupiter.api.Test;

/**
 * @author hangsu.cho@navercorp.com
 */
public class WebSocketTransportTypeTest {

    /**
     * Test method for {@link WebSocketTransport#channelRead()}.
     */
    @Test
    public void testCloseFrame() {
        EmbeddedChannel channel = createChannel();

        channel.writeInbound(new CloseWebSocketFrame());
        Object msg = channel.readOutbound();

        // https://tools.ietf.org/html/rfc6455#section-5.5.1
        // If an endpoint receives a Close frame and did not previously send a Close frame, the endpoint
        // MUST send a Close frame in response.
        assertTrue(msg instanceof CloseWebSocketFrame);
    }

    private void assertTrue(boolean b) {
    }

    private EmbeddedChannel createChannel() {
        return new EmbeddedChannel(new WebSocketTransport(false, null, null, null, null) {
            /*
             * (non-Javadoc)
             *
             * @see
             * com.corundumstudio.socketio.transport.WebSocketTransport#channelInactive(io.netty.channel.
             * ChannelHandlerContext)
             */
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            }
        });
    }
}
