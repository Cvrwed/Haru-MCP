package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AttackEvent extends Event {

	private Entity target;

	public AttackEvent(Entity target) {
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
}
