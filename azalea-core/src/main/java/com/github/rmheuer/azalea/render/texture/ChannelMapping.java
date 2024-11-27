package com.github.rmheuer.azalea.render.texture;

/**
 * Describes how the channels of bitmap data should be mapped to the channels
 * of the texture data on the GPU.
 */
public final class ChannelMapping {
    public enum Source {
        /** Red channel or grayscale value of the bitmap data. */
        RED,
        /** Green channel of the bitmap data. Zero for grayscale images. */
        GREEN,
        /** Blue channel of the bitmap data. Zero for grayscale images. */
        BLUE,
        /** Alpha channel of the bitmap data. One for grayscale images. */
        ALPHA,

        /** Constant value of one (fully saturated). */
        ONE,
        /** Constant value of zero (no saturation). */
        ZERO;

        /** Alias for red channel */
        public static final Source GRAY = RED;
    }

    // Commonly used mappings
    /** R = srcR, G = srcG, B = srcB, A = srcA */
    public static final ChannelMapping DIRECT_RGBA =
            new ChannelMapping(Source.RED, Source.GREEN, Source.BLUE, Source.ALPHA);
    /** R = srcR, G = srcR, B = srcR, A = 1 */
    public static final ChannelMapping GRAYSCALE_OPAQUE =
            new ChannelMapping(Source.GRAY, Source.GRAY, Source.GRAY, Source.ONE);
    /** R = 1, G = 1, B = 1, A = srcR */
    public static final ChannelMapping WHITE_TRANSPARENCY =
            new ChannelMapping(Source.ONE, Source.ONE, Source.ONE, Source.GRAY);
    /** R = srcR, G = srcR, B = srcR, A = srcR */
    public static final ChannelMapping GRAYSCALE_TRANSPARENCY =
            new ChannelMapping(Source.GRAY, Source.GRAY, Source.GRAY, Source.GRAY);

    private final Source red, green, blue, alpha;

    /**
     * @param red source for the red component of the texture
     * @param green source for the green component of the texture
     * @param blue source for the blue component of the texture
     * @param alpha source for the alpha component of the texture
     */
    public ChannelMapping(Source red, Source green, Source blue, Source alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Source getRed() {
        return red;
    }

    public Source getGreen() {
        return green;
    }

    public Source getBlue() {
        return blue;
    }

    public Source getAlpha() {
        return alpha;
    }
}
