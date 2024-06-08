package cc.unknown.module.impl.move;

import java.util.LinkedHashSet;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.move.MoveEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.PreJumpEvent;
import cc.unknown.event.impl.world.AirCollideEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.network.TimedPacket;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.vec.AxisAlignedBB;

@Info(name = "Flight", category = Category.Move)
public class Flight extends Module {

	private ModeValue mode = new ModeValue("Mode", "Verus", "Verus", "Vanilla", "Polar");
	private final LinkedHashSet<TimedPacket> packetsCatch = new LinkedHashSet<>();
	private double y;
	private int ticks = 0;
	private boolean damage = false;
	private boolean release = false;

	public Flight() {
		this.registerSetting(mode);
	}

	@Override
	public void onEnable() {
		y = mc.player.posY;
		ticks = 0;
		damage = false;
		release = false;
		super.onEnable();
	}
	

	@Override
	public void onDisable() {
		if (PlayerUtil.inGame()) {
			for (TimedPacket data : packetsCatch) {
				PacketUtil.handlePacket((Packet<? extends INetHandlerPlayClient>) data.getPacket());
			}
		}
		super.onDisable();
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
		switch (mode.getMode()) {
		case "Vanilla":
		case "Verus":
			e.setCancelled(true);
			break;
		}
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		Packet packet = e.getPacket();
		
		if (e.isReceive()) {
			switch (mode.getMode()) {
			case "Polar":
				
				if (packet instanceof SPacketEntityStatus && ((SPacketEntityStatus) packet).getEntityId() == mc.player.getEntityId() && ticks <= 0) {
					damage = true;
					ticks = 40;
				}
				
				if (packet instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) packet).getEntityID() == mc.player.getEntityId() && damage) {
					damage = false;
					release = true;
				}
				
				if (packet instanceof CPacketConfirmTransaction) {
					if (ticks > 0) {
						packetsCatch.add(new TimedPacket(packet, System.currentTimeMillis()));
						e.setCancelled(true);
						ticks--;
					} else {
						if (release) {
							setToggled(false);
						}
					}
				}
				break;
			}
		}
	}
}
