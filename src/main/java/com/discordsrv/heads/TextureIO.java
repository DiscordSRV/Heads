package com.discordsrv.heads;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("UnusedReturnValue")
public class TextureIO {

    private final BufferedImage texture;
    private BufferedImage head = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);

    public TextureIO(BufferedImage texture) throws IOException {
        this.texture = Objects.requireNonNull(texture);

        Graphics2D graphics = head.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 8, 8);
        graphics.drawImage(texture.getSubimage(8, 8, 8, 8), 0, 0, null);
        graphics.dispose();
    }

    public boolean hasHelmet() {
        return hasHelmet(texture);
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
        int backgroundColor = colors.entrySet().stream().min(Comparator.comparingInt(o -> o.getValue().get())).get().getKey();

        // check each helm pixel to see if there's any non-transparent pixels that also don't match a detected background color
        for (int x = 40; x < 48; x++) {
            for (int y = 8; y < 16; y++) {
                int color = texture.getRGB(x, y);
                int alpha = (color >> 24) & 0xFF;
                if (color != backgroundColor && alpha != 0) {
                    return true;
                }
            }
        }

        // no color found in helm area
        return false;
    }

    public TextureIO applyHelmet(boolean scale) {
        if (!hasHelmet()) return this;
        BufferedImage helmetLayer = texture.getSubimage(40, 8, 8, 8);

        if (scale) {
            int headSize = 64; // need a canvas large enough to slightly upscale helmet
            int inset = 4; // however many pixels on the sides of the head to upscale the helmet onto
            int scaledSize = headSize + inset * 2;
            BufferedImage scaledHead = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = scaledHead.createGraphics();
            graphics.drawImage(head, inset, inset, headSize, headSize, null);
            graphics.drawImage(helmetLayer, 0, 0, scaledHead.getWidth(), scaledHead.getHeight(), null);
            graphics.dispose();
            head = scaledHead;
        } else {
            Graphics2D graphics = head.createGraphics();
            graphics.drawImage(helmetLayer, 0, 0, null);
            graphics.dispose();
        }
        return this;
    }

    public TextureIO scale(int size) {
        head = scale(head, size);
        return this;
    }
    public static BufferedImage scale(BufferedImage image, int size) {
        float heightScaling = (float) image.getHeight() / image.getWidth();
        BufferedImage scaled = new BufferedImage(size, (int) (size * heightScaling), image.getType());
        Graphics2D graphics = scaled.createGraphics();
        graphics.drawImage(image, 0, 0, scaled.getWidth(), scaled.getHeight(), null);
        graphics.dispose();
        return scaled;
    }

    public BufferedImage getTexture() {
        return texture;
    }
    public BufferedImage getHead() {
        return head;
    }

}
