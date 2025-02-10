package com.discordsrv.heads.services;

import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.textures.TextureSupplier;
import com.github.kevinsawicki.http.HttpRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import static com.discordsrv.heads.Heads.GSON;

public class CraftHeadService implements ProfileSupplier, TextureSupplier {

    public Profile resolveProfile(String target) throws IOException {
        HttpRequest request = HttpRequest.get("https://crafthead.net/profile/" + target);
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        String body = request.body();
        return GSON.fromJson(body, Profile.class);
    }
    @Override
    public Profile resolve(String username) throws IOException {
        return resolveProfile(username);
    }
    @Override
    public Profile resolve(UUID uuid) throws IOException {
        return resolveProfile(uuid.toString());
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        HttpRequest request = HttpRequest.get("https://crafthead.net/skin/" + textureId);
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        return ImageIO.read(request.stream());
    }

}
