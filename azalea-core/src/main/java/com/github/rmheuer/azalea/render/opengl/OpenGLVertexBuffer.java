package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.mesh.AttribType;
import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.VertexBuffer;
import com.github.rmheuer.azalea.render.mesh.VertexLayout;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLVertexBuffer extends OpenGLBuffer implements VertexBuffer {
    private VertexLayout dataLayout;

    private int vao;
    private VertexLayout vaoLayout;

    private int vertexCount;

    public OpenGLVertexBuffer() {
        dataLayout = null;
        vao = 0;
    }

    @Override
    public void setData(ByteBuffer data, VertexLayout layout, DataUsage usage) {
        dataLayout = layout;
        vertexCount = data.remaining() / layout.sizeOf();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, data, getGlUsage(usage));
    }

    public int getVAO() {
        if (vao == 0)
            vao = glGenVertexArrays();

        // Update VAO layout if changed
        if (!dataLayout.equals(vaoLayout)) {
            AttribType[] attribs = dataLayout.getTypes();
            int stride = dataLayout.sizeOf();

            int offset = 0;
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, id);
            for (int i = 0; i < attribs.length; i++) {
                AttribType type = attribs[i];
                glVertexAttribPointer(i,
                        type.getElemCount(),
                        type.getValueType() == AttribType.ValueType.FLOAT ? GL_FLOAT : GL_UNSIGNED_BYTE,
                        type.isNormalized(),
                        stride,
                        offset);
                glEnableVertexAttribArray(i);
                offset += type.sizeOf();
            }

            if (vaoLayout != null) {
                // Disable any attribs that are no longer used
                for (int i = attribs.length; i < dataLayout.getTypes().length; i++) {
                    glDisableVertexAttribArray(i);
                }
            }

            vaoLayout = dataLayout;
        }

        return vao;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public boolean hasData() {
        return dataLayout != null;
    }

    @Override
    public void close() {
        if (vao != 0)
            glDeleteVertexArrays(vao);
        super.close();
    }
}
