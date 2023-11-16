#version 330 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec2 a_TexCoord;
layout(location = 2) in vec4 a_Color;
layout(location = 3) in float a_TextureSlot;

uniform mat4 u_Projection;
uniform mat4 u_View;
uniform mat4 u_Transform;

out vec4 v_Color;
out vec2 v_TexCoord;
flat out float v_TextureSlot;

void main(void) {
    gl_Position = u_Projection * u_View * u_Transform * vec4(a_Position, 1.0);
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;
    v_TextureSlot = a_TextureSlot;
}
