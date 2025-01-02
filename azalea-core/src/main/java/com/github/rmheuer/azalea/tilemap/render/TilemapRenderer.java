package com.github.rmheuer.azalea.tilemap.render;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.framebuffer.Framebuffer;
import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.render2d.DrawList2D;
import com.github.rmheuer.azalea.render2d.Renderer2D;
import com.github.rmheuer.azalea.tilemap.Tilemap;
import com.github.rmheuer.azalea.tilemap.TilemapLayer;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TilemapRenderer implements SafeCloseable {
    private static final class Animation implements SafeCloseable {
        private final Texture2D atlasTex;
        private final int spriteX, spriteY;

        private final AnimationFrame[] frames;
        private int currentFrame;
        private float timeUntilNextFrame;
        private Bitmap interpolated;

        public Animation(TextureCache.StoredTexture texture, AnimationFrame[] frames) {
            atlasTex = texture.region.getSourceTexture();
            spriteX = texture.x;
            spriteY = texture.y;

            this.frames = frames;
            currentFrame = 0;
            timeUntilNextFrame = frames[currentFrame].getTime();
            interpolated = null;

            // Set initial data
            atlasTex.setSubData(frames[currentFrame].getImg(), spriteX, spriteY);
        }

        // TODO: Only update texture when changed, and in render()
        public void tick(float dt) {
            timeUntilNextFrame -= dt;
            while (timeUntilNextFrame < 0) {
                currentFrame++;
                currentFrame %= frames.length;

                AnimationFrame frame = frames[currentFrame];
                timeUntilNextFrame += frame.getTime();

                if (!frame.isInterpolateToNext())
                    atlasTex.setSubData(frame.getImg(), spriteX, spriteY);
            }

            if (frames[currentFrame].isInterpolateToNext()) {
                AnimationFrame currentF = frames[currentFrame];

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
            }
        }

        @Override
        public void close() {
            if (interpolated != null)
                interpolated.close();
        }
    }

    // Needed to make the generics work
    private static final class TilemapWrapper<T extends RenderableTile<T>> {
        public final Tilemap<T> tilemap;

        public TilemapWrapper(Tilemap<T> tilemap) {
            this.tilemap = tilemap;
        }
    }

    private final TextureCache textureCache;
    private final List<Animation> animations;

    private TilemapWrapper<?> tilemapWrapper;
    private final Map<Integer, Boolean> visibleLayers;
    private float zPerLayer;

    public TilemapRenderer(Renderer renderer) {
        textureCache = new TextureCache(renderer, 512, 0);
        animations = new ArrayList<>();

        tilemapWrapper = null;
        visibleLayers = new HashMap<>();
        zPerLayer = 1.0f;
    }

    public TileSprite createStaticSprite(BitmapRegion img) {
        TextureCache.StoredTexture stored = textureCache.store(img);
        return new TileSprite(stored.region.getFlippedVertically());
    }

    public TileSprite createAnimatedSprite(float fps, BitmapRegion... frames) {
        float frameTime = 1 / fps;
        AnimationFrame[] animFrames = new AnimationFrame[frames.length];
        for (int i = 0; i < frames.length; i++) {
            animFrames[i] = new AnimationFrame(frames[i], frameTime);
        }

        return createAnimatedSprite(animFrames);
    }

    public TileSprite createInterpolatedAnimatedSprite(float fps, BitmapRegion... frames) {
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

        return new TileSprite(stored.region.getFlippedVertically());
    }

    public <T extends RenderableTile<T>> void setTilemap(Tilemap<T> tilemap) {
        tilemapWrapper = tilemap != null ? new TilemapWrapper<>(tilemap) : null;
    }

    public void setLayerVisible(int layer, boolean visible) {
        visibleLayers.put(layer, visible);
    }

    public void setLayerZScale(float zPerLayer) {
        this.zPerLayer = zPerLayer;
    }

    public void tickAnimations(float dt) {
        for (Animation anim : animations) {
            anim.tick(dt);
        }
    }

    public void renderTilemap(Matrix4f proj, Matrix4f view, Renderer2D renderer2D) {
        renderTilemapGeneric(
                proj,
                view,
                renderer2D,
                renderer2D.getRenderer().getDefaultFramebuffer(),
                tilemapWrapper.tilemap
        );
    }

    public void renderTilemap(Matrix4f proj, Matrix4f view, Renderer2D renderer2D, Framebuffer targetFb) {
        renderTilemapGeneric(proj, view, renderer2D, targetFb, tilemapWrapper.tilemap);
    }

    private <T extends RenderableTile<T>> void renderTilemapGeneric(
            Matrix4f proj,
            Matrix4f view,
            Renderer2D renderer2D,
            Framebuffer targetFb,
            Tilemap<T> tilemap
    ) {
        for (TilemapLayer<T> layer : tilemap.getLayersBackToFront()) {
            boolean visible = visibleLayers.getOrDefault(layer.getZIndex(), true);
            if (visible)
                renderLayer(proj, view, renderer2D, targetFb, tilemap, layer);
        }
    }

    private <T extends RenderableTile<T>> void renderLayer(
            Matrix4f proj,
            Matrix4f view,
            Renderer2D renderer2D,
            Framebuffer targetFb,
            Tilemap<T> tilemap,
            TilemapLayer<T> layer
    ) {
        // TODO: Cache tilemap meshes
        // TODO: Frustum cull tile chunks

        Vector2ic min = layer.getBoundsMin();
        int minX = min.x();
        int minY = min.y();
        Vector2ic max = layer.getBoundsMax();
        int maxX = max.x();
        int maxY = max.y();

        DrawList2D draw = new DrawList2D();
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                T tile = layer.getTile(x, y);
                if (tile == null)
                    continue;

                TileSprite sprite = tile.getSprite(tilemap, x, y);
                if (sprite == null)
                    continue;

                draw.drawImage(x, y, 1, 1, sprite.getTexRegion());
            }
        }

        Matrix4f model = new Matrix4f().translation(0, 0, layer.getZIndex() * zPerLayer);
        Matrix4f mvp = new Matrix4f(proj).mul(view).mul(model);
        renderer2D.draw(draw, mvp, targetFb);
    }

    @Override
    public void close() {
        for (Animation animation : animations) {
            animation.close();
        }
        textureCache.close();
    }
}
