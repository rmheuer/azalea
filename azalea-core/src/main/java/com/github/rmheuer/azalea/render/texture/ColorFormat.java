package com.github.rmheuer.azalea.render.texture;

import org.lwjgl.system.MemoryUtil;

public enum ColorFormat {
    /**
     * 8-bit red, green, blue, alpha components. Each color value is stored as
     * an {@code int}.
     */
    RGBA {
        @Override
        public int getByteCount() {
            return 4;
        }

        @Override
        public int getPixel(long dataPtr, int pixelIdx) {
            return MemoryUtil.memGetInt(dataPtr + pixelIdx * 4);
        }

        @Override
        public void setPixel(long dataPtr, int pixelIdx, int color) {
            MemoryUtil.memPutInt(dataPtr + pixelIdx * 4, color);
        }

        @Override
        public void fillBuffer(long dataPtr, int pixelCount, int color) {
            long twoPixels = (long) color << 32 | color;
            for (int i = 0; i < pixelCount / 2; i++) {
                MemoryUtil.memPutLong(dataPtr + i * 8, twoPixels);
            }

            if ((pixelCount & 1) != 0) {
                MemoryUtil.memPutInt(dataPtr + pixelCount * 4 - 4, color);
            }
        }
    },
    /**
     * 8-bit grayscale (one component), stored as the red component. Each color
     * value is stored as a {@code byte}.
     */
    GRAYSCALE {
        @Override
        public int getByteCount() {
            return 1;
        }

        @Override
        public int getPixel(long dataPtr, int pixelIdx) {
            return MemoryUtil.memGetByte(dataPtr + pixelIdx);
        }

        @Override
        public void setPixel(long dataPtr, int pixelIdx, int color) {
            MemoryUtil.memPutByte(dataPtr + pixelIdx, (byte) color);
        }

        @Override
        public void fillBuffer(long dataPtr, int pixelCount, int color) {
            MemoryUtil.memSet(dataPtr, color, pixelCount);
        }
    };

    /**
     * Gets the number of bytes each pixel value uses.
     * @return size of pixel
     */
    public abstract int getByteCount();

    public abstract int getPixel(long dataPtr, int pixelIdx);

    public abstract void setPixel(long dataPtr, int pixelIdx, int color);

    public abstract void fillBuffer(long dataPtr, int pixelCount, int color);
}
