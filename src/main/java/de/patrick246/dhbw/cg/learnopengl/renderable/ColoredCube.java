package de.patrick246.dhbw.cg.learnopengl.renderable;

import de.patrick246.dhbw.cg.learnopengl.RenderPass;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.opengl.Vao;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL43C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL43C.glDrawArrays;

public class ColoredCube extends Cube {

    private boolean lighting;

    public ColoredCube(Shader shader, boolean lighting) {
        super(shader);

        this.vao = new Vao(generateVertices());
        this.vao.setColors(generateColors());
        this.vao.fillBuffer();
        this.lighting = lighting;
    }

    public ColoredCube(Shader shader) {
        this(shader, true);
    }

    private float[] generateColors() {
        float[] colors = new float[36 * 3];
        for (var i = 0; i < 36 * 3; i += 3) {
            colors[i] = 1.0f;
            colors[i + 1] = 1.0f;
            colors[i + 2] = 1.0f;
        }
        return colors;
    }


    @Override
    public void update(float dt) {
        rotate(dt * 1f, new Vector3f(1.0f, 0.3f, 0.5f));
    }

    @Override
    public void render(Shader s, RenderPass currentPass) {
        if(!lighting && currentPass == RenderPass.FORWARD_LIGHT_PASS) {
            return;
        }
        s.setMat4f("model", getModelMatrix());
        vao.bind();
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
