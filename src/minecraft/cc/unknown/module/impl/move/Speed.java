package cc.unknown.module.impl.move;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.move.MoveEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;

@Info(name = "Speed", category = Category.Move)
public class Speed extends Module {

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Strafe", "NCP");

	public Speed() {
		this.registerSetting(mode);
	}

	@EventLink
	public void onMotion(MotionEvent e) {
	    if (!e.isPre()) {
	        return;
	    }
	    
	    if (e.isOnGround()) {
	        mc.player.jump();
	    }

	    if (!PlayerUtil.isMoving()) return;

	    String mode = this.mode.getMode();
	    switch (mode) {
	        case "Verus":
	            MoveUtil.strafe(0.32F);
	            break;
	        case "NCP":
	            MoveUtil.strafe(0.25F);
	            break;
	    }
	}
	
	@EventLink
    public void onStrafe(StrafeEvent e) {
	    switch (mode.getMode()) {
        case "Strafe":
        	if(!mc.player.isSprinting()) {
        		e.setFriction(e.getFriction() * 1.3F);
        	}

            if(mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.jump();
            }
            break;
            
	    }
    }
	
	@EventLink
    public void onJump(JumpEvent e) {
        switch (mode.getMode()) {
            case "Strafe":
            	e.setYaw(MoveUtil.getPlayerDirection());
                break;
        }
    }
	
	@EventLink
	public void onMove(MoveEvent e) {
        switch (mode.getMode()) {
        case "Strafe":
            if(mc.player.hurtTime <= 10) {
                MoveUtil.strafe(e);
            }
            break;
            
        }
	}
}
