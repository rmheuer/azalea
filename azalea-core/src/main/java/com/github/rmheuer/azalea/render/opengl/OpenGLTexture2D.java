package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.Texture2D;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLTexture2D implements Texture2D, OpenGLTexture {
    private final int id;

    public OpenGLTexture2D() {
        id = glGenTextures();
        setFilters(Filter.NEAREST);
    }

    @Override
    public void setData(BitmapRegion data) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.getRgbaData());
    }

    @Override
    public void setSubData(BitmapRegion data, int x, int y) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, data.getWidth(), data.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, data.getRgbaData());
    }

    private int glFilter(Filter filter) {
        switch (filter) {
            case LINEAR: return GL_LINEAR;
            case NEAREST: return GL_NEAREST;
            default:
                throw new IllegalArgumentException(String.valueOf(filter));
        }
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

    @Override
    public void bind(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public void close() {
        glDeleteTextures(id);
    }

    public int getId() {
        return id;
    }
}
