package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.io.ResourceUtil;
import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.render.framebuffer.Framebuffer;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.MeshData;
import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.pipeline.ActivePipeline;
import com.github.rmheuer.azalea.render.pipeline.PipelineInfo;
import com.github.rmheuer.azalea.render.shader.ShaderProgram;
import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

/**
 * Renderer to render {@code DrawList2D}s.
 */
public final class Renderer2D implements SafeCloseable {
    private static final String VERTEX_SHADER_PATH = "azalea/shaders/render2d/vertex.glsl";
    private static final String FRAGMENT_SHADER_PATH = "azalea/shaders/render2d/fragment.glsl";

    private final Renderer renderer;
    private final Mesh mesh;
    private final ShaderProgram shader;
    private final Texture2D whiteTex;

    private boolean depthTest;

    /**
     * @param renderer renderer to use for rendering
     */
    public Renderer2D(Renderer renderer) {
        this.renderer = renderer;
        mesh = renderer.createMesh();
        try {
            shader =
                    renderer.createShaderProgram(
                            ResourceUtil.readAsStream(VERTEX_SHADER_PATH),
                            ResourceUtil.readAsStream(FRAGMENT_SHADER_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load built-in shaders", e);
        }

        try (Bitmap whiteData = new Bitmap(1, 1, ColorFormat.RGBA, Colors.RGBA.WHITE)) {
            whiteTex = renderer.createTexture2D();
            whiteTex.setData(whiteData);
        }

        try (ActivePipeline pipe = renderer.bindPipeline(new PipelineInfo(shader))) {
            for (int i = 0; i < Renderer.MAX_TEXTURE_SLOTS; i++) {
                pipe.getUniform("u_Textures[" + i + "]").setInt(i);
            }
        }

        depthTest = false;
    }

    private void drawBatch(ActivePipeline pipeline, VertexBatch batch) {
        Texture2D[] textures = batch.getTextures();
        for (int i = 0; i < Renderer.MAX_TEXTURE_SLOTS; i++) {
            Texture2D tex = textures[i];
            if (tex != null) {
                pipeline.bindTexture(i, tex);
            }
        }

        try (MeshData data = batch.getData()) {
            mesh.setData(data, DataUsage.DYNAMIC);
        }
        pipeline.draw(mesh);
    }

    public void draw(DrawList2D list, Matrix4f modelViewProj) {
        draw(list, modelViewProj, renderer.getDefaultFramebuffer());
    }

    public void draw(DrawList2D list, Matrix4f modelViewProj, Framebuffer fb) {
        try (ActivePipeline pipe = renderer.bindPipeline(new PipelineInfo(shader).setDepthTest(depthTest), fb)) {
            pipe.getUniform("u_ModelViewProj").setMat4(modelViewProj);

            List<VertexBatch> batches =
                    VertexBatcher2D.batch(list.getVertices(), list.getIndices(), whiteTex);
            for (VertexBatch batch : batches) {
                drawBatch(pipe, batch);
            }
        }
    }

    public void setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void close() {
        mesh.close();
        shader.close();
        whiteTex.close();
    }
}
