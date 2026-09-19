package com.brucelet.spacetrader.datatypes;

import com.brucelet.spacetrader.enumtypes.*;
import com.brucelet.spacetrader.platform.UiThread;
import com.brucelet.spacetrader.ui.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/** Triggers every special event once (text + accept) and reports exceptions. */
public class EventSweep {
	static final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

	static void pump() throws Exception {
		long idle = System.currentTimeMillis();
		while (System.currentTimeMillis() - idle < 250) {
			Runnable r = queue.poll();
			if (r != null) { r.run(); idle = System.currentTimeMillis(); } else Thread.sleep(2);
		}
	}

	public static void main(String[] args) throws Exception {
		UiThread.setPoster(queue::add);
		GameState g = new GameState();
		g.setUI(new GameUI() {
			public void showDialog(BaseDialog d) {
				if (d instanceof SimpleDialog && ((SimpleDialog) d).listener != null) queue.add(((SimpleDialog) d).listener::onConfirm);
				if (d instanceof ConfirmDialog && ((ConfirmDialog) d).confirm != null) queue.add(((ConfirmDialog) d).confirm::onConfirm);
			}
			public void setScreen(ScreenType s) {}
			public ScreenType currentScreen() { return null; }
			public void autosave() {}
			public void clearBackStack() {}
			public void encounterChanged() {}
			public void stateChanged() {}
			public void encounterAutoModeCleared() {}
			public void showEncounterDescription(String t) {}
			public void animateAttack(boolean x, boolean y, boolean z) {}
			public void animateEnterExit(boolean x, boolean y) {}
			public void moveTribble(int i) {}
		});
		int bad = 0, total = 0;
		for (SpecialEvent ev : SpecialEvent.values()) {
			g.newGame("Sweeper", 5, 5, 5, 5, DifficultyLevel.NORMAL, false);
			g.credits = 5000000;
			SolarSystem here = g.currentSystem();
			here.setSpecial(ev);
			total++;
			try {
				String title = g.specialEventTitle(), msg = g.specialEventMessage();
				g.systemInformationEntered();
				g.specialEventAccept();
				pump();
				g.questLines();
				g.specialCargoLines();
				g.newspaperTitle();
				g.newspaperHeadlines();
				System.out.println("ok   " + ev + " : " + title);
			} catch (Throwable t) {
				bad++;
				System.out.println("FAIL " + ev + " : " + t);
				t.printStackTrace(System.out);
			}
		}
		System.out.println(bad == 0 ? "ALL " + total + " EVENTS OK" : bad + " of " + total + " failed");
	}
}
