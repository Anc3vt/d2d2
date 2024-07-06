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
package com.ancevt.d2d2.engine.norender;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Renderer;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.Font;
import com.ancevt.d2d2.display.text.TrueTypeFontBuilder;
import com.ancevt.d2d2.engine.DisplayManager;
import com.ancevt.d2d2.engine.Engine;
import com.ancevt.d2d2.event.BaseEventDispatcher;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventPool;
import com.ancevt.d2d2.event.LifecycleEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.time.Timer;
import lombok.Getter;
import lombok.Setter;

public class NoRenderEngine extends BaseEventDispatcher implements Engine {

    private final int initialWidth;
    private final int initialHeight;
    private Stage stage;
    private String title;
    private Renderer renderer;
    private boolean running;
    private int frameRate = 60;
    private int frameCounter;
    private int fps = frameRate;
    private long time;
    private long tick;

    @Getter
    private int canvasWidth;

    @Getter
    private int canvasHeight;

    @Getter
    @Setter
    private int timerCheckFrameFrequency = 100;

    public NoRenderEngine(int initialWidth, int initialHeight, String title) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        D2D2.textureManager().setTextureEngine(new NoRenderTextureEngine());

        System.err.println("D2D2: No render engine is initialized");
    }

    @Override
    public void setCursorXY(int x, int y) {
        Mouse.setXY(x, y);
    }

    @Override
    public void setCanvasSize(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;

        dispatchEvent(EventPool.simpleEventSingleton(Event.RESIZE, this));
    }

    @Override
    public DisplayManager displayManager() {
        return new NoRenderDisplayManagerStub();
    }

    @Override
    public void setAlwaysOnTop(boolean b) {

    }

    @Override
    public boolean isAlwaysOnTop() {
        return false;
    }

    @Override
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    @Override
    public int getFrameRate() {
        return frameRate;
    }

    @Override
    public int getActualFps() {
        return fps;
    }

    @Override
    public void create() {
        stage = new Stage();
        stage.setSize(initialWidth, initialHeight);
        renderer = new NoRenderRendererStub(stage);
    }

    @Override
    public void start() {
        running = true;
        stage.dispatchEvent(
            LifecycleEvent.builder()
                .type(LifecycleEvent.START_MAIN_LOOP)
                .build()
        );
        startNoRenderLoop();
        stage.dispatchEvent(
            LifecycleEvent.builder()
                .type(LifecycleEvent.EXIT_MAIN_LOOP)
                .build()
        );
    }

    @Override
    public Stage stage() {
        return stage;
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void putToClipboard(String string) {

    }

    @Override
    public String getStringFromClipboard() {
        return null;
    }

    private void startNoRenderLoop() {
        while (running) {
            try {
                renderer.renderFrame();
                if (fps > frameRate) {
                    Thread.sleep(1000 / (frameRate + 10));
                } else {
                    Thread.sleep((long) (1000 / (frameRate * 1.5f)));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            frameCounter++;
            final long time2 = System.currentTimeMillis();

            if (time2 - time >= 1000) {
                time = System.currentTimeMillis();
                fps = frameCounter;
                frameCounter = 0;
            }

            tick++;

            if (tick % timerCheckFrameFrequency == 0) Timer.processTimers();
        }
    }


    @Override
    public Font generateBitmapFont(TrueTypeFontBuilder trueTypeFontBuilder) {
        return D2D2.bitmapFontManager().getDefaultFont();
    }


}
