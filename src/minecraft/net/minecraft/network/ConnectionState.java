package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.network.handshake.client.CHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.server.SPacketServerDisconnect;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketMapChunkBulk;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketUpdateSign;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketDisplayScoreboard;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketClientDisconnect;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketSetCompressionLevel;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketUpdateEntityNBT;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.network.status.server.SPacketPong;
import org.apache.logging.log4j.LogManager;

public enum ConnectionState {
	HANDSHAKING(-1) {
		{
			this.registerPacket(PacketDirection.Inbound, CHandshake.class);
		}
	},
	PLAY(0) {
		{
			this.registerPacket(PacketDirection.Outbound, SPacketKeepAlive.class);
			this.registerPacket(PacketDirection.Outbound, SPacketJoinGame.class);
			this.registerPacket(PacketDirection.Outbound, SPacketChat.class);
			this.registerPacket(PacketDirection.Outbound, SPacketTimeUpdate.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityEquipment.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnPosition.class);
			this.registerPacket(PacketDirection.Outbound, SPacketUpdateHealth.class);
			this.registerPacket(PacketDirection.Outbound, SPacketRespawn.class);
			this.registerPacket(PacketDirection.Outbound, SPacketPlayerPosLook.class);
			this.registerPacket(PacketDirection.Outbound, SPacketHeldItemChange.class);
			this.registerPacket(PacketDirection.Outbound, SPacketUseBed.class);
			this.registerPacket(PacketDirection.Outbound, SPacketAnimation.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnPlayer.class);
			this.registerPacket(PacketDirection.Outbound, SPacketCollectItem.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnObject.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnMob.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnPainting.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnExperienceOrb.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityVelocity.class);
			this.registerPacket(PacketDirection.Outbound, SPacketDestroyEntities.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntity.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntity.SPacketEntityRelMove.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntity.SPacketEntityLook.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntity.SPacketEntityLookMove.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityTeleport.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityHeadLook.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityStatus.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityAttach.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityMetadata.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityEffect.class);
			this.registerPacket(PacketDirection.Outbound, SPacketRemoveEntityEffect.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSetExperience.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEntityProperties.class);
			this.registerPacket(PacketDirection.Outbound, SPacketChunkData.class);
			this.registerPacket(PacketDirection.Outbound, SPacketMultiBlockChange.class);
			this.registerPacket(PacketDirection.Outbound, SPacketBlockChange.class);
			this.registerPacket(PacketDirection.Outbound, SPacketBlockAction.class);
			this.registerPacket(PacketDirection.Outbound, SPacketBlockBreakAnim.class);
			this.registerPacket(PacketDirection.Outbound, SPacketMapChunkBulk.class);
			this.registerPacket(PacketDirection.Outbound, SPacketExplosion.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEffect.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSoundEffect.class);
			this.registerPacket(PacketDirection.Outbound, SPacketParticles.class);
			this.registerPacket(PacketDirection.Outbound, SPacketChangeGameState.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSpawnGlobalEntity.class);
			this.registerPacket(PacketDirection.Outbound, SPacketOpenWindow.class);
			this.registerPacket(PacketDirection.Outbound, SPacketCloseWindow.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSetSlot.class);
			this.registerPacket(PacketDirection.Outbound, SPacketWindowItems.class);
			this.registerPacket(PacketDirection.Outbound, SPacketWindowProperty.class);
			this.registerPacket(PacketDirection.Outbound, SPacketConfirmTransaction.class);
			this.registerPacket(PacketDirection.Outbound, SPacketUpdateSign.class);
			this.registerPacket(PacketDirection.Outbound, SPacketMaps.class);
			this.registerPacket(PacketDirection.Outbound, SPacketUpdateTileEntity.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSignEditorOpen.class);
			this.registerPacket(PacketDirection.Outbound, SPacketStatistics.class);
			this.registerPacket(PacketDirection.Outbound, SPacketPlayerListItem.class);
			this.registerPacket(PacketDirection.Outbound, SPacketPlayerAbilities.class);
			this.registerPacket(PacketDirection.Outbound, SPacketTabComplete.class);
			this.registerPacket(PacketDirection.Outbound, SPacketScoreboardObjective.class);
			this.registerPacket(PacketDirection.Outbound, SPacketUpdateScore.class);
			this.registerPacket(PacketDirection.Outbound, SPacketDisplayScoreboard.class);
			this.registerPacket(PacketDirection.Outbound, SPacketTeams.class);
			this.registerPacket(PacketDirection.Outbound, SPacketCustomPayload.class);
			this.registerPacket(PacketDirection.Outbound, SPacketClientDisconnect.class);
			this.registerPacket(PacketDirection.Outbound, SPacketServerDifficulty.class);
			this.registerPacket(PacketDirection.Outbound, SPacketCombatEvent.class);
			this.registerPacket(PacketDirection.Outbound, SPacketCamera.class);
			this.registerPacket(PacketDirection.Outbound, SPacketWorldBorder.class);
			this.registerPacket(PacketDirection.Outbound, SPacketTitle.class);
			this.registerPacket(PacketDirection.Outbound, SPacketSetCompressionLevel.class);
			this.registerPacket(PacketDirection.Outbound, SPacketPlayerListHeaderFooter.class);
			this.registerPacket(PacketDirection.Outbound, SPacketResourcePackSend.class);
			this.registerPacket(PacketDirection.Outbound, SPacketUpdateEntityNBT.class);
			this.registerPacket(PacketDirection.Inbound, CPacketKeepAlive.class);
			this.registerPacket(PacketDirection.Inbound, CPacketChatMessage.class);
			this.registerPacket(PacketDirection.Inbound, CPacketUseEntity.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayer.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayer.CPacketPlayerPosition.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayer.CPacketPlayerLook.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayer.CPacketPlayerPosLook.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayerDigging.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayerBlockPlacement.class);
			this.registerPacket(PacketDirection.Inbound, CPacketHeldItemChange.class);
			this.registerPacket(PacketDirection.Inbound, CPacketAnimation.class);
			this.registerPacket(PacketDirection.Inbound, CPacketEntityAction.class);
			this.registerPacket(PacketDirection.Inbound, CPacketInput.class);
			this.registerPacket(PacketDirection.Inbound, CPacketCloseWindow.class);
			this.registerPacket(PacketDirection.Inbound, CPacketClickWindow.class);
			this.registerPacket(PacketDirection.Inbound, CPacketConfirmTransaction.class);
			this.registerPacket(PacketDirection.Inbound, CPacketCreativeInventoryAction.class);
			this.registerPacket(PacketDirection.Inbound, CPacketEnchantItem.class);
			this.registerPacket(PacketDirection.Inbound, CPacketUpdateSign.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPlayerAbilities.class);
			this.registerPacket(PacketDirection.Inbound, CPacketTabComplete.class);
			this.registerPacket(PacketDirection.Inbound, CPacketClientSettings.class);
			this.registerPacket(PacketDirection.Inbound, CPacketClientStatus.class);
			this.registerPacket(PacketDirection.Inbound, CPacketCustomPayload.class);
			this.registerPacket(PacketDirection.Inbound, CPacketSpectate.class);
			this.registerPacket(PacketDirection.Inbound, CPacketResourcePackStatus.class);
		}
	},
	STATUS(1) {
		{
			this.registerPacket(PacketDirection.Inbound, CPacketServerQuery.class);
			this.registerPacket(PacketDirection.Outbound, SPacketServerInfo.class);
			this.registerPacket(PacketDirection.Inbound, CPacketPing.class);
			this.registerPacket(PacketDirection.Outbound, SPacketPong.class);
		}
	},
	LOGIN(2) {
		{
			this.registerPacket(PacketDirection.Outbound, SPacketServerDisconnect.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEncryptionRequest.class);
			this.registerPacket(PacketDirection.Outbound, SPacketLoginSuccess.class);
			this.registerPacket(PacketDirection.Outbound, SPacketEnableCompression.class);
			this.registerPacket(PacketDirection.Inbound, CPacketLoginStart.class);
			this.registerPacket(PacketDirection.Inbound, CPacketEncryptionResponse.class);
		}
	};

