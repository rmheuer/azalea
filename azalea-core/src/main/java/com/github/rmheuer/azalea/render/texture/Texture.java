package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.utils.SafeCloseable;

/**
 * A set of image data stored on the GPU.
 */
// TODO: Checks for mip-map completeness
public interface Texture extends SafeCloseable {
    /** How points between pixels should be mapped to texture pixels. */
    enum Filter {
        /** The nearest texture pixel is selected. */
        NEAREST,
        /** The texture pixels around the point are bilinearly interpolated. */
        LINEAR
    }

    enum MipMapMode {
        /** The full-size texture should be used always. */
        DISABLED,
        /** The mip-map level closest to the target size should be used. */
        NEAREST,
        /** The two closest mip-map levels will be used and blended together. */
        LINEAR
    }

    /**
     * Sets the minification filter. This is used when the texture is rendered
     * smaller than its actual size.
     *
     * @param minFilter filter to use
     */
    void setMinFilter(Filter minFilter);

    /**
     * Sets the magnification filter. This is used when the texture is rendered
     * larger than its actual size.
     *
     * @param magFilter filter to use
     */
    void setMagFilter(Filter magFilter);

    /**
     * Sets how mip-maps should be used.
     *
     * @param mode mip-map mode
     */
    void setMipMapMode(MipMapMode mode);

    void setMipMapRange(int minLevel, int maxLevel);

    /**
     * Sets both the minification and magnification filters.
     *
     * @param filter filter to use
     */
    default void setFilters(Filter filter) {
        setMinFilter(filter);
        setMagFilter(filter);
    }

    /**
     * Sets the mapping from channels in the texture data to the channels that
     * are rendered. If this is not called, {@link ChannelMapping#DIRECT_RGBA}
     * will be used.
     *
     * @param mapping mapping from bitmap channels to texture channels
     */
    void setChannelMapping(ChannelMapping mapping);
}
