in vec2 TexCoords;

uniform sampler2D depthMap;

void main() {
    float depthValue = texture(depthMap, TexCoords).r;
    gl_FragColor = vec4(vec3(depthValue), 1.0);
}
