package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.enums.EnumFacing;
import net.minecraft.world.World;

@Register(name = "Velocity", category = Category.Combat)
public class Velocity extends Module {

	private ModeValue mode = new ModeValue("Mode", "S12Packet", "S12Packet", "Verus", "Ground Grim", "Polar");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyCombat = new BooleanValue("Only During Combat", false);
	private BooleanValue onlyGround = new BooleanValue("Only While on Ground", false);
	private boolean reset;
	private int timerTicks = 0;

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
		reset = false;
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (chance.getInputToInt() / 100 > Math.random()) return;
		
		Packet<?> p = e.getPacket();

		if (e.isReceive()) {
			if (mode.is("S12Packet")) {
				if (p instanceof S12PacketEntityVelocity) {
					final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;
					if (wrapper.getEntityID() == mc.player.getEntityId()) {

						if (horizontal.getInput() == 0) {
							e.setCancelled(true);

							if (vertical.getInput() != 0) {
								mc.player.motionY = wrapper.getMotionY() / 8000.0D;
							}
							return;
						}

						wrapper.motionX *= horizontal.getInput() / 100;
						wrapper.motionY *= vertical.getInput() / 100;
						wrapper.motionZ *= horizontal.getInput() / 100;

						e.setPacket(wrapper);
					}
				} else if (p instanceof S27PacketExplosion) {
					final S27PacketExplosion wrapper = (S27PacketExplosion) p;

					if (horizontal.getInput() != 0f && vertical.getInput() != 0f) {
						wrapper.field_149152_f = 0f;
						wrapper.field_149153_g = 0f;
						wrapper.field_149159_h = 0f;
						return;
					}

					wrapper.field_149152_f *= horizontal.getInput();
					wrapper.field_149153_g *= vertical.getInput();
					wrapper.field_149159_h *= horizontal.getInput();
				}
			}

			if (mode.is("Ground Grim") && PlayerUtil.isMoving() && mc.player.onGround) {
			    if (p instanceof S12PacketEntityVelocity) {
			        final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;
			        if (wrapper.getEntityID() == mc.player.getEntityId()) {
			            e.setCancelled(true);
			            reset = true;
			        }
			    } else if (p instanceof S27PacketExplosion) {
			        e.setCancelled(true);
			        reset = true;
			    }
			}
			
			if (mode.is("Polar")) {
				if (p instanceof S12PacketEntityVelocity) {
			        S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;
			        if (PlayerUtil.isMoving() && wrapper.getEntityID() == mc.player.getEntityId() && wrapper.motionY > 0 && (mc.player.hurtTime <= 14 || mc.player.hurtTime <= 1))
			            reset = true;
				}
			}
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
				BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
				if (checkAir(pos)) {
					reset = false;
				}
			}
		}
	}

	@EventLink
	public void onPre(MotionEvent e) {
		if (PlayerUtil.inGame() && e.isPre()) {
			if (mode.is("Verus") && mc.player.hurtTime == 10 - MathHelper.randomInt(3, 4)) {
				mc.player.motionX = 0.0D;
				mc.player.motionY = 0.0D;
				mc.player.motionZ = 0.0D;
			}
		}
	}
	
	private boolean checkAir(BlockPos blockPos) {
	    World world = mc.world;
	    if (world == null)
	        return false;

	    if (!world.isAirBlock(blockPos))
	        return false;

	    timerTicks = 20;

	    EntityPlayerSP player = mc.player;
	    if (player != null) {
	    	mc.getNetHandler().sendSilent(new C03PacketPlayer(true));
	    	mc.getNetHandler().sendSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
	    }

	    world.setBlockToAir(blockPos);

	    return true;
	}
}
