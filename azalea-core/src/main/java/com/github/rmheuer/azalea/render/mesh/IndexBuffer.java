package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.List;

public interface IndexBuffer extends SafeCloseable {
    void setData(ByteBuffer data, IndexFormat format, PrimitiveType primType, DataUsage usage);

    default void setData(IndexData data, DataUsage usage) {
        setData(data.getIndexBuf(), data.getFormat(), data.getPrimitiveType(), usage);
    }

    default void setData(List<Integer> indices, PrimitiveType primType, DataUsage usage) {
        ByteBuffer buf = MemoryUtil.memAlloc(indices.size() * SizeOf.INT);
        for (int i : indices)
            buf.putInt(i);
        buf.flip();

        setData(buf, IndexFormat.UNSIGNED_INT, primType, usage);

        MemoryUtil.memFree(buf);
    }

    default void setDataFrom(IndexedVertexData data, DataUsage usage) {
        setData(data.getIndices(), usage);
    }

    boolean hasData();

    int getIndexCount();
}
