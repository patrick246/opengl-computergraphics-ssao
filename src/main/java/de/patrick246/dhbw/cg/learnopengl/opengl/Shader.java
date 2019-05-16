package de.patrick246.dhbw.cg.learnopengl.opengl;

import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33C.*;

@Slf4j
public class Shader {
    private int programId;
    private String vertexShader;
    private String geometryShader;
    private String fragmentShader;
    private Map<String, Integer> compileTimeConstants = new HashMap<>();
    private boolean compiled = false;

    public Shader(String vertexShader, String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }

    public Shader(String vertexShader, String geometryShader, String fragmentShader) {
        this.vertexShader = vertexShader;
        this.geometryShader = geometryShader;
        this.fragmentShader = fragmentShader;
    }

    public Shader compile() {
        programId = glCreateProgram();
        int vertexShaderHandle = this.loadShader(vertexShader, GL_VERTEX_SHADER);
        int fragmentShaderHandle = this.loadShader(fragmentShader, GL_FRAGMENT_SHADER);
        if(this.geometryShader != null) {
            this.loadShader(this.geometryShader, GL_GEOMETRY_SHADER);
        }

        glLinkProgram(programId);
        int[] success = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, success);
        if(success[0] != GL_TRUE) {
            log.error("Shader linkage unsuccessful: {}", glGetProgramInfoLog(programId, 512));
            throw new RuntimeException("Shader linkage unsuccessful");
        }

        glDeleteShader(vertexShaderHandle);
        glDeleteShader(fragmentShaderHandle);
        this.compiled = true;
        return this;
    }

    public Shader setCompileTimeConstant(String name, int value) {
        this.compileTimeConstants.put(name, value);
        return this;
    }

    private int loadShader(String resource, int shaderType) {
        int handle = GL33C.glCreateShader(shaderType);
        try {
            StringBuilder source = new StringBuilder(new String(Files.readAllBytes(Paths.get(getClass().getResource(resource).toURI()))));
            for(var entry : compileTimeConstants.entrySet()) {
                source.insert(0, "#define " + entry.getKey() + " " + entry.getValue() + "\n");
            }
            source.insert(0, "#version 330 core\n");
            glShaderSource(handle, source.toString());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        glCompileShader(handle);
        int[] success = new int[1];
        glGetShaderiv(handle, GL_COMPILE_STATUS, success);
        if(success[0] == 0) {
            log.error("Shader compilation failed ({}): \"{}\"", resource, glGetShaderInfoLog(handle, 512));
            throw new RuntimeException("Shader compilation failed");
        }
        glAttachShader(programId, handle);
        return handle;
    }

    public Shader bind() {
        if(!this.compiled) {
            throw new IllegalStateException("Shader" + vertexShader + "/" + fragmentShader + " not compiled");
        }
        glUseProgram(programId);
        return this;
    }

    public Shader setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(programId, name), value ? 1 : 0);
        return this;
    }

    public Shader setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(programId, name), value);
        return this;
    }

    public Shader setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(programId, name), value);
        return this;
    }

    public Shader setMat4f(String name, Matrix4f mat) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(programId, name), false, mat.get(stack.mallocFloat(16)));
        }
        return this;
    }

    public Shader setVec3f(String name, Vector3f vec) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform3fv(glGetUniformLocation(programId, name), vec.get(stack.mallocFloat(3)));
        }
        return this;
    }

    public Shader setVec2f(String name, Vector2f vec) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform2fv(glGetUniformLocation(programId, name), vec.get(stack.mallocFloat(2)));
        }
        return this;
    }

    public int getProgramId() {
        return programId;
    }
}
