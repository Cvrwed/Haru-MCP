package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.client.CPacketEncryptionResponse;

public interface INetHandlerLoginServer extends INetHandler
{
    void processLoginStart(CPacketLoginStart packetIn);

    void processEncryptionResponse(CPacketEncryptionResponse packetIn);
}
