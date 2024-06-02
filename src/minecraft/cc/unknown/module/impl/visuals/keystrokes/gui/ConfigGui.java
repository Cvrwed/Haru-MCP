package cc.unknown.module.impl.visuals.keystrokes.gui;

import java.io.IOException;

import cc.unknown.Haru;
import cc.unknown.module.impl.visuals.keystrokes.KeyStroke;
import net.minecraft.client.gui.GuiScreen;

public class ConfigGui extends GuiScreen {

	private boolean d = false;
	private int lx;
	private int ly;

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		try {
			super.mouseClicked(mouseX, mouseY, button);
		} catch (IOException var9) {
		}

		if (button == 0) {
			int startX = KeyStroke.instance.getXPosition();
			int startY = KeyStroke.instance.getYPosition();
			int endX = startX + 74;
			int endY = startY + 74;
			if (mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY) {
				this.d = true;
				this.lx = mouseX;
				this.ly = mouseY;
			}
		}

	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int action) {
		super.mouseReleased(mouseX, mouseY, action);
		this.d = false;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
		super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
		if (this.d) {
			KeyStroke.instance.setXPosition(KeyStroke.instance.getXPosition() + mouseX - this.lx);
			KeyStroke.instance.setYPosition(KeyStroke.instance.getYPosition() + mouseY - this.ly);
			this.lx = mouseX;
			this.ly = mouseY;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		Haru.instance.getHudConfig().savePositionHud();
	}
}