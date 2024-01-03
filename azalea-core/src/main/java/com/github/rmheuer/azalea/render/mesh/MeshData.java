package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of mesh data to upload to the GPU.
 *
 * Add methods throw {@code IllegalStateException} if they are the wrong type
 * for the vertex layout.
 */
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

    /**
     * Creates a new data buffer with the specified layout.
     *
     * @param primType type of primitive to render
     * @param layout layout of the vertex data
     */
    public MeshData(PrimitiveType primType, AttribType... layout) {
        this(primType, new VertexLayout(layout));
    }

    /**
     * Creates a new data buffer with the specified layout.
     *
     * @param primType type of primitive to render
     * @param layout layout of the vertex data
     */
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

    /**
     * Marks the current position. The mark position is added to
     * indices added after this is called.
     *
     * @return this
     */
    public MeshData mark() {
        mark = getVertexCount();
        return this;
    }

    /**
     * Adds a {@code float} value.
     *
     * @param f value to add
     * @return this
     */
    public MeshData putFloat(float f) {
        prepare(AttribType.FLOAT);
        vertexBuf.putFloat(f);
        return this;
    }

    /**
     * Adds a {@code vec2} value.
     *
     * @param vec value to add
     * @return this
     */
    public MeshData putVec2(Vector2f vec) {
        return putVec2(vec.x, vec.y);
    }

    /**
     * Adds a {@code vec2} value.
     *
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @return this
     */
    public MeshData putVec2(float x, float y) {
        prepare(AttribType.VEC2);
        vertexBuf.putFloat(x);
        vertexBuf.putFloat(y);
        return this;
    }

    /**
     * Adds a {@code vec3} value.
     *
     * @param vec value to add
     * @return this
     */
    public MeshData putVec3(Vector3f vec) {
        return putVec3(vec.x, vec.y, vec.z);
    }

    /**
     * Adds a {@code vec3} value.
     *
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @return this
     */
    public MeshData putVec3(float x, float y, float z) {
        prepare(AttribType.VEC3);
        vertexBuf.putFloat(x);
        vertexBuf.putFloat(y);
        vertexBuf.putFloat(z);
        return this;
    }

    /**
     * Adds a {@code vec4} value.
     *
     * @param vec value to add
     * @return this
     */
    public MeshData putVec4(Vector4f vec) { return putVec4(vec.x, vec.y, vec.z, vec.w); }

    /**
     * Adds a {@code vec4} value.
     *
     * @param x x coordinate of the value
     * @param y y coordinate of the value
     * @param z z coordinate of the value
     * @param w w coordinate of the value
     * @return this
     */
    public MeshData putVec4(float x, float y, float z, float w) {
        prepare(AttribType.VEC4);
        vertexBuf.putFloat(x);
        vertexBuf.putFloat(y);
        vertexBuf.putFloat(z);
        vertexBuf.putFloat(w);
        return this;
    }

    /**
     * Adds an index to the index array. The index is added to the mark
     * position before adding it.
     *
     * @param i the index to add
     * @return this
     */
    public MeshData index(int i) {
        indices.add(mark + i);
        return this;
    }

    /**
     * Adds several indices to the index array. This is equivalent to calling
     * {@link #index(int)} for each index.
     * @param indices indices to add
     * @return this
     */
    public MeshData indices(int... indices) {
        for (int i : indices) {
            this.indices.add(mark + i);
        }
        return this;
    }

    /**
     * Adds several indices to the index array. This is equivalent to calling
     * {@link #index(int)} for each index.
     * @param indices indices to add
     * @return this
     */
    public MeshData indices(List<Integer> indices) {
        for (int i : indices) {
            this.indices.add(mark + i);
        }
        return this;
    }

    /**
     * Appends another mesh data buffer to this. This will not modify the
     * source buffer.
     *
     * @param other mesh data to append
     * @return this
     */
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

    /**
     * Marks this data as finished, so no other data can be added.
     *
     * @return this
     */
    public MeshData finish() {
        if (finished) throw new IllegalStateException("Already finished");
        finalVertexCount = getVertexCount();
        finished = true;
        vertexBuf.flip();
        return this;
    }

    /**
     * Gets the native data buffer. Do not free the returned buffer.
     *
     * @return vertex buffer
     */
    public ByteBuffer getVertexBuf() {
        if (!finished)
            finish();
        return vertexBuf;
    }

    /**
     * Gets the vertex layout this mesh data uses.
     *
     * @return layout
     */
    public VertexLayout getVertexLayout() {
        return layout;
    }

    /**
     * Gets the number of vertices in this data.
     *
     * @return vertex count
     */
    public int getVertexCount() {
        if (finished) return finalVertexCount;
        return vertexBuf.position() / layout.sizeOf();
    }

    /**
     * Gets the index array in this data.
     *
     * @return indices
     */
    public List<Integer> getIndices() {
        return indices;
    }

    /**
     * Gets the primitive type the vertices should be rendered as.
     *
     * @return primitive type
     */
    public PrimitiveType getPrimitiveType() {
        return primType;
    }

    /**
     * Gets the current mark position.
     *
     * @return mark
     */
    public int getMark() {
        return mark;
    }

    @Override
    public void close() {
        MemoryUtil.memFree(vertexBuf);
    }
}
