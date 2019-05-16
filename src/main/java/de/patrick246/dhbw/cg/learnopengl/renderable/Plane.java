package de.patrick246.dhbw.cg.learnopengl.renderable;

import de.patrick246.dhbw.cg.learnopengl.Material;
import de.patrick246.dhbw.cg.learnopengl.RenderPass;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.opengl.Vao;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL43C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL43C.glDrawArrays;

public class Plane implements Renderable {
    private Shader shader;
    private Matrix4f modelMatrix;
    private Material material;
    private Vao vao;


    public Plane(Shader shader, Material material) {
        this.shader = shader;
        this.material = material;
        this.modelMatrix = new Matrix4f().identity();
        this.vao = new Vao(generateVertices())
                .setTextureCoords(generateTextureCoords())
                .setNormals(generateNormals())
                .fillBuffer();
    }

    private float[] generateVertices() {
        return new float[]{
                -0.5f, 0.5f, 0f,
                0.5f, -0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                -0.5f, 0.5f, 0f,
                0.5f, 0.5f, 0f,
        };
    }

    private float[] generateNormals() {
        return new float[]{
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
        };
    }

    private float[] generateTextureCoords() {
        return new float[]{
                1f, 0f,
                0f, 1f,
                1f, 1f,
                0f, 1f,
                1f, 0f,
                0f, 0f,
        };
    }

    public Plane scale(Vector3f factor) {
        this.modelMatrix.scale(factor);
        return this;
    }

    public Plane translate(Vector3f translation) {
        this.modelMatrix.translate(translation);
        return this;
    }

    public Plane rotate(float angle, Vector3f axis) {
        this.modelMatrix.rotate(angle, axis);
        return this;
    }

    public Plane setTextureScale(float scale) {
        float[] textureCoords = generateTextureCoords();
        for(int i = 0; i < textureCoords.length; i++) {
            textureCoords[i] *= scale;
        }
        this.vao.setTextureCoords(textureCoords).fillBuffer();
        return this;
   }

    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public void render(Shader s, RenderPass currentPass) {
        s
                .setMat4f("model", modelMatrix)
                .setMat4f("modelNormal", modelMatrix.invert(new Matrix4f()).transpose());
        this.material.apply(s);
        this.vao.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
