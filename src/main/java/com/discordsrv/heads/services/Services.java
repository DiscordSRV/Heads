package com.discordsrv.heads.services;

import com.discordsrv.heads.SkinStorage;
import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.textures.TextureSupplier;
import io.javalin.http.HttpResponseException;
import net.jodah.expiringmap.ExpiringMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.discordsrv.heads.Heads.DEBUG;

public class Services<T extends ProfileSupplier & TextureSupplier> implements ProfileSupplier, TextureSupplier {

    private final List<T> suppliers = new LinkedList<>();
    public static SkinStorage skinStorage = new SkinStorage(new File("/storage"));
    private final Map<String, Profile> usernameProfileCache = ExpiringMap.builder().expiration(1, TimeUnit.HOURS).build();
    private final Map<UUID, Profile> uuidProfileCache = ExpiringMap.builder().expiration(1, TimeUnit.HOURS).build();

    @SafeVarargs
    public Services(T... suppliers) {
        this.suppliers.addAll(List.of(suppliers));
    }

    @Override
    public Profile resolve(String username) throws IOException {
        if (usernameProfileCache.containsKey(username)) return usernameProfileCache.get(username);

        for (T supplier : suppliers) {
            try {
                Profile profile = supplier.resolve(username);
                if (DEBUG) System.out.println("[" + serviceName(supplier) + "] Resolved username " + username + " to profile " + profile);
                usernameProfileCache.put(username, profile);
                return profile;
            } catch (HttpResponseException e) {
                throw e;
            } catch (Exception e) {
                System.err.println("[" + serviceName(supplier) + "] Failed to resolve username " + username);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Profile resolve(UUID uuid) throws IOException {
        if (uuidProfileCache.containsKey(uuid)) return uuidProfileCache.get(uuid);

        for (T supplier : suppliers) {
            try {
                Profile profile = supplier.resolve(uuid);
                if (DEBUG) System.out.println("[" + serviceName(supplier) + "] Resolved UUID " + uuid + " to profile " + profile);
                uuidProfileCache.put(uuid, profile);
                return profile;
            } catch (HttpResponseException e) {
                throw e;
            } catch (Exception e) {
                System.err.println("[" + serviceName(supplier) + "] Failed to resolve UUID " + uuid);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public BufferedImage getTexture(String textureId) throws IOException {
        BufferedImage texture = skinStorage.getTexture(textureId);
        if (texture != null) return texture;

        for (T supplier : suppliers) {
            try {
                texture = supplier.getTexture(textureId);
                if (DEBUG) System.out.println("[" + serviceName(supplier) + "] Retrieved texture " + textureId);
                skinStorage.saveTexture(textureId, texture);
                return texture;
            } catch (HttpResponseException e) {
                throw e;
            } catch (Exception e) {
                System.err.println("[" + serviceName(supplier) + "] Failed to get texture " + textureId);
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String serviceName(Object o) {
        return o.getClass().getSimpleName().replace("Service", "");
    }

}
