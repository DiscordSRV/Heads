package com.discordsrv.heads.services;

import com.discordsrv.heads.SkinStorage;
import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.textures.TextureSupplier;
import com.github.kevinsawicki.http.HttpRequest;
import io.javalin.http.HttpResponseException;
import io.javalin.http.NotFoundResponse;
import net.jodah.expiringmap.ExpiringMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.discordsrv.heads.Heads.DEBUG;

/**
 * Tries each supplier in order until one succeeds, caching resolved profiles and stored textures.
 * HTTP errors meant for the client (such as 404) are passed on immediately instead of trying the next supplier.
 */
public class Services<T extends ProfileSupplier & TextureSupplier> implements ProfileSupplier, TextureSupplier {

    private final List<T> suppliers;
    private final SkinStorage skinStorage = new SkinStorage(new File("/storage"));
    private final Map<String, Profile> usernameProfileCache = ExpiringMap.builder().expiration(1, TimeUnit.HOURS).build();
    private final Map<UUID, Profile> uuidProfileCache = ExpiringMap.builder().expiration(1, TimeUnit.HOURS).build();

    @SafeVarargs
    public Services(T... suppliers) {
        this.suppliers = List.of(suppliers);
    }

    @Override
    public Profile resolve(String username) {
        Profile profile = usernameProfileCache.get(username);
        if (profile != null) return profile;

        profile = first("username " + username, supplier -> supplier.resolve(username));
        if (profile != null) usernameProfileCache.put(username, profile);
        return profile;
    }

    @Override
    public Profile resolve(UUID uuid) {
        Profile profile = uuidProfileCache.get(uuid);
        if (profile != null) return profile;

        profile = first("UUID " + uuid, supplier -> supplier.resolve(uuid));
        if (profile != null) uuidProfileCache.put(uuid, profile);
        return profile;
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        BufferedImage texture = skinStorage.getTexture(textureId);
        if (texture != null) return texture;

        texture = first("texture " + textureId, supplier -> supplier.getTexture(textureId));
        if (texture != null) skinStorage.saveTexture(textureId, texture);
        return texture;
    }

    /**
     * The result of the first supplier that succeeds, or null if they all fail.
     * {@code what} describes what's being looked up, for logging.
     */
    private <R> R first(String what, Lookup<T, R> lookup) {
        for (T supplier : suppliers) {
            String name = supplier.getClass().getSimpleName().replace("Service", "");
            try {
                R result = lookup.apply(supplier);
                if (DEBUG) System.out.println("[" + name + "] Resolved " + what);
                return result;
            } catch (HttpResponseException e) {
                throw e;
            } catch (Exception e) {
                System.err.println("[" + name + "] Failed to resolve " + what);
                e.printStackTrace();
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface Lookup<T, R> {
        R apply(T supplier) throws IOException;
    }

    /**
     * Sends a GET request, throwing {@link NotFoundResponse} if there's no content
     * and {@link IOException} for any other unsuccessful status.
     */
    static HttpRequest get(String url) throws IOException {
        HttpRequest request = HttpRequest.get(url);
        int code = request.code();
        if (code == 204 || code == 404) throw new NotFoundResponse();
        if (code / 100 != 2) throw new IOException("Invalid status code " + code + " @ " + request.url());
        return request;
    }

}
