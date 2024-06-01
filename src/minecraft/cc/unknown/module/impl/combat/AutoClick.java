package cc.unknown.module.impl.combat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.MotionEvent;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import net.minecraft.client.gui.GuiScreen;

@Info(name = "AutoClick", category = Category.Combat)
public class AutoClick extends Module {

	private ModeValue clickMode = new ModeValue("Click Mode", "Left", "Left", "Right", "Both");

	private final DoubleSliderValue leftCPS = new DoubleSliderValue("Left Click Speed", 16, 19, 1, 80, 0.05);
	private final BooleanValue weaponOnly = new BooleanValue("Only Use Weapons", false);
	private final BooleanValue breakBlocks = new BooleanValue("Break Blocks", false);
	private BooleanValue invClicker = new BooleanValue("Auto-Click in Inventory", false);
	private ModeValue invMode = new ModeValue("Inventory Click Mode", "Pre", "Pre", "Post");
	private SliderValue invDelay = new SliderValue("Click Tick Delay", 5, 0, 10, 1);

	private final DoubleSliderValue rightCPS = new DoubleSliderValue("Right Click Speed", 12, 16, 1, 80, 0.05);
	private final BooleanValue onlyBlocks = new BooleanValue("Only Use Blocks", false);
	private final BooleanValue allowEat = new BooleanValue("Allow Eating & Drinking", true);
	private final BooleanValue allowBow = new BooleanValue("Allow Using Bow", true);

	private ModeValue clickEvent = new ModeValue("Click Event", "Render", "Render", "Render 2", "Tick");
	private ModeValue clickStyle = new ModeValue("Click Style", "Raven", "Raven", "Kuru", "Megumi");

	public AutoClick() {
		this.registerSetting(clickMode, leftCPS, weaponOnly, breakBlocks, invClicker,
				invMode, invDelay, rightCPS, onlyBlocks, allowEat, allowBow, clickEvent,
				clickStyle);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		AtomicReference<String> suffixRef = new AtomicReference<>();

		if (clickMode.is("Left")) {
			suffixRef.set("- [" + leftCPS.getInputMinToInt() + ", " + leftCPS.getInputMaxToInt() + "]");
		} else if (clickMode.is("Right")) {
			suffixRef.set("- [" + rightCPS.getInputMinToInt() + ", " + rightCPS.getInputMaxToInt() + "]");
		}

		this.setSuffix(suffixRef.get());
	}

	@Override
	public void onEnable() {
		ClickUtil.instance.setRand(new Random());
	}

	@Override
	public void onDisable() {
		ClickUtil.instance.setLeftDownTime(0L);
		ClickUtil.instance.setLeftUpTime(0L);
	}

	@EventLink
	public void onMotion(MotionEvent e) {
		if (invClicker.isToggled()) {
			switch (invMode.getMode()) {
			case "Pre":
				if (e.isPre()) {
					ClickUtil.instance.shouldInvClick();
				}
				break;
			case "Post":
				if (e.isPost()) {
					ClickUtil.instance.shouldInvClick();
				}
				break;
			}
		}
	}

	@EventLink
	public void onRender(RenderEvent e) {
		if (clickEvent.is("Render 2") && e.is2D()) {
			onClick();
		}
		
		if (clickEvent.is("Render") && e.is3D()) {
			onClick();
		}
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (clickEvent.is("Tick")) {
			onClick();
		}
	}

	private void onClick() {
		if (clickMode.is("Both")) {
			switch (clickStyle.getMode()) {
			case "Raven":
				ClickUtil.instance.ravenLeftClick();
				ClickUtil.instance.ravenRightClick();
				break;
			case "Kuru":
				ClickUtil.instance.kuruLeftClick();
				ClickUtil.instance.kuruRightClick();
				break;
			case "Megumi":
				ClickUtil.instance.megumiLeftClick();
				ClickUtil.instance.megumiRightClick();
				break;
			}
		} else if (clickMode.is("Left")) {
			switch (clickStyle.getMode()) {
			case "Raven":
				ClickUtil.instance.ravenLeftClick();
				break;
			case "Kuru":
				ClickUtil.instance.kuruLeftClick();
				break;
			case "Megumi":
				ClickUtil.instance.megumiLeftClick();
				break;
			}
		} else if (clickMode.is("Right")) {
			switch (clickStyle.getMode()) {
			case "Raven":
				ClickUtil.instance.ravenRightClick();
				break;
			case "Kuru":
				ClickUtil.instance.kuruRightClick();
				break;
			case "Megumi":
				ClickUtil.instance.megumiRightClick();
				break;
			}
		}
	}

	public ModeValue getClickMode() {
		return clickMode;
	}

	public ModeValue getClickStyle() {
		return clickStyle;
	}

	public DoubleSliderValue getLeftCPS() {
		return leftCPS;
	}

	public BooleanValue getWeaponOnly() {
		return weaponOnly;
	}

	public BooleanValue getBreakBlocks() {
		return breakBlocks;
	}

	public SliderValue getInvDelay() {
		return invDelay;
	}

	public DoubleSliderValue getRightCPS() {
		return rightCPS;
	}

	public BooleanValue getOnlyBlocks() {
		return onlyBlocks;
	}

	public BooleanValue getAllowEat() {
		return allowEat;
	}

	public BooleanValue getAllowBow() {
		return allowBow;
	}
}
