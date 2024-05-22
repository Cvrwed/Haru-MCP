package cc.unknown.utils.keystrokes;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.utils.Loona;

public class KeyStrokes implements Loona {
    private static boolean isConfigGui = false;
    private static KeyStroke keyStroke;
    private static KeyStrokeRenderer keyStrokeRenderer = new KeyStrokeRenderer();
    
    public KeyStrokes() {
    	Haru.instance.getEventBus().register(this);
    }
    
    public static KeyStrokeRenderer getKeyStrokeRenderer() {
        return keyStrokeRenderer;
    }

    public static void isConfigGui() {
    	isConfigGui = true;
    }

    @EventLink
    public void onTick(TickEvent.Input e){
        if (isConfigGui) {
        	isConfigGui = false;
            mc.displayGuiScreen(new ConfigGui());
        }
    }

	public static KeyStroke getKeyStroke() {
		return keyStroke;
	}
}
