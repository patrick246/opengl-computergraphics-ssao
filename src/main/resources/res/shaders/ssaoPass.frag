in vec2 TexCoord;

out float FragColor;

uniform sampler2D positionMap;
uniform float sampleRad;
uniform mat4 projection;

const int MAX_KERNEL_SIZE = 128;
uniform vec3 kernel[MAX_KERNEL_SIZE];

void main() {
    vec3 Pos = texture(positionMap, TexCoord).xyz;

    float AO = 0.0;

    for(int i = 0; i < MAX_KERNEL_SIZE; i++) {
        vec3 samplePos = Pos + kernel[i];
        vec4 offset = vec4(samplePos, 1.0);
        offset = projection * offset;
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5;

        float sampleDepth = texture(positionMap, offset.xy).z;

        float rangeCheck = smoothstep(0.0, 1.0, sampleRad / abs(Pos.z - sampleDepth));
        AO += (sampleDepth >= samplePos.z + 0.025 ? 1.0 : 0.0) * rangeCheck;
    }

    AO = 1.0 - (AO/float(MAX_KERNEL_SIZE));

    FragColor = AO;
}
