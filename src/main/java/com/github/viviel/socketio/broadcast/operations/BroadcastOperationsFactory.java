package com.github.viviel.socketio.broadcast.operations;

import com.github.viviel.socketio.SocketIOClient;
import com.github.viviel.socketio.store.StoreFactory;

import java.util.concurrent.ConcurrentMap;

public interface BroadcastOperationsFactory {

    BroadcastOperations getBroadcastOperations(
            String namespace, String room,
            Iterable<SocketIOClient> clients,
            StoreFactory storeFactory,
            ConcurrentMap<String, BroadcastAckCallback<?>> broadcastAck
    );
}
