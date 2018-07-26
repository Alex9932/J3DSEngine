#version 330 core
#extension GL_EXT_gpu_shader4 : enable

const vec2 lightBias = vec2(0.7, 0.6);//just indicates the balance between diffuse and ambient lighting

in vec2 pass_textureCoords;
in vec3 pass_normal;
in vec3 pass_pos;
in vec4 shadowCoords;

out vec4 out_colour;

uniform sampler2D diffuseMap;
uniform sampler2D shadowMap;
uniform sampler2D specularMap;
uniform vec3 lightDirection;
uniform vec3 camPos;
uniform float specPower;
uniform bool hasSpecular;

const int pcfCount = 4;
const float totalTexels = ((pcfCount * 2.0) + 1.0) * ((pcfCount * 2.0) + 1.0);

void main(void){
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	if(diffuseColour.a < 0.5) {
		discard;
	}
	////////////////////////////////////////////////////////////////////////////

	float objNearestLight = texture(shadowMap, shadowCoords.xy).r;
	float lightFactor = 1.0;
	if(shadowCoords.z > objNearestLight + 0.0003) {
		lightFactor -= shadowCoords.w * objNearestLight * 0.4;
	}

	float specularMapFactor = texture(specularMap, pass_textureCoords).r;

	vec3 unitNormal = normalize(pass_normal);
	float diffuseLight = (max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y);

	if(hasSpecular) {
		vec3 directionToCam = normalize(camPos - pass_pos);
		vec3 reflectDirection = normalize(reflect(lightDirection, unitNormal));
		float specularFactor = dot(directionToCam, reflectDirection);
		specularFactor = pow(specularFactor, 2);
		specularFactor = specularFactor * specularMapFactor;
		diffuseLight += specularFactor * lightFactor;
	}

	out_colour = diffuseColour * diffuseLight * lightFactor;
	
}
