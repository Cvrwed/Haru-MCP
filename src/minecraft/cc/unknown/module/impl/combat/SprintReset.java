package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketEntityAction;

@Register(name = "SprintReset", category = Category.Combat)
public class SprintReset extends Module {

	private ModeValue mode = new ModeValue("Mode", "WTap", "WTap", "STap", "Packet");
	private SliderValue packets = new SliderValue("Packets", 2, 0, 10, 2);
	private SliderValue onceEvery = new SliderValue("Once Every Hits", 0, 0, 10, 1);
	private SliderValue tapRange = new SliderValue("Tap Range", 3.0, 3.0, 6.0, 0.5);
	private SliderValue chance = new SliderValue("Tap Chance", 100, 0, 100, 1);
	private final Cold timer = new Cold(0);
	private int tap;
	private int hitsCount = 0;
	private EntityPlayer target = null;

	public SprintReset() {
		this.registerSetting(mode, packets, onceEvery, tapRange, chance);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (target == null) return;
		
		if (chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D) {
			return;
		}

		Packet<?> p = e.getPacket();
		if (e.isSend() && p instanceof CPacketUseEntity) {
			CPacketUseEntity wrapper = (CPacketUseEntity) p;
			if (wrapper.getAction() == CPacketUseEntity.Mode.ATTACK) {
				double entityDistance = mc.player.getDistanceToEntity(wrapper.getEntityFromWorld(mc.world));
				if (entityDistance <= tapRange.getInputToInt()) {
					hitsCount++;
					if (hitsCount >= onceEvery.getInputToInt()) {
						switch (mode.getMode()) {
						case "Packet":
							if (mc.player.isSprinting()) setSprinting(false);
							
							for (int i = 0; i < (packets.getInputToInt() - 2.0); i++) {
								if (i % 2 == 0) {
									setSprinting(true);
								} else {
									setSprinting(false);
								}
							}
							if (mc.player.isSprinting()) setSprinting(true);

							break;
						case "STap":
						case "WTap":
							if (timer.reached(500L)) {
								timer.reset();
								tap = 2;
							}
							break;
						}
						hitsCount = 0;
					}
				}
			}
		}
	}
	
	private void setSprinting(boolean pacman) {
		if (pacman) {
			mc.getNetHandler().sendQueue(
					new CPacketEntityAction(mc.player, CPacketEntityAction.Mode.START_SPRINTING));
		} else {
			mc.getNetHandler().sendQueue(
					new CPacketEntityAction(mc.player, CPacketEntityAction.Mode.STOP_SPRINTING));
		}
	}

	@EventLink
	public void onLiving(LivingEvent e) {
		if (PlayerUtil.inGame() && (PlayerUtil.isMoving() && mode.is("STap"))) {
			if (mode.is("STap")) {
				switch (tap) {
				case 2:
					mc.gameSettings.keyBindForward.pressed = false;
					mc.gameSettings.keyBindBack.pressed = true;
					tap--;
					break;
				case 1:
					mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
					mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
					tap--;
					break;
				}
			}

			if (mode.is("WTap")) {
				switch (tap) {
				case 2:
					mc.gameSettings.keyBindForward.pressed = false;
					tap--;
					break;
				case 1:
					mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
					tap--;
					break;
				}
			}
		}
	}
}
