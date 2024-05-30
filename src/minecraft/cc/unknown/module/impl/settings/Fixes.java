package cc.unknown.module.impl.settings;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.chat.ChatComponentText;

@Info(name = "Fixes", category = Category.Settings)
public class Fixes extends Module {
	private BooleanValue noClickDelay = new BooleanValue("No Click Delay", true);
	private BooleanValue noJumpDelay = new BooleanValue("No Jump Delay", true);
	public BooleanValue noHurtCam = new BooleanValue("No Hurt Cam", true);
	public BooleanValue rawInput = new BooleanValue("No Mouse Lag", true);

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
	}

}
