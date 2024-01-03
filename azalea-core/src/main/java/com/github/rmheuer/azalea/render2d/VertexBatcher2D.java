package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.render.texture.Texture2D;

import java.util.ArrayList;
import java.util.List;

final class VertexBatcher2D {
    /**
     * Assembles draw vertices into batches.
     *
     * @param vertices vertices to assemble
     * @param indices indices into the vertices
     * @param defaultTexture texture to use if none is specified
     * @return batched vertices
     */
    public static List<VertexBatch> batch(List<DrawVertex> vertices, List<Integer> indices, Texture2D defaultTexture) {
        // Here we assume that a polygon is grouped with sequential vertices
        // and indices, and that the texture is constant throughout the polygon.
        // This is true for all polygons from a DrawList2D.

        List<VertexBatch> batches = new ArrayList<>();
        VertexBatch currentBatch = new VertexBatch();
        batches.add(currentBatch);

        int vertexCount = vertices.size();
        int indexCount = indices.size();

        // Add vertices
        int indicesIndex = 0;
        for (int i = 0; i < vertexCount; i++) {
            DrawVertex v = vertices.get(i);
            if (currentBatch.addVertex(v, defaultTexture))
                continue;

            // If we got here, the current batch has no free texture slots, so we need
            // to start another one.

            // Add the indices before this vertex to the current batch
            List<Integer> batchIndices = new ArrayList<>();
            int index;
            while ((index = indices.get(indicesIndex)) < i) {
                batchIndices.add(index);
                indicesIndex++;
                if (indicesIndex == indexCount)
                    break;
            }
            currentBatch.addIndices(batchIndices);

            // Start a new batch
            currentBatch = new VertexBatch();
            batches.add(currentBatch);
        }

        // Add indices
        for (; indicesIndex < indexCount; indicesIndex++) {
            currentBatch.addIndex(indices.get(indicesIndex));
        }

        return batches;
    }

    private VertexBatcher2D() {
        throw new AssertionError();
    }
}
