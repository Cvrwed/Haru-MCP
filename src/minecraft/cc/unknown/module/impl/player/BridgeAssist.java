package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MoveEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.MathHelper;

@Info(name = "BridgeAssist", category = Category.Player)
public class BridgeAssist extends Module {

	private boolean waitingForAim;
	private boolean gliding;
	private long startWaitTime;
	private final float[] godbridgePos = { 75.6f, -315, -225, -135, -45, 0, 45, 135, 225, 315 };
	private final float[] moonwalkPos = { 79.6f, -340, -290, -250, -200, -160, -110, -70, -20, 0, 20, 70, 110, 160, 200,
			250, 290, 340 };
	private final float[] breezilyPos = { 79.9f, -360, -270, -180, -90, 0, 90, 180, 270, 360 };
	private final float[] normalPos = { 78f, -315, -225, -135, -45, 0, 45, 135, 225, 315 };
	private double speedYaw, speedPitch;
	private float waitingForYaw, waitingForPitch;

	private ModeValue assistMode = new ModeValue("Assist Mode", "Basic", "God Bridge", "Moon Walk", "Breezily",
			"Basic");
	private SliderValue assistChance = new SliderValue("Assist Range", 38, 1, 40, 1);
	private SliderValue speedAngle = new SliderValue("Angle Speed", 50, 1, 100, 1);
	private SliderValue waitFor = new SliderValue("Wait Time", 70, 0, 200, 1);
	private BooleanValue onlySneaking = new BooleanValue("Only While Sneaking", false);
	private BooleanValue enableSafeWalk = new BooleanValue("Enable SafeWalk", true);
	private BooleanValue safeInAir = new BooleanValue("Safe in Air", false);

	public BridgeAssist() {
		this.registerSetting(assistMode, assistChance, waitFor, speedAngle, onlySneaking, enableSafeWalk, safeInAir);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + assistMode.getMode() + "]");
	}

	@Override
	public void onEnable() {
		this.waitingForAim = false;
		this.gliding = false;
		super.onEnable();
	}

	@EventLink
	public void onSafe(MoveEvent e) {
		if ((enableSafeWalk.isToggled() && mc.player.onGround) || (safeInAir.isToggled() && PlayerUtil.playerOverAir())) {
			e.setSaveWalk(true);
		}
	}

	@EventLink
	public void onRender(RenderEvent e) {
		if (e.is3D()) {
			if (!PlayerUtil.inGame() || (!PlayerUtil.playerOverAir() && mc.player.onGround)
					|| (onlySneaking.isToggled() && !mc.player.isSneaking())) {
				return;
			}

			if (gliding) {
				float yaw = MathHelper.wrapAngle180(mc.player.rotationYaw).floatValue();
				float pitch = MathHelper.wrapAngle90(mc.player.rotationPitch).floatValue();
				double d0 = Math.abs(yaw - speedYaw);
				double d1 = Math.abs(yaw + speedYaw);
				double d2 = Math.abs(pitch - speedPitch);
				double d3 = Math.abs(pitch + speedPitch);

				if (speedYaw > d0 || speedYaw > d1 || speedPitch > d2 || speedPitch > d3) {
					mc.player.rotationYaw = waitingForYaw;
					mc.player.rotationPitch = waitingForPitch;
				} else {
					mc.player.rotationYaw += (mc.player.rotationYaw < waitingForYaw) ? speedYaw : -speedYaw;
					mc.player.rotationPitch += (mc.player.rotationPitch < waitingForPitch) ? speedPitch
							: -speedPitch;
				}

				if (mc.player.rotationYaw == waitingForYaw && mc.player.rotationPitch == waitingForPitch) {
					gliding = false;
					waitingForAim = false;
				}
				return;
			}

			if (!waitingForAim) {
				waitingForAim = true;
				startWaitTime = System.currentTimeMillis();
				return;
			}

			if (System.currentTimeMillis() - startWaitTime < waitFor.getInput()) {
				return;
			}

			float yaw = MathHelper.wrapAngle180(mc.player.rotationYaw).floatValue();
			float pitch = MathHelper.wrapAngle90(mc.player.rotationPitch).floatValue();
			float range = (float) assistChance.getInput();

			float[] positions = null;

			switch (assistMode.getMode()) {
			case "God Bridge":
				positions = godbridgePos;
				break;
			case "Moon Walk":
				positions = moonwalkPos;
				break;
			case "Breezily":
				positions = breezilyPos;
				break;
			case "Basic":
				positions = normalPos;
				break;
			}

			if (positions != null && positions.length > 0 && positions[0] >= pitch - range
					&& positions[0] <= pitch + range) {
				for (int k = 1; k < positions.length; k++) {
					if (positions[k] >= yaw - range && positions[k] <= yaw + range) {
						CombatUtil.instance.aimAt(positions[0], positions[k], mc.player.rotationYaw,
								mc.player.rotationPitch, speedAngle.getInput());
						waitingForAim = false;
						return;
					}
				}
			}
			waitingForAim = false;
		}
	}
}
