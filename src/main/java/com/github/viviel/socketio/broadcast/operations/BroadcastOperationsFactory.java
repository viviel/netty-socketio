package com.github.viviel.socketio.broadcast.operations;

import com.github.viviel.socketio.SocketIOClient;
import com.github.viviel.socketio.store.StoreFactory;

public interface BroadcastOperationsFactory {

    BroadcastOperations getBroadcastOperations(
            String namespace, String room,
            Iterable<SocketIOClient> clients,
            StoreFactory storeFactory
    );
}
