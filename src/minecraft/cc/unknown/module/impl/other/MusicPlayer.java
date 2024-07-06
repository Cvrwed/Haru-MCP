package cc.unknown.module.impl.other;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Info;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.music.RadioPlayer;

@Info(name = "MusicPlayer", category = Category.Other)
public class MusicPlayer extends Module {

	public static RadioPlayer radioPlayer = new RadioPlayer();
	private static final Map<String, String> Urls = new HashMap<>();

	private ModeValue mode = new ModeValue("Mode", "The Sun", "Casual", "Dance", "Chill Hop", "Greatest Hits",
			"Hard Style", "Hip Hop", "Mashup", "The Club", "Rap", "Bass", "Party Hard", "The Sun");
	
	public MusicPlayer() {
		this.registerSetting(mode);
	}

	@Override
	public void onEnable() {
		playMusic();
	}

	@Override
	public void onDisable() {
		new Thread(() -> {
			if (radioPlayer != null) {
				radioPlayer.stop();
				radioPlayer.setCurrent("");
			}
		}).start();
	}
	
	private void playMusic() {
		new Thread(() -> {
			String selectedStation = mode.getMode();
			String stationUrl = Urls.get(selectedStation);
	
			if (stationUrl != null) {
				radioPlayer.stop();
				try {
					radioPlayer.start(stationUrl);
				} catch (Exception e) { }
			}
		}).start();
	}

	@EventLink
	public void onShutdown(GameEvent.ShutdownEvent e) {
		this.disable();
	}

	@EventLink
	public void onStartGame(GameEvent.StartEvent e) {
		this.disable();
	}

	static {
		Urls.put("Casual", "https://streams.ilovemusic.de/iloveradio1.mp3");
		Urls.put("Dance", "https://streams.ilovemusic.de/iloveradio36.mp3");
		Urls.put("Chill Hop", "https://streams.ilovemusic.de/iloveradio17.mp3");
		Urls.put("Greatest Hits", "https://streams.ilovemusic.de/iloveradio16.mp3");
		Urls.put("Hard Style", "https://streams.ilovemusic.de/iloveradio21.mp3");
		Urls.put("Hip Hop", "https://streams.ilovemusic.de/iloveradio35.mp3");
		Urls.put("Mashup", "https://streams.ilovemusic.de/iloveradio5.mp3");
		Urls.put("The Club", "https://streams.ilovemusic.de/iloveradio20.mp3");
		Urls.put("Rap", "https://streams.ilovemusic.de/iloveradio13.mp3");
		Urls.put("Bass", "https://streams.ilovemusic.de/iloveradio29.mp3");
		Urls.put("Party Hard", "https://streams.ilovemusic.de/iloveradio14.mp3");
		Urls.put("The Sun", "https://streams.ilovemusic.de/iloveradio15.mp3");
	}
}