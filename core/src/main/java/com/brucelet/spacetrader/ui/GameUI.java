package com.brucelet.spacetrader.ui;

import com.brucelet.spacetrader.enumtypes.ScreenType;

/**
 * Everything the game rules need from the frontend. Implementations may be called from any thread;
 * dialog listeners are always invoked on the UI thread.
 */
public interface GameUI {
	/** Show a dialog. Returns immediately; listeners fire when the player answers. */
	void showDialog(BaseDialog dialog);

	/** Switch the main screen. */
	void setScreen(ScreenType screen);

	ScreenType currentScreen();

	/** Persist the current game. */
	void autosave();

	/** Drop any screen history so Back can't return to a stale screen. */
	void clearBackStack();

	/** The opponent or player ship display changed (hit, destroyed, entering, leaving). */
	void encounterChanged();

	/** Game data changed in a way the current screen may need to refresh. */
	void stateChanged();

	/** Hide the auto attack/flee indicator and interrupt button. */
	void encounterAutoModeCleared();

	/** Text describing what just happened in the current encounter round. */
	void showEncounterDescription(String text);

	/** Play the attack animation for one side. */
	void animateAttack(boolean commanderUnderAttack, boolean hit, boolean destroyed);

	/** Play a ship entering or leaving the encounter view. */
	void animateEnterExit(boolean commandersShip, boolean enter);

	/** Move a tribble sprite to a new random position. */
	void moveTribble(int tribbleId);

	/** No-op implementation for tests and headless runs. */
	GameUI NONE = new GameUI() {
		public void showDialog(BaseDialog dialog) {}
		public void setScreen(ScreenType screen) {}
		public ScreenType currentScreen() { return null; }
		public void autosave() {}
		public void clearBackStack() {}
		public void encounterChanged() {}
		public void stateChanged() {}
		public void encounterAutoModeCleared() {}
		public void showEncounterDescription(String text) {}
		public void animateAttack(boolean commanderUnderAttack, boolean hit, boolean destroyed) {}
		public void animateEnterExit(boolean commandersShip, boolean enter) {}
		public void moveTribble(int tribbleId) {}
	};
}
