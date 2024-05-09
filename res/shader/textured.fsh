#version 450 core

in vec3 v_position;
in vec3 v_normal;
in vec2 v_texCoords;
in flat int v_isOnSelectedBlock;

out vec4 out_Color;

uniform sampler2D tex;

void main() {
    out_Color = texture(tex, v_texCoords);
    if (v_isOnSelectedBlock != 0) {
        out_Color = out_Color + vec4(0.1);
    }
}