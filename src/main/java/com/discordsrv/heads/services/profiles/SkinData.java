package com.discordsrv.heads.services.profiles;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.UUID;

import static com.discordsrv.heads.Heads.GSON;
import static com.discordsrv.heads.Heads.uuidString;

public record SkinData(UUID profileId, String profileName, String textureId, long timestamp) implements JsonDeserializer<SkinData> {

    public static SkinData deserializeBase64(String base64) {
        return deserializeJson(new String(Base64.getDecoder().decode(base64)));
    }
    public static SkinData deserializeJson(String json) {
        return GSON.fromJson(json, SkinData.class);
    }

    @Override
    public SkinData deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        /**
         *         Dynamic root = Dynamic.from(jsonElement.getAsJsonObject().asMap());
         *         return new SkinData(
         *                 UUIDUtil.fromString(root.get("profileId").asString()),
         *                 root.get("profileName").asString(),
         *                 root.dget("textures.SKIN.url").asString().replace("http://textures.minecraft.net/texture/", ""),
         *                 root.get("timestamp").as(Long.class)
         *         );
         */

        JsonObject root = jsonElement.getAsJsonObject();
        return new SkinData(
                uuidString(root.get("profileId").getAsString()),
                root.get("profileName").getAsString(),
                root.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString().replace("http://textures.minecraft.net/texture/", ""),
                root.get("timestamp").getAsLong()
        );
    }

}
