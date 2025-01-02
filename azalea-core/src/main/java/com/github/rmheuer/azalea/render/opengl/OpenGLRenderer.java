package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.*;
import com.github.rmheuer.azalea.render.framebuffer.Framebuffer;
import com.github.rmheuer.azalea.render.framebuffer.FramebufferBuilder;
import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.PrimitiveType;
import com.github.rmheuer.azalea.render.mesh.VertexBuffer;
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
import com.github.rmheuer.azalea.render.texture.TextureCubeMap;
import com.github.rmheuer.azalea.utils.SizeOf;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL33C.*;

public final class OpenGLRenderer implements Renderer {
    private final GLStateManager state;
    private boolean pipelineActive = false;

    private final Framebuffer defaultFramebuffer;

    public OpenGLRenderer(OpenGLWindow window) {
        Vector2i size = window.getFramebufferSize();
        state = new GLStateManager(size);
        setClipRect(0, 0, size.x, size.y);

        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        defaultFramebuffer = new Framebuffer() {
            @Override
            public Vector2i getSize() {
                return window.getFramebufferSize();
            }

            @Override
            public Texture2D getColorTexture(int index) {
                return null;
            }

            @Override
            public void close() {
                throw new UnsupportedOperationException("Cannot close default framebuffer");
            }
        };
    }

    @Override
    public void setClipRect(int x, int y, int w, int h) {
        state.setClipRect(x, y, w, h);
    }

    @Override
    public void setClearColor(int colorRGBA) {
        state.setClearColor(
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

    @Override
    public ActivePipeline bindPipeline(PipelineInfo pipeline, Framebuffer framebuffer) {
        if (pipelineActive)
            throw new IllegalStateException("Another pipeline is already active");
        pipelineActive = true;

        if (framebuffer == defaultFramebuffer) {
            state.bindFramebuffer(0);
        } else {
            ((OpenGLFramebuffer) framebuffer).bind();
        }
        Vector2i size = framebuffer.getSize();
        glViewport(0, 0, size.x, size.y);

        OpenGLShaderProgram shader = (OpenGLShaderProgram) pipeline.getShader();
        shader.bind();

        boolean blend = pipeline.isBlend();
        state.setBlendEnabled(blend);
        if (blend)
            state.setBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        state.setScissorEnabled(pipeline.isClip());

        if (pipeline.isDepthTest()) {
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
            state.setDepthTestEnabled(true);
            state.setDepthFunc(func);
        } else {
            state.setDepthTestEnabled(false);
        }

        if (pipeline.getCullMode() == CullMode.OFF) {
            state.setCullFaceEnabled(false);
        } else {
            state.setCullFaceEnabled(true);
            state.setCullFace(pipeline.getCullMode() == CullMode.FRONT ? GL_FRONT : GL_BACK);
            state.setFrontFace(pipeline.getWinding() == FaceWinding.CW_FRONT ? GL_CW : GL_CCW);
        }

        state.setPolygonMode(pipeline.getFillMode() == FillMode.FILLED ? GL_FILL : GL_LINE);

        return new ActivePipelineImpl(shader);
    }

    @Override
    public ShaderStage createShaderStage(ShaderStage.Type type, String glsl) {
        return new OpenGLShaderStage(type, glsl);
    }

    @Override
    public ShaderProgram createShaderProgram(ShaderStage... stages) {
        return new OpenGLShaderProgram(state, stages);
    }

    @Override
    public VertexBuffer createVertexBuffer() {
        return new OpenGLVertexBuffer(state);
    }

    @Override
    public IndexBuffer createIndexBuffer() {
        return new OpenGLIndexBuffer(state);
    }

    @Override
    public Texture2D createTexture2D() {
        return new OpenGLTexture2D(state);
    }

    @Override
    public TextureCubeMap createTextureCubeMap() {
        return new OpenGLTextureCubeMap(state);
    }

    @Override
    public Framebuffer getDefaultFramebuffer() {
        return defaultFramebuffer;
    }

    @Override
    public FramebufferBuilder createFramebufferBuilder(int width, int height) {
        return new OpenGLFramebufferBuilder(state, width, height);
    }
    
    public static int getGlPrimitiveType(PrimitiveType primitiveType) {
        switch (primitiveType) {
            case POINTS: return GL_POINTS; 
            case LINE_STRIP: return GL_LINE_STRIP; 
            case LINE_LOOP: return GL_LINE_LOOP; 
            case LINES: return GL_LINES; 
            case TRIANGLE_STRIP: return GL_TRIANGLE_STRIP; 
            case TRIANGLE_FAN: return GL_TRIANGLE_FAN; 
            case TRIANGLES: return GL_TRIANGLES; 
            default:
                throw new IllegalArgumentException("Unknown primitive type: " + primitiveType);
        }
    }

    @Override
    public void close() {
        state.close();
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
        public void draw(VertexBuffer vertices, PrimitiveType primType, int startIdx, int count) {
            if (count == 0)
                return;

            OpenGLVertexBuffer vertexBuf = (OpenGLVertexBuffer) vertices;
            int vertexCount = vertexBuf.getVertexCount();
            if (startIdx >= vertexCount)
                throw new IndexOutOfBoundsException("Start index out of bounds: " + startIdx + " >= " + vertexCount);
            if (startIdx + count > vertexCount)
                throw new IndexOutOfBoundsException("Buffer overflow: " + (startIdx + count) + " > " + vertexCount);

            state.getVertexArrayManager().bindForDrawing(vertexBuf.getId(), 0, vertexBuf.getDataLayout());
            glDrawArrays(getGlPrimitiveType(primType), startIdx, count);
        }

        @Override
        public void draw(VertexBuffer vertices, IndexBuffer indices, int startIdx, int count, int indexOffset) {
            if (count == 0)
                return;

            OpenGLVertexBuffer vertexBuf = (OpenGLVertexBuffer) vertices;
            OpenGLIndexBuffer indexBuf = (OpenGLIndexBuffer) indices;

            int indexCount = indexBuf.getIndexCount();
            if (startIdx >= indexCount)
                throw new IndexOutOfBoundsException("Start index out of bounds: " + startIdx + " >= " + indexCount);
            if (startIdx + count > indexCount)
                throw new IndexOutOfBoundsException("Buffer overflow: " + (startIdx + count) + " > " + indexCount);

            int format = indexBuf.getGlFormat();
            state.getVertexArrayManager().bindForDrawing(
                    vertexBuf.getId(),
                    indexBuf.getId(),
                    vertexBuf.getDataLayout()
            );
            glDrawElementsBaseVertex(
                    indexBuf.getGlPrimType(),
                    count,
                    indexBuf.getGlFormat(),
                    (long) startIdx * (format == GL_UNSIGNED_INT ? SizeOf.INT : SizeOf.SHORT),
                    indexOffset
            );
        }

        @Override
        public void close() {
            pipelineActive = false;
        }
    }
}
