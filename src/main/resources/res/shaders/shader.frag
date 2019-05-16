in vec2 TexCoord;
in vec3 Normal;
in vec3 FragWorldPos;
in vec4 FragLightPos;

out vec4 FragColor;

uniform vec3 viewPos;

struct Material {
    vec3 ambient;
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};
uniform Material material;

struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    sampler2D shadowMap;
};
uniform DirLight dirLight;

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

uniform sampler2D ssaoMap;
uniform vec2 screenSize;

vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir, float shadow, float ao);
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, float shadow, float ao);
float ShadowCalculationDirectional(DirLight light, vec4 fragLightPos);
float ShadowCalculationPoint(PointLight linear, vec3 fragPos);

void main()
{
    vec3 norm = normalize(Normal);
    vec3 viewDir = normalize(viewPos - FragWorldPos);

    float shadowDirectional = ShadowCalculationDirectional(dirLight, FragLightPos);

    float AO = texture(ssaoMap, gl_FragCoord.xy / screenSize).r;

    vec3 result = CalcDirLight(dirLight, norm, viewDir, shadowDirectional, AO);
    for(int i = 0; i < NR_POINT_LIGHTS; i++) {
        float shadow = ShadowCalculationPoint(pointLights[i], FragWorldPos);
        result += CalcPointLight(pointLights[i], norm, FragWorldPos, viewDir, shadow, AO);
    }

    FragColor = vec4(result, 1.0);
}


vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir, float shadow, float ao)
{
    vec3 lightDir = normalize(-light.direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TexCoord)) * ao;
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TexCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoord));

    return (ambient + (1.0 - shadow) * (diffuse + specular));
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, float shadow, float ao)
{
    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), material.shininess);
    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TexCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TexCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoord));
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;

    ambient *= ao;

    return (ambient + (1.0 - shadow) * (diffuse + specular));
}

float ShadowCalculationDirectional(DirLight light, vec4 fragLightPos) {
    vec3 projCoords = (fragLightPos.xyz / fragLightPos.w) * 0.5 + 0.5;
    float closestDepth = texture(dirLight.shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;

    float bias = max(0.05 * (1.0 - dot(normalize(Normal), dirLight.direction)), 0.005);

    if(projCoords.z > 1.0) {
        return 0.0;
    }
    return currentDepth - bias > closestDepth ? 1.0 : 0.0;
}

float ShadowCalculationPoint(PointLight light, vec3 fragPos) {
    vec3 fragToLight = fragPos - light.position;
    float closest = texture(light.shadowMap, fragToLight).r * far_plane;
    float currentDepth = length(fragToLight);
    float bias = 0.05;
    return currentDepth - bias > closest ? 1.0 : 0.0;
}
