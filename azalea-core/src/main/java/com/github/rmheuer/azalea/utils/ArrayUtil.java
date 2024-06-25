package com.github.rmheuer.azalea.utils;

import java.util.Arrays;

public final class ArrayUtil {
    public static <T> T[] append(T[] a, T[] b) {
        T[] combined = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }

    private ArrayUtil() {
        throw new AssertionError();
    }
}
