package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AttackEvent extends Event {

    /** The target entity of the attack event. */
    private Entity target;

    /**
     * Constructs a new {@code AttackEvent} with the specified target entity.
     *
     * @param target the target entity of the attack event
     */
    public AttackEvent(Entity target) {
        this.target = target;
    }

    /**
     * Returns the target entity of the attack event.
     *
     * @return the target entity of the attack event
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Sets the target entity of the attack event.
     *
     * @param target the target entity to set
     */
    public void setTarget(Entity target) {
        this.target = target;
    }
}