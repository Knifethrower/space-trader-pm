package com.brucelet.spacetrader.datatypes;

import com.brucelet.spacetrader.enumtypes.DifficultyLevel;
import com.brucelet.spacetrader.enumtypes.EncounterButton;
import com.brucelet.spacetrader.enumtypes.ScreenType;
import com.brucelet.spacetrader.platform.Resources;
import com.brucelet.spacetrader.platform.UiThread;
import com.brucelet.spacetrader.ui.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/** Headless smoke test: starts a game and warps repeatedly, answering every dialog automatically. */
public class BotRun {
	static final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
	static volatile ScreenType screen = null;
	static volatile boolean encounterPending = false;
	static int dialogs = 0, encounters = 0;
	static GameState g;

	public static void main(String[] args) throws Exception {
		int trips = args.length > 0 ? Integer.parseInt(args[0]) : 50;
		UiThread.setPoster(queue::add);
		g = new GameState();
		g.setUI(new GameUI() {
			public void showDialog(BaseDialog d) {
				dialogs++;
				if (d instanceof SimpleDialog) {
					OnConfirmListener l = ((SimpleDialog) d).listener;
					if (l != null) queue.add(l::onConfirm);
				} else if (d instanceof ConfirmDialog) {
					OnConfirmListener l = ((ConfirmDialog) d).confirm;
					if (l != null) queue.add(l::onConfirm);
				} else if (d instanceof InputDialog) {
					InputDialog in = (InputDialog) d;
					if (in.neutral != null) queue.add(in.neutral::onClickNeutralButton);
					else if (in.positive != null) queue.add(() -> in.positive.onClickPositiveButton(0));
				} else if (d instanceof JettisonDialog) {
					OnConfirmListener l = ((JettisonDialog) d).listener;
					if (l != null) queue.add(l::onConfirm);
				} else {
					System.out.println("  (unhandled dialog " + d.getClass().getSimpleName() + ")");
				}
			}
			public void setScreen(ScreenType s) {
				screen = s;
				if (s == ScreenType.ENCOUNTER) encounterPending = true;
			}
			public ScreenType currentScreen() { return screen; }
			public void autosave() {}
			public void clearBackStack() {}
			public void encounterChanged() { if (screen == ScreenType.ENCOUNTER) encounterPending = true; }
			public void stateChanged() {}
			public void encounterAutoModeCleared() {}
			public void showEncounterDescription(String text) {}
			public void animateAttack(boolean a, boolean b, boolean c) {}
			public void animateEnterExit(boolean a, boolean b) {}
			public void moveTribble(int id) {}
		});
		g.newGame("Tester", 5, 5, 5, 5, DifficultyLevel.NORMAL, false);
		System.out.println("New game: " + g.commanderMember().name + " at " + g.currentSystem().name + ", credits=" + g.credits);

		for (int trip = 1; trip <= trips; trip++) {
			if (g.ship == null || screen == ScreenType.ENDGAME) {
				System.out.println("Game ended on trip " + trip + " (" + g.endStatus() + ")");
				break;
			}
			SolarSystem target = null;
			for (SolarSystem s : g.solarSystems()) {
				if (s != g.currentSystem() && GameState.realDistance(g.currentSystem(), s) <= g.ship.getFuel()) { target = s; break; }
			}
			if (target == null) { System.out.println("No reachable system; refuel"); g.buyFuel(1000); g.debt = g.debt; continue; }
			boolean ok = g.warpTo(target, false);
			pump(ok);
			System.out.println("trip " + trip + " -> " + g.currentSystem().name + " days=" + g.days + " credits=" + g.credits + " hull=" + g.ship.hull + " screen=" + screen);
			java.nio.file.Path tmp = java.nio.file.Paths.get("build/roundtrip.properties");
			g.save(new com.brucelet.spacetrader.platform.SharedPreferences(tmp));
			GameState h = new GameState();
			h.load(new com.brucelet.spacetrader.platform.SharedPreferences(tmp));
			if (!h.hasActiveGame()) System.out.println("  ROUNDTRIP FAIL trip " + trip + ": ship=" + (h.ship != null) + " end=" + h.endStatus() + " orig ship=" + (g.ship != null) + " end=" + g.endStatus() + " screen=" + screen);
		}
		System.out.println("dialogs=" + dialogs + " encounters=" + encounters);
	}

	/** Prefer peaceful options; fall back to whatever button 1 is. */
	static EncounterButton pick() {
		EncounterButton[] prefer = { EncounterButton.IGNORE, EncounterButton.INTERRUPT, EncounterButton.SUBMIT, EncounterButton.FLEE, EncounterButton.SURRENDER, EncounterButton.YIELD };
		for (EncounterButton want : prefer)
			for (int n = 1; n <= 4; n++) {
				EncounterButton b = g.encounterButtonFor(n);
				if (b == want) return b;
			}
		return g.encounterButtonFor(1);
	}

	/** Run queued UI work until the trip resolves (arrived at an info-type screen) or we time out. */
	static void pump(boolean started) throws Exception {
		long deadline = System.currentTimeMillis() + 15000;
		long idleSince = System.currentTimeMillis();
		while (System.currentTimeMillis() < deadline) {
			Runnable r = queue.poll();
			if (r != null) { r.run(); idleSince = System.currentTimeMillis(); continue; }
			if (encounterPending && screen == ScreenType.ENCOUNTER) {
				encounterPending = false;
				encounters++;
				g.encounterButton(pick());
				idleSince = System.currentTimeMillis();
				continue;
			}
			if (System.currentTimeMillis() - idleSince > 1500) return;
			Thread.sleep(5);
		}
		System.out.println("  (timed out; screen=" + screen + ")");
	}
}
