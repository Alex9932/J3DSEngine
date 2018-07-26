#version 330 core

in vec2 coords;

out vec4 color;

uniform sampler2D tex;

void main() {
	color = texture(tex, coords);



	//BLUR

	/**float step = 0.001;
	int blur = 10;

	for (int i = -(blur / 2); i < blur / 2; ++i) {
		for (int j = -(blur / 2); j < blur / 2; ++j) {
			vec2 texCoord = vec2(coords.x + (step * i), coords.y + (step * j));
			color += texture(tex, texCoord);
		}
	}

	color /= blur*blur;**/

	//SOBEL

	/**vec4 top         = texture(tex, vec2(coords.x, coords.y + 1.0 / 200.0));
	vec4 bottom      = texture(tex, vec2(coords.x, coords.y - 1.0 / 200.0));
	vec4 left        = texture(tex, vec2(coords.x - 1.0 / 300.0, coords.y));
	vec4 right       = texture(tex, vec2(coords.x + 1.0 / 300.0, coords.y));
	vec4 topLeft     = texture(tex, vec2(coords.x - 1.0 / 300.0, coords.y + 1.0 / 200.0));
	vec4 topRight    = texture(tex, vec2(coords.x + 1.0 / 300.0, coords.y + 1.0 / 200.0));
	vec4 bottomLeft  = texture(tex, vec2(coords.x - 1.0 / 300.0, coords.y - 1.0 / 200.0));
	vec4 bottomRight = texture(tex, vec2(coords.x + 1.0 / 300.0, coords.y - 1.0 / 200.0));
	vec4 sx = -topLeft - 2 * left - bottomLeft + topRight   + 2 * right  + bottomRight;
	vec4 sy = -topLeft - 2 * top  - topRight   + bottomLeft + 2 * bottom + bottomRight;
	vec4 sobel = sqrt(sx * sx + sy * sy);
	color = sobel;**/
}
