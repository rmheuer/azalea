package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.render.ColorRGBA;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public final class Bitmap {
    public static final int RED_SHIFT   = 0;
    public static final int GREEN_SHIFT = 8;
    public static final int BLUE_SHIFT  = 16;
    public static final int ALPHA_SHIFT = 24;

    public static final int RED_MASK   = 0xFF << RED_SHIFT;
    public static final int GREEN_MASK = 0xFF << GREEN_SHIFT;
    public static final int BLUE_MASK  = 0xFF << BLUE_SHIFT;
    public static final int ALPHA_MASK = 0xFF << ALPHA_SHIFT;

    public static int encodeColor(ColorRGBA color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        int a = (int) (color.getAlpha() * 255);

        return r << RED_SHIFT | g << GREEN_SHIFT | b << BLUE_SHIFT | a << ALPHA_SHIFT;
    }

    public static ColorRGBA decodeColor(int color) {
        int r = (color & RED_MASK)   >>> RED_SHIFT;
        int g = (color & GREEN_MASK) >>> GREEN_SHIFT;
        int b = (color & BLUE_MASK)  >>> BLUE_SHIFT;
        int a = (color & ALPHA_MASK) >>> ALPHA_SHIFT;

        return ColorRGBA.rgba(r, g, b, a);
    }

    public static Bitmap decode(InputStream in) throws IOException {
        ByteBuffer data = IOUtil.readToByteBuffer(in);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer pChannels = stack.mallocInt(1);

            ByteBuffer pixels = stbi_load_from_memory(
                    data,
                    pWidth,
                    pHeight,
                    pChannels,
                    4
            );
            if (pixels == null) {
                throw new IOException("Failed to decode image");
            }

            int width = pWidth.get(0);
            int height = pHeight.get(0);

            int[] rgbaData = new int[width * height];
            for (int i = 0; i < rgbaData.length; i++) {
                rgbaData[i] = pixels.getInt(i * SizeOf.INT);
            }
            stbi_image_free(pixels);
            MemoryUtil.memFree(data);

            return new Bitmap(width, height, rgbaData);
        }
    }

    private final int width;
    private final int height;
    private final int[] rgbaData;

    public Bitmap(int width, int height) {
        this(width, height, ColorRGBA.white());
    }

    public Bitmap(int width, int height, ColorRGBA fillColor) {
        this(width, height, new int[width * height]);

        int fill = encodeColor(fillColor);
        Arrays.fill(rgbaData, fill);
    }

    public Bitmap(int width, int height, int[] rgbaData) {
        if (rgbaData.length != width * height)
            throw new IllegalArgumentException("RGBA data is wrong size");

        this.width = width;
        this.height = height;
        this.rgbaData = rgbaData;
    }

    public ColorRGBA getPixel(Vector2i pos) {
        return getPixel(pos.x, pos.y);
    }

    public ColorRGBA getPixel(int x, int y) {
        checkBounds(x, y);
        return decodeColor(rgbaData[x + y * width]);
    }

    public void setPixel(Vector2i pos, ColorRGBA color) {
        setPixel(pos.x, pos.y, color);
    }

    public void setPixel(int x, int y, ColorRGBA color) {
        checkBounds(x, y);
        rgbaData[x + y * width] = encodeColor(color);
    }

    public void blit(Bitmap img, int x, int y) {
        checkBounds(x, y);
        if (x + img.width > width || y + img.height > height)
            throw new IndexOutOfBoundsException("Image extends out of bounds");

        for (int row = 0; row < img.height; row++) {
            System.arraycopy(img.rgbaData, row * img.width, rgbaData, x + (y + row) * width, img.width);
        }
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(x + ", " + y);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getRgbaData() {
        return rgbaData;
    }
}
