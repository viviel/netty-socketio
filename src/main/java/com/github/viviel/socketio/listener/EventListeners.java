/*
 * Copyright (c) 2012-2019 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.viviel.socketio.listener;

public interface EventListeners {

    void addMultiTypeEventListener(String event, MultiTypeEventListener listener, Class<?>... dataClass);

    <T> void addEventListener(String event, Class<T> dataClass, DataListener<T> listener);

    <T> void addGlobalEventListener(String event, Class<T> dataClass, GlobalDataListener listener);

    void addDisconnectListener(DisconnectListener listener);

    void addConnectListener(ConnectListener listener);

    void addPingListener(PingListener listener);

    void addListeners(Object listener);

    void addListeners(Object listener, Class<?> listenersClass);

    void removeAllListeners(String event);
}
