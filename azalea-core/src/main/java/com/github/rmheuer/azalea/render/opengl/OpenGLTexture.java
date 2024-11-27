package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ChannelMapping;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public abstract class OpenGLTexture implements Texture {
    protected final int id;
    private ColorFormat colorFormat;

    public OpenGLTexture() {
        id = glGenTextures();
        colorFormat = null;
    }

    protected abstract void bindToTarget();

    private ByteBuffer bitmapToByteBuffer(BitmapRegion data) {
        ByteBuffer buf;
        switch (data.getColorFormat()) {
            case RGBA: {
                int[] rgba = data.getDataRGBA();
                buf = MemoryUtil.memAlloc(rgba.length * SizeOf.INT);
                buf.asIntBuffer().put(rgba);
                break;
            }
            case GRAYSCALE: {
                byte[] gray = data.getDataGrayscale();
                buf = MemoryUtil.memAlloc(gray.length);
                buf.put(gray);
                break;
            }
            default:
                throw new AssertionError();
        }
        buf.flip();
        return buf;
    }

    private int getGlFormat(ColorFormat format) {
        switch (format) {
            case RGBA: return GL_RGBA;
            case GRAYSCALE: return GL_RED;
            default:
                throw new IllegalArgumentException("Unknown color format: " + format);
        }
    }

    protected void setData(int target, ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        this.colorFormat = colorFormat;
        int format = getGlFormat(colorFormat);
        glTexImage2D(target, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    protected void setData(int target, BitmapRegion data) {
        ByteBuffer buf = bitmapToByteBuffer(data);
        setData(target, buf, data.getWidth(), data.getHeight(), data.getColorFormat());
        MemoryUtil.memFree(buf);
    }

    protected void setSubData(int target, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        if (this.colorFormat == null)
            throw new IllegalStateException("Must call setData() or setSize() first");
        if (this.colorFormat != colorFormat)
            throw new IllegalArgumentException("Color format does not match: expected " + this.colorFormat + ", given " + colorFormat);

        int format = getGlFormat(colorFormat);
        glTexSubImage2D(target, 0, x, y, width, height, format, GL_UNSIGNED_BYTE, data);
    }

    protected void setSubData(int target, BitmapRegion data, int x, int y) {
        ByteBuffer buf = bitmapToByteBuffer(data);
        setSubData(target, buf, data.getWidth(), data.getHeight(), data.getColorFormat(), x, y);
        MemoryUtil.memFree(buf);
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
        glActiveTexture(GL_TEXTURE0 + slot);
        bindToTarget();
    }

    @Override
    public void close() {
        glDeleteTextures(id);
    }

    public int getId() {
        return id;
    }
}
