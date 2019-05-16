layout (location = 0) in vec3 aPos;

out vec2 TexCoord;

void main() {
    gl_Position = vec4(aPos, 1.0);
    TexCoord = (aPos.xy + vec2(1.0)) / 2.0;
}
