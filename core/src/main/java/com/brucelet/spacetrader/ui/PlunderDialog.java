package com.brucelet.spacetrader.ui;

/** Plunder screen shown after destroying or robbing an opponent (frontend reads the game state). */
public class PlunderDialog extends BaseDialog {
	private PlunderDialog() {}

	public static PlunderDialog newInstance() { return new PlunderDialog(); }
}
