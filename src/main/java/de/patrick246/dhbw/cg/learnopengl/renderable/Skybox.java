package de.patrick246.dhbw.cg.learnopengl.renderable;

import de.patrick246.dhbw.cg.learnopengl.opengl.Cubemap;
import de.patrick246.dhbw.cg.learnopengl.RenderPass;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.opengl.Vao;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL43C.*;

public class Skybox implements Renderable {
    private Cubemap cubemap;
    private Vao vao;
    private Shader shader;

    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public void render(Shader s, RenderPass currentPass) {
        if (currentPass == RenderPass.FORWARD_GEOMETRY_PASS) {
            glDepthFunc(GL_LEQUAL);
            s.setMat4f("model", new Matrix4f().identity());
            this.vao.bind();
            this.cubemap.bind(s, "skybox", 0);
            glDrawArrays(GL_TRIANGLES, 0, 36);
            glDepthFunc(GL_LESS);
        }
    }

    public Skybox(List<String> path) {
        this.cubemap = new Cubemap(path);
        this.shader = new Shader("/res/shaders/cubemapShader.vert", "/res/shaders/cubemapShader.frag").compile();
        this.vao = new Vao(new float[]{
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,

                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,

                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f
        }).fillBuffer();
    }
}
