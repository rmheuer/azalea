package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class Bitmap implements BitmapRegion, SafeCloseable {
    public static Bitmap decode(InputStream in) throws IOException {
        ByteBuffer data = IOUtil.readToByteBuffer(in);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer pChannels = stack.mallocInt(1);

            long pixelsPtr = nstbi_load_from_memory(
                    memAddress(data), data.remaining(),
                    memAddress(pWidth),
                    memAddress(pHeight),
                    memAddress(pChannels),
                    4
            );
            if (pixelsPtr == NULL) {
                throw new IOException("Failed to decode image: " + stbi_failure_reason());
            }

            memFree(data);

            int width = pWidth.get(0);
            int height = pHeight.get(0);
            return new Bitmap(width, height, ColorFormat.RGBA, pixelsPtr, true);
        }
    }


    public static Bitmap fromPixelData(int width, int height, ColorFormat colorFormat, ByteBuffer pixelData) {
        Bitmap bitmap = new Bitmap(width, height, colorFormat, memAddress(pixelData), false);
        if (pixelData.remaining() < bitmap.dataLen)
            throw new IllegalArgumentException("Not enough image data provided");

        return bitmap;
    }

    private final int width;
    private final int height;
    private final ColorFormat colorFormat;

    private final long dataPtr;
    private final int dataLen;
    private final boolean useStbFree;

    public Bitmap(int width, int height, ColorFormat format) {
        this(width, height, format, nmemAlloc(width * height * format.getByteCount()), false);
    }

    public Bitmap(int width, int height, ColorFormat format, int fillColor) {
        this(width, height, format);
        fill(fillColor);
    }

    private Bitmap(int width, int height, ColorFormat colorFormat, long dataPtr, boolean useStbFree) {
        this.width = width;
        this.height = height;
        this.colorFormat = colorFormat;
        this.dataPtr = dataPtr;
        this.dataLen = width * height * colorFormat.getByteCount();
        this.useStbFree = useStbFree;
    }

    private int pixelIdx(int x, int y) {
        return x + y * width;
    }

    @Override
    public int getPixel(int x, int y) {
        checkBounds(x, y);
        return colorFormat.getPixel(dataPtr, pixelIdx(x, y));
    }

    @Override
    public void setPixel(int x, int y, int color) {
        checkBounds(x, y);
        colorFormat.setPixel(dataPtr, pixelIdx(x, y), color);
    }

    @Override
    public void fill(int color) {
        colorFormat.fillBuffer(dataPtr, width * height, color);
    }

    @Override
    public void blit(BitmapRegion img, int x, int y) {
        if (img.getColorFormat() != colorFormat)
            throw new IllegalArgumentException("Color formats do not match");

        checkBounds(x, y);

        int imgW = img.getWidth();
        int imgH = img.getHeight();
        if (x + imgW > width || y + imgH > height)
            throw new IndexOutOfBoundsException("Image extends out of bounds");

        Bitmap src = img.getSourceBitmap();
        int byteCount = colorFormat.getByteCount();

        int srcStride = src.getWidth() * byteCount;
        long srcBase = src.dataPtr + img.getSourceOffsetX() * byteCount + img.getSourceOffsetY() * srcStride;

        int dstStride = width * byteCount;
        long dstBase = dataPtr + x * byteCount + y * dstStride;

        int rowBytes = imgW * byteCount;
        for (int row = 0; row < imgH; row++) {
            memCopy(
                    srcBase + row * srcStride,
                    dstBase + row * dstStride,
                    rowBytes
            );
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

    public ByteBuffer getPixelData() {
        return memByteBuffer(dataPtr, dataLen);
    }

    public long getPixelDataPtr() {
        return dataPtr;
    }

    @Override
    public boolean spansFullSource() {
        // This is the full source!
        return true;
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

    @Override
    public void close() {
        if (useStbFree) {
            nstbi_image_free(dataPtr);
        } else {
            nmemFree(dataPtr);
        }
    }
}
