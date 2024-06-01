package cc.unknown.module.impl.combat;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.util.IChatComponent;

@Info(name = "BlockHit", category = Category.Combat)
public class BlockHit extends Module {
	
	@EventLink
	public void onPacket(PacketEvent e) {
	    if (e.isSend()) {
	        if (e.getPacket() instanceof CPacketUpdateSign) {
	            final CPacketUpdateSign packet = (CPacketUpdateSign) e.getPacket();

	            if (packet.lines[3].getUnformattedText().length() <= 0) {
	                CharSequence textComponent = new SimpleDateFormat("MMM dd, yyyy").format(new Date());
	                packet.lines[3] = (IChatComponent) textComponent;
	            }
	        }
	    }
	}

}