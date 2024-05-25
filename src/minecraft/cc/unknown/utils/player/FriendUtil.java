package cc.unknown.utils.player;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.utils.Loona;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public enum FriendUtil implements Loona {
	instance;

	public CopyOnWriteArrayList<String> friends = new CopyOnWriteArrayList<String>();

}
