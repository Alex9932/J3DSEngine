#version 330 core

#define TYPE_MODEL_STATIC 0
#define TYPE_MODEL_ANIMATED 1

#define MAX_JOINTS 50
#define MAX_WEIGHTS 3

in vec3 in_position;
in vec2 in_textureCoords;
in vec3 in_normal;
in ivec3 in_jointIndices;
in vec3 in_weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;
out vec3 pass_pos;
out vec4 shadowCoords;
out float visibility;

uniform mat4 proj;
uniform mat4 view;
uniform mat4 model;

uniform mat4 shadowMapSpace;
uniform mat4 jointTransforms[MAX_JOINTS];
uniform int model_type;
uniform float shadowLength;

const float transitionDist = 2.0;

const float density = 0.007;
const float gradient = 1.5;

void main(void){
	mat4 mvp = proj * view * model;

	vec4 p = mvp * vec4(in_position, 1);
	if(p.z > 150) {
		gl_Position = p;
		gl_Position = p;
		gl_Position.w = 0;
		return;
	}
	
	vec4 posRelToCam;

	if(model_type == TYPE_MODEL_ANIMATED) {
		vec4 totalLocalPos = vec4(0.0);
		vec4 totalNormal = vec4(0.0);
		for(int i = 0; i < MAX_WEIGHTS; i++){
			mat4 jointTransform = jointTransforms[in_jointIndices[i]];
			vec4 posePosition = jointTransform * vec4(in_position, 1.0);
			totalLocalPos += posePosition * in_weights[i];
			vec4 worldNormal = jointTransform * vec4(in_normal, 0.0);
			totalNormal += worldNormal * in_weights[i];
		}
		gl_Position = mvp * totalLocalPos;
		posRelToCam = view * model * totalLocalPos;
		pass_pos = (model * totalLocalPos).xyz;
		pass_normal = totalNormal.xyz;
	} else if(model_type == TYPE_MODEL_STATIC) {
		gl_Position = mvp * vec4(in_position, 1);
		posRelToCam = view * model * vec4(in_position, 1);
		pass_pos = (proj *model * vec4(in_position, 1)).xyz;
		pass_normal = (model * vec4(in_normal, 0)).xyz;
	}

	vec4 world_position = model * vec4(in_position, 1);
	shadowCoords = shadowMapSpace * world_position;
	
	pass_textureCoords = in_textureCoords;

	float distance = length((view * model * vec4(in_position, 1.0)).xyz);

	distance = distance - (shadowLength - transitionDist);
	distance = distance / transitionDist;
	shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);

	float fogdistance = length(posRelToCam.xyz);
	visibility = exp(-pow((fogdistance * density), gradient));
	visibility = clamp(visibility, 0, 1);

}
