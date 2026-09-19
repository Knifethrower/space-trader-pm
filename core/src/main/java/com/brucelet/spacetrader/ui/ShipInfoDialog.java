package com.brucelet.spacetrader.ui;

/** Specification sheet for the ship type selected in the shipyard (frontend reads GameState.selectedShipType). */
public class ShipInfoDialog extends BaseDialog {
	private ShipInfoDialog() {}

	public static ShipInfoDialog newInstance() { return new ShipInfoDialog(); }
}
