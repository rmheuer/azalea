package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.texture.*;
import com.github.rmheuer.azalea.utils.SafeCloseable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TextureCache implements SafeCloseable {
    public static final class StoredTexture {
        public final Texture2DRegion region;
        public final int x, y;

        public StoredTexture(Texture2DRegion region, int x, int y) {
            this.region = region;
            this.x = x;
            this.y = y;
        }
    }

    private static final class Node {
        private final int x, y;
        private final int width, height;
        private Node childA, childB;
        private boolean filled;

        public Node(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Node insert(int imageW, int imageH, int pad) {
            if (filled)
                return null;

            if (childA != null && childB != null) {
                Node newNode = childA.insert(imageW, imageH, pad);
                if (newNode != null)
                    return newNode;
                return childB.insert(imageW, imageH, pad);
            }

            if (imageW > width || imageH > height)
                return null;
            if (width - imageW <= pad && height - imageH <= pad) {
                filled = true;
                return this;
            }

            int dw = width - imageW;
            int dh = height - imageH;
            if (dw > dh) {
                childA = new Node(x, y, imageW, height);
                childB = new Node(x + imageW + pad, y, width - imageW - pad, height);
            } else {
                childA = new Node(x, y, width, imageH);
                childB = new Node(x, y + imageH + pad, width, height - imageH - pad);
            }

            return childA.insert(imageW, imageH, pad);
        }
    }

    private static final class Page implements SafeCloseable {
        private final int size;
        private final int padding;
        private final Texture2D texture;
        private final Node root;

        // Dimensions of the smallest image that didn't fit
        private int minNotFitW, minNotFitH;

        public Page(Renderer renderer, ColorFormat colorFormat, int size, int padding) {
            this.size = size;
            this.padding = padding;

            System.out.println("Allocating new " + colorFormat + " texture cache page");
            texture = renderer.createTexture2D();
            texture.setSize(size, size, colorFormat);
            if (colorFormat == ColorFormat.GRAYSCALE) {
                texture.setChannelMapping(ChannelMapping.GRAYSCALE_TRANSPARENCY);
            }

            root = new Node(0, 0, size, size);
            minNotFitW = minNotFitH = Integer.MAX_VALUE;
        }

        public StoredTexture reserve(int width, int height) {
            // Early out if image definitely won't fit in this page
            boolean couldFit = (width < minNotFitW && height <= minNotFitH)
                    || (width <= minNotFitW && height < minNotFitH);
            if (!couldFit) {
                return null;
            }

            Node imageNode = root.insert(width, height, padding);
            if (imageNode == null) {
                minNotFitW = Math.min(minNotFitW, width);
                minNotFitH = Math.min(minNotFitH, height);

                return null;
            }

            Texture2DRegion region = texture.getSubRegion(
                    (float) imageNode.x / size,
                    (float) imageNode.y / size,
                    (float) (imageNode.x + width) / size,
                    (float) (imageNode.y + height) / size
            );
            return new StoredTexture(region, imageNode.x, imageNode.y);
        }

        @Override
        public void close() {
            texture.close();
        }
    }

    private final Renderer renderer;
    private final int size;
    private final int padding;

    private final Map<ColorFormat, List<Page>> pages;
    private final List<Texture2D> largeSprites;

    public TextureCache(Renderer renderer, int size, int padding) {
        this.renderer = renderer;
        this.size = size;
        this.padding = padding;

        pages = new EnumMap<>(ColorFormat.class);
        largeSprites = new ArrayList<>();
    }

    public StoredTexture reserve(int width, int height, ColorFormat format) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Image must have positive size");

        if (width > size || height > size) {
            // Image is too large to fit in a shared page, so it gets its own texture
            Texture2D tex = renderer.createTexture2D();
            tex.setSize(width, height, format);
            if (format == ColorFormat.GRAYSCALE) {
                tex.setChannelMapping(ChannelMapping.GRAYSCALE_TRANSPARENCY);
            }

            largeSprites.add(tex);
            return new StoredTexture(tex, 0, 0);
        }

        List<Page> pageList = pages.computeIfAbsent(format, (f) -> new ArrayList<>());

        for (Page page : pageList) {
            StoredTexture stored = page.reserve(width, height);
            if (stored != null)
                return stored;
        }

        Page newPage = new Page(renderer, format, size, padding);
        pageList.add(newPage);

        StoredTexture stored = newPage.reserve(width, height);
        if (stored == null)
            throw new AssertionError("Image did not fit in empty page");
        return stored;
    }

    public StoredTexture store(BitmapRegion image) {
        StoredTexture stored = reserve(image.getWidth(), image.getHeight(), image.getColorFormat());
        stored.region.getSourceTexture().setSubData(image, stored.x, stored.y);
        return stored;
    }

    @Override
    public void close() {
        for (List<Page> pageList : pages.values()) {
            for (Page page : pageList) {
                page.close();
            }
        }
        for (Texture2D texture : largeSprites) {
            texture.close();
        }
    }
}
