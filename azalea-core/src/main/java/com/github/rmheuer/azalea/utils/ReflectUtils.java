package com.github.rmheuer.azalea.utils;

public final class ReflectUtils {
    public static Class<?> boxPrimitives(Class<?> unboxed) {
        if (unboxed.equals(boolean.class)) return Boolean.class;
        if (unboxed.equals(byte.class)) return Byte.class;
        if (unboxed.equals(short.class)) return Short.class;
        if (unboxed.equals(int.class)) return Integer.class;
        if (unboxed.equals(long.class)) return Long.class;
        if (unboxed.equals(float.class)) return Float.class;
        if (unboxed.equals(double.class)) return Double.class;
        if (unboxed.equals(char.class)) return Character.class;
        return unboxed;
    }

    public static boolean sameBoxed(Class<?> a, Class<?> b) {
        return boxPrimitives(a).equals(boxPrimitives(b));
    }

    private ReflectUtils() {
        throw new AssertionError();
    }
}
