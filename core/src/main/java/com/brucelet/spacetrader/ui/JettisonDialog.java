package com.brucelet.spacetrader.ui;

/** Pick cargo to jettison; the listener runs once the player has finished. */
public class JettisonDialog extends BaseDialog {
	public OnConfirmListener listener;

	private JettisonDialog() {}

	public static JettisonDialog newInstance(OnConfirmListener listener) {
		JettisonDialog d = new JettisonDialog();
		d.listener = listener;
		return d;
	}
}
