package com.discordsrv.heads.renderers;

import java.awt.image.BufferedImage;

public interface Renderer {
    BufferedImage render(BufferedImage skinTexture);

    /**
     * Whether resizing the render should average pixels together, smoothing edges that would otherwise
     * stairstep. 3D renders are drawn at a high resolution for this; flat ones keep their sharp pixels.
     */
    default boolean antialiased() {
        return false;
    }
}
