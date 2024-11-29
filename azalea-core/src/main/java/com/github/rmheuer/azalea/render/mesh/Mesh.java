package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;

public final class Mesh implements SafeCloseable {
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    // Takes ownership of buffers
    public Mesh(VertexBuffer vertexBuffer, IndexBuffer indexBuffer) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
    }

    public void setData(IndexedVertexData data, DataUsage usage) {
        vertexBuffer.setData(data.getVertices(), usage);
        indexBuffer.setDataFrom(data, usage);
    }

    public VertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public boolean hasData() {
        return vertexBuffer.hasData() && indexBuffer.hasData();
    }

    @Override
    public void close() {
        vertexBuffer.close();
        indexBuffer.close();
    }
}
