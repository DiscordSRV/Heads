package com.discordsrv.heads.renderers;

import com.discordsrv.heads.Heads;
import com.discordsrv.heads.renderers.SkullRenderer.Side;
import org.jetbrains.annotations.Nullable;

public enum AvatarType {

    HEAD,
    OVERLAY,
    HELM,
    BUST,
    BUST_OVERLAY,
    BUST_HELM,
    BODY,
    BODY_OVERLAY,
    BODY_HELM,
    SKULL,
    SKULL_HELM,
    SKULL_RIGHT,
    SKULL_RIGHT_HELM,
    SKULL_ISOMETRIC,
    SKULL_ISOMETRIC_HELM,
    SKULL_ISOMETRIC_RIGHT,
    SKULL_ISOMETRIC_RIGHT_HELM,
    PLAYER,
    PLAYER_RIGHT;

    /** Output width in px when the request doesn't give a size. Full players are tall and thin, so they get more. */
    public int defaultSize() {
        return switch (this) {
            case PLAYER, PLAYER_RIGHT -> 256;
            default -> Heads.DEFAULT_SIZE;
        };
    }

    /**
     * Creates the renderer for this type. {@code yaw} and {@code pitch} (degrees, nullable) override the
     * camera angles of 3D renders; any angle left null keeps the type's left/right default.
     * Giving either angle for an oblique skull switches it to an orthographic render.
     * {@code slim} selects 3 px wide arms for renders that include them.
     */
    public Renderer createRenderer(@Nullable Double yaw, @Nullable Double pitch, boolean slim) {
        return switch (this) {
            case HEAD                       -> new HeadRenderer(HelmetMode.NONE);
            case OVERLAY                    -> new HeadRenderer(HelmetMode.OVERLAY);
            case HELM                       -> new HeadRenderer(HelmetMode.SCALED);
            case BUST                       -> BodyRenderer.bust(HelmetMode.NONE, slim);
            case BUST_OVERLAY               -> BodyRenderer.bust(HelmetMode.OVERLAY, slim);
            case BUST_HELM                  -> BodyRenderer.bust(HelmetMode.SCALED, slim);
            case BODY                       -> BodyRenderer.body(HelmetMode.NONE, slim);
            case BODY_OVERLAY               -> BodyRenderer.body(HelmetMode.OVERLAY, slim);
            case BODY_HELM                  -> BodyRenderer.body(HelmetMode.SCALED, slim);
            case SKULL                      -> skull(false, Side.LEFT, false, yaw, pitch);
            case SKULL_HELM                 -> skull(true, Side.LEFT, false, yaw, pitch);
            case SKULL_RIGHT                -> skull(false, Side.RIGHT, false, yaw, pitch);
            case SKULL_RIGHT_HELM           -> skull(true, Side.RIGHT, false, yaw, pitch);
            case SKULL_ISOMETRIC            -> skull(false, Side.LEFT, true, yaw, pitch);
            case SKULL_ISOMETRIC_HELM       -> skull(true, Side.LEFT, true, yaw, pitch);
            case SKULL_ISOMETRIC_RIGHT      -> skull(false, Side.RIGHT, true, yaw, pitch);
            case SKULL_ISOMETRIC_RIGHT_HELM -> skull(true, Side.RIGHT, true, yaw, pitch);
            case PLAYER                     -> new PlayerRenderer(PlayerRenderer.LEFT.with(yaw, pitch), slim);
            case PLAYER_RIGHT               -> new PlayerRenderer(PlayerRenderer.RIGHT.with(yaw, pitch), slim);
        };
    }

    private static Renderer skull(boolean helm, Side side, boolean isometric, @Nullable Double yaw, @Nullable Double pitch) {
        if (!isometric && yaw == null && pitch == null) return SkullRenderer.oblique(helm, side);
        View defaults = side == Side.LEFT ? SkullRenderer.ISOMETRIC_LEFT : SkullRenderer.ISOMETRIC_RIGHT;
        return SkullRenderer.orthographic(helm, defaults.with(yaw, pitch));
    }
}
