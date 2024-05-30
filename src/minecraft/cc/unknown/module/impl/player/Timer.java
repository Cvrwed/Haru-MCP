package cc.unknown.module.impl.player;

import java.util.LinkedList;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.impl.combat.AimAssist;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

@Info(name = "Timer", category = Category.Player)
public class Timer extends Module {
	
	private ModeValue mode = new ModeValue("Mode", "Constant", "Constant", "Random", "Ground");
	private SliderValue spid = new SliderValue("Speed", 1.5, 0.05, 25, 0.05);
	private SliderValue variation = new SliderValue("Randomness", 15, 0.05, 50, 0.05);
	
	private BooleanValue weaponOnly = new BooleanValue("Only Use Weapons", false);
	private BooleanValue onlyOnGround = new BooleanValue("Only on ground", false);
	private BooleanValue speedOnly = new BooleanValue("Only Speed Potion", false);
	private BooleanValue onlyForward = new BooleanValue("Only Forward", false);
	private BooleanValue noCombo = new BooleanValue("No Combo", false);

	private BooleanValue blink = new BooleanValue("Blink", false);
	
	private LinkedList<Packet> packets = new LinkedList<Packet>();

	public Timer() {
		this.registerSetting(mode, spid, variation, weaponOnly, onlyOnGround, speedOnly, onlyForward, noCombo,
				blink);
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
	}
	
	@EventLink
	public void onTick(TickEvent e) {
	    if (mc.player == null) {
	        return;
	    }

	    float timerSpeed = 1.0f;

	    switch (mode.getMode()) {
	        case "Constant":
	            timerSpeed = calculateConstantTimer();
	            break;
	        case "Random":
	            timerSpeed = calculateRandomTimer();
	            break;
	    }

	    mc.timer.timerSpeed = timerSpeed;
	}
	
	@EventLink
	public void onPacket(PacketEvent e) {
	    Packet<?> p = e.getPacket();

	    if (e.isSend() && p instanceof CPacketPlayer) {
	        if (blink.isToggled()) {
	            mc.timer.timerSpeed = 1.0f;
	            e.setCancelled(true);
	            packets.add(p);
	        }
	    }
	}
	
	private float calculateConstantTimer() {
		float speed = spid.getInputToFloat();
		return applyConditionals(speed);	
	}

	private double getDistancePrediction() { 
	    double predictX = mc.player.posX + (mc.player.posX - mc.player.lastTickPosX) * 2;
	    double predictZ = mc.player.posZ + (mc.player.posZ - mc.player.lastTickPosZ) * 2;
	    float deltaX = (float) (predictX - mc.player.posX);
	    float deltaY = (float) (mc.player.posY - mc.player.posY);
	    float deltaZ = (float) (predictZ - mc.player.posZ);
	    return MathHelper.sqrt_float(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}
	
	private float calculateRandomTimer() {
	    float speed = spid.getInputToFloat();
	    int variationHalf = variation.getInputToInt() / 2;
	    float randomFactor = ThreadLocalRandom.current().nextFloat() * (variationHalf * 2) - variationHalf;
	    float adjustedSpeed = Math.max(speed + randomFactor, 1.0f);
	    return applyConditionals(adjustedSpeed);
	}
	
	private float applyConditionals(float timer) {

	    if (weaponOnly.isToggled() && !PlayerUtil.isHoldingWeapon()) {
	        return 1.0f;
	    }

	    if (onlyOnGround.isToggled() && !mc.player.onGround) {
	        return 1.0f;
	    }

	    if (speedOnly.isToggled() && !mc.player.isPotionActive(Potion.moveSpeed)) {
	        return 1.0f;
	    }
	    
	    double predictedDistance = getDistancePrediction();
	    if (onlyForward.isToggled() && !mc.gameSettings.keyBindForward.pressed || getDistancePrediction() > mc.player.getDistanceToEntity(mc.player) + 0.08) {
	    	return 1.0f;	
	    }
	    
	    if (noCombo.isToggled() && mc.player.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ) < mc.player.getDistance(0, 0, 0)) {
	    	return 1.0f;	
	    }
	    
	    return timer;
	}
}
