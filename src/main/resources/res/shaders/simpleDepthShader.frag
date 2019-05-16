in vec3 ViewSpaceWorldPos;

void main() {
    gl_FragColor = vec4(ViewSpaceWorldPos, 1.0);
}
