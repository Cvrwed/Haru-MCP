package cc.unknown.module.impl.other;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.enums.EnumChatFormatting;

@Info(name = "Midclick", category = Category.Other)
public class MidClick extends Module {

	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private AtomicInteger prevSlot = new AtomicInteger(0);
	private AtomicInteger pearlSlot = new AtomicInteger(4);
	private AtomicBoolean x = new AtomicBoolean(false);
	private Robot bot;
	
	private ModeValue mode = new ModeValue("Mode", "Add/Remove friend", "Add/Remove friend", "Throw pearl");

	public MidClick() {
		this.registerSetting(mode);
	}
	
    @Override
    public void onEnable() {
        try {
			this.bot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@EventLink
	public void onMouse(MouseEvent e) {
		if (mc.currentScreen != null)
			return;

        if (pearlSlot.get() < 4) {
            if (pearlSlot.get() == 3)
                mc.player.inventory.currentItem = prevSlot.get();
            pearlSlot.incrementAndGet();
        }

		if (!x.get() && e.getButton() == 2) {
			if (mode.is("Add/Remove friend") && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
				final String name = mc.objectMouseOver.entityHit.getName();
				if (FriendUtil.instance.friends.contains(name)) {
					FriendUtil.instance.friends.remove(name);
					PlayerUtil.send(EnumChatFormatting.GRAY + "Removed friend " + name);
				} else {
					FriendUtil.instance.friends.add(name);
					PlayerUtil.send(EnumChatFormatting.GREEN + "Added friend " + name);
				}
			}

            if (mode.is("Throw pearl")) {
                for (int s = 0; s <= 8; s++) {
                    ItemStack item = mc.player.inventory.getStackInSlot(s);
                    if (item != null && item.getItem() instanceof ItemEnderPearl) {
                        prevSlot.set(mc.player.inventory.currentItem);
                        mc.player.inventory.currentItem = s;
                        executorService.execute(() -> {
                            bot.mousePress(InputEvent.BUTTON3_MASK);
                            bot.mouseRelease(InputEvent.BUTTON3_MASK);
                        });
                        pearlSlot.set(0);
                        x.set(true);
                        return;
                    }
                }
            }
		}
		x.set(e.getButton() == 2);
	}
}