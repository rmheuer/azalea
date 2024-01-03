package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SizeOf;

/**
 * Data types for vertex attributes.
 */
public enum AttribType {
    /** GLSL {@code float} */
    FLOAT(1),
    /** GLSL {@code vec2} */
    VEC2(2),
    /** GLSL {@code vec3} */
    VEC3(3),
    /** GLSL {@code vec4} */
    VEC4(4);

    private final int elemCount;

    AttribType(int elemCount) {
        this.elemCount = elemCount;
    }

    /**
     * Gets the number of values within this type.
     *
     * @return element count
     */
    public int getElemCount() {
        return elemCount;
    }

    /**
     * Gets the size of this type in bytes.
     *
     * @return size in bytes
     */
    public int sizeOf() {
        return elemCount * SizeOf.FLOAT;
    }
}
