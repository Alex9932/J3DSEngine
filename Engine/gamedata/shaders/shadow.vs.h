#version 150

#define TYPE_MODEL_STATIC 0
#define TYPE_MODEL_ANIMATED 1

#define MAX_JOINTS 50
#define MAX_WEIGHTS 3

in vec3 in_position;
in vec2 texcoords;
in vec3 in_normal;
in ivec3 in_jointIndices;
in vec3 in_weights;

out vec2 tex_coords;

uniform mat4 mvp;
uniform mat4 jointTransforms[MAX_JOINTS];
uniform int model_type;

void main(void){
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
	} else if(model_type == TYPE_MODEL_STATIC) {
		gl_Position = mvp * vec4(in_position, 1);
	}

	//gl_Position = mvpMatrix * vec4(in_position, 1.0);

	tex_coords = texcoords;
}
