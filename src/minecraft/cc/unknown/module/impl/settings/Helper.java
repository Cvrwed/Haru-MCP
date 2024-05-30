package cc.unknown.module.impl.settings;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.client.CPacketClientSettings;

@Info(name = "Helper", category = Category.Settings)
public class Helper extends Module {
	public BooleanValue idleFps = new BooleanValue("Unfocused FPS", true);
	public BooleanValue noScoreboard = new BooleanValue("No Scoreboard", false);
	private BooleanValue cancelC15 = new BooleanValue("Prevent v4Guard Block", false);
	
	private final List<Runnable> glTasks = new ArrayList<>();
	private int before = 0;
	
	public Helper() {
		this.registerSetting(noScoreboard, cancelC15, idleFps);
	}
	
	@EventLink
	public void onCancelC15(PacketEvent e) {
		if (!PlayerUtil.inGame()) return;
		
		if (e.isSend()) {
			if (cancelC15.isToggled()) {
				if (e.getPacket() instanceof CPacketClientSettings) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventLink
	public void onRender(RenderEvent e) {
		if (!e.is2D()) return;
		
	    if (idleFps.isToggled()) {
	        if (Display.isActive()) {
	            if (before != -1) {
	                mc.gameSettings.limitFramerate = before;
	                before = -1;
	            }
	        } else {
	            if (before == -1) {
	                before = mc.gameSettings.limitFramerate;
	            }
	            mc.gameSettings.limitFramerate = 15;
	        }
	    }
	    try {
	        glTasks.forEach(Runnable::run);
	    } finally {
	        glTasks.clear();
	    }
	}
}
