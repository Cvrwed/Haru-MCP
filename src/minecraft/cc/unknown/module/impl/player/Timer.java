package cc.unknown.module.impl.player;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.impl.combat.AutoClick;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.PlayerUtil;
import io.netty.util.internal.ThreadLocalRandom;

@Register(name = "Timer", category = Category.Player)
public class Timer extends Module {

	private ModeValue mode = new ModeValue("Mode", "Constant", "Constant", "Random", "Ground");
	private SliderValue spid = new SliderValue("Speed", 1.5, 0.05, 25, 0.05);
	private SliderValue variation = new SliderValue("Variation", 15, 0.05, 50, 0.05);
	private SliderValue onGroundTicksPerSecond = new SliderValue("On Ground Speed", 1.5, 0.05, 20, 0.05);
	private SliderValue offGroundTicksPerSecond = new SliderValue("Off Ground Speed", 1.5, 0.05, 20, 0.05);

	private BooleanValue weaponOnly = new BooleanValue("Only Use Weapons", false);

	public Timer() {
		this.registerSetting(mode, spid, variation, onGroundTicksPerSecond, offGroundTicksPerSecond, weaponOnly);
	}

	@Override
	public void onDisable() {
		this.resetTimer();
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (!PlayerUtil.inGame()) {
			return;
		}

		float timerSpeed = 1.0F;
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		
		if (weaponOnly.isToggled() && !PlayerUtil.isHoldingWeapon()) {
			this.resetTimer();
			return;
		}
		
		switch (mode.getMode()) {
		case "Constant":
			timerSpeed = calculateConstantTimer();
			break;
		case "Random":
			timerSpeed = calculateRandomTimer();
			break;
		case "Ground":
			timerSpeed = calculateGroundTimer();
			break;
		}

		mc.timer.timerSpeed = timerSpeed;
	}

	private float calculateConstantTimer() {
		float speed = spid.getInputToFloat();
		return speed;
	}

	private float calculateRandomTimer() {
		float speed = spid.getInputToFloat();
		int variationHalf = variation.getInputToInt() / 2;
		float randomFactor = ThreadLocalRandom.current().nextInt(-variationHalf, variationHalf + 1) / 2.0f;
		float adjustedTicksPerSec = Math.max(speed + randomFactor, 1.0f);
		return adjustedTicksPerSec;
	}

	private float calculateGroundTimer() {
		boolean isOnGround = mc.player.onGround;
		float ticksPerSec = isOnGround ? onGroundTicksPerSecond.getInputToFloat()
				: offGroundTicksPerSecond.getInputToFloat();
		return ticksPerSec;
	}
	
	private void resetTimer() {
		mc.timer.timerSpeed = 1.0F;
	}
}
