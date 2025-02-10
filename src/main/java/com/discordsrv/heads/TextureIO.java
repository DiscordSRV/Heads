package com.discordsrv.heads;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

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
        for (int x = 40; x < 48; x++) {
            for (int y = 8; y < 16; y++) {
                int pixel = texture.getRGB(x, y);
                if ((pixel >> 24) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public TextureIO applyHelmet(boolean scale) {
        BufferedImage helmetLayer = texture.getSubimage(40, 8, 8, 8);

        if (scale) {
            int headSize = 64; // need a canvas large enough to slightly upscale helmet
            int padding = 4; // however many pixels on the sides of the head to upscale the helmet onto

            BufferedImage scaledHead = new BufferedImage(headSize + padding * 2, headSize + padding * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = scaledHead.createGraphics();
            graphics.drawImage(head, padding, padding, headSize, headSize, null);
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
        BufferedImage scaledHead = new BufferedImage(size, size, head.getType());
        Graphics2D graphics = scaledHead.createGraphics();
        graphics.drawImage(head, 0, 0, scaledHead.getWidth(), scaledHead.getHeight(), null);
        graphics.dispose();
        head = scaledHead;
        return this;
    }

    public BufferedImage getTexture() {
        return texture;
    }
    public BufferedImage getHead() {
        return head;
    }

}
