package cc.unknown.module.impl.combat;

import java.util.List;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.vec.AxisAlignedBB;
import net.minecraft.util.vec.Vec3;

@Register(name = "Reach", category = Category.Combat)
public class Reach extends Module {
	private DoubleSliderValue rangeCombat = new DoubleSliderValue("Range", 3, 3, 3.0, 6, 0.01);
	private SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue weapon_only = new BooleanValue("Only Weapon", false);
	private BooleanValue moving_only = new BooleanValue("Only Move", false);
	private BooleanValue sprint_only = new BooleanValue("Only Sprint", false);
	private BooleanValue speed_only = new BooleanValue("Only Speed Potion", false);
	private BooleanValue hit_through_blocks = new BooleanValue("Hit through blocks", false);

	public Reach() {
		this.registerSetting(rangeCombat, chance, weapon_only, moving_only, sprint_only, speed_only, hit_through_blocks);
	}
	
	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + rangeCombat.getInputMin() + ", " + rangeCombat.getInputMax() + "]");
	}
		
	@EventLink
	public void onMouse(MouseEvent e) {
		AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
		if (PlayerUtil.inGame() && e.getButton() == 0 && (!clicker.isEnabled() || !Mouse.isButtonDown(0)) || ClickUtil.instance.isClicking()) {
			callReach();
		}
	}
	
	public double getReach() {
	    double min = Math.min(rangeCombat.getInputMin(), rangeCombat.getInputMax());
	    double max = Math.max(rangeCombat.getInputMin(), rangeCombat.getInputMax());
	    return Math.random() * (max - min) + min;
	}

	private boolean callReach() {
		if (!PlayerUtil.inGame()) {
			return false;
		} else if (moving_only.isToggled() && (double) mc.player.moveForward == 0.0D
				&& (double) mc.player.moveStrafing == 0.0D) {
			return false;
		} else if (weapon_only.isToggled() && !PlayerUtil.isHoldingWeapon()) {
			return false;
		} else if (sprint_only.isToggled() && !mc.player.isSprinting()) {
			return false;
		} else if (speed_only.isToggled() && !mc.player.isPotionActive(Potion.moveSpeed)) {
			return false;
		} else if (!(chance.getInput() == 100 || Math.random() <= chance.getInput() / 100)) {
			return false;
		} else {
			if (!hit_through_blocks.isToggled() && mc.objectMouseOver != null) {
				BlockPos p = mc.objectMouseOver.getBlockPos();
				if (p != null && mc.world.getBlockState(p).getBlock() != Blocks.air) {
					return false;
				}
			}

		    double reach = getReach();

		    Object[] entityData = getEntity(reach, 0.0);
		    if (entityData == null) {
		        return false;
		    } else {
		        Entity entity = (Entity) entityData[0];
		        mc.objectMouseOver = new MovingObjectPosition(entity, (Vec3) entityData[1]);
		        mc.pointedEntity = entity;
		        return true;
		    }
		}
	}
	
	public Object[] getEntity(double distance, double expand) {
	    Entity renderViewEntity = mc.getRenderViewEntity();
	    Entity entity = null;

	    if (renderViewEntity != null && mc.world != null) {
	        mc.mcProfiler.startSection("pick");

	        double var3 = distance;
	        double var5 = var3;
	        Vec3 var7 = renderViewEntity.getPositionEyes(0.0f);
	        Vec3 var8 = renderViewEntity.getLook(0.0f);
	        Vec3 var9 = var7.addVector(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3);
	        Vec3 var10 = null;

	        float var11 = 1.0f;
	        List<Entity> var12 = mc.world.getEntitiesWithinAABBExcludingEntity(
	            renderViewEntity,
	            renderViewEntity.getEntityBoundingBox()
	                .addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3)
	                .expand(var11, var11, var11)
	        );

	        double var13 = var5;

	        for (Entity var16 : var12) {
	            if (var16.canBeCollidedWith()) {
	                float var17 = var16.getCollisionBorderSize();
	                AxisAlignedBB var18 = var16.getEntityBoundingBox()
	                    .expand(var17, var17, var17)
	                    .expand(expand, expand, expand);

	                MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);

	                if (var18.isVecInside(var7)) {
	                    if (0.0 < var13 || var13 == 0.0) {
	                        entity = var16;
	                        var10 = (var19 == null) ? var7 : var19.hitVec;
	                        var13 = 0.0;
	                    }
	                } else if (var19 != null) {
	                    double var20 = var7.distanceTo(var19.hitVec);
	                    if (var20 < var13 || var13 == 0.0) {
	                        if (var16 == renderViewEntity.ridingEntity) {
	                            if (var13 == 0.0) {
	                                entity = var16;
	                                var10 = var19.hitVec;
	                            }
	                        } else {
	                            entity = var16;
	                            var10 = var19.hitVec;
	                            var13 = var20;
	                        }
	                    }
	                }
	            }
	        }

	        if (var13 < var5 && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
	            entity = null;
	        }

	        mc.mcProfiler.endSection();

	        if (entity == null || var10 == null) {
	            return null;
	        }

	        return new Object[]{entity, var10};
	    }

	    return null;
	}
}
