package de.patrick246.dhbw.cg.learnopengl.camera;

import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
    @NonNull
    @Getter
    @Setter
    private float aspectRatio;

    public PerspectiveCamera(@NonNull Vector3f position, @NonNull Vector3f front, @NonNull Vector3f up, @NonNull float aspectRatio) {
        super(position, front, up);
        this.aspectRatio = aspectRatio;
    }

    @Override
    public void setupView(Shader shader) {
        Matrix4f view = new Matrix4f().identity()
                .lookAt(position, position.add(front, new Vector3f()), up);
        Matrix4f projection = new Matrix4f().identity().perspective((float)Math.toRadians(45.0), aspectRatio, 0.1f, 1000.0f);

        shader.setMat4f("view", view);
        shader.setMat4f("projection", projection);
        shader.setVec3f("viewPos", getPosition());
    }
}
