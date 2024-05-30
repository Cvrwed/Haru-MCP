package cc.unknown.module.impl.visuals;

import java.io.IOException;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.keystrokes.KeyStroke;
import cc.unknown.utils.keystrokes.RenderKeys;
import cc.unknown.utils.keystrokes.RenderMouse;
import cc.unknown.utils.keystrokes.gui.ConfigGui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

@Info(name = "KeyStrokes", category = Category.Visuals)
public class KeyStrokes extends Module {
	private final RenderKeys[] keyRenderers = { new RenderKeys(mc.gameSettings.keyBindForward, 26, 2),
			new RenderKeys(mc.gameSettings.keyBindBack, 26, 26), new RenderKeys(mc.gameSettings.keyBindLeft, 2, 26),
			new RenderKeys(mc.gameSettings.keyBindRight, 50, 26) };

	private final RenderMouse[] mouseButtons = { new RenderMouse(0, 2, 50), new RenderMouse(1, 38, 50) };
	private BooleanValue editPosition = new BooleanValue("Edit Position", false);
	private BooleanValue showButtons = new BooleanValue("Mouse Buttons", false);
	private BooleanValue showOutline = new BooleanValue("Outline", false);

	public KeyStrokes() {
		this.registerSetting(editPosition, showButtons, showOutline);
	}
	
	@Override
	public void guiButtonToggled(BooleanValue b) {
		if (b == editPosition) {
			editPosition.disable();
			mc.displayGuiScreen(new ConfigGui());
		}
	}

	@EventLink
	public void onRender(RenderEvent event) {
		if (event.is2D()) {
			if (mc.currentScreen != null) {
				if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof ConfigGui) {
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
		int xPosition = KeyStroke.instance.getXPosition();
		int yPosition = KeyStroke.instance.getYPosition();
		int textColor = getTheme().getMainColor().getRGB();
		ScaledResolution resolution = new ScaledResolution(mc);
		int width = 74;
		int height = showButtons.isToggled() ? 74 : 50;

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
		
		if (showButtons.isToggled()) {
			this.drawMouseButtons(xPosition, yPosition, textColor);
		}
	}

	private void drawMovementKeys(int x, int y, int textColor) {
	    for (RenderKeys keyRenderer : this.keyRenderers) {
	        keyRenderer.renderKey(x, y, textColor, showOutline.isToggled());
	    }
	}

	private void drawMouseButtons(int x, int y, int textColor) {
	    for (RenderMouse mouseButton : this.mouseButtons) {
	        mouseButton.render(x, y, textColor, showOutline.isToggled());
	    }
	}

}
