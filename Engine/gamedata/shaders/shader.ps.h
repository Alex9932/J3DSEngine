#version 330 core
#extension GL_EXT_gpu_shader4 : enable

#define PIXEL (1.0 / 8190.0)
#define SMOOTH_SHADOW false

in vec2 pass_textureCoords;
in vec3 pass_normal;
in vec3 pass_pos;
in vec4 shadowCoords;
in vec3 outpos;

out vec4 out_color;
out vec4 out_position;
out vec4 out_normal;
out vec4 out_specular;

uniform sampler2D diffuseMap;
uniform sampler2D shadowMap;
uniform sampler2D specularMap;
uniform vec3 lightDirection;
uniform vec3 camPos;
uniform float specPower;
uniform bool hasSpecular;

const int pcfCount = 4;
const float totalTexels = ((pcfCount * 2.0) + 1.0) * ((pcfCount * 2.0) + 1.0);

float calcShadow() {
	float objNearestLight;

	if (SMOOTH_SHADOW) {
		int s = 5;
		int e = 0;
		for (int i = -s; i <= s; i++) {
			for (int k = -s; k <= s; k++) {
				objNearestLight += texture(shadowMap, shadowCoords.xy + vec2(i * PIXEL, k * PIXEL)).r;
			}
			e++;
		}
		objNearestLight /= (e * e);
	} else {
		objNearestLight = texture(shadowMap, shadowCoords.xy).r;
	}

	return objNearestLight;
}

void main(void){
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	if(diffuseColour.a < 0.5) {
		discard;
	}
	////////////////////////////////////////////////////////////////////////////


	float objNearestLight = calcShadow();
	float lightFactor = 1.0;
	if(shadowCoords.z > objNearestLight + 0.0003) {
		lightFactor -= shadowCoords.w * objNearestLight * 0.5;
	}

	vec3 unitNormal = normalize(pass_normal);
	float specularMapFactor = texture(specularMap, pass_textureCoords).r;
	float specularFactor;
	if(hasSpecular) {
		vec3 directionToCam = normalize(camPos - pass_pos);
		vec3 reflectDirection = normalize(reflect(lightDirection, unitNormal));
		specularFactor = dot(directionToCam, reflectDirection);
		specularFactor = pow(specularFactor, 2);
		specularFactor = specularFactor;
	}

	out_color = diffuseColour;
	out_position = vec4(outpos, 1);
	out_normal = (vec4(unitNormal, 1) + 1) / 2;
	//out_normal.xyz = normalize(unitNormal);
	out_normal.a = lightFactor;
	out_specular = vec4(specularFactor, specularFactor, specularFactor, 1);
	
}
