package com.github.viviel.socketio.broadcast.operations;

import com.github.viviel.socketio.SocketIOClient;
import com.github.viviel.socketio.store.StoreFactory;

import java.util.concurrent.ConcurrentMap;

public class SimpleBroadcastOperationsFactory implements BroadcastOperationsFactory {

    @Override
    public BroadcastOperations getBroadcastOperations(
            String namespace, String room, Iterable<SocketIOClient> clients, StoreFactory storeFactory,
            ConcurrentMap<String, BroadcastAckCallback<?>> broadcastAck
    ) {
        return new SingleRoomBroadcastOperations(namespace, room, clients, storeFactory, broadcastAck);
    }
}
