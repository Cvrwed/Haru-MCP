package cc.unknown.module.impl.combat;

import java.util.function.Consumer;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.enums.EnumFacing;

@Info(name = "Velocity", category = Category.Combat)
public class Velocity extends Module {

	public ModeValue mode = new ModeValue("Mode", "Universocraft", "Packet", "Verus", "Ground Grim", "Minemen", "Watchdog",
			"Intave", "Universocraft");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private boolean reset;
	private int timerTicks = 0, intaveTick = 0, ticks = 0;

	public Velocity() {
		this.registerSetting(mode, horizontal, vertical, chance);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		timerTicks = 0;
		intaveTick = 0;
		ticks = 0;
		reset = false;
	}

	@EventLink
	public void onKnockBack(KnockBackEvent e) {
		if (chance.getInput() != 100.0D) {
			if (Math.random() >= chance.getInput() / 100.0D) {
				return;
			}
		}
		
	    String mode = this.mode.getMode();
	    switch (mode) {
	        case "Watchdog":
	            if (mc.player.hurtTime == 8) {
	                MoveUtil.strafe(MoveUtil.getSpeed() * 0.7f);
	            }
	            break;

	        case "Packet":
	        	e.setX(e.getX() * horizontal.getInput() / 100.0);
	        	e.setY(e.getY() * vertical.getInput() / 100.0);
	        	e.setZ(e.getZ() * horizontal.getInput() / 100.0);
	        	break;

	        case "Ground Grim":
	            if (PlayerUtil.isMoving() && mc.player.onGround) {
	                e.setCancelled(true);
	                reset = true;
	            }
	            break;

	        case "Intave":
	            reset = true;
	            break;

	        case "Minemen":
	            if (ticks > 14) {
	                adjustKnockBack(e, 1 / 42000.0, 1 / 8000.0);
	                e.setCancelled(true);
	                ticks = 0;
	            }
	            break;

	        case "Universocraft":
				float radians = (float) Math.toDegrees(/* player.rotationYaw */ 1.2224324);
				mc.player.motionX -= MathHelper.sin(radians) * 0.0000001;
				mc.player.motionY -= MathHelper.sin(radians) * 0.0000001;
				mc.player.motionZ -= MathHelper.sin(radians) * 0.0000001;
	            break;

	        default:
	            // Handle unexpected modes if necessary
	            break;
	    }
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (mode.is("Ground Grim")) {
			if (timerTicks > 0 && mc.timer.timerSpeed <= 1) {
				float timerSpeed = 0.8f + (0.2f * (20 - timerTicks) / 20);
				mc.timer.timerSpeed = Math.min(timerSpeed, 1f);
				timerTicks--;
			} else if (mc.timer.timerSpeed <= 1) {
				mc.timer.timerSpeed = 1f;
			}

			if (reset) {
				if (checkAir(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					reset = false;
				}
			}
		}
	}

	@EventLink
	public void onPre(MotionEvent e) {
	    if (!PlayerUtil.inGame() || !e.isPre()) {
	        return;
	    }

	    String mode = this.mode.getMode();
	    switch (mode) {
	        case "Verus":
	    	    if (mc.player.hurtTime == 10 - MathHelper.randomValue(3, 4)) {
	    	        mc.player.motionX = 0.0D;
	    	        mc.player.motionY = 0.0D;
	    	        mc.player.motionZ = 0.0D;
	    	    }
	            break;
	        case "Intave":
	    	    intaveTick++;
	    	    if (reset && mc.player.hurtTime == 2) {
	    	        if (mc.player.onGround && intaveTick % 2 == 0) {
	    	            mc.player.jump();
	    	            intaveTick = 0;
	    	        }
	    	        reset = false;
	    	    }
	            break;
	        default:
	        	// Handle unexpected modes if necessary
	            break;
	    }
	}

	private boolean checkAir(BlockPos blockPos) {
		if (mc.world == null)
			return false;

		if (!mc.world.isAirBlock(blockPos))
			return false;

		timerTicks = 20;

		if (mc.player != null) {
			mc.getNetHandler().sendSilent(new CPacketPlayer(true));
			mc.getNetHandler().sendSilent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
		}

		mc.world.setBlockToAir(blockPos);

		return true;
	}
	
    private void adjustPlayerMovement(Consumer<EntityPlayerSP> adjuster) {
        adjuster.accept(mc.player);
    }
    
    private void adjustKnockBack(KnockBackEvent e, double horizontalFactor, double verticalFactor) {
        e.setX(e.getX() * horizontalFactor / 100.0);
        e.setY(e.getY() * verticalFactor / 100.0);
        e.setZ(e.getZ() * horizontalFactor / 100.0);
    }

	private boolean isValidMotion(double motion, double min, double max) {
	    return Math.abs(motion) > min && Math.abs(motion) < max;
	}
}
