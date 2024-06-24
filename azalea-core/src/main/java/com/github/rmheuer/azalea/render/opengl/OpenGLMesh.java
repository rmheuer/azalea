package com.github.rmheuer.azalea.render.opengl;
import com.github.rmheuer.azalea.render.mesh.AttribType;
import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.mesh.MeshData;
import com.github.rmheuer.azalea.render.mesh.VertexLayout;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLMesh implements Mesh {
    private final int vao;
    private final int vbo;
    private final int ibo;

    private VertexLayout currentLayout;
    private int primType;

    private int indexCount;

    public OpenGLMesh() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ibo = glGenBuffers();

        currentLayout = null;
    }

    @Override
    public void setData(MeshData data, DataUsage usage) {
        int glUsage;
        switch (usage) {
            case STATIC: glUsage = GL_STATIC_DRAW; break;
            case DYNAMIC: glUsage = GL_DYNAMIC_DRAW; break;
            case STREAM: glUsage = GL_STREAM_DRAW; break;
            default: throw new IllegalArgumentException();
        }

        switch (data.getPrimitiveType()) {
            case POINTS: primType = GL_POINTS; break;
            case LINE_STRIP: primType = GL_LINE_STRIP; break;
            case LINE_LOOP: primType = GL_LINE_LOOP; break;
            case LINES: primType = GL_LINES; break;
            case TRIANGLE_STRIP: primType = GL_TRIANGLE_STRIP; break;
            case TRIANGLE_FAN: primType = GL_TRIANGLE_FAN; break;
            case TRIANGLES: primType = GL_TRIANGLES; break;
            default: throw new IllegalArgumentException();
        }

        VertexLayout layout = data.getVertexLayout();
        if (!layout.equals(currentLayout))
            setLayout(layout);

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data.getVertexBuf(), glUsage);

        List<Integer> idxList = data.getIndices();
        IntBuffer indices = MemoryUtil.memAllocInt(idxList.size());
        for (int i : idxList)
            indices.put(i);
        indices.flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, glUsage);
        MemoryUtil.memFree(indices);

        indexCount = idxList.size();
    }

    private void setLayout(VertexLayout layout) {
        AttribType[] attribs = layout.getTypes();
        int stride = layout.sizeOf();

        int offset = 0;
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
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

        if (currentLayout != null) {
            // Disable any attribs that are no longer used
            for (int i = attribs.length; i < currentLayout.getTypes().length; i++) {
                glDisableVertexAttribArray(i);
            }
        }

        currentLayout = layout;
    }

    void render() {
        if (currentLayout == null)
            return;

        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glDrawElements(primType, indexCount, GL_UNSIGNED_INT, 0L);
    }

    @Override
    public boolean hasData() {
        return currentLayout != null;
    }

    @Override
    public void close() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
        glDeleteVertexArrays(vao);
    }
}
