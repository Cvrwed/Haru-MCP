package cc.unknown.module.impl.move;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;

@Info(name = "Speed", category = Category.Move)
public class Speed extends Module {

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Strafe", "Watchdog", "NCP");

	public Speed() {
		this.registerSetting(mode);
	}

	@EventLink
	public void onMotion(MotionEvent e) {
	    if (!e.isPre()) {
	        return;
	    }

	    if (!PlayerUtil.isMoving()) {
	        return;
	    }

	    String mode = this.mode.getMode();
	    switch (mode) {
	        case "Strafe":
	            MoveUtil.strafeY(0);
	            break;
	        case "Verus":
	            MoveUtil.strafe(0.32F);
	            break;
	        case "Watchdog":
	            MoveUtil.strafe(MoveUtil.getSpeed());
	            break;
	        case "NCP":
	            MoveUtil.strafe(0.25F);
	            break;
	        default:
	            // Handle unexpected modes if necessary
	            return;
	    }

	    if (e.isOnGround()) {
	        mc.player.jump();
	    }
	}
}
