package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.network.DisconnectionEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.event.impl.world.ChangeWorldEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.network.PacketUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.vec.Vec3;

@Register(name = "Blink", category = Category.Player)
public class Blink extends Module {

	private final List<Packet<?>> packets = new ArrayList<>();
	private final List<Packet<?>> packetsReceived = new ArrayList<>();
	private final List<Packet<?>> queuedPackets = new ArrayList<>();
	private final List<Vec3> positions = new ArrayList<>();

	private ModeValue renderPosition = new ModeValue("Render Position", "None", "None", "Fake Player", "Line");

	private BooleanValue pulse = new BooleanValue("Pulse", false);
	private SliderValue pulseDelay = new SliderValue("Pulse Delay", 500, 100, 3500, 50);

	private BooleanValue disableDisconnect = new BooleanValue("Release on disconnect", true);
	private BooleanValue disableAttack = new BooleanValue("Release when attacking", true);
	private BooleanValue disableDmg = new BooleanValue("Release when receive dmg", true);
	private Cold timer = new Cold();
	
	 private EntityOtherPlayerMP fakePlayer;

	public Blink() {
		this.registerSetting(renderPosition, pulse, pulseDelay, disableDisconnect, disableAttack, disableDmg);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (mc.player == null) {
			toggle();
			return;
		}
		packets.clear();
	}

	@Override
	public void onDisable() {
		super.onDisable();

		if (mc.player == null)
			return;
		releasePackets();
	}

	@EventLink
	public void onAttack(AttackEvent e) {
		if (mc.objectMouseOver.entityHit != null) {
			if (disableAttack.isToggled()) {
				if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;

					if (player.hurtTime <= 1) {
						releasePackets();
					}
				}
			}
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		final Packet<?> p = e.getPacket();
		if (mc.player == null || mc.player.isDead)
			return;

		if (p.getClass().getSimpleName().startsWith("S") || p.getClass().getSimpleName().startsWith("C00")
				|| p.getClass().getSimpleName().startsWith("C01"))
			return;

		if (e.isReceive()) {

			synchronized (packetsReceived) {
				queuedPackets.addAll(packetsReceived);
			}
			packetsReceived.clear();
		}

		if (e.isSend()) {

			e.setCancelled(true);
			synchronized (packets) {
				packets.add(p);
			}

			if (p instanceof CPacketPlayer && ((CPacketPlayer) p).isMoving()) {
				CPacketPlayer wrapper = (CPacketPlayer) p;
				Vec3 packetPos = new Vec3(wrapper.x, wrapper.y, wrapper.z);
				synchronized (positions) {
					positions.add(packetPos);

				}
			}
		}
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (pulse.isToggled()) {
			if (timer.hasTimeElapsed(pulseDelay.getInputToLong(), true)) {
				releasePackets();
			}
			
			if (renderPosition.is("Fake")) {
				
			}
		}

		if (disableDmg.isToggled()) {
			if (mc.player.hurtTime > 0) {
				releasePackets();
			}
		}

		if (e.isPost()) {
			if (mc.player == null || mc.player.isDead || mc.player.ticksExisted <= 10) {
				releasePackets();
			}
			synchronized (packetsReceived) {
				queuedPackets.addAll(packetsReceived);
			}
			packetsReceived.clear();
		}
	}

	@EventLink
	public void onRender3D(RenderEvent e) {
		if (e.is3D()) {
			if (renderPosition.is("Line")) {
				synchronized (positions) {
					GL11.glPushMatrix();
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					mc.entityRenderer.disableLightmap();
					GL11.glBegin(GL11.GL_LINE_STRIP);
					GL11.glColor4f(getTheme().getMainColor().getRed() / 255.0f,
							getTheme().getMainColor().getGreen() / 255.0f,
							getTheme().getMainColor().getBlue() / 255.0f,
							getTheme().getMainColor().getAlpha() / 255.0f);

					double renderPosX = mc.getRenderManager().viewerPosX;
					double renderPosY = mc.getRenderManager().viewerPosY;
					double renderPosZ = mc.getRenderManager().viewerPosZ;

					for (Vec3 pos : positions) {
						GL11.glVertex3d(pos.xCoord - renderPosX, pos.yCoord - renderPosY, pos.zCoord - renderPosZ);
					}

					GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
					GL11.glEnd();
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_LINE_SMOOTH);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glPopMatrix();
				}
			}
		}
	}

	private void releasePackets() {
		synchronized (packetsReceived) {
			queuedPackets.addAll(packetsReceived);
		}
		synchronized (packets) {
			PacketUtil.send(packets.toArray(new Packet<?>[0]));
		}

		reset();
	}

	private void reset() {
		packets.clear();
		packetsReceived.clear();
		positions.clear();
	}

	@EventLink
	public void onWorldLoad(ChangeWorldEvent e) {
		if (e.getChangeWorld() == null) {
			reset();
		}
	}

	@EventLink
	public void onDisconnect(final DisconnectionEvent e) {
		if (e.isClient()) {
			this.packets.clear();
			if (disableDisconnect.isToggled()) {
				this.disable();
			}
		}
	}
}
