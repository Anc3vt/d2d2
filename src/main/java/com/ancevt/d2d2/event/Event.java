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

import com.ancevt.d2d2.display.Container;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class Event {

    public static final String LOOP_UPDATE       = "loopUpdate";
    public static final String EXIT_FRAME        = "exitFrame";
    public static final String ENTER_FRAME       = "enterFrame";
    public static final String ADD               = "add";
    public static final String REMOVE            = "remove";
    public static final String ADD_TO_STAGE      = "addToStage";
    public static final String REMOVE_FROM_STAGE = "removeFromStage";
    public static final String START             = "start";
    public static final String COMPLETE          = "complete";
    public static final String RESIZE            = "resize";
    public static final String CHANGE            = "change";
    public static final String ACTION            = "action";
    public static final String ERROR             = "error";


    String type;
    Object source;
    private Container parent;

    private Map<String, Object> extra;

    public <T> T casted() {
        return (T) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "type='" + type + '\'' +
            ", source=" + source +
            ", extra=" + extra +
            '}';
    }
}
