package cc.unknown.utils.player;

import cc.unknown.event.impl.move.MoveInputEvent;
import cc.unknown.utils.Loona;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class MoveUtil implements Loona {
	public static float[] incrementMoveDirection(float forward, float strafe) {
		if (forward == 0 && strafe == 0) {
			return new float[] { forward, strafe };
		}

		float value = Math.abs(forward != 0 ? forward : strafe);

		if (forward > 0) {
			strafe = (strafe > 0) ? 0 : ((strafe == 0) ? -value : strafe);
		} else if (forward == 0) {
			forward = (strafe > 0) ? value : -value;
		} else {
			strafe = (strafe < 0) ? 0 : ((strafe == 0) ? value : (strafe > 0) ? 0 : strafe);
		}

		return new float[] { forward, strafe };
	}
	
    public static void correctInput(MoveInputEvent e, float yaw) {
        float f1 = MathHelper.sin((mc.player.rotationYaw - yaw) * (float) Math.PI / 180.0F);
        float f2 = MathHelper.cos((mc.player.rotationYaw - yaw) * (float) Math.PI / 180.0F);
        float x = e.getStrafe() * f2 - e.getForward() * f1;
        float z = e.getForward() * f2 + e.getStrafe() * f1;

        e.setStrafe(Math.round(x));
        e.setForward(Math.round(z));
    }

	public static float getPlayerDirection() {
		float direction = mc.player.rotationYaw;

		if (mc.player.movementInput.moveForward > 0) {
			if (mc.player.movementInput.moveStrafe > 0) {
				direction -= 45;
			} else if (mc.player.movementInput.moveStrafe < 0) {
				direction += 45;
			}
		} else if (mc.player.movementInput.moveForward < 0) {
			if (mc.player.movementInput.moveStrafe > 0) {
				direction -= 135;
			} else if (mc.player.movementInput.moveStrafe < 0) {
				direction += 135;
			} else {
				direction -= 180;
			}
		} else {
			if (mc.player.movementInput.moveStrafe > 0) {
				direction -= 90;
			} else if (mc.player.movementInput.moveStrafe < 0) {
				direction += 90;
			}
		}

		return direction;
	}

	public static float direction() {
		return direction(mc.player.rotationYaw, mc.player.moveForward, mc.player.moveStrafing);
	}

	public static float direction(float rotationYaw) {
		return direction(rotationYaw, mc.player.moveForward, mc.player.moveStrafing);
	}

	public static float direction(float rotationYaw, float forward, float strafe) {
		return (float) Math.toRadians(directionYaw(rotationYaw, forward, strafe));
	}

	public static float directionYaw(float rotationYaw, float forward, float strafe) {
		if (forward < 0f)
			rotationYaw += 180f;
		float f = 1f;
		if (forward < 0f)
			f = -0.5f;
		if (forward > 0f)
			f = 0.5f;
		if (strafe > 0f)
			rotationYaw -= 90f * f;
		if (strafe < 0f)
			rotationYaw += 90f * f;
		return rotationYaw;
	}

	public static void strafe(float yaw, float speed) {
		mc.player.motionX = -Math.sin(yaw) * speed;
		mc.player.motionZ = Math.cos(yaw) * speed;
	}

	public static void strafe(float speed) {
		strafe(direction(), speed);
	}

	public static void strafeY(float speed) {
		strafe(direction(), speed);
		if (mc.player.onGround) {
			mc.player.jump();
		}
	}

	public static boolean isOnGround(double height) {
		if (!mc.world.getCollidingBoundingBoxes((Entity) mc.player,
				mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
			return true;
		}

		return false;
	}

	public static float getSpeed() {
		return getSpeed(mc.player.motionX, mc.player.motionZ);
	}

	public static float getSpeed(double motionX, double motionZ) {
		return (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
	}

	public static void updateBinds() {
		mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
		mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
		mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
		mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
		mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
		mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
	}

	public static boolean isBindsMoving() {
		return mc.player != null && (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
				|| GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
				|| GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
				|| GameSettings.isKeyDown(mc.gameSettings.keyBindLeft));
	}

	public static float getBindsDirection(float rotationYaw) {
		int moveForward = 0;
		if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
			moveForward++;
		if (GameSettings.isKeyDown(mc.gameSettings.keyBindBack))
			moveForward--;

		int moveStrafing = 0;
		if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
			moveStrafing++;
		if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
			moveStrafing--;

		boolean reversed = moveForward < 0;
		double strafingYaw = 90 * (moveForward > 0 ? .5 : reversed ? -.5 : 1);

		if (reversed)
			rotationYaw += 180.f;
		if (moveStrafing > 0)
			rotationYaw += strafingYaw;
		else if (moveStrafing < 0)
			rotationYaw -= strafingYaw;

		return rotationYaw;
	}

}
