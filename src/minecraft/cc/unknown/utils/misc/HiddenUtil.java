package cc.unknown.utils.misc;

import java.util.Arrays;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.other.*;
import cc.unknown.module.impl.player.Sprint;
import cc.unknown.module.impl.visuals.*;

public class HiddenUtil {
	public static void setVisible(boolean visible) {
	    if (Haru.instance.getModuleManager() != null) {
	        List<Class<? extends Module>> modules = Arrays.asList(
	        		Tweaks.class,
	        		
	        		Ambience.class,
	        		ClickGuiModule.class,
	        		Fullbright.class,
	        		HUD.class,
	        		Nametags.class,
	        		CpsDisplay.class,
	        		ESP.class,
	        		Fullbright.class,
	        		Nametags.class,
	        		TargetHUD.class,
	        		Trajectories.class,
	        		
	        		Sprint.class,
	        		
	        		AutoLeave.class,
	        		Autoplay.class,
	        		Inventory.class,
	        		Keystrokes.class,
	        		MidClick.class

	        );

	        List<Module> x = Haru.instance.getModuleManager().getModule(modules.toArray(new Class<?>[0]));

	        for (Module m : x) {
	            m.setHidden(visible);
	        }
	    }
	}
}
