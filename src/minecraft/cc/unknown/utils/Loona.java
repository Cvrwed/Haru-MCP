package cc.unknown.utils;

import cc.unknown.Haru;
import cc.unknown.ui.clickgui.impl.theme.Theme;
import net.minecraft.client.Minecraft;

public interface Loona {
	static Minecraft mc = Minecraft.getMinecraft();
	
    default Theme getTheme() {
        return Haru.instance.getThemeManager().getTheme();
    }
}