package com.github.rmheuer.azalea.render.pipeline;

import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.shader.ShaderUniform;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.utils.SafeCloseable;

/**
 * Provides access to an actively bound render pipeline.
 */
public interface ActivePipeline extends SafeCloseable {
    /**
     * Binds a texture into a specified texture slot.
     *
     * @param slot slot to bind from 0 to 15
     * @param texture texture to bind into the slot
     */
    void bindTexture(int slot, Texture texture);

    /**
     * Gets a shader uniform by name. This name is the name of the uniform
     * variable in the GLSL shader.
     *
     * @param name uniform name
     * @return uniform
     */
    ShaderUniform getUniform(String name);

    /**
     * Renders a mesh to the current framebuffer, using the bound textures.
     *
     * @param mesh mesh to render
     */
    void draw(Mesh mesh);
}
