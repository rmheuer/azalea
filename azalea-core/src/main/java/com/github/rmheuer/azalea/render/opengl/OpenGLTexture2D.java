package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ChannelMapping;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.Texture2D;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLTexture2D extends OpenGLTexture implements Texture2D {
    public OpenGLTexture2D(GLStateManager state) {
        super(state);
        setFilters(Filter.NEAREST);
        setWrappingModes(WrappingMode.CLAMP_TO_EDGE);
    }

    @Override
    protected void bindToTarget() {
        state.bindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public void setSize(int width, int height, ColorFormat colorFormat) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setData(null, width, height, colorFormat);
    }

    @Override
    public void setMipMapData(int mipLevel, BitmapRegion data) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMipMapData(GL_TEXTURE_2D, mipLevel, data);
    }

    @Override
    public void setMipMapData(int mipLevel, ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMipMapData(GL_TEXTURE_2D, mipLevel, data, width, height, colorFormat);
    }

    @Override
    public void setMipMapSubData(int mipLevel, BitmapRegion data, int x, int y) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMipMapSubData(GL_TEXTURE_2D, mipLevel, data, x, y);
    }

    @Override
    public void setMipMapSubData(int mipLevel, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMipMapSubData(GL_TEXTURE_2D, mipLevel, data, width, height, colorFormat, x, y);
    }

    @Override
    public void generateAllMipMaps() {
        state.bindTexture(GL_TEXTURE_2D, id);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public void setMinFilter(Filter minFilter) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMinFilter(GL_TEXTURE_2D, minFilter);
    }

    @Override
    public void setMagFilter(Filter magFilter) {
        state.bindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, getGlFilter(magFilter));
    }

    private int getGlWrappingMode(WrappingMode mode) {
        switch (mode) {
            case REPEAT: return GL_REPEAT;
            case REPEAT_MIRRORED: return GL_MIRRORED_REPEAT;
            case CLAMP_TO_EDGE: return GL_CLAMP_TO_EDGE;
            default:
                throw new IllegalArgumentException("Unknown wrapping mode: " + mode);
        }
    }

    @Override
    public void setWrappingModeU(WrappingMode mode) {
        state.bindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, getGlWrappingMode(mode));
    }

    @Override
    public void setWrappingModeV(WrappingMode mode) {
        state.bindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, getGlWrappingMode(mode));
    }

    @Override
    public void setMipMapMode(MipMapMode mode) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMipMapMode(GL_TEXTURE_2D, mode);
    }

    @Override
    public void setMipMapRange(int minLevel, int maxLevel) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setMipMapRange(GL_TEXTURE_2D, minLevel, maxLevel);
    }

    @Override
    public void setChannelMapping(ChannelMapping mapping) {
        state.bindTexture(GL_TEXTURE_2D, id);
        setChannelMapping(GL_TEXTURE_2D, mapping);
    }
}
