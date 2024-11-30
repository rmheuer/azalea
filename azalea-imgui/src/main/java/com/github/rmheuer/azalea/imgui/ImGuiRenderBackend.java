package com.github.rmheuer.azalea.imgui;

import com.github.rmheuer.azalea.io.ResourceUtil;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.*;
import com.github.rmheuer.azalea.render.pipeline.ActivePipeline;
import com.github.rmheuer.azalea.render.pipeline.PipelineInfo;
import com.github.rmheuer.azalea.render.shader.ShaderProgram;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import imgui.*;
import imgui.flag.ImGuiBackendFlags;
import imgui.type.ImInt;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class ImGuiRenderBackend implements SafeCloseable {
    private static final int FONT_TEXTURE_ID = -1;

    private static final VertexLayout VERTEX_LAYOUT = new VertexLayout(
            AttribType.VEC2, // Position
            AttribType.VEC2, // UV
            AttribType.COLOR_RGBA // Color
    );

    private final Renderer renderer;
    private final FrameTextures frameTextures;

    private final ShaderProgram shader;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    private Texture2D fontTexture;

    private final ImVec2 displaySize = new ImVec2();
    private final ImVec2 displayPos = new ImVec2();
    private final ImVec2 framebufferScale = new ImVec2();
    private final ImVec4 clipRect = new ImVec4();
    private final Matrix4f projMtx = new Matrix4f();

    public ImGuiRenderBackend(Renderer renderer, FrameTextures frameTextures) {
        this.renderer = renderer;
        this.frameTextures = frameTextures;

        ImGuiIO io = ImGui.getIO();
        io.setBackendRendererName("azalea");
        io.addBackendFlags(ImGuiBackendFlags.RendererHasVtxOffset);

        try {
            shader = renderer.createShaderProgram(
                    ResourceUtil.readAsStream("azalea/shaders/imgui/vertex.glsl"),
                    ResourceUtil.readAsStream("azalea/shaders/imgui/fragment.glsl")
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load built-in shaders", e);
        }

        vertexBuffer = renderer.createVertexBuffer();
        indexBuffer = renderer.createIndexBuffer();

        updateFontTexture();
    }

    public void updateFontTexture() {
        if (fontTexture != null)
            fontTexture.close();

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer textureData = fontAtlas.getTexDataAsRGBA32(width, height);

        fontTexture = renderer.createTexture2D();
        fontTexture.setFilters(Texture.Filter.LINEAR);
        fontTexture.setData(textureData, width.get(), height.get(), ColorFormat.RGBA);

        fontAtlas.setTexID(FONT_TEXTURE_ID);
    }

    public void renderDrawData(ImDrawData drawData) {
        if (drawData.getCmdListsCount() <= 0)
            return;

        drawData.getDisplaySize(displaySize);
        drawData.getDisplayPos(displayPos);
        drawData.getFramebufferScale(framebufferScale);

        float clipOffX = displayPos.x;
        float clipOffY = displayPos.y;
        float clipScaleX = framebufferScale.x;
        float clipScaleY = framebufferScale.y;

        int fbWidth = (int) (displaySize.x * framebufferScale.x);
        int fbHeight = (int) (displaySize.y * framebufferScale.y);
        if (fbWidth <= 0 || fbHeight <= 0)
            return;

        PipelineInfo info = new PipelineInfo(shader)
                .setClip(true);
        try (ActivePipeline pipe = renderer.bindPipeline(info)) {
            projMtx.setOrtho2D(
                    displayPos.x,
                    displayPos.x + displaySize.x,
                    displayPos.y + displaySize.y,
                    displayPos.y
            );
            pipe.getUniform("u_ProjMtx").setMat4(projMtx);

            for (int cmdListIdx = 0; cmdListIdx < drawData.getCmdListsCount(); cmdListIdx++) {
                vertexBuffer.setData(drawData.getCmdListVtxBufferData(cmdListIdx), VERTEX_LAYOUT, DataUsage.STREAM);
                indexBuffer.setData(
                        drawData.getCmdListIdxBufferData(cmdListIdx),
                        IndexBuffer.IndexFormat.UNSIGNED_SHORT,
                        PrimitiveType.TRIANGLES,
                        DataUsage.STREAM
                );

                for (int cmdBufferIdx = 0; cmdBufferIdx < drawData.getCmdListCmdBufferSize(cmdListIdx); cmdBufferIdx++) {
                    drawData.getCmdListCmdBufferClipRect(cmdListIdx, cmdBufferIdx, clipRect);

                    float clipMinX = (clipRect.x - clipOffX) * clipScaleX;
                    float clipMinY = (clipRect.y - clipOffY) * clipScaleY;
                    float clipMaxX = (clipRect.z - clipOffX) * clipScaleX;
                    float clipMaxY = (clipRect.w - clipOffY) * clipScaleY;
                    if (clipMaxX <= clipMinX || clipMaxY <= clipMinY)
                        continue;

                    renderer.setClipRect((int) clipMinX, (int) (fbHeight - clipMaxY), (int) (clipMaxX - clipMinX), (int) (clipMaxY - clipMinY));

                    int texId = drawData.getCmdListCmdBufferTextureId(cmdListIdx, cmdBufferIdx);
                    if (texId == FONT_TEXTURE_ID)
                        pipe.bindTexture(0, fontTexture);
                    else
                        pipe.bindTexture(0, frameTextures.getTexture(texId));

                    pipe.draw(
                            vertexBuffer,
                            indexBuffer,
                            drawData.getCmdListCmdBufferIdxOffset(cmdListIdx, cmdBufferIdx),
                            drawData.getCmdListCmdBufferElemCount(cmdListIdx, cmdBufferIdx),
                            drawData.getCmdListCmdBufferVtxOffset(cmdListIdx, cmdBufferIdx));
                }
            }
        }
    }

    @Override
    public void close() {
        vertexBuffer.close();
        indexBuffer.close();
        fontTexture.close();
        shader.close();
    }
}
