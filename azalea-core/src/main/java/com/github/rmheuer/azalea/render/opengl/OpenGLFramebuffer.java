package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.Framebuffer;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import org.joml.Vector2i;

import java.util.Map;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLFramebuffer implements Framebuffer {
    private final int id;
    private final Map<Integer, OpenGLTexture2D> colorTextures;
    private final int[] rboIds;

    private final int width, height;

    public OpenGLFramebuffer(int id, Map<Integer, OpenGLTexture2D> colorTextures, int[] rboIds, int width, int height) {
        this.id = id;
        this.colorTextures = colorTextures;
        this.rboIds = rboIds;
        this.width = width;
        this.height = height;
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    @Override
    public Texture2D getColorTexture(int index) {
        return colorTextures.get(index);
    }

    @Override
    public void close() {
        glDeleteFramebuffers(id);
        for (OpenGLTexture2D texture : colorTextures.values()) {
            texture.close();
        }
        if (rboIds.length > 0)
            glDeleteRenderbuffers(rboIds);
    }
}