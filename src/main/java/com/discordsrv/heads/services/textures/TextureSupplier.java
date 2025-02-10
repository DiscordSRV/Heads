package com.discordsrv.heads.services.textures;

import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.SkinData;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface TextureSupplier {

    BufferedImage getTexture(String textureId) throws IOException;

    default BufferedImage getTexture(Profile profile) throws IOException {
        return getTexture(profile.skinData());
    }
    default BufferedImage getTexture(SkinData skinData) throws IOException {
        return getTexture(skinData.textureId());
    }

}
