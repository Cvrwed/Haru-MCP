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
}
