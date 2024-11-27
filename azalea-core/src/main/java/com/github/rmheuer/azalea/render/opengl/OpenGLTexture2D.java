package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
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
    public void setData(BitmapRegion data) {
        glBindTexture(GL_TEXTURE_2D, id);
        setData(GL_TEXTURE_2D, data);
    }

    @Override
    public void setSubData(BitmapRegion data, int x, int y) {
        glBindTexture(GL_TEXTURE_2D, id);
        setSubData(GL_TEXTURE_2D, data, x, y);
    }

    @Override
    public void setSize(int width, int height) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
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
