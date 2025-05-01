package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * Represents a collection of both vertex and index array data to upload to the
 * GPU. All add methods throw {@code IllegalStateException} if they are the
 * wrong type for the vertex layout.
 */
public final class MeshData implements SafeCloseable {
    private final VertexData vertices;
    private final IndexData indices;

    /**
     * Creates a new data buffer with the specified layout and
     * {@link IndexFormat#UNSIGNED_INT} index format.
     *
     * @param layout layout of the vertex data
     * @param primitiveType type of primitive to render
     */
    public MeshData(VertexLayout layout, PrimitiveType primitiveType) {
        this(layout, primitiveType, IndexFormat.UNSIGNED_INT);
    }

    /**
     * Creates a new data buffer with the specified layout.
     *
     * @param layout layout of the vertex data
     * @param primitiveType type of primitive to render
     * @param indexFormat format to store indices in
     */
    public MeshData(VertexLayout layout, PrimitiveType primitiveType, IndexFormat indexFormat) {
        vertices = new VertexData(layout);
        indices = new IndexData(primitiveType, indexFormat);
    }

    /**
     * Adds an index to the index array. These indices are relative to the
     * current number of vertices, so an index of 0 would point to the next
     * vertex added.
     *
     * @param i index to add
     */
    public void putIndex(int i) {
        indices.putIndex(vertices.getVertexCount() + i);
    }

    /**
     * Adds an index to the index array. These indices are relative to the
     * start of the vertex array.
     *
     * @param i index to add
     */
    public void putIndexAbsolute(int i) {
        indices.putIndex(i);
    }

    /**
     * Adds several indices to the index array. These indices are relative to
     * the current number of vertices, so an index of 0 would point to the next
     * vertex added.
     *
     * @param indices indices to add
     */
    public void putIndices(int... indices) {
        int base = vertices.getVertexCount();
        this.indices.putIndicesOffset(base, indices);
    }

    /**
     * Adds several indices to the index array. These indices are relative to
     * the start of the vertex array.
     *
     * @param indices indices to add
     */
    public void putIndicesAbsolute(int... indices) {
        this.indices.putIndices(indices);
    }

    /**
     * Adds several indices to the index array. These indices are relative to
     * the start of the vertex array, with the provided offset added to each.
     *
     * @param indices indices to add
     */
    public void putIndicesOffset(int offset, int... indices) {
        this.indices.putIndicesOffset(offset, indices);
    }

    /**
     * Appends another data buffer to this. This will not modify the source
     * buffer.
     *
     * @param other mesh data to append
     */
    public void append(MeshData other) {
        int base = vertices.getVertexCount();
        vertices.append(other.vertices);
        indices.append(other.indices, base);
    }

    /**
     * Marks this data as finished, so no other data can be added.
     */
    public void finish() {
        vertices.finish();
        indices.finish();
    }

    /**
     * Gets the vertex data stored in this buffer.
     *
     * @return vertex data
     */
    public VertexData getVertices() {
        return vertices;
    }

    /**
     * Gets the index data stored in this buffer.
     *
     * @return indices
     */
    public IndexData getIndices() {
        return indices;
    }

    @Override
    public void close() {
        vertices.close();
        indices.close();
    }

    /**
     * Reserves memory for at least the amount of indices specified.
     *
     * @param additionalIndices number of additional indices to allocate
     */
    public void reserveIndices(int additionalIndices) {
        indices.reserve(additionalIndices);
    }

    // -----------------------------------------------------------

    /**
     * Reserves memory for at least the amount of vertices specified.
     *
     * @param additionalVertices number of additional vertices to allocate
     */
    public void reserveVertices(int additionalVertices) {
        vertices.reserve(additionalVertices);
    }

    public void putFloat(float f) {
        vertices.putFloat(f);
    }

    public void putInt(int i) {
        vertices.putInt(i);
    }

    public void putUint(int i) {
        vertices.putUint(i);
    }

    public void putVec2(Vector2fc v) {
        vertices.putVec2(v);
    }

    public void putVec2(float x, float y) {
        vertices.putVec2(x, y);
    }

    public void putVec3(Vector3fc v) {
        vertices.putVec3(v);
    }

    public void putVec3(float x, float y, float z) {
        vertices.putVec3(x, y, z);
    }

    public void putVec4(Vector4fc v) {
        vertices.putVec4(v);
    }

    public void putVec4(float x, float y, float z, float w) {
        vertices.putVec4(x, y, z, w);
    }

    public void putColorRGBA(int rgba) {
        vertices.putColorRGBA(rgba);
    }

    /**
     * Gets the layout the vertices are stored in.
     *
     * @return layout
     */
    public VertexLayout getVertexLayout() {
        return vertices.getLayout();
    }

    /**
     * Gets the number of vertices in this data.
     *
     * @return vertex count
     */
    public int getVertexCount() {
        return vertices.getVertexCount();
    }

    /**
     * Gets the number of indices in this data.
     *
     * @return index count
     */
    public int getIndexCount() {
        return indices.getIndexCount();
    }
}
