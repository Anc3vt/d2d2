/**
 * Copyright (C) 2023 the original author or authors.
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
package com.ancevt.d2d2.interactive;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.input.MouseButton;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InteractiveManager {

    private static InteractiveManager instance;

    public static InteractiveManager getInstance() {
        return instance == null ? instance = new InteractiveManager() : instance;
    }

    private final List<Interactive> interactiveList;
    private static final int KEY_HOLD_TIME = 30;

    private boolean leftMouseButton;
    private boolean rightMouseButton;
    private boolean middleMouseButton;
    private Interactive hoveredInteractive;
    private Interactive focusedInteractive;
    private int focusedInteractiveIndex;
    private boolean tabbingEnabled;
    private int keyHoldTime = KEY_HOLD_TIME;
    private int keyHoldTabDirection;

    private InteractiveManager() {
        interactiveList = new CopyOnWriteArrayList<>();
        focusedInteractiveIndex = -1;

        D2D2.stage().addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            Interactive focused = getFocused();
            if (focused != null) {
                dispatch(focused, InteractiveEvent.builder()
                    .type(InteractiveEvent.KEY_DOWN)
                    .keyChar(e.getKeyChar())
                    .keyCode(e.getKeyCode())
                    .alt(e.isAlt())
                    .control(e.isControl())
                    .shift(e.isShift())
                    .build());
            }
        });

        D2D2.stage().addEventListener(InputEvent.KEY_REPEAT, event -> {
            var e = (InputEvent) event;
            Interactive focused = getFocused();
            if (focused != null) {
                dispatch(focused, InteractiveEvent.builder()
                    .type(InteractiveEvent.KEY_REPEAT)
                    .keyChar(e.getKeyChar())
                    .keyCode(e.getKeyCode())
                    .alt(e.isAlt())
                    .control(e.isControl())
                    .shift(e.isShift())
                    .build());
            }
        });

        D2D2.stage().addEventListener(InputEvent.KEY_UP, event -> {
            var e = (InputEvent) event;
            Interactive focused = getFocused();
            if (focused != null) {
                dispatch(focused, InteractiveEvent.builder()
                    .type(InteractiveEvent.KEY_UP)
                    .keyChar(e.getKeyChar())
                    .keyCode(e.getKeyCode())
                    .alt(e.isAlt())
                    .control(e.isControl())
                    .shift(e.isShift())
                    .build());
            }
        });

        D2D2.stage().addEventListener(InputEvent.MOUSE_WHEEL, event -> {
            var e = (InputEvent) event;
            Interactive interactive = hoveredInteractive != null && hoveredInteractive.isOnScreen() && hoveredInteractive.isHovering()
                ? hoveredInteractive : getFocused();


            if (interactive != null) {
                dispatch(interactive, InteractiveEvent.builder()
                    .type(InteractiveEvent.WHEEL)
                    .delta(e.getDelta())
                    .shift(e.isShift())
                    .control(e.isControl())
                    .build());
            }
        });

        D2D2.stage().addEventListener(InputEvent.KEY_TYPE, event -> {
            var e = (InputEvent) event;
            Interactive focused = getFocused();
            if (focused != null) {
                dispatch(focused, InteractiveEvent.builder()
                    .type(InteractiveEvent.KEY_TYPE)
                    .keyCode(e.getKeyCode())
                    .keyChar(e.getKeyChar())
                    .keyType(e.getKeyType())
                    .alt(e.isAlt())
                    .control(e.isControl())
                    .shift(e.isShift())
                    .build());
            }
        });
    }

    public void registerInteractive(final Interactive interactive) {
        if (!interactiveList.contains(interactive)) {
            interactive.addEventListener(this, Event.REMOVE_FROM_STAGE, event -> {
                if (interactive.isFocused()) {
                    resetFocus();
                }
            });
            interactiveList.add(interactive);
        }
    }

    public final void unregisterInteractive(final Interactive interactive) {
        interactiveList.remove(interactive);
        interactive.removeEventListener(this, Event.REMOVE_FROM_STAGE);
    }

    public final void clear() {
        while (!interactiveList.isEmpty()) {
            interactiveList.remove(0);
        }
    }

    public final void screenTouch(final int x, final int y, final int pointer, int mouseButton, final boolean down) {
        switch (mouseButton) {
            case MouseButton.LEFT -> leftMouseButton = down;
            case MouseButton.RIGHT -> rightMouseButton = down;
            case MouseButton.MIDDLE -> middleMouseButton = down;
        }

        Interactive pressedInteractive = null;

        if (down) {

            int maxIndex = 0;
            float _tcX = 0.0f, _tcY = 0.0f;

            for (Interactive interactive : interactiveList) {
                final float tcX = interactive.getAbsoluteX();
                final float tcY = interactive.getAbsoluteY();
                final float tcW = interactive.getInteractiveArea().getWidth() * interactive.getAbsoluteScaleX();
                final float tcH = interactive.getInteractiveArea().getHeight() * interactive.getAbsoluteScaleY();

                if (interactive.isOnScreen() && x >= tcX && x <= tcX + tcW && y >= tcY && y <= tcY + tcH) {
                    int index = interactive.getAbsoluteZOrderIndex();
                    if (index >= maxIndex) {
                        pressedInteractive = interactive;
                        maxIndex = index;
                        _tcX = tcX;
                        _tcY = tcY;
                    }
                }
            }

            if (pressedInteractive != null) {
                setFocused(pressedInteractive, true);
                dispatch(pressedInteractive, InteractiveEvent.builder()
                    .type(InteractiveEvent.DOWN)
                    .x((int) (x - _tcX))
                    .y((int) (y - _tcY))
                    .onArea(true)
                    .leftMouseButton(leftMouseButton)
                    .rightMouseButton(rightMouseButton)
                    .middleMouseButton(middleMouseButton)
                    .mouseButton(mouseButton)
                    .build());

                pressedInteractive.setDragging(true);
            }

        } else {
            for (Interactive interactive : interactiveList) {
                if (interactive != null) {

                    if (interactive.isOnScreen()) {
                        final float tcX = interactive.getAbsoluteX();
                        final float tcY = interactive.getAbsoluteY();
                        final float tcW = interactive.getInteractiveArea().getWidth() * interactive.getAbsoluteScaleX();
                        final float tcH = interactive.getInteractiveArea().getHeight() * interactive.getAbsoluteScaleY();

                        final boolean onArea = x >= tcX && x <= tcX + tcW && y >= tcY && y <= tcY + tcH;

                        if (interactive.isDragging()) {
                            dispatch(interactive, InteractiveEvent.builder()
                                .type(InteractiveEvent.UP)
                                .x((int) (x - tcX))
                                .y((int) (y - tcY))
                                .onArea(onArea)
                                .leftMouseButton(leftMouseButton)
                                .rightMouseButton(rightMouseButton)
                                .middleMouseButton(middleMouseButton)
                                .build());

                            interactive.setDragging(false);
                        }
                    }
                }
            }
        }
    }

    public final void screenDrag(int pointer, final int x, final int y) {
        float _tcX = 0.0f, _tcY = 0.0f;
        int maxIndex = 0;
        Interactive upperIntaractive = null;

        for (final Interactive interactive : interactiveList) {
            final float tcX = interactive.getAbsoluteX();
            final float tcY = interactive.getAbsoluteY();
            final float tcW = interactive.getInteractiveArea().getWidth() * interactive.getAbsoluteScaleX();
            final float tcH = interactive.getInteractiveArea().getHeight() * interactive.getAbsoluteScaleY();

            final boolean onScreen = interactive.isOnScreen();
            final boolean onArea = x >= tcX && x <= tcX + tcW && y >= tcY && y <= tcY + tcH;

            if (onScreen) {


                if (onArea) {
                    int index = interactive.getAbsoluteZOrderIndex();
                    if (index >= maxIndex) {
                        maxIndex = index;
                        _tcX = tcX;
                        _tcY = tcY;
                        upperIntaractive = interactive;
                    }
                }

                if (interactive.isDragging()) {
                    dispatch(interactive, InteractiveEvent.builder()
                        .type(InteractiveEvent.DRAG)
                        .x((int) (x - tcX))
                        .y((int) (y - tcY))
                        .onArea(onArea)
                        .leftMouseButton(leftMouseButton)
                        .rightMouseButton(rightMouseButton)
                        .middleMouseButton(middleMouseButton)
                        .build());
                }

                if (interactive.isHovering() && !onArea) {
                    interactive.setHovering(false);
                    dispatch(interactive, InteractiveEvent.builder()
                        .type(InteractiveEvent.OUT)
                        .x((int) (x - tcX))
                        .y((int) (y - tcY))
                        .onArea(false)
                        .leftMouseButton(leftMouseButton)
                        .rightMouseButton(rightMouseButton)
                        .middleMouseButton(middleMouseButton)
                        .build());
                }

            }

        }
        if (upperIntaractive != null) {
            if (!upperIntaractive.isHovering()) {
                if (hoveredInteractive != null) {
                    dispatch(hoveredInteractive, InteractiveEvent.builder()
                        .type(InteractiveEvent.OUT)
                        .x((int) (x - _tcX))
                        .y((int) (y - _tcY))
                        .onArea(false)
                        .leftMouseButton(leftMouseButton)
                        .rightMouseButton(rightMouseButton)
                        .middleMouseButton(middleMouseButton)
                        .build());
                    hoveredInteractive.setHovering(false);
                }

                hoveredInteractive = upperIntaractive;

                upperIntaractive.setHovering(true);
                dispatch(upperIntaractive, InteractiveEvent.builder()
                    .type(InteractiveEvent.HOVER)
                    .x((int) (x - _tcX))
                    .y((int) (y - _tcY))
                    .onArea(true)
                    .leftMouseButton(leftMouseButton)
                    .rightMouseButton(rightMouseButton)
                    .middleMouseButton(middleMouseButton)
                    .build());
            }
        }
    }

    public void setFocused(Interactive interactive, boolean byMouseDown) {
        if (focusedInteractive == interactive) return;

        if (focusedInteractive != null) {
            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.FOCUS_OUT)
                .build());
        }

        int index = interactiveList.indexOf(interactive);
        if (index == -1) {
            focusedInteractiveIndex = -1;
            focusedInteractive = null;
        } else {
            focusedInteractive = interactive;
            focusedInteractiveIndex = index;

            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.FOCUS_IN)
                .byMouseDown(byMouseDown)
                .build());
        }
    }

    public void setFocused(int index) {
        if (interactiveList.size() == 0) return;

        focusedInteractiveIndex = index;

        if (focusedInteractiveIndex < 0)
            focusedInteractiveIndex = 0;
        else if (focusedInteractiveIndex >= interactiveList.size())
            focusedInteractiveIndex = interactiveList.size() - 1;

        if (focusedInteractive == interactiveList.get(focusedInteractiveIndex)) return;

        if (focusedInteractive != null) {
            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.FOCUS_OUT)
                .build());
        }

        focusedInteractive = interactiveList.get(focusedInteractiveIndex);

        dispatch(focusedInteractive, InteractiveEvent.builder()
            .type(InteractiveEvent.FOCUS_IN)
            .build());
    }

    public Interactive getFocused() {
        return focusedInteractive;
    }

    public void focusNext() {
        if (interactiveList.size() == 0 || getTabbingEnabledAndOnScreenAndVisibleCount() == 0) return;
        focusedInteractiveIndex++;
        if (focusedInteractiveIndex >= interactiveList.size()) focusedInteractiveIndex = 0;

        if (focusedInteractive != null) {
            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.OUT)
                .build());
        }

        setFocused(focusedInteractiveIndex);

        if (!getFocused().isTabbingEnabled() || !getFocused().isOnScreen() || !getFocused().isVisible()) focusNext();
    }

    public void focusPrevious() {
        if (interactiveList.size() == 0 || getTabbingEnabledAndOnScreenAndVisibleCount() == 0) return;
        focusedInteractiveIndex--;
        if (focusedInteractiveIndex < 0) focusedInteractiveIndex = interactiveList.size() - 1;

        if (focusedInteractive != null) {
            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.OUT)
                .build());
        }

        setFocused(focusedInteractiveIndex);

        if (!getFocused().isTabbingEnabled() || !getFocused().isOnScreen() || !getFocused().isVisible())
            focusPrevious();
    }

    private int getTabbingEnabledAndOnScreenAndVisibleCount() {
        int count = 0;
        for (Interactive interactive : interactiveList) {
            if (interactive.isTabbingEnabled() && interactive.isOnScreen() && interactive.isVisible())
                count++;
        }
        return count;
    }

    public void setTabbingEnabled(boolean tabbingEnabled) {
        if (this.tabbingEnabled == tabbingEnabled) return;

        this.tabbingEnabled = tabbingEnabled;

        if (tabbingEnabled) {
            D2D2.stage().addEventListener(this, InputEvent.KEY_DOWN, event -> {
                var e = (InputEvent) event;
                switch (e.getKeyCode()) {
                    case KeyCode.TAB -> {
                        if (e.isShift()) {
                            focusPrevious();
                            keyHoldTabDirection = -1;
                        } else {
                            focusNext();
                            keyHoldTabDirection = 1;
                        }
                        D2D2.stage().addEventListener(this, InputEvent.EXIT_FRAME, event1 -> {
                            keyHoldTime--;
                            if (keyHoldTime < 0) {
                                keyHoldTime = 3;
                                if (keyHoldTabDirection == 1) {
                                    focusNext();
                                } else {
                                    focusPrevious();
                                }
                            }
                        });
                    }
                    case KeyCode.ENTER -> {
                        if (focusedInteractive != null) {
                            dispatch(focusedInteractive, InteractiveEvent.builder()
                                .type(InteractiveEvent.DOWN)
                                .onArea(true)
                                .build());
                        }
                    }
                    case KeyCode.ESCAPE -> {
                        resetFocus();
                    }
                }
            });
            D2D2.stage().addEventListener(this, InputEvent.KEY_UP, event -> {
                var e = (InputEvent) event;

                switch (e.getKeyCode()) {
                    case KeyCode.TAB -> {
                        keyHoldTime = KEY_HOLD_TIME;
                        keyHoldTabDirection = 0;
                        D2D2.stage().removeEventListener(this, Event.EXIT_FRAME);
                    }
                    case KeyCode.ENTER -> {
                        if (focusedInteractive != null) {
                            dispatch(focusedInteractive, InteractiveEvent.builder()
                                .type(InteractiveEvent.UP)
                                .onArea(true)
                                .build());
                        }
                    }
                }
            });
        } else {
            D2D2.stage().removeEventListener(this, InputEvent.KEY_DOWN);
            D2D2.stage().removeEventListener(this, InputEvent.KEY_UP);
            D2D2.stage().removeEventListener(this, Event.EXIT_FRAME);
        }
    }

    public boolean isTabbingEnabled() {
        return tabbingEnabled;
    }

    public void resetFocus() {
        if (focusedInteractive != null) {
            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.FOCUS_OUT)
                .build());

            dispatch(focusedInteractive, InteractiveEvent.builder()
                .type(InteractiveEvent.OUT)
                .build());
        }

        focusedInteractiveIndex = -1;
        focusedInteractive = null;
    }

    private static void dispatch(Interactive interactive, InteractiveEvent event) {
        interactive.dispatchEvent(event);
        if (interactive.isPushEventsUp() && interactive.getParent() instanceof Interactive parent) {
            dispatch(parent, event);
        }
    }

    @Override
    public String toString() {
        return "InteractiveManager{interactiveList.size=" + interactiveList.size() + '}';
    }
}
