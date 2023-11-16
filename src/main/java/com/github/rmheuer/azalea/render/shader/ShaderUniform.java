package com.github.rmheuer.azalea.render.shader;

import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public interface ShaderUniform {
    void setFloat(float f);
    void setVec2(Vector2fc v);
    void setVec3(Vector3fc v);
    void setVec4(Vector4fc v);
    void setMat4(Matrix4fc m);

    void setInt(int i);

    void setTexture(int slotIdx);
}
