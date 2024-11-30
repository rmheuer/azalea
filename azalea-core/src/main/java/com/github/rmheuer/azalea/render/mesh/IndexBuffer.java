package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

public interface IndexBuffer extends SafeCloseable {
    enum IndexFormat {
        UNSIGNED_SHORT(SizeOf.SHORT),
        UNSIGNED_INT(SizeOf.INT);

        private final int sizeOf;

        IndexFormat(int sizeOf) {
            this.sizeOf = sizeOf;
        }

        public int sizeOf() {
            return sizeOf;
        }
    }

    void setData(ByteBuffer data, IndexFormat format, PrimitiveType primType, DataUsage usage);

    default void setData(List<Integer> indices, PrimitiveType primType, DataUsage usage) {
        ByteBuffer buf = MemoryUtil.memAlloc(indices.size() * SizeOf.INT);
        for (int i : indices)
            buf.putInt(i);
        buf.flip();

        setData(buf, IndexFormat.UNSIGNED_INT, primType, usage);

        MemoryUtil.memFree(buf);
    }

    default void setDataFrom(IndexedVertexData data, DataUsage usage) {
        setData(data.getIndices(), data.getPrimitiveType(), usage);
    }

    boolean hasData();

    int getIndexCount();
}
