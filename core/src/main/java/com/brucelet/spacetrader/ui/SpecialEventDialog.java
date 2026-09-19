package com.brucelet.spacetrader.ui;

/** The special event offered by the current system (the frontend asks GameState for its text). */
public class SpecialEventDialog extends BaseDialog {
	private SpecialEventDialog() {}

	public static SpecialEventDialog newInstance() { return new SpecialEventDialog(); }
}
