package com.brucelet.spacetrader.datatypes;

import com.brucelet.spacetrader.enumtypes.*;
import com.brucelet.spacetrader.platform.UiThread;
import com.brucelet.spacetrader.ui.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/** Headless check of bank / equipment / shipyard logic in a high-tech system. */
public class ShopRun {
	static final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
	static GameState g;
	static String last = "";

	static void pump() throws Exception {
		long idle = System.currentTimeMillis();
		while (System.currentTimeMillis() - idle < 400) {
			Runnable r = queue.poll();
			if (r != null) { r.run(); idle = System.currentTimeMillis(); } else Thread.sleep(5);
		}
	}

	static void check(boolean ok, String what) {
		System.out.println((ok ? "PASS " : "FAIL ") + what);
		if (!ok) failures++;
	}
	static int failures = 0;

	public static void main(String[] a) throws Exception {
		UiThread.setPoster(queue::add);
		g = new GameState();
		g.setUI(new GameUI() {
			public void showDialog(BaseDialog d) {
				last = d.getClass().getSimpleName();
				if (d instanceof SimpleDialog && ((SimpleDialog) d).listener != null) queue.add(((SimpleDialog) d).listener::onConfirm);
				if (d instanceof ConfirmDialog && ((ConfirmDialog) d).confirm != null) queue.add(((ConfirmDialog) d).confirm::onConfirm);
				if (d instanceof InputDialog) {
					InputDialog in = (InputDialog) d;
					queue.add(() -> in.positive.onClickPositiveButton(5000));
				}
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
		g.newGame("Shopper", 5, 5, 5, 5, DifficultyLevel.NORMAL, false);

		// move to the highest-tech system so everything is on sale
		SolarSystem best = null;
		for (SolarSystem s : g.solarSystems()) if (best == null || s.techLevel().compareTo(best.techLevel()) > 0) best = s;
		g.commanderMember().setSystem(best);
		g.determinePrices(best);
		System.out.println("At " + best.name + " (" + best.techLevel() + ")");

		// bank
		g.bankGetLoan(); pump();
		check(g.debt() == 1000 && g.credits() == 2000, "loan capped at the starting maximum of 1000 (debt=" + g.debt() + ", credits=" + g.credits() + ")");
		g.bankPayBack(); pump();
		check(g.debt() == 0 && g.credits() == 1000, "pay back (debt=" + g.debt() + ", credits=" + g.credits() + ")");

		// equipment
		g.credits = 200000;
		int before = g.credits();
		int price = g.equipmentBuyPrice(Weapon.BEAM);
		g.buyEquipment(Weapon.BEAM); pump();
		check(price > 0, "beam laser on sale for " + price);
		check(g.playerShip().weapon[0] == Weapon.PULSE, "gnat has 1 weapon slot, so purchase refused (dialog " + last + ")");
		g.sellEquipment(0, 0); pump();
		check(g.playerShip().weapon[0] == null && g.credits() > before, "sold pulse laser, credits=" + g.credits());
		g.buyEquipment(Weapon.BEAM); pump();
		check(g.playerShip().weapon[0] == Weapon.BEAM, "bought beam laser");
		g.buyEquipment(Gadget.EXTRABAYS); pump();
		check(g.playerShip().hasGadget(Gadget.EXTRABAYS), "bought extra bays (bays=" + g.playerShip().totalCargoBays() + ")");

		// shipyard
		g.enterBuyShip();
		int cash = g.credits();
		ShipType target = ShipType.HORNET;
		check(g.shipPriceOf(target) > 0, "hornet priced " + g.shipPriceOf(target));
		g.buyShipType(target); pump();
		Thread.sleep(500); pump();
		check(g.playerShip().type == target, "now flying " + g.playerShip().type + " (credits " + cash + " -> " + g.credits() + ")");

		g.yardRepair(true); g.yardFuel(true); pump();
		System.out.println(failures == 0 ? "ALL OK" : failures + " FAILURES");
	}
}
