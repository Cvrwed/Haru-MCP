package cc.unknown.utils.misc;

import cc.unknown.utils.Loona;
import cc.unknown.utils.client.FuckUtil;
import net.minecraft.client.gui.ScaledResolution;

public class DragUtil implements Loona {

	private double x, y, width, height;
	private float scale;

	public DragUtil(double x, double y, double width, double height, float scale) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scale = scale;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public boolean isInside(int x, int y) {
		return x >= getX() && y >= getY() && x <= getX() + getWidth() && y <= getY() + getHeight();
	}

	public static double[] setScaledPosition(double x, double y) {
		ScaledResolution sr = new ScaledResolution(mc.getMinecraft());

		double width = sr.getScaledWidth();
		double height = sr.getScaledHeight_double();

		return new double[] { width / 1000F * x, height / 1000F * y };
	}

	public static double[] setPosition(double x, double y) {
		ScaledResolution sr = new ScaledResolution(mc.getMinecraft());

		double width = sr.getScaledWidth();
		double height = sr.getScaledHeight_double();

		return new double[] { x * 1000F / width, y * 1000F / height };
	}
}
