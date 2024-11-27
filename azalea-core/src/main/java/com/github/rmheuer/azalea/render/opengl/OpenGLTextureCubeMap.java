package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.math.CubeFace;
import com.github.rmheuer.azalea.render.texture.BitmapRegion;
import com.github.rmheuer.azalea.render.texture.ChannelMapping;
import com.github.rmheuer.azalea.render.texture.ColorFormat;
import com.github.rmheuer.azalea.render.texture.TextureCubeMap;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLTextureCubeMap extends OpenGLTexture implements TextureCubeMap {
    public OpenGLTextureCubeMap() {
        setFilters(Filter.LINEAR);
    }

    @Override
    protected void bindToTarget() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
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
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        setData(getGlFace(face), null, width, height, colorFormat);
    }

    @Override
    public void setFaceData(CubeFace face, BitmapRegion data) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        setData(getGlFace(face), data);
    }

    @Override
    public void setFaceData(CubeFace face, ByteBuffer data, int width, int height, ColorFormat colorFormat) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        setData(getGlFace(face), data, width, height, colorFormat);
    }

    @Override
    public void setFaceSubData(CubeFace face, BitmapRegion data, int x, int y) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        setSubData(getGlFace(face), data, x, y);
    }

    @Override
    public void setFaceSubData(CubeFace face, ByteBuffer data, int width, int height, ColorFormat colorFormat, int x, int y) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        setSubData(getGlFace(face), data, width, height, colorFormat, x, y);
    }

    @Override
    public void setMinFilter(Filter minFilter) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, getGlFilter(minFilter));
    }

    @Override
    public void setMagFilter(Filter magFilter) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, getGlFilter(magFilter));
    }

    @Override
    public void setChannelMapping(ChannelMapping mapping) {
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        setChannelMapping(GL_TEXTURE_CUBE_MAP, mapping);
    }
}
