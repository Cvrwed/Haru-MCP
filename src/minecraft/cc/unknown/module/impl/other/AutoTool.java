package cc.unknown.module.impl.other;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.MovingObjectPosition;

@Register(name = "AutoTool", category = Category.Other)
public class AutoTool extends Module {
	private int prevItem = 0;
	private boolean mining = false;
	private int bestSlot = 0;

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isSend()) {
			if (e.getPacket() instanceof CPacketUseEntity) {
				CPacketUseEntity wrapper = (CPacketUseEntity) e.getPacket();
				if (wrapper.getAction() == CPacketUseEntity.Mode.ATTACK)
					mining = false;
			}
		}
	}

	@EventLink
	public void onRender(RenderEvent e) {
		if (e.is3D()) {
			if (!mc.gameSettings.keyBindUseItem.isKeyDown() && mc.gameSettings.keyBindAttack.isKeyDown()
					&& mc.objectMouseOver != null
					&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

				int bestSpeed = 0;
				bestSlot = -1;

				if (!mining) {
					prevItem = mc.player.inventory.currentItem;
				}

				Block block = mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

				for (int i = 0; i <= 8; i++) {
					ItemStack item = mc.player.inventory.getStackInSlot(i);
					if (item == null)
						continue;

					float speed = item.getStrVsBlock(block);

					if (speed > bestSpeed) {
						bestSpeed = (int) speed;
						bestSlot = i;
					}

					if (bestSlot != -1) {
						mc.player.inventory.currentItem = bestSlot;
					}
				}
				mining = true;
			} else {
				if (mining) {
					mining = false;
				} else {
					prevItem = mc.player.inventory.currentItem;
				}
			}
		}
	}
}