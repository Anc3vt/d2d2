package com.ancevt.d2d2.motion;

public class DoubleOperations implements NumberOperations<Double> {

    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public boolean lessThan(Double a, Double b) {
        return a < b;
    }

    @Override
    public boolean greaterThan(Double a, Double b) {
        return a > b;
    }

    @Override
    public boolean lessThanOrEqual(Double a, Double b) {
        return a <= b;
    }

    @Override
    public boolean greaterThanOrEqual(Double a, Double b) {
        return a >= b;
    }
}
