package cc.unknown.module.impl.settings;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.CPacketClientSettings;

@Register(name = "Helper", category = Category.Settings)
public class Helper extends Module {
	public BooleanValue noScoreboard = new BooleanValue("No Scoreboard", false);
	private BooleanValue cancelC15 = new BooleanValue("Bypass V4Guard Block", true);
	
	public Helper() {
		this.registerSetting(noScoreboard, cancelC15);
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
