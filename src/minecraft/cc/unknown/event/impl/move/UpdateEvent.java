package cc.unknown.event.impl.move;

import cc.unknown.event.Event;

public class UpdateEvent extends Event {

    /** The mode of the update event (Pre or Post). */
    private final Mode mode;

    /**
     * Constructs a new {@code UpdateEvent} with the specified mode.
     *
     * @param mode the mode of the update event (Pre or Post)
     */
    public UpdateEvent(Mode mode) {
        this.mode = mode;
    }

    /**
     * Returns true if the mode of the update event is "Pre", false otherwise.
     *
     * @return true if the mode is "Pre", false otherwise
     */
    public boolean isPre() {
        return mode == Mode.Pre;
    }

    /**
     * Returns true if the mode of the update event is "Post", false otherwise.
     *
     * @return true if the mode is "Post", false otherwise
     */
    public boolean isPost() {
        return mode == Mode.Post;
    }

    /**
     * Enumerates the possible modes of an update event (Pre or Post).
     */
    public enum Mode {
        Pre, Post
    }
}