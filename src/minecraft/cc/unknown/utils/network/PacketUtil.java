package cc.unknown.utils.network;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import cc.unknown.utils.Loona;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PacketUtil implements Loona {
    public static final ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
    
    public static void send(Packet<?> i) {
        mc.getNetHandler().addToSendQueue(i);
    }
    
	public static void send(Packet<?>[] i) {
        NetworkManager netManager = mc.getNetHandler() != null ? mc.getNetHandler().getNetworkManager() : null;
        if (netManager != null && netManager.isChannelOpen()) {
            netManager.flushOutboundQueue();
            for (Packet<?> packet : i) {
                netManager.dispatchPacket(packet, null);
            }
        } else if (netManager != null) {
            try {
                netManager.field_181680_j.writeLock().lock();
                for (Packet<?> packet : i) {
                    netManager.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packet, Arrays.asList((GenericFutureListener<? extends Future<? super Void>>) null).toArray(new GenericFutureListener[0])));
                }
            } finally {
                netManager.field_181680_j.writeLock().unlock();
            }
        }
    }
}
