package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.Framebuffer;
import com.github.rmheuer.azalea.render.FramebufferBuilder;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class OpenGLFramebufferBuilder implements FramebufferBuilder {
    private final int width, height;
    private final int id;

    private final List<Integer> texIds, rboIds;

    public OpenGLFramebufferBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        id = glGenFramebuffers();

        texIds = new ArrayList<>();
        rboIds = new ArrayList<>();
    }

    // Returned texture should not be closed, will be closed when the
    // framebuffer is closed
    @Override
    public Texture2D addColorTexture(int index) {
        OpenGLTexture2D tex = new OpenGLTexture2D();
        texIds.add(tex.getId());

        glBindTexture(GL_TEXTURE_2D, tex.getId());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

        glBindFramebuffer(GL_FRAMEBUFFER, id);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, GL_TEXTURE_2D, tex.getId(), 0);

        return tex;
    }

    private void addAttachment(int format, int attachment) {
        int rbo = glGenRenderbuffers();
        rboIds.add(rbo);

        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, format, width, height);

        glBindFramebuffer(GL_FRAMEBUFFER, id);
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
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer incomplete");
        }

        int[] tex = ArrayUtil.toArray(texIds);
        int[] rbo = ArrayUtil.toArray(rboIds);
        return new OpenGLFramebuffer(id, tex, rbo, width, height);
    }
}
