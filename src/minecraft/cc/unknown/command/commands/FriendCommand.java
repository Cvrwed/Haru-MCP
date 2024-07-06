package cc.unknown.command.commands;

import cc.unknown.command.Command;
import cc.unknown.command.api.Flips;
import cc.unknown.utils.player.FriendUtil;
import net.minecraft.entity.player.EntityPlayer;

@Flips(name = "Friend", alias = "fr", desc = "It allows you to save a friend", syntax = ".friend add <name>")
public class FriendCommand extends Command {

	@Override
	public void onExecute(String[] args) {
	    if (args.length == 0 || (args.length == 2 && args[1].isEmpty())) {
	        this.sendChat(getColor("Red") + syntax);
	        return;
	    }

	    if (args[0].equalsIgnoreCase("add")) {
	        if (args.length < 2) {
	            this.sendChat(getColor("Red") + "Nick invalid or incorrect.");
	            return;
	        }
	        addFriend(args[1]);
	    } else if (args[0].equalsIgnoreCase("remove")) {
	        if (args.length < 2) {
	            this.sendChat(getColor("Red") + "Nick invalid or incorrect.");
	            return;
	        }
	        FriendUtil.friends.remove(args[1]);
	        this.sendChat(getColor("Red") + "Removed friend " + args[1]);
	    } else if (args[0].equalsIgnoreCase("list")) {
	        this.sendChat(getFriendList());
	    } else {
	        this.sendChat(getColor("Red") + syntax);
	    }
	}

	private void addFriend(String name) {
	    EntityPlayer player = findEntity(name);
	    if (player == null) {
	        this.sendChat(getColor("Red") + name + " not found.");
	        return;
	    }

	    String playerName = player.getName();
	    if (FriendUtil.friends.contains(playerName)) {
	        this.sendChat(getColor("Gray") + playerName + " is already your friend");
	    } else {
	        FriendUtil.friends.add(playerName);
	        this.sendChat(getColor("Green") + "Added friend " + playerName);
	    }
	}

	private String getFriendList() {
	    if (FriendUtil.friends.isEmpty()) {
	        return getColor("Gray") + "Your friend list is empty.";
	    }

	    StringBuilder message = new StringBuilder(getColor("Green") + "Friend list:\n");
	    for (String friend : FriendUtil.friends) {
	        message.append("- ").append(friend).append("\n");
	    }
	    return message.toString();
	}

	private EntityPlayer findEntity(String name) {
	    return mc.world.playerEntities.stream().filter(entity -> entity.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

}
