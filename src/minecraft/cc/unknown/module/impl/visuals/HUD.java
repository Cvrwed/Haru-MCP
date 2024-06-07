package cc.unknown.module.impl.visuals;

import static cc.unknown.ui.HudPosition.arrayListX;
import static cc.unknown.ui.HudPosition.arrayListY;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.HudPosition;
import cc.unknown.ui.clickgui.HaruGui;
import cc.unknown.ui.clickgui.impl.theme.Theme;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.FuckUtil.PositionMode;
import cc.unknown.utils.misc.HiddenUtil;
import net.minecraft.client.gui.Gui;

@Info(name = "HUD", category = Category.Visuals)
public class HUD extends Module {

	private ModeValue colorMode = new ModeValue("ArrayList Theme", "Static", "Static", "Aubergine", "Aqua", "Blend", "Bubblegum", "Cherry", "Christmas", "Digital Horizon", "Express", "Lime Water", "Lush", "Halogen", "Hyper", "Magic", "May", "Orange Juice", "Pastel", "Satin", "Snowy Sky", "Sundae", "Sunkist", "Water", "Theme");
	private SliderValue arrayColor = new SliderValue("Array Color [H/S/B]", 0, 0, 350, 10);
	private SliderValue saturation = new SliderValue("Saturation [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	private SliderValue brightness = new SliderValue("Brightness [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	private BooleanValue editPosition = new BooleanValue("Edit Position", false);
	private BooleanValue noRenderModules = new BooleanValue("No Render Modules", true);
	private BooleanValue background = new BooleanValue("Background", true);
	private BooleanValue lowercase = new BooleanValue("Lowercase", false);
	public BooleanValue suffix = new BooleanValue("Suffix", false);

	public HUD() {
		this.registerSetting(colorMode, arrayColor, saturation, brightness, editPosition, noRenderModules, background,
				lowercase, suffix);
	}

	@Override
	public void onEnable() {
		Haru.instance.getModuleManager().sort();
	}

	@Override
	public void guiButtonToggled(BooleanValue b) {
		if (b == editPosition) {
			editPosition.disable();
			mc.displayGuiScreen(new HudPosition());
		}
	}

	@EventLink
	public void onDraw(RenderEvent e) {
		if (e.is2D()) {
			if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof HaruGui) {
				return;
			}

			HiddenUtil.setVisible(!noRenderModules.isToggled());

			int margin = 2;
			AtomicInteger y = new AtomicInteger(arrayListY.get());

			if (Arrays.asList(PositionMode.DOWNLEFT, PositionMode.DOWNRIGHT)
					.contains(FuckUtil.instance.getPositionMode())) {
				Haru.instance.getModuleManager().sort();
			}

			List<Module> en = new ArrayList<>(Haru.instance.getModuleManager().getModule());
			if (en.isEmpty()) {
				return;
			}

			AtomicInteger textBoxWidth = new AtomicInteger(
					Haru.instance.getModuleManager().getLongestActiveModule(mc.fontRendererObj));
			AtomicInteger textBoxHeight = new AtomicInteger(
					Haru.instance.getModuleManager().getBoxHeight(mc.fontRendererObj, margin));

			if (arrayListX.get() < 0) {
				arrayListX.set(margin);
			}

			if (arrayListY.get() < 0) {
				arrayListY.set(margin);
			}

			arrayListX.set((arrayListX.get() + textBoxWidth.get() > mc.displayWidth / 2)
					? (mc.displayWidth / 2 - textBoxWidth.get() - margin)
					: arrayListX.get());

			arrayListY.set((arrayListY.get() + textBoxHeight.get() > mc.displayHeight / 2)
					? (mc.displayHeight / 2 - textBoxHeight.get())
					: arrayListY.get());

			AtomicInteger color = new AtomicInteger(0);

			en.stream().filter(m -> m.isEnabled() && m.isHidden()).forEach(m -> {

				String nameOrSuffix = m.getRegister().name();
				if (suffix.isToggled()) {
					nameOrSuffix += " §7" + m.getSuffix();
				}
				if (lowercase.isToggled()) {
					nameOrSuffix = nameOrSuffix.toLowerCase();
				}

				switch (colorMode.getMode()) {
				case "Static":
					color.set(Color.getHSBColor((arrayColor.getInputToFloat() % 360) / 360.0f,
							saturation.getInputToFloat(), brightness.getInputToFloat()).getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Aubergine":
					color.set(getTheme().AUBERGINE.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Aqua":
					color.set(getTheme().AQUA.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Blend":
					color.set(getTheme().BLEND.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Bubblegum":
					color.set(getTheme().BUBBLEGUM.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Cherry":
					color.set(getTheme().CHERRY.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Digital Horizon":
					color.set(getTheme().DIGITAL_HORIZON.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Express":
					color.set(getTheme().EXPRESS.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Lime Water":
					color.set(getTheme().LIME_WATER.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Lush":
					color.set(getTheme().LUSH.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Halogen":
					color.set(getTheme().HALOGEN.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Hyper":
					color.set(getTheme().HYPER.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Magic":
					color.set(getTheme().MAGIC.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "May":
					color.set(getTheme().MAY.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Orange Juice":
					color.set(getTheme().ORANGE_JUICE.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Pastel":
					color.set(getTheme().PASTEL.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Satin":
					color.set(getTheme().SATIN.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Snowy Sky":
					color.set(getTheme().SNOWY_SKY.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Sundae":
					color.set(getTheme().SUNDAE.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Sunkist":
					color.set(getTheme().SUNKIST.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Water":
					color.set(getTheme().WATER.getAccentColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				case "Theme":
					color.set(getTheme().getMainColor().getRGB());
					y.addAndGet(mc.fontRendererObj.FONT_HEIGHT + margin);
					break;
				}

				if ((FuckUtil.instance.getPositionMode() == PositionMode.DOWNRIGHT)
						|| (FuckUtil.instance.getPositionMode() == PositionMode.UPRIGHT)) {
					if (background.isToggled()) {
						int backgroundWidth;
						backgroundWidth = mc.fontRendererObj.getStringWidth(nameOrSuffix) + 5; // Ajuste adicional para
																								// la fuente
																								// predeterminada
						Gui.drawRect(arrayListX.get() + (textBoxWidth.get()) + 4, y.get(),
								arrayListX.get() + (textBoxWidth.get() - backgroundWidth),
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 2, (new Color(0, 0, 0, 87)).getRGB());
					}

					mc.fontRendererObj.drawString(nameOrSuffix,
							(float) arrayListX.get()	
									+ (textBoxWidth.get() - mc.fontRendererObj.getStringWidth(nameOrSuffix)),
							(float) y.get() + 2, color.get(), true);

				} else {
					if (background.isToggled()) {
						int backgroundWidth;
						backgroundWidth = mc.fontRendererObj.getStringWidth(nameOrSuffix) + 4; // Ajuste adicional
						Gui.drawRect(arrayListX.get() - 3, y.get(), arrayListX.get() + backgroundWidth,
								y.get() + mc.fontRendererObj.FONT_HEIGHT + 2, (new Color(0, 0, 0, 100)).getRGB());
					}

					mc.fontRendererObj.drawString(nameOrSuffix, (float) arrayListX.get(), (float) y.get() + 2,
							color.get(), true);

				}
			});
		}
	}

}
