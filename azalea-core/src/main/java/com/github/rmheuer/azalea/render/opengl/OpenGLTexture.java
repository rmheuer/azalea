package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public abstract class OpenGLTexture implements Texture {
    protected final GLStateManager state;
    protected final int id;
    private ColorFormat colorFormat;

    public OpenGLTexture(GLStateManager state) {
        this.state = state;
        id = glGenTextures();
        colorFormat = null;
    }

    protected abstract void bindToTarget();

    private static final class DataPtr {
        public final long ptr;
        private final boolean owned;

        public DataPtr(long ptr, boolean owned) {
            this.ptr = ptr;
            this.owned = owned;
        }

        public void freeIfOwned() {
            if (owned) {
                MemoryUtil.nmemFree(ptr);
            }
        }
    }

    private DataPtr getBitmapData(BitmapRegion data) {
        Bitmap src = data.getSourceBitmap();
        long srcPtr = src.getPixelDataPtr();
        if (data.spansFullSource()) {
            return new DataPtr(srcPtr, false);
        }

        // Data is a subregion, copy out the region to a new buffer

        ColorFormat format = data.getColorFormat();
        int byteCount = format.getByteCount();

        int srcStride = src.getWidth() * byteCount;
        long base = srcPtr
                + data.getSourceOffsetX() * byteCount
                + data.getSourceOffsetY() * srcStride;

        int dataStride = data.getWidth() * byteCount;
        int dataH = data.getHeight();
        long newBuffer = MemoryUtil.nmemAlloc(dataStride * dataH);
        for (int y = 0; y < dataH; y++) {
            MemoryUtil.memCopy(base + y * srcStride, newBuffer + y * dataStride, dataStride);
        }

        return new DataPtr(newBuffer, true);
    }

    private int getGlFormat(ColorFormat format) {
        switch (format) {
            case RGBA: return GL_RGBA;
            case GRAYSCALE: return GL_RED;
            default:
                throw new IllegalArgumentException("Unknown color format: " + format);
        }
    }

    private int getGlInternalFormat(ColorFormat format) {
        switch (format) {
            case RGBA: return GL_RGBA8;
            case GRAYSCALE: return GL_R8;
            default:
                throw new IllegalStateException("Unknown color format: " + format);
        }
    }

    private void setUnpackAlignment(long ptr, int width, ColorFormat format) {
        int rowBytes = width * format.getByteCount();

        // Choose the largest alignment that the data matches
        long mask = ptr | rowBytes;
        int align;
        if ((mask & 0b11) == 0)
            align = 4;
        else if ((mask & 0b1) == 0)
            align = 2;
        else
            align = 1;

        state.setPixelUnpackAlignment(align);
    }

    protected void setData(int target, ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        this.colorFormat = colorFormat;
        setUnpackAlignment(MemoryUtil.memAddressSafe(data), width, colorFormat);
        glTexImage2D(
                target,
                0,
                getGlInternalFormat(colorFormat),
                width,
                height,
                0,
                getGlFormat(colorFormat),
                GL_UNSIGNED_BYTE,
                data
        );
    }

    protected void setData(int target, BitmapRegion data) {
        DataPtr ptr = getBitmapData(data);
        colorFormat = data.getColorFormat();

        setUnpackAlignment(ptr.ptr, data.getWidth(), colorFormat);
        glTexImage2D(
                target,
                0,
                getGlInternalFormat(colorFormat),
                data.getWidth(),
                data.getHeight(),
                0,
                getGlFormat(colorFormat),
                GL_UNSIGNED_BYTE,
                ptr.ptr
        );

        ptr.freeIfOwned();
    }

    protected void setSubData(int target, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        if (this.colorFormat == null)
            throw new IllegalStateException("Must call setData() or setSize() first");
        if (this.colorFormat != colorFormat)
            throw new IllegalArgumentException("Color format does not match: expected " + this.colorFormat + ", given " + colorFormat);

        int format = getGlFormat(colorFormat);
        setUnpackAlignment(MemoryUtil.memAddressSafe(data), width, colorFormat);
        glTexSubImage2D(target, 0, x, y, width, height, format, GL_UNSIGNED_BYTE, data);
    }

    protected void setSubData(int target, BitmapRegion data, int x, int y) {
        ColorFormat colorFormat = data.getColorFormat();
        if (this.colorFormat == null)
            throw new IllegalStateException("Must call setData() or setSize() first");
        if (this.colorFormat != colorFormat)
            throw new IllegalArgumentException("Color format does not match: expected " + this.colorFormat + ", given " + colorFormat);

        DataPtr ptr = getBitmapData(data);
        int format = getGlFormat(colorFormat);
        setUnpackAlignment(ptr.ptr, data.getWidth(), colorFormat);
        glTexSubImage2D(target, 0, x, y, data.getWidth(), data.getHeight(), format, GL_UNSIGNED_BYTE, ptr.ptr);

        ptr.freeIfOwned();
    }

    private int getGlChannelSource(ChannelMapping.Source source) {
        switch (source) {
            case RED: return GL_RED;
            case GREEN: return GL_GREEN;
            case BLUE: return GL_BLUE;
            case ALPHA: return GL_ALPHA;
            case ONE: return GL_ONE;
            case ZERO: return GL_ZERO;
            default:
                throw new IllegalArgumentException("Unknown channel source: " + source);
        }
    }

    protected void setChannelMapping(int target, ChannelMapping mapping) {
        glTexParameteriv(target, GL_TEXTURE_SWIZZLE_RGBA, new int[] {
                getGlChannelSource(mapping.getRed()),
                getGlChannelSource(mapping.getGreen()),
                getGlChannelSource(mapping.getBlue()),
                getGlChannelSource(mapping.getAlpha())
        });
    }

    protected int getGlFilter(Filter filter) {
        switch (filter) {
            case LINEAR: return GL_LINEAR;
            case NEAREST: return GL_NEAREST;
            default:
                throw new IllegalArgumentException(String.valueOf(filter));
        }
    }

    public void bind(int slot) {
        state.setActiveTexture(slot);
        bindToTarget();
    }

    @Override
    public void close() {
        glDeleteTextures(id);
        state.textureDeleted(id);
    }

    public int getId() {
        return id;
    }
}
