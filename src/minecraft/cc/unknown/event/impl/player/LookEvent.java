package cc.unknown.event.impl.player;

import cc.unknown.event.Event;

public class LookEvent extends Event {

	private float yaw, pitch;

	public LookEvent(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
