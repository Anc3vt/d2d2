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

import com.ancevt.commons.io.ByteInput;
import com.ancevt.commons.io.ByteOutput;
import com.ancevt.commons.io.InputStreamFork;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.shape.BorderedRectangle;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.interactive.InteractiveContainer;
import com.ancevt.d2d2.display.text.Text;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.input.MouseButton;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ByteDisplay extends InteractiveContainer {

    private static final float DEFAULT_WIDTH = 580.0f;
    private static final float DEFAULT_HEIGHT = 400.0f;
    private static final float PADDING = 5.0f;


    public static final Color DEFAULT_BG_COLOR = Color.DARK_BLUE;
    private static final Color DEFAULT_BORDER_COLOR = Color.YELLOW;
    private static final int PAGE_SIZE = 64;

    private final BorderedRectangle bgRect;
    @Getter
    private final Text text;

    private int oldX;
    private int oldY;
    private int mouseButton;
    private byte[] bytes;

    private int position;
    private boolean isHex = true;
    private boolean isUnsigned = true;
    private String gotoIndexString = "";
    private String utfString;
    private int inline, line;

    public ByteDisplay() {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        bgRect = new BorderedRectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_BG_COLOR, DEFAULT_BORDER_COLOR);
        addChild(bgRect);

        text = new Text();
        text.setText("#<FFFF00>ready");
        text.setMulticolor(true);
        text.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        addChild(text, PADDING, 2);

        addEventListener(this, InteractiveEvent.DOWN, this::mouseDown);
        addEventListener(this, InteractiveEvent.DRAG, this::mouseDrag);
        addEventListener(this, InteractiveEvent.KEY_DOWN, this::keyDown);
        addEventListener(this, InteractiveEvent.KEY_REPEAT, this::keyDown);
        addEventListener(this, InteractiveEvent.WHEEL, this::wheel);

    }

    private void wheel(Event event) {
        var e = (InteractiveEvent) event;

        scroll(-e.getDelta() * inline * (e.isControl() ? 10 : 1));
    }

    private void keyDown(Event event) {
        var e = (InteractiveEvent) event;

        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyCode.A, KeyCode.LEFT -> {
                scroll(-1);
            }
            case KeyCode.D, KeyCode.RIGHT -> {
                scroll(1);
            }
            case KeyCode.S, KeyCode.DOWN -> {
                scroll(inline);
            }
            case KeyCode.W, KeyCode.UP -> {
                scroll(-inline);
            }
            case KeyCode.F, KeyCode.PAGE_DOWN -> {
                scroll(PAGE_SIZE);
            }
            case KeyCode.R, KeyCode.PAGE_UP -> {
                scroll(-PAGE_SIZE);
            }
            case KeyCode.END -> {
                setPosition(bytes.length);
            }
            case KeyCode.HOME -> {
                setPosition(0);
            }
            case KeyCode.H -> {
                setHex(!isHex());
            }
            case KeyCode.U -> {
                setUnsigned(!isUnsigned());
            }
            case KeyCode.BACKSPACE -> {
                if (!gotoIndexString.isEmpty()) {
                    gotoIndexString = gotoIndexString.substring(0, gotoIndexString.length() - 1);
                    render();
                }
            }
            case KeyCode.ENTER -> {
                if (!gotoIndexString.isEmpty()) {
                    int targetPosition = Integer.parseInt(gotoIndexString);
                    setPosition(targetPosition);
                    gotoIndexString = "";
                    render();
                }
            }
        }

        if ((keyCode >= 48 && keyCode <= 57) || (keyCode >= 320 && keyCode <= 329)) {
            int num = keyCode < 100 ? keyCode - 48 : keyCode - 320;
            gotoIndexString += "" + num;
            render();
        }

    }

    private void scroll(int i) {
        setPosition(getPosition() + i);
    }

    public void setPosition(int position) {
        if (bytes == null) return;

        this.position = position;

        if (this.position < 0) this.position = 0;

        if (this.position > bytes.length - 5) {
            this.position = bytes.length - 5;
        }

        render();
    }

    public int getPosition() {
        return position;
    }

    @Override
    public void onExitFrame() {
        super.onExitFrame();
        focus();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        bgRect.setWidth(width);
        text.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        bgRect.setHeight(height);
        text.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        bgRect.setSize(width, height);
        text.setSize(width, height);
    }

    private void mouseDown(Event event) {
        var e = (InteractiveEvent) event;

        mouseButton = e.getMouseButton();

        oldX = (int) (e.getX() + getX());
        oldY = (int) (e.getY() + getY());

        Container parent = getParent();
        parent.removeChild(this);
        parent.addChild(this);

        focus();
    }

    private void mouseDrag(Event event) {
        var e = (InteractiveEvent) event;

        if (mouseButton == MouseButton.RIGHT) {
            setSize(e.getX() / getScaleX() + 1, e.getY() / getScaleY() + 1);
            if (getWidth() < 320f) setWidth(320f);
            if (getHeight() < 100f) setHeight(100f);
            render();
            return;
        }

        final int tx = (int) (e.getX() + getX());
        final int ty = (int) (e.getY() + getY());

        move(tx - oldX, ty - oldY);

        oldX = tx;
        oldY = ty;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        while (this.bytes.length <= position) {
            position -= 15;
        }
        setPosition(position);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setHex(boolean hex) {
        isHex = hex;
        if (isHex) isUnsigned = true;
        render();
    }

    public boolean isHex() {
        return isHex;
    }

    public void setUnsigned(boolean unsigned) {
        isUnsigned = unsigned;
        render();
    }

    public boolean isUnsigned() {
        return isUnsigned;
    }

    private void render() {
        StringBuilder s = new StringBuilder();
        s.append("#<CCCCCC>");
        renderStatusLine(s);
        renderDecorLine(s);
        renderData(s);

        int count = getHeightInChars() - line - 6;
        if (count < 0) count = 0;

        s.append("\n".repeat(count));

        renderDecorLine(s);
        renderTextRepresentation(s);

        s.append(utfString);

        text.setText(s.toString());
    }

    private void renderTextRepresentation(StringBuilder s) {

    }

    private void renderData(StringBuilder s) {
        ByteInput byteInput = ByteInput.newInstance(bytes);
        ByteInput byteInputUtf = ByteInput.newInstance(bytes);
        byteInput.skipBytes(position);
        byteInputUtf.skipBytes(position);

        ByteOutput byteOutputUtf = ByteOutput.newInstance();

        final int heightInChars = getHeightInChars();

        int inlineCounter = 0;
        inline = 0;
        line = 0;

        int p = 0;
        for (int i = position, j = 0; i < bytes.length; i++, j++) {
            String color = inlineCounter % 4 == 0 ? "<FFFFFF>" : "<999999>";

            byteOutputUtf.writeByte(byteInputUtf.readByte());

            int b = isUnsigned ? byteInput.readUnsignedByte() : byteInput.readByte();

            if (b == 0) s.append("<000000>");
            else s.append(color);

            String string = indent(isHex ? toHex(b < 0 ? b + 256 : b) : b);
            s.append(string);

            p += string.length();
            inlineCounter++;

            if (inlineCounter > inline) inline = inlineCounter;

            if (p >= getWidthInChars() - 4 && p % 4 == 0) {
                s.append("\n");
                p = 0;
                inlineCounter = 0;
                line++;
            }
            if (i == bytes.length - 1) {
                s.append(" <FF0000>X");
            }

            if (line >= heightInChars - 6) break;
        }

        s.append("\n");

        utfString = new String(byteOutputUtf.toArray(), StandardCharsets.UTF_8);
    }

    private int getWidthInChars() {
        return (int) ((getWidth() / text.getCharWidth()) - 4);
    }

    private int getHeightInChars() {
        return (int) (getHeight() / text.getCharHeight());
    }

    private static String indent(Object o) {
        StringBuilder s = new StringBuilder();

        if (o instanceof Integer i && i < 0) {

        } else {
            s.append(" ");
        }

        s.append(o);

        int l = s.length() - 1;

        s.append(" ".repeat(3 - l));

        return s.toString();
    }

    private static String toHex(int b) {
        String result = Integer.toHexString(b);
        if (result.length() == 1) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }

    private void renderStatusLine(StringBuilder s) {
        String totalSize = bytes != null ? bytes.length + "" : "-";

        s
            .append("position: ")
            .append(position)
            .append(" of ")
            .append(totalSize)
            .append("; ");

        if (isHex) {
            s.append("HEXADECIMAL ");
        } else {
            s.append("DECIMAL ");
        }

        if (isUnsigned) {
            s.append("<00FF00>UNSIGNED ");
        } else {
            s.append("<CCCCCC>SIGNED ");
        }

        if (gotoIndexString != null) {
            s
                .append("<CCCCCC>")
                .append(gotoIndexString);
        }

        s.append("\n");
    }

    private void renderDecorLine(StringBuilder s) {
        float cw = text.getCharWidth();
        int charCount = (int) ((getWidth() - PADDING * 2) / cw);
        String line = "<FFFFFF>%s".formatted("-".repeat(charCount));
        s.append(line).append("\n");
    }

    public static ByteDisplay show(byte[] bytes) {
        ByteDisplay result = new ByteDisplay();
        result.setBytes(bytes);
        D2D2.stage().addChild(result, 100 + new Random().nextInt(100), 100 + new Random().nextInt(100));
        return result;
    }

    public static ByteDisplay show(InputStream inputStream) {
        InputStreamFork fork = InputStreamFork.fork(inputStream);
        try {
            byte[] bytes = fork.left().readAllBytes();
            return show(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
