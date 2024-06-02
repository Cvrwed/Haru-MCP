package cc.unknown.command.commands;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.api.Flips;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

@Flips(name = "Game", alias = "join", desc = "It automatically enters the selected minigame. [Only for Universocraft]", syntax = ".game <mini game> <lobby>")
public class GameCommand extends Command {

    private HashMap<String, Item> hashMap = new HashMap<>();
    private boolean joining;
    private Item item;
    private int lobby;
    private int stage;
    private boolean foundItem;

    public GameCommand() {
        this.hashMap.put("sw", Items.bow);
        this.hashMap.put("tsw", Items.arrow);
        this.hashMap.put("bw", Items.bed);
        this.hashMap.put("tnt", Items.gunpowder);
        this.hashMap.put("pgames", Items.cake);
        this.hashMap.put("arena", Items.diamond_sword);
        Haru.instance.getEventBus().register(this);
    }
    
    @Override
    public void onExecute(String[] args) {
        AtomicReference<String> message = new AtomicReference<>("");

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            clearChat();
            message.set(getList());
        } else {
            if (args.length < 2 || args.length == 0) {
                message.set(getColor("Red") + " Syntax Error. Use: " + syntax);
                return;
            }

            String gameName = args[0];
            int lobbyNumber;
            int delays;

            if (!this.hashMap.containsKey(gameName)) {
                message.set(getColor("Red") + " Invalid game. Use: .game list");
                return;
            }

            if (!args[1].matches("\\d+")) {
                message.set(getColor("Red") + " Invalid number.");
                return;
            }

            lobbyNumber = Integer.parseInt(args[1]);

            if (lobbyNumber == 0) {
                message.set(getColor("Red") + " Invalid lobby.");
                return;
            }


            this.startJoining(hashMap.get(gameName), lobbyNumber);
            
            message.set(getColor("Yellow") + " Have a coffee while I try to get you into the mini-game.");
        }

        this.sendChat(message.get());
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (e.isReceive() && PlayerUtil.inGame()) {
            if (e.getPacket() instanceof SPacketPlayerPosLook)
                this.joining = false;
            if (this.stage == 2 && e.getPacket() instanceof SPacketOpenWindow)
                this.stage = 3;
            if (this.stage >= 3 && e.getPacket() instanceof SPacketCloseWindow)
                this.stage = 0;
        }
    }

    @EventLink
    public void onTick(TickEvent e) {
        if (PlayerUtil.inGame()) {

            if (mc.currentScreen instanceof GuiChat || PlayerUtil.isMoving()) {
                this.joining = false;
                return;
            }

            if (!this.joining)
                return;

            EntityPlayerSP player = mc.player;

            switch (this.stage) {

                case 0:
                    if (!this.foundItem && player.inventoryContainer.getSlot(36).getHasStack()) {
                        mc.getNetHandler().sendQueue((Packet<INetHandlerPlayServer>) new CPacketPlayerBlockPlacement(player.getHeldItem()));
                        this.stage++;
                    }
                    break;
                case 1:
                    if (mc.currentScreen instanceof GuiContainer) {
                        GuiContainer container = (GuiContainer) mc.currentScreen;
                        List<ItemStack> inventory = container.inventorySlots.getInventory();
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack slot = inventory.get(i);
                            if (slot != null)
                                if (slot.getItem() == this.item) {
                                    mc.getNetHandler().sendQueue((Packet<INetHandlerPlayServer>) new CPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
                                    this.stage++;
                                    break;
                                }
                        }
                    }
                    break;
                case 3:
                    if (mc.currentScreen instanceof GuiContainer) {
                        GuiContainer container = (GuiContainer) mc.currentScreen;
                        List<ItemStack> inventory = container.inventorySlots.getInventory();
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack slot = inventory.get(i);
                            if (slot != null)
                                if (slot.stackSize == this.lobby) {
                                    mc.getNetHandler().sendQueue((Packet<INetHandlerPlayServer>) new CPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
                                    this.stage++;
                                    break;
                                }
                        }
                    }
                    break;
                case 4:
                    if (player.ticksExisted % 11 == 0)
                        this.stage = 3;
                    break;
            }
        }
    }

    private String getList() {
        return "\n" +
                getColor("Green") + " - " + getColor("White") + "sw" + getColor("Gray") + " (Skywars)        \n" +
                getColor("Green") + " - " + getColor("White") + "tsw" + getColor("Gray") + " (Team Skywars)  \n" +
                getColor("Green") + " - " + getColor("White") + "tnt" + getColor("Gray") + " (Tnt Tag)       \n" +
                getColor("Green") + " - " + getColor("White") + "bw" + getColor("Gray") + " (Bedwars)        \n" +
                getColor("Green") + " - " + getColor("White") + "pgames" + getColor("Gray") + " (Party Games)\n" +
                getColor("Green") + " - " + getColor("White") + "arena" + getColor("Gray") + " (Arenapvp)    \n";
    }

    /**
     * Starts the joining process for a game.
     *
     * @param name  The item associated with the game.
     * @param lobbyNumber The lobby number.
     * @param enterDelay The delay number.
     */
    private void startJoining(Item name, int lobbyNumber) {
        joining = true;
        item = name;
        lobby = lobbyNumber;
        stage = 0;
        foundItem = false;
    }
}
