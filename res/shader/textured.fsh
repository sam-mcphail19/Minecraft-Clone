#version 450 core

in DATA {
    vec2 texCoords;
} in_data;

out vec4 out_Color;

uniform sampler2D tex;

float near = 1.0;
float far  = 5.0;

float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0; // back to NDC
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main() {
    //float depth = LinearizeDepth(gl_FragCoord.z) / far;
    //out_Color = vec4(vec3(pow(depth, 1.4)), 1.0);
    //out_Color = vec4(gl_FragCoord.x, gl_FragCoord.y, gl_FragCoord.z, 1.0);
    out_Color = texture(tex, in_data.texCoords);
}