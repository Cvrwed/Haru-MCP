package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AttackEvent extends Event {

	private EntityPlayer playerIn;
	private Entity target;
	private final Mode mode;

	public AttackEvent(Mode mode, EntityPlayer playerIn, Entity target) {
		this.mode = mode;
		this.playerIn = playerIn;
		this.target = target;
	}

	public EntityPlayer getPlayerIn() {
		return playerIn;
	}

	public void setPlayerIn(EntityPlayer playerIn) {
		this.playerIn = playerIn;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public boolean isPre() {
		return mode == Mode.Pre;
	}
	
	public boolean isPost() {
		return mode == Mode.Post;
	}

	public enum Mode {
		Pre, Post;
	}
}
