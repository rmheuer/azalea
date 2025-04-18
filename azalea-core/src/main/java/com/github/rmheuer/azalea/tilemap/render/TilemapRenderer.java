package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.render2d.DrawList2D;
import com.github.rmheuer.azalea.tilemap.Tilemap;
import com.github.rmheuer.azalea.tilemap.TilemapLayer;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

public final class TilemapRenderer<T extends RenderableTile<T>> implements SafeCloseable {
    static final class Animation implements SafeCloseable {
        private final Texture2D atlasTex;
        private final int spriteX, spriteY;

        private final AnimationFrame[] frames;
        private int currentFrame;
        private float timeUntilNextFrame;
        private Bitmap interpolated;

        private boolean needsUpdateTexture;

        public Animation(TextureCache.StoredTexture texture, AnimationFrame[] frames) {
            atlasTex = texture.region.getSourceTexture();
            spriteX = texture.x;
            spriteY = texture.y;

            this.frames = frames;
            currentFrame = 0;
            timeUntilNextFrame = frames[currentFrame].getTime();
            interpolated = null;

            needsUpdateTexture = true;
        }

        public void tick(float dt) {
            timeUntilNextFrame -= dt;
            while (timeUntilNextFrame < 0) {
                currentFrame++;
                currentFrame %= frames.length;

                AnimationFrame frame = frames[currentFrame];
                timeUntilNextFrame += frame.getTime();

                needsUpdateTexture = true;
            }

            if (frames[currentFrame].isInterpolateToNext())
                needsUpdateTexture = true;
        }

        public void updateTexture() {
            if (!needsUpdateTexture)
                return;

            AnimationFrame currentF = frames[currentFrame];
            if (currentF.isInterpolateToNext()) {
                BitmapRegion current = currentF.getImg();
                BitmapRegion next = frames[(currentFrame + 1) % frames.length].getImg();

                ColorFormat format = current.getColorFormat();
                if (interpolated == null)
                    interpolated = new Bitmap(current.getWidth(), current.getHeight(), format);

                float f = 1 - timeUntilNextFrame / currentF.getTime();
                for (int y = 0; y < current.getWidth(); y++) {
                    for (int x = 0; x < current.getHeight(); x++) {
                        int currentCol = current.getPixel(x, y);
                        int nextCol = next.getPixel(x, y);
                        interpolated.setPixel(x, y, format.lerp(currentCol, nextCol, f));
                    }
                }

                atlasTex.setSubData(interpolated, spriteX, spriteY);
            } else {
                atlasTex.setSubData(currentF.getImg(), spriteX, spriteY);
            }

            needsUpdateTexture = false;
        }

        @Override
        public void close() {
            if (interpolated != null)
                interpolated.close();

            for (AnimationFrame frame : frames) {
                frame.close();
            }
        }
    }

    private final TextureCache textureCache;
    private final List<Animation> animations;

    private Tilemap<T> tilemap;
    private TileRenderer<T> tileRenderer;

    private final FrustumIntersection frustum = new FrustumIntersection();

    public TilemapRenderer(Renderer renderer) {
        textureCache = new TextureCache(renderer, 512, 0);
        animations = new ArrayList<>();

        tilemap = null;
        tileRenderer = DefaultTileRenderer.getInstance();
    }

    // All createXXXSprite() methods take ownership of the bitmap(s)

    public TileSprite createStaticSprite(Bitmap img) {
        TextureCache.StoredTexture stored = textureCache.store(img);
        img.close();

        return new TileSprite(stored.region.getFlippedVertically(), null);
    }

    public TileSprite createAnimatedSprite(float fps, Bitmap... frames) {
        float frameTime = 1 / fps;
        AnimationFrame[] animFrames = new AnimationFrame[frames.length];
        for (int i = 0; i < frames.length; i++) {
            animFrames[i] = new AnimationFrame(frames[i], frameTime);
        }

        return createAnimatedSprite(animFrames);
    }

    public TileSprite createInterpolatedAnimatedSprite(float fps, Bitmap... frames) {
        float frameTime = 1 / fps;
        AnimationFrame[] animFrames = new AnimationFrame[frames.length];
        for (int i = 0; i < frames.length; i++) {
            animFrames[i] = new AnimationFrame(frames[i], frameTime, true);
        }

        return createAnimatedSprite(animFrames);
    }

    public TileSprite createAnimatedSprite(AnimationFrame... frames) {
        if (frames.length < 1)
            throw new IllegalArgumentException("Animation must have at least one frame");

        // Make sure animation works
        int width = frames[0].getImg().getWidth();
        int height = frames[0].getImg().getHeight();
        ColorFormat format = frames[0].getImg().getColorFormat();
        float totalTime = 0;
        for (int i = 1; i < frames.length; i++) {
            if (width != frames[i].getImg().getWidth() ||
                    height != frames[i].getImg().getHeight()) {
                throw new IllegalArgumentException("All animation frames must be the same size");
            }
            if (format != frames[i].getImg().getColorFormat())
                throw new IllegalArgumentException("All animation frames must have the same color format");

            float time = frames[i].getTime();
            if (time < 0)
                throw new IllegalArgumentException("Frame time must be positive");
            totalTime += time;
        }
        if (totalTime == 0)
            throw new IllegalArgumentException("Total animation time must be nonzero");

        TextureCache.StoredTexture stored = textureCache.reserve(width, height, format);
        Animation anim = new Animation(stored, frames);
        animations.add(anim);

        return new TileSprite(stored.region.getFlippedVertically(), anim);
    }

    public void tickAnimations(float dt) {
        for (Animation anim : animations) {
            anim.tick(dt);
        }
    }

    private boolean updateFrustum(Matrix4f modelView) {
        if (modelView == null)
            return false;

        frustum.set(modelView);
        return true;
    }

    public void renderAllLayers(DrawList2D draw) { renderAllLayers(draw, null); }
    public void renderAllLayers(DrawList2D draw, Matrix4f modelView) {
        boolean useFrustumCull = updateFrustum(modelView);
        for (TilemapLayer<T> layer : tilemap.getLayersBackToFront()) {
            renderLayer(layer, draw, useFrustumCull);
        }
    }

    public void renderLayer(int layer, DrawList2D draw) { renderLayer(layer, draw, null); }
    public void renderLayer(int layer, DrawList2D draw, Matrix4f modelView) {
        boolean useFrustumCull = updateFrustum(modelView);
        renderLayer(tilemap.getLayer(layer), draw, useFrustumCull);
    }

    private void renderLayer(TilemapLayer<T> layer, DrawList2D draw, boolean useFrustumCull) {
        // TODO: Actually do frustum cull

        Vector2ic min = layer.getBoundsMin();
        int minX = min.x();
        int minY = min.y();
        Vector2ic max = layer.getBoundsMax();
        int maxX = max.x();
        int maxY = max.y();

        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                T tile = layer.getTile(x, y);
                if (tile == null)
                    continue;

                TileSprite sprite = tile.getSprite(tilemap, x, y);
                if (sprite == null)
                    continue;

                Animation anim = sprite.getAnimation();
                if (anim != null)
                    anim.updateTexture();

                tileRenderer.renderTile(draw, sprite.getTexRegion(), tile, tilemap, x, y);
            }
        }
    }

    public void setTilemap(Tilemap<T> tilemap) {
        this.tilemap = tilemap;
    }

    public void setTileRenderer(TileRenderer<T> tileRenderer) {
        this.tileRenderer = tileRenderer;
    }

    @Override
    public void close() {
        for (Animation animation : animations) {
            animation.close();
        }
        textureCache.close();
    }
}
