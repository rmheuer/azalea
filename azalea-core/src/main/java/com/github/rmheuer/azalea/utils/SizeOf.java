package com.github.rmheuer.azalea.utils;

/**
 * Sizes of primitive types in bytes.
 */
public final class SizeOf {
    public static final int BYTE = 1;
    public static final int SHORT = 2;
    public static final int INT = 4;
    public static final int LONG = 8;
    public static final int FLOAT = 4;
    public static final int DOUBLE = 8;
    public static final int CHAR = 2;
    public static final int BOOLEAN = 1;

    private SizeOf() {
        throw new AssertionError();
    }
}
