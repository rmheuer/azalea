package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ChannelMapping;
import com.github.rmheuer.azalea.render.texture.Texture2D;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLTexture2D extends OpenGLTexture implements Texture2D {
    public OpenGLTexture2D() {
        setFilters(Filter.NEAREST);

        // Texture already bound from setFilters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void bindToTarget() {
        glBindTexture(GL_TEXTURE_2D, id);
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

    private void setChannelMapping(ChannelMapping mapping) {
        glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, new int[] {
                getGlChannelSource(mapping.getRed()),
                getGlChannelSource(mapping.getGreen()),
                getGlChannelSource(mapping.getBlue()),
                getGlChannelSource(mapping.getAlpha())
        });
    }

    @Override
    public void setSize(int width, int height, ChannelMapping mapping) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        setChannelMapping(mapping);
    }

    @Override
    public void setData(BitmapRegion data, ChannelMapping mapping) {
        glBindTexture(GL_TEXTURE_2D, id);
        setData(GL_TEXTURE_2D, data);
        setChannelMapping(mapping);
    }

    @Override
    public void setSubData(BitmapRegion data, int x, int y) {
        glBindTexture(GL_TEXTURE_2D, id);
        setSubData(GL_TEXTURE_2D, data, x, y);
    }

    @Override
    public void setMinFilter(Filter minFilter) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, glFilter(minFilter));
    }

    @Override
    public void setMagFilter(Filter magFilter) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, glFilter(magFilter));
    }
}
