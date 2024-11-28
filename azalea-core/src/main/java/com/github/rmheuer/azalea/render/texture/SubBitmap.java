package com.github.rmheuer.azalea.render.texture;

final class SubBitmap implements BitmapRegion {
    private final Bitmap srcBitmap;
    private final int x, y, width, height;

    public SubBitmap(Bitmap srcBitmap, int x, int y, int width, int height) {
        this.srcBitmap = srcBitmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(x + ", " + y);
        }
    }

    @Override
    public ColorFormat getColorFormat() {
        return srcBitmap.getColorFormat();
    }

    @Override
    public int getPixel(int x, int y) {
        checkBounds(x, y);
        return srcBitmap.getPixel(this.x + x, this.y + y);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        checkBounds(x, y);
        srcBitmap.setPixel(this.x + x, this.y + y, color);
    }

    @Override
    public void fill(int color) {
        if (spansFullSource()) {
            srcBitmap.fill(color);
            return;
        }

        long srcPtr = srcBitmap.getPixelDataPtr();
        ColorFormat format = srcBitmap.getColorFormat();

        int byteCount = format.getByteCount();
        int stride = srcBitmap.getWidth() * byteCount;
        long base = srcPtr + x * byteCount + y * stride;

        for (int y = 0; y < height; y++) {
            format.fillBuffer(base + y * stride, width, color);
        }
    }

    @Override
    public void blit(BitmapRegion src, int x, int y) {
        checkBounds(x, y);
        if (x + src.getWidth() > width || y + src.getHeight() > height)
            throw new IndexOutOfBoundsException("Image extends out of bounds");

        srcBitmap.blit(src, x + this.x, y + this.y);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean spansFullSource() {
        return width == srcBitmap.getWidth() && height == srcBitmap.getHeight();
    }

    @Override
    public Bitmap getSourceBitmap() {
        return srcBitmap;
    }

    @Override
    public int getSourceOffsetX() {
        return x;
    }

    @Override
    public int getSourceOffsetY() {
        return y;
    }
}
