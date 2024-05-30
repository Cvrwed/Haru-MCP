package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.network.PacketUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@Info(name = "NoSlow", category = Category.Player)
public class NoSlow extends Module {
	public ModeValue mode = new ModeValue("Mode", "Old Grim", "Old Grim", "Vanilla", "No Item Release", "Vulcan");
	public SliderValue vForward = new SliderValue("Vanilla forward", 1.0, 0.2, 1.0, 0.1);
	public SliderValue vStrafe = new SliderValue("Vanilla strafe", 1.0, 0.2, 1.0, 0.1);

	public NoSlow() {
		this.registerSetting(mode, vForward, vStrafe);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
	    this.setSuffix("- [" + mode.getMode() + "]");
	}
	
	public void slow() {
		switch (mode.getMode()) {
		case "Old Grim":
			int slot = mc.player.inventory.currentItem;
			mc.getNetHandler().sendSilent(new CPacketHeldItemChange(slot < 8 ? slot + 1 : 0));
			mc.getNetHandler().sendSilent(new CPacketHeldItemChange(slot));
			break;
		case "Vanilla":
			mc.player.movementInput.moveForward *= vForward.getInputToFloat();
			mc.player.movementInput.moveStrafe *= vStrafe.getInputToFloat();
			break;
		case "Vulcan":
			if (mc.player.ticksExisted % 3 == 0) {
				mc.getNetHandler().sendQueue(new CPacketPlayerBlockPlacement(mc.player.getHeldItem()));
			}
			break;
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend()) {
			final Packet<INetHandlerPlayServer> p = (Packet<INetHandlerPlayServer>) e.getPacket();
			if (mode.is("No Item Release")) {
				if (p instanceof CPacketPlayerDigging) {
					CPacketPlayerDigging wrapper = (CPacketPlayerDigging) p;
					if (wrapper.getStatus() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
						if (!(mc.player.getHeldItem().getItem() instanceof ItemBow)) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
