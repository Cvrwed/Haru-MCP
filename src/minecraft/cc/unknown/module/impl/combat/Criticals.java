package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

@Info(name = "Criticals", category = Category.Combat)
public class Criticals extends Module {

	private BooleanValue aggressive = new BooleanValue("Aggressive", true);
	private SliderValue packetSendingRate = new SliderValue("Packet Sending Rate", 500, 250, 1000, 1);
	private SliderValue criticalHitChance = new SliderValue("Hit Chance", 100, 0, 100, 1);

	private boolean onAir, hitGround;
	private List<Packet<INetHandlerPlayServer>> packets = new ArrayList<>(), attackPackets = new ArrayList<>();
	private Cold timer = new Cold();

	public Criticals() {
		this.registerSetting(aggressive, packetSendingRate, criticalHitChance);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + packetSendingRate.getInputToInt() + " ms]");
	}

	@Override
	public void onEnable() {
		onAir = false;
		hitGround = false;
	}

	@Override
	public void onDisable() {
		releasePackets();
	}

	@EventLink
	public void onSend(PacketEvent e) {
		if (e.isSend()) {
			if (mc.player.onGround)
				hitGround = true;

			if (!timer.reached(packetSendingRate.getInputToLong()) && onAir) {
				e.setCancelled(true);
				if (e.getPacket() instanceof CPacketUseEntity && e.getPacket() instanceof CPacketAnimation) {
					if (aggressive.isToggled()) {
						e.setCancelled(false);
					} else {
						attackPackets.add((Packet<INetHandlerPlayServer>) e.getPacket());
					}
				} else {
					packets.add((Packet<INetHandlerPlayServer>) e.getPacket());
				}
			}

			if (timer.reached(packetSendingRate.getInputToLong()) && onAir) {
				onAir = false;
				releasePackets();
			}

			if (e.getPacket() instanceof CPacketUseEntity) {
				CPacketUseEntity wrapper = (CPacketUseEntity) e.getPacket();

				Entity entity = wrapper.getEntityFromWorld(mc.world);
				if (entity == null)
					return;
				if (wrapper.getAction() == CPacketUseEntity.Mode.ATTACK) {
					if (!mc.player.onGround) {
						if (!onAir && hitGround && mc.player.fallDistance <= 1
								&& (criticalHitChance.getInputToInt() / 100) > Math.random()) {
							timer.reset();
							onAir = true;
							hitGround = false;
						}
						return;
					}

					if (onAir) {
					    mc.player.onCriticalHit(entity);
					    PlayerUtil.send("Crit");
					}
				}
			}
		}

		if (e.isReceive()) {
			if (mc.player == null) hitGround = true;
			if (e.getPacket() instanceof SPacketPlayerPosLook) hitGround = true;
		}
	}

	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		if (e.isClient()) {
			this.disable();
		}
	}

	private void releasePackets() {
		if (PlayerUtil.inGame()) {
			if (!attackPackets.isEmpty())
				attackPackets.forEach(mc.getNetHandler()::sendSilent);
			if (!packets.isEmpty())
				packets.forEach(mc.getNetHandler()::sendSilent);
		}

		packets.clear();
		attackPackets.clear();
		timer.reset();
	}
}
