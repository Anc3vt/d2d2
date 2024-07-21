package com.ancevt.d2d2.display.svg;

import com.ancevt.d2d2.asset.Assets;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.SimpleContainer;
import com.ancevt.d2d2.display.shape.FreeShape;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * WORK IN PROGRESS
 */
public class SvgShapeParser {

    private float step = 8;

    public SvgShapeParser(float step) {
        this.step = step;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public Container parseAsset(String assetPath) {
        return parse(Assets.getAsset(assetPath));
    }

    public Container parse(InputStream inputStream) {
        try {
            return parseSvg(inputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Container parseSvg(InputStream inputStream) throws Exception {
        Container result = new SimpleContainer();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);

        NodeList pathNodes = document.getElementsByTagName("path");

        for (int i = 0; i < pathNodes.getLength(); i++) {
            Element pathElement = (Element) pathNodes.item(i);
            String dAttribute = pathElement.getAttribute("d");

            Color color;

            if (pathElement.hasAttribute("fill")) {
                String fillAttribute = pathElement.getAttribute("fill");
                color = getFillColorFromFillAttribute(fillAttribute);
            } else if (pathElement.hasAttribute("style")) {
                String styleAttribute = pathElement.getAttribute("style");
                color = getFillColorFromStyleAttribute(styleAttribute);
            } else {
                throw new IllegalArgumentException("No fill or style attribute");
            }
            drawPathData(dAttribute, color, result);
        }

        return result;
    }

    private Color getFillColorFromFillAttribute(String fillAttribute) {
        fillAttribute = fillAttribute.substring(1);
        return Color.of(fillAttribute);
    }

    private static Color getFillColorFromStyleAttribute(String styleAttribute) {
        Map<String, String> m = parseStyle(styleAttribute);
        String fill = m.get("fill");
        fill = fill.substring(1);
        return Color.of(fill);
    }

    private static Map<String, String> parseStyle(String style) {
        Map<String, String> styleMap = new HashMap<>();
        String[] attributes = style.split(";");

        for (String attribute : attributes) {
            String[] keyValue = attribute.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                styleMap.put(key, value);
            }
        }

        return styleMap;
    }

    private void drawPathData(String d, Color color, Container container) {
        List<Command> commands = parsePathData(d);

        FreeShape shape = null;

        for (Command command : commands) {

            switch (command.command) {
                case "M", "m" -> {
                    if (shape != null) {
                        shape.commit();
                        container.addChild(shape);
                    }
                    shape = new FreeShape();
                    shape.setColor(color);
                    linesTo(shape, command.coordsList, command.command.equals("m"));
                }
                case "L", "l" -> linesTo(shape, command.coordsList, command.command.equals("l"));
                case "H", "h" -> horLineTo(shape, command.coordsList, command.command.equals("h"));
                case "V", "v" -> vertLineTo(shape, command.coordsList, command.command.equals("v"));
                case "C", "c" -> {
                    curveTo(shape, command.coordsList, command.command.equals("c"));
                }
                case "Z", "z" -> linesToBegin(shape);
            }

        }

        shape.commit();
        container.addChild(shape);
    }

    private void curveTo(FreeShape shape, List<Coords> coordsList, boolean relative) {
        shape.curveTo(
                coordsList.get(0).x + (relative ? shape.getCurrentX() : 0f),
                coordsList.get(0).y + (relative ? shape.getCurrentY() : 0f),

                coordsList.get(1).x + (relative ? shape.getCurrentX() : 0f),
                coordsList.get(1).y + (relative ? shape.getCurrentY() : 0f),

                coordsList.get(2).x + (relative ? shape.getCurrentX() : 0f),
                coordsList.get(2).y + (relative ? shape.getCurrentY() : 0f),

                step
        );
    }

    private static void vertLineTo(FreeShape shape, List<Coords> coordsList, boolean relative) {
        for (Coords c : coordsList) {
            shape.addVertex(shape.getCurrentX(), c.value + (relative ? shape.getCurrentY() : 0f));
        }
    }

    private static void horLineTo(FreeShape shape, List<Coords> coordsList, boolean relative) {
        for (Coords c : coordsList) {
            shape.addVertex(c.value + (relative ? shape.getCurrentX() : 0f), shape.getCurrentY());
        }
    }

    private void linesToBegin(FreeShape shape) {
        shape.closePath();
    }

    private static void linesTo(FreeShape shape, List<Coords> coordsList, boolean relative) {
        for (Coords c : coordsList) {
            shape.addVertex(c.x + (relative ? shape.getCurrentX() : 0f), c.y + (relative ? shape.getCurrentY() : 0f));
        }

    }

    private static List<Command> parsePathData(String path) {
        final var oldPath = path;

        path = insertSpaces(path);

        StringTokenizer st = new StringTokenizer(path, " ");

        List<Command> result = new ArrayList<>();

        Command cur = null;

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (Character.isLetter(token.charAt(0))) {
                if (cur != null) {
                    result.add(cur);
                }
                cur = new Command(token);
            } else {
                Coords coords = new Coords(token);
                cur.coordsList.add(coords);
            }
        }
        return result;
    }

    // M 775.7 736.6 v 132.9 c 0 10.5 -8.6 19.1 -19.1 19.1 h 38.1 c 10.5 0 19.1-8.5 19.1-19.1V746.1c-11.1-6.6-24.6-9.5-38.1-9.5z
    //
    private static String insertSpaces(String path) {
        StringBuilder builder = new StringBuilder();

        char p = '\0';

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            char n = i != path.length() -1 ? path.charAt(i + 1) : '\0';

            if (p != ',' && (Character.isLetter(c) || c == '-') && n != ' ' && p != ' ') {
                builder.append(' ');
            }

            builder.append(c);

            if ((Character.isLetter(c) && n != ' ') || n == 'z') {
                builder.append(' ');
            }

            p = c;
        }

        return builder.toString();
    }


    @RequiredArgsConstructor
    @ToString
    private static class Command {

        private final String command;
        private final List<Coords> coordsList = new ArrayList<>();
    }

    @ToString
    private static class Coords {

        private float x;
        private float y;
        private float value;

        public Coords(String string) {
            if (string.contains(",")) {
                x = Float.parseFloat(string.split(",")[0]);
                y = Float.parseFloat(string.split(",")[1]);
            } else {
                value = Float.parseFloat(string);
            }
        }

    }

    public static void main(String[] args) {
        var d = "M775.7 736.6v132.9c0 10.5-8.6 19.1-19.1 19.1h38.1c10.5 0 19.1-8.5 19.1-19.1V746.1c-11.1-6.6-24.6-9.5-38.1-9.5z";

        List<Command> commands = parsePathData(d);
        for (Command command : commands) {
            System.out.println(command.command + ": " + command.coordsList);
        }

        var d2 = "m 92.648604,111.27202 v 15.31335 l 4.532979,0.74902 -0.318103,-17.22752 8.35023,-0.49935 0.15905,-12.566934 H 89.467566 l -2.862935,13.066284 z";

        commands = parsePathData(d);
        for (Command command : commands) {
            System.out.println(command.command + ": " + command.coordsList);
        }
    }
}
