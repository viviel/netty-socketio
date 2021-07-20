package com.github.viviel.socketio.function;

@FunctionalInterface
public interface Supplier<T> {

    T get() throws Exception;
}
