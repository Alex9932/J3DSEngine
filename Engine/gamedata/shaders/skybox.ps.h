#version 330 core

in vec3 texcoord;

out vec4 color;

uniform samplerCube texture;

void main() {
	vec3 colora = vec3(0.3164, 0.7343, 1);
	vec3 colorb = vec3(0.3164, 0.2734, 1);

	color = vec4(mix(colora, colorb, texcoord.y), 1);
}
