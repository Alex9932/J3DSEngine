#version 330 core

in vec2 tex_coord;
in vec4 color;

out vec4 out_color;

uniform sampler2D tex;
uniform int has_texture;

void main () {
	if (has_texture == 1) {
		out_color = color * texture(tex, tex_coord);
	} else if (has_texture == 0) {
		out_color = color;
	}
}
