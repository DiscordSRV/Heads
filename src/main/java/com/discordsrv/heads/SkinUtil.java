package com.discordsrv.heads;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class SkinUtil {

    private SkinUtil() {}

    public static boolean isOldSkin(BufferedImage texture) {
        return texture.getHeight() == 32;
    }

    /**
     * Guesses whether a skin uses the slim (Alex) model from its texture, for when there's no profile metadata.
     * Slim arms are 3 px wide, leaving the last 2 columns of each arm's classic UV region unused (transparent).
     */
    public static boolean isSlim(BufferedImage texture) {
        if (isOldSkin(texture) || !texture.getColorModel().hasAlpha()) return false;
        return isTransparent(texture, 54, 20, 2, 12) && isTransparent(texture, 46, 52, 2, 12);
    }

    /** Whether every pixel in the region is fully transparent. */
    public static boolean isTransparent(BufferedImage texture, int x, int y, int w, int h) {
        for (int ty = y; ty < y + h; ty++) {
            for (int tx = x; tx < x + w; tx++) {
                if (alpha(texture.getRGB(tx, ty)) != 0) return false;
            }
        }
        return true;
    }

    /** Whether every pixel in the region is fully opaque. */
    public static boolean isOpaque(BufferedImage texture, int x, int y, int w, int h) {
        for (int ty = y; ty < y + h; ty++) {
            for (int tx = x; tx < x + w; tx++) {
                if (alpha(texture.getRGB(tx, ty)) != 0xFF) return false;
            }
        }
        return true;
    }

    public static int alpha(int argb) {
        return (argb >>> 24);
    }

    public static boolean hasHelmet(BufferedImage texture) {
        if (!texture.getColorModel().hasAlpha()) return false;

        // sample colors from the top-left of the skin to find the most common "background" color
        Map<Integer, AtomicInteger> colors = new HashMap<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                colors.computeIfAbsent(texture.getRGB(x, y), k -> new AtomicInteger()).incrementAndGet();
            }
        }
        int backgroundColor = colors.entrySet().stream().max(Comparator.comparingInt(o -> o.getValue().get())).get().getKey();

        // check each helm pixel to see if there's any non-transparent pixels that also don't match the background
        for (int x = 40; x < 48; x++) {
            for (int y = 8; y < 16; y++) {
                int color = texture.getRGB(x, y);
                if (color != backgroundColor && alpha(color) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static BufferedImage scale(BufferedImage image, int width) {
        float heightScaling = (float) image.getHeight() / image.getWidth();
        BufferedImage scaled = new BufferedImage(width, (int) (width * heightScaling), image.getType());
        Graphics2D g = scaled.createGraphics();
        g.drawImage(image, 0, 0, scaled.getWidth(), scaled.getHeight(), null);
        g.dispose();
        return scaled;
    }

    /**
     * Resizes an image to the given width with a box filter: each output pixel averages every source pixel it
     * covers, weighted by coverage. Colors are averaged premultiplied by alpha, so transparent pixels don't darken
     * edges. Unlike {@link #scale}, shrinking anti-aliases edges instead of stairstepping them, and enlarging only
     * blends where source pixels meet.
     */
    public static BufferedImage smoothScale(BufferedImage image, int width) {
        int sw = image.getWidth(), sh = image.getHeight();
        int height = Math.max(1, (int) (width * ((float) sh / sw)));
        double fx = (double) sw / width, fy = (double) sh / height;
        int[] src = image.getRGB(0, 0, sw, sh, null, 0, sw);
        int[] out = new int[width * height];

        for (int y = 0; y < height; y++) {
            double y0 = y * fy, y1 = y0 + fy;
            int syEnd = Math.min(sh, (int) Math.ceil(y1));
            for (int x = 0; x < width; x++) {
                double x0 = x * fx, x1 = x0 + fx;
                int sxEnd = Math.min(sw, (int) Math.ceil(x1));
                double a = 0, r = 0, g = 0, b = 0, total = 0;
                for (int sy = (int) y0; sy < syEnd; sy++) {
                    double wy = Math.min(y1, sy + 1) - Math.max(y0, sy);
                    for (int sx = (int) x0; sx < sxEnd; sx++) {
                        double w = wy * (Math.min(x1, sx + 1) - Math.max(x0, sx));
                        int argb = src[sy * sw + sx];
                        double pa = alpha(argb) * w;
                        a += pa;
                        r += ((argb >> 16) & 0xFF) * pa;
                        g += ((argb >> 8) & 0xFF) * pa;
                        b += (argb & 0xFF) * pa;
                        total += w;
                    }
                }
                int oa = (int) Math.round(a / total);
                if (oa == 0) continue;
                out[y * width + x] = (oa << 24)
                        | ((int) Math.round(r / a) << 16)
                        | ((int) Math.round(g / a) << 8)
                        | (int) Math.round(b / a);
            }
        }

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaled.setRGB(0, 0, width, height, out, 0, width);
        return scaled;
    }

    public static BufferedImage flipHorizontally(BufferedImage image) {
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, image.getType());
        Graphics2D g = flipped.createGraphics();
        g.drawImage(image, w, 0, -w, h, null);
        g.dispose();
        return flipped;
    }

    public static int applyShading(int argb, float brightness) {
        if (brightness >= 1.0f) return argb;
        int a = alpha(argb);
        int r = Math.round(((argb >> 16) & 0xFF) * brightness);
        int g = Math.round(((argb >> 8) & 0xFF) * brightness);
        int b = Math.round((argb & 0xFF) * brightness);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
