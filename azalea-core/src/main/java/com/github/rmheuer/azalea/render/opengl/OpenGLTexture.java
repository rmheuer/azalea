package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public abstract class OpenGLTexture implements Texture {
    protected final GLStateManager state;
    protected final int id;
    private ColorFormat colorFormat;

    private Filter minFilter;
    private MipMapMode mipMapMode;

    public OpenGLTexture(GLStateManager state) {
        this.state = state;
        id = glGenTextures();
        colorFormat = null;

        minFilter = null; // Should be set by subclasses
        mipMapMode = MipMapMode.DISABLED;
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

    protected void setMipMapData(int target, int mipLevel, ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        this.colorFormat = colorFormat;
        setUnpackAlignment(MemoryUtil.memAddressSafe(data), width, colorFormat);
        glTexImage2D(
                target,
                mipLevel,
                getGlInternalFormat(colorFormat),
                width,
                height,
                0,
                getGlFormat(colorFormat),
                GL_UNSIGNED_BYTE,
                data
        );
    }

    protected void setMipMapData(int target, int mipLevel, BitmapRegion data) {
        DataPtr ptr = getBitmapData(data);
        colorFormat = data.getColorFormat();

        setUnpackAlignment(ptr.ptr, data.getWidth(), colorFormat);
        glTexImage2D(
                target,
                mipLevel,
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

    protected void setMipMapSubData(int target, int mipLevel, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        if (this.colorFormat == null)
            throw new IllegalStateException("Must call setData() or setSize() first");
        if (this.colorFormat != colorFormat)
            throw new IllegalArgumentException("Color format does not match: expected " + this.colorFormat + ", given " + colorFormat);

        int format = getGlFormat(colorFormat);
        setUnpackAlignment(MemoryUtil.memAddressSafe(data), width, colorFormat);
        glTexSubImage2D(target, mipLevel, x, y, width, height, format, GL_UNSIGNED_BYTE, data);
    }

    protected void setMipMapSubData(int target, int mipLevel, BitmapRegion data, int x, int y) {
        ColorFormat colorFormat = data.getColorFormat();
        if (this.colorFormat == null)
            throw new IllegalStateException("Must call setData() or setSize() first");
        if (this.colorFormat != colorFormat)
            throw new IllegalArgumentException("Color format does not match: expected " + this.colorFormat + ", given " + colorFormat);

        DataPtr ptr = getBitmapData(data);
        int format = getGlFormat(colorFormat);
        setUnpackAlignment(ptr.ptr, data.getWidth(), colorFormat);
        glTexSubImage2D(target, mipLevel, x, y, data.getWidth(), data.getHeight(), format, GL_UNSIGNED_BYTE, ptr.ptr);

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

    private void updateMinFilterMode(int target) {
        boolean linear = minFilter == Filter.LINEAR;

        int mode;
        switch (mipMapMode) {
            case DISABLED:
                mode = linear ? GL_LINEAR : GL_NEAREST;
                break;
            case NEAREST:
                mode = linear ? GL_LINEAR_MIPMAP_NEAREST : GL_NEAREST_MIPMAP_NEAREST;
                break;
            case LINEAR:
                mode = linear ? GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST_MIPMAP_LINEAR;
                break;
            default:
                throw new AssertionError();
        }

        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, mode);
    }

    protected void setMinFilter(int target, Filter minFilter) {
        this.minFilter = minFilter;
        updateMinFilterMode(target);
    }

    protected void setMipMapMode(int target, MipMapMode mode) {
        this.mipMapMode = mode;
        updateMinFilterMode(target);
    }

    protected void setMipMapRange(int target, int minLevel, int maxLevel) {
        glTexParameteri(target, GL_TEXTURE_BASE_LEVEL, minLevel);
        glTexParameteri(target, GL_TEXTURE_MAX_LEVEL, maxLevel);
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
