#version 330 core

in vec2 coords;

out vec4 color;

const int MAX_KERNEL_SIZE = 128;
const float gSampleRad = 10;

uniform sampler2D positionmap;
uniform mat4 gProj;
uniform vec3 gKernel[MAX_KERNEL_SIZE];

void main() {
    vec3 Pos = texture(positionmap, coords).xyz;

    float AO = 0.0;

    for (int i = 0 ; i < MAX_KERNEL_SIZE ; i++) {
        vec3 samplePos = Pos + gKernel[i];
        vec4 offset = vec4(samplePos, 1.0);
        offset = gProj * offset;
        offset.xy /= offset.w;
        offset.xy = offset.xy * 0.5 + vec2(0.5);

        float sampleDepth = texture(positionmap, offset.xy).b;

        if (abs(Pos.z - sampleDepth) < gSampleRad) {
            AO += step(sampleDepth,samplePos.z);
        }
    }

    AO = 1.0 - AO/128.0;

    color = vec4(pow(AO, 2.0));
}
