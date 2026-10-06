package com.discordsrv.heads.services.profiles;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

import static com.discordsrv.heads.Heads.parseUuid;

public record Profile(UUID uuid, String username, SkinData skinData) {

    public static class Deserializer implements JsonDeserializer<Profile> {

        @Override
        public Profile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root = jsonElement.getAsJsonObject();
            UUID uuid = parseUuid(root.get("id").getAsString());
            String username = root.get("name").getAsString();
            JsonArray properties = root.getAsJsonArray("properties");
            SkinData skinData = null;
            for (JsonElement element : properties) {
                JsonObject property = element.getAsJsonObject();
                if (property.get("name").getAsString().equals("textures")) {
                    skinData = SkinData.deserializeBase64(property.get("value").getAsString());
                    break;
                }
            }
            if (skinData == null) throw new JsonParseException("texture property not found");
            return new Profile(uuid, username, skinData);
        }

    }

}
