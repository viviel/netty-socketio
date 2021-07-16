package com.github.viviel.socketio;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class SocketIOServerTest {

    @Test
    void test1() throws InterruptedException {
        Configuration conf = new Configuration();
        conf.setPort(8888);
        SocketIOServer s = new SocketIOServer(conf);
        s.start();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
