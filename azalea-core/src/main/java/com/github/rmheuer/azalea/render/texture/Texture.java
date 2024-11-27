package com.github.rmheuer.azalea.render.texture;

import com.github.rmheuer.azalea.utils.SafeCloseable;

/**
 * A set of image data stored on the GPU.
 */
public interface Texture extends SafeCloseable {
    /** How points between pixels should be mapped to texture pixels. */
    enum Filter {
        /** The nearest texture pixel is selected. */
        NEAREST,
        /** The texture pixels around the point are bilinearly interpolated. */
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
