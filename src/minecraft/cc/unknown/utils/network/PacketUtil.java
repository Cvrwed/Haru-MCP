package cc.unknown.utils.network;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import cc.unknown.utils.Loona;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.*;

public class PacketUtil implements Loona {
    public static final ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
    
	public static void send(Packet<?>[] i) {
        NetworkManager netManager = mc.getNetHandler() != null ? mc.getNetHandler().getNetworkManager() : null;
        if (netManager != null && netManager.isChannelOpen()) {
            netManager.flushOutboundQueue();
            for (Packet<?> p : i) {
                netManager.dispatchPacket(p, null);
            }
        } else if (netManager != null) {
            try {
                netManager.readWriteLock.writeLock().lock();
                for (Packet<?> p : i) {
                    netManager.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(p, Arrays.asList((GenericFutureListener<? extends Future<? super Void>>) null).toArray(new GenericFutureListener[0])));
                }
            } finally {
                netManager.readWriteLock.writeLock().unlock();
            }
        }
    }
	
	public static void handlePacket(Packet<? extends INetHandlerPlayClient> p) {
		INetHandlerPlayClient netHandler = (INetHandlerPlayClient) mc.getNetHandler();

        if (p instanceof SPacketKeepAlive) {
            netHandler.handleKeepAlive((SPacketKeepAlive) p);
        } else if (p instanceof  SPacketJoinGame) {
            netHandler.handleJoinGame((SPacketJoinGame) p);
        } else if (p instanceof SPacketChat) {
            netHandler.handleChat((SPacketChat) p);
        } else if (p instanceof SPacketTimeUpdate) {
            netHandler.handleTimeUpdate((SPacketTimeUpdate) p);
        } else if (p instanceof SPacketEntityEquipment) {
            netHandler.handleEntityEquipment((SPacketEntityEquipment) p);
        } else if (p instanceof SPacketSpawnPosition) {
            netHandler.handleSpawnPosition((SPacketSpawnPosition) p);
        } else if (p instanceof SPacketUpdateHealth) {
            netHandler.handleUpdateHealth((SPacketUpdateHealth) p);
        } else if (p instanceof SPacketRespawn) {
            netHandler.handleRespawn((SPacketRespawn) p);
        } else if (p instanceof SPacketPlayerPosLook) {
            netHandler.handlePlayerPosLook((SPacketPlayerPosLook) p);
        } else if (p instanceof SPacketHeldItemChange) {
            netHandler.handleHeldItemChange((SPacketHeldItemChange) p);
        } else if (p instanceof SPacketSpawnPainting) {
            netHandler.handleSpawnPainting((SPacketSpawnPainting) p);
        } else if (p instanceof SPacketUseBed) {
            netHandler.handleUseBed((SPacketUseBed) p);
        } else if (p instanceof SPacketAnimation) {
            netHandler.handleAnimation((SPacketAnimation) p);
        } else if (p instanceof SPacketSpawnPlayer) {
            netHandler.handleSpawnPlayer((SPacketSpawnPlayer) p);
        } else if (p instanceof SPacketCollectItem) {
            netHandler.handleCollectItem((SPacketCollectItem) p);
        } else if (p instanceof SPacketSpawnObject) {
            netHandler.handleSpawnObject((SPacketSpawnObject) p);
        } else if (p instanceof SPacketSpawnMob) {
            netHandler.handleSpawnMob((SPacketSpawnMob) p);
        } else if (p instanceof SPacketSpawnExperienceOrb) {
            netHandler.handleSpawnExperienceOrb((SPacketSpawnExperienceOrb) p);
        } else if (p instanceof SPacketEntityVelocity) {
            netHandler.handleEntityVelocity((SPacketEntityVelocity) p);
        } else if (p instanceof SPacketDestroyEntities) {
            netHandler.handleDestroyEntities((SPacketDestroyEntities) p);
        } else if (p instanceof SPacketEntity) {
            netHandler.handleEntityMovement((SPacketEntity) p);
        } else if (p instanceof SPacketEntityTeleport) {
            netHandler.handleEntityTeleport((SPacketEntityTeleport) p);
        } else if (p instanceof SPacketEntityStatus) {
            netHandler.handleEntityStatus((SPacketEntityStatus) p);
        } else if (p instanceof SPacketEntityHeadLook) {
            netHandler.handleEntityHeadLook((SPacketEntityHeadLook) p);
        } else if (p instanceof SPacketEntityAttach) {
            netHandler.handleEntityAttach((SPacketEntityAttach) p);
        } else if (p instanceof SPacketEntityMetadata) {
            netHandler.handleEntityMetadata((SPacketEntityMetadata) p);
        } else if (p instanceof SPacketEntityEffect) {
            netHandler.handleEntityEffect((SPacketEntityEffect) p);
        } else if (p instanceof SPacketRemoveEntityEffect) {
            netHandler.handleRemoveEntityEffect((SPacketRemoveEntityEffect) p);
        } else if (p instanceof SPacketSetExperience) {
            netHandler.handleSetExperience((SPacketSetExperience) p);
        } else if (p instanceof SPacketEntityProperties) {
            netHandler.handleEntityProperties((SPacketEntityProperties) p);
        } else if (p instanceof SPacketChunkData) {
            netHandler.handleChunkData((SPacketChunkData) p);
        } else if (p instanceof SPacketMultiBlockChange) {
            netHandler.handleMultiBlockChange((SPacketMultiBlockChange) p);
        } else if (p instanceof SPacketBlockChange) {
            netHandler.handleBlockChange((SPacketBlockChange) p);
        } else if (p instanceof SPacketBlockAction) {
            netHandler.handleBlockAction((SPacketBlockAction) p);
        } else if (p instanceof SPacketBlockBreakAnim) {
            netHandler.handleBlockBreakAnim((SPacketBlockBreakAnim) p);
        } else if (p instanceof SPacketMapChunkBulk) {
            netHandler.handleMapChunkBulk((SPacketMapChunkBulk) p);
        } else if (p instanceof SPacketExplosion) {
            netHandler.handleExplosion((SPacketExplosion) p);
        } else if (p instanceof SPacketEffect) {
            netHandler.handleEffect((SPacketEffect) p);
        } else if (p instanceof SPacketSoundEffect) {
            netHandler.handleSoundEffect((SPacketSoundEffect) p);
        } else if (p instanceof SPacketParticles) {
            netHandler.handleParticles((SPacketParticles) p);
        } else if (p instanceof SPacketChangeGameState) {
            netHandler.handleChangeGameState((SPacketChangeGameState) p);
        } else if (p instanceof SPacketSpawnGlobalEntity) {
            netHandler.handleSpawnGlobalEntity((SPacketSpawnGlobalEntity) p);
        } else if (p instanceof SPacketOpenWindow) {
            netHandler.handleOpenWindow((SPacketOpenWindow) p);
        } else if (p instanceof SPacketCloseWindow) {
            netHandler.handleCloseWindow((SPacketCloseWindow) p);
        } else if (p instanceof SPacketSetSlot) {
            netHandler.handleSetSlot((SPacketSetSlot) p);
        } else if (p instanceof SPacketWindowItems) {
            netHandler.handleWindowItems((SPacketWindowItems) p);
        } else if (p instanceof SPacketWindowProperty) {
            netHandler.handleWindowProperty((SPacketWindowProperty) p);
        } else if (p instanceof SPacketConfirmTransaction) {
            netHandler.handleConfirmTransaction((SPacketConfirmTransaction) p);
        } else if (p instanceof SPacketUpdateSign) {
            netHandler.handleUpdateSign((SPacketUpdateSign) p);
        } else if (p instanceof SPacketMaps) {
            netHandler.handleMaps((SPacketMaps) p);
        } else if (p instanceof SPacketUpdateTileEntity) {
            netHandler.handleUpdateTileEntity((SPacketUpdateTileEntity) p);
        } else if (p instanceof SPacketSignEditorOpen) {
            netHandler.handleSignEditorOpen((SPacketSignEditorOpen) p);
        } else if (p instanceof SPacketStatistics) {
            netHandler.handleStatistics((SPacketStatistics) p);
        } else if (p instanceof SPacketPlayerListItem) {
            netHandler.handlePlayerListItem((SPacketPlayerListItem) p);
        } else if (p instanceof SPacketPlayerAbilities) {
            netHandler.handlePlayerAbilities((SPacketPlayerAbilities) p);
        } else if (p instanceof SPacketTabComplete) {
            netHandler.handleTabComplete((SPacketTabComplete) p);
        } else if (p instanceof SPacketScoreboardObjective) {
            netHandler.handleScoreboardObjective((SPacketScoreboardObjective) p);
        } else if (p instanceof SPacketUpdateScore) {
            netHandler.handleUpdateScore((SPacketUpdateScore) p);
        } else if (p instanceof SPacketDisplayScoreboard) {
            netHandler.handleDisplayScoreboard((SPacketDisplayScoreboard) p);
        } else if (p instanceof SPacketTeams) {
            netHandler.handleTeams((SPacketTeams) p);
        } else if (p instanceof SPacketCustomPayload) {
            netHandler.handleCustomPayload((SPacketCustomPayload) p);
        } else if (p instanceof SPacketClientDisconnect) {
            netHandler.handleDisconnect((SPacketClientDisconnect) p);
        } else if (p instanceof SPacketServerDifficulty) {
            netHandler.handleServerDifficulty((SPacketServerDifficulty) p);
        } else if (p instanceof SPacketCombatEvent) {
            netHandler.handleCombatEvent((SPacketCombatEvent) p);
        } else if (p instanceof SPacketCamera) {
            netHandler.handleCamera((SPacketCamera) p);
        } else if (p instanceof SPacketWorldBorder) {
            netHandler.handleWorldBorder((SPacketWorldBorder) p);
        } else if (p instanceof SPacketTitle) {
            netHandler.handleTitle((SPacketTitle) p);
        } else if (p instanceof SPacketSetCompressionLevel) {
            netHandler.handleSetCompressionLevel((SPacketSetCompressionLevel) p);
        } else if (p instanceof SPacketPlayerListHeaderFooter) {
            netHandler.handlePlayerListHeaderFooter((SPacketPlayerListHeaderFooter) p);
        } else if (p instanceof SPacketResourcePackSend) {
            netHandler.handleResourcePack((SPacketResourcePackSend) p);
        } else if (p instanceof SPacketUpdateEntityNBT) {
            netHandler.handleEntityNBT((SPacketUpdateEntityNBT) p);
        }
    }
}
