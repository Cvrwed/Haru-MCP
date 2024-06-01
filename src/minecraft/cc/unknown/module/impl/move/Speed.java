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

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Strafe", "Hypixel");
	
	public Speed() {
		this.registerSetting(mode);
	}
	
	@EventLink
	public void onMotion(MotionEvent e) {
		if (e.isPre()) {
			switch (mode.getMode()) {
			case "Strafe":
				if(PlayerUtil.isMoving()) {
					MoveUtil.strafeY(0);
					if (e.isOnGround()) {
						mc.player.jump();
					}
				} else {
					mc.player.motionX = 0.0;
					mc.player.motionY = 0.0;
				}
				break;
			case "Verus":
				if (PlayerUtil.isMoving()) {
					MoveUtil.strafe(0.32F);
				}
				
				if (e.isOnGround()) {
					mc.player.jump();
				}

				break;
			case "Hypixel":
				if(PlayerUtil.isMoving()) { }
				
				if(e.isOnGround()) {
					mc.player.jump();
					MoveUtil.strafe(MoveUtil.getSpeed());	
				}
				break;
				}
				
			}
		}
	}

