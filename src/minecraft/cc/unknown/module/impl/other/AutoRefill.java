package cc.unknown.module.impl.other;

import java.util.List;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@Info(name = "AutoRefill", category = Category.Other)
public class AutoRefill extends Module {

	private SliderValue delay = new SliderValue("Delay", 0, 0, 500, 1);
	private BooleanValue pots = new BooleanValue("Pots", true);
	private BooleanValue soup = new BooleanValue("Soup", true);

	private int lastShiftedPotIndex = -1;
	private long lastUsageTime = 0;
	private long delay1 = 800;
	private boolean refillOpened = false;

	public AutoRefill() {
		this.registerSetting(delay, pots, soup);
	}

	@Override
	public void onEnable() {
		if (PlayerUtil.inGame() && mc.currentScreen == null) {
			refillOpened = true;
			newDelay();
			openInventory();
			if (isHotbarFull()) {
				closeInventory();
			}
		}
	}

	@EventLink
	public void onPre(UpdateEvent e) {
		if (e.isPre()) {
			long currentTime = System.currentTimeMillis();
			if (mc.currentScreen instanceof GuiInventory && !isHotbarFull()) {
				if (refillOpened && currentTime - lastUsageTime >= delay1) {
					refillHotbar();
					lastUsageTime = currentTime;
				}
			} else if (mc.currentScreen == null && this.isEnabled()) {
				this.disable();
			}
		}
	}

	private boolean isHotbarFull() {
		for (int i = 36; i < 45; i++) {
			if (!mc.player.inventoryContainer.getSlot(i).getHasStack()) {
				return false;
			}
		}
		return true;
	}

	private void openInventory() {
		mc.getNetHandler()
				.sendQueue(new CPacketClientStatus(CPacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
		mc.displayGuiScreen(new GuiInventory(mc.player));
	}

	private void refillHotbar() {
		int nextPotIndex = findNextPotIndex();
		if (nextPotIndex != -1) {
			newDelay();
			shiftRightClickItem(nextPotIndex);
			lastShiftedPotIndex = nextPotIndex;
			if (isHotbarFull()) {
				closeInventory();
			}
		} else {
			closeInventory();
		}
	}

	private int findNextPotIndex() {
		int inventorySize = mc.player.inventory.getSizeInventory();
		int startIndex = (lastShiftedPotIndex + 1 + 9) % inventorySize;

		for (int i = startIndex; i != startIndex - 1; i = (i + 1) % inventorySize) {
			int slotIndex = i % inventorySize;

			if (slotIndex < 9) {
				continue;
			}

			ItemStack stack = mc.player.inventory.getStackInSlot(slotIndex);

			if (isValidStack(stack)) {
				lastShiftedPotIndex = slotIndex;
				return slotIndex;
			}

			if (i == (startIndex - 1 + inventorySize) % inventorySize) {
				break;
			}
		}

		return -1;
	}

	private void newDelay() {
		delay1 = (long) (delay.getInput() + (Math.random() * (delay.getInput() - delay.getInput())));
	}

	private boolean isValidStack(ItemStack stack) {
		if (stack == null)
			return false;
		return (pots.isToggled() && isPot(stack)) || (soup.isToggled() && isSoup(stack));
	}

	private boolean isSoup(ItemStack stack) {
		return stack.getItem() == Items.mushroom_stew;
	}

    private boolean isPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) stack.getItem();
            List<PotionEffect> effects = potion.getEffects(stack);
            for (PotionEffect effect : effects) {
                if ((effect.getPotionID() == Potion.heal.id || effect.getPotionID() == Potion.regeneration.id) && ItemPotion.isSplash(stack.getMetadata())) {
                    return true;
                }
            }
        }
        return false;
    }

	private void shiftRightClickItem(int slotIndex) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slotIndex, 0, 1, mc.player);
		mc.playerController.updateController();
	}

	private void closeInventory() {
		mc.player.closeScreen();
		mc.playerController.sendPacketDropItem(mc.player.inventory.getItemStack());
		refillOpened = false;
		this.disable();
	}
}
