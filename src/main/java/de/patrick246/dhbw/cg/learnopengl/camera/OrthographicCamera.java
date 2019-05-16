package de.patrick246.dhbw.cg.learnopengl.camera;

import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import lombok.NonNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
    private Matrix4f view;
    private Matrix4f projection;
    private Matrix4f lightMatrix;


    public OrthographicCamera(@NonNull Vector3f position, @NonNull Vector3f front, @NonNull Vector3f up) {
        super(position, front, up);
        view = new Matrix4f().identity()
                .lookAt(position, front, up);
        projection = new Matrix4f().identity().ortho(-10.f, 10.f, -10.f, 10.f,-10f, 30f);
        lightMatrix = projection.mul(view, new Matrix4f());
    }

    @Override
    public void setupView(Shader shader) {
        shader.setMat4f("view", view);
        shader.setMat4f("projection", projection);
        shader.setVec3f("viewPos", getPosition());
    }

    public Matrix4f getLightMatrix() {
        return lightMatrix;
    }
}
