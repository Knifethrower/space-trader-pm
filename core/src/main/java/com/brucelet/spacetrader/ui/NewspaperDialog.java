package com.brucelet.spacetrader.ui;

/** The local newspaper (the frontend asks GameState for the masthead and headlines). */
public class NewspaperDialog extends BaseDialog {
	private NewspaperDialog() {}

	public static NewspaperDialog newInstance() { return new NewspaperDialog(); }
}
