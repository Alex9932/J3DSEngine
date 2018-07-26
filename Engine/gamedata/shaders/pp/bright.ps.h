#version 150

in vec2 coords;

out vec4 color;

uniform sampler2D texture0;

void main(void){
	color = texture(texture0, coords);
	float bright = (color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722);
	color *= bright;
}
