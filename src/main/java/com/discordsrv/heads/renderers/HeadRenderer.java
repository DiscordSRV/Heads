package com.discordsrv.heads.renderers;

import com.discordsrv.heads.SkinUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders the front of the head.
 *
 * NONE / OVERLAY: 8 × 8 px
 * SCALED:         72 × 72 px (64 px head, with the helm drawn {@link #HELM_INSET} px larger on every side)
 */
public class HeadRenderer implements Renderer {

    /** How far a scaled helm extends past the 64 px head on every side, in px. */
    static final int HELM_INSET = 4;

    private final HelmetMode helmetMode;

    public HeadRenderer(HelmetMode helmetMode) {
        this.helmetMode = helmetMode;
    }

    @Override
    public BufferedImage render(BufferedImage texture) {
        BufferedImage head = face(texture);
        if (helmetMode == HelmetMode.NONE || !SkinUtil.hasHelmet(texture)) {
            return head;
        }

        if (helmetMode == HelmetMode.OVERLAY) {
            Graphics2D g = head.createGraphics();
            g.drawImage(helm(texture), 0, 0, null);
            g.dispose();
            return head;
        }

        int headSize = 64;
        int helmSize = headSize + HELM_INSET * 2;
        BufferedImage scaledHead = new BufferedImage(helmSize, helmSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledHead.createGraphics();
        g.drawImage(head, HELM_INSET, HELM_INSET, headSize, headSize, null);
        g.drawImage(helm(texture), 0, 0, helmSize, helmSize, null);
        g.dispose();
        return scaledHead;
    }

    /** The 8 × 8 front of the head's base layer, on black so it's fully opaque. */
    static BufferedImage face(BufferedImage texture) {
        BufferedImage face = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = face.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 8, 8);
        g.drawImage(texture.getSubimage(8, 8, 8, 8), 0, 0, null);
        g.dispose();
        return face;
    }

    /** The 8 × 8 front of the helm layer. */
    static BufferedImage helm(BufferedImage texture) {
        return texture.getSubimage(40, 8, 8, 8);
    }
}
