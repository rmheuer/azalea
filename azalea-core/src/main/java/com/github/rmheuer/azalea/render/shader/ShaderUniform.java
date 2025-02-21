package com.github.rmheuer.azalea.render.shader;

import com.github.rmheuer.azalea.render.texture.Texture;
import org.joml.*;

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
     * @param x x component of the value to set
     * @param y y component of the value to set
     */
    void setVec2(float x, float y);

    /**
     * Sets the variable to a {@code vec2} value.
     *
     * @param v value to set
     */
    default void setVec2(Vector2fc v) {
        setVec2(v.x(), v.y());
    }

    /**
     * Sets the variable to a {@code vec3} value.
     *
     * @param x x component of the value to set
     * @param y y component of the value to set
     * @param z z component of the value to set
     */
    void setVec3(float x, float y, float z);

    /**
     * Sets the variable to a {@code vec3} value.
     *
     * @param v value to set
     */
    default void setVec3(Vector3fc v) {
        setVec3(v.x(), v.y(), v.z());
    }

    /**
     * Sets the variable to a {@code vec4} value.
     *
     * @param x x component of the value to set
     * @param y y component of the value to set
     * @param z z component of the value to set
     * @param w w component of the value to set
     */
    void setVec4(float x, float y, float z, float w);

    /**
     * Sets the variable to a {@code vec4} value.
     *
     * @param v value to set
     */
    default void setVec4(Vector4fc v) {
        setVec4(v.x(), v.y(), v.z(), v.w());
    }

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
     * Sets the variable to a {@code ivec2} value.
     *
     * @param x x component of the value to set
     * @param y y component of the value to set
     */
    void setIvec2(int x, int y);

    /**
     * Sets the variable to a {@code ivec2} value.
     *
     * @param v value to set
     */
    default void setIvec2(Vector2ic v) {
        setIvec2(v.x(), v.y());
    }

    /**
     * Sets the variable to a {@code ivec3} value.
     *
     * @param x x component of the value to set
     * @param y y component of the value to set
     * @param z z component of the value to set
     */
    void setIvec3(int x, int y, int z);

    /**
     * Sets the variable to a {@code ivec3} value.
     *
     * @param v value to set
     */
    default void setIvec3(Vector3ic v) {
        setIvec3(v.x(), v.y(), v.z());
    }

    /**
     * Sets the variable to a {@code ivec4} value.
     *
     * @param x x component of the value to set
     * @param y y component of the value to set
     * @param z z component of the value to set
     * @param w w component of the value to set
     */
    void setIvec4(int x, int y, int z, int w);

    /**
     * Sets the variable to a {@code ivec4} value.
     *
     * @param v value to set
     */
    default void setIvec4(Vector4ic v) {
        setIvec4(v.x(), v.y(), v.z(), v.w());
    }

    /**
     * Sets the variable to a texture value. The slot corresponds to the slot
     * index the texture is bound using
     * {@link com.github.rmheuer.azalea.render.pipeline.ActivePipeline#bindTexture(int, Texture)}.
     *
     * @param slotIdx texture slot to pass into the shader
     */
    void setTexture(int slotIdx);
}
