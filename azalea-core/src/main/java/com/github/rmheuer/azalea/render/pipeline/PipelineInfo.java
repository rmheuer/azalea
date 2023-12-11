package com.github.rmheuer.azalea.render.pipeline;

import com.github.rmheuer.azalea.render.shader.ShaderProgram;

public final class PipelineInfo {
    private final ShaderProgram shader;
    private boolean blend;
    private boolean depthTest;
    private CullMode cullMode;
    private FaceWinding winding;

    public PipelineInfo(ShaderProgram shader) {
        this.shader = shader;
        blend = true;
        depthTest = false;
        cullMode = CullMode.OFF;
        winding = FaceWinding.CW_FRONT;
    }

    public PipelineInfo setBlend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public PipelineInfo setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
        return this;
    }

    public PipelineInfo setCullMode(CullMode cullMode) {
        this.cullMode = cullMode;
        return this;
    }

    public PipelineInfo setWinding(FaceWinding winding) {
        this.winding = winding;
        return this;
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public boolean isBlend() {
        return blend;
    }

    public boolean isDepthTest() {
        return depthTest;
    }

    public CullMode getCullMode() {
        return cullMode;
    }

    public FaceWinding getWinding() {
        return winding;
    }
}
