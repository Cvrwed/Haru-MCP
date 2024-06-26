package cc.unknown.ui.clickgui.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.GL11;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.Category;
import cc.unknown.utils.Loona;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.gui.FontRenderer;

public class CategoryComp implements Loona {
	private ArrayList<ModuleComp> modulesInCategory = new ArrayList<>();
	private Category category;
	private boolean categoryOpened = false;
	private int width = 92; // 92
	private int x = 5;
	private int y = 5;
	private final int bh = 13;
	private boolean dragging = false;
	private AtomicInteger tY = new AtomicInteger(bh + 3);
	private int dragX;
	private int dragY;
	private boolean n4m = false;
	private String pvp;
	private boolean pin = false;
	private final double marginX = 80;
	private final double marginY = 4.5;

	public CategoryComp(Category category) {
		this.category = category;
	    AtomicInteger posY = new AtomicInteger(tY.get());
	    Haru.instance.getModuleManager().getCategory(this.category).forEach(mod -> {
	        ModuleComp moduleComp = new ModuleComp(mod, this, posY.getAndAdd(16));
	        this.modulesInCategory.add(moduleComp);
	    });
	}

	public ArrayList<ModuleComp> getModules() {
		return this.modulesInCategory;
	}

	public void setX(int n) {
		this.x = n;
		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().savePositionHud();
		}
	}

	public void setY(int y) {
		this.y = y;
		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().savePositionHud();
		}
	}

	public boolean p() {
		return this.pin;
	}

	public void cv(boolean on) {
		this.pin = on;
	}

	public void setOpened(boolean on) {
		this.categoryOpened = on;
		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().savePositionHud();
		}
	}

	public void render(FontRenderer r) {				
		this.width = 92;
		if (!this.modulesInCategory.isEmpty() && this.categoryOpened) {
			int categoryHeight = 0;

			for (ModuleComp module : this.modulesInCategory) {
				categoryHeight += module.getHeight();
			}

			RenderUtil.drawBorderedRoundedRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + categoryHeight + 4, 20, 2, getTheme().getMainColor().getRGB(), getTheme().getBackColor().getRGB());
		} else if (!this.categoryOpened) {
			RenderUtil.drawBorderedRoundedRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + 4, 20, 2, getTheme().getMainColor().getRGB(), getTheme().getBackColor().getRGB());
		}

		String center = this.n4m ? this.pvp : this.category.getName();
		int gf = (int) r.getStringWidth(this.n4m ? this.pvp : this.category.getName());
		int x = this.x + (this.width - gf) / 2;
		int y = this.y + 4;
		r.drawStringWithShadow(center, (float) x, (float) y, getTheme().getMainColor().getRGB());

		if (!this.n4m) {
	         GL11.glPushMatrix();
	         r.drawStringWithShadow(this.categoryOpened ? "*" : "^", (float)(this.x + marginX), (float)((double)this.y + marginY), Color.white.getRGB());
	         GL11.glPopMatrix();
			if (this.categoryOpened && !this.modulesInCategory.isEmpty()) {
			    this.modulesInCategory.forEach(Component::renderComponent);
			}
		}
	}

	public void refresh() {
		int offset = this.bh + 3;

	    for (Component c : this.modulesInCategory) {
	    	c.setOffset(offset);
	    	offset += c.getHeight();
	    }
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public void updatePosition(int x, int y) {
		if (this.dragging) {
			this.setX(x - this.dragX);
			this.setY(y - this.dragY);
		}
	}

	public boolean i(int x, int y) {
		return x >= this.x + 92 - 13 && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F
				&& y <= this.y + this.bh + 1;
	}

	public boolean mousePressed(int x, int y) {
		return x >= this.x + 77 && x <= this.x + this.width - 6 && (float) y >= (float) this.y + 2.0F
				&& y <= this.y + this.bh + 1;
	}

	public boolean isInside(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.bh;
	}

	public String getName() {
		return String.valueOf(modulesInCategory);
	}

	public Category getCategory() {
		return category;
	}

	public void setDragX(int dragX) {
		this.dragX = dragX;
	}
	
	public void setDragY(int dragY) {
		this.dragY = dragY;
	}

	public ArrayList<ModuleComp> getModulesInCategory() {
		return modulesInCategory;
	}

	public boolean isN4m() {
		return n4m;
	}
	
	public String getPvp() {
		return pvp;
	}

	public AtomicInteger gettY() {
		return tY;
	}

	public boolean isPin() {
		return pin;
	}

	public int getBh() {
		return bh;
	}

	public double getMarginX() {
		return marginX;
	}

	public double getMarginY() {
		return marginY;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public boolean isOpen() {
		return categoryOpened;
	}

	public void setOpen(boolean open) {
		this.categoryOpened = open;
	}

}
