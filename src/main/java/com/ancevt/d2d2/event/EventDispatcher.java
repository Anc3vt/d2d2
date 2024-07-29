/**
 * Copyright (C) 2024 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2.event;

import java.util.Map;

public interface EventDispatcher {

    void addEventListener(String type, EventListener listener);

    void removeEventListener(String type, EventListener listener);

    void dispatchEvent(String type, Map<String, Object> extra);

    void dispatchEvent(Event event);

    void removeAllEventListeners();

    void removeAllEventListeners(String type);

    void removeEventListener(Object key, String type);

    void addEventListener(Object key, String type, EventListener listener);
}
