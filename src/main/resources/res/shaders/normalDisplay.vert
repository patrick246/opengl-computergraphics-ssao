layout (location = 0) in vec3 aPos;
layout (location = 2) in vec2 aTexCoord;
layout (location = 3) in vec3 aNormal;

out vec3 Normal;

uniform mat4 model;
uniform mat4 modelNormal;
uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    Normal = mat3(modelNormal) * normalize(aNormal);
}
