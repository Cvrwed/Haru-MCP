package cc.unknown;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ConfigManager;
import cc.unknown.config.HudConfig;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.HaruGui;
import cc.unknown.utils.keystrokes.KeyStrokeRenderer;
import cc.unknown.utils.keystrokes.KeyStrokes;
import cc.unknown.utils.player.rotation.RotationManager;

public enum Haru {
	instance;
	
	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private KeyStrokes keyStrokes;
	private KeyStrokeRenderer keyStrokeRenderer;
	private RotationManager rotationManager;
	private ModuleManager moduleManager;

	private HaruGui haruGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		rotationManager = new RotationManager();
		keyStrokes = new KeyStrokes();
		keyStrokeRenderer = new KeyStrokeRenderer();
		haruGui = new HaruGui();
		configManager = new ConfigManager();
		hudConfig = new HudConfig();
		hudConfig.applyKeyStrokes();
		hudConfig.applyHud();
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
}