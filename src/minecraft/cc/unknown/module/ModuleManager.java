package cc.unknown.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.combat.*;
import cc.unknown.module.impl.exploit.*;
import cc.unknown.module.impl.move.*;
import cc.unknown.module.impl.other.*;
import cc.unknown.module.impl.player.*;
import cc.unknown.module.impl.settings.Helper;
import cc.unknown.module.impl.settings.Fixes;
import cc.unknown.module.impl.visuals.*;
import cc.unknown.utils.Loona;
import net.minecraft.client.gui.FontRenderer;

public class ModuleManager implements Loona {
	
	private final List<Module> modules = new ArrayList<>();
	private final List<Module> draggable = new ArrayList<>();
	private boolean initialized = false;
	
	public ModuleManager() {
		if (initialized) return;

		addModule(
				// combat
				new AutoClick(),
				new AimAssist(),
				new AutoBlock(),
				new Criticals(),
				new BlockHit(),
				new HitSelect(),
				new JumpReset(),
				new SprintReset(),
				new Velocity(),
				new Reach(),
				
				// exploit
				new ChatBypass(),
				new PingSpoof(),
				new TimerRange(),
				new FakeLag(),
				new TickBase(),
				new BackTrack(),
				new LagRange(),
				new Ping(),
				
				// move
				new Speed(),
				new BlockClutch(),
				new KeepSprint(),
				//new Flight(),
				
				// other
				new Autoplay(),
				new AutoLeave(),
				new AutoRefill(),
				new AutoTool(),
				new AutoRegister(),
				new BedWarsHelper(),
				new MidClick(),
				new MusicPlayer(),
				new Inventory(),
				new SelfTrap(),
				
				// player
				new AntiFireBall(),
				new AntiVoid(),
				new InvManager(),
				new Stealer(),
				new FastPlace(),
				new Scaffold(),
				new BridgeAssist(),
				new LegitScaffold(),
				new Sprint(),
				new Blink(),
				new Timer(),
				new NoSlow(),
				new NoFall(),
				
				// visuals
				new Ambience(),
				new Animations(),
				new Fullbright(),
				new ClickGuiModule(),
				new HUD(),
				new CpsDisplay(),
				new KeyStrokes(),
				new TargetHUD(),
				new Trajectories(),
				//new FreeLook(),
				new Nametags(),
				new ESP(),
				
				//
				new Fixes(),
				new Helper()
				
		);
		
   		for(Module m : modules) {
			draggable.addAll(Arrays.asList(m));
		}

		initialized = true;
	}
	
	public void addModule(Module... s) {
		modules.addAll(Arrays.asList(s));
	}
    
    public Module getModule(String name) {
        return initialized ? modules.stream().filter(module -> module.getRegister().name().equalsIgnoreCase(name)).findFirst().orElse(null) : null;
    }

    public Module getModule(Class<? extends Module> clazz) {
        return initialized ? modules.stream().filter(module -> module.getClass().equals(clazz)).findFirst().orElse(null) : null;
    }

    public List<Module> getModule() {
        return modules;
    }
    
    public List<Module> getDraggable() {
    	return draggable;
    }

    public List<Module> getModule(Class<?>[] classes) {
        return initialized ? modules.stream().filter(module -> Arrays.stream(classes).anyMatch(clazz -> module.getClass().equals(clazz))).collect(Collectors.toList()) : Collections.emptyList();
    }

    public List<Module> getCategory(Category category) {
        return initialized ? modules.stream().filter(module -> module.getRegister().category().equals(category)).collect(Collectors.toList()) : Collections.emptyList();
    }

    public void sort() {
    	HUD hud = (HUD) Haru.instance.getModuleManager().getModule(HUD.class); 
    	modules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getRegister().name() + (hud.suffix.isToggled() ? o2.getSuffix() : "")) - mc.fontRendererObj.getStringWidth(o1.getRegister().name() + (hud.suffix.isToggled() ? o1.getSuffix(): "")));
    }

    public int getLongestActiveModule(FontRenderer fontRenderer) {
        return initialized ? modules.stream().filter(Module::isEnabled).mapToInt(module -> fontRenderer.getStringWidth(module.getRegister().name())).max().orElse(0) : 0;
    }

    public int getBoxHeight(FontRenderer fontRenderer, int margin) {
        return initialized ? modules.stream().filter(Module::isEnabled).mapToInt(module -> fontRenderer.FONT_HEIGHT + margin).sum() : 0;
    }

    public int numberOfModules() {
        return modules.size();
    }

}
