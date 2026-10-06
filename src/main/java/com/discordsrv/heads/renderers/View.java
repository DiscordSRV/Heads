package com.discordsrv.heads.renderers;

import org.jetbrains.annotations.Nullable;

/**
 * Camera angles for orthographic 3D renders, in degrees.
 *
 * @param yaw   how far the camera is turned around the model from the front; positive turns toward
 *              the player's left (showing their left side), negative toward their right
 * @param pitch how far the camera looks down at the model; negative looks up from below
 */
public record View(double yaw, double pitch) {

    /** This view with any non-null angles replaced. */
    public View with(@Nullable Double yaw, @Nullable Double pitch) {
        return new View(yaw != null ? yaw : this.yaw, pitch != null ? pitch : this.pitch);
    }

}
