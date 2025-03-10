#version 330 core

in vec4 v_Color;

layout(location = 0) out vec4 o_Color;

void main(void) {
    o_Color = v_Color;
}
