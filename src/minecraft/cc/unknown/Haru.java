package cc.unknown;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ConfigManager;
import cc.unknown.config.HudConfig;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.HaruGui;
import cc.unknown.ui.clickgui.impl.theme.ThemeManager;
import cc.unknown.utils.network.PacketManager;
import cc.unknown.utils.player.rotation.RotationManager;
import de.florianmichael.viamcp.ViaMCP;

public enum Haru {
	instance;

	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private RotationManager rotationManager;
	private PacketManager packetManager;
	private ModuleManager moduleManager;
	private ThemeManager themeManager;

	private HaruGui haruGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		rotationManager = new RotationManager();
		packetManager = new PacketManager();
		haruGui = new HaruGui();
		configManager = new ConfigManager();
		themeManager = new ThemeManager();
		hudConfig = new HudConfig();
		hudConfig.applyPositionHud();
		
		try {
		    ViaMCP.INSTANCE.initAsyncSlider();
		 } catch (Exception e) {
		    e.printStackTrace();
		}
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