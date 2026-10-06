package com.discordsrv.heads.services.profiles;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.UUID;

import static com.discordsrv.heads.Heads.GSON;
import static com.discordsrv.heads.Heads.parseUuid;

/**
 * @param slim whether the skin uses the slim (Alex) model, with 3 px wide arms
 */
public record SkinData(UUID profileId, String profileName, String textureId, boolean slim, long timestamp) {

    public static SkinData deserializeBase64(String base64) {
        return deserializeJson(new String(Base64.getDecoder().decode(base64)));
    }
    public static SkinData deserializeJson(String json) {
        return GSON.fromJson(json, SkinData.class);
    }

    public static class Deserializer implements JsonDeserializer<SkinData> {

        @Override
        public SkinData deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = jsonElement.getAsJsonObject();
            JsonObject skin = root.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject();
            JsonObject metadata = skin.has("metadata") ? skin.getAsJsonObject("metadata") : null;
            boolean slim = metadata != null && metadata.has("model") && metadata.get("model").getAsString().equals("slim");
            return new SkinData(
                    parseUuid(root.get("profileId").getAsString()),
                    root.get("profileName").getAsString(),
                    skin.get("url").getAsString().replace("http://textures.minecraft.net/texture/", ""),
                    slim,
                    root.get("timestamp").getAsLong()
            );
        }

    }

}
