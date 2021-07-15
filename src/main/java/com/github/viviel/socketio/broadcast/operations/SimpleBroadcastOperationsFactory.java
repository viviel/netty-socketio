package com.github.viviel.socketio.broadcast.operations;

import com.github.viviel.socketio.SocketIOClient;
import com.github.viviel.socketio.store.StoreFactory;

public class SimpleBroadcastOperationsFactory implements BroadcastOperationsFactory {

    @Override
    public BroadcastOperations getBroadcastOperations(String namespace, String room,
                                                      Iterable<SocketIOClient> clients,
                                                      StoreFactory storeFactory) {
        return new SingleRoomBroadcastOperations(namespace, room, clients, storeFactory);
    }
}
