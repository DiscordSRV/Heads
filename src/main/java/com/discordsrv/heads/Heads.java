package com.discordsrv.heads;

import com.discordsrv.heads.renderers.AvatarType;
import com.discordsrv.heads.renderers.Renderer;
import com.discordsrv.heads.services.CraftHeadService;
import com.discordsrv.heads.services.GeyserService;
import com.discordsrv.heads.services.MojangService;
import com.discordsrv.heads.services.Services;
import com.discordsrv.heads.services.profiles.Profile;
import com.discordsrv.heads.services.profiles.SkinData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Header;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Heads {

    public static boolean DEBUG = false;
    public static final int DEFAULT_SIZE = 64;
    public static final int MAX_SIZE = 512;
    public static final String DEFAULT_HEAD_TEXTURE_ID = "9f954e93fe1640f47e916c26622eacf92fd4586371f5f07fd9a7ddedaf4"; // Steve

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Profile.class, new Profile.Deserializer())
            .registerTypeAdapter(SkinData.class, new SkinData.Deserializer())
            .create();

    public static final Services<?> services = new Services<>(
            new MojangService(),
            new CraftHeadService()
    );

    public static void main(String[] args) {
        Javalin.create(config -> {
            config.showJavalinBanner = false;
            // The frontend (see frontend/), built into the jar by Gradle. Next.js fingerprints _next/static files, so they can be cached forever
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/_next/static";
                staticFiles.directory = "static/_next/static";
                staticFiles.headers = Map.of(Header.CACHE_CONTROL, "public, max-age=31536000, immutable");
            });
            config.staticFiles.add("static");
            config.requestLogger.http((ctx, executionTimeMs) -> {
                if (ctx.status().equals(HttpStatus.FOUND)) return;
                System.out.println(MessageFormat.format("{0}ms \t{1} [{2}] {3} {4} @ {5}",
                        Math.round(executionTimeMs),
                        clientIp(ctx),
                        clientName(ctx),
                        ctx.status().getCode(),
                        ctx.status().getMessage(),
                        ctx.fullUrl()
                ));
            });
            config.router.apiBuilder(() -> {
                get("head.png", ctx -> {
                    // legacy DiscordSRV heads proxy: ?texture=&uuid=&name=&overlay
                    AvatarType type = ctx.queryParam("overlay") != null ? AvatarType.OVERLAY : AvatarType.HEAD;
                    String uuid = param(ctx, "uuid");
                    String target = Stream.of(param(ctx, "texture"), uuid != null ? parseUuid(uuid) : null, param(ctx, "name"))
                            .filter(Objects::nonNull).findFirst().orElseThrow(BadRequestResponse::new).toString();
                    ctx.redirect(target + "/" + type.name().toLowerCase());
                });
                path("{target}", () -> {
                    avatar("head", AvatarType.HEAD);
                    avatar("overlay", AvatarType.OVERLAY);
                    avatar("helm", AvatarType.HELM);

                    avatar("bust/overlay", AvatarType.BUST_OVERLAY);
                    avatar("bust/helm", AvatarType.BUST_HELM);
                    avatar("bust", AvatarType.BUST);

                    avatar("body/overlay", AvatarType.BODY_OVERLAY);
                    avatar("body/helm", AvatarType.BODY_HELM);
                    avatar("body", AvatarType.BODY);

                    avatar("skull/isometric/left/helm", AvatarType.SKULL_ISOMETRIC_HELM);
                    avatar("skull/isometric/left", AvatarType.SKULL_ISOMETRIC);
                    avatar("skull/isometric/right/helm", AvatarType.SKULL_ISOMETRIC_RIGHT_HELM);
                    avatar("skull/isometric/right", AvatarType.SKULL_ISOMETRIC_RIGHT);
                    avatar("skull/isometric/helm", AvatarType.SKULL_ISOMETRIC_HELM);
                    avatar("skull/isometric", AvatarType.SKULL_ISOMETRIC);
                    avatar("skull/left/helm", AvatarType.SKULL_HELM);
                    avatar("skull/left", AvatarType.SKULL);
                    avatar("skull/right/helm", AvatarType.SKULL_RIGHT_HELM);
                    avatar("skull/right", AvatarType.SKULL_RIGHT);
                    avatar("skull/helm", AvatarType.SKULL_HELM);
                    avatar("skull", AvatarType.SKULL);

                    avatar("player/left", AvatarType.PLAYER);
                    avatar("player/right", AvatarType.PLAYER_RIGHT);
                    avatar("player", AvatarType.PLAYER);

                    avatar("texture", null);
                });
            });
        }).start(7070);
    }

    /**
     * Registers {@code path} and {@code path/{size}}, rendering the given type, or the raw texture if it's null.
     */
    private static void avatar(String path, @Nullable AvatarType avatarType) {
        int defaultSize = avatarType != null ? avatarType.defaultSize() : DEFAULT_SIZE;
        get(path + "/{size}", ctx -> handle(ctx, avatarType, ctx.pathParamAsClass("size", Integer.class).getOrDefault(defaultSize)));
        get(path, ctx -> handle(ctx, avatarType, defaultSize));
    }

    private static void handle(Context ctx, @Nullable AvatarType avatarType, int size) {
        String target = ctx.pathParam("target");
        Double yaw = angleParam(ctx, "yaw", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        Double pitch = angleParam(ctx, "pitch", -90, 90);
        try {
            String textureId = DEFAULT_HEAD_TEXTURE_ID;
            Profile profile = null;
            if (target.length() <= 16) {
                profile = services.resolve(target);
            } else if (target.length() == 32 || target.length() == 36) {
                UUID uuid = parseUuid(target);
                if (uuid != null && (uuid.version() == 4 || GeyserService.isFloodgate(uuid))) profile = services.resolve(uuid);
            } else {
                textureId = target;
            }
            if (profile != null) textureId = profile.skinData().textureId();

            BufferedImage texture = services.getTexture(textureId);
            BufferedImage result;
            if (avatarType != null) {
                // Prefer the profile's declared model; texture-only requests have to guess from the texture
                boolean slim = profile != null ? profile.skinData().slim() : SkinUtil.isSlim(texture);
                Renderer renderer = avatarType.createRenderer(yaw, pitch, slim);
                result = renderer.render(texture);
                int width = Math.min(size, MAX_SIZE);
                if (result.getWidth() != width) {
                    result = renderer.antialiased() ? SkinUtil.smoothScale(result, width) : SkinUtil.scale(result, width);
                }
            } else {
                // Raw textures are only scaled by whole multiples of the 64 px texture width
                int step = 64;
                result = SkinUtil.scale(texture, Math.clamp(((size + step / 2) / step) * step, step, MAX_SIZE));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(result, "png", out);
            ctx.contentType("image/png");
            ctx.result(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String clientIp(Context ctx) {
        String ip = ctx.header("CF-Connecting-IP");
        if (ip == null) ip = ctx.header("X-Forwarded-For");
        return ip != null ? ip : ctx.ip();
    }

    private static String clientName(Context ctx) {
        String userAgent = ctx.userAgent();
        if (userAgent == null) return "no UA";
        return userAgent.contains("+https://discordapp.com") ? "Discord" : userAgent;
    }

    /**
     * An optional query parameter, or null if it's blank or an unfilled "{name}" placeholder.
     */
    @Nullable
    private static String param(Context ctx, String name) {
        String v = ctx.queryParam(name);
        return (v != null && !v.isBlank() && !v.equals("{" + name + "}")) ? v : null;
    }

    /**
     * Parses an optional angle query parameter in degrees, rejecting values that aren't finite or are outside [min, max].
     */
    @Nullable
    private static Double angleParam(Context ctx, String name, double min, double max) {
        String value = param(ctx, name);
        if (value == null) return null;
        double angle;
        try {
            angle = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BadRequestResponse(name + " must be a number");
        }
        if (!Double.isFinite(angle)) throw new BadRequestResponse(name + " must be a finite number");
        if (angle < min || angle > max) throw new BadRequestResponse(name + " must be between " + min + " and " + max);
        return angle;
    }

    /**
     * Parses a dashed or non-dashed UUID, or returns null if it's neither length.
     */
    @Nullable
    public static UUID parseUuid(String uuid) {
        if (uuid.length() == 32) uuid = uuid.replaceFirst("(.{8})(.{4})(.{4})(.{4})(.{12})", "$1-$2-$3-$4-$5");
        return uuid.length() == 36 ? UUID.fromString(uuid) : null;
    }
}
