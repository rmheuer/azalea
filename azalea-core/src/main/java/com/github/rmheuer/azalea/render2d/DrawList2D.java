package com.github.rmheuer.azalea.render2d;

import com.github.rmheuer.azalea.math.MathUtil;
import com.github.rmheuer.azalea.math.PoseStack;
import com.github.rmheuer.azalea.math.Transform;
import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.render.texture.Texture2DRegion;
import com.github.rmheuer.azalea.render2d.font.Font;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

// TODO: Add renderer state change functions (clipping, blending), then allow
//  multiple draw commands for one buffer
public class DrawList2D {
    private static final int CURVE_PRECISION = 8;
    private static final float[] curveLookup = new float[CURVE_PRECISION * 2 + 2];

    static {
        for (int i = 0; i <= CURVE_PRECISION; i++) {
            double angle = i / (double) CURVE_PRECISION * Math.PI / 2;

            curveLookup[i * 2] = (float) Math.cos(angle);
            curveLookup[i * 2 + 1] = (float) Math.sin(angle);
        }
    }

    private final List<DrawVertex> vertices;
    private final List<Integer> indices;

    private PoseStack poseStack;

    private float depth;

    public DrawList2D() {
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        poseStack = new PoseStack();
        depth = 0;
    }

    public void join(DrawList2D other) {
        int mark = vertices.size();
        vertices.addAll(other.vertices);
        for (int index : other.indices) {
            indices.add(index + mark);
        }
    }

    public boolean isEmpty() {
        return vertices.isEmpty() && indices.isEmpty();
    }

    List<DrawVertex> getVertices() {
        return vertices;
    }

