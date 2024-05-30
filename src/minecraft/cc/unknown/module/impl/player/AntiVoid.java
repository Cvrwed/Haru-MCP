package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.move.MoveEvent;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.vec.Vec3;

@Info(name = "AntiVoid", category = Category.Player)
public class AntiVoid extends Module {

	private int overVoidTicks;
	private Vec3 position;
	private Vec3 motion;
	private boolean wasVoid;
	private boolean setBack;
	boolean shouldStuck;
	double x;
	double y;
	double z;
	boolean wait;

	private ModeValue mode = new ModeValue("Mode", "Grim", "Grim", "Polar");
	private SliderValue fall = new SliderValue("Min fall distance", 5, 0, 10, 1);

	public AntiVoid() {
		this.registerSetting(mode, fall);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + mode.getMode() + "]");
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		mc.player.isDead = false;
	}

	@EventLink
	public void onPacket(final PacketEvent e) {
		Packet<?> p = e.getPacket();

		if (e.isSend()) {
			if (mode.is("Grim")) {
				if (!mc.player.onGround && shouldStuck && p instanceof CPacketPlayer
						&& !(p instanceof CPacketPlayer.CPacketPlayerLook)
						&& !(p instanceof CPacketPlayer.CPacketPlayerPosLook)) {
					e.setCancelled(true);
				}
				if (p instanceof CPacketPlayerBlockPlacement && wait) {
					shouldStuck = false;
					mc.timer.timerSpeed = 0.2f;
					wait = false;
				}
			}
		}

		if (e.isReceive()) {
			if (mode.is("Grim")) {
				if (p instanceof SPacketPlayerPosLook) {
					final SPacketPlayerPosLook wrapper = (SPacketPlayerPosLook) p;
					x = wrapper.getX();
					y = wrapper.getY();
					z = wrapper.getZ();
					mc.timer.timerSpeed = 0.2f;
				}
			}
		}
	}

	@EventLink
	public void onMove(MoveEvent e) {
		if (!mc.player.onGround && mc.player.fallDistance > fall.getInput() && mode.is("Polar")) {
			mc.player.motionY = 0;
			e.setCancelled(true);
			shouldStuck = true;

		}
	}

	@EventLink
	public void onMotion(final MotionEvent e) {
		try {
			if (e.isPre()) {
				if (mode.is("Grim")) {

					if (mc.player.getHeldItem() == null) {
						mc.timer.timerSpeed = 1.0f;
					}

					if (mc.player.getHeldItem().getItem() instanceof ItemEnderPearl) {
						wait = true;
					}

					if (shouldStuck && !mc.player.onGround) {
						mc.player.motionX = 0.0;
						mc.player.motionY = 0.0;
						mc.player.motionZ = 0.0;
						mc.player.setPositionAndRotation(x, y, z, mc.player.rotationYaw, mc.player.rotationPitch);
					}
					final boolean overVoid = !mc.player.onGround && !PlayerUtil.isBlockUnder(30);
					if (!overVoid) {
						shouldStuck = false;
						x = mc.player.posX;
						y = mc.player.posY;
						z = mc.player.posZ;
						mc.timer.timerSpeed = 1.0f;
					}
					if (overVoid) {
						++overVoidTicks;
					} else if (mc.player.onGround) {
						overVoidTicks = 0;
					}
					if (overVoid && position != null && motion != null
							&& overVoidTicks < 30.0 + fall.getInput() * 20.0) {
						if (!setBack) {
							wasVoid = true;
							if (mc.player.fallDistance > fall.getInput() || setBack) {
								mc.player.fallDistance = 0.0f;
								setBack = true;
								shouldStuck = true;
								x = mc.player.posX;
								y = mc.player.posY;
								z = mc.player.posZ;
							}
						}
					} else {
						if (shouldStuck) {
							toggle();
						}
						shouldStuck = false;
						mc.timer.timerSpeed = 1.0f;
						setBack = false;
						if (wasVoid) {
							wasVoid = false;
						}
						motion = new Vec3(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
						position = new Vec3(mc.player.posX, mc.player.posY, mc.player.posZ);
					}
				}
			}

		} catch (NullPointerException ex) {

		}
	}

	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		if (e.isClient()) {
			this.disable();
		}
	}
}
