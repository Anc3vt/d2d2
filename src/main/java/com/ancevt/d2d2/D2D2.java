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
package com.ancevt.d2d2;

import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapFontManager;
import com.ancevt.d2d2.display.texture.TextureManager;
import com.ancevt.d2d2.engine.DisplayManager;
import com.ancevt.d2d2.engine.Engine;
import com.ancevt.d2d2.engine.SoundManager;
import com.ancevt.d2d2.engine.norender.NoRenderEngine;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.lifecycle.D2D2Application;
import com.ancevt.d2d2.lifecycle.SystemProperties;
import com.ancevt.util.args.Args;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static com.ancevt.commons.string.ConvertableString.convert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class D2D2 {
    private static final String PROPERTIES_FILENAME = "application.properties";

    private static final TextureManager textureManager = new TextureManager();

    private static BitmapFontManager bitmapFontManager;

    @Getter
    private static DisplayObject cursor;
    private static Engine engine;

    @Getter
    private static Args args;

    public static void init(Class<? extends D2D2Application> d2d2EntryPointClass, String[] args) {
        D2D2.args = Args.of(args);

        readPropertyFileIfExist();
        addCliArgsToSystemProperties(args);

        if (Arrays.asList(args).contains("--lwjgl")) {
            System.setProperty("d2d2.engine", "com.ancevt.d2d2.engine.lwjgl.LwjglEngine");
            System.setProperty("d2d2.window.width", "1168");
            System.setProperty("d2d2.window.height", "1000");
            System.err.println("D2D2: `--lwjgl` default engine and window properties preset is set");
        }

        Properties p = System.getProperties();
        String engineClassName = p.getProperty(SystemProperties.D2D2_ENGINE, NoRenderEngine.class.getName());
        String titleText = p.getProperty(SystemProperties.D2D2_WINDOW_TITLE, "D2D2 Application");
        int width = convert(p.getProperty(SystemProperties.D2D2_WINDOW_WIDTH, "800")).toInt();
        int height = convert(p.getProperty(SystemProperties.D2D2_WINDOW_HEIGHT, "600")).toInt();
        Engine engine = createEngine(engineClassName, width, height, titleText);

        Stage stage = D2D2.createStage(engine);
        D2D2Application entryPoint = createMain(d2d2EntryPointClass);
        entryPoint.onCreate(stage);
        D2D2.loop();
        entryPoint.onDispose();
    }

    public static Stage createStage(Engine engine) {
        bitmapFontManager = new BitmapFontManager();
        D2D2.engine = engine;
        engine.create();
        return engine.stage();
    }

    public static D2D2Application createMain(Class<? extends D2D2Application> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Engine createEngine(String engineClassName, int width, int height, String titleText) {
        try {
            Engine engine = (Engine) Class.forName(engineClassName)
                .getConstructor(int.class, int.class, String.class)
                .newInstance(width, height, titleText);

            return engine;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void readPropertyFileIfExist() {
        InputStream inputStream = D2D2.class
            .getClassLoader()
            .getResourceAsStream(PROPERTIES_FILENAME);

        if (inputStream != null) {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                System.getProperties().putAll(properties);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static void addCliArgsToSystemProperties(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-D")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                System.setProperty(key, value);
            }
        }
    }

    public static void setCursor(DisplayObject cursor) {
        if (cursor == D2D2.cursor) return;

        if (cursor != null) {
            Mouse.setVisible(false);
            cursor.removeEventListener(Mouse.class, Event.LOOP_UPDATE);
            cursor.addEventListener(Mouse.class, Event.LOOP_UPDATE, event -> cursor.setXY(Mouse.getX(), Mouse.getY()));
        } else {
            Mouse.setVisible(true);
            D2D2.cursor.removeEventListener(Mouse.class, Event.LOOP_UPDATE);
        }
        D2D2.cursor = cursor;
    }

    public static Stage stage() {
        return engine.stage();
    }

    public static void loop() {
        engine.start();
    }

    public static void exit() {
        engine.stop();
    }

    public static TextureManager textureManager() {
        return textureManager;
    }

    public static BitmapFontManager bitmapFontManager() {
        return bitmapFontManager;
    }

    public static Engine engine() {
        return engine;
    }

    public static DisplayManager displayManager() {
        return engine.displayManager();
    }

    public static SoundManager soundManager() {
        return engine().soundManager();
    }

}
