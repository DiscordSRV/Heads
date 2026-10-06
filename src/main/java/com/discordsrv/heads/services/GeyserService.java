package com.discordsrv.heads.services;

import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.SkinData;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.UUID;

/**
 * Resolves skins of Bedrock players joining through Geyser/Floodgate, using the GeyserMC global API.
 */
public class GeyserService {

    /**
     * Whether the UUID is a Floodgate UUID, which is all zeros except for the player's XUID in the lower 64 bits.
     */
    public static boolean isFloodgate(UUID uuid) {
        return uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() != 0;
    }

    /**
     * The player's profile, or null if GeyserMC doesn't have a skin cached for them.
     * The profile's username is null, since only the skin is known.
     */
    public Profile resolve(UUID uuid) throws IOException {
        long xuid = uuid.getLeastSignificantBits();
        JsonObject root = JsonParser.parseString(Services.get("https://api.geysermc.org/v2/skin/" + xuid).body()).getAsJsonObject();
        if (!root.has("value")) return null;
        return new Profile(uuid, null, SkinData.deserializeBase64(root.get("value").getAsString()));
    }

}
