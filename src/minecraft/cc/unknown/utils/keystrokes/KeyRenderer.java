package cc.unknown.utils.keystrokes;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import cc.unknown.utils.Loona;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;

public class KeyRenderer implements Loona {
	private final KeyBinding keyBinding;
	private final int c;
	private final int d;
	private boolean e = true;
	private long f = 0L;

	public KeyRenderer(KeyBinding key, int j, int k) {
		this.keyBinding = key;
		this.c = j;
		this.d = k;
	}

	public void renderKey(int l, int m, int color) {
		boolean keyDown = this.keyBinding.isKeyDown();
		String keyName = Keyboard.getKeyName(this.keyBinding.getKeyCode());
		if (keyDown != this.e) {
			this.e = keyDown;
			this.f = System.currentTimeMillis();
		}

		double hue = 1.0D;
		int green = 255;
		if (keyDown) {
			green = Math.min(255, (int) (2L * (System.currentTimeMillis() - this.f)));
			hue = Math.max(0.0D, 1.0D - (double) (System.currentTimeMillis() - this.f) / 20.0D);
		} else {
			green = Math.max(0, 255 - (int) (2L * (System.currentTimeMillis() - this.f)));
			hue = Math.min(1.0D, (double) (System.currentTimeMillis() - this.f) / 20.0D);
		}

		int q = color >> 16 & 255;
		int red = color >> 8 & 255;
		int s = color & 255;
		int c = (new Color(q, red, s)).getRGB();
		Gui.drawRect(l + this.c, m + this.d, l + this.c + 22, m + this.d + 22, 2013265920 + (green << 16) + (green << 8) + green);
		if (KeyStroke.instance.isDisplayOutline()) {
			Gui.drawRect(l + this.c, m + this.d, l + this.c + 22, m + this.d + 1, c);
			Gui.drawRect(l + this.c, m + this.d + 21, l + this.c + 22, m + this.d + 22, c);
			Gui.drawRect(l + this.c, m + this.d, l + this.c + 1, m + this.d + 22, c);
			Gui.drawRect(l + this.c + 21, m + this.d, l + this.c + 22, m + this.d + 22, c);
		}

		mc.fontRendererObj.drawString(keyName, l + this.c + 8, m + this.d + 8,
				-16777216 + ((int) ((double) q * hue) << 16) + ((int) ((double) red * hue) << 8) + (int) ((double) s * hue));
	}
}