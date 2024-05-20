package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.KnockBackEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.Loona;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.enums.EnumFacing;
import net.minecraft.world.World;

@Register(name = "Velocity", category = Category.Combat)
public class Velocity extends Module {

	public ModeValue mode = new ModeValue("Mode", "Packet", "Packet", "Verus", "Ground Grim", "Polar", "Minemen",
			"Intave");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyCombat = new BooleanValue("Only During Combat", false);
	private BooleanValue onlyGround = new BooleanValue("Only While on Ground", false);
	private boolean reset;
	private int timerTicks = 0, intaveTick = 0, ticks = 0;

	public Velocity() {
		this.registerSetting(mode, horizontal, vertical, chance, onlyCombat, onlyGround);
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

		switch (mode.getMode()) {
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
		case "Polar":
			if (mc.player.onGround && e.getY() > 0) {
				mc.player.jump();
			}
			break;
		case "Intave":
			reset = true;
			break;
		case "Minemen":
			if (ticks > 14) {
				e.setX(e.getX() / 42000.0);
				e.setY(e.getY() / 8000.0);
				e.setZ(e.getZ() / 42000.0);
				e.setCancelled(true);
				ticks = 0;
			}
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
		if (PlayerUtil.inGame() && e.isPre()) {
			switch (mode.getMode()) {
			case "Verus":
				if (mc.player.hurtTime == 10 - MathHelper.randomInt(3, 4)) {
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
			}
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
			mc.getNetHandler().sendSilent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
					blockPos, EnumFacing.DOWN));
		}

		mc.world.setBlockToAir(blockPos);

		return true;
	}
}
