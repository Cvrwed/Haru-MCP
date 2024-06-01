package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Info(name = "Scaffold", category = Category.Player)
public class Scaffold extends Module{
	public final ModeValue rotationMode = new ModeValue("Rotation Mode", "None", "None", "Static");
	public final ModeValue spoofMode = new ModeValue("Spoof Mode", "Switch", "Silent");
	public final ModeValue sprintMode = new ModeValue("Sprint", "None", "None", "Vanilla");
	public final SliderValue timer = new SliderValue("Timer", 1, 0.1, 1, 3);
	public RotationManager rotation, targetRotation, prevRotation;
	
	
	
	@Override
	public void onEnable() {
		super.onEnable();
		this.rotation = this.targetRotation = this.prevRotation =
		
		
		}		
	}
