/**
 * Copyright (C) 2022 the original author or authors.
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

import com.ancevt.commons.util.ApplicationMainClassNameExtractor;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IContainer;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.MouseButton;
import com.ancevt.d2d2.interactive.InteractiveContainer;
import com.ancevt.localstorage.FileLocalStorage;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.localstorage.LocalStorageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.ancevt.d2d2.input.KeyCode.isShift;

public class DebugPanel extends Container {

    private static final Map<String, DebugPanel> debugPanels = new HashMap<>();
    private static boolean enabled;
    private static float scale = 1;
    private static LocalStorage localStorage;

    private final BitmapText text;
    private final String systemPropertyName;
    private final PlainRect bg;
    private final InteractiveContainer interactiveButton;
    private int oldX;
    private int oldY;
    private boolean shiftDown;
    private int mouseButton;

    private final List<Button> buttonList;
    private final Map<String, Button> buttonMap;

    private DebugPanel(String systemPropertyName) {
        debugPanels.put(systemPropertyName, this);

        final int width = 300;
        final int height = 300;

        this.systemPropertyName = systemPropertyName;
        addEventListener(Event.EACH_FRAME, this::this_eachFrame);

        buttonList = new ArrayList<>();
        buttonMap = new HashMap<>();

        bg = new PlainRect(width, height, Color.BLACK);
        bg.setAlpha(0.75f);
        add(bg);

        text = new BitmapText();
        text.setColor(Color.WHITE);
        text.setBounds(width, height);
        add(text, 1, 1);

        interactiveButton = new InteractiveContainer(width, height);
        interactiveButton.addEventListener(InteractiveEvent.DOWN, this::interactiveButton_down);
        interactiveButton.addEventListener(InteractiveEvent.DRAG, this::interactiveButton_drag);

        addEventListener(this, Event.ADD_TO_STAGE, this::this_addToStage);

        add(interactiveButton);

        load();

        setScale(scale, scale);
    }

    public void setText(Object text) {
        System.setProperty(systemPropertyName, String.valueOf(text));
    }

    public static void setScale(float scale) {
        DebugPanel.scale = scale;
    }

    public static float getScale() {
        return DebugPanel.scale;
    }

    public static void setEnabled(boolean enabled) {
        DebugPanel.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private void this_addToStage(Event event) {
        removeEventListener(this, Event.ADD_TO_STAGE);
        D2D2.stage().addEventListener(InputEvent.KEY_DOWN, this::root_keyDown);
        D2D2.stage().addEventListener(InputEvent.KEY_UP, this::root_keyUp);
    }

    private void root_keyDown(Event event) {
        var e = (InputEvent) event;
        if (isShift(e.getKeyCode())) {
            shiftDown = true;
        }
    }

    private void root_keyUp(Event event) {
        var e = (InputEvent) event;
        if (isShift(e.getKeyCode())) {
            shiftDown = false;
        }
    }

    private void interactiveButton_down(Event event) {
        var e = (InteractiveEvent) event;

        mouseButton = e.getMouseButton();

        oldX = (int) (e.getX() + getX());
        oldY = (int) (e.getY() + getY());

        IContainer parent = getParent();
        parent.remove(this);
        parent.add(this);

        dispatchEvent(event);
    }

    private void interactiveButton_drag(Event event) {
        var e = (InteractiveEvent) event;

        if (mouseButton == MouseButton.RIGHT) {
            bg.setSize(e.getX() + 1, e.getY() + 1);
            if (bg.getWidth() < 5f) {
                bg.setWidth(5f);
            }
            if (bg.getHeight() < 5f) {
                bg.setHeight(5f);
            }

            text.setBounds(bg.getWidth(), bg.getHeight());
            interactiveButton.setSize(bg.getWidth(), bg.getHeight());
            return;
        }

        final int tx = (int) (e.getX() + getX());
        final int ty = (int) (e.getY() + getY());

        move(tx - oldX, ty - oldY);

        oldX = tx;
        oldY = ty;
    }

    private void this_eachFrame(Event event) {
        if (System.getProperty(systemPropertyName) != null) {
            text.setText("[" + systemPropertyName + "]\n" + System.getProperty(systemPropertyName));
        }

        if (bg.getWidth() < 10) bg.setWidth(10);
        if (text.getBoundWidth() < 10) text.setBoundWidth(10);
    }

    private static LocalStorage getLocalStorage() {
        if (localStorage == null) {
            localStorage = new LocalStorageBuilder("debug-panel.ls", FileLocalStorage.class)
                    .storageId("d2d2-debug-panel-" + ApplicationMainClassNameExtractor.get())
                    .saveOnWrite(true)
                    .build();
        }
        return localStorage;
    }

    private void save() {
        getLocalStorage().put(systemPropertyName, (int) getX() + ";" +
                (int) getY() + ";" +
                (int) bg.getWidth() + ";" +
                (int) bg.getHeight() + ";" +
                text.getText().replace("\n", "\\n")
        );
    }

    private void load() {
        String string = getLocalStorage().getString(systemPropertyName);
        if (string != null) {
            string = string.replace("\\n", "\n");
            final String[] split = string.split(";", 5);
            final int x = Integer.parseInt(split[0]);
            final int y = Integer.parseInt(split[1]);
            final int w = Integer.parseInt(split[2]);
            final int h = Integer.parseInt(split[3]);
            final String data = split[2];

            setXY(x, y);

            bg.setSize(w, h);
            text.setBounds(w, h);
            interactiveButton.setSize(w, h);
            text.setText(data);
        }
    }

    public DebugPanel addButton(String text, Runnable onPress) {
        if (!buttonMap.containsKey(text)) {
            Button button = new Button(text);
            button.pressFunction = onPress;
            add(button, buttonList.size() * (Button.DEFAULT_WIDTH + 1), -Button.DEFAULT_HEIGHT);
            buttonList.add(button);
            buttonMap.put(text, button);
        }

        return this;
    }

    public static void saveAll() {
        debugPanels.values().forEach(DebugPanel::save);
    }

    public static Optional<DebugPanel> show(String propertyName) {
        return show(propertyName, "");
    }

    public static Optional<DebugPanel> show(String propertyName, Object value) {
        if (enabled) {
            DebugPanel debugPanel = debugPanels.get(propertyName);
            if (debugPanel == null) {
                debugPanel = new DebugPanel(propertyName);
            }

            D2D2.stage().add(debugPanel);
            if (propertyName != null) {
                System.setProperty(propertyName, String.valueOf(value));
            }
            return Optional.of(debugPanel);
        }
        return Optional.empty();
    }

    public static Optional<DebugPanel> show(String propertyName, @NotNull Supplier<Object> supplier) {
        return show(propertyName, supplier.get());
    }

    public static void setProperty(String key, Object value) {
        System.setProperty(key, String.valueOf(value));
    }

    public static void main(String[] args) {
        D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2.stage().setBackgroundColor(Color.DARK_GRAY);

        DebugPanel.setEnabled(true);

        for (int i = 0; i < 1; i++) {
            DebugPanel.show("debug-panel-" + i).ifPresent(debugPanel -> {
                debugPanel.setText(debugPanel.getX());
                debugPanel.addEventListener(Event.EACH_FRAME, event -> {
                    debugPanel.setText(debugPanel.getX());

                    debugPanel
                            .addButton("Move<", () -> debugPanel.moveX(-1))
                            .addButton("Move>", () -> debugPanel.moveX(1));
                });
            });
        }

        System.out.println(ApplicationMainClassNameExtractor.get());


        D2D2.loop();
        DebugPanel.saveAll();
    }

    public static class Button extends BorderedRect {

        private static final float DEFAULT_WIDTH = 50f;
        private static final float DEFAULT_HEIGHT = 12f;

        private final InteractiveContainer interactiveButton;

        private Runnable pressFunction;

        public Button(Object text) {
            super(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK, Color.WHITE);
            setBorderWidth(0.2f);
            interactiveButton = new InteractiveContainer(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            BitmapText bitmapText = new BitmapText(String.valueOf(text));

            add(interactiveButton);
            add(bitmapText, 2, -2);

            interactiveButton.addEventListener(InteractiveEvent.DOWN, this::interactiveButton_down);
        }

        private void interactiveButton_down(Event event) {
            if (pressFunction != null) {
                pressFunction.run();
            }
        }


    }


}