    List<Integer> getIndices() {
        return indices;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public void pushTransform() {
        poseStack.push();
    }

    public void popTransform() {
        poseStack.pop();
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public void applyTransform(Transform tx) {
        poseStack.applyTransform(tx);
    }
    
    private void vertex(float x, float y, int color) { vertex(x, y, 0, 0, color, null); }
    private void vertex(float x, float y, float u, float v, int tint, Texture2DRegion tex) {
        Vector3f pos = new Vector3f(x, y, depth);
        pos = poseStack.getMatrix().transformPosition(pos);

        if (tex != null) {
            Vector2f uvMin = tex.getRegionTopLeftUV();
            Vector2f uvMax = tex.getRegionBottomRightUV();

            u = MathUtil.lerp(uvMin.x, uvMax.x, u);
            v = MathUtil.lerp(uvMin.y, uvMax.y, v);
        }

        vertices.add(new DrawVertex(pos, u, v, tint, tex));
    }

    private void addIndices(int... i) {
        int base = vertices.size();
        for (int index : i) {
            indices.add(base + index);
        }
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

    public void drawLineStrip(Vector2f[] points, float width, int colorRGBA) { drawLineStrip(Arrays.asList(points), width, colorRGBA); }
    public void drawLineStrip(List<Vector2f> points, float width, int colorRGBA) {
        Vector2f prevPoint = null;
        for (Vector2f point : points) {
            if (prevPoint != null)
                drawLine(prevPoint.x, prevPoint.y, point.x, point.y, width, colorRGBA);

            prevPoint = point;
        }
    }

    public void fillConvexPolygon(Vector2f[] points, int colorRGBA) { fillConvexPolygon(Arrays.asList(points), colorRGBA); }
    public void fillConvexPolygon(List<Vector2f> points, int colorRGBA) {
        int mark = vertices.size();
        boolean setFirst = false;
        int lastIndex = -1;

        for (Vector2f point : points) {
            vertex(point.x, point.y, colorRGBA);

            if (!setFirst) {
                setFirst = true;
            } else if (lastIndex < 0) {
                lastIndex = 1;
            } else {
                int temp = lastIndex;
                lastIndex++;
                indices.add(mark);
                indices.add(mark + temp);
                indices.add(mark + lastIndex);
            }
        }
    }

    public void drawLine(float x1, float y1, float x2, float y2, float width, int colorRGBA) {
        Vector2f pos1 = new Vector2f(x1 + 0.5f, y1 + 0.5f);
        Vector2f pos2 = new Vector2f(x2 + 0.5f, y2 + 0.5f);

        Vector2f dir = new Vector2f(pos2)
                .sub(pos1)
                .normalize()
                .mul(width / 2.0f);
        Vector2f perp = new Vector2f(-dir.y, dir.x);

        Vector2f p1 = new Vector2f(pos1).sub(dir).sub(perp);
        Vector2f p2 = new Vector2f(pos1).sub(dir).add(perp);
        Vector2f p3 = new Vector2f(pos2).add(dir).add(perp);
        Vector2f p4 = new Vector2f(pos2).add(dir).sub(perp);

        addIndices(0, 1, 2, 0, 2, 3);
        vertex(p1.x, p1.y, colorRGBA);
        vertex(p2.x, p2.y, colorRGBA);
        vertex(p3.x, p3.y, colorRGBA);
        vertex(p4.x, p4.y, colorRGBA);
    }

    public void drawQuad(float x, float y, float w, float h, float width, int colorRGBA) {
        float x2 = x + w - 1;
        float y2 = y + h - 1;
        drawLine(x, y, x2, y, width, colorRGBA);
        drawLine(x2, y, x2, y2, width, colorRGBA);
        drawLine(x2, y2, x, y2, width, colorRGBA);
        drawLine(x, y2, x, y, width, colorRGBA);
    }

    public void fillQuad(float x, float y, float w, float h, int colorRGBA) {
        addIndices(0, 1, 2, 0, 2, 3);
        vertex(x, y, colorRGBA);
        vertex(x + w, y, colorRGBA);
        vertex(x + w, y + h, colorRGBA);
        vertex(x, y + h, colorRGBA);
    }

    public void drawRoundedQuad(float x, float y, float w, float h, float rad, float width, int colorRGBA) { drawRoundedQuad(x, y, w, h, rad, rad, rad, rad, width, colorRGBA); }
    public void drawRoundedQuad(float x, float y, float w, float h, float ul, float ur, float ll, float lr, float width, int colorRGBA) {
        float mx = x + w - 1;
        float my = y + h - 1;

        // Edges
        drawLine(x + ul, y, mx - ur, y, width, colorRGBA);
        drawLine(x, y + ul, x, my - ll, width, colorRGBA);
        drawLine(x + ll, my, mx - lr, my, width, colorRGBA);
        drawLine(mx, y + ur, mx, my - lr, width, colorRGBA);

        // Corners
        for (int i = 1; i <= CURVE_PRECISION; i++) {
            float lx = curveLookup[i * 2 - 2];
            float ly = curveLookup[i * 2 - 1];
            float px = curveLookup[i * 2];
            float py = curveLookup[i * 2 + 1];

            drawLine(mx - lr + lr * lx, my - lr + lr * ly, mx - lr + lr * px, my - lr + lr * py, width, colorRGBA);
            drawLine(x + ul - ul * lx, y + ul - ul * ly, x + ul - ul * px, y + ul - ul * py, width, colorRGBA);
            drawLine(mx - ur + ur * lx, y + ur - ur * ly, mx - ur + ur * px, y + ur - ur * py, width, colorRGBA);
            drawLine(x + ll - ll * lx, my - ll + ll * ly, x + ll - ll * px, my - ll + ll * py, width, colorRGBA);
        }
    }

    public void fillRoundedQuad(float x, float y, float w, float h, float rad, int colorRGBA) { fillRoundedQuad(x, y, w, h, rad, rad, rad, rad, colorRGBA); }
    public void fillRoundedQuad(float x, float y, float w, float h, float ul, float ur, float ll, float lr, int colorRGBA) {
        float maxX = x + w;
        float maxY = y + h;
        List<Vector2f> v = new ArrayList<>();

        // Bottom right
        for (int i = 0; i <= CURVE_PRECISION; i++) {
            float vx = curveLookup[i * 2] * lr + maxX - lr;
            float vy = curveLookup[i * 2 + 1] * lr + maxY - lr;
            v.add(new Vector2f(vx, vy));
        }

        // Bottom left
        for (int i = CURVE_PRECISION; i >= 0; i--) {
            float vx = x + ll - curveLookup[i * 2] * ll;
            float vy = curveLookup[i * 2 + 1] * ll + maxY - ll;
            v.add(new Vector2f(vx, vy));
        }

        // Top left
        for (int i = 0; i <= CURVE_PRECISION; i++) {
            float vx = x + ur - curveLookup[i * 2] * ul;
            float vy = y + ur - curveLookup[i * 2 + 1] * ul;
            v.add(new Vector2f(vx, vy));
        }

        // Top right
        for (int i = CURVE_PRECISION; i >= 0; i--) {
            float vx = curveLookup[i * 2] * ur + maxX - ur;
            float vy = y + ur - curveLookup[i * 2 + 1] * ur;
            v.add(new Vector2f(vx, vy));
        }

        fillConvexPolygon(v, colorRGBA);
    }

    public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float width, int colorRGBA) {
        drawLine(x1, y1, x2, y2, width, colorRGBA);
        drawLine(x2, y2, x3, y3, width, colorRGBA);
        drawLine(x3, y3, x1, y1, width, colorRGBA);
    }

    public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int colorRGBA) {
        addIndices(0, 1, 2);
        vertex(x1, y1, colorRGBA);
        vertex(x2, y2, colorRGBA);
        vertex(x3, y3, colorRGBA);
    }

    public void drawImage(float x, float y, float w, float h, Texture2DRegion img) { drawImage(x, y, w, h, img, Colors.RGBA.WHITE, 0, 0, 1, 1); }
    public void drawImage(float x, float y, float w, float h, Texture2DRegion img, int tintRGBA) { drawImage(x, y, w, h, img, tintRGBA, 0, 0, 1, 1); }
    public void drawImage(float x, float y, float w, float h, Texture2DRegion img, float u0, float v0, float u1, float v1) { drawImage(x, y, w, h, img, Colors.RGBA.WHITE, u0, v0, u1, v1); }
    public void drawImage(float x, float y, float w, float h, Texture2DRegion img, int tintRGBA, float u0, float v0, float u1, float v1) {
        addIndices(0, 1, 2, 0, 2, 3);
        vertex(x, y, u0, v0, tintRGBA, img);
        vertex(x + w, y, u1, v0, tintRGBA, img);
        vertex(x + w, y + h, u1, v1, tintRGBA, img);
        vertex(x, y + h, u0, v1, tintRGBA, img);
    }

    public void drawImageQuad(
            float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
            Texture2DRegion img,
            float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {
        drawImageQuad(x1, y1, x2, y2, x3, y3, x4, y4, img, Colors.RGBA.WHITE, u0, v0, u1, v1, u2, v2, u3, v3);
    }

    public void drawImageQuad(
            float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
            Texture2DRegion img, int tintRGBA,
            float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {
        addIndices(0, 1, 2, 0, 2, 3);
        vertex(x1, y1, u0, v0, tintRGBA, img);
        vertex(x2, y2, u1, v1, tintRGBA, img);
        vertex(x3, y3, u2, v2, tintRGBA, img);
        vertex(x4, y4, u3, v3, tintRGBA, img);
    }
}
