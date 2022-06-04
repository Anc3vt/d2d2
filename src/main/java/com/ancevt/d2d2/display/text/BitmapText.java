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
package com.ancevt.d2d2.display.text;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.IColored;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.d2d2.D2D2.init;
import static com.ancevt.d2d2.D2D2.loop;
import static java.lang.Integer.parseInt;

public class BitmapText extends DisplayObject implements IColored {

    protected static final String DEFAULT_TEXT = "";

    protected static final float DEFAULT_BOUND_WIDTH = 512f;
    protected static final float DEFAULT_BOUND_HEIGHT = 512f;

    protected static final Color DEFAULT_COLOR = Color.WHITE;

    private String text;
    private Color color;

    private BitmapFont bitmapFont;

    private float lineSpacing;
    private float spacing;

    private float width;
    private float height;

    private double textureBleedingFix = 0.0;
    private double vertexBleedingFix = 0.0;
    private boolean multicolorEnabled;
    private ColorTextData colorTextData;
    private boolean autosize;

    public BitmapText(final BitmapFont bitmapFont, float boundWidth, float boundHeight, String text) {
        setBitmapFont(bitmapFont);
        setColor(DEFAULT_COLOR);
        setWidth(boundWidth);
        setHeight(boundHeight);
        setText(text);
        setName("_" + getClass().getSimpleName() + displayObjectId());
    }

    public BitmapText(final BitmapFont bitmapFont, float boundWidth, float boundHeight) {
        this(bitmapFont, boundWidth, boundHeight, DEFAULT_TEXT);
    }

    public BitmapText(String text) {
        this(BitmapFont.getDefaultBitmapFont(), DEFAULT_BOUND_WIDTH, DEFAULT_BOUND_HEIGHT, text);
    }

    public BitmapText(final BitmapFont bitmapFont) {
        this(bitmapFont, DEFAULT_BOUND_WIDTH, DEFAULT_BOUND_HEIGHT, DEFAULT_TEXT);
    }

    public BitmapText(float boundWidth, float boundHeight) {
        this(BitmapFont.getDefaultBitmapFont(), boundWidth, boundHeight, DEFAULT_TEXT);
    }

    public BitmapText() {
        this(BitmapFont.getDefaultBitmapFont(), DEFAULT_BOUND_WIDTH, DEFAULT_BOUND_HEIGHT, DEFAULT_TEXT);
    }

    public void setTextureBleedingFix(double textureBleedingFix) {
        this.textureBleedingFix = textureBleedingFix;
    }

    public double getTextureBleedingFix() {
        return textureBleedingFix;
    }

    public void setVertexBleedingFix(double vertexBleedingFix) {
        this.vertexBleedingFix = vertexBleedingFix;
    }

    public double getVertexBleedingFix() {
        return vertexBleedingFix;
    }

    public void setAutosize(boolean autosize) {
        this.autosize = autosize;
        if (autosize) {
            setSize(getTextWidth(), getTextHeight());
        }
    }

    public boolean isAutosize() {
        return autosize;
    }

    public float getTextWidth() {
        if (isEmpty()) return 0;

        final char[] chars = getPlainText().toCharArray();
        int result = 0;

        final BitmapFont font = getBitmapFont();

        int max = 0;

        for (final char c : chars) {
            if (c == '\n' || (getWidth() > 0 && result > getWidth())) result = 0;

            BitmapCharInfo info = font.getCharInfo(c);
            if (info == null) continue;

            result += info.width() + getSpacing();

            if (result > max) max = result;
        }

        return (int) (max - getSpacing()) + font.getCharInfo('0').width();
    }

    public float getTextHeight() {
        if (getText() == null) return 0;

        final char[] chars = getPlainText().toCharArray();
        int result = 0;

        final BitmapFont font = getBitmapFont();

        for (final char c : chars) {
            if (c == '\n' || (getWidth() > 0 && result > getWidth())) {
                result += font.getCharHeight() + getLineSpacing();
            }
        }

        return result + font.getCharHeight();
    }

    public Sprite toSprite() {
        Sprite result = new Sprite(D2D2.getTextureManager().bitmapTextToTextureAtlas(this).createTexture());
        result.setXY(getX(), getY());
        result.setScale(getScaleX(), getScaleY());
        result.setRotation(getRotation());
        result.setColor(getColor());
        return result;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        if (multicolorEnabled) {
            colorTextData = new ColorTextData(getText(), color);
        }
    }

    @Override
    public void setColor(int rgb) {
        setColor(new Color(rgb));
    }

