package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class MoveInputEvent extends Event {

    /** The forward movement input. */
    private float forward;

    /** The strafe movement input. */
    private float strafe;

    /** The jump input. */
    private boolean jump;

    /** The sneak input. */
    private boolean sneak;

    /** The sneak multiplier. */
    private double sneakMultiplier;

    /**
     * Constructs a new {@code MoveInputEvent} with the specified parameters.
     *
     * @param forward the forward movement input
     * @param strafe the strafe movement input
     * @param jump the jump input
     * @param sneak the sneak input
     * @param sneakMultiplier the sneak multiplier
     */
    public MoveInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakMultiplier) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakMultiplier = sneakMultiplier;
    }

    /**
     * Returns the forward movement input.
     *
     * @return the forward movement input
     */
    public float getForward() {
        return forward;
    }

    /**
     * Sets the forward movement input.
     *
     * @param forward the forward movement input to set
     */
    public void setForward(float forward) {
        this.forward = forward;
    }

    /**
     * Returns the strafe movement input.
     *
     * @return the strafe movement input
     */
    public float getStrafe() {
        return strafe;
    }

    /**
     * Sets the strafe movement input.
     *
     * @param strafe the strafe movement input to set
     */
    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    /**
     * Returns true if the jump input is active, false otherwise.
     *
     * @return true if the jump input is active, false otherwise
     */
    public boolean isJump() {
        return jump;
    }

    /**
     * Sets the jump input.
     *
     * @param jump the jump input to set
     */
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    /**
     * Returns true if the sneak input is active, false otherwise.
     *
     * @return true if the sneak input is active, false otherwise
     */
    public boolean isSneak() {
        return sneak;
    }

    /**
     * Sets the sneak input.
     *
     * @param sneak the sneak input to set
     */
    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    /**
     * Returns the sneak multiplier.
     *
     * @return the sneak multiplier
     */
    public double getSneakMultiplier() {
        return sneakMultiplier;
    }

    /**
     * Sets the sneak multiplier.
     *
     * @param sneakMultiplier the sneak multiplier to set
     */
    public void setSneakMultiplier(double sneakMultiplier) {
        this.sneakMultiplier = sneakMultiplier;
    }
}