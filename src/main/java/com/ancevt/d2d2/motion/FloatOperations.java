package com.ancevt.d2d2.motion;

public class FloatOperations implements NumberOperations<Float> {

    @Override
    public Float add(Float a, Float b) {
        return a + b;
    }

    @Override
    public Float subtract(Float a, Float b) {
        return a - b;
    }

    @Override
    public boolean lessThan(Float a, Float b) {
        return a < b;
    }

    @Override
    public boolean greaterThan(Float a, Float b) {
        return a > b;
    }

    @Override
    public boolean lessThanOrEqual(Float a, Float b) {
        return a <= b;
    }

    @Override
    public boolean greaterThanOrEqual(Float a, Float b) {
        return a >= b;
    }
}
