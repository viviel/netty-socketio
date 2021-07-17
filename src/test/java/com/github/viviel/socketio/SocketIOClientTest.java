package com.github.viviel.socketio;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

class SocketIOClientTest {

    @Test
    void test1() throws URISyntaxException, InterruptedException {
        String url = String.format("http://localhost:%s", SocketIOServerTest.PORT);
        Socket socket = IO.socket(url);
        connect(socket);
    }

    private void connect(Socket socket) throws InterruptedException {
        socket.on(Socket.EVENT_CONNECT, args1 -> {
            System.out.println("connected");
        });
        socket.on("message", args -> {
            Ack ack = (Ack) args[args.length - 1];
            System.out.println(Arrays.toString(args));
        });
        socket.connect();
        while (!Thread.interrupted()) {
            TimeUnit.SECONDS.sleep(1);
            if (!socket.connected()) {
                continue;
            }
            socket.emit("message", "message");
        }
    }
}
