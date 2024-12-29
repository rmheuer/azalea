package com.github.rmheuer.azalea.render.mesh;

import com.github.rmheuer.azalea.utils.SafeCloseable;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * Represents a collection of index array data to upload to the GPU.
 */
public final class IndexData implements SafeCloseable {
    private static final int INITIAL_CAPACITY = 512;

    private final PrimitiveType primitiveType;
    private final IndexFormat format;
    private final int sizeOfFormat;
    private final int sizeShift;

    private ByteBuffer data;
    private boolean finished;
    private int finalIndexCount;

    /**
     * Creates a new data buffer with the {@link IndexFormat#UNSIGNED_INT}
     * format.
     *
     * @param primitiveType type of primitive the indices form
     */
    public IndexData(PrimitiveType primitiveType) {
        this(primitiveType, IndexFormat.UNSIGNED_INT);
    }

    /**
     * Creates a new data buffer with the specified format.
     *
     * @param primitiveType type of primitive the indices form
     * @param format format to store the indices in
     */
    public IndexData(PrimitiveType primitiveType, IndexFormat format) {
        this.primitiveType = primitiveType;
        this.format = format;

        sizeOfFormat = format.sizeOf();
        sizeShift = format == IndexFormat.UNSIGNED_INT ? 2 : 1;
        data = MemoryUtil.memAlloc(INITIAL_CAPACITY << sizeShift);
    }

    private void ensureSpace(int spaceBytes) {
        if (data.remaining() < spaceBytes) {
            int cap = data.capacity();
            data = MemoryUtil.memRealloc(data, Math.max(cap + spaceBytes, cap * 2));
        }
    }

    /**
     * Reserves memory for at least the amount of indices specified.
     *
     * @param additionalIndices number of additional indices to allocate
     */
    public void reserve(int additionalIndices) {
        if (finished)
            throw new IllegalStateException("Data is finished");
        ensureSpace(additionalIndices << sizeShift);
    }

    public void putIndex(int i) {
        if (finished)
            throw new IllegalStateException("Data is finished");

        ensureSpace(sizeOfFormat);
        if (format == IndexFormat.UNSIGNED_INT)
            data.putInt(i);
        else
            data.putShort((short) i);
    }

    public void putIndices(int... indices) {
        if (finished)
            throw new IllegalStateException("Data is finished");

        ensureSpace(indices.length << sizeShift);
        if (format == IndexFormat.UNSIGNED_INT) {
            for (int i : indices) {
                data.putInt(i);
            }
        } else {
            for (int i : indices) {
                data.putShort((short) i);
            }
        }
    }

    public void putIndicesOffset(int offset, int... indices) {
        if (finished)
            throw new IllegalStateException("Data is finished");

        ensureSpace(indices.length << sizeShift);
        if (format == IndexFormat.UNSIGNED_INT) {
            for (int i : indices) {
                data.putInt(offset + i);
            }
        } else {
            for (int i : indices) {
                data.putShort((short) (offset + i));
            }
        }
    }

    /**
     * Appends another index data buffer to this. This will not modify the
     * source buffer.
     *
     * @param other index data to append
     */
    public void append(IndexData other) {
        if (finished)
            throw new IllegalStateException("Data is finished");
        if (format != other.format)
            throw new IllegalArgumentException("Can only append data with same format");

        ensureSpace(other.data.position());

        int posTmp = other.data.position();
        int limitTmp = other.data.limit();

        other.data.flip();
        data.put(other.data);

        other.data.position(posTmp);
        other.data.limit(limitTmp);
    }

    /**
     * Appends another vertex data buffer to this, adding an offset to each
     * index. This will not modify the source buffer.
     *
     * @param other mesh data to append
     * @param offset offset to add to each index from the source buffer
     */
    public void append(IndexData other, int offset) {
        if (finished)
            throw new IllegalStateException("Data is finished");
        if (format != other.format)
            throw new IllegalArgumentException("Can only append data with same format");

        ensureSpace(other.data.position());

        int pos = other.data.position();
        int limit = other.data.limit();
        if (format == IndexFormat.UNSIGNED_INT) {
            for (int i = pos; i < limit; i += SizeOf.INT) {
                data.putInt(other.data.getInt(i) + offset);
            }
        } else {
            for (int i = pos; i < limit; i += SizeOf.SHORT) {
                data.putShort((short) (other.data.getShort(i) + offset));
            }
        }
    }

    /**
     * Marks this data as finished, so no other data can be added.
     */
    public void finish() {
        if (finished)
            throw new IllegalStateException("Already finished");

        finalIndexCount = getIndexCount();
        finished = true;

        data.flip();
    }

    /**
     * Gets the finished native data buffer. Do not free the returned buffer.
     * If this data is not finished, it will be marked as finished.
     */
    public ByteBuffer getIndexBuf() {
        if (!finished)
            finish();
        return data;
    }

    /**
     * Gets the format the indices are stored in.
     *
     * @return format
     */
    public IndexFormat getFormat() {
        return format;
    }

    /**
     * Gets the type of primitives the indices form.
     *
     * @return primitive type
     */
    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    /**
     * Gets the number of indices in this data.
     *
     * @return index count
     */
    public int getIndexCount() {
        if (finished)
            return finalIndexCount;
        return data.position() >> sizeShift;
    }

    @Override
    public void close() {
        MemoryUtil.memFree(data);
    }
}
