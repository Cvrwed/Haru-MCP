package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;

public class MotionEvent extends Event {

    /** The x-coordinate associated with the event. */
    private double x;

    /** The y-coordinate associated with the event. */
    private double y;

    /** The z-coordinate associated with the event. */
    private double z;

    /** The yaw value associated with the event. */
    private float yaw;

    /** The pitch value associated with the event. */
    private float pitch;

    /** The on-ground status associated with the event. */
    private boolean onGround;

    /** The motion type (Pre or Post) associated with the event. */
    private final MotionType motionType;

    /** The player entity associated with the event. */
    private final EntityPlayerSP player;

    /**
     * Constructs a new {@code MotionEvent} with the specified parameters.
     *
     * @param motionType the motion type (Pre or Post) of the event
     * @param x the x-coordinate of the player's position
     * @param y the y-coordinate of the player's position
     * @param z the z-coordinate of the player's position
     * @param yaw the yaw value (horizontal direction) of the player's orientation
     * @param pitch the pitch value (vertical direction) of the player's orientation
     * @param onGround the on-ground status of the player
     * @param player the player entity associated with the event
     */
    public MotionEvent(MotionType motionType, double x, double y, double z, float yaw, float pitch, float lastTickYaw, float lastTickPitch, boolean onGround, EntityPlayerSP player) {
        this.motionType = motionType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.player = player;
    }

    /**
     * Gets the x-coordinate associated with the event.
     *
     * @param x The x-coordinate to set.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate associated with the event.
     *
     * @param x The x-coordinate to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate associated with the event.
     *
     * @return The y-coordinate associated with the event.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate associated with the event.
     *
     * @param y The y-coordinate to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the z-coordinate associated with the event.
     *
     * @return The z-coordinate associated with the event.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the z-coordinate associated with the event.
     *
     * @param z The z-coordinate to set.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Gets the yaw value associated with the event.
     *
     * @return The yaw value associated with the event.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw value associated with the event.
     *
     * @param yaw The yaw value to set.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the pitch value associated with the event.
     *
     * @return The pitch value associated with the event.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch value associated with the event.
     *
     * @param pitch The pitch value to set.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Checks if the on-ground status of the event is true.
     *
     * @return true if the on-ground status is true, false otherwise.
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Sets the on-ground status associated with the event.
     *
     * @param onGround The on-ground status to set.
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    /**
     * Checks if the motion type of the event is "Pre".
     *
     * @return true if the motion type is "Pre", false otherwise.
     */
    public boolean isPre() {
        return motionType == MotionType.Pre;
    }

    /**
     * Checks if the motion type of the event is "Post".
     *
     * @return true if the motion type is "Post", false otherwise.
     */
    public boolean isPost() {
        return motionType == MotionType.Post;
    }

	/**
     * Enumerates the possible motion types of a motion event (Pre or Post).
     */
    public enum MotionType {
        Pre, Post
    }
}
