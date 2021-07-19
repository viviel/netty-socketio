package io.netty.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class HashedWheelTimerTest {

    private HashedWheelTimer o;

    @BeforeEach
    void setUp() {
        o = new HashedWheelTimer();
    }

    @Test
    void test1() throws InterruptedException {
        o.newTimeout(t -> {
            System.out.println("1");
            throw new RuntimeException();
        }, 1, TimeUnit.SECONDS);
        o.newTimeout(t -> {
            System.out.println("2");
            throw new RuntimeException();
        }, 2, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
    }
}
