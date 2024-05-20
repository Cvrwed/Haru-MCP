package cc.unknown.utils.player.rotation;

import java.util.Random;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.utils.Loona;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.vec.AxisAlignedBB;
import net.minecraft.util.vec.Vec3;

public class RotationManager implements Loona {

	public RotationManager() {
		Haru.instance.getEventBus().register(this);
	}
	
	@EventLink
	public void onTick(final TickEvent e) {
        if (targetRotation != null) {
            keepLength--;
            if (keepLength <= 0) 
            	reset();
        }
	}

	@EventLink
	public void onPacket(final PacketEvent e) {
		final Packet<?> p = e.getPacket();
		if (e.isSend()) {
			
			if (p instanceof CPacketPlayer) {
				final CPacketPlayer wrapper = (CPacketPlayer) p;

				if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
					wrapper.yaw = targetRotation.getYaw();
					wrapper.pitch = targetRotation.getPitch();
					wrapper.rotating = true;	
				}
				
				if (wrapper.rotating) {
					serverRotation = new Rotation(wrapper.yaw, wrapper.pitch);
				}
			}
		}
	}
	
	private static Random random = new Random();

	private static int keepLength;
	private static int revTick;

	public static Rotation targetRotation;
	public static Rotation serverRotation = new Rotation(0F, 0F);
	public static float[] clientRotation = new float[]{0.0F, 0.0F};

	public static boolean keepCurrentRotation = false;

	public static Rotation getRotationsEntity(EntityLivingBase entity) {
		return getRotations(entity.posX, entity.posY + entity.getEyeHeight() - 0.4, entity.posZ);
	}
	
    public static float[] getRotationsToPosition(final double x, final double y, final double z) {
        final double deltaX = x - mc.player.posX;
        final double deltaY = y - mc.player.posY - mc.player.getEyeHeight();
        final double deltaZ = z - mc.player.posZ;
        final double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final float yaw = (float)Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        final float pitch = (float)Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));
        return new float[] { yaw, pitch };
    }

	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.player.posX,
				mc.player.getEntityBoundingBox().minY + mc.player.getEyeHeight(), mc.player.posZ);

		if (predict) {
			if (mc.player.onGround) {
				eyesPos.addVector(mc.player.motionX, 0.0, mc.player.motionZ);
			} else
				eyesPos.addVector(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
		}

		final double diffX = vec.xCoord - eyesPos.xCoord;
		final double diffY = vec.yCoord - eyesPos.yCoord;
		final double diffZ = vec.zCoord - eyesPos.zCoord;

		return new Rotation(MathHelper.wrapAngle180((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F).floatValue(),
				MathHelper.wrapAngle180(
						(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))).floatValue());
	}

	public static Vec3 getCenter(final AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5,
				bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static double getRotationDifference(final Entity entity) {
		final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

		return getRotationDifference(rotation, new Rotation(mc.player.rotationYaw, mc.player.rotationPitch));
	}

	public static double getRotationDifference(final Rotation rotation) {
		return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
	}

	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
	}

	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation,
			final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
				currentRotation.getYaw()
						+ (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
				currentRotation.getPitch()
						+ (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static void setTargetRotation(final Rotation rotation) {
		setTargetRotation(rotation, 0);
	}

	public static void setTargetRotation(final Rotation rotation, final int keep) {
		if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch()) || rotation.getPitch() > 90
				|| rotation.getPitch() < -90)
			return;

		rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
		targetRotation = rotation;
		keepLength = keep;
	}
	
    public static Rotation getRotationsNonLivingEntity(Entity entity) {
        return getRotations(entity.posX, entity.posY + (entity.getEntityBoundingBox().maxY-entity.getEntityBoundingBox().minY)*0.5, entity.posZ);
    }

	public static void reset() {
        keepLength = 0;
        targetRotation = null;
	}

	public static Rotation getRotations(Entity ent) {
		double x = ent.posX;
		double z = ent.posZ;
		double y = ent.posY + (double) (ent.getEyeHeight() / 2.0f);
		return getRotationFromPosition(x, z, y);
	}

	public static Rotation getRotations(double posX, double posY, double posZ) {
		double x = posX - mc.player.posX;
		double y = posY - (mc.player.posY + (double) mc.player.getEyeHeight());
		double z = posZ - mc.player.posZ;
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
		return new Rotation(yaw, pitch);
	}

	public static Rotation getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - mc.player.posX;
		double zDiff = z - mc.player.posZ;
		double yDiff = y - mc.player.posY - 1.2;
		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
		float pitch = (float) (-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
		return new Rotation(yaw, pitch);
	}
	
    public static VecRotation faceBlock(final BlockPos blockPos) {
        if (blockPos == null)
            return null;

        VecRotation vecRotation = null;

        for(double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
            for(double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                    final Vec3 eyesPos = new Vec3(mc.player.posX, mc.player.getEntityBoundingBox().minY + mc.player.getEyeHeight(), mc.player.posZ);
                    final Vec3 posVec = new Vec3(blockPos).addVector(xSearch, ySearch, zSearch);
                    final double dist = eyesPos.distanceTo(posVec);

                    final double diffX = posVec.xCoord - eyesPos.xCoord;
                    final double diffY = posVec.yCoord - eyesPos.yCoord;
                    final double diffZ = posVec.zCoord - eyesPos.zCoord;

                    final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                    final Rotation rotation = new Rotation(
                            MathHelper.wrapAngle180((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F).floatValue(),
                            MathHelper.wrapAngle180((float) -Math.toDegrees(Math.atan2(diffY, diffXZ))).floatValue()
                    );

                    final Vec3 rotationVector = getVectorForRotation(rotation);
                    final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * dist, rotationVector.yCoord * dist,
                            rotationVector.zCoord * dist);
                    final MovingObjectPosition obj = mc.world.rayTraceBlocks(eyesPos, vector, false,
                            false, true);

                    if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        final VecRotation currentVec = new VecRotation(posVec, rotation);

                        if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }
    
    public static Vec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

	public static Rotation getTargetRotation() {
		return targetRotation;
	}

	public static Rotation getServerRotation() {
		return serverRotation;
	}
}
