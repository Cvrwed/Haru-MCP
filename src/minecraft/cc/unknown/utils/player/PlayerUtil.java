package cc.unknown.utils.player;

import org.lwjgl.input.Mouse;

import cc.unknown.utils.Loona;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSnow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.chat.ChatComponentText;

public class PlayerUtil implements Loona {

	public static void send(final Object message, final Object... objects) {
		if (inGame()) {
			final String format = String.format(message.toString(), objects);
			mc.player.addChatMessage(new ChatComponentText("" + format));
		}
	}

	public static boolean inGame() {
		return mc.player != null && mc.world != null;
	}

	public static boolean isMoving() {
		return mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F;
	}

	public static boolean tryingToCombo() {
		return Mouse.isButtonDown(0) && Mouse.isButtonDown(1);
	}

	public static boolean lookingAtPlayer(EntityPlayer v, EntityPlayer e, double m) {
		double deltaX = e.posX - v.posX;
		double deltaY = e.posY - v.posY + v.getEyeHeight();
		double deltaZ = e.posZ - v.posZ;
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
		return distance < m;
	}

	public static double fovFromEntity(Entity en) {
		return ((double) (mc.player.rotationYaw - fovToEntity(en)) % 360.0D + 540.0D) % 360.0D - 180.0D;
	}

    public static double PitchFromEntity(Entity en, float f) {
        return (double) (mc.player.rotationPitch - pitchToEntity(en, f));
    }

    public static float pitchToEntity(Entity ent, float f) {
        double x = mc.player.getDistanceToEntity(ent);
        double y = mc.player.posY - (ent.posY + f);
        double pitch = (((Math.atan2(x, y) * 180.0D) / Math.PI));
        return (float) (90 - pitch);
    }

	public static float fovToEntity(Entity ent) {
		double x = ent.posX - mc.player.posX;
		double z = ent.posZ - mc.player.posZ;
		double yaw = Math.atan2(x, z) * 57.2957795D;
		return (float) (yaw * -1.0D);
	}
	
	public static boolean isAirBlock(final Block block) {
		return !block.getMaterial().isReplaceable()
				|| (block instanceof BlockSnow && !(block.getBlockBoundsMaxY() <= 0.125));
	}

	public static boolean fov(Entity entity, float fov) {
		fov = (float) ((double) fov * 0.5D);
		double v = ((double) (mc.player.rotationYaw - fovToEntity(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
		return v > 0.0D && v < (double) fov || (double) (-fov) < v && v < 0.0D;
	}

	public static boolean onMouseOver() {
		return mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null;
	}

	public static boolean playerOverAir() {
		return mc.world.isAirBlock(new BlockPos(MathHelper.floor_double(mc.player.posX),
				MathHelper.floor_double(mc.player.posY - 1.0D), MathHelper.floor_double(mc.player.posZ)));
	}

	public static boolean isBlockUnder(int offset) {
		for (int i = (int) (mc.player.posY - offset); i > 0; i--) {
			BlockPos pos = new BlockPos(mc.player.posX, i, mc.player.posZ);
			if (!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir))
				return true;
		}
		return false;
	}

	public static boolean isHoldingWeapon() {
		if (mc.player.getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.player.getCurrentEquippedItem().getItem();
			return item instanceof ItemSword;
		}
	}
	
	public static double getDirection() {
		float moveYaw = mc.player.rotationYaw;
		if (mc.player.moveForward != 0f && mc.player.moveStrafing == 0f) {
			moveYaw += (mc.player.moveForward > 0) ? 0 : 180;
		} else if (mc.player.moveForward != 0f && mc.player.moveStrafing != 0f) {
			if (mc.player.moveForward > 0)
				moveYaw += (mc.player.moveStrafing > 0) ? -45 : 45;
			else
				moveYaw -= (mc.player.moveStrafing > 0) ? -45 : 45;
			moveYaw += (mc.player.moveForward > 0) ? 0 : 180;
		} else if (mc.player.moveStrafing != 0f && mc.player.moveForward == 0f) {
			moveYaw += (mc.player.moveStrafing > 0) ? -90 : 90;
		}
		return Math.floorMod((int) moveYaw, 360);
	}

	public static ItemStack getBestSword() {
		int size = mc.player.inventoryContainer.getInventory().size();
		ItemStack lastSword = null;
		for (int i = 0; i < size; i++) {
			ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
			if (stack != null && stack.getItem() instanceof ItemSword)
				if (lastSword == null) {
					lastSword = stack;
				} else if (isBetterSword(stack, lastSword)) {
					lastSword = stack;
				}
		}
		return lastSword;
	}

	public static ItemStack getBestAxe() {
		int size = mc.player.inventoryContainer.getInventory().size();
		ItemStack lastAxe = null;
		for (int i = 0; i < size; i++) {
			ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
			if (stack != null && stack.getItem() instanceof ItemAxe)
				if (lastAxe == null) {
					lastAxe = stack;
				} else if (isBetterTool(stack, lastAxe, Blocks.planks)) {
					lastAxe = stack;
				}
		}
		return lastAxe;
	}

	public static ItemStack getBestPickaxe() {
		int size = mc.player.inventoryContainer.getInventory().size();
		ItemStack lastPickaxe = null;
		for (int i = 0; i < size; i++) {
			ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
			if (stack != null && stack.getItem() instanceof ItemPickaxe)
				if (lastPickaxe == null) {
					lastPickaxe = stack;
				} else if (isBetterTool(stack, lastPickaxe, Blocks.stone)) {
					lastPickaxe = stack;
				}
		}
		return lastPickaxe;
	}

	public static boolean isBetterTool(ItemStack better, ItemStack than, Block versus) {
		return (getToolDigEfficiency(better, versus) > getToolDigEfficiency(than, versus));
	}

	public static boolean isBetterSword(ItemStack better, ItemStack than) {
		return (getSwordDamage((ItemSword) better.getItem(), better) > getSwordDamage((ItemSword) than.getItem(),
				than));
	}

	public static float getSwordDamage(ItemSword sword, ItemStack stack) {
		float base = sword.getMaxDamage();
		return base + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
	}

	public static float getToolDigEfficiency(ItemStack stack, Block block) {
		float f = stack.getStrVsBlock(block);
		if (f > 1.0F) {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
			if (i > 0)
				f += (i * i + 1);
		}
		return f;
	}

}
