package cc.unknown.utils.player;

import cc.unknown.utils.Loona;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.MathHelper;

public enum CombatUtil implements Loona {
	instance;

	public boolean canTarget(Entity entity) {
	    if (entity != null && entity != mc.player) {
	        boolean isTeam = isTeam(mc.player);

	        return !(entity instanceof EntityArmorStand) &&
	               ((entity instanceof EntityPlayer && !isTeam && entity.isInvisible() && !(entity instanceof EntityAnimal)) ||
	                !(entity instanceof EntityMob) ||
	                (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isEntityAlive()));
	    } else {
	        return false;
	    }
	}

	public boolean isTeam(EntityPlayer player, Entity entity) {
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getTeam() != null && player.getTeam() != null) {
			Character entity_3 = entity.getDisplayName().getFormattedText().charAt(3);
			Character player_3 = player.getDisplayName().getFormattedText().charAt(3);
			Character entity_2 = entity.getDisplayName().getFormattedText().charAt(2);
			Character player_2 = player.getDisplayName().getFormattedText().charAt(2);
			boolean isTeam = false;
			if (entity_3.equals(player_3) && entity_2.equals(player_2)) {
				isTeam = true;
			} else {
				Character entity_1 = entity.getDisplayName().getFormattedText().charAt(1);
				Character player_1 = player.getDisplayName().getFormattedText().charAt(1);
				Character entity_0 = entity.getDisplayName().getFormattedText().charAt(0);
				Character player_0 = player.getDisplayName().getFormattedText().charAt(0);
				if (entity_1.equals(player_1) && Character.isDigit(0) && entity_0.equals(player_0)) {
					isTeam = true;
				}
			}

			return isTeam;
		} else {
			return true;
		}
	}

	public float rotsToFloat(final float[] rots, final int m) {
		if (m == 1) {
			return rots[0];
		}
		if (m == 2) {
			return rots[1] + 4.0f;
		}
		return -1.0f;
	}

	public void aim(final Entity en, final float offset) {
		if (en != null) {
			final float[] rots = getTargetRotations(en);
			if (rots != null) {
				final float yaw = rotsToFloat(rots, 1);
				final float pitch = rotsToFloat(rots, 2) + 4.0f + offset;
				mc.player.rotationYaw = yaw;
				mc.player.rotationPitch = pitch;
			}
		}
	}

	public float[] getTargetRotations(final Entity en) {
		if (en == null) {
			return null;
		}
		final double diffX = en.posX - mc.player.posX;
		double diffY;
		if (en instanceof EntityLivingBase) {
			final EntityLivingBase x = (EntityLivingBase) en;

			diffY = x.posY + x.getEyeHeight() * 0.9 - (mc.player.posY + mc.player.getEyeHeight());
		} else {
			diffY = (en.getEntityBoundingBox().minY + en.getEntityBoundingBox().maxY) / 2.0
					- (mc.player.posY + mc.player.getEyeHeight());
		}
		final double diffZ = en.posZ - mc.player.posZ;
		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
		final float pitch = (float) (-(Math.atan2(diffY, MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)) * 180.0
				/ Math.PI));
		return new float[] { mc.player.rotationYaw + MathHelper.wrapAngle180(yaw - mc.player.rotationYaw).floatValue(),
				mc.player.rotationPitch + MathHelper.wrapAngle180(pitch - mc.player.rotationPitch).floatValue() };
	}

	public void aimAt(float pitch, float yaw, float fuckedYaw, float fuckedPitch, double speed) {
		float[] gcd = getPatchedRots(new float[] { yaw, pitch + ((int) fuckedPitch / 360) * 360 },
				new float[] { mc.player.prevRotationYaw, mc.player.prevRotationPitch });
		float cappedYaw = maxAngleChange(mc.player.prevRotationYaw, gcd[0], (float) speed);
		float cappedPitch = maxAngleChange(mc.player.prevRotationPitch, gcd[1], (float) speed);
		mc.player.rotationPitch = cappedPitch;
		mc.player.rotationYaw = cappedYaw;
	}

	public float[] getPatchedRots(final float[] currentRots, final float[] prevRots) {
		final float yawDif = currentRots[0] - prevRots[0];
		final float pitchDif = currentRots[1] - prevRots[1];
		final double gcd = mouseSens();

		currentRots[0] -= (float) (yawDif % gcd);
		currentRots[1] -= (float) (pitchDif % gcd);
		return currentRots;
	}

	public float maxAngleChange(final float prev, final float now, final float maxTurn) {
		float dif = MathHelper.wrapAngle180(now - prev).floatValue();
		if (dif > maxTurn)
			dif = maxTurn;
		if (dif < -maxTurn)
			dif = -maxTurn;
		return prev + dif;
	}

	public double mouseSens() {
		final float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float pow = sens * sens * sens * 8.0F;
		return pow * 0.15D;
	}
	
	public int getPing() {
		return mc.getNetHandler().getPlayerInfo(mc.player.getUniqueID()) != null
				? mc.getNetHandler().getPlayerInfo(mc.player.getUniqueID()).getResponseTime()
				: 0;
	}
}
