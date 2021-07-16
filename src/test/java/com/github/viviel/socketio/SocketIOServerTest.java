package com.github.viviel.socketio;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class SocketIOServerTest {

    public static final int PORT = 8888;

    @Test
    void test1() throws InterruptedException {
        Configuration conf = new Configuration();
        conf.setPort(PORT);
        SocketIOServer s = new SocketIOServer(conf);
        s.start();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
