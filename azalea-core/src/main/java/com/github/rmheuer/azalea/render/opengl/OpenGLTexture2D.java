package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ChannelMapping;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
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

    @Override
    public void setSize(int width, int height, ColorFormat colorFormat) {
        glBindTexture(GL_TEXTURE_2D, id);
        setData(null, width, height, colorFormat);
    }

    @Override
    public void setData(BitmapRegion data) {
        glBindTexture(GL_TEXTURE_2D, id);
        setData(GL_TEXTURE_2D, data);
    }

    @Override
    public void setData(ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        glBindTexture(GL_TEXTURE_2D, id);
        setData(GL_TEXTURE_2D, data, width, height, colorFormat);
    }

    @Override
    public void setSubData(BitmapRegion data, int x, int y) {
        glBindTexture(GL_TEXTURE_2D, id);
        setSubData(GL_TEXTURE_2D, data, x, y);
    }

    @Override
    public void setSubData(ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        glBindTexture(GL_TEXTURE_2D, id);
        setSubData(GL_TEXTURE_2D, data, width, height, colorFormat, x, y);
    }

    @Override
    public void setMinFilter(Filter minFilter) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, getGlFilter(minFilter));
    }

    @Override
    public void setMagFilter(Filter magFilter) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, getGlFilter(magFilter));
    }

    @Override
    public void setChannelMapping(ChannelMapping mapping) {
        setChannelMapping(GL_TEXTURE_2D, mapping);
    }
}
