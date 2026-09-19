"""UI-specific rewrites applied to GameState.java after extraction (exact-match, fails loudly)."""
import re, sys
F = "core/src/main/java/com/brucelet/spacetrader/datatypes/GameState.java"
t = open(F, encoding="utf-8").read()

def sub(old, new, count=1):
    global t
    n = t.count(old)
    if n != count:
        sys.exit("patch mismatch (%d != %d): %s" % (n, count, old[:80]))
    t = t.replace(old, new)

sub("""			BaseScreen screen = mGameManager.getCurrentScreen();
			if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;
			screen.setViewVisibilityById(R.id.screen_encounter_continuous_interrupt, false);
			screen.setViewVisibilityById(R.id.screen_encounter_continuous_ticker, false);
""", """			ui.encounterAutoModeCleared();
""")

# encounter button handling: replace R.id-based dispatch with a button-based entry point
i = t.index("\tpublic void encounterFormHandleEvent ( final int buttonId )")
j = t.index("\tprivate enum Result {") if "\tprivate enum Result {" in t[i:] else None
end_marker = t.index("\t\t\t\t\t}));\n\t\t}\n\t}\n", i) + len("\t\t\t\t\t}));\n\t\t}\n\t}\n")
t = t[:i] + """	/** Player pressed an encounter button (attack, flee, surrender, ...). */
	public void encounterButton(EncounterButton button)
	{
		if (encounterButtonRunning) return;
		encounterButtonRunning = true;

		autoHandler.removeCallbacksAndMessages(null);
		runningTask = new EncounterButtonTask();
		runningTask.execute(button);
	}

	/** The n-th (1-4) button of the current encounter, so the frontend can label and route it. */
	public EncounterButton encounterButtonFor(int n) { return encounterType.button(n); }

	/** Player tapped a tribble on the encounter screen. */
	public void encounterTribble(final int tribbleId)
	{
		encounterButton(EncounterButton.TRIBBLE);
		ui.showDialog(SimpleDialog.newInstance(
				R.string.screen_encounter_squeek_title,
				R.string.screen_encounter_squeek_message,
				R.string.help_squeek,
				new OnConfirmListener() {
					@Override
					public void onConfirm() {
						ui.moveTribble(tribbleId);
					}
				}));
	}
""" + t[end_marker:]

sub("""			encounterButtons();
			encounterDisplayShips();
			
			switch (result) {""", """			ui.encounterChanged();
			
			switch (result) {""")
sub("""			encounterDisplayShips();
		}
				""", """			ui.encounterChanged();
		}
				""")
sub("""				BaseScreen screen = mGameManager.getCurrentScreen();
				if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;
				
""", "")
pat = re.compile(r'[' + '\t' + r']+TextView textView = .*?encounterDisplayNextAction' + r'\(false\);' + '\n', re.S)
rep = '\t\t\t\t' + 'ui.showEncounterDescription(description);' + '\n' + '\t\t\t\t' + 'ui.encounterChanged();' + '\n'
t, n = pat.subn(rep, t)
assert n == 1
t = t.replace("showEndGameScreen(EndStatus.KILLED);", "showEndGameScreen(EndStatus.KILLED);")
t = t.replace("if (encounterAnim) animateAttack(", "if (encounterAnim) ui.animateAttack(")
t = t.replace("if (encounterAnim) animateEnterExit(", "if (encounterAnim) ui.animateEnterExit(")
t = t.replace("new OnPositiveListener()", "new InputDialog.OnPositiveListener()")
t = t.replace("new OnNeutralListener()", "new InputDialog.OnNeutralListener()")
t = t.replace("new OnNegativeListener()", "new InputDialog.OnNegativeListener()")

# end game screen: state only; the frontend reads endStatus via getter
t = t.replace("\tprivate Resources getResources()", """	public void showEndGameScreen(EndStatus endStatus)
	{
		this.endStatus = endStatus;
		ui.setScreen(ScreenType.ENDGAME);
	}

	public EndStatus endStatus() { return endStatus; }

	private Resources getResources()""", 1)

# drop debug logging; screens are redrawn every frame so redraw calls become a single hook
t = re.sub(r"[ 	]*Log\.d\([^;]*\);[ 	]*" + chr(10), "", t)
for old in ("drawBuyShipForm();", "showShipYard();", "drawBuyCargoForm();", "drawSellCargoForm();", "showDumpCargo();", "showAveragePrices();"):
    t = t.replace(old, "ui.stateChanged();")
t = re.sub(r"setAdapterSystems\(\(\(WarpSubScreen\)mGameManager\.getCurrentScreen\(\)\)\.getPagerAdapter\(\)\);", "", t)
t = t.replace("mGameManager.findDialogByClass(PlunderDialog.class).onRefreshDialog();", "ui.stateChanged();")

t = t.replace("	private Resources getResources()", open("tools/extra_api.java.txt", encoding="utf-8").read() + open("tools/extra_api2.java.txt", encoding="utf-8").read() + open("tools/extra_api3.java.txt", encoding="utf-8").read() + open("tools/extra_api4.java.txt", encoding="utf-8").read() + "	private Resources getResources()", 1)

t = t.replace('selectedShipType = ShipType.values()[prefs.getInt("selectedShipType",0)];',
              'int selType = prefs.getInt("selectedShipType", -1); '
              'selectedShipType = selType < 0 ? null : ShipType.values()[selType];')

t = re.sub(r"[ 	]*ui\.currentScreen\(\)\.setViewTextById\([^;]*;[ 	]*" + chr(10), "", t)
t = re.sub(r"[ 	]*mGameManager\.getCurrentScreen\(\)\.setViewTextById\([^;]*;[ 	]*" + chr(10), "", t)
open(F, "w", encoding="utf-8").write(t)
import subprocess
subprocess.check_call([sys.executable, "tools/patch_ui2.py"])
subprocess.check_call([sys.executable, "tools/publicize.py", F])
print("patched")
