package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.io.ResourceUtil;
import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.framebuffer.Framebuffer;
import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.MeshData;
import com.github.rmheuer.azalea.render.mesh.VertexBuffer;
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
 * Renderer to render {@link DrawList2D}s.
 */
public final class Renderer2D implements SafeCloseable {
    private static final String VERTEX_SHADER_PATH = "azalea/shaders/render2d/vertex.glsl";
    private static final String FRAGMENT_SHADER_PATH = "azalea/shaders/render2d/fragment.glsl";

    private final Renderer renderer;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;
    private final ShaderProgram shader;
    private final Texture2D whiteTex;

    /**
     * @param renderer renderer to use for rendering
     */
    public Renderer2D(Renderer renderer) {
        this.renderer = renderer;
        vertexBuffer = renderer.createVertexBuffer();
        indexBuffer = renderer.createIndexBuffer();
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
    }

    public void draw(DrawList2D list, Matrix4f modelViewProj) {
        draw(list, modelViewProj, renderer.getDefaultFramebuffer());
    }

    public void draw(DrawList2D list, Matrix4f modelViewProj, Framebuffer fb) {
        MeshData data = list.getMeshData();
        List<DrawList2D.DrawBatch> batches = list.getBatches();

        vertexBuffer.setDataFrom(data, DataUsage.STREAM);
        indexBuffer.setDataFrom(data, DataUsage.STREAM);

        for (DrawList2D.DrawBatch batch : batches) {
            PipelineInfo info = new PipelineInfo(shader);

            info.setBlend(batch.blendEnabled);
            if (batch.blendEnabled) {
                info.setBlend(true);
                info.setBlendOps(batch.blendOpRGB, batch.blendOpAlpha);
                info.setBlendFactors(
                        batch.blendSrcRGBFactor, batch.blendDstRGBFactor,
                        batch.blendSrcAlphaFactor, batch.blendDstAlphaFactor
                );
            }

            info.setClip(batch.clipEnabled);
            if (batch.clipEnabled)
                renderer.setClipRect(batch.clipX, batch.clipY, batch.clipW, batch.clipH);

            try (ActivePipeline pipe = renderer.bindPipeline(info, fb)) {
                pipe.getUniform("u_ModelViewProj").setMat4(modelViewProj);
                pipe.bindTexture(0, whiteTex);

                for (DrawList2D.DrawCmd cmd : batch.drawCommands) {
                    for (int i = 0; i < Renderer.MAX_TEXTURE_SLOTS - 1; i++) {
                        if (cmd.textures[i] != null) {
                            pipe.bindTexture(i + 1, cmd.textures[i]);
                        }
                    }

                    pipe.draw(vertexBuffer, indexBuffer, cmd.indexStart, cmd.elementCount, cmd.indexOffset);
                }
            }
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public void close() {
        vertexBuffer.close();
        indexBuffer.close();
        shader.close();
        whiteTex.close();
    }
}
