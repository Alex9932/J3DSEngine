#version 330

in vec2 tex_coords;

out vec4 out_colour;

uniform sampler2D modelTexture;

void main(void){

	float alpha = texture(modelTexture, tex_coords).a;

	if(alpha < 0.5) {
		discard;
	}

	out_colour = vec4(1);
	
}
