package com.discordsrv.heads.services.textures;

public enum AvatarType {

    /**
     * Just the head of the skin
     */
    HEAD(false),

    /**
     * The head of the skin with the helm overlaid
     */
    OVERLAY(true),

    /**
     * The head of the skin with the helm overlaid after being scaled up 1.5x, on a transparent background
     */
    HELM(true);

    private final boolean helmet;

    AvatarType(boolean helmet) {
        this.helmet = helmet;
    }

    public boolean hasHelmet() {
        return helmet;
    }

}
