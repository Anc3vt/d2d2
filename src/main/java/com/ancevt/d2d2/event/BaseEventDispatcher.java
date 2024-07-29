/**
 * Copyright (C) 2024 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
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
package com.ancevt.d2d2.event;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@NoArgsConstructor
public class BaseEventDispatcher implements EventDispatcher {

    private final Map<String, List<EventListener>> map = new HashMap<>();

    /**
     * ref to 'map'
     * key : type
     */
    private final Map<Object, TypeAndListener> keysTypeListenerMap = new HashMap<>();

    @Override
    public void addEventListener(String type, EventListener listener) {
        List<EventListener> listeners = map.getOrDefault(type, createList());
        listeners.add(listener);
        map.put(type, listeners);
    }

    @Override
    public void addEventListener(Object key, String type, EventListener listener) {
        internalAddEventListenerByKey(key.hashCode() + type, type, listener);
    }

    @Override
    public void removeEventListener(String type, EventListener listener) {
        List<EventListener> listeners = map.get(type);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void dispatchEvent(String type, Map<String, Object> extra) {
        dispatchEvent(Event.builder()
                .type(type)
                .extra(extra)
                .build());
    }

    @Override
    public void dispatchEvent(Event event) {
        List<EventListener> listeners = map.get(event.getType());
        event.setSource(this);

        if (listeners != null) {
            listeners.forEach(e -> e.onEvent(event));
        }
    }

    @Override
    public void removeEventListener(Object key, String type) {
        internalRemoveEventListenerByKey(key.hashCode() + type);
    }

    @Override
    public void removeAllEventListeners(String type) {
        map.remove(type);
    }

    @Override
    public void removeAllEventListeners() {
        map.clear();
    }

    private List<EventListener> createList() {
        return new CopyOnWriteArrayList<>();
    }

    private void internalAddEventListenerByKey(Object key, String type, EventListener listener) {
        addEventListener(type, listener);
        keysTypeListenerMap.put(key, new TypeAndListener(type, listener));
    }

    private void internalRemoveEventListenerByKey(Object key) {
        TypeAndListener typeAndListener = keysTypeListenerMap.remove(key);
        if (typeAndListener != null) {
            removeEventListener(typeAndListener.type, typeAndListener.listener);
        }
    }

    private record TypeAndListener(String type, EventListener listener) {
    }
}
