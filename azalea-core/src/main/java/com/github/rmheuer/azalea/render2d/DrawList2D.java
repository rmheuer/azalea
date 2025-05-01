package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.math.MathUtil;
import com.github.rmheuer.azalea.math.PoseStack;
import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.*;
import com.github.rmheuer.azalea.render.pipeline.BlendFactor;
import com.github.rmheuer.azalea.render.pipeline.BlendOp;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.render.texture.Texture2DRegion;
import com.github.rmheuer.azalea.render2d.font.Font;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class DrawList2D implements SafeCloseable {
    private static final VertexLayout LAYOUT = new VertexLayout(
            AttribType.VEC2, // Position
            AttribType.VEC2, // Texture coordinate
            AttribType.COLOR_RGBA, // Color
            AttribType.INT // Texture slot
    );

    public static final class DrawBatch {
        public final boolean clipEnabled;
        public final int clipX, clipY, clipW, clipH;

        public final boolean blendEnabled;
        public final BlendOp blendOpRGB, blendOpAlpha;
        public final BlendFactor blendSrcRGBFactor, blendDstRGBFactor;
        public final BlendFactor blendSrcAlphaFactor, blendDstAlphaFactor;

        public final List<DrawCmd> drawCommands;

        public DrawBatch(boolean clipEnabled, int clipX, int clipY, int clipW, int clipH, boolean blendEnabled, BlendOp blendOpRGB, BlendOp blendOpAlpha, BlendFactor blendSrcRGBFactor, BlendFactor blendDstRGBFactor, BlendFactor blendSrcAlphaFactor, BlendFactor blendDstAlphaFactor) {
            this.clipEnabled = clipEnabled;
            this.clipX = clipX;
            this.clipY = clipY;
            this.clipW = clipW;
            this.clipH = clipH;
            this.blendEnabled = blendEnabled;
            this.blendOpRGB = blendOpRGB;
            this.blendOpAlpha = blendOpAlpha;
            this.blendSrcRGBFactor = blendSrcRGBFactor;
            this.blendDstRGBFactor = blendDstRGBFactor;
            this.blendSrcAlphaFactor = blendSrcAlphaFactor;
            this.blendDstAlphaFactor = blendDstAlphaFactor;

            drawCommands = new ArrayList<>();
        }
    }

    public static final class DrawCmd {
        public final int indexOffset;
        public final int indexStart;
        public final int elementCount;
        public final Texture2D[] textures;

        public DrawCmd(int indexOffset, int indexStart, int elementCount, Texture2D[] textures) {
            this.indexOffset = indexOffset;
            this.indexStart = indexStart;
            this.elementCount = elementCount;
            this.textures = textures;
        }
    }

    private final MeshData meshData;
    private final List<DrawBatch> batches;
    private boolean finished;

    private DrawBatch currentBatch;
    private boolean maybeStartNewBatch;

    private int cmdIndexOffset;
    private int cmdIndexStart;
    private Texture2D[] cmdTextures;

    private boolean clipEnabled;
    private int clipX, clipY, clipW, clipH;

    private boolean blendEnabled;
    private BlendOp blendOpRGB, blendOpAlpha;
    private BlendFactor blendSrcRGBFactor, blendDstRGBFactor;
    private BlendFactor blendSrcAlphaFactor, blendDstAlphaFactor;

    private final PoseStack poseStack;

    public DrawList2D() {
        meshData = new MeshData(LAYOUT, PrimitiveType.TRIANGLES, IndexFormat.UNSIGNED_SHORT);
        batches = new ArrayList<>();
        finished = false;

        currentBatch = null;
        maybeStartNewBatch = true;

        clipEnabled = false;
        blendEnabled = true;
        blendOpRGB = blendOpAlpha = BlendOp.ADD;
        blendSrcRGBFactor = blendSrcAlphaFactor = BlendFactor.SRC_ALPHA;
        blendDstRGBFactor = blendDstAlphaFactor = BlendFactor.ONE_MINUS_SRC_ALPHA;

        // FIXME: Should get a better solution for transforms than this
        poseStack = new PoseStack();
    }

    private boolean didPipelineSettingsChange() {
        if (currentBatch == null)
            return true;

        if (clipEnabled) {
            if (!currentBatch.clipEnabled)
                return true;

            if (clipX != currentBatch.clipX || clipY != currentBatch.clipY || clipW != currentBatch.clipW || clipH != currentBatch.clipH)
                return true;
        } else {
            if (currentBatch.clipEnabled)
                return true;
        }

        if (blendEnabled) {
            if (!currentBatch.blendEnabled)
                return true;

            if (blendOpRGB != currentBatch.blendOpRGB || blendOpAlpha != currentBatch.blendOpAlpha)
                return true;

            if (blendSrcRGBFactor != currentBatch.blendSrcRGBFactor || blendDstRGBFactor != currentBatch.blendDstRGBFactor)
                return true;
            if (blendSrcAlphaFactor != currentBatch.blendSrcAlphaFactor || blendDstAlphaFactor != currentBatch.blendDstAlphaFactor)
                return true;
        } else {
            if (currentBatch.blendEnabled)
                return true;
        }

        return false;
    }

    private void finishDrawCmd() {
        currentBatch.drawCommands.add(new DrawCmd(cmdIndexOffset, cmdIndexStart, meshData.getIndexCount() - cmdIndexStart, cmdTextures));
    }

    private void startNewDrawCmd() {
        cmdIndexOffset = meshData.getVertexCount();
        cmdIndexStart = meshData.getIndexCount();
        // Reserve one slot for the white texture
        cmdTextures = new Texture2D[Renderer.MAX_TEXTURE_SLOTS - 1];
    }

    private void preparePolygon(int vertexCount) {
        if (maybeStartNewBatch && didPipelineSettingsChange()) {
            if (currentBatch != null) {
                finishDrawCmd();
                batches.add(currentBatch);
            }

            currentBatch = new DrawBatch(clipEnabled, clipX, clipY, clipW, clipH, blendEnabled, blendOpRGB, blendOpAlpha, blendSrcRGBFactor, blendDstRGBFactor, blendSrcAlphaFactor, blendDstAlphaFactor);
            startNewDrawCmd();
        } else {
            int cmdVertexCount = meshData.getVertexCount() - cmdIndexOffset;
            if (cmdVertexCount + vertexCount > 65536) {
                // Reached maximum number of vertices addressable by
                // UNSIGNED_SHORT index buffer, need to start new draw command
                finishDrawCmd();
                startNewDrawCmd();
            }
        }
        maybeStartNewBatch = false;
    }

    private int getTextureSlot(Texture2D texture) {
        for (int i = 0; i < cmdTextures.length; i++) {
            Texture2D current = cmdTextures[i];

            if (current == texture)
                return i + 1;

            if (current == null) {
                cmdTextures[i] = texture;
                return i + 1;
            }
        }

        // Can't fit this texture in this draw command, start a new one
        finishDrawCmd();
        startNewDrawCmd();
        cmdTextures[0] = texture;
        return 1;
    }

    private void indices(int... indices) {
        preparePolygon(indices.length);
        meshData.putIndicesOffset(meshData.getVertexCount() - cmdIndexOffset, indices);
    }

    private void vertex(float x, float y, int color) { vertex(x, y, color, 0, null, 0, 0); }
    private void vertex(float x, float y, int color, int texSlot, Texture2DRegion tex, float u, float v) {
        if (tex != null) {
            Vector2f topLeft = tex.getRegionTopLeftUV();
            Vector2f botRight = tex.getRegionBottomRightUV();

            u = MathUtil.lerp(topLeft.x, botRight.x, u);
            v = MathUtil.lerp(topLeft.y, botRight.y, v);
        }

        Vector3f pos = new Vector3f(x, y, 0);
        poseStack.getMatrix().transformPosition(pos);

        meshData.putVec2(pos.x, pos.y);
        meshData.putVec2(u, v);
        meshData.putColorRGBA(color);
        meshData.putInt(texSlot);
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    // -------------------------------------------

    public void drawLine(float x1, float y1, float x2, float y2, float thickness, int colorRGBA) {
        Vector2f pos1 = new Vector2f(x1, y1);
        Vector2f pos2 = new Vector2f(x2, y2);

        Vector2f dir = new Vector2f(pos2)
                .sub(pos1)
                .normalize()
                .mul(thickness / 2.0f);
        Vector2f perp = new Vector2f(-dir.y, dir.x);

        Vector2f p1 = new Vector2f(pos1).sub(dir).sub(perp);
        Vector2f p2 = new Vector2f(pos1).sub(dir).add(perp);
        Vector2f p3 = new Vector2f(pos2).add(dir).add(perp);
        Vector2f p4 = new Vector2f(pos2).add(dir).sub(perp);

        indices(0, 1, 2, 0, 2, 3);
        vertex(p1.x, p1.y, colorRGBA);
        vertex(p2.x, p2.y, colorRGBA);
        vertex(p3.x, p3.y, colorRGBA);
        vertex(p4.x, p4.y, colorRGBA);
    }

    public void drawRect(float x, float y, float w, float h, float thickness, int colorRGBA) {
        float x2 = x + w;
        float y2 = y + h;
        drawLine(x, y, x2, y, thickness, colorRGBA);
        drawLine(x2, y, x2, y2, thickness, colorRGBA);
        drawLine(x2, y2, x, y2, thickness, colorRGBA);
        drawLine(x, y2, x, y, thickness, colorRGBA);
    }

    public void fillRect(float x, float y, float w, float h, int colorRGBA) {
        indices(0, 1, 2, 0, 2, 3);
        vertex(x, y, colorRGBA);
        vertex(x + w, y, colorRGBA);
        vertex(x + w, y + h, colorRGBA);
        vertex(x, y + h, colorRGBA);
    }

    public void drawImage(float x, float y, float w, float h, Texture2DRegion img) { drawImage(x, y, w, h, img, Colors.RGBA.WHITE, 0, 0, 1, 1); }
    public void drawImage(float x, float y, float w, float h, Texture2DRegion img, int tintRGBA) { drawImage(x, y, w, h, img, tintRGBA, 0, 0, 1, 1); }
    public void drawImage(float x, float y, float w, float h, Texture2DRegion img, float u1, float v1, float u2, float v2) { drawImage(x, y, w, h, img, Colors.RGBA.WHITE, u1, v1, u2, v2); }
    public void drawImage(float x, float y, float w, float h, Texture2DRegion img, int tintRGBA, float u1, float v1, float u2, float v2) {
        indices(0, 1, 2, 0, 2, 3);
        int texSlot = getTextureSlot(img.getSourceTexture());
        vertex(x, y, tintRGBA, texSlot, img, u1, v1);
        vertex(x + w, y, tintRGBA, texSlot, img, u2, v1);
        vertex(x + w, y + h, tintRGBA, texSlot, img, u2, v2);
        vertex(x, y + h, tintRGBA, texSlot, img, u1, v2);
    }

    public void drawText(String text, float x, float y, float alignX, float alignY, Font font, int colorRGBA) {
        float width = font.textWidth(text);
        float ascent = font.getMetrics().getAscent();
        float height = font.getMetrics().getHeight();

        drawText(text, x - width * alignX, y + ascent - height * alignY, font, colorRGBA);
    }

    public void drawText(String text, float x, float y, Font font, int colorRGBA) {
        font.draw(this, text, x, y, colorRGBA);
    }

    // -------------------------------------------

    // Screen coordinates!
    public void setClipRect(int x, int y, int w, int h) {
        if (!clipEnabled || clipX != x || clipY != y || clipW != w || clipH != h) {
            clipEnabled = true;
            clipX = x;
            clipY = y;
            clipW = w;
            clipH = h;
            maybeStartNewBatch = true;
        }
    }

    public void disableClip() {
        if (clipEnabled) {
            clipEnabled = false;
            maybeStartNewBatch = true;
        }
    }

    public void setBlendEnabled(boolean blend) {
        if (blend != blendEnabled) {
            blendEnabled = blend;
            maybeStartNewBatch = true;
        }
    }

    public void setBlendOps(BlendOp op) { setBlendOps(op, op); }
    public void setBlendOps(BlendOp rgb, BlendOp alpha) {
        if (blendOpRGB != rgb || blendOpAlpha != alpha) {
            blendOpRGB = rgb;
            blendOpAlpha = alpha;
            maybeStartNewBatch = true;
        }
    }

    public void setBlendFactors(BlendFactor src, BlendFactor dst) { setBlendFactors(src, dst, src, dst); }
    public void setBlendFactors(BlendFactor srcRGB, BlendFactor dstRGB, BlendFactor srcAlpha, BlendFactor dstAlpha) {
        if (blendSrcRGBFactor != srcRGB || blendDstRGBFactor != dstRGB || blendSrcAlphaFactor != srcAlpha || blendDstAlphaFactor != dstAlpha) {
            blendSrcRGBFactor = srcRGB;
            blendDstRGBFactor = dstRGB;
            blendSrcAlphaFactor = srcAlpha;
            blendDstAlphaFactor = dstAlpha;
            maybeStartNewBatch = true;
        }
    }

    // -------------------------------------------

    public void finish() {
        if (finished)
            throw new IllegalStateException("Already finished");

        if (currentBatch != null) {
            finishDrawCmd();
            batches.add(currentBatch);
        }
        meshData.finish();

        finished = true;
    }

    public MeshData getMeshData() {
        if (!finished)
            finish();

        return meshData;
    }

    public List<DrawBatch> getBatches() {
        if (!finished)
            finish();

        return batches;
    }

    @Override
    public void close() {
        meshData.close();
    }
}
