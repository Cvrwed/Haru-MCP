package cc.unknown.command.commands;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.ui.clickgui.impl.api.Theme;
import cc.unknown.utils.keystrokes.KeyStroke;
import cc.unknown.utils.keystrokes.gui.ConfigGui;
import cc.unknown.utils.keystrokes.render.RenderKeys;
import cc.unknown.utils.keystrokes.render.RenderMouse;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

@Flips(name = "Key", alias = "key", desc = "Show the classic keystrokes", syntax = ".key")
public class KeyStrokesCommand extends Command {

	private final RenderKeys[] keyRenderers = { new RenderKeys(mc.gameSettings.keyBindForward, 26, 2),
			new RenderKeys(mc.gameSettings.keyBindBack, 26, 26), new RenderKeys(mc.gameSettings.keyBindLeft, 2, 26),
			new RenderKeys(mc.gameSettings.keyBindRight, 50, 26) };

	private final RenderMouse[] mouseButtons = { new RenderMouse(0, 2, 50), new RenderMouse(1, 38, 50) };

	private final AtomicBoolean toggle = new AtomicBoolean(false);
	private final AtomicBoolean showOutline = new AtomicBoolean(false);
	private final AtomicBoolean showButtons = new AtomicBoolean(false);
	private final AtomicBoolean showGui = new AtomicBoolean(false);

	public KeyStrokesCommand() {
		Haru.instance.getEventBus().register(this);
	}
	
	// Recordatorio: Falta mostrar todos los sub comandos [.help key]

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			toggle.set(!toggle.get());
		} else if (args.length == 1) {
			String command = args[0].toLowerCase();
			if (command.equals("edit")) {
				showGui.set(!showGui.get());
			} else if (command.equals("buttons")) {
				showButtons.set(!showButtons.get()); // true
			} else if (command.equals("outline")) {
				showOutline.set(!showOutline.get());
			}
		}
	}

	@EventLink
	public void onTick(TickEvent.Input e) {
		if (showGui.get()) {
			showOutline.set(showOutline.get());
			mc.displayGuiScreen(new ConfigGui());
		}
	}

	@EventLink
	public void onRender(RenderEvent event) {
		if (event.is2D()) {
			if (mc.currentScreen != null) {
				if (mc.currentScreen instanceof ConfigGui || mc.currentScreen instanceof GuiChat) {
					try {
						mc.currentScreen.handleInput();
					} catch (IOException e) {
						e.printStackTrace();
					}
					this.showKeystrokes();
				}
			} else if (mc.inGameHasFocus && !mc.gameSettings.showDebugInfo) {
				this.showKeystrokes();
			}
		}
	}

	public void showKeystrokes() {
		if (!toggle.get()) return;
		int xPosition = KeyStroke.instance.getXPosition();
		int yPosition = KeyStroke.instance.getYPosition();
		int textColor = Theme.instance.getMainColor().getRGB();
		ScaledResolution resolution = new ScaledResolution(mc);
		int width = 74;
		int height = showButtons.get() ? 74 : 50;

		if (xPosition < 0) {
			KeyStroke.instance.setXPosition(0);
			xPosition = KeyStroke.instance.getXPosition();
		} else if (xPosition > resolution.getScaledWidth() - width) {
			KeyStroke.instance.setXPosition(resolution.getScaledWidth() - width);
			xPosition = KeyStroke.instance.getXPosition();
		}

		if (yPosition < 0) {
			KeyStroke.instance.setYPosition(0);
			yPosition = KeyStroke.instance.getYPosition();
		} else if (yPosition > resolution.getScaledHeight() - height) {
			KeyStroke.instance.setYPosition(resolution.getScaledHeight() - height);
			yPosition = KeyStroke.instance.getYPosition();
		}

		this.drawMovementKeys(xPosition, yPosition, textColor);
		
		if (showButtons.get()) {
			showOutline.set(showOutline.get()); // false
			this.drawMouseButtons(xPosition, yPosition, textColor);
		}
	}

	private void drawMovementKeys(int x, int y, int textColor) {
		for (RenderKeys keyRenderer : this.keyRenderers) {
			keyRenderer.renderKey(x, y, textColor, showOutline.get());
		}
	}

	private void drawMouseButtons(int x, int y, int textColor) {
		for (RenderMouse mouseButton : this.mouseButtons) {
			mouseButton.render(x, y, textColor, showOutline.get());
		}
	}
}
