package com.discordsrv.heads.services.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface TextureSupplier {

    BufferedImage getTexture(String textureId) throws IOException;

}
