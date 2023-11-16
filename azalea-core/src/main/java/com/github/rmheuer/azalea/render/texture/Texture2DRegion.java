package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.math.MathUtil;
import org.joml.Vector2f;

public interface Texture2DRegion {
    Texture2D getSourceTexture();
    Vector2f getRegionMinUV();
    Vector2f getRegionMaxUV();

    default Texture2DRegion getSubRegion(float minX, float minY, float maxX, float maxY) {
        Vector2f min = getRegionMinUV();
        Vector2f max = getRegionMaxUV();

        return new SubTexture2D(
                getSourceTexture(),
                new Vector2f(MathUtil.lerp(min.x, max.x, minX), MathUtil.lerp(min.y, max.y, minY)),
                new Vector2f(MathUtil.lerp(min.x, max.x, maxX), MathUtil.lerp(min.y, max.y, maxY))
        );
    }

    /**
     * Gets a view of the region reflected over the X axis
     * @return flipped region
     */
    default Texture2DRegion getFlippedX() {
        Vector2f min = getRegionMinUV();
        Vector2f max = getRegionMaxUV();
        return new SubTexture2D(
                getSourceTexture(),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y)
        );
    }

    /**
     * Gets a view of the region reflected over the Y axis
     * @return flipped region
     */
    default Texture2DRegion getFlippedY() {
        Vector2f min = getRegionMinUV();
        Vector2f max = getRegionMaxUV();
        return new SubTexture2D(
                getSourceTexture(),
                new Vector2f(max.x, min.y),
                new Vector2f(min.x, max.y)
        );
    }
}
