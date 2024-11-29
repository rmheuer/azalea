package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.mesh.DataUsage;
import com.github.rmheuer.azalea.utils.SafeCloseable;

import static org.lwjgl.opengl.GL33C.*;

public abstract class OpenGLBuffer implements SafeCloseable {
    protected final int id;

    public OpenGLBuffer() {
        id = glGenBuffers();
    }

    protected int getGlUsage(DataUsage usage) {
        switch (usage) {
            case STATIC: return GL_STATIC_DRAW;
            case DYNAMIC: return GL_DYNAMIC_DRAW;
            case STREAM: return GL_STREAM_DRAW;
            default:
                throw new IllegalArgumentException("Unknown data usage: " + usage);
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public void close() {
        glDeleteBuffers(id);
    }
}