    @Override
    public Color getColor() {
        return color;
    }

    public void setText(String text) {
        this.text = text;
        if (multicolorEnabled) {
            colorTextData = new ColorTextData(getText(), getColor());
        }
        if (autosize) {
            setSize(getTextWidth(), getTextHeight());
        }
    }

    public String getPlainText() {
        if (!multicolorEnabled) return text;

        return getColorTextData().getPlainText();
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty() {
        return text == null || text.length() == 0;
    }

    public BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    public void setBitmapFont(BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;
    }

    public void setLineSpacing(float value) {
        this.lineSpacing = value;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setSpacing(float value) {
        this.spacing = value;
    }

    public float getSpacing() {
        return spacing;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setWidth(float value) {
        width = value;
    }

    public void setHeight(float value) {
        height = value;
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setMulticolorEnabled(boolean multicolorEnabled) {
        if (multicolorEnabled == isMulticolorEnabled()) return;
        this.multicolorEnabled = multicolorEnabled;
        if (multicolorEnabled) {
            colorTextData = new ColorTextData(getText(), getColor());
        } else {
            colorTextData = null;
        }
    }

    public float getCharWidth() {
        return getBitmapFont().getCharInfo('0').width();
    }

    private float getCharHeight() {
        return getBitmapFont().getCharInfo('0').height();
    }

    public boolean isMulticolorEnabled() {
        return multicolorEnabled;
    }

    public ColorTextData getColorTextData() {
        return colorTextData;
    }

    @Override
    public void onEachFrame() {
        // For overriding
    }

    @Override
    public String toString() {
        return "BitmapText{" +
                "text='" + text + '\'' +
                ", color=" + color +
                ", bitmapFont=" + bitmapFont +
                ", lineSpacing=" + lineSpacing +
                ", spacing=" + spacing +
                ", boundWidth=" + width +
                ", boundHeight=" + height +
                '}';
    }

    public static class ColorTextData {

        private Letter[] letters;
        private String plainText;
        private final Color defaultColor;

        private ColorTextData(String text, Color defaultColor) {
            this.defaultColor = defaultColor;
            createData(text);
        }

        private void createData(String text) {
            List<Letter> letterList = new ArrayList<>();
            Color color = defaultColor;

            if (text.isEmpty()) text = " ";

            boolean firstIndexSharp = text.charAt(0) == '#';
            int firstIndexOpen = text.indexOf('<');
            int lastIndexClose = text.lastIndexOf('>');

            StringBuilder stringBuilder = new StringBuilder();

            if (firstIndexSharp && firstIndexOpen < lastIndexClose) {

                for (int i = 1; i < text.length(); i++) {
                    char c = text.charAt(i);

                    try {

                        if (c == '<') {
                            String colorString = text.substring(i + 1, i + 8);
                            colorString = colorString.substring(0, colorString.indexOf('>'));

                            color = Color.of(parseInt(colorString, 16));

                            i += colorString.length() + 1;
                        } else {
                            letterList.add(new Letter(c, color));
                            stringBuilder.append(c);
                        }

                    } catch (StringIndexOutOfBoundsException exception) {
                        exception.printStackTrace();
                    }

                }

            } else {
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    letterList.add(new Letter(c, defaultColor));
                    stringBuilder.append(c);
                }
            }

            letters = letterList.toArray(new Letter[0]);
            plainText = stringBuilder.toString();
        }

        public String getPlainText() {
            return plainText;
        }

        public Letter getColoredLetter(int index) {
            return letters[index];
        }

        public int length() {
            return letters.length;
        }

        public static class Letter {
            private final char character;
            private final Color color;

            public Letter(char character, Color color) {

                this.character = character;
                this.color = color;
            }

            public char getCharacter() {
                return character;
            }

            public Color getColor() {
                return color;
            }
        }

    }

    public static void main(String[] args) {
        Stage stage = init(new LWJGLBackend(800, 600, "(floating)"));


        String text = """
                #<0000FF>Hello <FFFF00>D2D2 <0000FF>world
                <FFFFFF>Second line
                                
                ABCDEFGHIJKLMNOPQRSTUWYXYZ
                abcdefghijklmnopqrstuvwxyz""";

        BitmapText bitmapText = new BitmapText(BitmapFont.loadBitmapFont("PressStart2P.bmf"));
        bitmapText.setMulticolorEnabled(true);
        bitmapText.setText(text);
        bitmapText.setScale(4, 4);
        stage.add(bitmapText, 100, 100);


        loop();
    }


}














































