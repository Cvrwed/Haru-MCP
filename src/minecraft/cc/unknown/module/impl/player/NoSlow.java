package cc.unknown.module.impl.player;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.enums.EnumFacing;

@Info(name = "NoSlow", category = Category.Player)
public class NoSlow extends Module {
	public ModeValue mode = new ModeValue("Mode", "Old Grim", "Old Grim", "Vanilla", "C08 Tick", "Legit", "Polar");
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
		final Item item = mc.player.getHeldItem().getItem();
		switch (mode.getMode()) {
		case "Old Grim":
			int slot = mc.player.inventory.currentItem;
			mc.getNetHandler().sendSilent(new CPacketHeldItemChange(slot < 8 ? slot + 1 : 0));
			mc.getNetHandler().sendSilent(new CPacketHeldItemChange(slot));
			break;
		case "Legit":
			mc.getNetHandler().sendQueue(new CPacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));
			mc.getNetHandler().sendQueue(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
			break;
		case "Vanilla":
			mc.player.movementInput.moveForward *= vForward.getInputToFloat();
			mc.player.movementInput.moveStrafe *= vStrafe.getInputToFloat();
			break;
		case "Polar":
			if (item instanceof ItemSword) {
				mc.getNetHandler().sendQueue(new CPacketPlayerBlockPlacement(mc.player.getHeldItem()));
				mc.getNetHandler().sendQueue(new CPacketInput(0, 0.82f, false, false));
            }
			break;
		case "C08 Tick":
			if (mc.player.ticksExisted % 3 == 0) {
				mc.getNetHandler().sendQueue(new CPacketPlayerBlockPlacement(mc.player.getHeldItem()));
			}
			break;
		}
	}
}
