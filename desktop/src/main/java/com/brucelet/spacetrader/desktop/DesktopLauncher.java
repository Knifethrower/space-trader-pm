package com.brucelet.spacetrader.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.brucelet.spacetrader.front.SpaceTraderApp;

/** Usage: DesktopLauncher [width height] [fullscreen] */
public class DesktopLauncher {
	public static void main(String[] args) {
		int w = args.length > 1 ? Integer.parseInt(args[0]) : 640;
		int h = args.length > 1 ? Integer.parseInt(args[1]) : 480;
		boolean fullscreen = args.length > 2 && args[2].equals("fullscreen");
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle("Space Trader");
		if (fullscreen) cfg.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		else cfg.setWindowedMode(w, h);
		// the game has no sound, which skips OpenAL start-up entirely; st.testmusic is a dev-only
		// switch (see IbxmMusicPlayer) to try tracker-format music through libGDX/OpenAL
		cfg.disableAudio(System.getProperty("st.testmusic") == null);
		cfg.setForegroundFPS(60);
		cfg.useVsync(true);
		new Lwjgl3Application(new SpaceTraderApp(), cfg);
	}
}
