package com.discordsrv.heads.renderers;

import com.discordsrv.heads.SkinUtil;

import java.awt.image.BufferedImage;

/**
 * Renders a full orthographic 3D player in a neutral standing pose from the given {@link View}.
 * Output is ARGB with a transparent background and 2 px of padding.
 *
 * The base layer is drawn fully opaque (as Minecraft does), with the overlay layer on top.
 * Slim (Alex) skins get 3 px wide arms, attached to the torso.
 */
public class PlayerRenderer implements Renderer {

    /** Turned 35° toward the player's left, looking down 15°. */
    public static final View LEFT = new View(35, 15);
    /** Turned 35° toward the player's right, looking down 15°. */
    public static final View RIGHT = new View(-35, 15);

    // Overlay boxes are slightly expanded so they sit just outside the base layer (same as Minecraft)
    private static final double HAT_EXPAND   = 0.5;
    private static final double LAYER_EXPAND = 0.25;

    private final View view;
    private final boolean slim;

    public PlayerRenderer(View view, boolean slim) {
        this.view = view;
        this.slim = slim;
    }

    @Override
    public BufferedImage render(BufferedImage texture) {
        return buildModel(texture, slim).render(view, true, 2);
    }

    /**
     * Each box is given by its bounds and the top-left corner of its standard Minecraft UV layout.
     */
    private static BoxModel buildModel(BufferedImage tex, boolean slim) {
        boolean old = SkinUtil.isOldSkin(tex);
        int arm = slim ? 3 : 4; // arm width
        BufferedImage opaque = BoxModel.opaque(tex);
        BoxModel model = new BoxModel();

        // HEAD (8×8×8)
        model.addBase(opaque, -4, 24, -4, 4, 32, 4, 0, 0, false);
        // BODY (8×12×4)
        model.addBase(opaque, -4, 12, -2, 4, 24, 2, 16, 16, false);
        // RIGHT ARM (4×12×4, or 3×12×4 if slim, player's right = -X side)
        model.addBase(opaque, -4 - arm, 12, -2, -4, 24, 2, 40, 16, false);
        // RIGHT LEG (4×12×4)
        model.addBase(opaque, -4, 0, -2, 0, 12, 2, 0, 16, false);

        // Old skins have no left limb textures: mirror the right ones
        if (old) {
            model.addBase(opaque, 4, 12, -2, 8, 24, 2, 40, 16, true);
            model.addBase(opaque, 0, 0, -2, 4, 12, 2, 0, 16, true);
        } else {
            model.addBase(opaque, 4, 12, -2, 4 + arm, 24, 2, 32, 48, false);
            model.addBase(opaque, 0, 0, -2, 4, 12, 2, 16, 48, false);
        }

        // HAT (helm). Old skins with a fully opaque hat region don't use it (Minecraft's legacy transparency rule)
        if (!old || !SkinUtil.isOpaque(tex, 32, 0, 32, 16)) {
            model.addOverlay(tex, -4, 24, -4, 4, 32, 4, 32, 0, false, HAT_EXPAND);
        }

        if (!old) {
            // JACKET
            model.addOverlay(tex, -4, 12, -2, 4, 24, 2, 16, 32, false, LAYER_EXPAND);
            // RIGHT SLEEVE
            model.addOverlay(tex, -4 - arm, 12, -2, -4, 24, 2, 40, 32, false, LAYER_EXPAND);
            // LEFT SLEEVE
            model.addOverlay(tex, 4, 12, -2, 4 + arm, 24, 2, 48, 48, false, LAYER_EXPAND);
            // RIGHT PANTS
            model.addOverlay(tex, -4, 0, -2, 0, 12, 2, 0, 32, false, LAYER_EXPAND);
            // LEFT PANTS
            model.addOverlay(tex, 0, 0, -2, 4, 12, 2, 0, 48, false, LAYER_EXPAND);
        }

        return model;
    }
}
