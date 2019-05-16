package de.patrick246.dhbw.cg.learnopengl.opengl;

import de.patrick246.dhbw.cg.learnopengl.IOUtil;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL43C.*;

public class Cubemap {
    private int id;
    public Cubemap(List<String> textureFaces) {
        if(textureFaces.size() != 6) {
            throw new RuntimeException("A cubemap needs exactly six face textures");
        }

        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);

        for(int i = 0; i < textureFaces.size(); i++) {
            loadTextureFace(textureFaces.get(i), i);
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }

    private void loadTextureFace(String texture, int face) {
        ByteBuffer imageBuffer;
        try {
            imageBuffer = IOUtil.ioResourceToByteBuffer(texture,8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int[] width = new int[1];
        int[] height = new int[1];
        int[] nrChannels = new int[1];
        ByteBuffer data = STBImage.stbi_load_from_memory(imageBuffer, width, height, nrChannels, 0);

        int format = (nrChannels[0] == 3 ? GL_RGB : GL_RGBA);

        glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0, format, width[0], height[0], 0, format, GL_UNSIGNED_BYTE, data);
    }

    public Cubemap bind(Shader shader, String name, int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        shader.setInt(name, 0);
        return this;
    }
}
