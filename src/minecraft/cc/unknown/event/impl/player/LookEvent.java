package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class LookEvent extends Event {

    /** The yaw angle (horizontal direction) of the player's viewing direction. */
    private float yaw;

    /** The pitch angle (vertical direction) of the player's viewing direction. */
    private float pitch;

    /**
     * Constructs a new {@code LookEvent} with the specified yaw and pitch angles.
     *
     * @param yaw the yaw angle (horizontal direction) of the player's viewing direction
     * @param pitch the pitch angle (vertical direction) of the player's viewing direction
     */
    public LookEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Returns the yaw angle (horizontal direction) of the player's viewing direction.
     *
     * @return the yaw angle of the player's viewing direction
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Returns the pitch angle (vertical direction) of the player's viewing direction.
     *
     * @return the pitch angle of the player's viewing direction
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the yaw angle (horizontal direction) of the player's viewing direction.
     *
     * @param yaw the yaw angle to set
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Sets the pitch angle (vertical direction) of the player's viewing direction.
     *
     * @param pitch the pitch angle to set
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}