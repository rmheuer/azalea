package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.render.Colors;
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
     * Reads and decodes a bitmap from an {@code InputStream}. This will take
     * ownership of the stream.
     *
     * @param in input stream to read from
     * @return decoded bitmap
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

            return new Bitmap(width, height, rgbaData);
        }
    }

    private final int width;
    private final int height;
    private final int[] rgbaData;

    /**
     * Creates a new empty bitmap with the specified size, filled with white.
     *
     * @param width width to create in pixels
     * @param height height to create in pixels
     */
    public Bitmap(int width, int height) {
        this(width, height, Colors.RGBA.WHITE);
    }

    /**
     * Creates a new bitmap with the specified size, filled with the specified
     * color.
     *
     * @param width width to create in pixels
     * @param height height to create in pixels
     * @param fillColorRGBA color to fill with
     */
    public Bitmap(int width, int height, int fillColorRGBA) {
        this(width, height, new int[width * height]);
        Arrays.fill(rgbaData, fillColorRGBA);
    }

    /**
     * Creates a new bitmap with provided RGBA data.
     *
     * @param width width in pixels
     * @param height height in pixels
     * @param rgbaData packed RGBA data, should have length {@code width*height}.
     */
    public Bitmap(int width, int height, int[] rgbaData) {
        if (rgbaData.length != width * height)
            throw new IllegalArgumentException("RGBA data is wrong size");

        this.width = width;
        this.height = height;
        this.rgbaData = rgbaData;
    }

    private int pixelIdx(int x, int y) {
        return x + y * width;
    }

    @Override
    public int getPixel(int x, int y) {
        checkBounds(x, y);
        return rgbaData[pixelIdx(x, y)];
    }

    @Override
    public void setPixel(int x, int y, int colorRGBA) {
        checkBounds(x, y);
        rgbaData[pixelIdx(x, y)] = colorRGBA;
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

            System.arraycopy(src.rgbaData, src.pixelIdx(imgXInSrc, srcY), rgbaData, pixelIdx(x, dstY), imgW);
        }
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(x + ", " + y);
        }
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
    public int[] getRgbaData() {
        return rgbaData;
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
