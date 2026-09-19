package com.brucelet.spacetrader.platform;

import java.util.function.Consumer;

/** Where UI-thread work runs. The frontend installs a poster (for libGDX, Gdx.app::postRunnable). */
public final class UiThread {
	private static volatile Consumer<Runnable> poster = Runnable::run;

	private UiThread() {}

	public static void setPoster(Consumer<Runnable> p) { poster = p; }

	public static void post(Runnable r) { poster.accept(r); }
}
