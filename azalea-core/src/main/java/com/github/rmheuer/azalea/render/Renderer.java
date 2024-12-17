package com.github.rmheuer.azalea.render;

import com.github.rmheuer.azalea.io.IOUtil;
import com.github.rmheuer.azalea.render.framebuffer.Framebuffer;
import com.github.rmheuer.azalea.render.framebuffer.FramebufferBuilder;
import com.github.rmheuer.azalea.render.mesh.IndexBuffer;
import com.github.rmheuer.azalea.render.mesh.Mesh;
import com.github.rmheuer.azalea.render.mesh.VertexBuffer;
import com.github.rmheuer.azalea.render.pipeline.ActivePipeline;
import com.github.rmheuer.azalea.render.pipeline.PipelineInfo;
import com.github.rmheuer.azalea.render.shader.ShaderProgram;
import com.github.rmheuer.azalea.render.shader.ShaderStage;
import com.github.rmheuer.azalea.render.texture.Bitmap;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.render.texture.TextureCubeMap;
import com.github.rmheuer.azalea.utils.SafeCloseable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a renderer used to render into a window.
 */
public interface Renderer extends SafeCloseable {
    int MAX_TEXTURE_SLOTS = 16;

    void setClipRect(int x, int y, int w, int h);

    /**
     * Sets the color used to fill the window when clearing the color buffer.
     *
     * @param colorRGBA new clear color
     */
    void setClearColor(int colorRGBA);

    /**
     * Clears the contents of the specified buffers.
     *
     * @param buffers buffers to clear
     */
    void clear(BufferType... buffers);

    /**
     * Binds a pipeline for use.
     *
     * @param pipeline pipeline configuration
     * @param framebuffer framebuffer to render into
     * @return access to draw commands
     * @throws IllegalStateException if another pipeline is currently bound
     */
    ActivePipeline bindPipeline(PipelineInfo pipeline, Framebuffer framebuffer);

    /**
     * Binds a pipeline for use, rendering to the default framebuffer.
     *
     * @param pipeline pipeline configuration
     * @return access to draw commands
     * @throws IllegalStateException if another pipeline is currently bound
     */
    default ActivePipeline bindPipeline(PipelineInfo pipeline) {
        return bindPipeline(pipeline, getDefaultFramebuffer());
    }

    /**
     * Creates and compiles a shader stage from GLSL source.
     *
     * @param type type of stage
     * @param glsl GLSL source code to compile
     * @return the stage
     */
    ShaderStage createShaderStage(ShaderStage.Type type, String glsl);

    /**
     * Creates a shader program from a set of shader stages. Typically there
     * will be at least a vertex stage and a fragment stage.
     *
     * @param stages stages to construct the program
     * @return the program
     */
    ShaderProgram createShaderProgram(ShaderStage... stages);

    /**
     * Creates a shader stage, reading the source from an {@code InputStream}
     * in UTF-8 encoding.
     *
     * @param type type of stage
     * @param in input stream to read source from
     * @return the stage
     * @throws IOException if an IO error occurs while reading the source
     */
    default ShaderStage createShaderStage(ShaderStage.Type type, InputStream in) throws IOException {
        return createShaderStage(type, IOUtil.readToString(in));
    }

    /**
     * Creates a shader program with a vertex stage and a fragment stage.
     *
     * @param vertexSrc GLSL source of the vertex stage
     * @param fragmentSrc GLSL source of the fragment stage
     * @return the program
     */
    default ShaderProgram createShaderProgram(String vertexSrc, String fragmentSrc) {
        try (ShaderStage vertexStage = createShaderStage(ShaderStage.Type.VERTEX, vertexSrc);
             ShaderStage fragmentStage = createShaderStage(ShaderStage.Type.FRAGMENT, fragmentSrc)) {
            return createShaderProgram(vertexStage, fragmentStage);
        }
    }

    /**
     * Creates a shader program with a vertex stage and a fragment stage from
     * {@code InputStream}s.
     *
     * @param vertexSrc input stream to read source of the vertex stage
     * @param fragmentSrc input stream to read source of the fragment stage
     * @return the program
     * @throws IOException if an IO error occurs while reading the sources
     */
    default ShaderProgram createShaderProgram(InputStream vertexSrc, InputStream fragmentSrc) throws IOException {
        try (ShaderStage vertexStage = createShaderStage(ShaderStage.Type.VERTEX, vertexSrc);
             ShaderStage fragmentStage = createShaderStage(ShaderStage.Type.FRAGMENT, fragmentSrc)) {
            return createShaderProgram(vertexStage, fragmentStage);
        }
    }

    /**
     * Creates an empty {@code VertexBuffer}. You will need to upload data to
     * the buffer before drawing it.
     *
     * @return the created vertex buffer
     */
    VertexBuffer createVertexBuffer();

    /**
     * Creates an empty {@code IndexBuffer}. You will need to upload data to
     * the buffer before drawing it.
     *
     * @return the created index buffer
     */
    IndexBuffer createIndexBuffer();

    /**
     * Creates an empty {@code Mesh}. You will need to upload data to the mesh
     * before drawing it.
     *
     * @return the created mesh
     */
    default Mesh createMesh() {
        return new Mesh(createVertexBuffer(), createIndexBuffer());
    }

    /**
     * Creates an empty {@code Texture2D}. You will need to upload data to the
     * texture before rendering it.
     *
     * @return the created texture
     */
    Texture2D createTexture2D();

    /**
     * Creates a {@code Texture2D} and loads its data from an
     * {@code InputStream}. The stream is decoded using
     * {@link Bitmap#decode(InputStream)}.
     *
     * @param in input stream to read image data from
     * @return the created texture
     * @throws IOException if an IO error occurs while decoding the image data
     */
    default Texture2D createTexture2D(InputStream in) throws IOException {
        try (Bitmap bitmap = Bitmap.decode(in)) {
            Texture2D tex = createTexture2D();
            tex.setData(bitmap);
            return tex;
        }
    }

    /**
     * Create an empty {@code TextureCubeMap}. You will need to upload data to
     * each face of the texture before rendering it.
     *
     * @return the created texture
     */
    TextureCubeMap createTextureCubeMap();

    /**
     * Gets the framebuffer used to render into the window.
     *
     * @return default window framebuffer
     */
    Framebuffer getDefaultFramebuffer();

    /**
     * Creates a {@code FramebufferBuilder} to build a framebuffer.
     *
     * @return the created framebuffer builder
     */
    FramebufferBuilder createFramebufferBuilder(int width, int height);
}
