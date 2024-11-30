package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of both vertex and index array data to upload to the
 * GPU. All add methods throw {@code IllegalStateException} if they are the
 * wrong type for the vertex layout.
 */
// TODO: Change so indices are stored in ByteBuffer
public final class IndexedVertexData implements SafeCloseable {
    private final VertexData vertices;
    private final List<Integer> indices;
    private final PrimitiveType primitiveType;

    /**
     * Creates a new data buffer with the specified layout.
     *
     * @param layout layout of the vertex data
     * @param primitiveType type of primitive to render
     */
    public IndexedVertexData(VertexLayout layout, PrimitiveType primitiveType) {
        vertices = new VertexData(layout);
        indices = new ArrayList<>();
        this.primitiveType = primitiveType;
    }

    /**
     * Adds an index to the index array. These indices are relative to the
     * current number of vertices, so an index of 0 would point to the next
     * vertex added.
     *
     * @param i index to add
     */
    public void index(int i) {
        indices.add(vertices.getVertexCount() + i);
    }

    /**
     * Adds an index to the index array. These indices are relative to the
     * start of the vertex array.
     *
     * @param i index to add
     */
    public void indexAbsolute(int i) {
        indices.add(i);
    }

    /**
     * Adds several indices to the index array. These indices are relative to
     * the current number of vertices, so an index of 0 would point to the next
     * vertex added.
     *
     * @param indices indices to add
     */
    public void indices(int... indices) {
        int base = vertices.getVertexCount();
        for (int i : indices) {
            this.indices.add(base + i);
        }
    }

    /**
     * Adds several indices to the index array. These indices are relative to
     * the start of the vertex array.
     *
     * @param indices indices to add
     */
    public void indicesAbsolute(int... indices) {
        for (int i : indices) {
            this.indices.add(i);
        }
    }

    /**
     * Appends another data buffer to this. This will not modify the source
     * buffer.
     *
     * @param other mesh data to append
     */
    public void append(IndexedVertexData other) {
        int base = vertices.getVertexCount();
        vertices.append(other.vertices);
        for (int i : other.indices) {
            indices.add(base + i);
        }
    }

    /**
     * Marks this data as finished, so no other data can be added.
     */
    public void finish() {
        vertices.finish();
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
     * Gets the list of indices stored in this buffer.
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
        return primitiveType;
    }

    @Override
    public void close() {
        vertices.close();
    }

    // -----------------------------------------------------------

    /**
     * Reserves memory for at least the amount of vertices specified.
     *
     * @param additionalVertices number of additional vertices to allocate
     */
    public void reserve(int additionalVertices) {
        vertices.reserve(additionalVertices);
    }

    public void putFloat(float f) {
        vertices.putFloat(f);
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
}
