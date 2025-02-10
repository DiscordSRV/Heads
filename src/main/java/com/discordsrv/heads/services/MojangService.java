package com.discordsrv.heads.services;

import alexh.weak.Dynamic;
import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.profiles.SkinData;
import com.discordsrv.heads.services.textures.TextureSupplier;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonParseException;
import io.javalin.http.NotFoundResponse;
import net.jodah.expiringmap.ExpiringMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.discordsrv.heads.Heads.GSON;
import static com.discordsrv.heads.Heads.uuidString;

public class MojangService implements ProfileSupplier, TextureSupplier {

    private final Map<String, UUID> usernameUuidCache = ExpiringMap.builder().expiration(1, TimeUnit.HOURS).build();
    private final Map<UUID, Profile> uuidProfileCache = ExpiringMap.builder().expiration(1, TimeUnit.HOURS).build();

    @Override
    public Profile resolve(String username) throws IOException {
        if (usernameUuidCache.containsKey(username)) return resolve(usernameUuidCache.get(username));

        HttpRequest request = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + username);
        if (request.code() == 404) throw new NotFoundResponse();
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        UUID uuid = uuidString((String) GSON.fromJson(request.body(), Map.class).get("id"));
        usernameUuidCache.put(username, uuid);
        return resolve(uuid);
    }

    @Override
    public Profile resolve(UUID uuid) throws IOException {
        if (uuidProfileCache.containsKey(uuid)) return uuidProfileCache.get(uuid);

        HttpRequest request = HttpRequest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
        if (request.code() == 404) throw new NotFoundResponse();
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        Dynamic root = Dynamic.from(GSON.fromJson(request.body(), Map.class));
        String username = root.get("name").asString();
        SkinData skinData = root.get("properties").children()
                .filter(d -> d.get("name").asString().equals("textures"))
                .map(d -> d.get("value").asString())
                .map(SkinData::deserializeBase64)
                .findFirst().orElseThrow(() -> new JsonParseException("Could not find profile textures"));
        Profile profile = new Profile(uuid, username, skinData);
        uuidProfileCache.put(uuid, profile);
        return profile;
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        HttpRequest request = HttpRequest.get("https://textures.minecraft.net/texture/" + textureId);
        if (request.code() / 100 != 2) throw new IOException("Invalid status code " + request.code() + " @ " + request.url());
        return ImageIO.read(request.stream());
    }

}
