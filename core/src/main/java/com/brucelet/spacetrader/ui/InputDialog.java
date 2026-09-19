package com.brucelet.spacetrader.ui;

/** Numeric entry dialog with up to three buttons; the positive listener receives the entered value. */
public class InputDialog extends BaseDialog {
	public int positiveId = -1;
	public int neutralId = -1;
	public int negativeId = -1;
	public OnPositiveListener positive;
	public OnNeutralListener neutral;
	public OnNegativeListener negative;

	private InputDialog() {}

	public static InputDialog newInstance(int titleId, int messageId, int positiveId, int neutralId, int negativeId, int helpId,
			OnPositiveListener positive, OnNeutralListener neutral, Object... args) {
		return newInstance(titleId, messageId, positiveId, neutralId, negativeId, helpId, positive, neutral, (OnNegativeListener) null, args);
	}

	public static InputDialog newInstance(int titleId, int messageId, int positiveId, int neutralId, int negativeId, int helpId,
			OnPositiveListener positive, OnNeutralListener neutral, OnNegativeListener negative, Object... args) {
		InputDialog d = new InputDialog();
		d.titleId = titleId; d.messageId = messageId; d.positiveId = positiveId; d.neutralId = neutralId; d.negativeId = negativeId;
		d.helpId = helpId; d.positive = positive; d.neutral = neutral; d.negative = negative; d.args = args;
		return d;
	}

	public interface OnPositiveListener { void onClickPositiveButton(int value); }
	public interface OnNeutralListener { void onClickNeutralButton(); }
	public interface OnNegativeListener { void onClickNegativeButton(); }
}
