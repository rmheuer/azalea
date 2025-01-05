package com.github.rmheuer.azalea.utils;

public final class RectPackNode {
    public final int x, y;
    public final int width, height;

    private RectPackNode childA, childB;
    private boolean filled;

    public RectPackNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        childA = childB = null;
        filled = false;
    }

    public RectPackNode insert(int rectW, int rectH, int pad) {
        if (filled)
            return null;

        if (childA != null && childB != null) {
            RectPackNode newNode = childA.insert(rectW, rectH, pad);
            if (newNode != null)
                return newNode;
            return childB.insert(rectW, rectH, pad);
        }

        if (rectW > width || rectH > height)
            return null;
        if (width - rectW <= pad && height - rectH <= pad) {
            filled = true;
            return this;
        }

        int dw = width - rectW;
        int dh = height - rectH;
        if (dw > dh) {
            childA = new RectPackNode(x, y, rectW, height);
            childB = new RectPackNode(x + rectW + pad, y, width - rectW - pad, height);
        } else {
            childA = new RectPackNode(x, y, width, rectH);
            childB = new RectPackNode(x, y + rectH + pad, width, height - rectH - pad);
        }

        return childA.insert(rectW, rectH, pad);
    }
}
