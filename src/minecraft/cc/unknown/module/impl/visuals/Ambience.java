package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketTimeUpdate;

@Info(name = "Ambience", category = Category.Visuals)
public class Ambience extends Module {

	private SliderValue time = new SliderValue("Time", 14.0, 0.0, 24.0, 1.0);

	public Ambience() {
		this.registerSetting(time);
	}

	@EventLink
	public void onRender3D(RenderEvent e) {
		if (e.is3D()) {
			mc.world.setWorldTime((long)(time.getInputToInt()) * 1000L);
		}
	}

	@EventLink
	public void onPacketReceive(PacketEvent e) {
		if (e.isReceive()) {
			if (e.getPacket() instanceof SPacketTimeUpdate) {
				e.setCancelled(true);
			}
	
			if (e.getPacket() instanceof SPacketChangeGameState) {
				SPacketChangeGameState S2BPacket = (SPacketChangeGameState) e.getPacket();
				if (S2BPacket.getGameState() == 7 || S2BPacket.getGameState() == 8) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventLink
	public void onTick(TickEvent e) {
		mc.world.setRainStrength(0.0F);
		mc.world.setThunderStrength(0.0F);
	}
}
