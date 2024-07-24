package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.Texture;

import static org.lwjgl.opengl.GL33C.*;

public abstract class OpenGLTexture implements Texture {
    protected final int id;

    public OpenGLTexture() {
        id = glGenTextures();
    }

    protected abstract void bindToTarget();

    protected void setData(int target, BitmapRegion data) {
        glTexImage2D(target, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.getRgbaData());
    }

    protected void setSubData(int target, BitmapRegion data, int x, int y) {
        glTexSubImage2D(target, 0, x, y, data.getWidth(), data.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, data.getRgbaData());
    }

    protected int glFilter(Filter filter) {
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
