package cc.unknown.module.impl.move;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.move.MoveEvent;
import cc.unknown.event.impl.player.PreJumpEvent;
import cc.unknown.event.impl.world.AirCollideEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.MoveUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.vec.AxisAlignedBB;

@Info(name = "Flight", category = Category.Move)
public class Flight extends Module {

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Vanilla");
	private double y;

	public Flight() {
		this.registerSetting(mode);
	}

	@Override
	public void onEnable() {
		y = mc.player.posY;
		super.onEnable();
	}

	@EventLink
	public void onMove(MoveEvent e) {
		switch (mode.getMode()) {
		case "Vanilla":
			e.setSpeed(4);
			e.setY(0.0D);
			if (mc.gameSettings.keyBindJump.isKeyDown())
				e.setY(0.5D);
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				e.setY(-0.5D);
			break;
		case "Verus":
			if (mc.player.onGround) {
				e.jump();
			}
			break;
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (e.isPre()) {
			switch (mode.getMode()) {
			case "Vanilla":
				e.setOnGround(true);
				break;
			case "Verus":
				double moveForward = mc.player.movementInput.moveForward;
				if (moveForward > 0.0f)
					mc.player.setSprinting(true);
				break;
			}
		}
	}

	@EventLink
	public void onAirCollide(AirCollideEvent e) {
		switch (mode.getMode()) {
		case "Verus":
			if (e.getState().getBlock() instanceof BlockAir && e.getPos().getY() <= y) {
				e.setBoundingBox(AxisAlignedBB.fromBounds(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), (e.getPos().getX() + 1), y, (e.getPos().getZ() + 1)));
			}
			break;
		}
	}

	@EventLink
	public void onPreJump(PreJumpEvent e) {
		e.setCancelled(true);
	}

}
