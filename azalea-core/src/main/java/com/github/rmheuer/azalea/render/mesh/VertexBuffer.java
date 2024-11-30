package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;

import java.nio.ByteBuffer;

public interface VertexBuffer extends SafeCloseable {
    void setData(ByteBuffer data, VertexLayout layout, DataUsage usage);

    default void setData(VertexData data, DataUsage usage) {
        setData(data.getVertexBuf(), data.getLayout(), usage);
    }

    default void setDataFrom(MeshData data, DataUsage usage) {
        setData(data.getVertices(), usage);
    }

    boolean hasData();

    int getVertexCount();
}
