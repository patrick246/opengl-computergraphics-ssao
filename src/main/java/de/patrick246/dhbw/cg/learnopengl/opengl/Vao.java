package de.patrick246.dhbw.cg.learnopengl.opengl;

import lombok.Getter;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL43C.*;

public class Vao {
    @Getter
    private int vaoId;
    private float[] vertices;
    private float[] colors;
    private float[] textureCoords;
    private float[] normals;

    public Vao(float[] vertices) {
        this.vaoId = glGenVertexArrays();
        this.vertices = vertices;
    }

    public Vao fillBuffer() {
        glBindVertexArray(vaoId);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        int length = vertices.length + getLength(colors) + getLength(textureCoords) + getLength(normals);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(length);
        buffer.put(vertices);
        if (colors != null) {
            buffer.put(colors);
        }
        if (textureCoords != null) {
            buffer.put(textureCoords);
        }
        if(normals != null) {
            buffer.put(normals);
        }

        buffer.flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * 4, vertices.length * 4);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, vertices.length * 4 + getLength(colors) * 4);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 3,GL_FLOAT, false, 3 * 4, vertices.length * 4 + getLength(colors) * 4 + getLength(textureCoords) * 4);
        glEnableVertexAttribArray(3);
        glBindVertexArray(0);
        glDeleteBuffers(vbo);
        return this;
    }

    private int getLength(float[] array) {
        if (array == null) {
            return 0;
        }
        return array.length;
    }

    public Vao setColors(float[] colors) {
        this.colors = colors;
        return this;
    }

    public Vao setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
        return this;
    }

    public Vao setNormals(float[] normals) {
        this.normals = normals;
        return this;
    }

    public void bind() {
        glBindVertexArray(vaoId);
    }
}
