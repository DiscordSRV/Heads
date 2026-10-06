package com.discordsrv.heads.services;

import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.textures.TextureSupplier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import static com.discordsrv.heads.Heads.GSON;

public class CraftHeadService implements ProfileSupplier, TextureSupplier {

    @Override
    public Profile resolve(String username) throws IOException {
        return resolveProfile(username);
    }

    @Override
    public Profile resolve(UUID uuid) throws IOException {
        return resolveProfile(uuid.toString());
    }

    private Profile resolveProfile(String target) throws IOException {
        return GSON.fromJson(Services.get("https://crafthead.net/profile/" + target).body(), Profile.class);
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        return ImageIO.read(Services.get("https://crafthead.net/skin/" + textureId).stream());
    }

}
