package cc.unknown.event.impl.network;

import cc.unknown.event.Event;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private final PacketDirection direction;
    private Packet<?> packet;

    /**
     * Constructs a PacketEvent with the specified direction and packet.
     *
     * @param direction The direction of the packet (CLIENTBOUND or SERVERBOUND).
     * @param packet    The packet associated with the event.
     */
    public PacketEvent(PacketDirection direction, Packet<?> packet) {
        this.direction = direction;
        this.packet = packet;
    }

    /**
     * Gets the packet associated with the event.
     *
     * @return The packet associated with the event.
     */
    public Packet<?> getPacket() {
        return packet;
    }

    /**
     * Sets the packet associated with the event.
     *
     * @param packet The packet to set.
     */
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    /**
     * Checks if the direction of the packet is "CLIENTBOUND".
     *
     * @return true if the direction of the packet is "CLIENTBOUND", false otherwise.
     */
    public boolean isSend() {
        return direction == PacketDirection.Outbound;
    }

    /**
     * Checks if the direction of the packet is "SERVERBOUND".
     *
     * @return true if the direction of the packet is "SERVERBOUND", false otherwise.
     */
    public boolean isReceive() {
        return direction == PacketDirection.Inbound;
    }
}