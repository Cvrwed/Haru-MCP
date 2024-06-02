package cc.unknown.ui.clickgui.impl.theme;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.vecmath.Vector2d;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.utils.client.ColorUtil;
import net.minecraft.util.enums.EnumChatFormatting;

public enum Theme implements ColorUtil {

    // credits to alan
    AUBERGINE("Aubergine", new Color(170, 7, 107), new Color(97, 4, 95), KeyColors.PURPLE, KeyColors.RED),
    AQUA("Aqua", new Color(185, 250, 255), new Color(79, 199, 200), KeyColors.AQUA),
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160), KeyColors.AQUA, KeyColors.LIME),
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243), KeyColors.PINK, KeyColors.PURPLE),
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233), KeyColors.RED, KeyColors.PURPLE, KeyColors.PINK),
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135), KeyColors.AQUA, KeyColors.RED, KeyColors.PINK),
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83),  KeyColors.PURPLE, KeyColors.PINK),
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171), KeyColors.AQUA, KeyColors.LIME),
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47), KeyColors.LIME, KeyColors.DARK_GREEN),
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43), KeyColors.RED, KeyColors.ORANGE),
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), KeyColors.PINK, KeyColors.DARK_BLUE, KeyColors.AQUA),
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), KeyColors.DARK_BLUE, KeyColors.PURPLE),
    MAY("May", new Color(253, 219, 245), new Color(238, 79, 238), KeyColors.PINK, KeyColors.PURPLE),
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51), KeyColors.ORANGE, KeyColors.YELLOW),
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243), KeyColors.PINK),
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39), KeyColors.RED),
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234), new Color(18, 232, 232), KeyColors.AQUA, KeyColors.GRAY),
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(122, 44, 77), KeyColors.PINK, KeyColors.PURPLE, KeyColors.RED),
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74), KeyColors.YELLOW, KeyColors.ORANGE),
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232), KeyColors.AQUA, KeyColors.DARK_BLUE);

    private final String themeName;
    private final Color firstColor, secondColor, thirdColor;
    private final ArrayList<KeyColors> keyColors;

    Theme(String themeName, Color firstColor, Color secondColor, KeyColors... keyColors) {
        this.themeName = themeName;
        this.firstColor = this.thirdColor = firstColor;
        this.secondColor = secondColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
    }

    Theme(String themeName, Color firstColor, Color secondColor, Color thirdColor, KeyColors... keyColors) {
        this.themeName = themeName;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.thirdColor = thirdColor;
        this.keyColors = new ArrayList<>(Arrays.asList(keyColors));
    }

    public Color getAccentColor(Vector2d screenCoordinates) {
        return ColorUtil.mixColors(firstColor, secondColor, getBlendFactor(screenCoordinates));
    }
    
    public Color getAccentColor() {
        return getAccentColor(new Vector2d(0, 0));
    }
    
    public Color getBackColor() {
        return new Color(0, 0, 0, 100);
    }
    
    public Color getMainColor() {
        ClickGuiModule clickgui = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
        
        String themeName = clickgui.clientTheme.getMode();
        
        for (Theme theme : Theme.values()) {
            if (theme.getThemeName().equalsIgnoreCase(themeName)) {
                return theme.getAccentColor();
            }
        }
        
        return Color.getHSBColor((clickgui.clickGuiColor.getInputToFloat() % 360) / 360.0f, clickgui.saturation.getInputToFloat(), clickgui.brightness.getInputToFloat());        
    }

    public double getBlendFactor(Vector2d screenCoordinates) {
        return Math.sin(System.currentTimeMillis() / 600.0D
                + /*screenCoordinates.getX()*/ 10 * 0.005D
                + /*screenCoordinates.getY()*/ 10 * 0.06D
        ) * 0.5D + 0.5D;
    }
    
    public Color getGradient() {
    	return convert(firstColor, secondColor);
    }
    
    public Color convert(Color color, Color color2) {
        return new Color((int) (color.getRed() * (Math.sin(System.currentTimeMillis() / 1.0E8 * 0.5 * 400000.0 + 10 * 0.550000011920929) + 1.0) * 0.5 + color2.getRed() * 1.0 - (Math.sin(System.currentTimeMillis() / 1.0E8 * 0.5 * 400000.0 + 10 * 0.550000011920929) + 1.0) * 0.5), (int) (color.getGreen() * (Math.sin(System.currentTimeMillis() / 1.0E8 * 0.5 * 400000.0 + 10 * 0.550000011920929) + 1.0) * 0.5 + color2.getGreen() * 1.0 - (Math.sin(System.currentTimeMillis() / 1.0E8 * 0.5 * 400000.0 + 10 * 0.550000011920929) + 1.0) * 0.5), (int) (color.getBlue() * (Math.sin(System.currentTimeMillis() / 1.0E8 * 0.5 * 400000.0 + 10 * 0.550000011920929) + 1.0) * 0.5 + color2.getBlue() * 1.0 - (Math.sin(System.currentTimeMillis() / 1.0E8 * 0.5 * 400000.0 + 10 * 0.550000011920929) + 1.0) * 0.5));
    }

    public enum KeyColors {
        RED(new Color(255, 50, 50)),
        ORANGE(new Color(255, 128, 50)),
        YELLOW(new Color(255, 255, 50)),
        LIME(new Color(128, 255, 50)),
        DARK_GREEN(new Color(50, 128, 50)),
        AQUA(new Color(50, 200, 255)),
        DARK_BLUE(new Color(50, 100, 200)),
        PURPLE(new Color(128, 50, 255)),
        PINK(new Color(255, 128, 255)),
        GRAY(new Color(100, 100, 110));

        private KeyColors(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}

		private final Color color;
    }

	public String getThemeName() {
		return themeName;
	}

	public Color getThirdColor() {
		return thirdColor;
	}

	public ArrayList<KeyColors> getKeyColors() {
		return keyColors;
	}
}