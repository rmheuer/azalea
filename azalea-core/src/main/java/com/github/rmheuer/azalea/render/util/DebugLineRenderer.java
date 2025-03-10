package com.github.rmheuer.azalea.render.util;

import com.github.rmheuer.azalea.io.ResourceUtil;
import com.github.rmheuer.azalea.math.MathUtil;
import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.*;
import com.github.rmheuer.azalea.render.pipeline.ActivePipeline;
import com.github.rmheuer.azalea.render.pipeline.DepthFunc;
import com.github.rmheuer.azalea.render.pipeline.PipelineInfo;
import com.github.rmheuer.azalea.render.shader.ShaderProgram;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import java.io.IOException;

public final class DebugLineRenderer implements SafeCloseable {
    private static final VertexLayout LAYOUT = new VertexLayout(
            AttribType.VEC3, // Position,
            AttribType.COLOR_RGBA // Color
    );

    private final ShaderProgram shader;
    private final PipelineInfo pipeline;

    private final VertexBuffer vertexBuffer;
    private VertexData buildingData;

    public DebugLineRenderer(Renderer renderer) {
        try {
            shader = renderer.createShaderProgram(
                    ResourceUtil.readAsStream("azalea/shaders/debug_lines/vertex.glsl"),
                    ResourceUtil.readAsStream("azalea/shaders/debug_lines/fragment.glsl")
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load built-in shaders", e);
        }

        pipeline = new PipelineInfo(shader)
                .setDepthTest(true);

        vertexBuffer = renderer.createVertexBuffer();
        buildingData = new VertexData(LAYOUT);
    }

    public void addLine(Vector3fc pos1, Vector3fc pos2, int color) {
        addLine(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), color, color);
    }

    public void addLine(Vector3fc pos1, Vector3fc pos2, int color1, int color2) {
        addLine(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), color1, color2);
    }

    public void addLine(float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        addLine(x1, y1, z1, x2, y2, z2, color, color);
    }

    public void addLine(float x1, float y1, float z1, float x2, float y2, float z2, int color1, int color2) {
        buildingData.putVec3(x1, y1, z1);
        buildingData.putColorRGBA(color1);
        buildingData.putVec3(x2, y2, z2);
        buildingData.putColorRGBA(color2);
    }

    public void flush(Renderer renderer, Matrix4fc viewProj) {
        vertexBuffer.setData(buildingData, DataUsage.STREAM);

        int reserveCount = MathUtil.nextPowerOf2(buildingData.getVertexCount());
        buildingData.close();
        buildingData = new VertexData(LAYOUT);
        buildingData.reserve(reserveCount);

        try (ActivePipeline pipe = renderer.bindPipeline(pipeline)) {
            pipe.getUniform("u_ViewProj").setMat4(viewProj);
            pipe.draw(vertexBuffer, PrimitiveType.LINES);
        }
    }

    @Override
    public void close() {
        shader.close();
        vertexBuffer.close();
        buildingData.close();
    }

    public DebugLineRenderer setClip(boolean clip) {
        pipeline.setClip(clip);
        return this;
    }

    public DebugLineRenderer setBlend(boolean blend) {
        pipeline.setBlend(blend);
        return this;
    }

    public DebugLineRenderer setDepthTest(boolean depthTest) {
        pipeline.setDepthTest(depthTest);
        return this;
    }

    public DebugLineRenderer setDepthFunc(DepthFunc depthFunc) {
        pipeline.setDepthFunc(depthFunc);
        return this;
    }
}
