package com.brucelet.spacetrader.ui;

/** The high score table (the frontend asks GameState for the lines to show). */
public class HighScoresDialog extends BaseDialog {
	private HighScoresDialog() {}

	public static HighScoresDialog newInstance() { return new HighScoresDialog(); }
}
