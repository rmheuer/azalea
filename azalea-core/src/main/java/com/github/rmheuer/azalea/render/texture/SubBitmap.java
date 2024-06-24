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
    public int getPixel(int x, int y) {
        checkBounds(x, y);
        return srcBitmap.getPixel(this.x + x, this.y + y);
    }

    @Override
    public void setPixel(int x, int y, int colorRGBA) {
        checkBounds(x, y);
        srcBitmap.setPixel(this.x + x, this.y + y, colorRGBA);
    }

    @Override
    public void blit(BitmapRegion src, int x, int y) {
        if (x < 0 || y < 0 || x + src.getWidth() > width || y + src.getHeight() > height)
            throw new IndexOutOfBoundsException(x + ", " + y);

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
    public int[] getRgbaData() {
        int[] data = new int[width * height];
        int[] srcData = srcBitmap.getRgbaData();
        for (int y = 0; y < height; y++) {
            System.arraycopy(srcData, this.x + (this.y + y) * srcBitmap.getWidth(), data, y * width, width);
        }
        return data;
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
