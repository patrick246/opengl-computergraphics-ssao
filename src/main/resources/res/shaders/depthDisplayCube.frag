in vec3 FragWorldPos;

struct PointLight {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;

    samplerCube shadowMap;
};
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform float far_plane;

void main() {
    vec3 fragToLight = FragWorldPos - pointLights[0].position;
    float closest = texture(pointLights[0].shadowMap, fragToLight).r * far_plane;

    gl_FragColor = vec4(vec3(closest / far_plane), 1.0);
}
