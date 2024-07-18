package com.ancevt.d2d2.motion;

public class IntegerOperations implements NumberOperations<Integer> {

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public boolean lessThan(Integer a, Integer b) {
        return a < b;
    }

    @Override
    public boolean greaterThan(Integer a, Integer b) {
        return a > b;
    }

    @Override
    public boolean lessThanOrEqual(Integer a, Integer b) {
        return a <= b;
    }

    @Override
    public boolean greaterThanOrEqual(Integer a, Integer b) {
        return a >= b;
    }
}
