package com.brucelet.spacetrader.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** Minimal android.os.Handler stand-in: delayed runnables are delivered on the UI thread. */
public class Handler {
	private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "spacetrader-timer");
		t.setDaemon(true);
		return t;
	});
	private final List<ScheduledFuture<?>> pending = new ArrayList<>();

	public boolean post(Runnable r) {
		UiThread.post(r);
		return true;
	}

	public synchronized boolean postDelayed(final Runnable r, long delayMillis) {
		pending.removeIf(ScheduledFuture::isDone);
		pending.add(TIMER.schedule(() -> UiThread.post(r), delayMillis, TimeUnit.MILLISECONDS));
		return true;
	}

	public synchronized void removeCallbacksAndMessages(Object token) {
		for (ScheduledFuture<?> f : pending) f.cancel(false);
		pending.clear();
	}
}
