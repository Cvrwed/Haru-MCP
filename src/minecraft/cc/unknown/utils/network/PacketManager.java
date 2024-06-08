package cc.unknown.utils.network;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.utils.Loona;
import cc.unknown.utils.player.PlayerUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.ViaMCP;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class PacketManager implements Loona {

	public PacketManager() {
		Haru.instance.getEventBus().register(this);	
	}

    @EventLink
    public void onLiving(LivingEvent e) { // blocking fix
        if (ViaMCP.INSTANCE.newerThanOrEqualsTo1_9() && mc.player.isBlocking() && mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword) {
        	mc.getNetHandler().sendSilent(new CPacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));
            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            useItem.write(Type.VAR_INT, 1);
            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
        }
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (ViaMCP.INSTANCE.newerThanOrEqualsTo1_9() && e.isSend()) { // placement fix
            final Packet<?> packet = e.getPacket();
            if (packet instanceof CPacketPlayerBlockPlacement) {
                ((CPacketPlayerBlockPlacement) packet).facingX = 0.5F;
                ((CPacketPlayerBlockPlacement) packet).facingY = 0.5F;
                ((CPacketPlayerBlockPlacement) packet).facingZ = 0.5F;
            }
        }
    }
}
