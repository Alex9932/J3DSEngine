#version 330 core

in vec3 in_position;

out vec3 texcoord;

uniform mat4 proj;
uniform mat4 model;

void main() {
	mat4 mvp = proj * model;
	gl_Position = mvp * vec4(in_position, 1);
	texcoord = (in_position + 1) / 2;
}
