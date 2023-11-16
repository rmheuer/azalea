package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.texture.Texture;

public interface OpenGLTexture extends Texture {
    void bind(int slot);
}
