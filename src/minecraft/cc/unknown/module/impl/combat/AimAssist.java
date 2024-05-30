package cc.unknown.module.impl.combat;

import static org.apache.commons.lang3.RandomUtils.nextFloat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.rotation.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

@Info(name = "AimAssist", category = Category.Combat)
public class AimAssist extends Module {

	private ModeValue mode = new ModeValue("Priority", "Low Health", "Low Health", "Distance", "Angle", "Armor");
	private SliderValue horizontalAimSpeed = new SliderValue("Horizontal Aim Speed", 45, 5, 100, 1);
	private SliderValue horizontalAimFineTuning = new SliderValue("Horizontal Aim Fine-tuning", 15, 2, 97, 1);
	private BooleanValue horizontalRandomization = new BooleanValue("Horizontal Randomization", false);
	private SliderValue horizontalRandomizationAmount = new SliderValue("Horizontal Randomization", 1.2, 0.1, 5, 0.01);
	private SliderValue fieldOfView = new SliderValue("Field of View", 90.0, 15.0, 360.0, 1.0);
	private SliderValue enemyDetectionRange = new SliderValue("Enemy Detection Range", 4.5, 1.0, 10.0, 0.5);
	private BooleanValue verticalAlignmentCheck = new BooleanValue("Vertical Alignment Check", false);
	private BooleanValue verticalRandomization = new BooleanValue("Vertical Randomization", false);
	private SliderValue verticalRandomizationAmount = new SliderValue("Vertical Randomization", 1.2, 0.1, 5, 0.01);
	private SliderValue verticalAimSpeed = new SliderValue("Vertical Aim Speed", 10, 1, 15, 1);
	private SliderValue verticalAimFineTuning = new SliderValue("Vertical Aim Fine-tuning", 5, 1, 10, 1);
	private BooleanValue clickAim = new BooleanValue("Auto Aim on Click", true);
	private BooleanValue centerAim = new BooleanValue("Instant Aim Centering", false);
	private BooleanValue moveFix = new BooleanValue("Movement Fix", false);
	private BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friendly Entities", false);
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teammates", false);
	private BooleanValue aimAtInvisibleEnemies = new BooleanValue("Aim at Invisible Enemies", false);
	private BooleanValue lineOfSightCheck = new BooleanValue("Line of Sight Check", true);
	private BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Disable Aim While Breaking Block", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only Aim", false);
	private Random random = new Random();
	private EntityPlayer enemy = null; // fixed

	public AimAssist() {
		this.registerSetting(mode, horizontalAimSpeed, horizontalAimFineTuning, horizontalRandomization,
				horizontalRandomizationAmount, fieldOfView, enemyDetectionRange, verticalAlignmentCheck,
				verticalRandomization, verticalRandomizationAmount, verticalAimSpeed, verticalAimFineTuning, clickAim,
				centerAim, moveFix, ignoreFriendlyEntities, ignoreTeammates, aimAtInvisibleEnemies, lineOfSightCheck,
				disableAimWhileBreakingBlock, weaponOnly);
	}

	@EventLink
	public void onLiving(LivingEvent e) {
		if (mc.player == null || mc.currentScreen != null || !mc.inGameHasFocus) {
			return;
		}

		if (disableAimWhileBreakingBlock.isToggled() && mc.objectMouseOver != null) {
			BlockPos blockPos = mc.objectMouseOver.getBlockPos();
			if (blockPos != null) {
				Block block = mc.world.getBlockState(blockPos).getBlock();
				if (!isAirOrLiquidBlock(block)) {
					return;
				}
			}
		}

		if (!weaponOnly.isToggled() || PlayerUtil.isHoldingWeapon()) {
			AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
			if ((clickAim.isToggled() && ClickUtil.instance.isClicking())
					|| (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
				enemy = getEnemy();
				if (enemy != null) {
					if (centerAim.isToggled()) {
						CombatUtil.instance.aim(enemy, 0.0f);
					}

					double fovEntity = PlayerUtil.fovFromEntity(enemy);
					double pitchEntity = PlayerUtil.PitchFromEntity(enemy, 0);

		            double horizontalRandomOffset = ThreadLocalRandom.current().nextDouble(horizontalAimFineTuning.getInput() - 1.47328, horizontalAimFineTuning.getInput() + 2.48293) / 100;
		            float resultHorizontal = (float) (-(fovEntity * horizontalRandomOffset + fovEntity / (101.0D - (float) ThreadLocalRandom.current().nextDouble(horizontalAimSpeed.getInput() - 4.723847, horizontalAimSpeed.getInput()))));

		            double verticalRandomOffset = ThreadLocalRandom.current().nextDouble(verticalAimFineTuning.getInput() - 1.47328, verticalAimFineTuning.getInput() + 2.48293) / 100;
		            float resultVertical = (float) (-(pitchEntity * verticalRandomOffset + pitchEntity / (101.0D - (float) ThreadLocalRandom.current().nextDouble(verticalAimSpeed.getInput() - 4.723847, verticalAimSpeed.getInput()))));

		            if (fovEntity > 1.0D || fovEntity < -1.0D) {
		            	float yawChange = random.nextBoolean() ? -nextFloat(0F, horizontalRandomizationAmount.getInputToFloat()) : nextFloat(0F, horizontalRandomizationAmount.getInputToFloat());
		            	float yawAdjustment = (float) (horizontalRandomization.isToggled() ? yawChange : resultHorizontal);
		            	mc.player.rotationYaw += yawAdjustment;

		            	if (verticalAlignmentCheck.isToggled()) {
		            		float pitchChange = random.nextBoolean() ? -nextFloat(0F, verticalRandomizationAmount.getInputToFloat()) : nextFloat(0F, verticalRandomizationAmount.getInputToFloat());
		            		float pitchAdjustment = (float) (verticalRandomization.isToggled() ? pitchChange : resultVertical);
		            		float newPitch = mc.player.rotationPitch + pitchAdjustment;
		            	    mc.player.rotationPitch += pitchAdjustment;
		            	    mc.player.rotationPitch = newPitch >= 90f ? newPitch - 360f : newPitch <= -90f ? newPitch + 360f : newPitch;
		            	}
		            }
				}
			}
		}
	}

	@EventLink
	public void onJump(JumpEvent e) {
		if (enemy != null && moveFix.isToggled()) {
			e.setYaw(mc.player.rotationYaw);
		}
	}

	@EventLink
	public void onStrafe(StrafeEvent e) {
		if (enemy != null && moveFix.isToggled()) {
			e.setYaw(mc.player.rotationYaw);
		}
	}

	public EntityPlayer getEnemy() {
		int fov = (int) fieldOfView.getInput();
		if (mc.player == null || mc.world == null) return null;
		List<EntityPlayer> targets = new ArrayList<>();

		for (Entity entity : mc.world.getLoadedEntityList().stream().filter(Objects::nonNull).collect(Collectors.toList())) {
			if (entity instanceof EntityPlayer) {
				if (entity == mc.player) continue;
				
				if (mc.player.getDistanceToEntity(entity) > enemyDetectionRange.getInput()) continue;
				if (ignoreFriendlyEntities.isToggled() && FriendUtil.instance.friends.contains(entity.getName())) continue;
				if (!mc.player.canEntityBeSeen(entity) && lineOfSightCheck.isToggled()) continue;
				if (ignoreTeammates.isToggled() && CombatUtil.instance.isTeam((EntityPlayer) entity)) continue;
				if (!aimAtInvisibleEnemies.isToggled() && entity.isInvisible()) continue;
				if (!centerAim.isToggled() && fov != 360 && !isWithinFOV((EntityPlayer) entity, fov)) continue;
				targets.add((EntityPlayer) entity);
			}
		}
		
		switch (mode.getMode()) {
		case "Distance": {
			targets.sort(Comparator.comparingDouble(entity -> mc.player.getDistanceToEntity(entity)));
			}
			break;
		case "Angle": {
			targets.sort((entity1, entity2) -> {
				float[] rot1 = RotationManager.getRotations(entity1);
				float[] rot2 = RotationManager.getRotations(entity2);
				return (int) ((mc.player.rotationYaw - rot1[0]) - (mc.player.rotationYaw - rot2[0]));
			});}
		break;
		case "Armor": {
			targets.sort(Comparator.comparingInt(entity -> (entity instanceof EntityPlayer ? ((EntityPlayer) entity).inventory.getTotalArmorValue() : (int) entity.getHealth())));
			}
		break;
		case "Low Health": {
			targets.sort(Comparator.comparingDouble(entity -> ((EntityPlayer) entity).getHealth()).reversed());
		}
			break;
		}

    return targets.isEmpty() ? null : targets.get(0);
	}

	private boolean isWithinFOV(EntityPlayer player, int fieldOfView) {
		return PlayerUtil.fov(player, (float) fieldOfView);
	}

	private boolean isAirOrLiquidBlock(Block block) {
		return block == Blocks.air || block instanceof BlockLiquid;
	}
}
