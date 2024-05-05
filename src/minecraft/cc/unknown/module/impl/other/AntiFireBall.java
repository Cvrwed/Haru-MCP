package cc.unknown.module.impl.other;

import org.apache.commons.lang3.RandomUtils;

import cc.unknown.event.Event;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.RotationUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;

@Register(name = "AntiFireBall", category = Category.Other)
public class AntiFireBall extends Module {

	private DoubleSliderValue speed = new DoubleSliderValue("Rotation Speed", 98, 98, 1, 180, 1);
	private SliderValue range = new SliderValue("Range", 6.0, 1.0, 6.0, 0.01);
	private final BooleanValue moveFix = new BooleanValue("Move Fix", false);

	public AntiFireBall() {
		this.registerSetting(speed, range, moveFix);
	}

	@EventLink
	public void onFireball(Event event) {
	    if (!(event instanceof StrafeEvent || event instanceof JumpEvent)) {
	        return;
	    }

	    for (Entity entity : mc.world.loadedEntityList) {
	        if (entity instanceof EntityFireball) {
	            EntityFireball fireball = (EntityFireball) entity;
	            if (fireball != null && moveFix.isToggled()) {
	                if (event instanceof StrafeEvent) {
	                    ((StrafeEvent) event).setYaw(mc.player.rotationYaw);
	                } else if (event instanceof JumpEvent) {
	                	((JumpEvent) event).setYaw(mc.player.rotationYaw);
	                }
	            }
	        }
	    }
	}
	       
	@EventLink
	public void onUpdate(LivingEvent e) {
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityFireball) {
				EntityFireball fire = (EntityFireball) entity;
				if (mc.player.getDistanceToEntity(fire) < range.getInput()) {
					RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.getServerRotation(),
							RotationUtils.getRotations(fire),
							RandomUtils.nextFloat(speed.getInputMinToFloat(), speed.getInputMaxToFloat())));

					KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
				}
			}
		}
	}
}
