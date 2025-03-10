package com.github.rmheuer.azalea.render.util;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.IndexData;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import com.github.rmheuer.azalea.utils.SafeCloseable;

public final class SharedIndexBuffer implements SafeCloseable {
    private final PrimitiveType primitiveType;
    private final int verticesPerRepetition;
    private final int[] pattern;

    private final IndexBuffer indexBuffer;

    public SharedIndexBuffer(Renderer renderer, PrimitiveType primitiveType, int verticesPerRepetition, int... pattern) {
        this.primitiveType = primitiveType;
        this.verticesPerRepetition = verticesPerRepetition;
        this.pattern = pattern;

        indexBuffer = renderer.createIndexBuffer();
    }

    public void ensureCapacity(int repetitions) {
        try (IndexData data = new IndexData(primitiveType)) {
            data.reserve(repetitions * pattern.length);
            for (int i = 0; i < repetitions; i++) {
                data.putIndicesOffset(i * verticesPerRepetition, pattern);
            }

            indexBuffer.setData(data, DataUsage.STATIC);
        }
    }

    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public void close() {
        indexBuffer.close();
    }
}
