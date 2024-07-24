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

import com.ancevt.d2d2.display.shader.ShaderProgram;
import com.ancevt.d2d2.event.BaseEventDispatcher;

public abstract class BaseDisplayObject extends BaseEventDispatcher implements DisplayObject {

    private static int displayObjectIdCounter;
    private final int displayObjectId;
    private String name;
    private SimpleContainer parent;
    private float x;
    private float y;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float alpha = 1.0f;
    private float rotation;
    private boolean visible = true;
    private int zOrderIndex;
    private boolean integerPixelAlignmentEnabled = true;

    private ShaderProgram shaderProgram;

    protected BaseDisplayObject() {
        displayObjectId = displayObjectIdCounter++;
        name = "_" + getClass().getSimpleName() + getDisplayObjectId();
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    @Override
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public int getDisplayObjectId() {
        return displayObjectId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public void setAbsoluteZOrderIndex(int zOrder) {
        this.zOrderIndex = zOrder;
    }

    @Override
    public int getAbsoluteZOrderIndex() {
        return zOrderIndex;
    }

    final void setParent(final SimpleContainer container) {
        this.parent = container;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public void setXY(float x, float y) {
        setX(x);
        setY(y);
    }

    @Override
    public void setX(float value) {
        this.x = value;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setY(float value) {
        this.y = value;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        setScaleX(scaleX);
        setScaleY(scaleY);
    }

    @Override
    public void setScaleX(float value) {
        this.scaleX = value;
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public void setScaleY(float value) {
        this.scaleY = value;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public boolean isOnScreen() {
        Container currentParent = getParent();

        while (currentParent != null) {
            if (currentParent instanceof Stage) return true;
            currentParent = currentParent.getParent();
        }

        return false;
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setRotation(float degrees) {
        rotation = degrees;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void rotate(float toRotation) {
        rotation += toRotation;
    }

    @Override
    public void moveX(float value) {
        setX(getX() + value);
    }

    @Override
    public void moveY(float value) {
        setY(getY() + value);
    }

    @Override
    public void move(float toX, float toY) {
        moveX(toX);
        moveY(toY);
    }

    @Override
    public void scaleX(float value) {
        setScaleX(getScaleX() * value);
    }

    @Override
    public void scaleY(float value) {
        setScaleY(getScaleY() * value);
    }

    @Override
    public void scale(float toX, float toY) {
        scaleX(toX);
        scaleY(toY);
    }

    @Override
    public float getWidth() {
        return 0f;
    }

    @Override
    public float getHeight() {
        return 0f;
    }

    @Override
    public float getScaledWidth() {
        return getWidth() * getScaleX();
    }

    @Override
    public float getScaledHeight() {
        return getHeight() * getScaleY();
    }

    @Override
    public float getAbsoluteX() {
        return DisplayObjectAbsoluteComputer.getAbsoluteX(this);
    }

    @Override
    public float getAbsoluteY() {
        return DisplayObjectAbsoluteComputer.getAbsoluteY(this);
    }

    @Override
    public float getAbsoluteScaleX() {
        return DisplayObjectAbsoluteComputer.getAbsoluteScaleX(this);
    }

    @Override
    public float getAbsoluteScaleY() {
        return DisplayObjectAbsoluteComputer.getAbsoluteScaleY(this);
    }

    @Override
    public float getAbsoluteAlpha() {
        return DisplayObjectAbsoluteComputer.getAbsoluteAlpha(this);
    }

    @Override
    public float getAbsoluteRotation() {
        return DisplayObjectAbsoluteComputer.getAbsoluteRotation(this);
    }

    @Override
    public boolean isAbsoluteVisible() {
        return DisplayObjectAbsoluteComputer.isAbsoluteVisible(this);
    }

    @Override
    public final void removeFromParent() {
        if (hasParent()) {
            getParent().removeChild(this);
        }
    }

    @Override
    public void setAlpha(float value) {
        this.alpha = value;
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void toAlpha(float value) {
        alpha *= value;
    }

    @Override
    public void setIntegerPixelAlignmentEnabled(boolean value) {
        this.integerPixelAlignmentEnabled = value;
    }

    @Override
    public boolean isIntegerPixelAlignmentEnabled() {
        return integerPixelAlignmentEnabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "displayObjectId=" + displayObjectId + ", name='" + name + "'}";
    }
}
