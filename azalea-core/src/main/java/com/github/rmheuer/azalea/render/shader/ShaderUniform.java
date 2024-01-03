package com.github.rmheuer.azalea.render.shader;

import com.github.rmheuer.azalea.render.texture.Texture;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * A uniform variable inside a shader program. These variables can be set from
 * the CPU between draw calls.
 */
public interface ShaderUniform {
    /**
     * Sets the variable to a {@code float} value.
     *
     * @param f value to set
     */
    void setFloat(float f);

    /**
     * Sets the variable to a {@code vec2} value.
     *
     * @param v value to set
     */
    void setVec2(Vector2fc v);

    /**
     * Sets the variable to a {@code vec3} value.
     *
     * @param v value to set
     */
    void setVec3(Vector3fc v);

    /**
     * Sets the variable to a {@code vec4} value.
     *
     * @param v value to set
     */
    void setVec4(Vector4fc v);

    /**
     * Sets the variable to a {@code mat4} value.
     *
     * @param m value to set
     */
    void setMat4(Matrix4fc m);

    /**
     * Sets the variable to a {@code int} value.
     *
     * @param i value to set
     */
    void setInt(int i);

    /**
     * Sets the variable to a texture value. The slot corresponds to the slot
     * index the texture is bound using
     * {@link com.github.rmheuer.azalea.render.pipeline.ActivePipeline#bindTexture(int, Texture)}.
     *
     * @param slotIdx texture slot to pass into the shader
     */
    void setTexture(int slotIdx);
}
