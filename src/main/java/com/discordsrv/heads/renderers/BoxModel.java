package com.discordsrv.heads.renderers;

import com.discordsrv.heads.SkinUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A model made of textured boxes, rendered orthographically from any {@link View}.
 * Works at SCALE=8, so each skin pixel maps to an 8×8 area on the canvas when facing the camera.
 *
 * Coordinate system: +X = player's left, +Y = up, +Z = toward the player's front.
 *
 * Hidden surfaces are resolved per pixel with a depth buffer. Base boxes are drawn first,
 * then overlay boxes back-to-front so their translucent pixels blend correctly.
 */
final class BoxModel {

    static final double SCALE = 8.0;

    // Face shading factors
    private static final float SHADE_TOP    = 1.00f;
    private static final float SHADE_FRONT  = 1.00f;
    private static final float SHADE_SIDE   = 0.80f;
    private static final float SHADE_BACK   = 0.60f;
    private static final float SHADE_BOTTOM = 0.50f;

    private final List<Face> base = new ArrayList<>();
    private final List<Face> overlay = new ArrayList<>();

    void addBase(BufferedImage tex,
                 double x0, double y0, double z0,
                 double x1, double y1, double z1,
                 int u, int v, boolean mirror) {
        addBox(base, tex,
                x0, y0, z0,
                x1, y1, z1,
                u, v, mirror, 0
        );
    }

    void addOverlay(BufferedImage tex,
                    double x0, double y0, double z0,
                    double x1, double y1, double z1,
                    int u, int v, boolean mirror, double expand) {
        addBox(overlay, tex,
                x0, y0, z0,
                x1, y1, z1,
                u, v, mirror, expand
        );
    }

    /**
     * Renders the model with {@code padding} transparent px on each side. The canvas fits every face,
     * including overlay faces even when they aren't drawn, so renders with and without the overlay line up.
     */
    BufferedImage render(View view, boolean includeOverlay, int padding) {
        double yaw = -Math.toRadians(view.yaw()), pitch = Math.toRadians(view.pitch());
        double cosYaw   = Math.cos(yaw),   sinYaw   = Math.sin(yaw);
        double cosPitch = Math.cos(pitch), sinPitch = Math.sin(pitch);

        List<Face> faces = new ArrayList<>(base);
        faces.addAll(overlay);

        // Project all face vertices, compute depth
        for (Face f : faces) f.project(cosYaw, sinYaw, cosPitch, sinPitch);

        // Compute canvas bounds
        double minSX = Double.MAX_VALUE, maxSX = -Double.MAX_VALUE;
        double minSY = Double.MAX_VALUE, maxSY = -Double.MAX_VALUE;
        for (Face f : faces) {
            for (double[] sv : f.sv) {
                if (sv[0] < minSX) minSX = sv[0];
                if (sv[0] > maxSX) maxSX = sv[0];
                if (sv[1] < minSY) minSY = sv[1];
                if (sv[1] > maxSY) maxSY = sv[1];
            }
        }
        int cw = (int) Math.ceil(maxSX - minSX) + padding * 2;
        int ch = (int) Math.ceil(maxSY - minSY) + padding * 2;
        double ox = padding - minSX, oy = padding - minSY;
        for (Face f : faces) f.shift(ox, oy);

        BufferedImage canvas = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
        double[] depth = new double[cw * ch];
        Arrays.fill(depth, Double.NEGATIVE_INFINITY);

        for (Face f : base) if (f.isFrontFacing()) f.rasterize(canvas, depth);

        if (includeOverlay) {
            List<Face> visibleOverlay = new ArrayList<>();
            for (Face f : overlay) if (f.isFrontFacing()) visibleOverlay.add(f);
            visibleOverlay.sort(Comparator.comparingDouble(f -> f.avgDepth));
            for (Face f : visibleOverlay) f.rasterize(canvas, depth);
        }

        return canvas;
    }

    /** Copy of {@code tex} with every pixel fully opaque, as Minecraft draws the base skin layer. */
    static BufferedImage opaque(BufferedImage tex) {
        BufferedImage out = new BufferedImage(tex.getWidth(), tex.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < tex.getHeight(); y++) {
            for (int x = 0; x < tex.getWidth(); x++) {
                out.setRGB(x, y, tex.getRGB(x, y) | 0xFF000000);
            }
        }
        return out;
    }

