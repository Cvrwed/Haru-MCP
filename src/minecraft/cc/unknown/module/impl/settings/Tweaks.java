package cc.unknown.module.impl.settings;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.CPacketClientSettings;

@Register(name = "Tweaks", category = Category.Settings)
public class Tweaks extends Module {
	private BooleanValue noClickDelay = new BooleanValue("No Click Delay", true);
	private BooleanValue noJumpDelay = new BooleanValue("No Jump Delay", true);
	public BooleanValue noHurtCam = new BooleanValue("No Hurt Cam", true);
	public BooleanValue noScoreboard = new BooleanValue("No Scoreboard", false);
	private BooleanValue cancelC15 = new BooleanValue("Bypass V4Guard Block", true);

	public Tweaks() {
		this.registerSetting(noClickDelay, noJumpDelay, noHurtCam, noScoreboard, cancelC15);
	}

	@EventLink
	public void onClick(TickEvent e) {
		if (noClickDelay.isToggled() && this.isEnabled()) {
			mc.leftClickCounter = 0;
		}
	}
	
	@EventLink
	public void onJump(TickEvent e) {
		if (noJumpDelay.isToggled() && this.isEnabled()) {
			mc.player.jumpTicks = 0;
		}
	}
	
	@EventLink
	public void onCancelC15(PacketEvent e) {
		if (cancelC15.isToggled() && PlayerUtil.inGame() && this.isEnabled()) {
			if (e.isSend()) {
				if (e.getPacket() instanceof CPacketClientSettings) {
					e.setCancelled(true);
				}
			}
		}
	}
}
