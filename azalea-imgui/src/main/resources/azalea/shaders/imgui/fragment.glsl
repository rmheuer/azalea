#version 330 core

uniform sampler2D u_Texture;

in vec2 v_UV;
in vec4 v_Color;

layout(location = 0) out vec4 o_Color;

void main(void) {
    o_Color = v_Color * texture(u_Texture, v_UV);
}
