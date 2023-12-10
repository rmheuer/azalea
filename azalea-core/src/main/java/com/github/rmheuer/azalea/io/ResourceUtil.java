package com.github.rmheuer.azalea.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.stream.Stream;

public final class ResourceUtil {
    public static InputStream readAsStream(String path) throws IOException {
        InputStream stream = ResourceUtil.class.getClassLoader().getResourceAsStream(path);
        if (stream == null)
            throw new IOException("Resource not found: " + path);
        return stream;
    }

    public static String readAsString(String path) throws IOException {
        return IOUtil.readToString(readAsStream(path));
    }

    public static Path getDirectoryPath(String dir) throws IOException {
        if (dir.isEmpty())
            throw new IllegalArgumentException("directory cannot be empty");

        URL url = ResourceUtil.class.getClassLoader().getResource(dir);
        if (url == null)
            throw new IOException("Directory not found: " + dir);

        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IOException("Failed to convert to URI", e);
        }

        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            try {
                FileSystems.newFileSystem(uri, Collections.emptyMap());
            } catch (FileSystemAlreadyExistsException ignored) {
            }

            return Paths.get(uri);
        }
    }

    private ResourceUtil() {
        throw new AssertionError();
    }
}
