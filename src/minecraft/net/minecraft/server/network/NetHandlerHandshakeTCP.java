package net.minecraft.server.network;

import net.minecraft.network.ConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CHandshake;
import net.minecraft.network.login.server.SPacketServerDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.chat.ChatComponentText;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer
{
    private final MinecraftServer server;
    private final NetworkManager networkManager;

    public NetHandlerHandshakeTCP(MinecraftServer serverIn, NetworkManager netManager)
    {
        this.server = serverIn;
        this.networkManager = netManager;
    }

    /**
     * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
     * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
     * must pass a versioncheck or receive a disconnect otherwise
     */
    public void processHandshake(CHandshake packetIn)
    {
        switch (packetIn.getRequestedState())
        {
            case LOGIN:
                this.networkManager.setConnectionState(ConnectionState.LOGIN);

                if (packetIn.getProtocolVersion() > 47)
                {
                    ChatComponentText chatcomponenttext = new ChatComponentText("Outdated server! I\'m still on 1.8.9");
                    this.networkManager.sendPacket(new SPacketServerDisconnect(chatcomponenttext));
                    this.networkManager.closeChannel(chatcomponenttext);
                }
                else if (packetIn.getProtocolVersion() < 47)
                {
                    ChatComponentText chatcomponenttext1 = new ChatComponentText("Outdated client! Please use 1.8.9");
                    this.networkManager.sendPacket(new SPacketServerDisconnect(chatcomponenttext1));
                    this.networkManager.closeChannel(chatcomponenttext1);
                }
                else
                {
                    this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
                }

                break;

            case STATUS:
                this.networkManager.setConnectionState(ConnectionState.STATUS);
                this.networkManager.setNetHandler(new NetHandlerStatusServer(this.server, this.networkManager));
                break;

            default:
                throw new UnsupportedOperationException("Invalid intention " + packetIn.getRequestedState());
        }
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
    }
}
