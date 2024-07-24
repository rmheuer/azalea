package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.Framebuffer;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLFramebuffer implements Framebuffer {
    private final int id;
    private final int[] texIds;
    private final int[] rboIds;

    private final int width, height;

    public OpenGLFramebuffer(int id, int[] texIds, int[] rboIds, int width, int height) {
        this.id = id;
        this.texIds = texIds;
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
    public void close() {
        glDeleteFramebuffers(id);
        if (texIds.length > 0)
            glDeleteTextures(texIds);
        if (rboIds.length > 0)
            glDeleteRenderbuffers(rboIds);
    }
}
