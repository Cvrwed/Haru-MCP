package cc.unknown.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.keystrokes.KeyStroke;
import cc.unknown.ui.clickgui.impl.CategoryComp;
import cc.unknown.utils.Loona;
import cc.unknown.utils.client.FuckUtil;
import net.minecraft.util.MathHelper;

public class HudConfig implements Loona {
	private final File configFile;
	private final File configDir;

	public HudConfig() {
		configDir = new File(mc.mcDataDir, "Haru");
		if (!configDir.exists()) {
			configDir.mkdir();
		}

		configFile = new File(configDir, "hud");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void savePositionHud() {
		List<String> config = new ArrayList<>();
		config.add("ClickGuiPos:" + getClickGuiPos());
		
		config.add("ArrayListX:" + FuckUtil.instance.getArrayListX());
		config.add("ArrayListY:" + FuckUtil.instance.getArrayListY());

		config.add("WaifuX:" + FuckUtil.instance.getWaifuX());
		config.add("WaifuY:" + FuckUtil.instance.getWaifuY());
		
		config.add("KeystrokesX:" + KeyStroke.instance.getXPosition());
		config.add("KeystrokesY:" + KeyStroke.instance.getYPosition());

		try (PrintWriter writer = new PrintWriter(configFile)) {
			for (String line : config) {
				writer.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void applyPositionHud() {
		List<String> config = parseConfigFile();
		Map<String, Action> cfg = new HashMap<>();
		cfg.put("ClickGuiPos:", this::loadClickGuiCoords);
		
		cfg.put("ArrayListX:", hudX -> FuckUtil.instance.setArrayListX(Integer.parseInt(hudX)));
		cfg.put("ArrayListY:", hudY -> FuckUtil.instance.setArrayListY(Integer.parseInt(hudY)));
		
		cfg.put("WaifuX:", waifuX -> FuckUtil.instance.setWaifuX(Integer.parseInt(waifuX)));
		cfg.put("WaifuY:", waifuY -> FuckUtil.instance.setWaifuY(Integer.parseInt(waifuY)));
		
		cfg.put("KeystrokesX:", key -> KeyStroke.instance.setXPosition(Integer.parseInt(key)));
		cfg.put("KeystrokesY:", key -> KeyStroke.instance.setYPosition(Integer.parseInt(key)));
		
		for (String line : config) {
			for (Map.Entry<String, Action> entry : cfg.entrySet()) {
				if (line.startsWith(entry.getKey())) {
					entry.getValue().apply(line.replace(entry.getKey(), ""));
					break;
				}
			}
		}
	}

	private List<String> parseConfigFile() {
		List<String> configFileContents = new ArrayList<>();

		try (Scanner reader = new Scanner(configFile)) {
			while (reader.hasNextLine()) {
				configFileContents.add(reader.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return configFileContents;
	}

	private void loadClickGuiCoords(String decryptedString) {
		if (decryptedString == null || decryptedString.isEmpty()) {
			return;
		}

		for (String what : decryptedString.split("/")) {
			for (CategoryComp cat : Haru.instance.getHaruGui().getCategoryList()) {
				if (cat == null || cat.getCategory() == null) {
					continue;
				}

				if (what.startsWith(cat.getCategory().getName())) {
					try {
						List<String> cfg = MathHelper.StringListToList(what.split("~"));
						if (cfg.size() >= 4) {
							cat.setX(Integer.parseInt(cfg.get(1)));
							cat.setY(Integer.parseInt(cfg.get(2)));
							cat.setOpened(Boolean.parseBoolean(cfg.get(3)));
						}
					} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
					}
				}
			}
		}
	}

	private String getClickGuiPos() {
		StringJoiner posConfig = new StringJoiner("/");

		for (CategoryComp cat : Haru.instance.getHaruGui().getCategoryList()) {
			posConfig.add(String.join("~", cat.getCategory().getName(), String.valueOf(cat.getX()),
					String.valueOf(cat.getY()), String.valueOf(cat.isOpen())));
		}
		return posConfig.toString();
	}

	@FunctionalInterface
	public interface Action {
		void apply(String value);
	}

}