	private static int field_181136_e = -1;
	private static int field_181137_f = 2;
	private static final ConnectionState[] STATES_BY_ID = new ConnectionState[field_181137_f - field_181136_e
			+ 1];
	private static final Map<Class<? extends Packet>, ConnectionState> STATES_BY_CLASS = Maps
			.<Class<? extends Packet>, ConnectionState>newHashMap();
	private final int id;
	private final Map<PacketDirection, BiMap<Integer, Class<? extends Packet>>> directionMaps;

	private ConnectionState(int protocolId) {
		this.directionMaps = Maps.newEnumMap(PacketDirection.class);
		this.id = protocolId;
	}

	protected ConnectionState registerPacket(PacketDirection direction, Class<? extends Packet> packetClass) {
		BiMap<Integer, Class<? extends Packet>> bimap = (BiMap) this.directionMaps.get(direction);

		if (bimap == null) {
			bimap = HashBiMap.<Integer, Class<? extends Packet>>create();
			this.directionMaps.put(direction, bimap);
		}

		if (bimap.containsValue(packetClass)) {
			String s = direction + " packet " + packetClass + " is already known to ID "
					+ bimap.inverse().get(packetClass);
			LogManager.getLogger().fatal(s);
			throw new IllegalArgumentException(s);
		} else {
			bimap.put(Integer.valueOf(bimap.size()), packetClass);
			return this;
		}
	}

	public Integer getPacketId(PacketDirection direction, Packet packetIn) {
		return (Integer) ((BiMap) this.directionMaps.get(direction)).inverse().get(packetIn.getClass());
	}

	public Packet getPacket(PacketDirection direction, int packetId)
			throws InstantiationException, IllegalAccessException {
		Class<? extends Packet> oclass = (Class) ((BiMap) this.directionMaps.get(direction))
				.get(Integer.valueOf(packetId));
		return oclass == null ? null : (Packet) oclass.newInstance();
	}

	public int getId() {
		return this.id;
	}

	public static ConnectionState getById(int stateId) {
		return stateId >= field_181136_e && stateId <= field_181137_f ? STATES_BY_ID[stateId - field_181136_e] : null;
	}

	public static ConnectionState getFromPacket(Packet packetIn) {
		return (ConnectionState) STATES_BY_CLASS.get(packetIn.getClass());
	}

	static {
		for (ConnectionState enumconnectionstate : values()) {
			int i = enumconnectionstate.getId();

			if (i < field_181136_e || i > field_181137_f) {
				throw new Error("Invalid protocol ID " + Integer.toString(i));
			}

			STATES_BY_ID[i - field_181136_e] = enumconnectionstate;

			for (PacketDirection enumpacketdirection : enumconnectionstate.directionMaps.keySet()) {
				for (Class<? extends Packet> oclass : (enumconnectionstate.directionMaps.get(enumpacketdirection))
						.values()) {
					if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != enumconnectionstate) {
						throw new Error("Packet " + oclass + " is already assigned to protocol "
								+ STATES_BY_CLASS.get(oclass) + " - can\'t reassign to " + enumconnectionstate);
					}

					try {
						oclass.newInstance();
					} catch (Throwable var10) {
						throw new Error("Packet " + oclass + " fails instantiation checks! " + oclass);
					}

					STATES_BY_CLASS.put(oclass, enumconnectionstate);
				}
			}
		}
	}
}
