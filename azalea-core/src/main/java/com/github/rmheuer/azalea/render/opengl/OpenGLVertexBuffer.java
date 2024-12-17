package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.mesh.AttribType;
import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.VertexBuffer;
import com.github.rmheuer.azalea.render.mesh.VertexLayout;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLVertexBuffer extends OpenGLBuffer implements VertexBuffer {
    private final GLStateManager state;
    private VertexLayout dataLayout;
    private int vertexCount;

    public OpenGLVertexBuffer(GLStateManager state) {
        this.state = state;
        dataLayout = null;
    }

    @Override
    public void setData(ByteBuffer data, VertexLayout layout, DataUsage usage) {
        dataLayout = layout;
        vertexCount = data.remaining() / layout.sizeOf();

        state.bindArrayBuffer(id);
        glBufferData(GL_ARRAY_BUFFER, data, getGlUsage(usage));
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public boolean hasData() {
        return dataLayout != null;
    }

    public VertexLayout getDataLayout() {
        return dataLayout;
    }

    @Override
    public void close() {
        super.close();
        state.arrayBufferDeleted(id);
    }
}
