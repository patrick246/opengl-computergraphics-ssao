package de.patrick246.dhbw.cg.learnopengl.lighting;

import de.patrick246.dhbw.cg.learnopengl.RenderPass;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.framebuffer.Framebuffer;
import de.patrick246.dhbw.cg.learnopengl.framebuffer.RenderCubemap;
import de.patrick246.dhbw.cg.learnopengl.renderable.Renderable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL43C.*;

@Data
@NoArgsConstructor
public class PointLight {
    private Vector3f position;
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private float constant;
    private float linear;
    private float quadratic;

    private Matrix4f[] lightView = new Matrix4f[6];
    private Framebuffer shadowMapFbo;
    private RenderCubemap shadowMapTex;


    public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular, float constant, float linear, float quadratic) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;

        Matrix4f lightProjection = new Matrix4f().perspective((float)Math.toRadians(90), 1f, 1f, 25f);

        Vector3f[] directions = new Vector3f[]{
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(-1.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f),
                new Vector3f(0.0f,-1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f,-1.0f),
        };

        Vector3f[] up = new Vector3f[]{
                new Vector3f(0.0f,-1.0f, 0.0f),
                new Vector3f(0.0f,-1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f,-1.0f),
                new Vector3f(0.0f,-1.0f, 0.0f),
                new Vector3f(0.0f,-1.0f, 0.0f),
        };

        for(int i = 0; i < 6; i++) {
            this.lightView[i] = lightProjection.mul(new Matrix4f().lookAt(this.position, this.position.add(directions[i], new Vector3f()), up[i]), new Matrix4f());
        }

        this.shadowMapTex = new RenderCubemap(new Vector2i(1024));
        this.shadowMapFbo = new Framebuffer().attach(this.shadowMapTex);
    }

    public void apply(Shader shader, int id, int textureUnit) {
        shader.setVec3f("pointLights[" + id + "].position", position);
        shader.setVec3f("pointLights[" + id + "].ambient", ambient);
        shader.setVec3f("pointLights[" + id + "].diffuse", diffuse);
        shader.setVec3f("pointLights[" + id + "].specular", specular);
        shader.setFloat("pointLights[" + id + "].constant", constant);
        shader.setFloat("pointLights[" + id + "].linear", linear);
        shader.setFloat("pointLights[" + id + "].quadratic", quadratic);

        this.shadowMapTex.bind(textureUnit);
        shader.setInt("pointLights[" + id + "].shadowMap", textureUnit);
        shader.setFloat("far_plane", 25.f);
    }

    public void renderLightPass(List<Renderable> renderable, Shader pointLightShader) {
        glViewport(0,0,1024,1024);
        this.shadowMapFbo.bind();
        glClear(GL_DEPTH_BUFFER_BIT);
        pointLightShader.bind();
        for(int i = 0; i < 6; i++) {
            pointLightShader.setMat4f("shadowMatrices[" + i + "]", lightView[i]);
        }
        pointLightShader.setVec3f("lightPos", this.position);
        pointLightShader.setFloat("far_plane", 25.f);

        renderable.forEach(r -> r.render(pointLightShader, RenderPass.FORWARD_LIGHT_PASS));

        this.shadowMapFbo.unbind();
    }
}
