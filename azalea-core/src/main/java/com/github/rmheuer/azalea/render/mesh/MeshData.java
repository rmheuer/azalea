package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class MeshData implements SafeCloseable {
    private static final int INITIAL_CAPACITY = 16;

    private final PrimitiveType primType;
    private final VertexLayout layout;
    private int layoutElemIdx;

    private ByteBuffer vertexBuf;
    private final List<Integer> indices;
    private int mark;
    private boolean finished;
    private int finalVertexCount;

    public MeshData(PrimitiveType primType, VertexLayout layout) {
        this.primType = primType;
        this.layout = layout;
        layoutElemIdx = 0;

        indices = new ArrayList<>();
        mark = 0;
        finished = false;

        vertexBuf = MemoryUtil.memAlloc(INITIAL_CAPACITY * layout.sizeOf());
    }

    private void ensureSpace(int spaceBytes) {
        if (vertexBuf.remaining() < spaceBytes) {
            int cap = vertexBuf.capacity();
            vertexBuf = MemoryUtil.memRealloc(vertexBuf, Math.max(cap + spaceBytes, cap * 2));
        }
    }

    private void prepare(AttribType type) {
        AttribType[] types = layout.getTypes();

        ensureSpace(type.sizeOf());
        AttribType layoutType = types[layoutElemIdx++];
        if (layoutType != type)
            throw new IllegalStateException("Incorrect attribute added for format (added " + type + ", layout specifies " + layoutType + ")");
        if (layoutElemIdx >= types.length)
            layoutElemIdx = 0;
    }

    public MeshData mark() {
        mark = getVertexCount();
        return this;
    }

    public MeshData putFloat(float f) {
        prepare(AttribType.FLOAT);
        vertexBuf.putFloat(f);
        return this;
    }

    public MeshData putVec2(Vector2f vec) { return putVec2(vec.x, vec.y); }
    public MeshData putVec2(float x, float y) {
        prepare(AttribType.VEC2);
        vertexBuf.putFloat(x);
        vertexBuf.putFloat(y);
        return this;
    }

    public MeshData putVec3(Vector3f vec) { return putVec3(vec.x, vec.y, vec.z); }
    public MeshData putVec3(float x, float y, float z) {
        prepare(AttribType.VEC3);
        vertexBuf.putFloat(x);
        vertexBuf.putFloat(y);
        vertexBuf.putFloat(z);
        return this;
    }

    public MeshData putVec4(Vector4f vec) { return putVec4(vec.x, vec.y, vec.z, vec.w); }
    public MeshData putVec4(float x, float y, float z, float w) {
        prepare(AttribType.VEC4);
        vertexBuf.putFloat(x);
        vertexBuf.putFloat(y);
        vertexBuf.putFloat(z);
        vertexBuf.putFloat(w);
        return this;
    }

    public MeshData index(int i) {
        indices.add(mark + i);
        return this;
    }

    public MeshData indices(int... indices) {
        for (int i : indices) {
            this.indices.add(mark + i);
        }
        return this;
    }

    public MeshData indices(List<Integer> indices) {
        for (int i : indices) {
            this.indices.add(mark + i);
        }
        return this;
    }

    public MeshData append(MeshData other) {
        if (primType != other.primType)
            throw new IllegalArgumentException("Can only append data with same primitive type");
        if (!layout.equals(other.layout))
            throw new IllegalArgumentException("Can only append data with same layout");

        // Make sure our buffer is big enough
        ensureSpace(other.vertexBuf.position());

        mark();

        // Back up other buffer's bounds
        int posTmp = other.vertexBuf.position();
        int limitTmp = other.vertexBuf.limit();

        // Copy in the data
        other.vertexBuf.flip();
        vertexBuf.put(other.vertexBuf);

        // Restore backed up bounds
        other.vertexBuf.position(posTmp);
        other.vertexBuf.limit(limitTmp);

        // Copy indices with mark applied
        for (int i : other.indices) {
            indices.add(mark + i);
        }
        return this;
    }

    public MeshData finish() {
        if (finished) throw new IllegalStateException("Already finished");
        finalVertexCount = getVertexCount();
        finished = true;
        vertexBuf.flip();
        return this;
    }

    // Do not free the returned buffer
    public ByteBuffer getVertexBuf() {
        if (!finished)
            finish();
        return vertexBuf;
    }

    public VertexLayout getVertexLayout() {
        return layout;
    }

    public int getVertexCount() {
        if (finished) return finalVertexCount;
        return vertexBuf.position() / layout.sizeOf();
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public PrimitiveType getPrimitiveType() {
        return primType;
    }

    public int getMark() {
        return mark;
    }

    @Override
    public void close() {
        MemoryUtil.memFree(vertexBuf);
    }
}
