package cc.unknown.module.impl.visuals;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderItemEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

@Register(name = "Animations", category = Category.Visuals)
public class Animations extends Module {

	private ModeValue blockMode = new ModeValue("Block Mode", "1.7", "1.7", "1.8", "Astolfo", "Spin");

	public SliderValue animationSpeed = new SliderValue("Animation Speed", 1.0D, 0.1D, 3.0D, 0.1D);
	private SliderValue spinSpeed = new SliderValue("Spin Speed", 0.5D, 0.1D, 2.0D, 0.1D);
	private SliderValue xValue = new SliderValue("X", 0.0D, -1.0D, 1.0D, 0.05D);
	private SliderValue yValue = new SliderValue("Y", 0.0D, -1.0D, 1.0D, 0.05D);
	private SliderValue zValue = new SliderValue("Z", 0.0D, -1.0D, 1.0D, 0.05D);

	private BooleanValue fixRod = new BooleanValue("Fix Rod Position", true);

	public Animations() {
		registerSetting(blockMode, animationSpeed, spinSpeed, xValue, yValue, zValue, fixRod);
	}

	@EventLink
	public void onRenderItem(RenderItemEvent e) {
		if (e.getItemToRender().getItem() instanceof ItemMap) {
			return;
		}

		if (fixRod.isToggled() && e.getItemToRender().getItem() instanceof ItemFishingRod) {
			GlStateManager.translate(0.0F, 0.0F, -0.35F);
		}

		final EnumAction itemAction = e.getEnumAction();
		final ItemRenderer itemRenderer = mc.getItemRenderer();
		final float animationProgression = e.getAnimationProgression();
		final float swingProgress = e.getSwingProgress();
		final float convertedProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
		GlStateManager.translate(xValue.getInputToFloat(), yValue.getInputToFloat(), zValue.getInputToFloat());

		
		if ((blockMode.is("Spin") && !e.isUseItem() && (e.getItemToRender().getItem() instanceof ItemPickaxe || e.getItemToRender().getItem() instanceof ItemSword || e.getItemToRender().getItem() instanceof ItemAxe)) || (!blockMode.is("Spin") && e.isUseItem() && itemAction == EnumAction.BLOCK)) {
			switch (blockMode.getMode()) {
			case "1.7":
				itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
				itemRenderer.func_178103_d();
				break;
			case "1.8":
				itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
				itemRenderer.func_178103_d();
				break;
			case "Astolfo":
				GlStateManager.rotate(System.currentTimeMillis() % 360, 0, 0, -0.1f);
				itemRenderer.transformFirstPersonItem(animationProgression / 1.6f, 0);
				itemRenderer.func_178103_d();
				break;
			case "Spin":
				float angle = (float) (System.currentTimeMillis() % (360 * 20) * spinSpeed.getInput());
				GlStateManager.translate(0.54F, -0.4F, -0.81999997F);
				GlStateManager.translate(0.0F, 0f, 0.0F);
				GlStateManager.scale(0.4f, 0.4f, 0.4f);
				GlStateManager.rotate(72.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(angle, 0f, 0.1f, 0f);
				break;
			}
			e.setCancelled(true);
		}
	}
}
