package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.render.mesh.AttribType;
import com.github.rmheuer.azalea.render.mesh.MeshData;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import com.github.rmheuer.azalea.render.mesh.VertexLayout;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.render.texture.Texture2DRegion;

import java.util.List;

public final class VertexBatch {
    public static final VertexLayout LAYOUT = new VertexLayout(
            AttribType.VEC3, // Position
            AttribType.VEC2, // Texture coord
            AttribType.VEC4, // Color
            AttribType.FLOAT // Texture slot
    );

    private final Texture2D[] textures;
    private final MeshData data;

    public VertexBatch() {
        textures = new Texture2D[Renderer2D.MAX_TEXTURE_SLOTS];
        data = new MeshData(PrimitiveType.TRIANGLES, LAYOUT);
    }

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

        data.putVec3(v.getPos())
                .putVec2(v.getU(), v.getV())
                .putVec4(v.getColor())
                .putFloat(textureSlot);

        return true;
    }

    public void addIndex(int index) {
        data.index(index);
    }

    public void addIndices(List<Integer> indices) {
        for (int i : indices) {
            data.index(i);
        }
    }

    public Texture2D[] getTextures() {
        return textures;
    }

    // Transfers ownership of the data to the caller
    public MeshData getData() {
        return data;
    }
}
