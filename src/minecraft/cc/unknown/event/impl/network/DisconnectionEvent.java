package cc.unknown.event.impl.network;

import cc.unknown.event.Event;

public class DisconnectionEvent extends Event {

	private final Side side;

	public DisconnectionEvent(Side side) {
		this.side = side;
	}

	public boolean isClient() {
		return side == Side.Client;
	}

	public boolean isServer() {
		return side == Side.Server;
	}

	public enum Side {
		Client, Server;
	}
}
