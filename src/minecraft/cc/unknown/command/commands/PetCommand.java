package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.api.Flips;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.enums.EnumChatFormatting;

@Flips(name = "Pet", alias = "vpet", desc = "ur v-pet", syntax = ".pet cat")
public class PetCommand extends Command {

	private boolean toggle = false;
	private int image;
	private int deltaTime;
	private int running;
	private int height = 80;

	private ResourceLocation[] catTextures = new ResourceLocation[] { new ResourceLocation("haru/img/pet/cat/1.png"),
			new ResourceLocation("haru/img/pet/cat/2.png"), new ResourceLocation("haru/img/pet/cat/3.png"),
			new ResourceLocation("haru/img/pet/cat/4.png"), new ResourceLocation("haru/img/pet/cat/5.png"),
			new ResourceLocation("haru/img/pet/cat/6.png"), new ResourceLocation("haru/img/pet/cat/7.png"),
			new ResourceLocation("haru/img/pet/cat/8.png"), new ResourceLocation("haru/img/pet/cat/9.png"),
			new ResourceLocation("haru/img/pet/cat/10.png"), new ResourceLocation("haru/img/pet/cat/11.png"),
			new ResourceLocation("haru/img/pet/cat/12.png") };

	public PetCommand() {
		Haru.instance.getEventBus().register(this);
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 1) {
	        if (args[0].equalsIgnoreCase("cat")) {
	        	toggle = !toggle;
	        } else {
	            this.sendChat(getColor("Red") + " Syntax Error. Use .pet cat");
	        }
		}
	}

	@EventLink
	public void onRender2D(RenderEvent e) {
		if (!toggle || !e.is2D())
			return;
		running += 0.015f * deltaTime;
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		running++;
		RenderUtil.drawImage(catTextures[image], (int) running, scaledResolution.getScaledHeight() - height, 64, 32);
		if (scaledResolution.getScaledWidth() <= running)
			running = -64;
	}

	@EventLink
	public void onUpdate(LivingEvent e) {
		if (!toggle) {
			image = 0;
			return;
		}
		image++;
		if (image >= catTextures.length)
			image = 0;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
