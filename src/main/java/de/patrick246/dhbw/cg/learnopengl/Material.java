package de.patrick246.dhbw.cg.learnopengl;

import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.opengl.Texture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Material {
    private Vector3f ambient;
    private Texture diffuse;
    private Texture specular;
    private float shininess;



    public void apply(Shader shader) {
        diffuse.bind(0);
        specular.bind(1);
        shader.setVec3f("material.ambient", ambient);
        shader.setInt("material.diffuse", 0);
        shader.setInt("material.specular", 1);
        shader.setFloat("material.shininess", shininess);
    }
}
