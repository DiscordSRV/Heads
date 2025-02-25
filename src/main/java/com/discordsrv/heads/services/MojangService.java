package com.discordsrv.heads.services;

import alexh.weak.Dynamic;
import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.profiles.SkinData;
import com.discordsrv.heads.services.textures.TextureSupplier;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonParseException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.discordsrv.heads.Heads.GSON;
import static com.discordsrv.heads.Heads.uuidString;

public class MojangService implements ProfileSupplier, TextureSupplier {

    @Override
    public Profile resolve(String username) throws IOException {
        HttpRequest request = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + username);
        if (request.code() == 204 || request.code() == 404) throw new NotFoundResponse();
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        UUID uuid = uuidString((String) GSON.fromJson(request.body(), Map.class).get("id"));
        return resolve(uuid);
    }

    @Override
    public Profile resolve(UUID uuid) throws IOException {
        if (uuid.version() == 3) throw new BadRequestResponse("Offline mode UUID provided");

        HttpRequest request = HttpRequest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
        if (request.code() == 204 || request.code() == 404) throw new NotFoundResponse();
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        Dynamic root = Dynamic.from(GSON.fromJson(request.body(), Map.class));
        String username = root.get("name").asString();
        SkinData skinData = root.get("properties").children()
                .filter(d -> d.get("name").asString().equals("textures"))
                .map(d -> d.get("value").asString())
                .map(SkinData::deserializeBase64)
                .findFirst().orElseThrow(() -> new JsonParseException("Could not find profile textures"));
        return new Profile(uuid, username, skinData);
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        HttpRequest request = HttpRequest.get("https://textures.minecraft.net/texture/" + textureId);
        if (request.code() == 404) throw new NotFoundResponse();
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        return ImageIO.read(request.stream());
    }

}
