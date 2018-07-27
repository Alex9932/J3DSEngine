#version 330 core

in vec3 in_position;
in vec2 in_texture_coord;
in vec4 in_color;

out vec2 tex_coord;
out vec4 color;

uniform mat4 proj;
uniform mat4 view;

void main () {
	gl_Position = proj * view * vec4(in_position, 1);
	color = in_color;
	tex_coord = in_texture_coord;
}
