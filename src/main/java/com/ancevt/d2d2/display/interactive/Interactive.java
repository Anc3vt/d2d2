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
package com.ancevt.d2d2.display.interactive;

import com.ancevt.d2d2.common.Disposable;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.shape.FreeShape;

public interface Interactive extends DisplayObject, Disposable {

    void setPushEventsUp(boolean pushEventUp);

    boolean isPushEventsUp();

    void setTabbingEnabled(boolean tabbingEnabled);

    boolean isTabbingEnabled();

    InteractiveArea getInteractiveArea();

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void setDragging(boolean dragging);

    boolean isDragging();

    void setHovering(boolean hovering);

    boolean isHovering();

    void focus();

    boolean isFocused();

    default void unfocus() {
        if (isFocused()) {
            InteractiveManager.getInstance().resetFocus();
        }
    }

    void setInteractiveFreeShape(FreeShape freeShape);

    FreeShape getInteractiveFreeShape();
}
