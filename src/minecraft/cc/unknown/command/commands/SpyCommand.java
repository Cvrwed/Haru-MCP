package cc.unknown.command.commands;

import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import net.minecraft.entity.player.EntityPlayer;

@Flips(name = "Spy", alias = "spy", desc = "Spying...", syntax = ".spy <user>")
public class SpyCommand extends Command {

	@Override // remote view cmd of lb
	public void onExecute(String[] args) {
		if (args.length < 2) {
            if (mc.getRenderViewEntity() != mc.player) {
                mc.setRenderViewEntity(mc.player);
                return;
            }
            sendChat(getColor("Red") + " Syntax Error. Use: " + syntax);
            return;
		}
		
		String target = args[1];
		
		for (EntityPlayer entity : mc.world.playerEntities) {
		    if (target.equals(entity.getCommandSenderName())) {
		        mc.setRenderViewEntity(entity);
		        sendChat("Spying to §8${entity.name}§3.");
		        sendChat("Execute §8.spy §3again to go back to yours.");
                break;
            }
        }
	}
}
