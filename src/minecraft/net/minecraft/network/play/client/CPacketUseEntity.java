package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.vec.Vec3;
import net.minecraft.world.World;

public class CPacketUseEntity implements Packet<INetHandlerPlayServer> {
	private int entityId;
	private Mode action;
	private Vec3 hitVec;

	public CPacketUseEntity() {
	}

	public CPacketUseEntity(Entity entity, Mode action) {
		this.entityId = entity.getEntityId();
		this.action = action;
	}

	public CPacketUseEntity(Entity entity, Vec3 hitVec) {
		this(entity, Mode.INTERACT_AT);
		this.hitVec = hitVec;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readVarIntFromBuffer();
		this.action = buf.readEnumValue(Mode.class);

		if (this.action == Mode.INTERACT_AT) {
			this.hitVec = new Vec3((double) buf.readFloat(), (double) buf.readFloat(), (double) buf.readFloat());
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityId);
		buf.writeEnumValue(this.action);

		if (this.action == Mode.INTERACT_AT) {
			buf.writeFloat((float) this.hitVec.xCoord);
			buf.writeFloat((float) this.hitVec.yCoord);
			buf.writeFloat((float) this.hitVec.zCoord);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayServer handler) {
		handler.processUseEntity(this);
	}

	public Entity getEntityFromWorld(World worldIn) {
		return worldIn.getEntityByID(this.entityId);
	}

	public Mode getAction() {
		return this.action;
	}

	public Vec3 getHitVec() {
		return this.hitVec;
	}

	public enum Mode {
		INTERACT, ATTACK, INTERACT_AT;
	}
	
	public int getEntityId() {
		return entityId;
	}
}
