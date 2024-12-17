package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.framebuffer.Framebuffer;
import com.github.rmheuer.azalea.render.framebuffer.FramebufferBuilder;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class OpenGLFramebufferBuilder implements FramebufferBuilder {
    private final GLStateManager state;
    private final int width, height;
    private final int id;

    private final Map<Integer, OpenGLTexture2D> colorTextures;
    private final List<Integer> rboIds;

    public OpenGLFramebufferBuilder(GLStateManager state, int width, int height) {
        this.state = state;
        this.width = width;
        this.height = height;
        id = glGenFramebuffers();

        colorTextures = new HashMap<>();
        rboIds = new ArrayList<>();
    }

    // Returned texture should not be closed, will be closed when the
    // framebuffer is closed
    @Override
    public Texture2D addColorTexture(int index) {
        OpenGLTexture2D tex = new OpenGLTexture2D(state);
        colorTextures.put(index, tex);

        state.bindTexture(GL_TEXTURE_2D, tex.getId());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

        state.bindFramebuffer(id);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, GL_TEXTURE_2D, tex.getId(), 0);

        return tex;
    }

    private void addAttachment(int format, int attachment) {
        int rbo = glGenRenderbuffers();
        rboIds.add(rbo);

        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, format, width, height);

        state.bindFramebuffer(id);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, rbo);
    }

    @Override
    public void addColorAttachment(int index) {
        addAttachment(GL_RGBA8, GL_COLOR_ATTACHMENT0 + index);
    }

    @Override
    public void addDepthStencilAttachment() {
        addAttachment(GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL_ATTACHMENT);
    }

    @Override
    public Framebuffer build() {
        state.bindFramebuffer(id);
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer incomplete");
        }

        int[] rbo = ArrayUtil.toArray(rboIds);
        return new OpenGLFramebuffer(state, id, colorTextures, rbo, width, height);
    }
}
