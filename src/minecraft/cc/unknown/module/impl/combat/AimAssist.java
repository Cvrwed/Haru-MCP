package cc.unknown.module.impl.combat;

import static org.apache.commons.lang3.RandomUtils.nextFloat;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

@Info(name = "AimAssist", category = Category.Combat)
public class AimAssist extends Module {

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
	private BooleanValue ignoreFriendlyEntities = new BooleanValue("Ignore Friendly Entities", false);
	private BooleanValue ignoreTeammates = new BooleanValue("Ignore Teammates", false);
	private BooleanValue aimAtInvisibleEnemies = new BooleanValue("Aim at Invisible Enemies", false);
	private BooleanValue lineOfSightCheck = new BooleanValue("Line of Sight Check", true);
	private BooleanValue mouseOverEntity = new BooleanValue("Mouse Over Entity", false);
	private BooleanValue disableAimWhileBreakingBlock = new BooleanValue("Disable Aim While Breaking Block", false);
	private BooleanValue weaponOnly = new BooleanValue("Weapon Only Aim", false);
	private Random random = new Random();
	private EntityPlayer enemy = null; // fixed

	public AimAssist() {
		this.registerSetting(horizontalAimSpeed, horizontalAimFineTuning, horizontalRandomization, horizontalRandomizationAmount, fieldOfView, enemyDetectionRange, verticalAlignmentCheck, verticalRandomization, verticalRandomizationAmount, verticalAimSpeed, verticalAimFineTuning, clickAim, ignoreFriendlyEntities, ignoreTeammates, aimAtInvisibleEnemies, lineOfSightCheck, mouseOverEntity, disableAimWhileBreakingBlock, weaponOnly);
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
			if ((clickAim.isToggled() && ClickUtil.instance.isClicking()) || (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
				enemy = getEnemy();
				if (enemy != null) {
					
					if (mouseOverEntity.isToggled() && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
						return;
					}
					
					double yawFov = PlayerUtil.fovFromEntity(enemy);
					double pitchFov = PlayerUtil.PitchFromEntity(enemy, 0);

					double horizontalOffset = ThreadLocalRandom.current().nextDouble(horizontalAimFineTuning.getInput() - 1.47328, horizontalAimFineTuning.getInput() + 2.48293)/ 100;
					float resultHorizontal = (float) (-(yawFov * horizontalOffset + yawFov / (101.0D - (float) ThreadLocalRandom.current().nextDouble(horizontalAimSpeed.getInput() - 4.723847, horizontalAimSpeed.getInput()))));

					double verticalOffset = ThreadLocalRandom.current().nextDouble(verticalAimFineTuning.getInput() - 1.47328, verticalAimFineTuning.getInput() + 2.48293) / 100;
					float resultVertical = (float) (-(pitchFov * verticalOffset + pitchFov / (101.0D - (float) ThreadLocalRandom.current().nextDouble(verticalAimSpeed.getInput() - 4.723847, verticalAimSpeed.getInput()))));

					if (yawFov > 1.0D || yawFov < -1.0D) {
						float yawChange = random.nextBoolean() ? -nextFloat(0F, horizontalRandomizationAmount.getInputToFloat()) : nextFloat(0F, horizontalRandomizationAmount.getInputToFloat());
						float yawAdjustment = (float) (horizontalRandomization.isToggled() ? yawChange : resultHorizontal);
						mc.player.rotationYaw += yawAdjustment;

						if (verticalAlignmentCheck.isToggled()) {
							float pitchChange = random.nextBoolean() ? -nextFloat(0F, verticalRandomizationAmount.getInputToFloat()) : nextFloat(0F, verticalRandomizationAmount.getInputToFloat());
							float pitchAdjustment = (float) (verticalRandomization.isToggled() ? pitchChange : resultVertical);
							float newPitch = mc.player.rotationPitch + pitchAdjustment;
							mc.player.rotationPitch = newPitch >= 90f ? newPitch - 360f : newPitch <= -90f ? newPitch + 360f : newPitch;
						}
					}
				}
			}
		}
	}

	public EntityPlayer getEnemy() {
		int fov = fieldOfView.getInputToInt();
	    List<EntityPlayer> playerList = mc.world.playerEntities;
	    
		playerList.sort(new Comparator<EntityPlayer>() {
		    @Override
		    public int compare(EntityPlayer player1, EntityPlayer player2) {
		        boolean p1IsClose = isClose(player1, 3);
		        boolean p2IsClose = isClose(player2, 3);
		        if (p1IsClose != p2IsClose) {
		            return p1IsClose ? -1 : 1;
		        }

		        float distanceToP1 = mc.player.getDistanceToEntity(player1);
		        float distanceToP2 = mc.player.getDistanceToEntity(player2);
		        
		        return Double.compare(distanceToP1, distanceToP2);
		    }
		});
		
		for (final EntityPlayer player : playerList) {
			if (player != mc.player && player.deathTime == 0) {
				if (FriendUtil.friends.contains(player.getName()) && ignoreFriendlyEntities.isToggled()) {
					continue;
				}

				if (!mc.player.canEntityBeSeen(player) && lineOfSightCheck.isToggled()) {
					continue;
				}

				if (CombatUtil.instance.isTeam(mc.player, player) && ignoreTeammates.isToggled()) {
					continue;
				}

				if (!aimAtInvisibleEnemies.isToggled() && player.isInvisible()) {
					continue;
				}

				if (mc.player.getDistanceToEntity(player) > enemyDetectionRange.getInput()) {
					continue;
				}

				if (fov != 360 && !isWithinFOV(player, fov)) {
					continue;
				}
				
				return player;
			}
		}

		return null;
	}
	
	private boolean isWithinFOV(EntityPlayer player, int fieldOfView) {
		return PlayerUtil.fov(player, (float) fieldOfView);
	}

	private boolean isAirOrLiquidBlock(Block block) {
		return block == Blocks.air || block instanceof BlockLiquid;
	}
	
    private boolean isClose(EntityPlayer player, double range) {
    	return mc.player.getDistanceToEntity(player) <= range;
    }
}
