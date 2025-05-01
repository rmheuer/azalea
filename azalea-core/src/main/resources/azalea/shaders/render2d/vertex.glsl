#version 330 core

layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec2 a_TexCoord;
layout(location = 2) in vec4 a_Color;
layout(location = 3) in int a_TextureSlot;

uniform mat4 u_ModelViewProj;

out vec4 v_Color;
out vec2 v_TexCoord;
flat out int v_TextureSlot;

void main(void) {
    gl_Position = u_ModelViewProj * vec4(a_Position, 0.0, 1.0);
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;
    v_TextureSlot = a_TextureSlot;
}
