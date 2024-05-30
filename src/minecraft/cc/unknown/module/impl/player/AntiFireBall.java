package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;

@Info(name = "AntiFireBall", category = Category.Player)
public class AntiFireBall extends Module {

	private SliderValue range = new SliderValue("Range", 5.5, 1.0, 6.0, 0.05);
	private BooleanValue rotation = new BooleanValue("Rotations", true);

	public AntiFireBall() {
		this.registerSetting(range, rotation);
	}

	@EventLink
	public void onUpdate(MotionEvent e) {
		if (e.isPre()) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityFireball && mc.player.getDistanceToEntity(entity) < range.getInput()) {
					if (rotation.isToggled()) {
						RotationManager.setClientRotation(RotationManager.getRotations(entity));
					}
					KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
				}
			}
		}
	}
}
