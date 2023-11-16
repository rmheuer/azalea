#version 330 core

in vec4 v_Color;
in vec2 v_TexCoord;
flat in float v_TextureSlot;

uniform sampler2D u_Textures[16];

layout(location = 0) out vec4 o_Color;

void main(void) {
    o_Color = v_Color;

    int slot = int(v_TextureSlot);

    // It is not allowed to index a sampler2D array using an input variable
    switch (slot) {
        case 0: o_Color *= texture(u_Textures[0], v_TexCoord); break;
        case 1: o_Color *= texture(u_Textures[1], v_TexCoord); break;
        case 2: o_Color *= texture(u_Textures[2], v_TexCoord); break;
        case 3: o_Color *= texture(u_Textures[3], v_TexCoord); break;
        case 4: o_Color *= texture(u_Textures[4], v_TexCoord); break;
        case 5: o_Color *= texture(u_Textures[5], v_TexCoord); break;
        case 6: o_Color *= texture(u_Textures[6], v_TexCoord); break;
        case 7: o_Color *= texture(u_Textures[7], v_TexCoord); break;
        case 8: o_Color *= texture(u_Textures[8], v_TexCoord); break;
        case 9: o_Color *= texture(u_Textures[9], v_TexCoord); break;
        case 10: o_Color *= texture(u_Textures[10], v_TexCoord); break;
        case 11: o_Color *= texture(u_Textures[11], v_TexCoord); break;
        case 12: o_Color *= texture(u_Textures[12], v_TexCoord); break;
        case 13: o_Color *= texture(u_Textures[13], v_TexCoord); break;
        case 14: o_Color *= texture(u_Textures[14], v_TexCoord); break;
        case 15: o_Color *= texture(u_Textures[15], v_TexCoord); break;
    }

    if (o_Color.a < 0.01) {
        discard;
    }
}
