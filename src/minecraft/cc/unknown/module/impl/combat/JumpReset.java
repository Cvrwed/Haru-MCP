package cc.unknown.module.impl.combat;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.KeybindUtil;
import cc.unknown.utils.network.TimedPacket;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.MathHelper;

@Register(name = "JumpReset", category = Category.Combat)
public class JumpReset extends Module {
	private ModeValue mode = new ModeValue("Mode", "Legit", "Hit", "Tick", "Legit", "Jump");
	private BooleanValue onlyCombat = new BooleanValue("Enable only during combat", true);
	private BooleanValue antiCombo = new BooleanValue("Anti Combo", false);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private SliderValue comboTicks = new SliderValue("Combo Hits", 1, 0, 20, 1);
	private DoubleSliderValue tickTicks = new DoubleSliderValue("Ticks", 0, 0, 0, 20, 1);
	private DoubleSliderValue hitHits = new DoubleSliderValue("Hits", 0, 0, 0, 20, 1);

	private int limit = 0;
	private boolean reset = false;
	private boolean shouldspoof = false;
	private int hitCombo = 0;
	private int enabledticks = 0;
	private ConcurrentLinkedQueue<TimedPacket> inboundPacketsQueue = new ConcurrentLinkedQueue<TimedPacket>();
	private int counter = 0;

	public JumpReset() {
		registerSetting(mode, onlyCombat, antiCombo, chance, comboTicks, tickTicks, hitHits);
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
	public void onLiving(LivingEvent e) {
		if (PlayerUtil.inGame()) {
			if (mode.is("Tick") || mode.is("Hit")) {
				double direction = Math.atan2(mc.player.motionX, mc.player.motionZ);
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

			if (mode.is("Jump")) {
				float yaw = mc.player.rotationYaw * 0.017453292F;
				if (mc.player.onGround) {
					if (mc.player.hurtTime > 0) {
						if (reset) {
							mc.player.motionX -= Math.sin(yaw) * 0.2;
							mc.player.motionZ += Math.cos(yaw) * 0.2;
						}
					}
				}
			}
		}
	}

	@EventLink
	public void onTickPre(TickEvent.Pre e) {
		if (antiCombo.isToggled()) {
			if (hitCombo >= comboTicks.getInputToInt()) {
				shouldspoof = true;
			}
			if (shouldspoof) {
				++enabledticks;
			}
			if (enabledticks >= 100) {
				stop();
				clearInboundQueue();
			}
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend()) {
			if (e.getPacket() instanceof CPacketUseEntity && antiCombo.isToggled()) {
				CPacketUseEntity wrapper = (CPacketUseEntity) e.getPacket();
				if (wrapper.getAction() == CPacketUseEntity.Mode.ATTACK) {
					hitCombo = 0;
					if (shouldspoof) {
						stop();
						clearInboundQueue();
					}
				}
			}
		}
	}

	@EventLink
	public void onKnockBack(KnockBackEvent e) {
		if (mode.is("Legit")) {
			if (!mc.player.onGround || !(e.getY() > 0.0) || mc.currentScreen != null)
				return;
			;
			double velocityDist = Math.hypot(e.getX(), e.getZ());
			if (counter >= 4 && (velocityDist < 0.6 || counter >= 7)) {
				counter = 0;
				return;
			}
			reset = true;
			++counter;
			return;
		}
		
		if (mode.is("Jump")) {
			reset = true;
		}

		if (antiCombo.isToggled()) {
			++hitCombo;
		}
	}

	@EventLink
	public void onInput(TickEvent.Input e) {
		if (mode.is("Legit")) {
			if (!reset)
				return;
			mc.gameSettings.keyBindJump.pressed = true;
			mc.gameSettings.keyBindForward.pressed = true;
			mc.gameSettings.keyBindSprint.pressed = true;
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (mode.is("Legit") && e.isPre()) {
			if (!reset)
				return;
			KeybindUtil.instance.resetKeybindings(mc.gameSettings.keyBindJump, mc.gameSettings.keyBindForward,
					mc.gameSettings.keyBindSprint);
			reset = false;
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
            double random = MathHelper.randomValue(tickTicks.getInputMin(), tickTicks.getInputMax() + 0.1);
            return limit >= random;
		}
		case "Hits": {
			double random = MathHelper.randomValue(hitHits.getInputMin(), hitHits.getInputMax() + 0.1);
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
		shouldspoof = false;
		hitCombo = 0;
		enabledticks = 0;
		counter = 0;
	}

	private void clearInboundQueue() {
		for (final TimedPacket packet : inboundPacketsQueue) {
			final Packet p = packet.getPacket();
			p.processPacket(mc.player.sendQueue.getNetworkManager().getNetHandler());
			inboundPacketsQueue.remove(packet);
		}
	}

	private boolean applyChance() {
		Supplier<Boolean> chanceCheck = () -> {
			return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
		};

		return Stream.of(chanceCheck).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
	}
}
