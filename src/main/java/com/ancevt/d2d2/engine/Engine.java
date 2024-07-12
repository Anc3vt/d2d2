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
package com.ancevt.d2d2.engine;

import com.ancevt.d2d2.display.Renderer;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.Font;
import com.ancevt.d2d2.display.text.TrueTypeFontBuilder;
import com.ancevt.d2d2.event.EventDispatcher;

public interface Engine extends EventDispatcher {

    Stage stage();

    void setAlwaysOnTop(boolean b);

    boolean isAlwaysOnTop();

    void setFrameRate(int value);

    int getFrameRate();

    int getActualFps();

    void create();

    void start();

    Renderer getRenderer();

    void stop();

    void putToClipboard(String string);

    String getStringFromClipboard();

    default void setSmoothMode(boolean value) {}

    default boolean isSmoothMode() {return false;}

    Font generateBitmapFont(TrueTypeFontBuilder trueTypeFontBuilder);

    void setTimerCheckFrameFrequency(int v);

    int getTimerCheckFrameFrequency();

    DisplayManager displayManager();

    SoundManager soundManager();

    void setCursorXY(int x, int y);

    void setCanvasSize(int width, int height);

    int getCanvasWidth();

    int getCanvasHeight();
}
