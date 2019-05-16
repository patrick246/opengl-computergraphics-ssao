in vec2 TexCoords;

uniform sampler2D myTex;

void main() {
    gl_FragColor = vec4(vec3(texture(myTex, TexCoords).r), 1.0);
}
