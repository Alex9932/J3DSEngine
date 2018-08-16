#version 330 core

#define PIXEL (1.0 / 1280.0)

in vec2 coords;

out vec4 color;

uniform sampler2D diffuse;
uniform sampler2D normals;
uniform sampler2D specular;
uniform sampler2D ssao;
uniform vec3 lightDirection;

const vec2 lightBias = vec2(0.7, 0.6);

void main() {
	vec4 diffuse = texture(diffuse, coords);
	vec4 normal = texture(normals, coords);
	float ssao = texture(ssao, coords).r;

	vec3 unitNormal = (normal.xyz * 2) - 1;

	float shadowFactor = normal.a;
	int s = 5;
	int e = 0;
	for (int i = -s; i <= s; i++) {
		for (int k = -s; k <= s; k++) {
			shadowFactor += texture(normals, coords.xy + vec2(i * PIXEL, k * PIXEL)).a;
		}
		e++;
	}
	shadowFactor /= (e * e);

	float diffuseLight = (max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y);// * ssao;
	float specFactor = (texture(specular, coords).r * 0.5) * shadowFactor;

	vec4 lighting = diffuse;
	lighting *= diffuseLight;
	lighting *= shadowFactor;

	color = lighting + specFactor;
}
