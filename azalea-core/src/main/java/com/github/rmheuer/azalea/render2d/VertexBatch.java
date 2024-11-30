package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.render.mesh.AttribType;
import com.github.rmheuer.azalea.render.mesh.MeshData;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import com.github.rmheuer.azalea.render.mesh.VertexLayout;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.render.texture.Texture2DRegion;

import java.util.List;

/**
 * A batch of vertices to draw.
 */
final class VertexBatch {
    /**
     * The layout of the generated vertex data.
     */
    public static final VertexLayout LAYOUT = new VertexLayout(
            AttribType.VEC3, // Position
            AttribType.VEC2, // Texture coord
            AttribType.COLOR_RGBA, // Color
            AttribType.FLOAT // Texture slot
    );

    private final Texture2D[] textures;
    private final MeshData data;

    /**
     * Creates a new empty batch.
     */
    public VertexBatch() {
        textures = new Texture2D[Renderer2D.MAX_TEXTURE_SLOTS];
        data = new MeshData(LAYOUT, PrimitiveType.TRIANGLES);
    }

    /**
     * Tries to add another vertex to the batch. This will fail if the batch is
     * out of texture slots.
     *
     * @param v vertex to add
     * @param defaultTexture texture to use if none is specified
     * @return whether the vertex was able to be added
     */
    public boolean addVertex(DrawVertex v, Texture2D defaultTexture) {
        Texture2DRegion texSub = v.getTex();
        Texture2D tex;
        if (texSub == null)
            tex = defaultTexture;
        else
            tex = texSub.getSourceTexture();

        int textureSlot = -1;
        for (int i = 0; i < Renderer2D.MAX_TEXTURE_SLOTS; i++) {
            if (textures[i] == null) {
                textures[i] = tex;
                textureSlot = i;
                break;
            }

            if (textures[i].equals(tex)) {
                textureSlot = i;
                break;
            }
        }
        if (textureSlot == -1)
            return false;

        data.putVec3(v.getPos());
        data.putVec2(v.getU(), v.getV());
        data.putColorRGBA(v.getColor());
        data.putFloat(textureSlot);

        return true;
    }

    /**
     * Adds an index to the indices array.
     *
     * @param index index to add
     */
    public void addIndex(int index) {
        data.indexAbsolute(index);
    }

    /**
     * Adds multiple indices at once to the indices array.
     *
     * @param indices indices to add
     */
    public void addIndices(List<Integer> indices) {
        for (int i : indices) {
            data.indexAbsolute(i);
        }
    }

    /**
     * Gets the textures this batch uses.
     *
     * @return textures
     */
    public Texture2D[] getTextures() {
        return textures;
    }

    /**
     * Gets the mesh data that was generated. This transfers ownership of the
     * data to the caller.
     *
     * @return generated data
     */
    public MeshData getData() {
        return data;
    }
}
