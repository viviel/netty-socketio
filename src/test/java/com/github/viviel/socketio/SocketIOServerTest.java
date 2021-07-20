package com.github.viviel.socketio;

import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

class SocketIOServerTest {

    public static final int PORT = 8888;

    @Test
    void test1() throws InterruptedException {
        Configuration conf = getConf();
        SocketIOServer s = new SocketIOServer(conf);
        addNS(s);
        s.start();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private Configuration getConf() {
        Configuration conf = new Configuration();
        conf.setPort(PORT);
        conf.setSocketConfig(getSocketConfig());
        conf.setPingInterval(1000 * 3600);
        conf.setPingTimeout(1000 * 3600);
        return conf;
    }

    private SocketConfig getSocketConfig() {
        SocketConfig conf = new SocketConfig();
        conf.setReuseAddress(true);
        return conf;
    }

    private void addNS(SocketIOServer s) {
        SocketIONamespace ns = s.addNamespace("/vv");
        ns.addConnectListener(client -> {
            SocketIONamespace n = client.getNamespace();
            System.out.println(n.getName());
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ns.addEventListener("test", Object.class, ((client, data, ack) -> System.out.println(data)));
                System.out.println("add 'test' event");
            }
        }, 1000 * 20);
    }
}
