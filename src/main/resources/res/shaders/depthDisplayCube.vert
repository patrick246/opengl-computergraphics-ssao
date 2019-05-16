layout (location = 0) in vec3 aPos;
layout (location = 2) in vec2 aTexCoord;
layout (location = 3) in vec3 aNormal;

out vec2 TexCoord;
out vec3 Normal;
out vec3 FragWorldPos;
out vec4 FragLightPos;

uniform mat4 model;
uniform mat4 modelNormal;
uniform mat4 view;
uniform mat4 projection;

struct DirLightVS {
    mat4 lightSpaceMat;
};
uniform DirLightVS dirLightVS;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    TexCoord = aTexCoord;
    Normal = mat3(modelNormal) * aNormal;
    FragWorldPos = vec3(model * vec4(aPos, 1.0));
    FragLightPos = dirLightVS.lightSpaceMat * vec4(FragWorldPos, 1.0);
}
