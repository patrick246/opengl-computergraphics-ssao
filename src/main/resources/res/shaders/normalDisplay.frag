in vec3 Normal;

void main() {
    gl_FragColor = vec4(normalize(Normal) + 1 * 0.5, 1.0);
}
