package cc.unknown.ui.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.visuals.ClickGuiModule;
import cc.unknown.ui.clickgui.impl.CategoryComp;
import cc.unknown.utils.client.FuckUtil;
import cc.unknown.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class HaruGui extends CustomGuiScreen {
	private final ArrayList<CategoryComp> categoryList = new ArrayList<>();
	private final Map<String, ResourceLocation> waifuMap = new HashMap<>();
	
	private boolean isDragging = false;
	private AtomicInteger lastMouseX = new AtomicInteger(0);
	private AtomicInteger lastMouseY = new AtomicInteger(0);

	public HaruGui() {
		int topOffset = 5;
		for (Category category : Category.values()) {
			CategoryComp comp = new CategoryComp(category);
			comp.setY(topOffset);
			categoryList.add(comp);
			topOffset += 20;
		}

		String[] waifuNames = { "uzaki", "megumin", "ai", "mai", "kiwi", "astolfo", "ryo", "hitori", "elma" };
		Arrays.stream(waifuNames)
				.forEach(name -> waifuMap.put(name, new ResourceLocation("haru/img/clickgui/" + name + ".png")));
	}

    @Override
    public void init() {
        super.init();
    }

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		ClickGuiModule cg = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
		ResourceLocation waifuImage = waifuMap.get(cg.waifuMode.getMode().toLowerCase());

		if (cg.backGroundMode.is("Gradient")) {
			this.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), getTheme().getMainColor().getRGB(), getTheme().getBackColor().getTransparency());
		} else if (cg.backGroundMode.is("Normal")) {
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
		}

		categoryList.forEach(c -> {
			c.render(this.fontRendererObj);
			c.updatePosition(mouseX, mouseY);
			c.getModules().forEach(comp -> comp.updateComponent(mouseX, mouseY));
		});

		if (waifuImage != null) {
			RenderUtil.drawImage(waifuImage, FuckUtil.instance.getWaifuX(), FuckUtil.instance.getWaifuY(),
					sr.getScaledWidth() / 5.2f, sr.getScaledHeight() / 2f);
		}

		if (isDragging) {
			FuckUtil.instance.setWaifuX(FuckUtil.instance.getWaifuX() + mouseX - lastMouseX.get());
			FuckUtil.instance.setWaifuY(FuckUtil.instance.getWaifuY() + mouseY - lastMouseY.get());
			lastMouseX.set(mouseX);
			lastMouseY.set(mouseY);
		}
	}

	@Override
	public void click(int mouseX, int mouseY, int mouseButton) {
		ScaledResolution sr = new ScaledResolution(mc);
		
		categoryList.forEach(c -> {
			if (c.isInside(mouseX, mouseY)) {
				switch (mouseButton) {
				case 0:
					c.setDragging(true);
					c.setDragX(mouseX - c.getX());
					c.setDragY(mouseY - c.getY());
					break;
				case 1:
					c.setOpen(!c.isOpen());
					break;
				}
			}
			
			if (isBound(mouseX, mouseY, sr)) {
				switch (mouseButton) {
				case 0:
					isDragging = true;
					lastMouseX.set(mouseX);
					lastMouseY.set(mouseY);
					return;
				}
			}

			if (c.isOpen()) {
				if (!c.getModules().isEmpty()) {
					c.getModules().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
				}
			}
		});
	}

	@Override
	public void release(int mouseX, int mouseY, int state) {
		ScaledResolution sr = new ScaledResolution(mc);

		categoryList.forEach(c -> {
			c.setDragging(false);
			if (c.isOpen()) {
				if (!c.getModules().isEmpty()) {
					c.getModules().forEach(component -> component.mouseReleased(mouseX, mouseY, state));
				}
			}
			
			if (isBound(mouseX, mouseY, sr)) {
				switch (state) {
				case 0:
					isDragging = false;
					return;
				}
			}
		});

		if (Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().savePositionHud();
		}
	}

	@Override
	public void keyTyped(char t, int k) throws IOException {
		if (k == 1 || k == 54) {
			this.mc.displayGuiScreen(null);
		}
		
		categoryList.forEach(c -> {
			if (c.isOpen() && k != 1 && k != 54) {
				if (!c.getModules().isEmpty()) {
					c.getModules().forEach(component -> component.keyTyped(t, k));
				}
			}
		});
		
		super.keyTyped(t, k);
	}

	@Override
	public void onGuiClosed() {
		ClickGuiModule cg = (ClickGuiModule) Haru.instance.getModuleManager().getModule(ClickGuiModule.class);
		if (cg != null && cg.isEnabled() && Haru.instance.getHudConfig() != null) {
			Haru.instance.getHudConfig().savePositionHud();
			cg.disable();
		}
		
		super.onGuiClosed();
	}
	
    @Override
    public void onResize(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
        super.onResize(mcIn, p_175273_2_, p_175273_3_);
    }

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public ArrayList<CategoryComp> getCategoryList() {
		return categoryList;
	}

	private boolean isBound(int x, int y, ScaledResolution sr) {
		return x >= FuckUtil.instance.getWaifuX() && x <= FuckUtil.instance.getWaifuX() + (sr.getScaledWidth() / 5.1f)
				&& y >= FuckUtil.instance.getWaifuY()
				&& y <= FuckUtil.instance.getWaifuY() + (sr.getScaledHeight() / 2f);
	}
}
