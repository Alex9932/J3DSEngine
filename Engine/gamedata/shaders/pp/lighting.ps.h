#version 330 core

in vec2 coords;

out vec4 color;

uniform sampler2D diffuse;
uniform sampler2D normals;
uniform sampler2D specular;
uniform vec3 lightDirection;

const vec2 lightBias = vec2(0.7, 0.6);

void main() {
	vec4 normal = texture(normals, coords);
	float shadowFactor = normal.a;
	vec3 unitNormal = (normal.xyz * 2) - 1;
	float diffuseLight = (max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y);
	float specFactor = (texture(specular, coords).r * 0.5) * shadowFactor;

	color = (texture(diffuse, coords) * diffuseLight * shadowFactor) + specFactor;
}
