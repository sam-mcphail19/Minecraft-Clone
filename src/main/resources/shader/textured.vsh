#version 450 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;

uniform mat4 mvp;

out DATA {
	vec2 texCoords;
} out_data;

void main(void) {
	gl_Position = mvp * vec4(position, 1.0);
	out_data.texCoords = texCoords;
}