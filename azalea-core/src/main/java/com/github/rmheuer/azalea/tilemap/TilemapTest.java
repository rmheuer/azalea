package com.github.rmheuer.azalea.tilemap;

import com.github.rmheuer.azalea.io.ResourceUtil;
import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.WindowSettings;
import com.github.rmheuer.azalea.render.camera.Camera;
import com.github.rmheuer.azalea.render.camera.ScaledOrthoProjection;
import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render2d.Renderer2D;
import com.github.rmheuer.azalea.runtime.BaseGame;
import com.github.rmheuer.azalea.tilemap.render.RenderableTile;
import com.github.rmheuer.azalea.tilemap.render.TileSprite;
import com.github.rmheuer.azalea.tilemap.render.TilemapRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.io.IOException;

public final class TilemapTest extends BaseGame {
    private enum Tile implements RenderableTile<Tile> {
        EMPTY(0),
        BACKGROUND(2),
        SOLID(1),
        GOAL(3, 4, 5),
        CUBE_WIRE_ON(9, 10, 11, 10);

        public static void init(TilemapRenderer renderer) throws IOException {
            try (Bitmap atlas = Bitmap.decode(ResourceUtil.readAsStream("tiles-test.png"))) {
                for (Tile tile : values()) {
                    if (tile.frameIndices.length == 1) {
                        int idx = tile.frameIndices[0] - 1;
                        if (idx < 0)
                            continue;

                        BitmapRegion region = atlas.getSubRegion(0, idx * 16, 16, 16);
                        tile.sprite = renderer.createStaticSprite(region);
                    } else {
                        BitmapRegion[] frames = new BitmapRegion[tile.frameIndices.length];
                        for (int i = 0; i < frames.length; i++) {
                            frames[i] = atlas.getSubRegion(0, (tile.frameIndices[i] - 1) * 16, 16, 16);
                        }
                        tile.sprite = renderer.createAnimatedSprite(10, frames);
                    }
                }
            }
        }

        private final int[] frameIndices;
        private TileSprite sprite;

        Tile(int... frameIndices) {
            this.frameIndices = frameIndices;
        }

        @Override
        public TileSprite getSprite(Tilemap<Tile> tilemap, int tileX, int tileY) {
            return sprite;
        }
    }

    private final Renderer2D renderer2D;
    private final Camera camera;

    private final Tilemap<Tile> tilemap;
    private final TilemapRenderer tilemapRenderer;

    public TilemapTest() throws IOException {
        super(new WindowSettings(800, 600, "Tilemap Test"));
        renderer2D = new Renderer2D(getRenderer());

        camera = new Camera(new ScaledOrthoProjection(ScaledOrthoProjection.ScaleMode.FIT,
                10, 10, 5, -5));
        camera.getTransform().position.set(5, 5, 0);

        tilemap = new InfiniteTilemap<>(3, 3);
        Tile[] tiles = Tile.values();
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                int idx = (int) (Math.random() * tiles.length);
                tilemap.setTile(x, y, tiles[idx]);
            }
        }

        tilemapRenderer = new TilemapRenderer(getRenderer());
        tilemapRenderer.setTilemap(tilemap);
        Tile.init(tilemapRenderer);

        setBackgroundColor(Colors.RGBA.BLACK);
    }

    @Override
    protected void tick(float dt) {
        tilemapRenderer.tickAnimations(dt);
    }

    @Override
    protected void render(Renderer renderer) {
        Vector2i fbSize = getWindow().getFramebufferSize();
        Matrix4f proj = camera.getProjectionMatrix(fbSize.x, fbSize.y);
        Matrix4f view = camera.getViewMatrix();

        tilemapRenderer.renderTilemap(proj, view, renderer2D);
    }

    @Override
    protected void cleanUp() {
        renderer2D.close();
    }

    public static void main(String[] args) throws IOException {
        new TilemapTest().run();
    }
}
