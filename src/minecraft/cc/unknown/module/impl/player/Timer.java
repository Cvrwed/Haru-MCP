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
	
	private ModeValue mode = new ModeValue("Mode", "Constant", "Constant", "Random");
	private SliderValue spid = new SliderValue("Speed", 1.5, 0.01, 25, 0.01);
	private SliderValue variation = new SliderValue("Randomness", 15, 0.05, 50, 0.05);
	
	private LinkedList<Packet> packets = new LinkedList<Packet>();

	public Timer() {
		this.registerSetting(mode, spid, variation);
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
	
	private float calculateConstantTimer() {
		float speed = spid.getInputToFloat();
		return speed;	
	}

	private float calculateRandomTimer() {
	    float speed = spid.getInputToFloat();
	    int variationHalf = variation.getInputToInt() / 2;
	    float randomFactor = ThreadLocalRandom.current().nextFloat() * (variationHalf * 2) - variationHalf;
	    float adjustedSpeed = Math.max(speed + randomFactor, 1.0f);
	    return adjustedSpeed;
	}
}
