package cc.unknown.module.impl.player;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.enums.EnumFacing;
import net.minecraft.util.vec.Vec3;

@Info(name = "Scaffold", category = Category.Player)
public class Scaffold extends Module {

	private SliderValue customPitch = new SliderValue("Pitch", 90, -90, 90, 1);
	private SliderValue customYaw = new SliderValue("Yaw", 180, 0, 360, 1);
	private SliderValue keepRotation = new SliderValue("Keep rotation tick", 1, 0, 20, 1);
	private BooleanValue sprint = new BooleanValue("Sprint", true);
	private BooleanValue moveFix = new BooleanValue("Movement Fix", false);

	private Cold timer = new Cold(0);

	public Scaffold() {
		this.registerSetting(customPitch, customYaw, keepRotation, sprint);
	}

	@Override
	public void onDisable() {
		RotationManager.onDisable();
	}

	@EventLink
	public void onMotion(MotionEvent e) {
	    if (e.isPre()) {
	        placeBlock();

	        RotationManager.setStrafeFix(moveFix.isToggled(), false);

	        if (!sprint.isToggled()) {
	            mc.player.setSprinting(false);
	        }
	    }
	}

	private void placeBlock() {
		if (PlayerUtil.isAirBlock(getBlock(new BlockPos(mc.player).down()))) {
			return;
		}
		if (placeBlockSimple(new BlockPos(mc.player).down())) {
			return;
		}

		int dist = 0;
		while (dist <= 6) {
			for (int blockDist = 0; dist != blockDist; ++blockDist) {
				for (int x = blockDist; x >= 0; --x) {
					final int z = blockDist - x;
					final int y = dist - blockDist;
					if (placeBlockSimple(new BlockPos(mc.player).down(y).north(x).west(z))) {
						return;
					}
					if (placeBlockSimple(new BlockPos(mc.player).down(y).north(x).west(-z))) {
						return;
					}
					if (placeBlockSimple(new BlockPos(mc.player).down(y).north(-x).west(z))) {
						return;
					}
					if (placeBlockSimple(new BlockPos(mc.player).down(y).north(-x).west(-z))) {
						return;
					}
				}
			}
			++dist;
		}
	}

	private boolean placeBlockSimple(final BlockPos pos) {
		final Entity entity = mc.getRenderViewEntity();
		final double d0 = entity.posX;
		final double d2 = entity.posY;
		final double d3 = entity.posZ;
		final Vec3 eyesPos = new Vec3(d0, d2 + mc.player.getEyeHeight(), d3);
		for (final EnumFacing side : EnumFacing.values()) {
			if (!side.equals(EnumFacing.UP)) {
				if (!side.equals(EnumFacing.DOWN) || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
					final BlockPos neighbor = pos.offset(side);
					final EnumFacing side2 = side.getOpposite();
					if (getBlock(neighbor).canCollideCheck(mc.world.getBlockState(neighbor), false)) {
						final Vec3 hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5)
								.add(new Vec3(side2.getDirectionVec()));
						if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
							final float[] angles = getCustomRotation(neighbor, side2);
							mc.getRenderViewEntity().rotationYaw = angles[0];
							mc.getRenderViewEntity().rotationPitch = angles[1];
							mc.player.rotationYaw = angles[0];
							mc.player.rotationPitch = angles[1];
							mc.playerController.onPlayerRightClick(mc.player, mc.world,
									mc.player.getCurrentEquippedItem(), neighbor, side2, hitVec);
							mc.player.swingItem();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private Block getBlock(final BlockPos pos) {
		return mc.world.getBlockState(pos).getBlock();
	}

	// se ira mejorando con el tiempo
	private float[] getCustomRotation(final BlockPos block, final EnumFacing face) {
	    final Entity entity = mc.getRenderViewEntity();
	    final double posX = entity.posX;
	    final double posY = entity.posY + entity.getEyeHeight();
	    final double posZ = entity.posZ;

	    final double x = block.getX() + 0.5 - posX + face.getFrontOffsetX() / 2.0;
	    final double z = block.getZ() + 0.5 - posZ + face.getFrontOffsetZ() / 2.0;
	    final double y = block.getY() + 0.5;

	    final double horizontalDistance = Math.sqrt(x * x + z * z);
	    final double verticalDistance = posY - y;

	    float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
	    float pitch = (float) (Math.atan2(verticalDistance, horizontalDistance) * 180.0 / Math.PI);

	    yaw = MathHelper.wrapAngle180(yaw).floatValue();
	    pitch = MathHelper.clamp_float(pitch, -90.0f, 90.0f);

	    if (yaw < 60.0f || yaw > 0.0f || pitch < 0.0f || pitch > 90.0f) {
	        yaw = customYaw.getInputToFloat();
	        pitch = customPitch.getInputToFloat();
	    }

	    float[] rotations = new float[] { yaw, pitch };

	    RotationManager.setClientRotation(rotations, keepRotation.getInputToInt());
	    
	    return rotations;
	}

}
