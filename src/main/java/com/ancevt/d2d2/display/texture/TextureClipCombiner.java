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
package com.ancevt.d2d2.display.texture;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TextureClipCombiner {
    private final List<TextureClipCombinerCell> cells;
    @Getter
    private final int width;
    @Getter
    private final int height;
    private int cellIdCounter;

    public TextureClipCombiner(final int width, final int height) {
        this.width = width;
        this.height = height;
        cells = new ArrayList<>();
    }

    public TextureClipCombiner(float width, float height) {
        this((int) width, (int) height);
    }

    public final TextureClipCombinerCell append(
        final TextureClip textureClip,
        final int x,
        final int y,
        final Color color,
        final float scaleX,
        final float scaleY,
        final float alpha,
        final float rotation,
        final float repeatX,
        final float repeatY) {

        final TextureClipCombinerCell cell = new TextureClipCombinerCell();
        cell.setX(x);
        cell.setY(y);
        cell.setColor(color);
        cell.setTextureClip(textureClip);
        cell.setScaleX(scaleX);
        cell.setScaleY(scaleY);
        cell.setAlpha(alpha);
        cell.setRotation(rotation);
        cell.setRepeatX(repeatX);
        cell.setRepeatY(repeatY);
        cell.setId(cellIdCounter++);
        cells.add(cell);

        return cell;
    }



    public final TextureClipCombinerCell append(
        final TextureClip textureClip,
        final int x,
        final int y,
        final float scaleX,
        final float scaleY) {
        final TextureClipCombinerCell cell = new TextureClipCombinerCell();
        cell.setX(x);
        cell.setY(y);
        cell.setTextureClip(textureClip);
        cell.setScaleX(scaleX);
        cell.setScaleY(scaleY);
        cell.setId(cellIdCounter++);
        cells.add(cell);
        return cell;
    }

    public final TextureClipCombinerCell append(
        final TextureClip textureClip,
        final int x,
        final int y,
        final int repeatX,
        final int repeatY) {
        final TextureClipCombinerCell cell = new TextureClipCombinerCell();
        cell.setX(x);
        cell.setY(y);
        cell.setTextureClip(textureClip);
        cell.setRepeatX(repeatX);
        cell.setRepeatY(repeatY);
        cell.setId(cellIdCounter++);
        cells.add(cell);

        return cell;
    }

    public final TextureClipCombinerCell append(
        final TextureClip textureClip,
        final int x,
        final int y) {
        final TextureClipCombinerCell cell = new TextureClipCombinerCell();
        cell.setX(x);
        cell.setY(y);
        cell.setTextureClip(textureClip);
        cell.setId(cellIdCounter++);
        cells.add(cell);

        return cell;
    }

    public final void remove(TextureClipCombinerCell cell) {
        cells.remove(cell);
    }

    public final void remove(final int cellId) {
        final int count = cells.size();
        for (int i = 0; i < count; i++) {
            final TextureClipCombinerCell cell = cells.get(i);
            if (cell.getId() == cellId) {
                cells.remove(cell);
                return;
            }
        }
    }

    public final Texture createTexture() {
        return D2D2.textureManager().getTextureEngine().
            createTexture(width, height, cells.toArray(new TextureClipCombinerCell[]{}));
    }

}
