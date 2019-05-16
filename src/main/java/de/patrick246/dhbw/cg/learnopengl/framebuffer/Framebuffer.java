package de.patrick246.dhbw.cg.learnopengl.framebuffer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL43C.*;

public class Framebuffer {
    private int id;
    @Getter
    private List<FramebufferAttachable> attachments = new ArrayList<>();

    public Framebuffer() {
        id = glGenFramebuffers();
    }

    public Framebuffer attach(FramebufferAttachable texture) {
        this.attachments.add(texture);
        return this;
    }

    public Framebuffer bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        this.attachments.forEach(FramebufferAttachable::attachToFramebuffer);
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Tried to bind framebuffer that isn't complete");
        }
        return this;
    }

    public Framebuffer unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        return this;
    }


}
