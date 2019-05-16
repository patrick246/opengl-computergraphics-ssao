package de.patrick246.dhbw.cg.learnopengl.framebuffer;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL43C.*;


public class RenderTexture implements FramebufferAttachable {
    private int id;

    public enum Type {
        COLOR,
        DEPTH,
        DEPTH_STENCIL,
        POSITION,
        AO
    }

    private Type type;

    public RenderTexture(Type type, Vector2i textureSize) {
        this.id = glGenTextures();
        this.type = type;
        glBindTexture(GL_TEXTURE_2D, id);

        if (type == Type.COLOR) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, textureSize.x, textureSize.y, 0, GL_RGB, GL_UNSIGNED_BYTE, MemoryUtil.NULL);
        } else if(type == Type.DEPTH) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, textureSize.x, textureSize.y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        } else if (type == Type.DEPTH_STENCIL) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, textureSize.x, textureSize.y, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, MemoryUtil.NULL);
        } else if(type == Type.POSITION) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, textureSize.x, textureSize.y, 0, GL_RGB, GL_FLOAT, 0);
        } else if(type == Type.AO) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, textureSize.x, textureSize.y, 0, GL_RGB, GL_FLOAT, 0);
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = { 1.0f, 1.0f, 1.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void attachToFramebuffer() {
        if (type == Type.COLOR) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, id, 0);
        } else if(type == Type.DEPTH) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, id,0);
        } else if(type == Type.DEPTH_STENCIL) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, id, 0);
        } else if(type == Type.POSITION) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, id, 0);
        } else if(type == Type.AO) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, id, 0);
        }
    }

    public void bind(int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
