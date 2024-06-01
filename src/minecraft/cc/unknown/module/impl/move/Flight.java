package cc.unknown.module.impl.move;

import java.util.LinkedHashSet;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.network.TimedPacket;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.combat.EntityDamageSource;

@Info(name = "Flight", category = Category.Move)
public class Flight extends Module {

	private ModeValue mode = new ModeValue("Mode", "Polar", "Polar");
	private final LinkedHashSet<TimedPacket> packetQueue = new LinkedHashSet<>();
	private boolean damageTaken = false;
	private boolean release = false;
	private int ticks = 0;

	public Flight() {
		this.registerSetting(mode);
	}

	@Override
	public void onEnable() {
		handleFlush();
	}

	@Override
	public void onDisable() {
		handleFlush();
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		Packet packet = e.getPacket();

		switch (mode.getMode()) { // necesita el viaversion fix para k sirva
		case "Polar":

			if (e.isReceive()) {
				if (packet instanceof SPacketEntityStatus && ((SPacketEntityStatus) packet).getEntityId() == mc.player.getEntityId() && ticks <= 0) {
					damageTaken = true;
					ticks = 40;
				}

				if (packet instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) packet).getEntityID() == mc.player.getEntityId() && damageTaken) {
					packetQueue.add(new TimedPacket(packet, System.currentTimeMillis()));
					damageTaken = false;
					release = true;
				}

				if (packet instanceof SPacketConfirmTransaction) {
					if (ticks > 0) {
						packetQueue.add(new TimedPacket(packet, System.currentTimeMillis()));
						e.setCancelled(true);
						ticks--;
					} else {
						if (release) {
							setToggled(false);
						}
					}
				}
			}
			break;
		}
	}
	
	private void handleFlush() {
	    if (PlayerUtil.inGame()) {
	        for (TimedPacket data : packetQueue) {
	            PacketUtil.handlePacket((Packet<? extends INetHandlerPlayClient>) data.getPacket());
	        }
	    }
	    packetQueue.clear();
	    ticks = 0;
	    damageTaken = false;
	    release = false;
	}
}
