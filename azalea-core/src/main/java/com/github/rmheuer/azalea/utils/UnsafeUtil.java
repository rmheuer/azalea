package com.github.rmheuer.azalea.utils;

public final class UnsafeUtil {
    @SuppressWarnings("unchecked")
    public static <T> T[] newGenericArray(int size) {
        return (T[]) new Object[size];
    }
}
