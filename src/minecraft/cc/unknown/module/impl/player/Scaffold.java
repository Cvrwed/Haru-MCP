package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Info(name = "Scaffold", category = Category.Player)
public class Scaffold extends Module{
	
	private ModeValue rotationMode = new ModeValue("Rotation Mode", "None", "None", "Static");
	private ModeValue spoofMode = new ModeValue("Spoof Mode", "Switch", "Switch", "Silent");
	private ModeValue sprintMode = new ModeValue("Sprint", "None", "None", "Vanilla");
	private SliderValue timer = new SliderValue("Timer", 1, 0.19, 3, 0.1);
	public RotationManager rotation, targetRotation, prevRotation;
	
	public Scaffold() {
		this.registerSetting(rotationMode, spoofMode, sprintMode, timer);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.timer.timerSpeed = timer.getInputToFloat();
		}		
	
	public void onDisable() {
		mc.timer.timerSpeed = 1F;
	}
	
	@EventLink
	public void onUpdate(MotionEvent e) {
		if (e.isPre()) {
			switch(sprintMode.getMode()) {
			case "None":
				mc.player.setSprinting(false);
				mc.gameSettings.keyBindSprint.pressed = false;
				break;
			case "Vanilla":
				mc.player.setSprinting(true);
				mc.gameSettings.keyBindSprint.pressed = true;
				break;
			}
			switch(rotationMode.getMode()) {
			case "Static":
				
				
			}
		
		}
	}	
}


