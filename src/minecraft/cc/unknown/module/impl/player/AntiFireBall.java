package cc.unknown.module.impl.player;

import org.apache.commons.lang3.RandomUtils;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MathHelper;

@Register(name = "AntiFireBall", category = Category.Player)
public class AntiFireBall extends Module {

	private DoubleSliderValue speed = new DoubleSliderValue("Rotation Speed", 98, 98, 1, 180, 1);
	private SliderValue range = new SliderValue("Range", 5.5, 1.0, 6.0, 0.05);
	private BooleanValue rotation = new BooleanValue("Rotations", true);
	private long leftDelay = 50L;
	private long leftLastSwing = 0L;

	public AntiFireBall() {
		this.registerSetting(speed, range, rotation);
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
		if (e.isPre()) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityFireball && mc.player.getDistanceToEntity(entity) < range.getInput()) {
					if (rotation.isToggled()) {
						RotationManager.setTargetRotation(RotationManager.limitAngleChange(RotationManager.serverRotation, RotationManager.getRotationsNonLivingEntity(entity), RandomUtils.nextFloat(speed.getInputMinToFloat(), speed.getInputMaxToFloat())), 0);
					}

					if (mc.objectMouseOver.entityHit == entity) {
						if (System.currentTimeMillis() - leftLastSwing >= leftDelay) {
							leftLastSwing = System.currentTimeMillis();
							leftDelay = MathHelper.simpleRandom(12, 12);
							KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
						}
					}
				}
			}
		}
	}
}
