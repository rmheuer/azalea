package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.BufferType;
import com.github.rmheuer.azalea.render.Colors;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.pipeline.ActivePipeline;
import com.github.rmheuer.azalea.render.pipeline.CullMode;
import com.github.rmheuer.azalea.render.pipeline.FaceWinding;
import com.github.rmheuer.azalea.render.pipeline.FillMode;
import com.github.rmheuer.azalea.render.pipeline.PipelineInfo;
import com.github.rmheuer.azalea.render.shader.ShaderProgram;
import com.github.rmheuer.azalea.render.shader.ShaderStage;
import com.github.rmheuer.azalea.render.shader.ShaderUniform;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.render.texture.Texture2D;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLRenderer implements Renderer {
    private boolean pipelineActive = false;

    @Override
    public void setViewportRect(int x, int y, int width, int height) {
        glViewport(x, y, width, height);
    }

    @Override
    public void setClearColor(int colorRGBA) {
        glClearColor(
                Colors.RGBA.getRed(colorRGBA) / 255.0f,
                Colors.RGBA.getGreen(colorRGBA) / 255.0f,
                Colors.RGBA.getBlue(colorRGBA) / 255.0f,
                Colors.RGBA.getAlpha(colorRGBA) / 255.0f
        );
    }

    @Override
    public void clear(BufferType... buffers) {
        if (buffers.length == 0)
            return;

        int bits = 0;
        for (BufferType type : buffers) {
            switch (type) {
                case COLOR: bits |= GL_COLOR_BUFFER_BIT; break;
                case DEPTH: bits |= GL_DEPTH_BUFFER_BIT; break;
                case STENCIL: bits |= GL_STENCIL_BUFFER_BIT; break;
            }
        }

        glClear(bits);
    }

    private void setEnabled(int feature, boolean enabled) {
        if (enabled)
            glEnable(feature);
        else
            glDisable(feature);
    }

    @Override
    public ActivePipeline bindPipeline(PipelineInfo pipeline) {
        if (pipelineActive)
            throw new IllegalStateException("Another pipeline is already active");
        pipelineActive = true;

        OpenGLShaderProgram shader = (OpenGLShaderProgram) pipeline.getShader();
        shader.bind();

        setEnabled(GL_BLEND, pipeline.isBlend());
        if (pipeline.isBlend())
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        boolean depthTest = pipeline.isDepthTest();
        if (depthTest) {
            glEnable(GL_DEPTH_TEST);

            int func;
            switch (pipeline.getDepthFunc()) {
                case NEVER: func = GL_NEVER; break;
                case LESS: func = GL_LESS; break;
                case EQUAL: func = GL_EQUAL; break;
                case LESS_OR_EQUAL: func = GL_LEQUAL; break;
                case GREATER: func = GL_GREATER; break;
                case NOT_EQUAL: func = GL_NOTEQUAL; break;
                case GREATER_OR_EQUAL: func = GL_GEQUAL; break;
                case ALWAYS: func = GL_ALWAYS; break;
                default: throw new IllegalArgumentException("Unknown depth function: " + pipeline.getDepthFunc());
            }
            glDepthFunc(func);
        } else {
            glDisable(GL_DEPTH_TEST);
        }

        if (pipeline.getCullMode() == CullMode.OFF) {
            glDisable(GL_CULL_FACE);
        } else {
            glEnable(GL_CULL_FACE);
            glCullFace(pipeline.getCullMode() == CullMode.FRONT ? GL_FRONT : GL_BACK);
            glFrontFace(pipeline.getWinding() == FaceWinding.CW_FRONT ? GL_CW : GL_CCW);
        }

        glPolygonMode(GL_FRONT_AND_BACK, pipeline.getFillMode() == FillMode.FILLED ? GL_FILL : GL_LINE);

        return new ActivePipelineImpl(shader);
    }

    @Override
    public ShaderStage createShaderStage(ShaderStage.Type type, String glsl) {
        return new OpenGLShaderStage(type, glsl);
    }

    @Override
    public ShaderProgram createShaderProgram(ShaderStage... stages) {
        return new OpenGLShaderProgram(stages);
    }

    @Override
    public Mesh createMesh() {
        return new OpenGLMesh();
    }

    @Override
    public Texture2D createTexture2D() {
        return new OpenGLTexture2D();
    }

    private final class ActivePipelineImpl implements ActivePipeline {
        private final OpenGLShaderProgram shader;

        public ActivePipelineImpl(OpenGLShaderProgram shader) {
            this.shader = shader;
        }

        @Override
        public void bindTexture(int slot, Texture texture) {
            ((OpenGLTexture) texture).bind(slot);
        }

        @Override
        public ShaderUniform getUniform(String name) {
            return shader.getUniform(name);
        }

        @Override
        public void draw(Mesh mesh) {
            ((OpenGLMesh) mesh).render();
        }

        @Override
        public void close() {
            pipelineActive = false;
        }
    }
}
