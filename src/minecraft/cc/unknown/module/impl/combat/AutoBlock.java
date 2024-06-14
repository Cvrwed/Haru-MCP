package cc.unknown.module.impl.combat;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.event.impl.render.RenderItemEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;

@Info(name = "AutoBlock", category = Category.Combat)
public class AutoBlock extends Module {
	private SliderValue duration = new SliderValue("Block duration", 100, 1, 500, 1);
	private SliderValue distance = new SliderValue("Distance to player", 3, 0, 6, 0.01);
	private BooleanValue forceBlock = new BooleanValue("Force Block Animation", true);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	
	private boolean block;
	private final Cold blockTime = new Cold(0);
	private EntityPlayer target = null;

	public AutoBlock() {
		this.registerSetting(duration, distance, forceBlock, chance);
	}

	@EventLink
	public void onPacket(RenderEvent e) {
		if (e.is3D()) {
			if (!PlayerUtil.inGame() || !PlayerUtil.isHoldingWeapon())
				return;

			if (block) {
				if ((blockTime.hasFinished() || !Mouse.isButtonDown(0)) && duration.getInput() <= blockTime.getTime()) {
					block = false;
					release();
				}
				return;
			}

			if (Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null
					&& mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit) >= distance.getInput()
					&& mc.objectMouseOver.entityHit != null
					&& mc.player.getDistanceToEntity(mc.objectMouseOver.entityHit) <= distance.getInput()
					&& (chance.getInput() == 100 || Math.random() <= chance.getInput() / 100)) {
				block = true;
				blockTime.setCooldown(duration.getInputToLong());
				blockTime.start();
				press();
			}
		}
	}
	
    @EventLink
    public void onRenderItem(RenderItemEvent e) {
        if (PlayerUtil.isHoldingWeapon() && forceBlock.isToggled() && target != null) {
            e.setEnumAction(EnumAction.BLOCK);
            e.setUseItem(true);
        }
    }

	private void release() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	}

	private void press() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
		KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
	}
}