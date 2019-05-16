package de.patrick246.dhbw.cg.learnopengl.renderable;

import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.Updatable;
import de.patrick246.dhbw.cg.learnopengl.opengl.Vao;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Cube implements Renderable, Updatable {
    private Shader shader;
    private Matrix4f modelMatrix;
    protected Vao vao;

    @Getter
    private Vector3f position;

    public Cube(Shader shader) {
        this.shader = shader;
        this.modelMatrix = new Matrix4f().identity();
        this.position = new Vector3f();
    }

    public Cube translate(Vector3f translation) {
        this.modelMatrix.translate(translation);
        this.position.add(translation);
        return this;
    }

    public Cube rotate(float angle, Vector3f axis) {
        this.modelMatrix.rotate(angle, axis);
        return this;
    }

    public Cube scale(Vector3f factors) {
        this.modelMatrix.scale(factors);
        return this;
    }

    public Shader getShader() {
        return shader;
    }

    protected Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    protected float[] generateVertices() {
        return new float[]{
                -0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f,  0.5f,
        };
    }
}
