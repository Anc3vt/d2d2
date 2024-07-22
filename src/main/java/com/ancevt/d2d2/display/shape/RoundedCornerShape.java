package com.ancevt.d2d2.display.shape;

 public class RoundedCornerShape extends FreeShape {

    public RoundedCornerShape(float width, float height, float radius, int segments) {
        if (radius * 2 > width || radius * 2 > height) {
            throw new IllegalArgumentException("Radius is too large for the given width and height");
        }

        // Расчет позиций вершин
        float angleIncrement = (float) (Math.PI / 2 / segments);

        // Верхний левый угол
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (Math.PI / 2) + (i + segments) * angleIncrement;
            float x = radius + radius * (float) Math.cos(angle);
            float y = radius + radius * (float) Math.sin(angle);
            addVertex(x, y);
        }

        // Верхний правый угол
        for (int i = 0; i <= segments; i++) {
            float angle = 2 * (float) (Math.PI / 2) + (i + segments) * angleIncrement;
            float x = width - radius + radius * (float) Math.cos(angle);
            float y = radius + radius * (float) Math.sin(angle);
            addVertex(x, y);
        }

        // Нижний правый угол
        for (int i = 0; i <= segments; i++) {
            float angle = 3 * (float) (Math.PI / 2) + (i + segments) * angleIncrement;
            float x = width - radius + radius * (float) Math.cos(angle);
            float y = height - radius + radius * (float) Math.sin(angle);
            addVertex(x, y);
        }

        // Нижний левый угол
        for (int i = 0; i <= segments; i++) {
            float angle = 4 * (float) (Math.PI / 2) + (i + segments) * angleIncrement;
            float x = radius + radius * (float) Math.cos(angle);
            float y = height - radius + radius * (float) Math.sin(angle);
            addVertex(x, y);
        }

        commit();
    }

}