    /**
     * Adds all 6 faces of a box, expanded by {@code e} on every side.
     * UVs follow Minecraft's cube layout starting at (u, v), for a box of w×h×d texture pixels:
     *
     * <pre>
     *            d     w     d     w
     *         +-----+-----+-----+
     *       d |     | top | bot |
     *         +-----+-----+-----+-----+
     *       h |right|front|left |back |
     *         +-----+-----+-----+-----+
     * </pre>
     *
     * If {@code mirror} is set, every face is flipped horizontally and the left/right faces are swapped,
     * turning a right limb's texture into a left limb's.
     */
    private static void addBox(List<Face> faces, BufferedImage tex,
                               double x0, double y0, double z0, double x1, double y1, double z1,
                               int u, int v, boolean mirror, double e) {
        int w = (int) Math.round(x1 - x0), h = (int) Math.round(y1 - y0), d = (int) Math.round(z1 - z0);
        x0 -= e; y0 -= e; z0 -= e;
        x1 += e; y1 += e; z1 += e;

        int rightU = mirror ? u + d + w : u;
        int leftU  = mirror ? u : u + d + w;

        // Front (+Z)
        faces.add(face(tex, u + d, v + d, w, h, SHADE_FRONT, mirror, 0, 0, 1,
                x0,y1,z1,  x1,y1,z1,  x1,y0,z1,  x0,y0,z1));
        // Back (-Z)
        faces.add(face(tex, u + 2 * d + w, v + d, w, h, SHADE_BACK, mirror, 0, 0, -1,
                x1,y1,z0,  x0,y1,z0,  x0,y0,z0,  x1,y0,z0));
        // Top (+Y)
        faces.add(face(tex, u + d, v, w, d, SHADE_TOP, mirror, 0, 1, 0,
                x0,y1,z0,  x1,y1,z0,  x1,y1,z1,  x0,y1,z1));
        // Bottom (-Y)
        faces.add(face(tex, u + d + w, v, w, d, SHADE_BOTTOM, mirror, 0, -1, 0,
                x0,y0,z1,  x1,y0,z1,  x1,y0,z0,  x0,y0,z0));
        // Right (-X = player's right)
        faces.add(face(tex, rightU, v + d, d, h, SHADE_SIDE, mirror, -1, 0, 0,
                x0,y1,z0,  x0,y1,z1,  x0,y0,z1,  x0,y0,z0));
        // Left (+X = player's left)
        faces.add(face(tex, leftU, v + d, d, h, SHADE_SIDE, mirror, 1, 0, 0,
                x1,y1,z1,  x1,y1,z0,  x1,y0,z0,  x1,y0,z1));
    }

    /**
     * Creates a face whose vertices correspond to texture corners (0,0), (w,0), (w,h), (0,h),
     * or (w,0), (0,0), (0,h), (w,h) when mirrored. (nx, ny, nz) is the outward normal.
     */
    private static Face face(BufferedImage tex,
                             int uvX, int uvY, int uvW, int uvH, float shade, boolean mirror,
                             double nx, double ny, double nz,
                             double x0, double y0, double z0,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3) {
        Face f = new Face();
        f.wv = new double[][]{{x0,y0,z0},{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}};
        f.normal = new double[]{nx, ny, nz};
        f.uv = mirror
                ? new double[][]{{uvW,0},{0,0},{0,uvH},{uvW,uvH}}
                : new double[][]{{0,0},{uvW,0},{uvW,uvH},{0,uvH}};
        f.tex = tex.getSubimage(uvX, uvY, uvW, uvH);
        f.shade = shade;
        return f;
    }

    // -----------------------------------------------------------------------
    // Face inner class
    // -----------------------------------------------------------------------

    static class Face {
        double[][] wv;   // world vertices [4][3]
        double[] normal; // outward world normal [3]
        double[][] uv;   // UV per vertex  [4][2]
        BufferedImage tex;
        float shade;

        double[][] sv;   // screen vertices [4][3] = {sx, sy, depth}, larger depth = closer to the viewer
        double avgDepth;
        double normalDepth; // view-space depth component of the normal; > 0 when facing the viewer

        void project(double cosYaw, double sinYaw, double cosPitch, double sinPitch) {
            sv = new double[4][3];
            double sum = 0;
            for (int i = 0; i < 4; i++) {
                double[] p = rotate(wv[i][0], wv[i][1], wv[i][2], cosYaw, sinYaw, cosPitch, sinPitch);
                sv[i][0] = p[0] * SCALE;
                sv[i][1] = -p[1] * SCALE;
                sv[i][2] = p[2];
                sum += p[2];
            }
            avgDepth = sum / 4;
            normalDepth = rotate(normal[0], normal[1], normal[2], cosYaw, sinYaw, cosPitch, sinPitch)[2];
        }

