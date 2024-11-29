package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;

import java.util.List;

public interface IndexBuffer extends SafeCloseable {
    void setData(List<Integer> indices, PrimitiveType primType, DataUsage usage);

    default void setDataFrom(IndexedVertexData data, DataUsage usage) {
        setData(data.getIndices(), data.getPrimitiveType(), usage);
    }

    boolean hasData();
}
