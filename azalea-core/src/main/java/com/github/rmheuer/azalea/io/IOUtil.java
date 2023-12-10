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

public final class IOUtil {
    public static void copyStreams(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int read;
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
        }
    }

    public static byte[] readToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        copyStreams(in, b);
        in.close();
        return b.toByteArray();
    }

    public static ByteBuffer readToByteBuffer(InputStream in) throws IOException {
        byte[] data = readToByteArray(in);
        ByteBuffer buf = MemoryUtil.memAlloc(data.length);
        buf.put(data);
        buf.flip();
        return buf;
    }

    public static String readToString(InputStream in) throws IOException {
        return new String(readToByteArray(in), StandardCharsets.UTF_8);
    }

    public static byte[] readToByteArray(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public static String readToString(Path path) throws IOException {
        return new String(readToByteArray(path), StandardCharsets.UTF_8);
    }

    private IOUtil() {
        throw new AssertionError();
    }
}
