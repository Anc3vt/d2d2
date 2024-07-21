package com.ancevt.d2d2.display.shape;

import com.ancevt.d2d2.display.BaseDisplayObject;
import com.ancevt.d2d2.display.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class LineBatch extends BaseDisplayObject implements Shape {

    private Color color = Color.WHITE;

    @Getter
    private final List<Line> lines = new ArrayList<>();

    @Getter
    private float currentX = 0f;

    @Getter
    private float currentY = 0f;

    @Getter
    private boolean started = false;

    private float startX = 0f;
    private float startY = 0f;

    @Setter
    @Getter
    private float lineWidth = 1f;

    @Getter
    @Setter
    private int stippleFactor = 1;

    @Getter
    @Setter
    private short stipple = 0;

    private Line currentLine = null;

    public LineBatch() {
        setName("_" + getClass().getSimpleName() + getDisplayObjectId());
    }

    public void moveTo(float x, float y) {
        if (!started) {
            startX = x;
            startY = y;
            started = true;
        }
        if (currentLine != null) {
            currentLine.closing = true;
        }

        currentX = x;
        currentY = y;
    }

    public void moveTo(int x, int y) {
        moveTo((float) x, (float) y);
    }

    public Line lineTo(float x, float y) {
        Vertex vertexA = new Vertex(currentX, currentY);
        Vertex vertexB = new Vertex(x, y);

        currentX = x;
        currentY = y;

        currentLine = new Line(vertexA, vertexB);
        lines.add(currentLine);

        return currentLine;
    }

    public Line lintTo(int x, int y) {
        return lineTo((float) x, (float) y);
    }

    public void closePath() {
        if (!started) throw new IllegalStateException("Lint batch has not been started");
        lineTo(startX, startY);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @RequiredArgsConstructor
    @Getter
    public static class Line {
        private final Vertex vertexA;
        private final Vertex vertexB;
        private boolean closing;
    }
}
