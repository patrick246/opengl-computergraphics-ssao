package de.patrick246.dhbw.cg.learnopengl.opengl;

import de.patrick246.dhbw.cg.learnopengl.IOUtil;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43C.*;


public class Texture {

    private int textureId;

    public Texture(String texture) {
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

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        int format = (nrChannels[0] == 3 ? GL_RGB : GL_RGBA);
        glTexImage2D(GL_TEXTURE_2D, 0, format, width[0], height[0], 0, format, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(imageBuffer);
    }

    public void bind(int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
}
