package cc.unknown.event.impl.network;

import cc.unknown.event.Event;

public class DisconnectionEvent extends Event {

    /** The side of the disconnection event (Client or Server). */
    private final Side side;

    /**
     * Constructs a new {@code DisconnectionEvent} with the specified side.
     *
     * @param side the side of the disconnection event (Client or Server)
     */
    public DisconnectionEvent(Side side) {
        this.side = side;
    }

    /**
     * Returns true if the side of the disconnection event is "Client", false otherwise.
     *
     * @return true if the side is "Client", false otherwise
     */
    public boolean isClient() {
        return side == Side.Client;
    }

    /**
     * Returns true if the side of the disconnection event is "Server", false otherwise.
     *
     * @return true if the side is "Server", false otherwise
     */
    public boolean isServer() {
        return side == Side.Server;
    }

    /**
     * Enumerates the possible sides of a disconnection event (Client or Server).
     */
    public enum Side {
        Client, Server
    }
}