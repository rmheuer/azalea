package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.IndexFormat;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLIndexBuffer extends OpenGLBuffer implements IndexBuffer {
    private int format;
    private int primType;
    private int indexCount;

    public OpenGLIndexBuffer() {
        primType = -1;
    }

    @Override
    public void setData(ByteBuffer data, IndexFormat format, PrimitiveType primType, DataUsage usage) {
        this.format = format == IndexFormat.UNSIGNED_INT ? GL_UNSIGNED_INT : GL_UNSIGNED_SHORT;
        this.primType = OpenGLRenderer.getGlPrimitiveType(primType);

        indexCount = data.remaining() / format.sizeOf();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, getGlUsage(usage));
    }

    @Override
    public boolean hasData() {
        return primType != -1;
    }

    public int getGlFormat() {
        return format;
    }

    public int getGlPrimType() {
        return primType;
    }

    @Override
    public int getIndexCount() {
        return indexCount;
    }
}
