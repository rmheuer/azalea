package com.github.rmheuer.azalea.utils;

/**
 * Various unsafe utilities.
 */
public final class UnsafeUtil {
    /**
     * Creates a new generic array of type {@code T}.
     *
     * @param size length of the array to create
     * @param <T> element type
     * @return array of {@code T}
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newGenericArray(int size) {
        return (T[]) new Object[size];
    }
}
