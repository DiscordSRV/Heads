package com.discordsrv.heads.renderers;

import com.discordsrv.heads.SkinUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders the flat front of the full body, or of the bust (head plus the upper half of the torso and arms).
 *
 * Base canvas (NONE / OVERLAY), body 16 × 32 px, bust 16 × 14 px:
 *   y= 0...7  head row   (head at x=4..11)
 *   y= 8..19  torso row  (right arm x=0..3, torso x=4..11, left arm x=12..15; slim arms are 3 px, against the torso)
 *   y=20..31  legs row   (right leg x=4..7, left leg x=8..11)
 * The bust stops after the upper 6 px of the torso row.
 *
 * SCALED canvas: the base canvas scaled {@value #SCALE}×, with {@link HeadRenderer#HELM_INSET} px of room
 * at the top for the helm, which is drawn that much larger than the head on every side.
 * Body 128 × 260 px, bust 128 × 116 px.
 */
public class BodyRenderer implements Renderer {

    private static final int SCALE = 8;

    private final HelmetMode helmetMode;
    // Arm width: slim (Alex) skins have 3 px arms, drawn against the torso
    private final int arm;
    private final boolean bust;

    public static BodyRenderer body(HelmetMode helmetMode, boolean slim) {
        return new BodyRenderer(helmetMode, slim, false);
    }

    public static BodyRenderer bust(HelmetMode helmetMode, boolean slim) {
        return new BodyRenderer(helmetMode, slim, true);
    }

    private BodyRenderer(HelmetMode helmetMode, boolean slim, boolean bust) {
        this.helmetMode = helmetMode;
        this.arm = slim ? 3 : 4;
        this.bust = bust;
    }

    @Override
    public BufferedImage render(BufferedImage texture) {
        boolean hasHelm = helmetMode != HelmetMode.NONE && SkinUtil.hasHelmet(texture);
        if (helmetMode != HelmetMode.SCALED) {
            return renderFlat(texture, hasHelm);
        }

        BufferedImage flat = renderFlat(texture, false);
        int inset = HeadRenderer.HELM_INSET;
        int width = flat.getWidth() * SCALE, height = flat.getHeight() * SCALE;
        BufferedImage canvas = new BufferedImage(width, inset + height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.drawImage(flat, 0, inset, width, height, null);
        if (hasHelm) {
            int helmSize = 8 * SCALE + inset * 2;
            g.drawImage(HeadRenderer.helm(texture), 4 * SCALE - inset, 0, helmSize, helmSize, null);
        }
        g.dispose();
        return canvas;
    }

    private BufferedImage renderFlat(BufferedImage texture, boolean hasHelm) {
        boolean old = SkinUtil.isOldSkin(texture);
        int torsoH = bust ? 6 : 12;
        BufferedImage canvas = new BufferedImage(16, bust ? 14 : 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        g.drawImage(HeadRenderer.face(texture), 4, 0, null);
        if (hasHelm) g.drawImage(HeadRenderer.helm(texture), 4, 0, null);

        drawPart(g, texture, old, 44, 20, 44, 36, arm, torsoH, 4 - arm, 8); // right arm
        drawPart(g, texture, old, 20, 20, 20, 36, 8, torsoH, 4, 8);         // torso
        drawLeftPart(g, texture, old, 36, 52, 52, 52, 44, 20, arm, torsoH, 12, 8); // left arm
        if (!bust) {
            drawPart(g, texture, old, 4, 20, 4, 36, 4, 12, 4, 20);              // right leg
            drawLeftPart(g, texture, old, 20, 52, 4, 52, 4, 20, 4, 12, 8, 20);  // left leg
        }

        g.dispose();
        return canvas;
    }

    /** Draws the w × h base layer region at (u, v) to (x, y), then on new skins its overlay layer region at (ou, ov). */
    private static void drawPart(Graphics2D g, BufferedImage texture, boolean old,
                                 int u, int v, int ou, int ov, int w, int h, int x, int y) {
        g.drawImage(texture.getSubimage(u, v, w, h), x, y, null);
        if (!old) g.drawImage(texture.getSubimage(ou, ov, w, h), x, y, null);
    }

    /**
     * Like {@link #drawPart}, but old skins have no left limbs, so they get the matching right limb's base layer
     * at (ru, rv), mirrored.
     */
    private static void drawLeftPart(Graphics2D g, BufferedImage texture, boolean old,
                                     int u, int v, int ou, int ov, int ru, int rv, int w, int h, int x, int y) {
        if (old) {
            g.drawImage(SkinUtil.flipHorizontally(texture.getSubimage(ru, rv, w, h)), x, y, null);
        } else {
            drawPart(g, texture, false, u, v, ou, ov, w, h, x, y);
        }
    }
}
