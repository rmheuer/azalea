package com.github.rmheuer.azalea.render2d.font;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import org.joml.Vector2f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class TrueTypeFont extends Font {
    private static final char CHAR_BASE = 32;
    private static final char CHAR_COUNT = 96;

    private final ByteBuffer ttf;
    private final Texture2D atlas;
    private final GlyphInfo[] glyphs;
    private final float heightPx;

    // Copied macro definition from stb_truetype.h
    private static float STBTT_POINT_SIZE_FLOAT(float x) {
        return -x;
    }

    public TrueTypeFont(Renderer renderer, InputStream in, float heightPx) throws IOException {
        ttf = IOUtil.readToByteBuffer(in);
        this.heightPx = heightPx;

        int bitmapWidth = 512;
        int bitmapHeight = 512;
        int pixelCount = bitmapWidth * bitmapHeight;

        STBTTFontinfo fontInfo = STBTTFontinfo.malloc();
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.malloc(CHAR_COUNT);

        stbtt_InitFont(fontInfo, ttf);
        float scale = stbtt_ScaleForMappingEmToPixels(fontInfo, heightPx);

        ByteBuffer bitmap = MemoryUtil.memAlloc(pixelCount);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Get metrics
            IntBuffer bbX0 = stack.mallocInt(1);
            IntBuffer bbY0 = stack.mallocInt(1);
            IntBuffer bbX1 = stack.mallocInt(1);
            IntBuffer bbY1 = stack.mallocInt(1);
            stbtt_GetFontBoundingBox(fontInfo, bbX0, bbY0, bbX1, bbY1);
            // NOTE: This is reversed from the STBTT documentation,
            //       but it seems correct
            metrics = new FontMetrics(
                    bbY1.get(0) * scale, /* Ascent */
                    -bbY0.get(0) * scale  /* Descent */
            );

            STBTTPackContext pc = STBTTPackContext.malloc(stack);
            stbtt_PackBegin(pc, bitmap, bitmapWidth, bitmapHeight, 0, 1, NULL);
            stbtt_PackFontRange(pc, ttf, 0, STBTT_POINT_SIZE_FLOAT(heightPx), CHAR_BASE, cdata);
            stbtt_PackEnd(pc);

            // Convert bitmap data to RGBA
            int[] pixels = new int[pixelCount];
            for (int i = 0; i < pixelCount; i++) {
                pixels[i] = ((bitmap.get(i) << 24) | 0x00FFFFFF);
            }
            MemoryUtil.memFree(bitmap);

            Bitmap atlasBmp = new Bitmap(bitmapWidth, bitmapHeight, pixels);
            atlas = renderer.createTexture2D();
            atlas.setData(atlasBmp);
            atlas.setMinFilter(Texture.Filter.NEAREST);
            atlas.setMagFilter(Texture.Filter.NEAREST);
//            atlas.setMinFilter(Texture.Filter.LINEAR);
//            atlas.setMagFilter(Texture.Filter.LINEAR);

            STBTTAlignedQuad q = STBTTAlignedQuad.malloc(stack);
            glyphs = new GlyphInfo[CHAR_COUNT];
            FloatBuffer x = stack.mallocFloat(1);
            FloatBuffer y = stack.mallocFloat(1);
            IntBuffer advance = stack.mallocInt(1);
            for (int i = 0; i < CHAR_COUNT; i++) {
                x.put(0, 0.0f);
                y.put(0, 0.0f);
                stbtt_GetPackedQuad(
                        cdata,
                        bitmapWidth, bitmapHeight,
                        i,
                        x, y,
                        q,
                        false /* align_to_integer */
                );
                stbtt_GetCodepointHMetrics(fontInfo, (char) (i + CHAR_BASE), advance, null);

                GlyphInfo glyph = new GlyphInfo(
                        atlas,
                        new Vector2f(q.x1() - q.x0(), q.y1() - q.y0()),
                        new Vector2f(q.x0(), q.y0()),
                        advance.get(0) * scale,
                        new Vector2f(q.s0(), q.t0()),
                        new Vector2f(q.s1(), q.t1())
                );
                glyphs[i] = glyph;
            }
        }

        fontInfo.free();
        cdata.free();
    }

    public float getHeightPx() {
        return heightPx;
    }

    @Override
    protected GlyphInfo getGlyph(char c) {
        if (c < 32 || c >= 128)
            return getGlyph('?');

        return glyphs[c - CHAR_BASE];
    }

    @Override
    public void close() {
        MemoryUtil.memFree(ttf);
        atlas.close();
    }
}
