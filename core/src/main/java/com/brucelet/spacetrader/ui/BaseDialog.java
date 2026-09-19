package com.brucelet.spacetrader.ui;

/** Data-only description of a dialog. The frontend decides how to render and navigate it. */
public abstract class BaseDialog {
	public int titleId = -1;
	public int messageId = -1;
	public CharSequence title;
	public CharSequence message;
	public int helpId = -1;
	public Object[] args;
}
