package cc.unknown.module.impl.other;

import java.util.ArrayList;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Info(name = "BedWarsHelper", category = Category.Other)
public class BedWarsHelper extends Module {
	
	private BooleanValue 
	diamondSword = new BooleanValue("Diamond Sword", true),
	fireBall = new BooleanValue("FireBall", true),
	enderPearl = new BooleanValue("Ender Pearl", true),
	tnt = new BooleanValue("TNT", true),
	obsidian = new BooleanValue("Obsidian", true),
	diamondArmor = new BooleanValue("Diamond Armor", true);
	
	private ArrayList<String> 
	diamondSwordList = new ArrayList<>(), 
	fireBallList = new ArrayList<>(), 
	enderpearlList = new ArrayList<>(), 
	tntList = new ArrayList<>(), 
	obsidianList = new ArrayList<>(), 
	diamondArmorList = new ArrayList<>();

	public BedWarsHelper() {
		this.registerSetting(diamondSword, fireBall, enderPearl, tnt, obsidian,
				diamondArmor);
	}

	@EventLink
	public void onRender2D(RenderEvent e) {
		if (e.is2D() && PlayerUtil.inGame()) {
			if (mc.player.ticksExisted < 5) {
				diamondSwordList.clear();
				fireBallList.clear();
				enderpearlList.clear();
				tntList.clear();
				obsidianList.clear();
				diamondArmorList.clear();
			}
			
		    for (EntityPlayer entity : mc.world.playerEntities) {
		        if (entity.getHeldItem() != null) {   
		            if (entity.getHeldItem().getItem() == Items.diamond_sword && diamondSword.isToggled() && !diamondSwordList.contains(entity.getName())) {
		                PlayerUtil.send(entity.getDisplayName().getFormattedText() + " has Diamond Sword");
		                diamondSwordList.add(entity.getName());
		                mc.player.playSound("note.pling", 1.0f, 1.0f);
		            }
		            
		            if (entity.getHeldItem().getItem() == Items.fire_charge && fireBall.isToggled() && !fireBallList.contains(entity.getName())) {
		                PlayerUtil.send(entity.getDisplayName().getFormattedText() + " has FireBall");
		                fireBallList.add(entity.getName());
		                mc.player.playSound("note.pling", 1.0f, 1.0f);
		            }
		            
		            if (entity.getHeldItem().getItem() == Items.ender_pearl && enderPearl.isToggled() && !enderpearlList.contains(entity.getName())) {
		                PlayerUtil.send(entity.getDisplayName().getFormattedText() + " has Ender Pearl");
		                enderpearlList.add(entity.getName());
		                mc.player.playSound("note.pling", 1.0f, 1.0f);
		            }
		            
		            if (entity.getHeldItem().getItem() == ItemBlock.getItemById(46) && tnt.isToggled() && !tntList.contains(entity.getName())) {
		                PlayerUtil.send(entity.getDisplayName().getFormattedText() + " has TNT Block");
		                tntList.add(entity.getName());
		                mc.player.playSound("note.pling", 1.0f, 1.0f);
		            }
		            
		            if (entity.getHeldItem().getItem() == ItemBlock.getItemById(49) && obsidian.isToggled() && !obsidianList.contains(entity.getName())) {
		                PlayerUtil.send(entity.getDisplayName().getFormattedText() + " has Obsidian Block");
		                obsidianList.add(entity.getName());
		                mc.player.playSound("note.pling", 1.0f, 1.0f);
		            }
		            
		            if (isWearingDiamondArmor(entity) && diamondArmor.isToggled() && !diamondArmorList.contains(entity.getName())) {
		                PlayerUtil.send(entity.getDisplayName().getFormattedText() + " has Diamond Armor");
		                diamondArmorList.add(entity.getName());
		                mc.player.playSound("note.pling", 1.0f, 1.0f);
		            }
		        }
		        
		        if (entity.isDead) {
		            diamondSwordList.remove(entity.getName());
		            fireBallList.remove(entity.getName());
		            enderpearlList.remove(entity.getName());
		            tntList.remove(entity.getName());
		            obsidianList.remove(entity.getName());
		            diamondArmorList.remove(entity.getName());
		        }
		    }
		}
	}
	
	private boolean isWearingDiamondArmor(EntityPlayer player) {
	    ItemStack[] armorInventory = player.inventory.armorInventory;

	    for (ItemStack itemStack : armorInventory) {
	        if (itemStack.getItem() == Items.diamond_leggings || itemStack.getItem() == Items.diamond_chestplate) {
	            return true;
	        }
	    }

	    return false;
	}
}
