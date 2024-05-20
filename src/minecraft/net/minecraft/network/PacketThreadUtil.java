package net.minecraft.network;

import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    public static int lastDimensionId = Integer.MIN_VALUE;

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> p_180031_0_, final T p_180031_1_, IThreadListener p_180031_2_) throws ThreadQuickExitException
    {
        if (!p_180031_2_.isCallingFromMinecraftThread())
        {
            p_180031_2_.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    PacketThreadUtil.clientPreProcessPacket(p_180031_0_);
                    p_180031_0_.processPacket(p_180031_1_);
                }
            });
            throw ThreadQuickExitException.field_179886_a;
        }
        else
        {
            clientPreProcessPacket(p_180031_0_);
        }
    }

    protected static void clientPreProcessPacket(Packet p_clientPreProcessPacket_0_)
    {
        if (p_clientPreProcessPacket_0_ instanceof SPacketPlayerPosLook)
        {
            Config.getRenderGlobal().onPlayerPositionSet();
        }

        if (p_clientPreProcessPacket_0_ instanceof SPacketRespawn)
        {
            SPacketRespawn s07packetrespawn = (SPacketRespawn)p_clientPreProcessPacket_0_;
            lastDimensionId = s07packetrespawn.getDimensionID();
        }
        else if (p_clientPreProcessPacket_0_ instanceof SPacketJoinGame)
        {
            SPacketJoinGame s01packetjoingame = (SPacketJoinGame)p_clientPreProcessPacket_0_;
            lastDimensionId = s01packetjoingame.getDimension();
        }
        else
        {
            lastDimensionId = Integer.MIN_VALUE;
        }
    }
}
