package com.github.rmheuer.azalea.render.pipeline;

import com.github.rmheuer.azalea.render.shader.ShaderProgram;

public final class PipelineInfo {
    private final ShaderProgram shader;
    private boolean clip;
    private boolean depthTest; // TODO: Merge into DepthFunc enum
    private DepthFunc depthFunc;
    private boolean blend;
    private BlendOp blendOpRGB, blendOpAlpha;
    private BlendFactor blendSrcRGBFactor, blendDstRGBFactor;
    private BlendFactor blendSrcAlphaFactor, blendDstAlphaFactor;
    private CullMode cullMode;
    private FaceWinding winding;
    private FillMode fillMode;

    public PipelineInfo(ShaderProgram shader) {
        this.shader = shader;
        clip = false;
        depthTest = false;
        depthFunc = DepthFunc.LESS_OR_EQUAL;
        blend = true;
        blendOpRGB = blendOpAlpha = BlendOp.ADD;
        blendSrcRGBFactor = blendSrcAlphaFactor = BlendFactor.SRC_ALPHA;
        blendDstRGBFactor = blendDstAlphaFactor = BlendFactor.ONE_MINUS_SRC_ALPHA;
        cullMode = CullMode.OFF;
        winding = FaceWinding.CW_FRONT;
        fillMode = FillMode.FILLED;
    }

    public PipelineInfo setClip(boolean clip) {
        this.clip = clip;
        return this;
    }

    public PipelineInfo setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
        return this;
    }

    public PipelineInfo setDepthFunc(DepthFunc depthFunc) {
        this.depthFunc = depthFunc;
        return this;
    }

    public PipelineInfo setBlend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public PipelineInfo setBlendOp(BlendOp blendOp) {
        blendOpRGB = blendOpAlpha = blendOp;
        return this;
    }

    public PipelineInfo setBlendOpRGB(BlendOp blendOpRGB) {
        this.blendOpRGB = blendOpRGB;
        return this;
    }

    public PipelineInfo setBlendOpAlpha(BlendOp blendOpAlpha) {
        this.blendOpAlpha = blendOpAlpha;
        return this;
    }

    public PipelineInfo setBlendFactors(BlendFactor src, BlendFactor dst) {
        blendSrcRGBFactor = blendSrcAlphaFactor = src;
        blendDstRGBFactor = blendDstAlphaFactor = dst;
        return this;
    }

    public PipelineInfo setBlendFactors(BlendFactor srcRGB, BlendFactor dstRGB, BlendFactor srcAlpha, BlendFactor dstAlpha) {
        blendSrcRGBFactor = srcRGB;
        blendDstRGBFactor = dstRGB;
        blendSrcAlphaFactor = srcAlpha;
        blendDstAlphaFactor = dstAlpha;
        return this;
    }

    public PipelineInfo setBlendSrcRGBFactor(BlendFactor blendSrcRGBFactor) {
        this.blendSrcRGBFactor = blendSrcRGBFactor;
        return this;
    }

    public PipelineInfo setBlendDstRGBFactor(BlendFactor blendDstRGBFactor) {
        this.blendDstRGBFactor = blendDstRGBFactor;
        return this;
    }

    public PipelineInfo setBlendSrcAlphaFactor(BlendFactor blendSrcAlphaFactor) {
        this.blendSrcAlphaFactor = blendSrcAlphaFactor;
        return this;
    }

    public PipelineInfo setBlendDstAlphaFactor(BlendFactor blendDstAlphaFactor) {
        this.blendDstAlphaFactor = blendDstAlphaFactor;
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

    public boolean isDepthTest() {
        return depthTest;
    }

    public DepthFunc getDepthFunc() {
        return depthFunc;
    }

    public boolean isBlend() {
        return blend;
    }

    public BlendOp getBlendOpRGB() {
        return blendOpRGB;
    }

    public BlendOp getBlendOpAlpha() {
        return blendOpAlpha;
    }

    public BlendFactor getBlendSrcRGBFactor() {
        return blendSrcRGBFactor;
    }

    public BlendFactor getBlendDstRGBFactor() {
        return blendDstRGBFactor;
    }

    public BlendFactor getBlendSrcAlphaFactor() {
        return blendSrcAlphaFactor;
    }

    public BlendFactor getBlendDstAlphaFactor() {
        return blendDstAlphaFactor;
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
