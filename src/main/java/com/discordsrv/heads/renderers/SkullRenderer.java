package com.discordsrv.heads.renderers;

import com.discordsrv.heads.SkinUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders a 3D view of the skull with optional helm layer overlay.
 *
 * Projections:
 *   Oblique:      cabinet projection showing the front, top, and one side face; the front face is drawn
 *                 undistorted and receding edges go back at half scale, 45° up toward the visible side.
 *                 Output is square. Sides:
 *                   LEFT:  shows the player's left side (viewer's right)
 *                   RIGHT: shows the player's right side (viewer's left)
 *   Orthographic: rotated to any {@link View} and rendered with {@link BoxModel}. {@link #ISOMETRIC_LEFT}
 *                 and {@link #ISOMETRIC_RIGHT} give a true isometric projection, where all three axes
 *                 are 120° apart and every visible face is the same rhombus. Output keeps its natural
 *                 aspect ratio (√3:2 width:height for isometric).
 *
 * Works at SCALE=32 internally, so each skin pixel spans 32 px along every axis of the oblique skull and the
 * front face is 256 px across.
 * Faces are drawn on a canvas sized for a full helm, then cropped to the drawn pixels of the
 * head plus helm. The plain skull uses the same crop as the helm variant, so the two line up
 * exactly for any given skin.
 *
 * The helm is drawn as a box expanded by EXPAND units on every side, so it sits just outside the head.
 *
 * Oblique shading:
 *   Top face:   100% (lit from above)
 *   Front face: 100%
 *   Side face:  60.8% (partially shadowed)
 */
public class SkullRenderer implements Renderer {

    // Looking down at atan(1/√2) ≈ 35.26° from a 45° corner makes all three axes foreshorten equally
    private static final double ISOMETRIC_PITCH = Math.toDegrees(Math.atan(1 / Math.sqrt(2)));
    public static final View ISOMETRIC_LEFT = new View(45, ISOMETRIC_PITCH);
    public static final View ISOMETRIC_RIGHT = new View(-45, ISOMETRIC_PITCH);

    private static final int SCALE = 32;
    private static final double EXPAND = 0.5;

    private static final float SHADE_TOP   = 1.0f;
    private static final float SHADE_FRONT = 1.0f;
    private static final float SHADE_SIDE  = 0.608f;

    public enum Side { LEFT, RIGHT }

    private final boolean helm;
    private final Side side; // oblique only
    private final View view; // orthographic only

    // Oblique: +1 when the visible side is on the viewer's right (LEFT), -1 when it's on the viewer's left (RIGHT)
    private final int dir;

    // Oblique: canvas size and offset that fit the helm box, in px
    private final int canvasW, canvasH;
    private final double originX, originY;

    public static SkullRenderer oblique(boolean helm, Side side) {
        return new SkullRenderer(helm, side, null);
    }

    public static SkullRenderer orthographic(boolean helm, View view) {
        return new SkullRenderer(helm, null, view);
    }

    private SkullRenderer(boolean helm, Side side, View view) {
        this.helm = helm;
        this.side = side;
        this.view = view;
        this.dir = side == Side.RIGHT ? -1 : 1;

        double lo = -EXPAND, hi = 8 + EXPAND;
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (double x : new double[]{lo, hi}) {
            for (double y : new double[]{lo, hi}) {
                for (double d : new double[]{lo, hi}) {
                    double[] p = projectRaw(x, y, d);
                    minX = Math.min(minX, p[0]);
                    maxX = Math.max(maxX, p[0]);
                    minY = Math.min(minY, p[1]);
                    maxY = Math.max(maxY, p[1]);
                }
            }
        }
        this.originX = -minX * SCALE;
        this.originY = -minY * SCALE;
        this.canvasW = (int) Math.ceil((maxX - minX) * SCALE);
        this.canvasH = (int) Math.ceil((maxY - minY) * SCALE);
    }

    @Override
    public BufferedImage render(BufferedImage texture) {
        return view != null ? renderOrthographic(texture) : renderOblique(texture);
    }

    @Override
    public boolean antialiased() {
        return true;
    }

    private BufferedImage renderOrthographic(BufferedImage texture) {
        BoxModel model = new BoxModel();
        model.addBase(BoxModel.opaque(texture), -4, -4, -4, 4, 4, 4, 0, 0, false);
        if (!SkinUtil.hasHelmet(texture)) {
            BufferedImage canvas = model.render(view, false, 0);
            return crop(canvas, contentBounds(canvas), false);
        }

        // Both variants are cropped to the head plus helm so they line up exactly
        model.addOverlay(texture, -4, -4, -4, 4, 4, 4, 32, 0, false, EXPAND);
        BufferedImage withHelm = model.render(view, true, 0);
        return crop(helm ? withHelm : model.render(view, false, 0), contentBounds(withHelm), false);
    }

    private BufferedImage renderOblique(BufferedImage texture) {
        BufferedImage canvas = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);

        // Skull faces
        drawBox(canvas, texture, 8, 0, 8, 8, side == Side.LEFT ? 16 : 0, 8, 0);

        // Helm overlay (skip transparent pixels). The helm is always drawn when the skin has one so that
        // both variants are cropped to the same bounds and line up exactly; it's only kept for the helm variant.
        if (SkinUtil.hasHelmet(texture)) {
            BufferedImage withHelm = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = withHelm.createGraphics();
            g.drawImage(canvas, 0, 0, null);
            g.dispose();
            drawBox(withHelm, texture, 40, 0, 40, 8, side == Side.LEFT ? 48 : 32, 8, EXPAND);
            return crop(helm ? withHelm : canvas, contentBounds(withHelm), true);
        }

        return crop(canvas, contentBounds(canvas), true);
    }

    /**
     * Bounding box {minX, minY, maxX, maxY} of non-transparent pixels, or null if there are none.
     * Helm layers often only cover some faces, so the drawn extent varies per skin.
     */
    private static int[] contentBounds(BufferedImage canvas) {
        int minX = canvas.getWidth(), minY = canvas.getHeight(), maxX = -1, maxY = -1;
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                if (SkinUtil.alpha(canvas.getRGB(x, y)) == 0) continue;
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
        }
        return maxX < 0 ? null : new int[]{minX, minY, maxX, maxY};
    }

    /**
     * Crops to the given bounds, centered on a square canvas if {@code square} is set.
     */
    private static BufferedImage crop(BufferedImage canvas, int[] bounds, boolean square) {
        if (bounds == null) return canvas;

        int w = bounds[2] - bounds[0] + 1, h = bounds[3] - bounds[1] + 1;
        int outW = w, outH = h;
        if (square) outW = outH = Math.max(w, h);
        BufferedImage out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(canvas.getSubimage(bounds[0], bounds[1], w, h), (outW - w) / 2, (outH - h) / 2, null);
        g.dispose();
        return out;
    }

    /**
     * Draws the three visible faces of an 8×8×8 box, expanded by {@code e} units on every side.
     *
     * Box coordinates: x = 0..8 (player's right to left, i.e. viewer's left to right when facing the player),
     * y = 0..8 (top to bottom), d = 0..8 (front to back).
     */
    private void drawBox(BufferedImage canvas, BufferedImage tex,
                         int topU, int topV, int frontU, int frontV, int sideU, int sideV,
                         double e) {
        double lo = -e, hi = 8 + e;

        // Top (y=lo): tex y=0 is back, y=7 is front
        drawFace(canvas, tex.getSubimage(topU, topV, 8, 8), SHADE_TOP,
                lo, lo, hi,
                hi, lo, hi,
                hi, lo, lo,
                lo, lo, lo
        );
        if (side == Side.LEFT) {
            // Player's left side (x=hi): tex x=0 is front, x=7 is back
            drawFace(canvas, tex.getSubimage(sideU, sideV, 8, 8), SHADE_SIDE,
                    hi, lo, lo,
                    hi, lo, hi,
                    hi, hi, hi,
                    hi, hi, lo
            );
        } else {
            // Player's right side (x=lo): tex x=0 is back, x=7 is front
            drawFace(canvas, tex.getSubimage(sideU, sideV, 8, 8), SHADE_SIDE,
                    lo, lo, hi,
                    lo, lo, lo,
                    lo, hi, lo,
                    lo, hi, hi
            );
        }
        // Front (d=lo)
        drawFace(canvas, tex.getSubimage(frontU, frontV, 8, 8), SHADE_FRONT,
                lo, lo, lo,
                hi, lo, lo,
                hi, hi, lo,
                lo, hi, lo
        );
    }

    /**
     * Rasterize an 8×8 texture onto the quad whose corners correspond to texture
     * corners (0,0), (8,0), (8,8), (0,8), each given as box coordinates (x, y, d).
     */
    private void drawFace(BufferedImage canvas, BufferedImage face, float shade,
                          double x0, double y0, double d0,
                          double x1, double y1, double d1,
                          double x2, double y2, double d2,
                          double x3, double y3, double d3) {
        BoxModel.Face f = new BoxModel.Face();
        f.uv = new double[][]{
                { 0, 0 },
                { 8, 0 },
                { 8, 8 },
                { 0, 8 }
        };
        f.sv = new double[][]{
                project(x0, y0, d0),
                project(x1, y1, d1),
                project(x2, y2, d2),
                project(x3, y3, d3)
        };
        f.tex = face;
        f.shade = shade;
        f.rasterize(canvas);
    }

    private double[] project(double x, double y, double d) {
        double[] p = projectRaw(x, y, d);
        return new double[]{
                originX + p[0] * SCALE,
                originY + p[1] * SCALE,
                0
        };
    }

    /**
     * Oblique projection of box coordinates to unscaled, unanchored screen coordinates (y down).
     * Front face undistorted; depth goes up toward the visible side at half scale.
     */
    private double[] projectRaw(double x, double y, double d) {
        return new double[]{ x + dir * d / 2, y - d / 2 };
    }
}
