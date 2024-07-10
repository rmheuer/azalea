package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.shader.ShaderProgram;
import com.github.rmheuer.azalea.render.shader.ShaderStage;
import com.github.rmheuer.azalea.render.shader.ShaderUniform;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLShaderProgram implements ShaderProgram {
    private final int id;
    private final Map<String, ShaderUniform> uniforms;

    public OpenGLShaderProgram(ShaderStage[] stages) {
        id = glCreateProgram();
        for (ShaderStage stage : stages) {
            glAttachShader(id, ((OpenGLShaderStage) stage).getId());
        }
        glLinkProgram(id);

        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program linking failed:");
            System.err.println(glGetProgramInfoLog(id));
            throw new RuntimeException("Shader program linking failed");
        }

        uniforms = new HashMap<>();
    }

    public void bind() {
        glUseProgram(id);
    }

    public ShaderUniform getUniform(String name) {
        return uniforms.computeIfAbsent(name, (n) -> {
            int loc = glGetUniformLocation(id, n);
            return new UniformImpl(loc);
        });
    }

    @Override
    public void close() {
        glDeleteProgram(id);
    }

    private static final class UniformImpl implements ShaderUniform {
        private final int location;

        public UniformImpl(int location) {
            this.location = location;
        }

        @Override
        public void setFloat(float f) {
            glUniform1f(location, f);
        }

        @Override
        public void setVec2(Vector2fc v) {
            glUniform2f(location, v.x(), v.y());
        }

        @Override
        public void setVec3(Vector3fc v) {
            glUniform3f(location, v.x(), v.y(), v.z());
        }

        @Override
        public void setVec4(Vector4fc v) {
            glUniform4f(location, v.x(), v.y(), v.z(), v.w());
        }

        @Override
        public void setMat4(Matrix4fc m) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer buf = stack.mallocFloat(16);
                m.get(buf);
                glUniformMatrix4fv(location, false, buf);
            }
        }

        @Override
        public void setInt(int i) {
            glUniform1i(location, i);
        }

        @Override
        public void setTexture(int slotIdx) {
            glUniform1i(location, slotIdx);
        }
    }
}
