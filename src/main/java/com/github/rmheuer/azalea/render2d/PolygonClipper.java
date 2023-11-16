package com.github.rmheuer.azalea.render2d;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public final class PolygonClipper {
    private static boolean isInside(Vector2f p1, Vector2f p2, Vector2f q) {
        float r = (p2.x - p1.x) * (q.y - p1.y) - (p2.y - p1.y) * (q.x - p1.x);
        return r >= 0;
    }

    private static Vector2f computeIntersection(Vector2f p1, Vector2f p2, Vector2f p3, Vector2f p4) {
        float x, y;

        if (p2.x - p1.x == 0) {
            x = p1.x;
            float m2 = (p4.y - p3.y) / (p4.x - p3.x);
            float b2 = p3.y - m2 * p3.x;
            y = m2 * x + b2;
        } else if (p4.x - p3.x == 0) {
            x = p3.x;
            float m1 = (p2.y - p1.y) / (p2.x - p1.x);
            float b1 = p1.y - m1 * p1.x;
            y = m1 * x + b1;
        } else {
            float m1 = (p2.y - p1.y) / (p2.x - p1.x);
            float b1 = p1.y - m1 * p1.x;

            float m2 = (p4.y - p3.y) / (p4.x - p3.x);
            float b2 = p3.y - m2 * p3.x;

            x = (b2 - b1) / (m1 - m2);
            y = m1 * x + b1;
        }

        return new Vector2f(x, y);
    }

    private static void clipEdge(List<Vector2f> finalPolygon, Vector2f cEdgeStart, Vector2f cEdgeEnd) {
        List<Vector2f> nextPolygon = new ArrayList<>(finalPolygon);
        finalPolygon.clear();

        for (int i = 0; i < nextPolygon.size(); i++) {
            Vector2f sEdgeStart;
            if (i == 0)
                sEdgeStart = nextPolygon.get(nextPolygon.size() - 1);
            else
                sEdgeStart = nextPolygon.get(i - 1);
            Vector2f sEdgeEnd = nextPolygon.get(i);

            if (isInside(cEdgeStart, cEdgeEnd, sEdgeEnd)) {
                if (!isInside(cEdgeStart, cEdgeEnd, sEdgeStart)) {
                    Vector2f intersection = computeIntersection(sEdgeStart, sEdgeEnd, cEdgeStart, cEdgeEnd);
                    finalPolygon.add(intersection);
                }
                finalPolygon.add(sEdgeEnd);
            } else if (isInside(cEdgeStart, cEdgeEnd, sEdgeStart)) {
                Vector2f intersection = computeIntersection(sEdgeStart, sEdgeEnd, cEdgeStart, cEdgeEnd);
                finalPolygon.add(intersection);
            }
        }
    }

    public static List<Vector2f> clip(List<Vector2f> targetPolygon, List<Vector2f> clippingPolygon) {
        List<Vector2f> clipped = new ArrayList<>(targetPolygon);
        for (int i = 0; i < clippingPolygon.size(); i++) {
            Vector2f edgeStart;
            if (i == 0)
                edgeStart = clippingPolygon.get(clippingPolygon.size() - 1);
            else
                edgeStart = clippingPolygon.get(i - 1);
            Vector2f edgeEnd = clippingPolygon.get(i);

            clipEdge(clipped, edgeStart, edgeEnd);
        }
        return clipped;
    }

    public static List<Vector2f> clip(List<Vector2f> targetPolygon, Rectangle rect) {
        List<Vector2f> clip = new ArrayList<>();
        clip.add(rect.getMin());
        clip.add(new Vector2f(rect.getMax().x, rect.getMin().y));
        clip.add(rect.getMax());
        clip.add(new Vector2f(rect.getMin().x, rect.getMax().y));
        return clip(targetPolygon, clip);
    }

    private PolygonClipper() {
        throw new AssertionError();
    }
}
