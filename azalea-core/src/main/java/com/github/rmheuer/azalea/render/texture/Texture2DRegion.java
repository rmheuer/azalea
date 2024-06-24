package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.math.MathUtil;
import org.joml.Vector2f;

/**
 * Represents a region of a {@code Texture2D}.
 */
public interface Texture2DRegion {
    /**
     * Gets the texture this region is a part of.
     *
     * @return the source texture
     */
    Texture2D getSourceTexture();

    /**
     * Gets the top-left UV coordinates of the region in the source texture.
     *
     * @return top-left UV
     */
    Vector2f getRegionTopLeftUV();

    /**
     * Gets the bottom-right UV coordinates of the region in the source texture.
     *
     * @return bottom-right UV
     */
    Vector2f getRegionBottomRightUV();

    /**
     * Gets a sub-region of this texture. The positions specified here are
     * fractions from 0 to 1 of this region.
     *
     * @param leftX left x position fraction
     * @param topY top y position fraction
     * @param rightX right x position fraction
     * @param bottomY bottom y position fraction
     * @return the sub-region
     */
    default Texture2DRegion getSubRegion(float leftX, float topY, float rightX, float bottomY) {
        Vector2f min = getRegionTopLeftUV();
        Vector2f max = getRegionBottomRightUV();

        return new SubTexture2D(
                getSourceTexture(),
                new Vector2f(MathUtil.lerp(min.x, max.x, leftX), MathUtil.lerp(min.y, max.y, topY)),
                new Vector2f(MathUtil.lerp(min.x, max.x, rightX), MathUtil.lerp(min.y, max.y, bottomY))
        );
    }

    /**
     * Gets a view of the region reflected over the X axis
     * @return flipped region
     */
    default Texture2DRegion getFlippedX() {
        Vector2f min = getRegionTopLeftUV();
        Vector2f max = getRegionBottomRightUV();
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
        Vector2f min = getRegionTopLeftUV();
        Vector2f max = getRegionBottomRightUV();
        return new SubTexture2D(
                getSourceTexture(),
                new Vector2f(max.x, min.y),
                new Vector2f(min.x, max.y)
        );
    }
}
