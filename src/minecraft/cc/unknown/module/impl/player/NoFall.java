package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.world.AirCollideEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.enums.EnumFacing;
import net.minecraft.util.vec.AxisAlignedBB;

@Register(name = "NoFall", category = Category.Player)
public class NoFall extends Module {
	private boolean handling;
	private ModeValue mode = new ModeValue("Mode", "Legit", "Legit", "Packet", "Grim");

	public NoFall() {
		this.registerSetting(mode);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (e.isPre()) {
			switch (mode.getMode()) {
			case "Grim":
				if (mc.player.fallDistance >= 3) {
					mc.player.motionX *= 0.2D;
					mc.player.motionZ *= 0.2D;

					float distance = mc.player.fallDistance;

					if (MoveUtil.isOnGround(2)) {
						if (distance > 2) {
							MoveUtil.strafe(0.19f);
						}

						if (distance > 3 && MoveUtil.getSpeed() < 0.2) {
							e.setOnGround(true);
							distance = 0;
						}
					}

					mc.player.fallDistance = distance;
				}
				break;
			case "Legit":
				if (!PlayerUtil.inGame() || mc.player.dimension == -1)
					return;

				ItemStack item = mc.player.getHeldItem();

				if (inPosition() && holdWaterBucket()) {
					handling = true;
				}

				if (handling && item != null) {
					if (containsItem(item, Items.water_bucket) && mc.player.isCollidedVertically) {
						MovingObjectPosition object = mc.objectMouseOver;
						if (object.typeOfHit == MovingObjectType.BLOCK && object.sideHit == EnumFacing.UP) {
							setItem(item);
						}
					}

					if (mc.player.onGround || mc.player.motionY > 0.0D) {
						if (containsItem(item, Items.bucket)) {
							setItem(item);
						}

						handling = false;
					}
				}

				break;
			case "Packet":
				mc.getNetHandler().sendQueue(new CPacketPlayer(true));
				break;
			}
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isReceive()) {
			if (mode.is("Grim")) {
				if (e.getPacket() instanceof SPacketPlayerPosLook) {
					handling = true;
				}
			}
		}
	}
	
	@EventLink
	public void onAirCollide(AirCollideEvent e) {
		switch(mode.getMode()) {
		case "Grim":
			if (mc.player.fallDistance >= 3.0f && !handling) {
				if (mc.world.getBlockState(e.getPos()).getBlock() instanceof BlockAir && !mc.player.isSneaking()) {
		            final double x = e.getPos().getX(), y = e.getPos().getY(), z = e.getPos().getZ();
	
		            if (y < mc.player.posY) {
		                e.setReturnValue(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
		            }
		        }
			}
		}
	}

	private boolean inPosition() {
		if (mc.player.motionY < -0.6D && !mc.player.onGround && !mc.player.capabilities.isFlying
				&& !mc.player.capabilities.isCreativeMode && !handling) {
			BlockPos playerPos = mc.player.getPosition();

			for (int i = 1; i < 3; ++i) {
				BlockPos blockPos = playerPos.down(i);
				Block block = mc.world.getBlockState(blockPos).getBlock();
				if (block.isBlockSolid(mc.world, blockPos, EnumFacing.UP)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean holdWaterBucket() {
		if (containsItem(mc.player.getHeldItem(), Items.water_bucket)) {
			return true;
		} else {
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
				if (containsItem(mc.player.inventory.mainInventory[i], Items.water_bucket)) {
					mc.player.inventory.currentItem = i;
					return true;
				}
			}
			return false;
		}
	}

	private boolean containsItem(ItemStack itemStack, Item item) {
		return itemStack != null && itemStack.getItem() == item;
	}

	private boolean setItem(ItemStack item) {
		return mc.playerController.sendUseItem(mc.player, mc.world, item);
	}
}
