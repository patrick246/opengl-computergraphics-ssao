package de.patrick246.dhbw.cg.learnopengl.renderable;

import de.patrick246.dhbw.cg.learnopengl.RenderPass;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;

public interface Renderable {
    Shader getShader();
    void render(Shader s, RenderPass currentPass);
}
