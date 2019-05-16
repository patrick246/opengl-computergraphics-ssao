layout (location = 0) in vec3 aPos;

out vec3 ViewSpaceWorldPos;

uniform mat4 model;
uniform mat4 modelNormal;
uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    ViewSpaceWorldPos = (view * model * vec4(aPos, 1.0)).xyz;
}
