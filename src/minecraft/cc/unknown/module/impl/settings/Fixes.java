package cc.unknown.module.impl.settings;

import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;

@Info(name = "Tweaks", category = Category.Settings)
public class Fixes extends Module {
	private BooleanValue noClickDelay = new BooleanValue("NoClickDelay", true);
	private BooleanValue noJumpDelay = new BooleanValue("NoJumpDelay", true);
	public BooleanValue noHurtCam = new BooleanValue("NoHurtCam", true);
	private BooleanValue rawInput = new BooleanValue("No Mouse Lag", true);

	public Fixes() {
		this.registerSetting(noClickDelay, noJumpDelay, noHurtCam, rawInput);
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (!PlayerUtil.inGame()) return;
		
		if (noClickDelay.isToggled()) {
			mc.leftClickCounter = 0;
		}
		
		if (noJumpDelay.isToggled()) {
			mc.player.jumpTicks = 0;
		}
		
		if (rawInput.isToggled()) {
			if (mc.currentScreen instanceof GuiScreen) return;
			
			System.setProperty("fml.noGrab", "true");
			Mouse.setGrabbed(true);
			Mouse.updateCursor();
		} else if (mc.currentScreen != null) {
			System.setProperty("fml.noGrab", "false");
			Mouse.setGrabbed(false);
			Mouse.updateCursor();
		}
	}

}
