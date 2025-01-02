package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.math.MathUtil;
import com.github.rmheuer.azalea.render.Colors;
import org.lwjgl.system.MemoryUtil;

public enum ColorFormat {
    /**
     * 8-bit red, green, blue, alpha components. Each color value is stored as
     * an {@code int}. These color values can be manipulated using
     * {@link com.github.rmheuer.azalea.render.Colors.RGBA}.
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
            long colorL = Integer.toUnsignedLong(color);
            long twoPixels = colorL | (colorL << 32);
            for (int i = 0; i < pixelCount / 2; i++) {
                MemoryUtil.memPutLong(dataPtr + i * 8, twoPixels);
            }

            if ((pixelCount & 1) != 0) {
                MemoryUtil.memPutInt(dataPtr + pixelCount * 4 - 4, color);
            }
        }

        @Override
        public int lerp(int colorA, int colorB, float f) {
            return Colors.RGBA.lerp(colorA, colorB, f);
        }
    },

    /**
     * 8-bit grayscale (one component), stored as the red component. Each color
     * value is stored as a {@code byte}. The value ranges from 0 (black) to
     * 255 (white).
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

        @Override
        public int lerp(int colorA, int colorB, float f) {
            return MathUtil.lerpInt(colorA, colorB, f);
        }
    };

    /**
     * Gets the number of bytes each pixel value uses.
     * @return size of pixel
     */
    public abstract int getByteCount();

    /**
     * Gets the color of a pixel from a native data buffer.
     *
     * @param dataPtr pointer to the data buffer
     * @param pixelIdx index of the pixel
     * @return color of the pixel
     */
    public abstract int getPixel(long dataPtr, int pixelIdx);

    /**
     * Sets the color of a pixel in a native data buffer.
     *
     * @param dataPtr pointer to the data buffer
     * @param pixelIdx index of the pixel
     * @param color color to set the pixel
     */
    public abstract void setPixel(long dataPtr, int pixelIdx, int color);

    /**
     * Fills a native data buffer with a specified color.
     *
     * @param dataPtr pointer to the data buffer
     * @param pixelCount number of pixels to fill
     * @param color color to fill the buffer with
     */
    public abstract void fillBuffer(long dataPtr, int pixelCount, int color);

    public abstract int lerp(int colorA, int colorB, float f);
}
