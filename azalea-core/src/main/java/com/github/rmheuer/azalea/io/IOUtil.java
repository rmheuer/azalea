package com.github.rmheuer.azalea.io;

import org.lwjgl.system.MemoryUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class containing various utilities for working with Java IO.
 */
public final class IOUtil {
    /**
     * Copies the data from one stream into another. Neither stream is closed
     * by this method.
     *
     * @param in stream to read from
     * @param out stream to write into
     * @throws IOException if an IO error occurs
     */
    public static void copyStreams(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int read;
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
        }
    }

    /**
     * Reads the data from a stream into a byte array.
     *
     * @param in stream to read from
     * @return data in a byte array
     * @throws IOException if an IO error occurs
     */
    public static byte[] readToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        copyStreams(in, b);
        in.close();
        return b.toByteArray();
    }

    /**
     * Reads the data from a stream into a {@code ByteBuffer} allocated using
     * {@link MemoryUtil#memAlloc(int)}.
     *
     * @param in stream to read from
     * @return data in a ByteBuffer
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer readToByteBuffer(InputStream in) throws IOException {
        byte[] data = readToByteArray(in);
        ByteBuffer buf = MemoryUtil.memAlloc(data.length);
        buf.put(data);
        buf.flip();
        return buf;
    }

    /**
     * Reads the data from a stream as a {@code String} in UTF-8 encoding.
     *
     * @param in stream to read from
     * @return data as string
     * @throws IOException if an IO error occurs
     */
    public static String readToString(InputStream in) throws IOException {
        return new String(readToByteArray(in), StandardCharsets.UTF_8);
    }

    /**
     * Reads a file from a {@code Path} into a byte array.
     *
     * @param path path to read from
     * @return data read from the file
     * @throws IOException if an IO error occurs
     */
    public static byte[] readToByteArray(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    /**
     * Reads a file from a {@code Path} as a {@code String}.
     *
     * @param path path to read from
     * @return data as string
     * @throws IOException if an IO error occurs
     */
    public static String readToString(Path path) throws IOException {
        return new String(readToByteArray(path), StandardCharsets.UTF_8);
    }

    private IOUtil() {
        throw new AssertionError();
    }
}
