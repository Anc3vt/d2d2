package com.ancevt.d2d2.display.shape;

public class CircleShape extends FreeShape {

    public static final float DEFAULT_RADIUS = 100;
    public static final int DEFAULT_NUM_VERTICES = 50;

    public CircleShape() {
        this(DEFAULT_RADIUS, DEFAULT_NUM_VERTICES);
    }

    public CircleShape(int radius) {
        this(radius, DEFAULT_NUM_VERTICES);
    }

    public CircleShape(float radius, int numVertices) {
        for (int i = 0; i < numVertices; i++) {
            var angle = 2 * Math.PI * i / numVertices;
            var x = radius * Math.cos(angle);
            var y = radius * Math.sin(angle);
            addVertex((float) x, (float) y);
        }
        commit();
    }
}
