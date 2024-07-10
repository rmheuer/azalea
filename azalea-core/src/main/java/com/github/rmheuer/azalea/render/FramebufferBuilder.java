package com.github.rmheuer.azalea.render;

import com.github.rmheuer.azalea.render.texture.Texture2D;

public interface FramebufferBuilder {
    Texture2D addColorTexture(int index);
    void addColorAttachment(int index);

    void addDepthStencilAttachment();

    Framebuffer build();
}
