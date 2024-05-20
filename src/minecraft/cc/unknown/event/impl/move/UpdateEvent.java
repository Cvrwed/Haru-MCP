package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class UpdateEvent extends Event {

	private final Mode mode;
	
	public UpdateEvent(Mode mode) {
		this.mode = mode;
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
