package com.github.viviel.socketio.netty;

import com.github.viviel.socketio.Configuration;
import com.github.viviel.socketio.protocol.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SocketIOTest {

    private PacketEncoder encoder;

    @BeforeEach
    void setUp() {
        Configuration conf = new Configuration();
        JsonSupport jsonSupport = new JacksonJsonSupport();
        encoder = new PacketEncoder(conf, jsonSupport);
    }

    @Test
    void test1() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer());
        b.bind(8888);
        TimeUnit.DAYS.sleep(Integer.MAX_VALUE);
    }

    private ChannelInitializer<Channel> channelInitializer() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("HttpRequestDecoder", new HttpRequestDecoder());
                pipeline.addLast("HttpResponseEncoder", new HttpResponseEncoder());
                pipeline.addLast("readChannelHandler", readChannelHandler());
                pipeline.addLast("writeChannelHandler", writeChannelHandler());
            }
        };
    }

    private ChannelInboundHandlerAdapter readChannelHandler() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof HttpRequest) {
                    httpRequest(ctx, (HttpRequest) msg);
                } else if (msg instanceof HttpContent) {
                    httpContent((HttpContent) msg);
                } else {
                    super.channelRead(ctx, msg);
                }
            }
        };
    }

    private void httpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        AuthPacket authPacket = new AuthPacket(
                UUID.randomUUID(), new String[]{"websocket"}, 1000_0, 1000_0
        );
        Packet packet = new Packet(PacketType.OPEN);
        packet.setData(authPacket);
        ctx.channel().writeAndFlush(packet);
    }

    private void httpContent(HttpContent msg) {
    }

    private ChannelOutboundHandlerAdapter writeChannelHandler() {
        return new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                HttpHeaders headers = res.headers();
                headers.add(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

                ByteBuf out = ctx.alloc().heapBuffer();
                encoder.encodePacket((Packet) msg, out, ctx.alloc(), false);

                HttpUtil.setContentLength(res, out.readableBytes());

//                Channel channel = ctx.channel();
//                channel.write(res);
//                channel.write(new DefaultHttpContent(out));
//                channel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT, promise)
//                        .addListener(ChannelFutureListener.CLOSE);
                ctx.write(res);
                ctx.write(new DefaultHttpContent(out));
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT, promise)
                        .addListener(ChannelFutureListener.CLOSE);
            }
        };
    }
}
