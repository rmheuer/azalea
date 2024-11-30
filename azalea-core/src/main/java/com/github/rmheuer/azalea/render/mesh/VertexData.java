package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a collection of vertex array data to upload to the GPU. All put
 * methods throw {@code IllegalStateException} if they are the wrong type for
 * the vertex layout.
 */
public final class VertexData implements SafeCloseable {
    private static final int INITIAL_CAPACITY = 512;
    private static final boolean IS_LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;

    private final VertexLayout layout;
    private int layoutElemIdx;

    private ByteBuffer data;
    private boolean finished;
    private int finalVertexCount;

    /**
     * Creates a new data buffer with the specified layout.
     *
     * @param layout layout of the vertex data
     */
    public VertexData(AttribType... layout) {
        this(new VertexLayout(layout));
    }

    /**
     * Creates a new data buffer with the specified layout.
     *
     * @param layout layout of the vertex data
     */
    public VertexData(VertexLayout layout) {
        this.layout = layout;
        layoutElemIdx = 0;

        data = MemoryUtil.memAlloc(INITIAL_CAPACITY * layout.sizeOf());
    }

    private void ensureSpace(int spaceBytes) {
        if (data.remaining() < spaceBytes) {
            int cap = data.capacity();
            data = MemoryUtil.memRealloc(data, Math.max(cap + spaceBytes, cap * 2));
        }
    }

    /**
     * Reserves memory for at least the amount of vertices specified.
     *
     * @param additionalVertices number of additional vertices to allocate
     */
    public void reserve(int additionalVertices) {
        if (finished)
            throw new IllegalStateException("Data is finished");
        if (layoutElemIdx != 0)
            throw new IllegalStateException("Can only reserve space between vertices");

        ensureSpace(additionalVertices * layout.sizeOf());
    }

    private void prepare(AttribType type) {
        if (finished)
            throw new IllegalStateException("Data is finished");
        if (layoutElemIdx == 0)
            ensureSpace(layout.sizeOf());

        AttribType[] layoutTypes = layout.getTypes();
        AttribType layoutType = layoutTypes[layoutElemIdx++];
        if (layoutType != type)
            throw new IllegalStateException("Incorrect attribute added for format (added " + type + ", layout specifies " + layoutType + ")");
        if (layoutElemIdx >= layoutTypes.length)
            layoutElemIdx = 0;
    }

    public void putFloat(float f) {
        prepare(AttribType.FLOAT);
        data.putFloat(f);
    }

    public void putVec2(Vector2fc v) {
        putVec2(v.x(), v.y());
    }

    public void putVec2(float x, float y) {
        prepare(AttribType.VEC2);
        data.putFloat(x);
        data.putFloat(y);
    }

    public void putVec3(Vector3fc v) {
        putVec3(v.x(), v.y(), v.z());
    }

    public void putVec3(float x, float y, float z) {
        prepare(AttribType.VEC3);
        data.putFloat(x);
        data.putFloat(y);
        data.putFloat(z);
    }

    public void putVec4(Vector4fc v) {
        putVec4(v.x(), v.y(), v.z(), v.w());
    }

    public void putVec4(float x, float y, float z, float w) {
        prepare(AttribType.VEC4);
        data.putFloat(x);
        data.putFloat(y);
        data.putFloat(z);
        data.putFloat(w);
    }

    public void putColorRGBA(int rgba) {
        prepare(AttribType.COLOR_RGBA);

        if (IS_LITTLE_ENDIAN) {
            // OpenGL wants color in R, G, B, A order
            // Int is in A, B, G, R order (big to little)
            // If system is little-endian, we can directly put int
            // Otherwise, we need to reorder the bytes
            data.putInt(rgba);
        } else {
            data.put((byte) Colors.RGBA.getRed(rgba));
            data.put((byte) Colors.RGBA.getGreen(rgba));
            data.put((byte) Colors.RGBA.getBlue(rgba));
            data.put((byte) Colors.RGBA.getAlpha(rgba));
        }
    }

    /**
     * Appends another vertex data buffer to this. This will not modify the
     * source buffer.
     *
     * @param other mesh data to append
     */
    public void append(VertexData other) {
        if (finished)
            throw new IllegalStateException("Data is finished");
        if (!layout.equals(other.layout))
            throw new IllegalArgumentException("Can only append data with same layout");

        // Make sure our buffer is big enough
        ensureSpace(other.data.position());

        // Back up other buffer's bounds
        int posTmp = other.data.position();
        int limitTmp = other.data.limit();

        // Copy in the data
        other.data.flip();
        data.put(other.data);

        // Restore backed up bounds
        other.data.position(posTmp);
        other.data.limit(limitTmp);
    }

    /**
     * Marks this data as finished, so no other data can be added.
     */
    public void finish() {
        if (finished)
            throw new IllegalStateException("Already finished");
        finished = true;

        finalVertexCount = getVertexCount();
        data.flip();
    }

    /**
     * Gets the finished native data buffer. Do not free the returned buffer.
     * If this data is not finished, it will be marked as finished.
     */
    public ByteBuffer getVertexBuf() {
        if (!finished)
            finish();
        return data;
    }

    /**
     * Gets the layout the vertices are stored in.
     *
     * @return layout
     */
    public VertexLayout getLayout() {
        return layout;
    }

    /**
     * Gets the number of vertices in this data.
     *
     * @return vertex count
     */
    public int getVertexCount() {
        if (finished)
            return finalVertexCount;
        return data.position() / layout.sizeOf();
    }

    @Override
    public void close() {
        MemoryUtil.memFree(data);
    }
}
