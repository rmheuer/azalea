package com.github.rmheuer.azalea.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;

public final class ResourceUtil {
    /**
     * Reads a resource as an {@code InputStream}.
     *
     * @param path resource path to read
     * @return stream to read the resource
     * @throws FileNotFoundException if the resource is not found
     */
    public static InputStream readAsStream(String path) throws FileNotFoundException {
        InputStream stream = ResourceUtil.class.getClassLoader().getResourceAsStream(path);
        if (stream == null)
            throw new FileNotFoundException("Resource not found: " + path);
        return stream;
    }

    /**
     * Reads a resource as a {@code String}.
     *
     * @param path resource path to read
     * @return string contents of the resource
     * @throws IOException if an IO error occurs
     */
    public static String readAsString(String path) throws IOException {
        return IOUtil.readToString(readAsStream(path));
    }

    /**
     * Gets the {@code Path} of a resource directory. This can be used to
     * iterate the files within the directory.
     *
     * @param dir resource path to the directory
     * @return NIO Path of the directory
     * @throws IOException if an IO error occurs
     */
    public static Path getDirectoryPath(String dir) throws IOException {
        if (dir.isEmpty())
            throw new IllegalArgumentException("directory cannot be empty");

        URL url = ResourceUtil.class.getClassLoader().getResource(dir);
        if (url == null)
            throw new FileNotFoundException("Directory not found: " + dir);

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
