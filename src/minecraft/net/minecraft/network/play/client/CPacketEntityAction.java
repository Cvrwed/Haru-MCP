package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketEntityAction implements Packet<INetHandlerPlayServer> {
	private int entityID;
	private Mode action;
	private int auxData;

	public CPacketEntityAction() {
	}

	public CPacketEntityAction(Entity entity, Mode action) {
		this(entity, action, 0);
	}

	public CPacketEntityAction(Entity entity, Mode action, int auxData) {
		this.entityID = entity.getEntityId();
		this.action = action;
		this.auxData = auxData;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityID = buf.readVarIntFromBuffer();
		this.action = buf.readEnumValue(Mode.class);
		this.auxData = buf.readVarIntFromBuffer();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(entityID);
		buf.writeEnumValue(action);
		buf.writeVarIntToBuffer(auxData);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayServer handler) {
		handler.processEntityAction(this);
	}

	public Mode getAction() {
		return this.action;
	}

	public int getAuxData() {
		return this.auxData;
	}

	public enum Mode {
		START_SNEAKING, STOP_SNEAKING, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, RIDING_JUMP, OPEN_INVENTORY;
	}
}
