package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;

@Info(name = "FastPlace", category = Category.Player)
public class FastPlace extends Module {
	private SliderValue delaySlider = new SliderValue("Delay", 1, 0, 4, 1);
	private BooleanValue blockOnly = new BooleanValue("Blocks only", true);
	private BooleanValue projSeparate = new BooleanValue("Separate Projectile Delay", true);
	private BooleanValue pitchCheck = new BooleanValue("Pitch Check", false);
	private SliderValue projSlider = new SliderValue("Projectile Delay", 2, 0, 4, 1);

	public FastPlace() {
		this.registerSetting(delaySlider, blockOnly, projSeparate, pitchCheck, projSlider);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + delaySlider.getInput() + " ticks]");
	}

	@Override
	public boolean canBeEnabled() {
		return mc.rightClickDelayTimer != 4;
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (!PlayerUtil.inGame() && !mc.inGameHasFocus)
			return;

		ItemStack item = mc.player.getHeldItem();

		if (item != null) {

			if (!pitchCheck.isToggled() || !(mc.player.rotationPitch < 70.0F)) {
				if (blockOnly.isToggled()) {
					if (item.getItem() instanceof ItemBlock) {
						rightDelay(delaySlider.getInputToInt());
					} else if ((item.getItem() instanceof ItemSnowball || item.getItem() instanceof ItemEgg)
							&& projSeparate.isToggled()) {
						rightDelay(projSlider.getInputToInt());
					}
				} else {
					rightDelay(delaySlider.getInputToInt());
				}
			}
		}

	}

	private void rightDelay(int x) {
		if (x == 0) {
			mc.rightClickDelayTimer = 0;
		} else if (x != 4 && mc.rightClickDelayTimer == 4) {
			mc.rightClickDelayTimer = x;
		}
	}
}
