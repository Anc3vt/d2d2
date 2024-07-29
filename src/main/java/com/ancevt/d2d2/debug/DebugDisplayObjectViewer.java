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
package com.ancevt.d2d2.debug;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.shape.BorderedRectangle;
import com.ancevt.d2d2.display.shape.RectangleShape;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.Text;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.KeyCode;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DebugDisplayObjectViewer {

    private final List<DisplayObject> displayObjects = new ArrayList<>();

    @Getter
    private List<Class<? extends DisplayObject>> typesIncluded = new ArrayList<>();
    @Getter
    private List<Class<? extends DisplayObject>> typesExcluded = new ArrayList<>();
    private Container targetContainer;
    private boolean keyListenerEnabled;

    private int depth = -1;

    public DebugDisplayObjectViewer() {

    }

    public DebugDisplayObjectViewer(boolean keyListenerEnabled) {
        setKeyListenerEnabled(keyListenerEnabled);
    }

    public DebugDisplayObjectViewer(Container targetContainer, boolean keyListenerEnabled, int depth) {
        setTargetContainer(targetContainer);
        setKeyListenerEnabled(keyListenerEnabled);
        setDepth(depth);
    }

    public DebugDisplayObjectViewer(Container targetContainer, boolean keyListenerEnabled) {
        setTargetContainer(targetContainer);
        setKeyListenerEnabled(keyListenerEnabled);
    }

    public DebugDisplayObjectViewer setTypesExcluded(List<Class<? extends DisplayObject>> typesExcluded) {
        this.typesExcluded = typesExcluded;
        return this;
    }

    public DebugDisplayObjectViewer setTypesIncluded(List<Class<? extends DisplayObject>> typesIncluded) {
        this.typesIncluded = typesIncluded;
        return this;
    }

    public DebugDisplayObjectViewer setDepth(int depth) {
        this.depth = depth;
        return this;
    }

    public int getDepth() {
        return depth;
    }

    public DebugDisplayObjectViewer setTargetContainer(Container targetContainer) {
        this.targetContainer = targetContainer;
        return this;
    }

    public Container getTargetContainer() {
        if (targetContainer == null) targetContainer = D2D2.stage();
        return targetContainer;
    }

    public void show() {
        RectangleShape rectangleShape = new RectangleShape(D2D2.stage().getWidth(), D2D2.stage().getHeight(), Color.BLACK);
        rectangleShape.setAlpha(0.5f);
        D2D2.stage().addChild(rectangleShape);
        displayObjects.add(rectangleShape);

        show(getTargetContainer(), -1);
    }

    public void show(Container container, int deep) {
        int counter = 0;

        for (int i = 0; i < container.getNumChildren(); i++) {
            DisplayObject o = container.getChild(i);

            if ((typesIncluded.isEmpty() || typesIncluded.contains(o.getClass())) &&
                (typesExcluded.isEmpty() || !typesExcluded.contains(o.getClass()))) {
                Color color = Color.createVisibleRandomColor();

                BorderedRectangle borderedRectangle = new BorderedRectangle(
                    o.getWidth() * o.getAbsoluteScaleX(),
                    o.getHeight() * o.getAbsoluteY(),
                    null,
                    color);
                D2D2.stage().addChild(borderedRectangle, o.getAbsoluteX(), o.getAbsoluteY());

                Text text = new Text(o.getName());
                text.setColor(color);
                text.setRotation(-20);

                D2D2.stage().addChild(text, o.getAbsoluteX(), o.getAbsoluteY());

                displayObjects.add(borderedRectangle);
                displayObjects.add(text);

                borderedRectangle.addEventListener(Event.ENTER_FRAME, event -> {
                    borderedRectangle.setSize(o.getWidth() * o.getAbsoluteScaleX(), o.getHeight() * o.getAbsoluteScaleY());
                    borderedRectangle.setXY(o.getAbsoluteX(), o.getAbsoluteY());
                    text.setXY(borderedRectangle.getX(), borderedRectangle.getY());
                });
            }

            if (o instanceof Container oc && (deep >= 1 || deep == -1)) {
                show(oc, deep - 1);
            }

            counter++;
            if (counter >= 100) return;
        }
    }

    public void clear() {
        while (!displayObjects.isEmpty()) {
            displayObjects.remove(0).removeFromParent();
        }
    }

    public DebugDisplayObjectViewer setKeyListenerEnabled(boolean b) {
        if (keyListenerEnabled == b) return this;
        keyListenerEnabled = b;

        Stage s = D2D2.stage();

        s.removeEventListener(this, InteractiveEvent.KEY_DOWN);
        s.removeEventListener(this, InteractiveEvent.KEY_UP);

        if (b) {
            s.addEventListener(this, InteractiveEvent.KEY_DOWN, event -> {
                InteractiveEvent e = event.casted();
                if (e.isShift() && e.isControl()) {
                    switch (e.getKeyCode()) {
                        case KeyCode.F1 -> {
                            show();
                        }
                    }
                }
            });

            s.addEventListener(this, InteractiveEvent.KEY_UP, event -> {
                InteractiveEvent e = event.casted();
                switch (e.getKeyCode()) {
                    case KeyCode.F1 -> {
                        clear();
                    }
                }
            });
        }
        return this;
    }

    public boolean isKeyListenerEnabled() {
        return keyListenerEnabled;
    }
}
