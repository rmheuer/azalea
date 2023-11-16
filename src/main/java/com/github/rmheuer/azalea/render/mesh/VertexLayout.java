package com.github.rmheuer.azalea.render.mesh;

import java.util.Arrays;
import java.util.Objects;

public final class VertexLayout {
    private final AttribType[] types;
    private final int sizeOf;

    public VertexLayout(AttribType... types) {
        this.types = types;

        int sz = 0;
        for (AttribType type : types)
            sz += type.sizeOf();
        sizeOf = sz;
    }

    public AttribType[] getTypes() {
        return types;
    }

    public int sizeOf() {
        return sizeOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexLayout that = (VertexLayout) o;
        return sizeOf == that.sizeOf &&
                Arrays.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sizeOf);
        result = 31 * result + Arrays.hashCode(types);
        return result;
    }
}
