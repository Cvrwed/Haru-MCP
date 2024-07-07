package cc.unknown.module.impl.combat

import cc.unknown.event.impl.EventLink
import cc.unknown.event.impl.network.PacketEvent
import cc.unknown.event.impl.player.TickEvent
import cc.unknown.module.impl.Module
import cc.unknown.module.impl.api.Category
import cc.unknown.module.impl.api.Info
import net.minecraft.network.play.client.CPacketUseEntity

@Info(name = "WTap", category = Category.Combat)
class WTap : Module() {

    private var hits: Int = 0

    init {
        this.registerSetting(mode)
    }

    @EventLink
    fun onPacket(e: PacketEvent) {
        if (e.isSend && e.packet is CPacketUseEntity && (e.packet as CPacketUseEntity).action == ATTACK) {
            hits = 0
        }
    }

    @EventLink
    fun onTick(e: TickEvent) {
        val forward = mc.player.movementInput.moveForward

        if (mc.player.isSprinting && forward > 0) {
            hits++
            when (hits) {
                2 -> mc.player.isSprinting = false
                3 -> mc.player.isSprinting = true
            }
        }
    }
}