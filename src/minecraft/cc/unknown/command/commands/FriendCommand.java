package cc.unknown.command.commands;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.utils.player.FriendUtil;
import net.minecraft.entity.Entity;

@Flips(name = "Friend", alias = "fr", desc = "It allows you to save a friend", syntax = ".friend add <name>")
public class FriendCommand extends Command {

	@Override
	public void onExecute(String[] args) {
		if (args.length != 2 || args[1] == "") {
			this.sendChat(getColor("Red") + syntax);
			return;
		}
		
		if (args[0].equalsIgnoreCase("add")) {
			if (FriendUtil.instance.friends.contains(args[1])) {
				this.sendChat(getColor("Gray") + args[1] + " is already your friend");
			} else {
				FriendUtil.instance.friends.add(args[1]);
				this.sendChat(getColor("Green") + "Added friend " + args[1]);
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			FriendUtil.instance.friends.remove(args[1]);
			this.sendChat(getColor("Red") + "Removed friend " + args[1]);
		} else {
			this.sendChat(getColor("Red") + syntax);
			return;
		}
	}

}
