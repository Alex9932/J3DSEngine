#version 330 core

in vec2 pos;

out vec2 coords;

void main() {
	gl_Position = vec4(pos, 0, 1);
	coords = (pos + 1) / 2;
}
