package com.discordsrv.heads.services.profiles;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

import static com.discordsrv.heads.Heads.uuidString;

public record Profile(UUID uuid, String username, SkinData skinData) {

    public static class Deserializer implements JsonDeserializer<Profile> {

        @Override
        public Profile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            /**
             *         Dynamic root = Dynamic.from(jsonElement.getAsJsonObject().asMap());
             *         UUID uuid = UUIDUtil.fromString(root.get("id").asString());
             *         String username = root.get("name").asString();
             *         SkinData skinData = root.get("properties").children()
             *                 .filter(d -> d.get("name").asString().equals("textures"))
             *                 .map(d -> d.get("value").asString())
             *                 .map(SkinData::deserializeBase64)
             *                 .findFirst().orElseThrow(() -> new JsonParseException("Could not find profile textures"));
             *         return new Profile(uuid, username, skinData);
             */

            JsonObject root = jsonElement.getAsJsonObject();
            UUID uuid = uuidString(root.get("id").getAsString());
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
