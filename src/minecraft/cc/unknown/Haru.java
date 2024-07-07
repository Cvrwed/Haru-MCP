package cc.unknown;

import java.util.ArrayList;
import java.util.List;

import cc.unknown.config.HudConfig;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.managers.CommandManager;
import cc.unknown.managers.ConfigManager;
import cc.unknown.managers.ModuleManager;
import cc.unknown.managers.PacketManager;
import cc.unknown.ui.clickgui.HaruGui;
import cc.unknown.ui.clickgui.impl.theme.ThemeManager;
import de.florianmichael.viamcp.ViaMCP;

public enum Haru {
	instance;

	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private PacketManager packetManager;
	private ModuleManager moduleManager;
	private ThemeManager themeManager;

	private HaruGui haruGui;
	private EventBus eventBus = new EventBus();
	
	private final List<Loader> components = new ArrayList<>();
	
	{
		components.add(() -> commandManager = new CommandManager());
		components.add(() -> moduleManager = new ModuleManager());
		components.add(() -> themeManager = new ThemeManager());
		components.add(() -> configManager = new ConfigManager());
		components.add(() -> haruGui = new HaruGui());
		components.add(() -> {
			hudConfig = new HudConfig();
			hudConfig.applyPositionHud();
		});
		
		try {
		    ViaMCP.INSTANCE.initAsyncSlider();
		 } catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public void startClient() {
		components.forEach(Loader::init);
	}

	public void stopClient() {
		hudConfig.savePositionHud();
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public HudConfig getHudConfig() {
		return hudConfig;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public HaruGui getHaruGui() {
		return haruGui;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public ThemeManager getThemeManager() {
		return themeManager;
	}
}