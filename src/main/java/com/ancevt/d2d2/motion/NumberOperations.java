package com.ancevt.d2d2.motion;

interface NumberOperations<N extends Number> {
    N add(N a, N b);

    N subtract(N a, N b);

    boolean lessThan(N a, N b);

    boolean greaterThan(N a, N b);

    boolean lessThanOrEqual(N a, N b);

    boolean greaterThanOrEqual(N a, N b);
}
