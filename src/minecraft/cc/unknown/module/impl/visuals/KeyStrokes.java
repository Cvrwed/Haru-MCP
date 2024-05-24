package cc.unknown.module.impl.visuals;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.impl.api.Theme;
import cc.unknown.utils.keystrokes.render.RenderKeys;
import cc.unknown.utils.keystrokes.render.RenderMouse;
import cc.unknown.utils.misc.DragUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

@Register(name = "KeyStrokes", category = Category.Visuals)
public class KeyStrokes extends Module {
	private final RenderKeys[] keyRenderers = { new RenderKeys(mc.gameSettings.keyBindForward, 26, 2),
			new RenderKeys(mc.gameSettings.keyBindBack, 26, 26), new RenderKeys(mc.gameSettings.keyBindLeft, 2, 26),
			new RenderKeys(mc.gameSettings.keyBindRight, 50, 26) };

	private final RenderMouse[] mouseButtons = { new RenderMouse(0, 2, 50), new RenderMouse(1, 38, 50) };
	
	private final SliderValue posX = new SliderValue("Position X", 100, -1000, 1000, 0.1);
	private final SliderValue posY = new SliderValue("Position Y", 0, -1000, 1000, 0.1);
	private BooleanValue showButtons = new BooleanValue("Mouse Buttons", false);
	private BooleanValue showOutline = new BooleanValue("Outline", false);

	public KeyStrokes() {
		this.registerSetting(posX, posY, showButtons, showOutline);
	}

	@EventLink
	public void onRender(RenderEvent event) {
		if (event.is2D()) {
			if (mc.currentScreen != null) {
				if (mc.currentScreen instanceof GuiChat) {
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
	    double[] pos = DragUtil.setScaledPosition(posX.getInput(), posY.getInput());
	    
	    int textColor = Theme.instance.getMainColor().getRGB();
	    ScaledResolution resolution = new ScaledResolution(mc);
	    int width = 74;
	    int height = showButtons.isToggled() ? 74 : 50;

	    if (pos[0] < 0) {
	        pos[0] = 0;
	    } else if (pos[0] > resolution.getScaledWidth() - width) {
	        pos[0] = resolution.getScaledWidth() - width;
	    }

	    if (pos[1] < 0) {
	        pos[1] = 0;
	    } else if (pos[1] > resolution.getScaledHeight() - height) {
	        pos[1] = resolution.getScaledHeight() - height;
	    }

	    this.drawMovementKeys((int)pos[0], (int)pos[1], textColor);
	    
	    if (showButtons.isToggled()) {
	        this.drawMouseButtons((int)pos[0], (int)pos[1], textColor);
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

	@Override
	public DragUtil getPosition() {
	    double[] pos = DragUtil.setScaledPosition(posX.getInput(), posY.getInput());
	    return new DragUtil(pos[0], pos[1], 60, 60, 1);
	}

	@Override
	public void setXYPosition(double x, double y) {
	    this.posX.setValue(x);
	    this.posY.setValue(y);
	}
}
