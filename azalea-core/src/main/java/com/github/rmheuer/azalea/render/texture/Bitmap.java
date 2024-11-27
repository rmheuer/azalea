package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

/** A 2D RGBA texture stored with CPU access. */
public final class Bitmap implements BitmapRegion {
    /**
     * Reads and decodes a bitmap from an {@code InputStream}.
     *
     * @param in input stream to read from, will be closed
     * @return decoded bitmap, RGBA format
     * @throws IOException if an IO error occurs
     */
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

            return fromRGBA(width, height, rgbaData);
        }
    }

    /**
     * Creates a new bitmap with provided RGBA data.
     *
     * @param width width in pixels
     * @param height height in pixels
     * @param rgbaData packed RGBA data, should have length {@code width*height}.
     */
    public static Bitmap fromRGBA(int width, int height, int[] rgbaData) {
        if (rgbaData.length != width * height)
            throw new IllegalArgumentException("RGBA data is wrong size");

        return new Bitmap(width, height, ColorFormat.RGBA, rgbaData);
    }

    /**
     * Creates a new bitmap with provided grayscale data.
     *
     * @param width width in pixels
     * @param height height in pixels
     * @param grayData packed grayscale data, should have length {@code width*height}.
     */
    public static Bitmap fromGrayscale(int width, int height, byte[] grayData) {
        if (grayData.length != width * height)
            throw new IllegalArgumentException("RGBA data is wrong size");

        return new Bitmap(width, height, ColorFormat.GRAYSCALE, grayData);
    }

    private static Object createDataArray(int width, int height, ColorFormat format) {
        switch (format) {
            case RGBA: return new int[width * height];
            case GRAYSCALE: return new byte[width * height];
            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
    }

    private final int width;
    private final int height;
    private final ColorFormat colorFormat;
    private final Object colorData;

    /**
     * Creates a new bitmap with the specified size, filled with the specified
     * color.
     *
     * @param width width to create in pixels
     * @param height height to create in pixels
     * @param colorFormat format to store colors with
     * @param fillColor color to fill with
     */
    public Bitmap(int width, int height, ColorFormat colorFormat, int fillColor) {
        this(width, height, colorFormat);
        fill(fillColor);
    }

    /**
     * Creates a new empty bitmap with the specified size.
     *
     * @param width width to create in pixels
     * @param height height to create in pixels
     * @param colorFormat format to store colors with
     */
    public Bitmap(int width, int height, ColorFormat colorFormat) {
        this(width, height, colorFormat, createDataArray(width, height, colorFormat));
    }

    private Bitmap(int width, int height, ColorFormat colorFormat, Object colorData) {
        this.width = width;
        this.height = height;
        this.colorFormat = colorFormat;
        this.colorData = colorData;
    }

    private int pixelIdx(int x, int y) {
        return x + y * width;
    }

    @Override
    public int getPixel(int x, int y) {
        checkBounds(x, y);

        int i = pixelIdx(x, y);
        switch (colorFormat) {
            case RGBA:
                return ((int[]) colorData)[i];
            case GRAYSCALE:
                return ((byte[]) colorData)[i];
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void setPixel(int x, int y, int color) {
        checkBounds(x, y);

        int i = pixelIdx(x, y);
        switch (colorFormat) {
            case RGBA:
                ((int[]) colorData)[i] = color;
                break;
            case GRAYSCALE:
                ((byte[]) colorData)[i] = (byte) color;
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void fill(int color) {
        switch (colorFormat) {
            case RGBA:
                Arrays.fill((int[]) colorData, color);
                break;
            case GRAYSCALE:
                Arrays.fill((byte[]) colorData, (byte) color);
                break;
        }
    }

    @Override
    public void blit(BitmapRegion img, int x, int y) {
        checkBounds(x, y);

        int imgW = img.getWidth();
        int imgH = img.getHeight();
        if (x + imgW > width || y + imgH > height)
            throw new IndexOutOfBoundsException("Image extends out of bounds");

        Bitmap src = img.getSourceBitmap();
        int imgXInSrc = img.getSourceOffsetX();
        int imgYInSrc = img.getSourceOffsetY();

        for (int row = 0; row < imgH; row++) {
            int srcY = row + imgYInSrc;
            int dstY = y + row;

            System.arraycopy(src.colorData, src.pixelIdx(imgXInSrc, srcY), colorData, pixelIdx(x, dstY), imgW);
        }
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(x + ", " + y);
        }
    }

    @Override
    public ColorFormat getColorFormat() {
        return colorFormat;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int[] getDataRGBA() {
        return (int[]) colorData;
    }

    @Override
    public byte[] getDataGrayscale() {
        return (byte[]) colorData;
    }

    @Override
    public Bitmap getSourceBitmap() {
        return this;
    }

    @Override
    public int getSourceOffsetX() {
        return 0;
    }

    @Override
    public int getSourceOffsetY() {
        return 0;
    }
}
