#version 150

in vec2 blurTextureCoords[11];

out vec4 colur;

uniform sampler2D originalTexture;

float kernel[11];

void main(void){
	kernel[0] = 0.0093;
	kernel[1] = 0.028002;
	kernel[2] = 0.065984;
	kernel[3] = 0.121703;
	kernel[4] = 0.175713;
	kernel[5] = 0.198596;
	kernel[6] = 0.175713;
	kernel[7] = 0.121703;
	kernel[8] = 0.065984;
	kernel[9] = 0.028002;
	kernel[10] = 0.0093;

	colur = vec4(0);

	for (int i = 0; i < 11; ++i) {
		colur += texture(originalTexture, blurTextureCoords[i]) * kernel[i];
	}
}
