package com.discordsrv.heads.services;

import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.ProfileSupplier;
import com.discordsrv.heads.services.textures.TextureSupplier;
import io.javalin.http.HttpResponseException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.discordsrv.heads.Heads.DEBUG;

public class Services<T extends ProfileSupplier & TextureSupplier> implements ProfileSupplier, TextureSupplier {

    private final List<T> suppliers = new LinkedList<>();

    @SafeVarargs
    public Services(T... suppliers) {
        this.suppliers.addAll(List.of(suppliers));
    }

    @Override
    public Profile resolve(String username) throws IOException {
        for (T supplier : suppliers) {
            try {
                Profile profile = supplier.resolve(username);
                if (DEBUG) System.out.println("[" + serviceName(supplier) + "] Resolved username " + username + " to profile " + profile);
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
        for (T supplier : suppliers) {
            try {
                Profile profile = supplier.resolve(uuid);
                if (DEBUG) System.out.println("[" + serviceName(supplier) + "] Resolved UUID " + uuid + " to profile " + profile);
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
        for (T supplier : suppliers) {
            try {
                BufferedImage texture = supplier.getTexture(textureId);
                if (DEBUG) System.out.println("[" + serviceName(supplier) + "] Retrieved texture " + textureId);
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
