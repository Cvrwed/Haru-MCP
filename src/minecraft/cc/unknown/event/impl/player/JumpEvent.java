package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class JumpEvent extends Event { 
    /** The yaw angle (horizontal direction) associated with the jump event. */
    private float yaw;

    /**
     * Constructs a new {@code JumpEvent} with the specified parameters.
     *
     * @param yaw the yaw angle (horizontal direction) of the jump
     */
    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Returns the yaw angle (horizontal direction) of the jump event.
     *
     * @return the yaw angle (horizontal direction) of the jump event
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw angle (horizontal direction) of the jump event.
     *
     * @param yaw the yaw angle (horizontal direction) of the jump event
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}