        /** Rotates by yaw around Y, then by pitch around X. Returns {x, y (up), depth (toward viewer)}. */
        private static double[] rotate(double wx, double wy, double wz,
                                       double cosYaw, double sinYaw, double cosPitch, double sinPitch) {
            double x1 =  wx * cosYaw + wz * sinYaw;
            double z1 = -wx * sinYaw + wz * cosYaw;
            double y2 = wy * cosPitch - z1 * sinPitch;
            double d  = wy * sinPitch + z1 * cosPitch;
            return new double[]{x1, y2, d};
        }

        boolean isFrontFacing() {
            return normalDepth > 1e-9;
        }

        void shift(double ox, double oy) {
            for (double[] v : sv) { v[0] += ox; v[1] += oy; }
        }

        /** Draws the face without depth testing; later faces overwrite earlier ones. */
        void rasterize(BufferedImage canvas) {
            rasterize(canvas, null);
        }

        /** Draws the face, skipping pixels behind what's already in {@code depth} (row-major, canvas-sized). */
        void rasterize(BufferedImage canvas, double[] depth) {
            rasterizeTri(canvas, depth, 0, 1, 2);
            rasterizeTri(canvas, depth, 0, 2, 3);
        }

        private void rasterizeTri(BufferedImage canvas, double[] depth, int i0, int i1, int i2) {
            double[] p0 = sv[i0], p1 = sv[i1], p2 = sv[i2];
            double[] u0 = uv[i0], u1 = uv[i1], u2 = uv[i2];
            int tw = tex.getWidth(), th = tex.getHeight();
            int cw = canvas.getWidth(), ch = canvas.getHeight();

            int minX = Math.max(0, (int) Math.min(p0[0], Math.min(p1[0], p2[0])));
            int maxX = Math.min(cw-1, (int) Math.ceil(Math.max(p0[0], Math.max(p1[0], p2[0]))));
            int minY = Math.max(0, (int) Math.min(p0[1], Math.min(p1[1], p2[1])));
            int maxY = Math.min(ch-1, (int) Math.ceil(Math.max(p0[1], Math.max(p1[1], p2[1]))));

            double denom = (p1[1]-p2[1])*(p0[0]-p2[0]) + (p2[0]-p1[0])*(p0[1]-p2[1]);
            if (Math.abs(denom) < 1e-9) return;
            double invD = 1.0 / denom;

            for (int py = minY; py <= maxY; py++) {
                for (int px = minX; px <= maxX; px++) {
                    double cx = px + 0.5, cy = py + 0.5;
                    double w0 = ((p1[1]-p2[1])*(cx-p2[0]) + (p2[0]-p1[0])*(cy-p2[1])) * invD;
                    double w1 = ((p2[1]-p0[1])*(cx-p2[0]) + (p0[0]-p2[0])*(cy-p2[1])) * invD;
                    double w2 = 1 - w0 - w1;
                    if (w0 < 0 || w1 < 0 || w2 < 0) continue;

                    double z = w0*p0[2] + w1*p1[2] + w2*p2[2];
                    int idx = py * cw + px;
                    if (depth != null && z <= depth[idx]) continue;

                    double fu = w0*u0[0] + w1*u1[0] + w2*u2[0];
                    double fv = w0*u0[1] + w1*u1[1] + w2*u2[1];
                    int tu = Math.max(0, Math.min(tw-1, (int) fu));
                    int tv = Math.max(0, Math.min(th-1, (int) fv));

                    int argb = tex.getRGB(tu, tv);
                    int alpha = SkinUtil.alpha(argb);
                    if (alpha == 0) continue;

                    argb = SkinUtil.applyShading(argb, shade);
                    if (alpha < 255) argb = blend(argb, canvas.getRGB(px, py));
                    canvas.setRGB(px, py, argb);
                    if (depth != null) depth[idx] = z;
                }
            }
        }

        /** Source-over alpha compositing of {@code src} onto {@code dst}. */
        private static int blend(int src, int dst) {
            double sa = SkinUtil.alpha(src) / 255.0, da = SkinUtil.alpha(dst) / 255.0;
            double oa = sa + da * (1 - sa);
            if (oa <= 0) return 0;
            int r = (int) Math.round((((src >> 16) & 0xFF) * sa + ((dst >> 16) & 0xFF) * da * (1 - sa)) / oa);
            int g = (int) Math.round((((src >> 8) & 0xFF) * sa + ((dst >> 8) & 0xFF) * da * (1 - sa)) / oa);
            int b = (int) Math.round(((src & 0xFF) * sa + (dst & 0xFF) * da * (1 - sa)) / oa);
            return ((int) Math.round(oa * 255) << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
