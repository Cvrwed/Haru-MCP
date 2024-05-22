package cc.unknown.utils.keystrokes;

import java.io.IOException;

import cc.unknown.Haru;
import cc.unknown.config.HudConfig;
import cc.unknown.utils.helpers.CPSHelper;
import cc.unknown.utils.helpers.CPSHelper.MouseButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ConfigGui extends GuiScreen {
	private final String[] colors = new String[] { "White", "Red", "Green", "Blue", "Yellow", "Purple",
			"Rainbow" };
	private GuiButton modeBtn;
	private GuiButton textColorBtn;
	private GuiButton showMouseBtn;
	private GuiButton outlineBtn;
	private boolean d = false;
	private int lx;
	private int ly;

	@Override
	public void initGui() {
		KeyStroke st = KeyStrokes.getKeyStroke();
		this.buttonList.add(this.modeBtn = new GuiButton(0, this.width / 2 - 70, this.height / 2 - 28, 140, 20,
				"Mod: " + (KeyStroke.instance.isIsEnabled() ? "Enabled" : "Disabled")));
		this.buttonList.add(this.textColorBtn = new GuiButton(1, this.width / 2 - 70, this.height / 2 - 6, 140, 20,
				"Text color: " + colors[KeyStroke.instance.getColorIndex()]));
		this.buttonList.add(this.showMouseBtn = new GuiButton(2, this.width / 2 - 70, this.height / 2 + 16, 140, 20,
				"Show mouse buttons: " + (KeyStroke.instance.isDisplayMouseButtons() ? "On" : "Off")));
		this.buttonList.add(this.outlineBtn = new GuiButton(3, this.width / 2 - 70, this.height / 2 + 38, 140, 20,
				"Outline: " + (KeyStroke.instance.isDisplayOutline() ? "On" : "Off")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		KeyStrokes.getKeyStrokeRenderer().renderKeystrokes();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		KeyStroke st = KeyStrokes.getKeyStroke();
		if (button == this.modeBtn) {
			KeyStroke.instance.setIsEnabled(!KeyStroke.instance.isIsEnabled());
			this.modeBtn.displayString = "Mod: " + (KeyStroke.instance.isIsEnabled() ? "Enabled" : "Disabled");
		} else if (button == this.textColorBtn) {
			KeyStroke.instance.setColorIndex(KeyStroke.instance.getColorIndex() == 6 ? 0 : KeyStroke.instance.getColorIndex() + 1);
			this.textColorBtn.displayString = "Text color: " + colors[KeyStroke.instance.getColorIndex()];
		} else if (button == this.showMouseBtn) {
			KeyStroke.instance.setDisplayMouseButtons(!KeyStroke.instance.isDisplayMouseButtons());
			this.showMouseBtn.displayString = "Show mouse buttons: " + (KeyStroke.instance.isDisplayMouseButtons() ? "On" : "Off");
		} else if (button == this.outlineBtn) {
			KeyStroke.instance.setDisplayOutline(!KeyStroke.instance.isDisplayOutline());
			this.outlineBtn.displayString = "Outline: " + (KeyStroke.instance.isDisplayOutline() ? "On" : "Off");
		}

	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		try {
			super.mouseClicked(mouseX, mouseY, button);
		} catch (IOException var9) {
		}

		if (button == 0) {
			CPSHelper.getCPS(MouseButton.LEFT);
			KeyStroke st = KeyStrokes.getKeyStroke();
			int startX = KeyStroke.instance.getXPosition();
			int startY = KeyStroke.instance.getYPosition();
			int endX = startX + 74;
			int endY = startY + (KeyStroke.instance.isDisplayMouseButtons() ? 74 : 50);
			if (mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY) {
				this.d = true;
				this.lx = mouseX;
				this.ly = mouseY;
			}
		} else if (button == 1) {
			CPSHelper.getCPS(MouseButton.RIGHT);
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
			KeyStroke st = KeyStrokes.getKeyStroke();
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
		Haru.instance.getHudConfig().saveKeyStrokes();
	}
}