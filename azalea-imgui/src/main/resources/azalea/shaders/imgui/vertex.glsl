#version 330 core

uniform mat4 u_ProjMtx;

layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec2 a_UV;
layout(location = 2) in vec4 a_Color;

out vec2 v_UV;
out vec4 v_Color;

void main(void) {
    gl_Position = u_ProjMtx * vec4(a_Position, 0, 1);
    v_UV = a_UV;
    v_Color = a_Color;
}
