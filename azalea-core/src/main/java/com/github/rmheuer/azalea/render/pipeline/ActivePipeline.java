package com.github.rmheuer.azalea.render.pipeline;

import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.shader.ShaderUniform;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.utils.SafeCloseable;

public interface ActivePipeline extends SafeCloseable {
    void bindTexture(int slot, Texture texture);

    ShaderUniform getUniform(String name);

    void draw(Mesh mesh);
}
