package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import cc.unknown.utils.player.MoveUtil;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class MoveEvent extends Event {

	private double x;
	private double y;
	private double z;
	private boolean saveWalk;
	private boolean disableSneak;

	/**
	 * Constructs a new SafeWalkEvent object with the specified motion values.
	 *
	 * @param x The motion in the X direction.
	 * @param y The motion in the Y direction.
	 * @param z The motion in the Z direction.
	 */
	public MoveEvent(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Retrieves the motion in the X direction.
	 *
	 * @return The motion in the X direction.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the motion in the X direction.
	 *
	 * @param motionX The new motion in the X direction.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Retrieves the motion in the Y direction.
	 *
	 * @return The motion in the Y direction.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the motion in the Y direction.
	 *
	 * @param motionY The new motion in the Y direction.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Retrieves the motion in the Z direction.
	 *
	 * @return The motion in the Z direction.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Sets the motion in the Z direction.
	 *
	 * @param motionZ The new motion in the Z direction.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Checks if the safe walk feature is enabled.
	 *
	 * @return {@code true} if the safe walk feature is enabled, {@code false}
	 *         otherwise.
	 */
	public boolean isSaveWalk() {
		return saveWalk;
	}

	/**
	 * Sets the state of the safe walk feature.
	 *
	 * @param saveWalk The new state of the safe walk feature.
	 */
	public void setSaveWalk(boolean saveWalk) {
		this.saveWalk = saveWalk;
	}

	/**
	 * Checks if sneaking is disabled.
	 *
	 * @return {@code true} if sneaking is disabled, {@code false} otherwise.
	 */
	public boolean isDisableSneak() {
		return disableSneak;
	}

	/**
	 * Sets the state of sneaking.
	 *
	 * @param disableSneak The new state of sneaking.
	 */
	public void setDisableSneak(boolean disableSneak) {
		this.disableSneak = disableSneak;
	}

	public void setSpeed(double m) {
		MoveUtil.strafe(this, m);
	}

	public void jump() {
		setY(0.42D);
		if (mc.player.isPotionActive(Potion.jump))
			setY(getY() + ((mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F));
		if (mc.player.isSprinting()) {
			float f = mc.player.rotationYaw * 0.017453292F;
			setX(getX() - (MathHelper.sin(f) * 0.2F));
			setZ(getZ() + (MathHelper.cos(f) * 0.2F));
		}
	}
}