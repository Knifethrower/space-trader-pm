package com.brucelet.spacetrader.platform;

/**
 * Small stand-in for android.os.AsyncTask: doInBackground runs on a fresh thread, and the
 * pre/progress/post callbacks run on the UI thread (see {@link UiThread}).
 */
public abstract class AsyncTask<Params, Progress, Result> {
	private volatile boolean cancelled;

	protected void onPreExecute() {}

	@SuppressWarnings("unchecked")
	protected abstract Result doInBackground(Params... params);

	@SuppressWarnings("unchecked")
	protected void onProgressUpdate(Progress... values) {}

	protected void onPostExecute(Result result) {}

	@SuppressWarnings("unchecked")
	protected final void publishProgress(final Progress... values) {
		UiThread.post(() -> onProgressUpdate(values));
	}

	public final boolean isCancelled() { return cancelled; }

	public final void cancel(boolean mayInterrupt) { cancelled = true; }

	@SuppressWarnings("unchecked")
	public final AsyncTask<Params, Progress, Result> execute(final Params... params) {
		UiThread.post(() -> {
			onPreExecute();
			Thread t = new Thread(() -> {
				final Result r = doInBackground(params);
				UiThread.post(() -> { if (!cancelled) onPostExecute(r); });
			}, "spacetrader-task");
			t.setDaemon(true);
			t.start();
		});
		return this;
	}
}
