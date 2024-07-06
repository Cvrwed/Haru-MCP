package cc.unknown.module.impl.combat;

import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.KeybindUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.MathHelper;

@Info(name = "JumpReset", category = Category.Combat)
public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Hit", "Tick", "Hit");
	private BooleanValue onlyCombat = new BooleanValue("Enable only during combat", true);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private SliderValue tickTicks = new SliderValue("Ticks", 0, 0, 20, 1);
	private SliderValue hitHits = new SliderValue("Hits", 0, 0, 20, 1);

	private int limit = 0;
	private boolean reset = false;
	private int counter;

	public JumpReset() {
		registerSetting(mode, onlyCombat, chance, tickTicks, hitHits);
	}
	
	@Override
	public void onEnable() {
		stop();
	}
	
	@Override
	public void onDisable() {
		stop();
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onLiving(KnockBackEvent e) {
		if (PlayerUtil.inGame()) {
			if (mode.is("Tick") || mode.is("Hit")) {
				double direction = Math.atan2(e.getX(), e.getZ());
				double degreePlayer = PlayerUtil.getDirection();
				double degreePacket = Math.floorMod((int) Math.toDegrees(direction), 360);
				double angle = Math.abs(degreePacket + degreePlayer);
				double threshold = 120.0;
				angle = Math.floorMod((int) angle, 360);
				boolean inRange = angle >= 180 - threshold / 2 && angle <= 180 + threshold / 2;
				if (inRange) {
					reset = true;
				}
			}
		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (PlayerUtil.inGame()) {
			if (checkLiquids() || !applyChance())
				return;

			if (mode.is("Ticks") || mode.is("Hits") && reset) {
				if (!mc.gameSettings.keyBindJump.pressed && shouldJump() && mc.player.isSprinting()
						&& mc.player.hurtTime == 9
						|| (!onlyCombat.isToggled() && mc.gameSettings.keyBindAttack.isKeyDown())
						|| mc.player.onGround) {
					mc.gameSettings.keyBindJump.pressed = true;
					limit = 0;
				}
				reset = false;
				return;
			}

			switch (mode.getMode()) {
			case "Ticks": {
				limit++;
			}
				break;

			case "Hits": {
				if (mc.player.hurtTime == 9) {
					limit++;
				}
			}
				break;
			}
		}
	}

	private boolean shouldJump() {
		switch (mode.getMode()) {
		case "Ticks": {
            double random = MathHelper.randomValue(tickTicks.getInput(), tickTicks.getInput() + 0.1);
            return limit >= random;
		}
		case "Hits": {
			double random = MathHelper.randomValue(hitHits.getInput(), hitHits.getInput() + 0.1);
            return limit >= random;
		}
		default:
			return false;
		}
	}

	private boolean checkLiquids() {
		if (mc.player == null || mc.world == null) {
			return false;
		}
		return Stream.<Supplier<Boolean>>of(mc.player::isInLava, mc.player::isBurning, mc.player::isInWater,
				() -> mc.player.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}

	private void stop() {
		limit = 0;
		reset = false;
		counter = 0;
	}

	private boolean applyChance() {
		Supplier<Boolean> chanceCheck = () -> {
			return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
		};

		return Stream.of(chanceCheck).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
}
