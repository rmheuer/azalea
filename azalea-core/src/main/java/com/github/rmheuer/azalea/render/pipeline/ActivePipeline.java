package com.github.rmheuer.azalea.render.pipeline;

import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import com.github.rmheuer.azalea.render.mesh.VertexBuffer;
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
     * Renders a buffer of vertices to the current framebuffer, using the bound
     * textures. The vertices in the buffer will be processed in order.
     *
     * @param vertices buffer containing vertices to render
     * @param primType type of primitives to combine vertices into
     * @param startIdx index to start at within the vertex buffer
     * @param count number of vertices starting at {@code startIdx} to render
     */
    void draw(VertexBuffer vertices, PrimitiveType primType, int startIdx, int count);

    /**
     * Renders a mesh to the current framebuffer, using the bound textures. The
     * vertices will be processed in the order defined by the index buffer.
     *
     * @param vertices vertices to render, referenced by the indices
     * @param indices indices into the vertex buffer to draw
     * @param startIdx index to start at within the index buffer
     * @param count number of indices starting at {@code startIdx} to render
     */
    void draw(VertexBuffer vertices, IndexBuffer indices, int startIdx, int count);

    /**
     * Renders a buffer of vertices to the current framebuffer, using the bound
     * textures. The vertices in the buffer will be processed in order.
     *
     * @param vertices buffer containing vertices to render
     * @param primType type of primitives to combine vertices into
     */
    default void draw(VertexBuffer vertices, PrimitiveType primType) {
        draw(vertices, primType, 0, vertices.getVertexCount());
    }

    /**
     * Renders a mesh to the current framebuffer, using the bound textures. The
     * vertices will be processed in the order defined by the index buffer.
     *
     * @param vertices vertices to render, referenced by the indices
     * @param indices indices into the vertex buffer to draw
     */
    default void draw(VertexBuffer vertices, IndexBuffer indices) {
        draw(vertices, indices, 0, indices.getIndexCount());
    }

    /**
     * Renders a mesh to the current framebuffer, using the bound textures.
     *
     * @param mesh mesh to render
     */
    default void draw(Mesh mesh) {
        draw(mesh.getVertexBuffer(), mesh.getIndexBuffer());
    }
}
