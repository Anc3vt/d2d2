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
package com.ancevt.d2d2.display;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.shader.ShaderProgram;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureClip;

public class SimpleSprite extends BaseDisplayObject implements Sprite {

    public static final Color DEFAULT_COLOR = Color.WHITE;

    private float repeatX;
    private float repeatY;
    private Color color;
    private TextureClip textureClip;
    private double vertexBleedingFix = 0d;
    private double textureBleedingFix = 0d;
    private ShaderProgram shaderProgram;

    public SimpleSprite() {
        setColor(DEFAULT_COLOR);
        setRepeat(1, 1);
        setName("_" + getClass().getSimpleName() + getDisplayObjectId());
    }

    public SimpleSprite(String assetPathToImage) {
        this(D2D2.textureManager().loadTexture(assetPathToImage).createTextureClip());
    }

    public SimpleSprite(String assetPathToImage, int textureX, int textureY, int textureWidth, int textureHeight) {
        this(
            D2D2.textureManager()
                .loadTexture(assetPathToImage)
                .createTextureClip(
                    textureX,
                    textureY,
                    textureWidth,
                    textureHeight
                )
        );
    }

    public SimpleSprite(TextureClip textureClip) {
        setTextureClip(textureClip);

        setColor(DEFAULT_COLOR);
        setRepeat(1, 1);
    }

    public SimpleSprite(Texture texture) {
        this(texture.createTextureClip());
    }

    @Override
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    @Override
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setColor(int rgb) {
        setColor(new Color(rgb));
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setRepeat(float repeatX, float repeatY) {
        setRepeatX(repeatX);
        setRepeatY(repeatY);
    }

    @Override
    public void setRepeatX(float value) {
        this.repeatX = value;
    }

    @Override
    public void setRepeatY(float value) {
        this.repeatY = value;
    }

    @Override
    public float getRepeatX() {
        return repeatX;
    }

    @Override
    public float getRepeatY() {
        return repeatY;
    }

    @Override
    public TextureClip getTextureClip() {
        return textureClip;
    }

    @Override
    public void setTextureClip(TextureClip value) {
        this.textureClip = value;
        if (textureClip != null && textureClip.getTexture().isDisposed()) {
            throw new IllegalStateException("Texture " + textureClip.getTexture().getId() + " is disposed");
        }
    }

    @Override
    public void setTextureClip(String textureClipKey) {
        setTextureClip(D2D2.textureManager().getTextureClip(textureClipKey));
    }

    @Override
    public float getWidth() {
        return textureClip == null ? 0f : textureClip.getWidth();
    }

    @Override
    public float getHeight() {
        return textureClip == null ? 0f : textureClip.getHeight();
    }

    @Override
    public void onExitFrame() {
        // For overriding
    }

    @Override
    public SimpleSprite cloneSprite() {
        SimpleSprite result = new SimpleSprite(getTextureClip());
        result.setXY(getX(), getY());
        result.setRepeat(getRepeatX(), getRepeatY());
        result.setScale(getScaleX(), getScaleY());
        result.setAlpha(getAlpha());
        result.setColor(getColor().cloneColor());
        result.setVisible(isVisible());
        result.setRotation(getRotation());
        return result;
    }

    @Override
    public void setTextureBleedingFix(double v) {
        this.textureBleedingFix = v;
    }

    @Override
    public double getTextureBleedingFix() {
        return textureBleedingFix;
    }

    @Override
    public void setVertexBleedingFix(double v) {
        vertexBleedingFix = v;
    }

    @Override
    public double getVertexBleedingFix() {
        return vertexBleedingFix;
    }
}
