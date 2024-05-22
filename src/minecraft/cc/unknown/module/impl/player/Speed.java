package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;

@Register(name = "Speed", category = Category.Player)
public class Speed extends Module {

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus");
	
	public Speed() {
		this.registerSetting(mode);
	}
	
	@EventLink
	public void onMotion(MotionEvent e) {
		if (e.isPre()) {
			switch (mode.getMode()) {
			case "Verus":
				mc.player.movementInput.jump = true;
				if (PlayerUtil.isMoving())
					MoveUtil.strafeY(0.32F);
				break;
			}
		}
	}
}
