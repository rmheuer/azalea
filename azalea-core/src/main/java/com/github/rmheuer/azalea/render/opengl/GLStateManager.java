package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2ic;
import org.joml.Vector4f;
import org.joml.Vector4i;

import static org.lwjgl.opengl.GL33C.*;

public final class GLStateManager implements SafeCloseable {
    private final VertexArrayManager vertexArrayManager;

    // glEnable() and glDisable() state
    private boolean enabledScissor = false;
    private boolean enabledBlend = false;
    private boolean enabledDepthTest = false;
    private boolean enabledCullFace = false;

    // bindXXX() bindings
    private int boundFramebuffer = 0;
    private int boundProgram = 0;
    private int boundArrayBuffer = 0;

    // Technically the OpenGL spec allows binding multiple textures to the same
    // texture unit if they use different targets, but I don't trust all OpenGL
    // drivers to implement that edge case properly since that's not how GPU
    // hardware actually works, so only keep track of the most recent binding
    // to each target.
    private final int[] boundTextureTargets = new int[Renderer.MAX_TEXTURE_SLOTS];
    private final int[] boundTextures = new int[Renderer.MAX_TEXTURE_SLOTS];

    // Other pipeline state
    private final Vector4f clearColor = new Vector4f(0, 0, 0, 0);
    private final Vector4i viewport;
    private final Vector4i clipRect;
    private int blendEquationRGB = GL_FUNC_ADD;
    private int blendEquationAlpha = GL_FUNC_ADD;
    private int blendFuncSrcRGBFactor = GL_ONE;
    private int blendFuncDstRGBFactor = GL_ZERO;
    private int blendFuncSrcAlphaFactor = GL_ONE;
    private int blendFuncDstAlphaFactor = GL_ZERO;
    private int depthFunc = GL_LESS;
    private int cullFace = GL_BACK;
    private int frontFace = GL_CCW;
    private int polygonMode = GL_FILL;
    private int activeTexture = 0;
    private int pixelUnpackAlignment = 4;

    public GLStateManager(Vector2ic fbSize) {
        vertexArrayManager = new VertexArrayManager(this);

        viewport = new Vector4i(0, 0, fbSize.x(), fbSize.y());
        clipRect = new Vector4i(0, 0, fbSize.x(), fbSize.y());
    }

    private void setEnabled(int feature, boolean enabled) {
        if (enabled)
            glEnable(feature);
        else
            glDisable(feature);
    }

    public void setScissorEnabled(boolean enabled) {
        if (enabledScissor != enabled) {
            setEnabled(GL_SCISSOR_TEST, enabled);
            enabledScissor = enabled;
        }
    }

    public void setBlendEnabled(boolean enabled) {
        if (enabledBlend != enabled) {
            setEnabled(GL_BLEND, enabled);
            enabledBlend = enabled;
        }
    }

    public void setDepthTestEnabled(boolean enabled) {
        if (enabledDepthTest != enabled) {
            setEnabled(GL_DEPTH_TEST, enabled);
            enabledDepthTest = enabled;
        }
    }

    public void setCullFaceEnabled(boolean enabled) {
        if (enabledCullFace != enabled) {
            setEnabled(GL_CULL_FACE, enabled);
            enabledCullFace = enabled;
        }
    }

    public void bindFramebuffer(int fbo) {
        if (boundFramebuffer != fbo) {
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            boundFramebuffer = fbo;
        }
    }

    public void bindProgram(int program) {
        if (boundProgram != program) {
            glUseProgram(program);
            boundProgram = program;
        }
    }

    public void bindArrayBuffer(int vbo) {
        if (boundArrayBuffer != vbo) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            boundArrayBuffer = vbo;
        }
    }

    public void setActiveTexture(int index) {
        if (activeTexture != index) {
            glActiveTexture(GL_TEXTURE0 + index);
            activeTexture = index;
        }
    }

    public void bindTexture(int target, int texture) {
        int currentTarget = boundTextureTargets[activeTexture];
        int currentTexture = boundTextures[activeTexture];

        if (currentTarget != target || currentTexture != texture) {
            glBindTexture(target, texture);
            boundTextureTargets[activeTexture] = target;
            boundTextures[activeTexture] = texture;
        }
    }

    public void setClearColor(float r, float g, float b, float a) {
        if (!clearColor.equals(r, g, b, a)) {
            glClearColor(r, g, b, a);
            clearColor.set(r, g, b, a);
        }
    }

    public void setViewport(int x, int y, int w, int h) {
        if (!viewport.equals(x, y, w, h)) {
            glViewport(x, y, w, h);
            viewport.set(x, y, w, h);
        }
    }

    public void setClipRect(int x, int y, int w, int h) {
        if (!clipRect.equals(x, y, w, h)) {
            glScissor(x, y, w, h);
            clipRect.set(x, y, w, h);
        }
    }

    public void setBlendEquations(int rgb, int alpha) {
        if (blendEquationRGB != rgb || blendEquationAlpha != alpha) {
            glBlendEquationSeparate(rgb, alpha);
            blendEquationRGB = rgb;
            blendEquationAlpha = alpha;
        }
    }

    public void setBlendFunc(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        if (blendFuncSrcRGBFactor != srcRGB || blendFuncDstRGBFactor != dstRGB ||
                blendFuncSrcAlphaFactor != srcAlpha || blendFuncDstAlphaFactor != dstAlpha) {
            glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
            blendFuncSrcRGBFactor = srcRGB;
            blendFuncDstRGBFactor = dstRGB;
            blendFuncSrcAlphaFactor = srcAlpha;
            blendFuncDstAlphaFactor = dstAlpha;
        }
    }

    public void setDepthFunc(int func) {
        if (depthFunc != func) {
            glDepthFunc(func);
            depthFunc = func;
        }
    }

    public void setCullFace(int face) {
        if (cullFace != face) {
            glCullFace(face);
            cullFace = face;
        }
    }

    public void setFrontFace(int face) {
        if (frontFace != face) {
            glFrontFace(face);
            frontFace = face;
        }
    }

    public void setPolygonMode(int mode) {
        if (polygonMode != mode) {
            glPolygonMode(GL_FRONT_AND_BACK, mode);
            polygonMode = mode;
        }
    }

    public void setPixelUnpackAlignment(int align) {
        if (pixelUnpackAlignment != align) {
            glPixelStorei(GL_UNPACK_ALIGNMENT, align);
            pixelUnpackAlignment = align;
        }
    }

    public void framebufferDeleted(int fbo) {
        if (boundFramebuffer == fbo)
            boundFramebuffer = 0;
    }

    public void programDeleted(int program) {
        if (boundProgram == program)
            boundProgram = 0;
    }

    public void arrayBufferDeleted(int vbo) {
        if (boundArrayBuffer == vbo)
            boundArrayBuffer = 0;
        vertexArrayManager.vertexBufferDeleted(vbo);
    }

    public void elementArrayBufferDeleted(int ibo) {
        vertexArrayManager.indexBufferDeleted(ibo);
    }

    public void textureDeleted(int texture) {
        for (int i = 0; i < boundTextures.length; i++) {
            if (boundTextures[i] == texture) {
                boundTextures[i] = 0;
            }
        }
    }

    public VertexArrayManager getVertexArrayManager() {
        return vertexArrayManager;
    }

    @Override
    public void close() {
        vertexArrayManager.close();
    }
}
