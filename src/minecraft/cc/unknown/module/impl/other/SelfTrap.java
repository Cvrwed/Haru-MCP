package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Info(name = "SelfTrap", category = Category.Other)


public class SelfTrap extends Module{
	public ModeValue mode = new ModeValue("Mode", "Slow", "Slow", "Normal");
	public BooleanValue autoFindBlock = new BooleanValue("Auto find block", false);
	public int ticks = 0;
	public int prevItem = -1;

	public SelfTrap() {
		this.registerSetting(mode);
	}

	@Override
	public void onEnable() {
		prevItem = mc.player.inventory.currentItem;
		ticks = 0;
	}

	@Override
	public void onDisable() {
		mc.gameSettings.keyBindUseItem.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem);
		mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);

	}

	@EventLink
	public void onUpdate(MotionEvent e) {
		if (!mc.player.onGround && ticks == 0)
			return;
		for (int slot = 0; slot <= 8; slot++) {
			ItemStack itemInSlot = mc.player.inventory.getStackInSlot(slot);
			if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock && itemInSlot.stackSize > 0) {
				ItemBlock itemBlock = (ItemBlock) itemInSlot.getItem();
				Block block = itemBlock.getBlock();
				if (mc.player.inventory.currentItem != slot && block.isFullCube()) {
					mc.player.inventory.currentItem = slot;
				} else {
					return;
				}
				return;
			}
		}
		mc.rightClickDelayTimer = 0;
		mc.gameSettings.keyBindUseItem.pressed = true;
		++ticks;

		if(mode.equals("Normal"));{}

		if (mode.is("Normal")) {
			if (ticks >= 0 && ticks <= 4) {
				RotationManager.getNormalRotVector(mc.player.rotationYaw + 45F, 0f);
				mc.gameSettings.keyBindJump.pressed = true;
			}
			if (ticks >= 5 && ticks <= 7) {
				RotationManager.getNormalRotVector(mc.player.rotationYaw + 135F, 0f);
				mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
			}
			if (ticks >= 8 && ticks <= 10) {
				RotationManager.getNormalRotVector(mc.player.rotationYaw + 225F, 0f);
			}
		}
	}

}
