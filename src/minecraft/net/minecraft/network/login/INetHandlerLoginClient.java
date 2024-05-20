package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SPacketServerDisconnect;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.login.server.SPacketEnableCompression;

public interface INetHandlerLoginClient extends INetHandler
{
    void handleEncryptionRequest(SPacketEncryptionRequest packetIn);

    void handleLoginSuccess(SPacketLoginSuccess packetIn);

    void handleDisconnect(SPacketServerDisconnect packetIn);

    void handleEnableCompression(SPacketEnableCompression packetIn);
}
