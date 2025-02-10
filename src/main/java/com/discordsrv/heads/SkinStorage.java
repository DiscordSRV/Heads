package com.discordsrv.heads;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkinStorage {

    private final File directory;

    public SkinStorage(File directory) {
        this.directory = directory;
        if (new File("/.dockerenv").exists()) {
            // don't bother caching textures unless running in container
            this.directory.mkdirs();
        }
    }

    public BufferedImage getTexture(String textureId) throws IOException {
        File file = getFile(textureId);
        return file.exists() ? ImageIO.read(file) : null;
    }

    public void saveTexture(String textureId, BufferedImage texture) throws IOException {
        if (!directory.exists()) return;
        ImageIO.write(texture, "png", getFile(textureId));
    }

    public File getFile(String textureId) {
        return new File(directory, textureId + ".png");
    }

}
