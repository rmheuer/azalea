package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SizeOf;

/**
 * Data types for vertex attributes.
 */
public enum AttribType {
    /** GLSL {@code float} */
    FLOAT(1, ValueType.FLOAT, false),
    /** GLSL {@code int} */
    INT(1, ValueType.INT, false),
    /** GLSL {@code uint} */
    UINT(1, ValueType.UINT, false),
    /** GLSL {@code vec2} */
    VEC2(2, ValueType.FLOAT, false),
    /** GLSL {@code vec3} */
    VEC3(3, ValueType.FLOAT, false),
    /** GLSL {@code vec4} */
    VEC4(4, ValueType.FLOAT, false),
    /** GLSL {@code vec4}, data is packed RGBA */
    COLOR_RGBA(4, ValueType.BYTE, true);

    public enum ValueType {
        FLOAT(SizeOf.FLOAT),
        INT(SizeOf.INT),
        UINT(SizeOf.INT),
        BYTE(SizeOf.BYTE);

        private final int sizeOf;

        ValueType(int sizeOf) {
            this.sizeOf = sizeOf;
        }
    }

    private final int elemCount;
    private final ValueType valueType;
    private final boolean normalized;

    AttribType(int elemCount, ValueType valueType, boolean normalized) {
        this.elemCount = elemCount;
        this.valueType = valueType;
        this.normalized = normalized;
    }

    /**
     * Gets the number of values within this type.
     *
     * @return element count
     */
    public int getElemCount() {
        return elemCount;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public boolean isNormalized() {
        return normalized;
    }

    /**
     * Gets the size of this type in bytes.
     *
     * @return size in bytes
     */
    public int sizeOf() {
        return elemCount * valueType.sizeOf;
    }
}
