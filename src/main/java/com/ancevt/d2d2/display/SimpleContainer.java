/**
 * Copyright (C) 2024 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2.display;

import com.ancevt.d2d2.display.shader.ShaderProgram;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventPool;
import com.ancevt.d2d2.exception.ContainerException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class SimpleContainer extends BaseDisplayObject implements Container {

    static final float MAX_X = Float.MAX_VALUE;
    static final float MAX_Y = Float.MAX_VALUE;

    final List<DisplayObject> children;

    public SimpleContainer() {
        children = new CopyOnWriteArrayList<>();
        setName("_" + getClass().getSimpleName() + getDisplayObjectId());
    }

    public SimpleContainer(DisplayObject wrappingDisplayObject) {
        this();
        addChild(wrappingDisplayObject);
    }

    public SimpleContainer(DisplayObject wrappingDisplayObject, PlaceBy placeBy) {
        this(wrappingDisplayObject);
        placeBy(wrappingDisplayObject, placeBy);
    }

    @Override
    public final void addChild(DisplayObject child) {
        if (child.hasParent() && child.getParent() != this) {
            child.removeFromParent();
        }

        if (child instanceof BaseDisplayObject d) d.setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, this));

        children.remove(child);
        children.add(child);

        Stage.dispatchAddToStage(child);
    }

    @Override
    public final void addChild(DisplayObject child, int index) {
        if (child.hasParent() && child.getParent() != this) {
            child.removeFromParent();
        }

        if (child instanceof BaseDisplayObject d) d.setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, this));

        children.remove(child);
        children.add(index, child);

        Stage.dispatchAddToStage(child);
    }

    @Override
    public final void addChild(DisplayObject child, float x, float y) {
        if (child.hasParent() && child.getParent() != this) {
            child.removeFromParent();
        }

        child.setXY(x, y);
        if (child instanceof BaseDisplayObject d) d.setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, this));

        children.remove(child);
        children.add(child);

        Stage.dispatchAddToStage(child);
    }

    @Override
    public final void addChild(DisplayObject child, int index, float x, float y) {
        if (child.hasParent() && child.getParent() != this) {
            child.removeFromParent();
        }

        child.setXY(x, y);
        if (child instanceof BaseDisplayObject d) d.setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, this));

        children.remove(child);
        children.add(index, child);

        Stage.dispatchAddToStage(child);
    }

    @Override
    public final void addChild(DisplayObject child, PlaceBy placeBy) {
        if (child.hasParent() && child.getParent() != this) {
            child.removeFromParent();
        }

        addChild(child);
        placeBy(child, placeBy);
    }

    private void placeBy(DisplayObject displayObject, PlaceBy placeBy) {
        float w = displayObject.getWidth();
        float h = displayObject.getHeight();

        switch (placeBy) {
            case TOP_LEFT -> addChild(displayObject, -w, -h);
            case TOP -> addChild(displayObject, -w / 2, -h);
            case TOP_RIGHT -> addChild(displayObject, 0f, -h);
            case LEFT -> addChild(displayObject, -w, -h / 2);
            case CENTER -> addChild(displayObject, -w / 2, -h / 2);
            case RIGHT -> addChild(displayObject, 0f, -h / 2);
            case BOTTOM_LEFT -> addChild(displayObject, -w, 0f);
            case BOTTOM -> addChild(displayObject, -w / 2, 0f);
            case BOTTOM_RIGHT -> addChild(displayObject, 0f, 0f);
        }
    }

    @Override
    public void removeChild(DisplayObject child) {
        Stage.dispatchRemoveFromStage(child);
        if (child instanceof BaseDisplayObject d) d.setParent(null);
        child.dispatchEvent(EventPool.createEvent(Event.REMOVE, this));
        children.remove(child);
    }

    @Override
    public Stream<DisplayObject> children() {
        return children.stream();
    }

    @Override
    public int indexOf(DisplayObject child) {
        return children.indexOf(child);
    }

    @Override
    public int getNumChildren() {
        return children.size();
    }

    @Override
    public DisplayObject getChild(int index) {
        if (index < 0 || index >= children.size())
            throw new ContainerException("Child index %d out of bounds (0-%d)".formatted(index, children.size() - 1));

        return children.get(index);
    }

    @Override
    public DisplayObject getChild(String name) {
        for (DisplayObject displayObject : children) {
            if (displayObject.getName().equals(name)) return displayObject;
        }
        throw new ContainerException("No such display object named \"%s\" in container \"%s\"".formatted(name, getName()));
    }

    @Override
    public void removeAllChildren() {
        children.clear();
    }

    @Override
    public boolean contains(DisplayObject child) {
        return children.contains(child);
    }


    @Override
    public float getWidth() {
        float min = MAX_X;
        float max = 0;

        for (final DisplayObject child : children) {
            float x = child.getX();
            float xw = x + child.getWidth();

            min = Math.min(x, min);
            max = Math.max(xw, max);
        }

        return max - min;
    }

    @Override
    public float getHeight() {
        float min = MAX_Y;
        float max = 0;

        for (final DisplayObject child : children) {
            float y = child.getY();
            float yh = y + child.getHeight();

            min = Math.min(y, min);
            max = Math.max(yh, max);
        }

        return max - min;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                getName() +
                '}';
    }

}
