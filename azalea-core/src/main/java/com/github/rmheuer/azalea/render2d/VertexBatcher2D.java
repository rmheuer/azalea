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

        int vertexCount = vertices.size();
        int indexCount = indices.size();

        VertexBatch currentBatch = new VertexBatch();
        batches.add(currentBatch);
        int batchStartIdx = 0;
        int indicesIdx = 0;

        for (int vertexIdx = 0; vertexIdx < vertexCount; vertexIdx++) {
            // Try to add vertex to current batch
            DrawVertex v = vertices.get(vertexIdx);
            if (currentBatch.addVertex(v, defaultTexture))
                continue;

            // Current batch is out of texture slots, start a new one

            // Flush all indices that reference vertices in the current batch
            List<Integer> batchIndices = new ArrayList<>();
            int index;
            while ((index = indices.get(indicesIdx)) < vertexIdx) {
                batchIndices.add(index - batchStartIdx);

                indicesIdx++;
                if (indicesIdx == indexCount)
                    throw new IllegalStateException("Ran out of indices while searching for first reference to vertex " + vertexIdx);
            }
            currentBatch.addIndices(batchIndices);

            // Start new batch
            batchStartIdx = vertexIdx;
            currentBatch = new VertexBatch();
            batches.add(currentBatch);

            if (!currentBatch.addVertex(v, defaultTexture))
                throw new IllegalStateException("Could not add first vertex to batch");
        }

        // Add remaining indices
        for (; indicesIdx < indexCount; indicesIdx++) {
            currentBatch.addIndex(indices.get(indicesIdx) - batchStartIdx);
        }

        return batches;
    }

    private VertexBatcher2D() {
        throw new AssertionError();
    }
}
