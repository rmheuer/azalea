package com.github.rmheuer.azalea.render.pipeline;

import com.github.rmheuer.azalea.render.shader.ShaderProgram;

public final class PipelineInfo {
    private final ShaderProgram shader;
    private boolean clip;
    private boolean blend;
    private boolean depthTest;
    private DepthFunc depthFunc;
    private CullMode cullMode;
    private FaceWinding winding;
    private FillMode fillMode;

    public PipelineInfo(ShaderProgram shader) {
        this.shader = shader;
        clip = false;
        blend = true;
        depthTest = false;
        depthFunc = DepthFunc.LESS_OR_EQUAL;
        cullMode = CullMode.OFF;
        winding = FaceWinding.CW_FRONT;
        fillMode = FillMode.FILLED;
    }

    public PipelineInfo setClip(boolean clip) {
        this.clip = clip;
        return this;
    }

    public PipelineInfo setBlend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public PipelineInfo setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
        return this;
    }

    public void setDepthFunc(DepthFunc depthFunc) {
        this.depthFunc = depthFunc;
    }

    public PipelineInfo setCullMode(CullMode cullMode) {
        this.cullMode = cullMode;
        return this;
    }

    public PipelineInfo setWinding(FaceWinding winding) {
        this.winding = winding;
        return this;
    }

    public PipelineInfo setFillMode(FillMode fillMode) {
        this.fillMode = fillMode;
        return this;
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public boolean isClip() {
        return clip;
    }

    public boolean isBlend() {
        return blend;
    }

    public boolean isDepthTest() {
        return depthTest;
    }

    public DepthFunc getDepthFunc() {
        return depthFunc;
    }

    public CullMode getCullMode() {
        return cullMode;
    }

    public FaceWinding getWinding() {
        return winding;
    }

    public FillMode getFillMode() {
        return fillMode;
    }
}
