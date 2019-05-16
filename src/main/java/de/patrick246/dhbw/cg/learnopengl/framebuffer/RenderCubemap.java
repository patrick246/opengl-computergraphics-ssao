package de.patrick246.dhbw.cg.learnopengl.framebuffer;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL43C.*;

public class RenderCubemap implements FramebufferAttachable {

    private int id;

    public RenderCubemap(Vector2i size) {
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        for(int i = 0; i < 6; i++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,0, GL_DEPTH_COMPONENT,size.x, size.y, 0, GL_DEPTH_COMPONENT,GL_FLOAT, 0);
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

    }

    @Override
    public void attachToFramebuffer() {
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, id, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
    }

    public void bind(int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
    }
}
