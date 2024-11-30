package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLIndexBuffer extends OpenGLBuffer implements IndexBuffer {
    private int primType;
    private int indexCount;

    public OpenGLIndexBuffer() {
        primType = -1;
    }

    @Override
    public void setData(List<Integer> idxList, PrimitiveType primitiveType, DataUsage usage) {
        primType = OpenGLRenderer.getGlPrimitiveType(primitiveType);

        indexCount = idxList.size();
        IntBuffer indices = MemoryUtil.memAllocInt(indexCount);
        for (int i : idxList)
            indices.put(i);
        indices.flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, getGlUsage(usage));

        MemoryUtil.memFree(indices);
    }

    @Override
    public boolean hasData() {
        return primType != -1;
    }

    public int getGlPrimType() {
        return primType;
    }

    @Override
    public int getIndexCount() {
        return indexCount;
    }
}