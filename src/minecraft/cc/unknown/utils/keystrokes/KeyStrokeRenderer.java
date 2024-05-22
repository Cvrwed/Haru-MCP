package cc.unknown.utils.keystrokes;

import java.awt.Color;
import java.io.IOException;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.utils.Loona;
import net.minecraft.client.gui.ScaledResolution;

public class KeyStrokeRenderer implements Loona {
	private final int[] COLORS = new int[] { 16777215, 16711680, 65280, 255, 16776960, 11141290 };
	
    private final KeyRenderer[] keyRenderers = new KeyRenderer[4];
    private final KeyStrokeMouse[] mouseButtons = new KeyStrokeMouse[2];

	public KeyStrokeRenderer() {
        this.keyRenderers[0] = new KeyRenderer(mc.gameSettings.keyBindForward, 26, 2);
        this.keyRenderers[1] = new KeyRenderer(mc.gameSettings.keyBindBack, 26, 26);
        this.keyRenderers[2] = new KeyRenderer(mc.gameSettings.keyBindLeft, 2, 26);
        this.keyRenderers[3] = new KeyRenderer(mc.gameSettings.keyBindRight, 50, 26);

        this.mouseButtons[0] = new KeyStrokeMouse(0, 2, 50);
        this.mouseButtons[1] = new KeyStrokeMouse(1, 38, 50);

        Haru.instance.getEventBus().register(this);
	}

    @EventLink
    public void onRenderTick(RenderEvent event) {
        if (event.is2D()) {
            if (mc.currentScreen != null) {
                if (mc.currentScreen instanceof ConfigGui) {
                    try {
                        mc.currentScreen.handleInput();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (mc.inGameHasFocus && !mc.gameSettings.showDebugInfo) {
                this.renderKeystrokes();
            }
        }
    }

    public void renderKeystrokes() {
        KeyStroke keyStrokeConfig = KeyStrokes.getKeyStroke();
        if (KeyStroke.instance.isIsEnabled()) {
            int xPosition = KeyStroke.instance.getXPosition();
            int yPosition = KeyStroke.instance.getYPosition();
            int textColor = this.getColor(KeyStroke.instance.getColorIndex());
            boolean showMouseButtons = KeyStroke.instance.isDisplayMouseButtons();
            ScaledResolution resolution = new ScaledResolution(mc);
            int width = 74;
            int height = showMouseButtons ? 74 : 50;

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
            if (showMouseButtons) {
                this.drawMouseButtons(xPosition, yPosition, textColor);
            }
        }
    }

    private int getColor(int index) {
        return index == 6
            ? Color.getHSBColor((float) (System.currentTimeMillis() % 3750L) / 3750.0F, 1.0F, 1.0F).getRGB()
            : COLORS[index];
    }

    private void drawMovementKeys(int x, int y, int textColor) {
        for (KeyRenderer keyRenderer : this.keyRenderers) {
            keyRenderer.renderKey(x, y, textColor);
        }
    }

    private void drawMouseButtons(int x, int y, int textColor) {
        for (KeyStrokeMouse mouseButton : this.mouseButtons) {
            mouseButton.render(x, y, textColor);
        }
    }
}