package de.patrick246.dhbw.cg.learnopengl.lighting;

import de.patrick246.dhbw.cg.learnopengl.RenderPass;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.camera.OrthographicCamera;
import de.patrick246.dhbw.cg.learnopengl.framebuffer.Framebuffer;
import de.patrick246.dhbw.cg.learnopengl.framebuffer.RenderTexture;
import de.patrick246.dhbw.cg.learnopengl.renderable.Renderable;
import lombok.Data;
import lombok.NonNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL43C.*;


@Data
public class DirectionalLight {
    @NonNull
    private Vector3f direction;
    @NonNull
    private Vector3f ambient;
    @NonNull
    private Vector3f diffuse;
    @NonNull
    private Vector3f specular;

    private OrthographicCamera camera;
    private Framebuffer shadowMapFbo;
    private RenderTexture shadowMapTex;

    public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this.direction = direction;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;

        this.camera = new OrthographicCamera(direction.mul(-1.f, new Vector3f()), new Vector3f(), new Vector3f(0.0f, 1.0f, 0.0f));

        this.shadowMapTex = new RenderTexture(RenderTexture.Type.DEPTH, new Vector2i(1024, 1024));
        this.shadowMapFbo = new Framebuffer().attach(this.shadowMapTex);
    }

    public void apply(Shader shader, int textureUnit) {
        shader.setVec3f("dirLight.direction", direction);
        shader.setVec3f("dirLight.ambient", ambient);
        shader.setVec3f("dirLight.diffuse", diffuse);
        shader.setVec3f("dirLight.specular", specular);
        shadowMapTex.bind(textureUnit);
        shader.setInt("dirLight.shadowMap", textureUnit);
        shader.setMat4f("dirLightVS.lightSpaceMat", camera.getLightMatrix());
    }

    public void renderLightPass(List<Renderable> renderables, Shader simpleLightShader) {
        glViewport(0,0 ,1024, 1024);
        this.shadowMapFbo.bind();
        simpleLightShader.bind();
        glClear(GL_DEPTH_BUFFER_BIT);
        camera.setupView(simpleLightShader);

        renderables.forEach(r -> r.render(simpleLightShader, RenderPass.FORWARD_LIGHT_PASS));

        this.shadowMapFbo.unbind();
    }
}
