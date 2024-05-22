package cc.unknown.module.impl.other;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.enums.EnumChatFormatting;

@Register(name = "Midclick", category = Category.Other)
public class MidClick extends Module {

    private AtomicBoolean x = new AtomicBoolean(false);
    private AtomicInteger prevSlot = new AtomicInteger(0);
    private AtomicInteger pearlEvent = new AtomicInteger(4);
    private ModeValue mode = new ModeValue("Mode", "Add/Remove friend", "Add/Remove friend", "Throw pearl");

    public MidClick() {
        this.registerSetting(mode);
    }

    @EventLink
    public void onMouse(MouseEvent e) {
        if (mc.currentScreen != null)
            return;

        if (pearlEvent.get() < 4) {
            if (pearlEvent.get() == 3) {
                mc.player.inventory.currentItem = prevSlot.get();
            }
            pearlEvent.incrementAndGet();
        }

        if (!x.get() && e.getButton() == 2) {
            if (mode.is("Add/Remove friend") && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                handleFriendEvent((EntityPlayer) mc.objectMouseOver.entityHit);
            }

            if (mode.is("Throw pearl")) {
                throwPearl();
            }
        }

        x.set(e.getButton() == 2);
    }
    
    private void handleFriendEvent(EntityPlayer player) {
        if (!FriendUtil.instance.isAFriend(player)) {
            FriendUtil.instance.addFriend(player);
            if (Haru.instance.getHudConfig() != null) {
                Haru.instance.getHudConfig().saveHud();
            }
            PlayerUtil.send(EnumChatFormatting.GRAY + player.getName() + " was added to your friends.");
        } else {
            FriendUtil.instance.removeFriend(player);
            if (Haru.instance.getHudConfig() != null) {
                Haru.instance.getHudConfig().saveHud();
            }
            PlayerUtil.send(EnumChatFormatting.GRAY + player.getName() + " was removed from your friends.");
        }
    }

    private void throwPearl() {
        for (int s = 0; s <= 8; s++) {
            ItemStack item = mc.player.inventory.getStackInSlot(s);
            if (item != null && item.getItem() instanceof ItemEnderPearl) {
                prevSlot.set(mc.player.inventory.currentItem);
                mc.player.inventory.currentItem = s;
                sendEnderPearlPacket();
                pearlEvent.set(0);
                x.set(true);
                return;
            }
        }
    }
    
    private void sendEnderPearlPacket() {
        ItemStack pearlStack = mc.player.inventoryContainer.getSlot(pearlEvent.get() + 36).getStack();
        Packet<?> packet = new CPacketPlayerBlockPlacement(
            new BlockPos(-1, -1, -1), 255, pearlStack, 0, 0, 0);
        mc.getNetHandler().sendQueue(packet);
    }
}