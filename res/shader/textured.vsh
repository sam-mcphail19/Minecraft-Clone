#version 450 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoords;
layout (location = 3) in int flags;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 v_position;
out vec3 v_normal;
out vec2 v_texCoords;
out flat int v_isOnSelectedBlock;

void main(void) {
    v_position = vec3(model * vec4(position, 1)) - vec3(0, 55, 15);
    v_normal = normal;
    v_texCoords = texCoords;
    v_isOnSelectedBlock = flags;

    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}