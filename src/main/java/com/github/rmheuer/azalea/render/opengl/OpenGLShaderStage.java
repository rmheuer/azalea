package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.shader.ShaderStage;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLShaderStage implements ShaderStage {
    private final Type type;
    private final int id;

    public OpenGLShaderStage(Type type, String glsl) {
        this.type = type;

        int glType;
        switch (type) {
            case VERTEX: glType = GL_VERTEX_SHADER; break;
            case FRAGMENT: glType = GL_FRAGMENT_SHADER; break;
            default: throw new IllegalArgumentException(String.valueOf(type));
        }

        id = glCreateShader(glType);
        glShaderSource(id, glsl);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader compilation failed:");
            System.err.println(glGetShaderInfoLog(id));
            throw new RuntimeException("Shader compilation failed");
        }
    }

    int getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void close() {
        glDeleteShader(id);
    }
}
