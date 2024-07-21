package com.ancevt.d2d2.display.shape;

import com.ancevt.d2d2.asset.Assets;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.SimpleContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ComplexShapeParser {

    public static Container parseAsset(String assetPath) {
        return parse(Assets.getAsset(assetPath));
    }

    public static Container parse(InputStream inputStream) {
        try {
            return parse(convertInputStreamToString(inputStream));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Container parse(String string) {
        Container result = new SimpleContainer();

        AtomicReference<FreeShape> shape = new AtomicReference<>();

        string.lines().forEach(line -> {

            if (line.startsWith("#")) {
                if (shape.get() != null) {
                    result.addChild(shape.get());
                }

                line = line.substring(1);

                Color color = Color.of(line);

                FreeShape s = new FreeShape();
                s.setColor(color);
                shape.set(s);
            } else if (line.contains(" ")) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                float x = Float.parseFloat(stringTokenizer.nextToken());
                float y = Float.parseFloat(stringTokenizer.nextToken());
                shape.get().addVertex(x, y);
            }
        });

        result.addChild(shape.get());

        return result;
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
