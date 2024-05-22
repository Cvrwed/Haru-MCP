package cc.unknown.utils.keystrokes.render;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import cc.unknown.ui.clickgui.impl.api.Theme;
import cc.unknown.utils.Loona;
import cc.unknown.utils.keystrokes.KeyStroke;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;

public class RenderKeys implements Loona {
	private final KeyBinding keyBinding;
	private final int c;
	private final int d;
	private boolean e = true;
	private long f = 0L;

	public RenderKeys(KeyBinding key, int j, int k) {
		this.keyBinding = key;
		this.c = j;
		this.d = k;
	}

	public void renderKey(int l, int m, int color, boolean outline) {
		boolean keyDown = this.keyBinding.isKeyDown();
		String keyName = Keyboard.getKeyName(this.keyBinding.getKeyCode());
		if (keyDown != this.e) {
			this.e = keyDown;
			this.f = System.currentTimeMillis();
		}

		double brightness = 1.0D;
		int alpha = 255;
		if (keyDown) {
			alpha = Math.min(255, (int) (2L * (System.currentTimeMillis() - this.f)));
			brightness = Math.max(0.0D, 1.0D - (double) (System.currentTimeMillis() - this.f) / 20.0D);
		} else {
			alpha = Math.max(0, 255 - (int) (2L * (System.currentTimeMillis() - this.f)));
			brightness = Math.min(1.0D, (double) (System.currentTimeMillis() - this.f) / 20.0D);
		}

		int q = color >> 16 & 255;
		int red = color >> 8 & 255;
		int s = color & 255;
		int c = (new Color(q, red, s)).getRGB();
		Gui.drawRect(l + this.c, m + this.d, l + this.c + 22, m + this.d + 22, 2013265920 + (alpha << 16) + (alpha << 8) + alpha);
		if (outline) {
			Gui.drawRect(l + this.c, m + this.d, l + this.c + 22, m + this.d + 1, c);
			Gui.drawRect(l + this.c, m + this.d + 21, l + this.c + 22, m + this.d + 22, c);
			Gui.drawRect(l + this.c, m + this.d, l + this.c + 1, m + this.d + 22, c);
			Gui.drawRect(l + this.c + 21, m + this.d, l + this.c + 22, m + this.d + 22, c);
		}

		mc.fontRendererObj.drawString(keyName, l + this.c + 8, m + this.d + 8,
				-16777216 + ((int) ((double) q * brightness) << 16) + ((int) ((double) red * brightness) << 8) + (int) ((double) s * brightness));
	}
}
