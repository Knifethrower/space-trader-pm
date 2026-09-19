package com.brucelet.spacetrader.ui;

/** Message with confirm and cancel buttons. Button labels default to generic yes/no when the ids are -1. */
public class ConfirmDialog extends BaseDialog {
	public int posId = -1;
	public int negId = -1;
	public OnConfirmListener confirm;
	public OnCancelListener cancel;

	private ConfirmDialog() {}

	public static ConfirmDialog newInstance(int titleId, int messageId, int helpId, OnConfirmListener confirm, OnCancelListener cancel, Object... args) {
		ConfirmDialog d = new ConfirmDialog();
		d.titleId = titleId; d.messageId = messageId; d.helpId = helpId; d.confirm = confirm; d.cancel = cancel; d.args = args;
		return d;
	}

	public static ConfirmDialog newInstance(int titleId, int messageId, int posId, int negId, int helpId, OnConfirmListener confirm, OnCancelListener cancel, Object... args) {
		ConfirmDialog d = new ConfirmDialog();
		d.titleId = titleId; d.messageId = messageId; d.posId = posId; d.negId = negId; d.helpId = helpId;
		d.confirm = confirm; d.cancel = cancel; d.args = args;
		return d;
	}

	public static ConfirmDialog newInstance(int titleId, int messageId, int helpId, OnConfirmListener confirm, OnCancelListener cancel) {
		ConfirmDialog d = new ConfirmDialog();
		d.titleId = titleId; d.messageId = messageId; d.helpId = helpId; d.confirm = confirm; d.cancel = cancel;
		return d;
	}

	public static ConfirmDialog newInstance(String title, String message, int helpId, OnConfirmListener confirm, OnCancelListener cancel) {
		ConfirmDialog d = new ConfirmDialog();
		d.title = title; d.message = message; d.helpId = helpId; d.confirm = confirm; d.cancel = cancel;
		return d;
	}
}
