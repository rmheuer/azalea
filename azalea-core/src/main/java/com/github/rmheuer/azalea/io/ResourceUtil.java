package com.github.rmheuer.azalea.io;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceUtil {
    public static InputStream readAsStream(String path) throws IOException {
        InputStream stream = ResourceUtil.class.getClassLoader().getResourceAsStream(path);
        if (stream == null)
            throw new IOException("Resource not found: " + path);
        return stream;
    }

    private ResourceUtil() {
        throw new AssertionError();
    }
}
