#version 330 core

#define KERNEL_SIZE 16

in vec2 coords;

out vec4 color;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D texNoise;

uniform vec3 samples[KERNEL_SIZE];
uniform mat4 projection;

const vec2 noiseScale = vec2(1280.0/4.0, 720.0/4.0);
const float radius = 0.5;
const float bias = 0.025;

void main() {
	vec3 fragPos   = texture(gPosition, coords).xyz;
	//vec3 normal    = texture(gNormal, coords).rgb;
	vec3 normal = (texture(gNormal, coords).rgb * 2) - 1;
	vec3 randomVec = texture(texNoise, coords * noiseScale).xyz;

	vec3 tangent   = normalize(randomVec - normal * dot(randomVec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 TBN       = mat3(tangent, bitangent, normal);

	float occlusion = 0.0;
	for(int i = 0; i < KERNEL_SIZE; ++i) {
	    // get sample position
	    vec3 sample = TBN * samples[i]; // From tangent to view-space
	    sample = fragPos + sample * radius;

	    vec4 offset = vec4(sample, 1.0);
	    offset      = projection * offset;    // from view to clip-space
	    offset.xyz /= offset.w;               // perspective divide
	    offset.xyz  = offset.xyz * 0.5 + 0.5; // transform to range 0.0 - 1.0
	    float sampleDepth = texture(gPosition, offset.xy).z;
	    //occlusion += (sampleDepth >= sample.z + bias ? 1.0 : 0.0);

	    float rangeCheck = smoothstep(0.0, 1.0, radius / abs(fragPos.z - sampleDepth));
	    occlusion       += (sampleDepth >= sample.z + bias ? 1.0 : 0.0) * rangeCheck;
	}

	color = vec4(occlusion, occlusion, occlusion, 1);
}
