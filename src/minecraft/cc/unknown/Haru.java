package cc.unknown;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;

import cc.unknown.command.CommandManager;
import cc.unknown.config.ConfigManager;
import cc.unknown.config.HudConfig;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.api.EventBus;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.module.ModuleManager;
import cc.unknown.ui.clickgui.HaruGui;
import cc.unknown.ui.clickgui.impl.theme.ThemeManager;
import cc.unknown.utils.Loona;
import cc.unknown.utils.player.rotation.RotationManager;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.ViaMCP;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;

public enum Haru implements Loona {
	instance;

	private CommandManager commandManager;
	private ConfigManager configManager;
	private HudConfig hudConfig;
	private RotationManager rotationManager;
	private ModuleManager moduleManager;
	private ThemeManager themeManager;

	private HaruGui haruGui;
	private EventBus eventBus = new EventBus();

	public void startClient() {
		eventBus.register(this);
		
		commandManager = new CommandManager();
		moduleManager = new ModuleManager();
		rotationManager = new RotationManager();
		haruGui = new HaruGui();
		configManager = new ConfigManager();
		themeManager = new ThemeManager();
		hudConfig = new HudConfig();
		hudConfig.applyPositionHud();
		
		try {
		    ViaMCP.create();
		    ViaMCP.INSTANCE.initAsyncSlider();
		 } catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
    @EventLink
    public void onUpdate(LivingEvent e) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) { // blocking fix
            if (mc.player.isBlocking() && mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword) {
                PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                useItem.write(Type.VAR_INT, 1);
                PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                mc.getNetHandler().sendQueue(new CPacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));
            }
        }
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8) && e.isSend()) { // placement fix
            final Packet<?> packet = e.getPacket();
            if (packet instanceof CPacketPlayerBlockPlacement) {
                ((CPacketPlayerBlockPlacement) packet).facingX = 0.5F;
                ((CPacketPlayerBlockPlacement) packet).facingY = 0.5F;
                ((CPacketPlayerBlockPlacement) packet).facingZ = 0.5F;
            }
        }
    }

	public void stopClient() {
		eventBus.unregister(this);
		hudConfig.savePositionHud();
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public HudConfig getHudConfig() {
		return hudConfig;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public HaruGui getHaruGui() {
		return haruGui;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public ThemeManager getThemeManager() {
		return themeManager;
	}
}