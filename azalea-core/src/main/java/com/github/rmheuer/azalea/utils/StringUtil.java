package com.github.rmheuer.azalea.utils;

import java.util.Map;

public final class StringUtil {
    public static String iterableToString(Iterable<?> iter) {
        boolean comma = false;

        StringBuilder builder = new StringBuilder("[");
        for (Object value : iter) {
            if (comma) builder.append(", ");
            else comma = true;

            builder.append(value);
        }
        builder.append("]");

        return builder.toString();
    }

    public static String mapToString(Map<?, ?> map) {
        boolean comma = false;

        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (comma) builder.append(", ");
            else comma = true;

            builder.append(entry.getKey());
            builder.append(": ");
            builder.append(entry.getValue());
        }
        builder.append("}");

        return builder.toString();
    }

    private StringUtil() {
        throw new AssertionError();
    }
}
