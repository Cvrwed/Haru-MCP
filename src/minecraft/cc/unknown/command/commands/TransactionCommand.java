package cc.unknown.command.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.api.Flips;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;

@Flips(name = "Transaction", alias = "s32", desc = "Show server transaction IDs", syntax = ".transaction")
public class TransactionCommand extends Command {

    private AtomicBoolean toggle = new AtomicBoolean(false);

    public TransactionCommand() {
        Haru.instance.getEventBus().register(this);
    }

    @Override
    public void onExecute(String[] args) {
        toggle.set(!toggle.get());
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (!toggle.get()) return;
        if (e.isReceive() && e.getPacket() instanceof SPacketConfirmTransaction) {
            this.sendChat(getColor("Red") + " [Transaction ID]: " + getColor("White") + ((SPacketConfirmTransaction) e.getPacket()).getActionNumber());
        }
    }
}