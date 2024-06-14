package cc.unknown.ui.clickgui.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.impl.Module;
import cc.unknown.module.setting.Setting;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DescValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

public class ModuleComp extends Component {
	public Module mod;
	public CategoryComp category;
	public int o;
	private final ArrayList<Component> settings;
	public boolean open;

	public ModuleComp(Module mod, CategoryComp p, int o) {
		this.mod = mod;
		this.category = p;
		this.o = o;
		this.settings = new ArrayList<>();
		this.open = false;

		AtomicInteger y = new AtomicInteger(o + 12);

		mod.getSettings().forEach(setting -> {
			addComp(setting, y.getAndAdd(getOffset(setting)));
		});

		this.settings.add(new BindComp(this, y));
	}

	@Override
	public void setOffset(int n) {
		this.o = n;
		int y = this.o + 16;

		for (Component c : this.settings) {
			c.setOffset(y);
			if (c instanceof SliderComp) {
				y += 16;
			} else if (c instanceof BooleanComp || c instanceof DescComp || c instanceof ModeComp
					|| c instanceof BindComp) {
				y += 12;
			}
		}
	}

	public void render(float x, float y, float x1, float y1) {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
		GL11.glShadeModel(7425);
		GL11.glBegin(7);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glVertex2f(x, y1);
		GL11.glVertex2f(x1, y1);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glVertex2f(x1, y);
		GL11.glVertex2f(x, y);
		GL11.glEnd();
		GL11.glShadeModel(7424);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
		GL11.glEdgeFlag(true);
	}

	@Override
	public void renderComponent() {
		render(this.category.getX(), (this.category.getY() + this.o),
				(this.category.getX() + this.category.getWidth()), (this.category.getY() + 15 + this.o));
		
		GL11.glPushMatrix();
		int button;
		if (this.mod.isEnabled()) {
			button = getTheme().getMainColor().getRGB();
		} else if (this.mod.canBeEnabled()) {
			button = Color.lightGray.getRGB();
		} else {
			button = new Color(102, 102, 102).getRGB();
		}
		
		mc.fontRendererObj.drawStringWithShadow(this.mod.getRegister().name(),
				(float) (this.category.getX() + this.category.getWidth() / 2
						- mc.fontRendererObj.getStringWidth(this.mod.getRegister().name()) / 2),
				(float) (this.category.getY() + this.o + 4), button);
		GL11.glPopMatrix();
		if (this.open && !this.settings.isEmpty()) {
			this.settings.forEach(Component::renderComponent);
		}
	}

	@Override
	public int getHeight() {
		if (!this.open) {
			return 16;
		} else {
			int h = 16;

			for (Component c : this.settings) {
				if (c instanceof SliderComp) {
					h += 16;
				} else if (c instanceof BooleanComp || c instanceof DescComp || c instanceof ModeComp
						|| c instanceof BindComp) {
					h += 12;
				}
			}
			return h;
		}
	}

	@Override
	public void updateComponent(int mousePosX, int mousePosY) {
		if (!this.settings.isEmpty()) {
			this.settings.forEach(comp -> comp.updateComponent(mousePosX, mousePosY));
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (mod.canBeEnabled()) {
			if (isMouseOnButton(x, y)) {
				switch (b) {
				case 0:
					this.mod.toggle();
					break;
				case 1:
					this.open = !this.open;
					this.category.refresh();
					break;
				}
			}
		}

		this.settings.forEach(comp -> comp.mouseClicked(x, y, b));
	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		this.settings.forEach(comp -> comp.mouseReleased(x, y, m));
	}

	@Override
	public void keyTyped(char t, int k) {
		this.settings.forEach(comp -> comp.keyTyped(t, k));
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.category.getX() && x < this.category.getX() + this.category.getWidth()
				&& y > this.category.getY() + this.o && y < this.category.getY() + 16 + this.o;
	}

	private void addComp(Setting setting, int y) {
		if (setting instanceof SliderValue) {
			this.settings.add(new SliderComp((SliderValue) setting, this, y));
		} else if (setting instanceof BooleanValue) {
			this.settings.add(new BooleanComp(mod, (BooleanValue) setting, this, y));
		} else if (setting instanceof DescValue) {
			this.settings.add(new DescComp((DescValue) setting, this, y));
		} else if (setting instanceof ModeValue) {
			this.settings.add(new ModeComp((ModeValue) setting, this, y));
		}
	}

	private int getOffset(Setting setting) {
		return (setting instanceof SliderValue) ? 16 : 12;
	}
}
