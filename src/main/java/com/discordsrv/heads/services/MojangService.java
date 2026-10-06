package com.discordsrv.heads.services;

import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.textures.TextureSupplier;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import static com.discordsrv.heads.Heads.GSON;
import static com.discordsrv.heads.Heads.parseUuid;

public class MojangService implements ProfileSupplier, TextureSupplier {

    @Override
    public Profile resolve(String username) throws IOException {
        String body = Services.get("https://api.mojang.com/users/profiles/minecraft/" + username).body();
        UUID uuid = parseUuid(JsonParser.parseString(body).getAsJsonObject().get("id").getAsString());
        if (uuid == null) throw new NotFoundResponse();
        return resolve(uuid);
    }

    @Override
    public Profile resolve(UUID uuid) throws IOException {
        if (uuid.version() == 3) throw new BadRequestResponse("Offline mode UUID provided");

        String body = Services.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")).body();
        return GSON.fromJson(body, Profile.class);
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        return ImageIO.read(Services.get("https://textures.minecraft.net/texture/" + textureId).stream());
    }

}
