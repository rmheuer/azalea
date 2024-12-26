package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.math.CubeFace;
import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ChannelMapping;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.TextureCubeMap;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLTextureCubeMap extends OpenGLTexture implements TextureCubeMap {
    public OpenGLTextureCubeMap(GLStateManager state) {
        super(state);
        setFilters(Filter.LINEAR);
    }

    @Override
    protected void bindToTarget() {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
    }

    private int getGlFace(CubeFace face) {
        switch (face) {
            case POS_X: return GL_TEXTURE_CUBE_MAP_POSITIVE_X;
            case NEG_X: return GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
            case POS_Y: return GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
            case NEG_Y: return GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
            case POS_Z: return GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
            case NEG_Z: return GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
            default:
                throw new IllegalArgumentException(String.valueOf(face));
        }
    }

    @Override
    public void setFaceSize(CubeFace face, int width, int height, ColorFormat colorFormat) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapData(getGlFace(face), 0, null, width, height, colorFormat);
    }

    @Override
    public void setFaceData(CubeFace face, BitmapRegion data) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapData(getGlFace(face), 0, data);
    }

    @Override
    public void setFaceData(CubeFace face, ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapData(getGlFace(face), 0, data, width, height, colorFormat);
    }

    @Override
    public void setFaceSubData(CubeFace face, BitmapRegion data, int x, int y) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapSubData(getGlFace(face), 0, data, x, y);
    }

    @Override
    public void setFaceSubData(CubeFace face, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapSubData(getGlFace(face), 0, data, width, height, colorFormat, x, y);
    }

    @Override
    public void setMinFilter(Filter minFilter) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMinFilter(GL_TEXTURE_CUBE_MAP, minFilter);
    }

    @Override
    public void setMagFilter(Filter magFilter) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, getGlFilter(magFilter));
    }

    @Override
    public void setMipMapMode(MipMapMode mode) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapMode(GL_TEXTURE_CUBE_MAP, mode);
    }

    @Override
    public void setMipMapRange(int minLevel, int maxLevel) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setMipMapRange(GL_TEXTURE_CUBE_MAP, minLevel, maxLevel);
    }

    @Override
    public void setChannelMapping(ChannelMapping mapping) {
        state.bindTexture(GL_TEXTURE_CUBE_MAP, id);
        setChannelMapping(GL_TEXTURE_CUBE_MAP, mapping);
    }
}
