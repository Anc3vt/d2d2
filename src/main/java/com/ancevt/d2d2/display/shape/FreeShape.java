package com.ancevt.d2d2.display.shape;

import com.ancevt.d2d2.display.BaseDisplayObject;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Colored;
import com.ancevt.d2d2.display.shader.ShaderProgram;
import com.ancevt.d2d2.display.texture.Texture;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FreeShape extends BaseDisplayObject implements Shape, Colored {

    @Getter
    private List<Vertex> vertices = new ArrayList<>();

    @Setter
    private Color color = Color.WHITE;

    @Setter
    private Texture texture;
    @Getter
    private float currentX;
    @Getter
    private float currentY;

    @Getter
    private List<Triangle> triangles = new ArrayList<>();

    public boolean isPointInsideFreeShape(float x, float y) {
        int intersections = 0;
        int vertexCount = vertices.size();

        for (int i = 0; i < vertexCount; i++) {
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get((i + 1) % vertexCount);

            if (isIntersecting(v1, v2, x, y)) {
                intersections++;
            }
        }

        return (intersections % 2) == 1;
    }

    private static boolean isIntersecting(Vertex v1, Vertex v2, float x, float y) {
        if (v1.y > v2.y) {
            Vertex temp = v1;
            v1 = v2;
            v2 = temp;
        }

        if (y == v1.y || y == v2.y) {
            y += 0.0001f;
        }

        if (y > v2.y || y < v1.y || x > Math.max(v1.x, v2.x)) {
            return false;
        }

        if (x < Math.min(v1.x, v2.x)) {
            return true;
        }

        double red = (double) (v2.x - v1.x) * (y - v1.y) / (v2.y - v1.y) + v1.x;
        return x < red;
    }












    public Vertex addVertex(float x, float y) {
        this.currentX = x;
        this.currentY = y;
        var result = new Vertex(x, y);
        vertices.add(result);
        return result;
    }

    public void curveTo(float controlX1, float controlY1, float controlX2, float controlY2, float endX, float endY, float step) {
        float t = 0.0f;
        while (t <= 1.0f) {
            float oneMinusT = 1.0f - t;
            float x = oneMinusT * oneMinusT * oneMinusT * currentX +
                    3 * oneMinusT * oneMinusT * t * controlX1 +
                    3 * oneMinusT * t * t * controlX2 +
                    t * t * t * endX;
            float y = oneMinusT * oneMinusT * oneMinusT * currentY +
                    3 * oneMinusT * oneMinusT * t * controlY1 +
                    3 * oneMinusT * t * t * controlY2 +
                    t * t * t * endY;
            addVertex(x, y);
            this.currentX = x;
            this.currentY = y;
            t += step;
        }
        // Обновляем currentX и currentY

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + vertices.size() + "}";
    }

    public void closePath() {
        var begin = vertices.get(0);
        addVertex(begin.getX(), begin.getY());
    }

    public FreeShape copy() {
        FreeShape copy = new FreeShape();

        List<Vertex> copyVertices = new ArrayList<>();
        for (Vertex v : vertices) {
            Vertex newVertex = new Vertex(v.getX(), v.getY());
            copyVertices.add(newVertex);
        }
        copy.vertices = copyVertices;

        List<Triangle> copyTriangles = new ArrayList<>();
        for (Triangle t : triangles) {
            Triangle newTriangle = new Triangle(t.getX1(), t.getY1(), t.getX2(), t.getY2(), t.getX3(), t.getY3());
        }
        copy.triangles = copyTriangles;
        return copy;
    }

    public void commit() {
        triangles.clear();

        List<Double> coords = new ArrayList<>();
        for (Vertex vertex : vertices) {
            coords.add((double) vertex.x);
            coords.add((double) vertex.y);
        }

        double[] array = coords.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        List<Integer> indices = Earcut.earcut(array);

        int[] indicesArray = indices.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        for (int i = 0; i < indicesArray.length; i += 3) {
            int idx1 = indicesArray[i] * 2;
            int idx2 = indicesArray[i + 1] * 2;
            int idx3 = indicesArray[i + 2] * 2;

            Triangle t = new Triangle(
                    (float) array[idx1], (float) array[idx1 + 1],
                    (float) array[idx2], (float) array[idx2 + 1],
                    (float) array[idx3], (float) array[idx3 + 1]
            );
            triangles.add(t);
        }


    }

}
