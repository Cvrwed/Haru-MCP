package net.minecraft.server.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.chat.ChatComponentText;

public class NetHandlerStatusServer implements INetHandlerStatusServer
{
    private static final IChatComponent field_183007_a = new ChatComponentText("Status request has been handled.");
    private final MinecraftServer server;
    private final NetworkManager networkManager;
    private boolean field_183008_d;

    public NetHandlerStatusServer(MinecraftServer serverIn, NetworkManager netManager)
    {
        this.server = serverIn;
        this.networkManager = netManager;
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
    }

    public void processServerQuery(CPacketServerQuery packetIn)
    {
        if (this.field_183008_d)
        {
            this.networkManager.closeChannel(field_183007_a);
        }
        else
        {
            this.field_183008_d = true;
            this.networkManager.sendPacket(new SPacketServerInfo(this.server.getServerStatusResponse()));
        }
    }

    public void processPing(CPacketPing packetIn)
    {
        this.networkManager.sendPacket(new SPacketPong(packetIn.getClientTime()));
        this.networkManager.closeChannel(field_183007_a);
    }
}
