package com.github.rmheuer.azalea.utils;

import java.util.List;

public final class ArrayUtil {
    public static int[] toArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private ArrayUtil() {
        throw new AssertionError();
    }
}
