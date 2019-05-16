package de.patrick246.dhbw.cg.learnopengl.camera;

import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

@Data
@RequiredArgsConstructor
public abstract class Camera {

    @NonNull
    protected Vector3f position;
    @NonNull
    protected Vector3f front;
    @NonNull
    protected Vector3f up;


    private float speed = 10.f;

    private float yaw = 0;
    private float pitch = 0;

    public abstract void setupView(Shader shader);



    public void yawPitch(float yawOffset, float pitchOffset) {
        this.yaw += yawOffset;
        this.pitch += pitchOffset;


        if(pitch > 89.0f)
            pitch = 89.0f;
        if(pitch < -89.0f)
            pitch = -89.0f;

        this.front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        this.front.y = (float)(Math.sin(Math.toRadians(pitch)));
        this.front.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        this.front.normalize();
    }
}
