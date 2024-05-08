package cc.unknown.event.impl.move;

import cc.unknown.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;

public class PreUpdateEvent extends Event {
	private final EntityPlayerSP player;

	public PreUpdateEvent(EntityPlayerSP player) {
		this.player = player;
	}

	public EntityPlayerSP getPlayer() {
		return this.player;
	}
}
