package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;

@Info(name = "SprintReset", category = Category.Combat)
public class SprintReset extends Module {

	private ModeValue mode = new ModeValue("Mode", "WTap", "WTap");
	private int hits;

	public SprintReset() {
		this.registerSetting(mode);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@EventLink
	public void onPacket(PacketEvent e) {
        if (e.isSend() && e.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) e.getPacket()).getAction() == CPacketUseEntity.Mode.ATTACK) {
            hits = 0;
        }
    }

	@EventLink
	public void onTick(TickEvent e) {
        double forward = mc.player.movementInput.moveForward;

        if (mc.player.isSprinting() && forward > 0) {
            hits++;
            switch (hits) {
                case 2:
                    mc.player.setSprinting(false);
                case 3:
                    mc.player.setSprinting(true);
            }
        }
    }

}
