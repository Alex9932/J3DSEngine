#version 150

in vec2 coords;

out vec4 color;

uniform sampler2D texture0;
uniform sampler2D texture1;

void main(void){
	color = (texture(texture0, coords) + texture(texture1, coords)) * 0.7;
}
