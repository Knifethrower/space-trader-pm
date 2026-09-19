package com.brucelet.spacetrader.ui;

/** Message with a single OK button; the optional listener runs after it is dismissed. */
public class SimpleDialog extends BaseDialog {
	public OnConfirmListener listener;

	private SimpleDialog() {}

	public static SimpleDialog newInstance(int titleId, int messageId, int helpId, OnConfirmListener listener, Object... args) {
		SimpleDialog d = new SimpleDialog();
		d.titleId = titleId; d.messageId = messageId; d.helpId = helpId; d.listener = listener; d.args = args;
		return d;
	}

	public static SimpleDialog newInstance(int titleId, int messageId, int helpId, Object... args) {
		SimpleDialog d = new SimpleDialog();
		d.titleId = titleId; d.messageId = messageId; d.helpId = helpId; d.args = args;
		return d;
	}

	public static SimpleDialog newInstance(int titleId, int messageId, int helpId, OnConfirmListener listener) {
		SimpleDialog d = new SimpleDialog();
		d.titleId = titleId; d.messageId = messageId; d.helpId = helpId; d.listener = listener;
		return d;
	}

	public static SimpleDialog newInstance(CharSequence title, CharSequence message, int helpId, OnConfirmListener listener) {
		SimpleDialog d = new SimpleDialog();
		d.title = title; d.message = message; d.helpId = helpId; d.listener = listener;
		return d;
	}

	public static SimpleDialog newInstance(int titleId, int messageId, int helpId) {
		SimpleDialog d = new SimpleDialog();
		d.titleId = titleId; d.messageId = messageId; d.helpId = helpId;
		return d;
	}

	public static SimpleDialog newInstance(CharSequence title, CharSequence message, int helpId) {
		SimpleDialog d = new SimpleDialog();
		d.title = title; d.message = message; d.helpId = helpId;
		return d;
	}
}
