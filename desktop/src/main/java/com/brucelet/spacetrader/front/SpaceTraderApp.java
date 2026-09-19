package com.brucelet.spacetrader.front;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.brucelet.spacetrader.R;
import com.brucelet.spacetrader.datatypes.GameState;
import com.brucelet.spacetrader.datatypes.Ship;
import com.brucelet.spacetrader.datatypes.SolarSystem;
import com.brucelet.spacetrader.enumtypes.DifficultyLevel;
import com.brucelet.spacetrader.enumtypes.EncounterButton;
import com.brucelet.spacetrader.enumtypes.Opponent;
import com.brucelet.spacetrader.enumtypes.ScreenType;
import com.brucelet.spacetrader.enumtypes.SellOperation;
import com.brucelet.spacetrader.enumtypes.Gadget;
import com.brucelet.spacetrader.enumtypes.Purchasable;
import com.brucelet.spacetrader.enumtypes.Shield;
import com.brucelet.spacetrader.enumtypes.ShipType;
import com.brucelet.spacetrader.enumtypes.TradeItem;
import com.brucelet.spacetrader.enumtypes.Weapon;
import com.brucelet.spacetrader.enumtypes.XmlString;
import com.brucelet.spacetrader.platform.Resources;
import com.brucelet.spacetrader.platform.SharedPreferences;
import com.brucelet.spacetrader.platform.UiThread;
import com.brucelet.spacetrader.ui.BaseDialog;
import com.brucelet.spacetrader.ui.ConfirmDialog;
import com.brucelet.spacetrader.ui.GameUI;
import com.brucelet.spacetrader.ui.InputDialog;
import com.brucelet.spacetrader.ui.JettisonDialog;
import com.brucelet.spacetrader.ui.PlunderDialog;
import com.brucelet.spacetrader.ui.HighScoresDialog;
import com.brucelet.spacetrader.ui.NewspaperDialog;
import com.brucelet.spacetrader.ui.ShipInfoDialog;
import com.brucelet.spacetrader.ui.SpecialEventDialog;
import com.brucelet.spacetrader.ui.SimpleDialog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Text-mode frontend. Everything is laid out on a virtual 640x480 canvas; wider windows get extra
 * margin at the sides, so the same layout serves 640x480, 1280x720 and other resolutions.
 */
public class SpaceTraderApp extends ApplicationAdapter implements GameUI {

	static final float VW = 640, VH = 480;

	enum Mode { TITLE, NEWGAME, HUB, BUY, SELL, WARP, STATUS, SYSINFO, ENCOUNTER, ENDGAME, YARD, BUYEQ, SELLEQ, BANK, BUYSHIP, PERSONNEL, QUESTS, CARGO, CHART, OPTIONS, HELP, HELPSUB, HELPPAGE, SHORT, AVGPRICES }

	static final Color BG = new Color(0.03f, 0.04f, 0.10f, 1);
	static final Color PANEL = new Color(0.08f, 0.10f, 0.22f, 1);
	static final Color HILITE = new Color(0.20f, 0.30f, 0.65f, 1);
	static final Color TEXT = new Color(0.85f, 0.90f, 1f, 1);
	static final Color DIM = new Color(0.55f, 0.60f, 0.75f, 1);
	static final Color GOLD = new Color(1f, 0.85f, 0.3f, 1);
	static final Color WARN = new Color(1f, 0.45f, 0.35f, 1);

	final Resources res = Resources.get();
	final Path savePath = Paths.get(System.getProperty("st.save", "savedata/save.properties"));
	final String shotDir = System.getProperty("st.shot");

	ExtendViewport viewport;
	OrthographicCamera camera;
	SpriteBatch batch;
	ShapeRenderer shapes;
	BitmapFont font;
	Texture pixel;
	GlyphLayout layout = new GlyphLayout();

	GameState game;
	Mode mode = Mode.TITLE;
	int sel = 0;

	// new game form
	String cmdrName = "Jameson";
	int[] skills = { 5, 5, 5, 5 };
	DifficultyLevel difficulty = DifficultyLevel.NORMAL;

	// encounter
	String encDescription = "", encAction = "";
	List<EncounterButton> encButtons = new ArrayList<>();

	// dialogs
	final Deque<BaseDialog> dialogs = new ArrayDeque<>();
	String inputText = "";
	int plunderSel = 0;

	// scripted input for testing (-Dst.script=DOWN,ENTER,...)
	final Deque<Integer> script = new ArrayDeque<>();
	int scriptDelay = 0, shotIndex = 0;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(VW, VH, camera);
		batch = new SpriteBatch();
		shapes = new ShapeRenderer();
		Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pm.setColor(Color.WHITE);
		pm.fill();
		pixel = new Texture(pm);
		pm.dispose();

		UiThread.setPoster(r -> Gdx.app.postRunnable(r));

		if (System.getProperty("st.hidecursor") != null) {
			// the game is pad/keyboard driven: replace the mouse pointer with an invisible one
			Pixmap blank = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
			Gdx.graphics.setCursor(Gdx.graphics.newCursor(blank, 0, 0));
			blank.dispose();
		}

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override public boolean keyDown(int keycode) { onKey(keycode); return true; }
			@Override public boolean keyTyped(char c) { onChar(c); return true; }
		});

		String s = System.getProperty("st.script");
		if (s != null && !s.isEmpty())
			for (String k : s.split(",")) script.add(keyByName(k.trim()));
		if (shotDir != null) try { Files.createDirectories(Paths.get(shotDir)); } catch (Exception ignored) {}

		loadAll();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> saveNow("shutdown")));
	}

	/** Case-insensitive key lookup (libGDX names are like "Enter", "Down"). */
	static int keyByName(String name) {
		for (int i = 0; i < 256; i++) {
			String n = Input.Keys.toString(i);
			if (n != null && n.equalsIgnoreCase(name)) return i;
		}
		throw new IllegalArgumentException("Unknown key " + name);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		if (fontReady) rebuildFont();
	}

	// ---- start-up: everything is loaded before the first frame, so the first thing drawn is the title screen ----

	boolean fontReady;
	boolean ready;

	void loadAll() {
		rebuildFont();
		fontReady = true;
		game = new GameState();
		game.setUI(this);
		if (Files.exists(savePath)) {
			try {
				long size = Files.size(savePath);
				game.load(new SharedPreferences(savePath));
				System.out.println("[save] loaded " + savePath + " (" + size + " bytes), game in progress: " + game.hasActiveGame() + " (ship " + (game.ship != null) + ", end status " + game.endStatus() + ", day " + game.days() + ")");
			} catch (Exception e) {
				System.out.println("[save] could not load " + savePath + ": " + e + " (the file is kept as .bad)");
				e.printStackTrace();
				try {
					Files.copy(savePath, savePath.resolveSibling(savePath.getFileName() + ".bad"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception ignored) {}
				game = new GameState();
				game.setUI(this);
			}
		} else {
			System.out.println("[save] no save file at " + savePath.toAbsolutePath());
		}
		sprite("title_bg");
		ready = true;
	}

	int fontPixelHeight = -1;

	/** Regenerates the TTF at the real pixel size so text stays sharp at any resolution (layout stays in 640x480 units). */
	void rebuildFont() {
		int h = Math.max(1, Gdx.graphics.getBackBufferHeight());
		if (h == fontPixelHeight && font != null) return;
		fontPixelHeight = h;
		float k = Math.max(1f, h / VH);
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.classpath("fonts/DejaVuSans.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter par = new FreeTypeFontGenerator.FreeTypeFontParameter();
		par.size = Math.round(14 * k);
		par.minFilter = Texture.TextureFilter.Linear;
		par.magFilter = Texture.TextureFilter.Linear;
		par.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "…–—éèüöä";
		BitmapFont nf = gen.generateFont(par);
		gen.dispose();
		nf.getData().setScale(1f / k);
		nf.setUseIntegerPositions(false);
		if (font != null) font.dispose();
		font = nf;
		helpLayouts = null;
	}

	// ------------------------------------------------------------------ GameUI

	@Override public void showDialog(final BaseDialog d) {
		Gdx.app.postRunnable(() -> {
			dialogs.add(d);
			if (dialogs.size() == 1) dialogOpened(d);
		});
	}

	@Override public void setScreen(final ScreenType s) {
		Gdx.app.postRunnable(() -> {
			switch (s) {
				case TITLE: mode = Mode.TITLE; break;
				case ENCOUNTER: enterEncounter(); break;
				case ENDGAME: mode = Mode.ENDGAME; break;
				case BUY: mode = Mode.BUY; break;
				case SELL: mode = Mode.SELL; break;
				case WARP: mode = Mode.WARP; break;
				case STATUS: mode = Mode.STATUS; break;
				default: mode = Mode.HUB; if (game.ship != null) game.systemInformationEntered(); break;
			}
			sel = 0;
		});
	}

	@Override public ScreenType currentScreen() {
		switch (mode) {
			case ENCOUNTER: return ScreenType.ENCOUNTER;
			case ENDGAME: return ScreenType.ENDGAME;
			case TITLE: return ScreenType.TITLE;
			case BUY: return ScreenType.BUY;
			case SELL: return ScreenType.SELL;
			default: return ScreenType.INFO;
		}
	}

	@Override public void autosave() { saveNow("game event"); }

	/** Saves the running game and logs the outcome (visible in log.txt on the device). */
	synchronized void saveNow(String why) {
		if (game == null || game.ship == null) return;
		long t = System.currentTimeMillis();
		boolean ok = false;
		try {
			ok = game.save(new SharedPreferences(savePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		long size = 0;
		try { size = Files.size(savePath); } catch (Exception ignored) {}
		System.out.println("[save] " + why + ": " + (ok ? "ok" : "FAILED") + ", " + size + " bytes, " + (System.currentTimeMillis() - t) + " ms");
	}

	@Override public void clearBackStack() {}
	@Override public void stateChanged() {}
	@Override public void encounterChanged() {}
	@Override public void encounterAutoModeCleared() {}
	@Override public void animateAttack(boolean a, boolean b, boolean c) {}
	@Override public void animateEnterExit(boolean a, boolean b) {}
	@Override public void moveTribble(int id) {}

	@Override public void showEncounterDescription(final String text) {
		Gdx.app.postRunnable(() -> {
			encDescription = text;
			encAction = game.encounterNextAction(false);
		});
	}

	void enterEncounter() {
		mode = Mode.ENCOUNTER;
		game.encounterScreenEntered();
		encDescription = game.encounterInitialDescription();
		encAction = game.encounterNextAction(true);
		sel = 0;
	}

	// ------------------------------------------------------------------ strings

	String str(int id, Object... args) {
		return args.length == 0 ? res.getString(id) : res.getString(id, convert(args));
	}

	Object[] convert(Object[] args) {
		Object[] out = args.clone();
		for (int i = 0; i < out.length; i++)
			if (out[i] instanceof XmlString) out[i] = ((XmlString) out[i]).toXmlString(res);
		return out;
	}

	String title(BaseDialog d) {
		if (d instanceof SpecialEventDialog) return game.specialEventTitle();
		if (d.title != null) return d.title.toString();
		if (d.titleId < 0) return "";
		return d.args != null && d.args.length > 0 ? res.getString(d.titleId, convert(d.args)) : res.getString(d.titleId);
	}

	String message(BaseDialog d) {
		if (d instanceof SpecialEventDialog) return game.specialEventMessage();
		if (d.message != null) return d.message.toString();
		if (d.messageId < 0) return "";
		return d.args != null && d.args.length > 0 ? res.getString(d.messageId, convert(d.args)) : res.getString(d.messageId);
	}

	// ------------------------------------------------------------------ input

	List<String> newsLines = new ArrayList<>();
	String newsTitle = "";

	void dialogOpened(BaseDialog d) {
		inputText = "";
		plunderSel = 0;
		if (d instanceof NewspaperDialog) {
			newsTitle = game.newspaperTitle();
			newsLines = game.newspaperHeadlines();
		}
	}

	void closeDialog() {
		dialogs.poll();
		BaseDialog next = dialogs.peek();
		if (next != null) dialogOpened(next);
	}

	void onChar(char c) {
		if (!ready) return;
		BaseDialog d = dialogs.peek();
		if (d instanceof InputDialog) {
			if (c >= '0' && c <= '9' && inputText.length() < 9) inputText += c;
		} else if (d == null && mode == Mode.NEWGAME && (sel == 0 || nameEntry)) {
			if (c >= 32 && c < 127 && cmdrName.length() < 20) cmdrName += c;
		}
	}

	boolean kUp, kDown, kLeft, kRight, kAlt2;

	// on-screen keyboard for the commander name (handhelds have no keyboard)
	boolean nameEntry;
	int keyRow, keyCol;
	static final String[] KEY_ROWS = { "ABCDEFGHIJKLM", "NOPQRSTUVWXYZ", "abcdefghijklm", "nopqrstuvwxyz", "0123456789 -'" };
	static final int KEY_LAST_ROW = KEY_ROWS.length; // row of command keys: DEL, OK

	int keyCols(int row) { return row == KEY_LAST_ROW ? 2 : KEY_ROWS[row].length(); }

	void nameKey(boolean up, boolean down, boolean left, boolean right, boolean ok, boolean back, boolean alt) {
		if (up) keyRow = (keyRow + KEY_LAST_ROW) % (KEY_LAST_ROW + 1);
		if (down) keyRow = (keyRow + 1) % (KEY_LAST_ROW + 1);
		int cols = keyCols(keyRow);
		if (keyCol >= cols) keyCol = cols - 1;
		if (left) keyCol = (keyCol + cols - 1) % cols;
		if (right) keyCol = (keyCol + 1) % cols;
		if (up || down) keyCol = Math.min(keyCol, keyCols(keyRow) - 1);
		if (ok) {
			if (keyRow == KEY_LAST_ROW) {
				if (keyCol == 0) { if (!cmdrName.isEmpty()) cmdrName = cmdrName.substring(0, cmdrName.length() - 1); }
				else nameEntry = false;
			} else if (cmdrName.length() < 20) cmdrName += KEY_ROWS[keyRow].charAt(keyCol);
		}
		if (back && !cmdrName.isEmpty()) cmdrName = cmdrName.substring(0, cmdrName.length() - 1);
		if (alt) nameEntry = false;
	}

	void drawNameEntry(float x) {
		float bx = x + 90, bw = VW - 180, by = 80, bh = 290;
		rect(bx, VH - by - bh, bw, bh, PANEL);
		text("Commander name", bx + 16, by + 10, GOLD);
		text(cmdrName + "_", bx + 16, by + 36, TEXT);
		float cell = 34;
		float gx = bx + (bw - 13 * cell) / 2f;
		for (int r = 0; r < KEY_ROWS.length; r++) {
			for (int c = 0; c < KEY_ROWS[r].length(); c++) {
				float cx = gx + c * cell, cy = by + 70 + r * 30;
				if (r == keyRow && c == keyCol) rect(cx, VH - cy - 24, cell - 2, 26, HILITE);
				text(String.valueOf(KEY_ROWS[r].charAt(c)), cx + 10, cy, TEXT);
			}
		}
		float cy = by + 70 + KEY_ROWS.length * 30;
		String[] labels = { "Delete", "Done" };
		for (int c = 0; c < 2; c++) {
			float cx = gx + c * 110;
			if (keyRow == KEY_LAST_ROW && keyCol == c) rect(cx, VH - cy - 24, 100, 26, HILITE);
			text(labels[c], cx + 14, cy, TEXT);
		}
		text("A: type   B: erase   X: done", bx + 16, by + bh - 28, DIM);
	}

	int inputValue() { return inputText.isEmpty() ? 0 : Integer.parseInt(inputText); }

	void onKey(int k) {
		if (!ready) return;
		dirty = true;
		lastInputMs = System.currentTimeMillis();
		boolean up = k == Input.Keys.UP || k == Input.Keys.W;
		boolean down = k == Input.Keys.DOWN || k == Input.Keys.S;
		boolean left = k == Input.Keys.LEFT || k == Input.Keys.A;
		boolean right = k == Input.Keys.RIGHT || k == Input.Keys.D;
		boolean ok = k == Input.Keys.ENTER || k == Input.Keys.Z;
		boolean back = k == Input.Keys.ESCAPE || k == Input.Keys.X;
		boolean alt = k == Input.Keys.TAB;
		boolean alt2 = k == Input.Keys.C;

		if (nameEntry) { nameKey(up, down, left, right, ok, back, alt || alt2); return; }
		BaseDialog d = dialogs.peek();
		if (d != null) { dialogKey(d, k, up, down, left, right, ok, back, alt || alt2); return; }

		if (mode == Mode.NEWGAME && k == Input.Keys.BACKSPACE && sel == 0 && !cmdrName.isEmpty()) {
			cmdrName = cmdrName.substring(0, cmdrName.length() - 1);
			return;
		}
		int n = itemCount();
		if (up && n > 0) sel = (sel + n - 1) % n;
		if (down && n > 0) sel = (sel + 1) % n;
		kUp = up; kDown = down; kLeft = left; kRight = right; kAlt2 = alt2;
		screenKey(left, right, ok, back, alt);
	}

	void dialogKey(BaseDialog d, int k, boolean up, boolean down, boolean left, boolean right, boolean ok, boolean back, boolean alt) {
		if (d instanceof SimpleDialog) {
			if (ok || back) {
				closeDialog();
				if (((SimpleDialog) d).listener != null) ((SimpleDialog) d).listener.onConfirm();
			}
		} else if (d instanceof ConfirmDialog) {
			ConfirmDialog c = (ConfirmDialog) d;
			if (ok) { closeDialog(); if (c.confirm != null) c.confirm.onConfirm(); }
			else if (back) { closeDialog(); if (c.cancel != null) c.cancel.onCancel(); }
		} else if (d instanceof SpecialEventDialog) {
			if (game.specialEventIsMessage()) {
				if (ok || back) { closeDialog(); game.specialEventAccept(); }
			} else if (ok) { closeDialog(); game.specialEventAccept(); }
			else if (back) closeDialog();
		} else if (d instanceof NewspaperDialog || d instanceof HighScoresDialog) {
			if (ok || back) closeDialog();
		} else if (d instanceof InputDialog) {
			InputDialog in = (InputDialog) d;
			if (k == Input.Keys.BACKSPACE && !inputText.isEmpty()) inputText = inputText.substring(0, inputText.length() - 1);
			int v = inputValue();
			if (up) inputText = String.valueOf(v + 1);
			if (down) inputText = String.valueOf(Math.max(0, v - 1));
			if (right) inputText = String.valueOf(v + 10);
			if (left) inputText = String.valueOf(Math.max(0, v - 10));
			if (ok) { int val = inputValue(); closeDialog(); if (in.positive != null) in.positive.onClickPositiveButton(val); }
			else if (alt && in.neutralId >= 0) { closeDialog(); if (in.neutral != null) in.neutral.onClickNeutralButton(); }
			else if (back) { closeDialog(); if (in.negative != null) in.negative.onClickNegativeButton(); }
		} else if (d instanceof PlunderDialog) {
			TradeItem[] items = TradeItem.values();
			if (up) plunderSel = (plunderSel + items.length) % (items.length + 1);
			if (down) plunderSel = (plunderSel + 1) % (items.length + 1);
			if (ok) {
				if (plunderSel == items.length) { closeDialog(); game.plunderDone(); }
				else game.plunderPick(items[plunderSel]);
			} else if (alt && plunderSel < items.length) game.plunderAll(items[plunderSel]);
			else if (back) { closeDialog(); game.plunderDone(); }
		} else if (d instanceof JettisonDialog) {
			TradeItem[] items = TradeItem.values();
			if (up) plunderSel = (plunderSel + items.length - 1) % items.length;
			if (down) plunderSel = (plunderSel + 1) % items.length;
			if (ok) game.dumpPick(items[plunderSel]);
			else if (alt) game.dumpAll(items[plunderSel]);
			else if (back) {
				closeDialog();
				if (((JettisonDialog) d).listener != null) ((JettisonDialog) d).listener.onConfirm();
			}
		} else {
			if (ok || back) closeDialog();
		}
	}

	// ------------------------------------------------------------------ per-screen behaviour

	static final String[] HUB_ITEMS = { "Buy Cargo", "Sell Cargo", "Ship Yard", "Buy Equipment", "Sell Equipment", "Personnel", "Bank", "Short Range Chart", "Galactic Chart", "Warp (list)", "Commander Status", "System Information", "Options", "Help", "Retire", "Quit to Title" };

	int itemCount() {
		switch (mode) {
			case TITLE: return titleItems().length;
			case NEWGAME: return 8;
			case HUB: return HUB_ITEMS.length;
			case BUY: case SELL: return TradeItem.values().length;
			case YARD: return 6;
			case BUYEQ: return equipmentForSale().size();
			case SELLEQ: return installedEquipment().size();
			case BANK: return 3;
			case OPTIONS: return game.optionCount();
			case HELP: return helpTopics().size();
			case HELPSUB: return helpEntries().size();
			case PERSONNEL: return 3;
			case SYSINFO: return sysInfoActions().size();
			case BUYSHIP: return ShipType.buyableValues().length;
			case WARP: return warpTargets().size();
			case ENCOUNTER: return encButtons.size();
			default: return 0;
		}
	}

	boolean hasSave() { return Files.exists(savePath); }

	void screenKey(boolean left, boolean right, boolean ok, boolean back, boolean alt) {
		switch (mode) {
			case TITLE: {
				String[] items = titleItems();
				if (ok) {
					String it = items[sel];
					if (it.equals("Continue")) {
						mode = Mode.HUB; sel = 0;
						game.systemInformationEntered();
					} else if (it.equals("New Game")) { mode = Mode.NEWGAME; sel = 0; }
					else if (it.equals("High Scores")) game.viewHighScores();
					else if (it.equals("Options")) { optionsReturn = Mode.TITLE; mode = Mode.OPTIONS; sel = 0; }
					else if (it.equals("Help")) openHelp(Mode.TITLE);
					else Gdx.app.exit();
				}
				break;
			}
			case NEWGAME: {
				int dir = right ? 1 : left ? -1 : 0;
				if (sel == 1 && dir != 0) difficulty = dir > 0 ? difficulty.next() : difficulty.prev();
				if (sel >= 2 && sel <= 5 && dir != 0) {
					int i = sel - 2;
					int total = skills[0] + skills[1] + skills[2] + skills[3];
					if (dir > 0 && skills[i] < GameState.MAXSKILL && total < 2 * GameState.MAXSKILL) skills[i]++;
					if (dir < 0 && skills[i] > 1) skills[i]--;
				}
				if (ok && sel == 0) { nameEntry = true; keyRow = 0; keyCol = 0; }
				if (ok && sel == 6) startGame();
				if (ok && sel == 7 || back) { mode = Mode.TITLE; sel = 0; }
				break;
			}
			case HUB: {
				if (back) break;
				if (!ok) break;
				switch (sel) {
					case 0: mode = Mode.BUY; sel = 0; break;
					case 1: mode = Mode.SELL; sel = 0; break;
					case 2: mode = Mode.YARD; sel = 0; break;
					case 3: mode = Mode.BUYEQ; sel = 0; break;
					case 4: mode = Mode.SELLEQ; sel = 0; break;
					case 5: mode = Mode.PERSONNEL; sel = 0; break;
					case 6: mode = Mode.BANK; sel = 0; break;
					case 7: enterShortChart(); break;
					case 8: enterChart(); break;
					case 9: mode = Mode.WARP; sel = 0; break;
					case 10: mode = Mode.STATUS; break;
					case 11: mode = Mode.SYSINFO; sel = 0; game.systemInformationEntered(); break;
					case 12: optionsReturn = Mode.HUB; mode = Mode.OPTIONS; sel = 0; break;
					case 13: openHelp(Mode.HUB); break;
					case 14: game.retire(); break;
					default: autosave(); mode = Mode.TITLE; sel = 0; break;
				}
				break;
			}
			case YARD: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				if (!ok) break;
				switch (sel) {
					case 0: game.yardFuel(false); break;
					case 1: game.yardFuel(true); break;
					case 2: game.yardRepair(false); break;
					case 3: game.yardRepair(true); break;
					case 4: mode = Mode.BUYSHIP; sel = 0; game.enterBuyShip(); break;
					default: if (game.canBuyPod()) game.yardBuyPod(); break;
				}
				break;
			}
			case BUYEQ: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				List<Purchasable> items = equipmentForSale();
				if (ok && sel < items.size()) game.buyEquipment(items.get(sel));
				break;
			}
			case SELLEQ: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				List<int[]> items = installedEquipment();
				if (ok && sel < items.size()) game.sellEquipment(items.get(sel)[0], items.get(sel)[1]);
				break;
			}
			case BANK: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				if (!ok) break;
				if (sel == 0) game.bankGetLoan();
				else if (sel == 1) game.bankPayBack();
				else game.bankToggleInsurance();
				break;
			}
			case BUYSHIP: {
				if (back) { mode = Mode.YARD; sel = 0; break; }
				ShipType t = ShipType.buyableValues()[sel];
				if (ok) game.buyShipType(t);
				if (alt) game.showShipInfo(t);
				break;
			}
			case BUY:
				if (ok) game.getAmountToBuy(TradeItem.values()[sel]);
				if (back) { mode = Mode.HUB; sel = 0; }
				break;
			case SELL:
				if (ok) game.getAmountToSell(TradeItem.values()[sel], SellOperation.SELL);
				if (alt) game.dumpPick(TradeItem.values()[sel]);
				if (back) { mode = Mode.HUB; sel = 0; }
				break;
			case WARP: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				List<SolarSystem> targets = warpTargets();
				if (ok && !targets.isEmpty()) {
					game.warpTo(targets.get(sel), false);
				}
				if (kAlt2 && !targets.isEmpty()) openPrices(targets.get(sel), Mode.WARP);
				break;
			}
			case STATUS: case QUESTS: case CARGO:
				if (back || ok) { mode = Mode.HUB; sel = 0; }
				else if (right) mode = mode == Mode.STATUS ? Mode.QUESTS : mode == Mode.QUESTS ? Mode.CARGO : Mode.STATUS;
				else if (left) mode = mode == Mode.STATUS ? Mode.CARGO : mode == Mode.CARGO ? Mode.QUESTS : Mode.STATUS;
				break;
			case CHART: chartKey(ok, back, alt); break;
			case SHORT: shortKey(ok, back, alt); break;
			case AVGPRICES: pricesKey(left, right, ok, back); break;
			case HELP: case HELPSUB: case HELPPAGE: helpKey(left, right, ok, back); break;
			case OPTIONS:
				if (back) { mode = optionsReturn; sel = 0; }
				else if (ok) game.optionToggle(sel);
				break;
			case SYSINFO: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				List<String> acts = sysInfoActions();
				if (ok && sel < acts.size()) {
					String a = acts.get(sel);
					if (a.startsWith("Special")) game.showSpecialEvent();
					else if (a.startsWith("Read")) game.readNewspaper();
					else if (a.startsWith("Personnel")) { mode = Mode.PERSONNEL; sel = 0; }
					else { mode = Mode.HUB; sel = 0; }
				}
				break;
			}
			case PERSONNEL: {
				if (back) { mode = Mode.HUB; sel = 0; break; }
				if (!ok) break;
				if (sel == 0 && game.crewSlotHasMercenary(1)) game.personnelFire(1);
				else if (sel == 1 && game.crewSlotHasMercenary(2)) game.personnelFire(2);
				else if (sel == 2 && game.mercenaryForHire()) game.personnelHire();
				break;
			}
			case ENCOUNTER:
				if (ok && sel < encButtons.size()) game.encounterButton(encButtons.get(sel));
				break;
			case ENDGAME:
				if (ok || back) game.endOfGame();
				break;
		}
	}

	String[] titleItems() {
		return game.hasActiveGame() ? new String[] { "Continue", "New Game", "High Scores", "Options", "Help", "Quit" } : new String[] { "New Game", "High Scores", "Options", "Help", "Quit" };
	}

	void startGame() {
		String name = cmdrName.trim().isEmpty() ? "Jameson" : cmdrName.trim();
		if (skills[0] + skills[1] + skills[2] + skills[3] < 2 * GameState.MAXSKILL) {
			showDialog(SimpleDialog.newInstance(R.string.dialog_newgame_morepoints, R.string.dialog_newgame_morepoints_message, R.string.help_moreskillpoints));
			return;
		}
		game.newGame(name, skills[0], skills[1], skills[2], skills[3], difficulty, false);
	}

	List<SolarSystem> warpTargets() {
		List<SolarSystem> out = new ArrayList<>();
		if (game.ship == null) return out;
		final SolarSystem cur = game.currentSystem();
		for (SolarSystem s : game.solarSystems())
			if (game.reachable(s)) out.add(s);
		out.sort(Comparator.comparingInt(s -> GameState.realDistance(cur, s)));
		return out;
	}

	// ------------------------------------------------------------------ rendering

	float left() { return (viewport.getWorldWidth() - VW) / 2f; }

	@Override
	public void render() {
		if (!script.isEmpty() && ++scriptDelay >= 20) {
			scriptDelay = 0;
			int k = script.poll();
			onKey(k);
			if (k >= Input.Keys.NUM_0 && k <= Input.Keys.NUM_9) onChar((char) ('0' + (k - Input.Keys.NUM_0)));
			pendingShot = 3;
		}
		if (mode == Mode.ENCOUNTER) refreshEncounterButtons();
		if (dirty && System.currentTimeMillis() - lastInputMs > 1500) {
			dirty = false;
			saveNow("after activity");
		}

		ScreenUtils.clear(BG.r, BG.g, BG.b, 1);
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		shapes.setProjectionMatrix(camera.combined);
		batch.begin();
		float x = left();
		switch (mode) {
			case TITLE: drawTitle(x); break;
			case NEWGAME: drawNewGame(x); break;
			case HUB: drawHub(x); break;
			case BUY: drawTrade(x, true); break;
			case SELL: drawTrade(x, false); break;
			case WARP: drawWarp(x); break;
			case STATUS: drawStatus(x); break;
			case SYSINFO: drawSysInfo(x); break;
			case ENCOUNTER: drawEncounter(x); break;
			case ENDGAME: drawEnd(x); break;
			case YARD: drawYard(x); break;
			case BUYEQ: drawBuyEq(x); break;
			case SELLEQ: drawSellEq(x); break;
			case BANK: drawBank(x); break;
			case BUYSHIP: drawBuyShip(x); break;
			case PERSONNEL: drawPersonnel(x); break;
			case QUESTS: drawLines(x, "Quests", game.questLines()); break;
			case CARGO: drawLines(x, "Special Cargo", game.specialCargoLines()); break;
			case CHART: drawChart(x); break;
			case SHORT: drawShortChart(x); break;
			case AVGPRICES: drawPrices(x); break;
			case OPTIONS: drawOptions(x); break;
			case HELP: {
				List<String> names = new ArrayList<>();
				for (HelpTopic t : helpTopics()) names.add(t.title);
				drawHelpList(x, "Help", names, "A: open    B: back");
				break;
			}
			case HELPSUB: {
				List<String> names = new ArrayList<>();
				for (HelpTopic t : helpEntries()) names.add(t.title);
				drawHelpList(x, helpSection.title, names, "A: read    B: back");
				break;
			}
			case HELPPAGE: drawHelpPage(x); break;
		}
		BaseDialog d = dialogs.peek();
		if (d != null) drawDialog(x, d);
		batch.end();

		if (pendingShot > 0 && --pendingShot == 0 && shotDir != null) screenshot();
		// test harness only: quit shortly after the scripted keys (and their screenshots) are done
		if (script.isEmpty() && pendingShot == 0 && System.getProperty("st.exit") != null && ++exitFrames > 45) Gdx.app.exit();
	}

	int pendingShot = 0;
	int exitFrames = 0;
	volatile boolean dirty;
	volatile long lastInputMs;

	void screenshot() {
		Pixmap pm = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
		// glReadPixels returns rows bottom-up; flip so the PNG is upright.
		java.nio.ByteBuffer px = pm.getPixels();
		int w = pm.getWidth(), h = pm.getHeight();
		byte[] top = new byte[w * 4], bottom = new byte[w * 4];
		for (int y = 0; y < h / 2; y++) {
			px.position(y * w * 4); px.get(top);
			px.position((h - 1 - y) * w * 4); px.get(bottom);
			px.position(y * w * 4); px.put(bottom);
			px.position((h - 1 - y) * w * 4); px.put(top);
		}
		px.position(0);
		PixmapIO.writePNG(Gdx.files.absolute(shotDir + "/step_" + String.format("%02d", shotIndex++) + ".png"), pm);
		pm.dispose();
	}

	void refreshEncounterButtons() {
		encButtons.clear();
		try {
			for (int i = 1; i <= 4; i++) {
				EncounterButton b = game.encounterButtonFor(i);
				if (b != null) encButtons.add(b);
			}
		} catch (RuntimeException ignored) {}
		if (sel >= encButtons.size()) sel = Math.max(0, encButtons.size() - 1);
	}

	// -- drawing helpers

	void rect(float x, float y, float w, float h, Color c) {
		batch.setColor(c);
		batch.draw(pixel, x, y, w, h);
		batch.setColor(Color.WHITE);
	}

	/** Draw text with the top-left at (x, VH - y). Returns the height used. */
	float text(String s, float x, float y, Color c) {
		font.setColor(c);
		font.draw(batch, s, x, VH - y);
		return font.getLineHeight();
	}

	float textRight(String s, float xRight, float y, Color c) {
		layout.setText(font, s);
		font.setColor(c);
		font.draw(batch, s, xRight - layout.width, VH - y);
		return font.getLineHeight();
	}

	float wrap(String s, float x, float y, float width, Color c) {
		font.setColor(c);
		layout.setText(font, s, c, width, com.badlogic.gdx.utils.Align.left, true);
		font.draw(batch, layout, x, VH - y);
		return layout.height;
	}

	void header(float x, String title) {
		rect(x, VH - 34, VW, 34, PANEL);
		text(title, x + 12, 8, GOLD);
		boolean titleSideHelp = helpReturn == Mode.TITLE && (mode == Mode.HELP || mode == Mode.HELPSUB || mode == Mode.HELPPAGE);
		if (game.ship != null && mode != Mode.TITLE && mode != Mode.NEWGAME && !titleSideHelp) {
			textRight("Day " + game.days() + "   " + game.credits() + " cr.", x + VW - 12, 8, TEXT);
		}
	}

	void footer(float x, String hint) {
		rect(x, 0, VW, 26, PANEL);
		text(hint, x + 12, VH - 22, DIM);
	}

	void row(float x, float y, float w, boolean selected) {
		if (selected) rect(x, VH - y - 21, w, 22, HILITE);
	}

	/** Draws a texture scaled to cover the whole visible area, centred (any excess is cropped). */
	void drawCover(Texture t) {
		float ww = viewport.getWorldWidth();
		float k = Math.max(ww / t.getWidth(), VH / t.getHeight());
		float w = t.getWidth() * k, h = t.getHeight() * k;
		batch.setColor(Color.WHITE);
		batch.draw(t, (ww - w) / 2f, (VH - h) / 2f, w, h);
	}

	void drawTitle(float x) {
		Texture bg = sprite("title_bg");
		if (bg != null) drawCover(bg);
		else {
			// no artwork available: plain text title
			text("SPACE TRADER", x + 220, 90, GOLD);
		}

		// menu: lower centre, on a translucent panel so it stays readable over the stars
		String[] items = titleItems();
		float rowH = 21, panelW = 230, panelH = items.length * rowH + 12;
		float mx = x + (VW - panelW) / 2f, top = VH - panelH - 26;
		batch.setColor(0, 0, 0, 0.6f);
		batch.draw(pixel, mx, VH - top - panelH, panelW, panelH);
		batch.setColor(Color.WHITE);
		for (int i = 0; i < items.length; i++) {
			float y = top + 6 + i * rowH;
			if (i == sel) rect(mx + 4, VH - y - rowH + 1, panelW - 8, rowH - 1, HILITE);
			layout.setText(font, items[i]);
			font.setColor(i == sel ? Color.WHITE : TEXT);
			font.draw(batch, items[i], mx + (panelW - layout.width) / 2f, VH - y - 2);
		}
		textRight("A: select", x + VW - 10, VH - 22, DIM);
	}

	void drawNewGame(float x) {
		header(x, "New Commander");
		String[] labels = { "Name", "Difficulty", "Pilot", "Fighter", "Trader", "Engineer" };
		String[] vals = new String[6];
		vals[0] = cmdrName + (sel == 0 ? "_" : "");
		vals[1] = difficulty.toXmlString(res);
		for (int i = 0; i < 4; i++) vals[i + 2] = String.valueOf(skills[i]);
		for (int i = 0; i < 6; i++) {
			row(x + 40, 60 + i * 30, 560, sel == i);
			text(labels[i], x + 52, 60 + i * 30, TEXT);
			text(vals[i], x + 300, 60 + i * 30, sel == i ? GOLD : TEXT);
		}
		int total = skills[0] + skills[1] + skills[2] + skills[3];
		text("Skill points left: " + (2 * GameState.MAXSKILL - total), x + 52, 60 + 6 * 30, DIM);
		row(x + 40, 60 + 7 * 30 + 10, 560, sel == 6);
		text("Start Game", x + 52, 60 + 7 * 30 + 10, TEXT);
		row(x + 40, 60 + 8 * 30 + 10, 560, sel == 7);
		text("Back", x + 52, 60 + 8 * 30 + 10, TEXT);
		if (nameEntry) drawNameEntry(x);
		footer(x, "Up/Down: choose   Left/Right: change   A on Name: edit");
	}

	void drawHub(float x) {
		SolarSystem s = game.currentSystem();
		header(x, s.name);
		Ship ship = game.playerShip();
		text(s.techLevel().toXmlString(res) + " / " + s.politics().toXmlString(res), x + 12, 44, DIM);
		text("Fuel " + ship.getFuel() + "   Hull " + ship.hull + "   Bays " + ship.filledCargoBays() + "/" + ship.totalCargoBays() + "   Debt " + game.debt(), x + 12, 66, TEXT);
		for (int i = 0; i < HUB_ITEMS.length; i++) {
			row(x + 120, 96 + i * 22, 400, sel == i);
			text(HUB_ITEMS[i], x + 134, 96 + i * 22, TEXT);
		}
		footer(x, "Up/Down: choose    A / Enter: select");
	}

	void drawTrade(float x, boolean buying) {
		header(x, buying ? "Buy Cargo" : "Sell Cargo");
		text("Item", x + 20, 44, DIM);
		textRight(buying ? "Price" : "Sell for", x + 340, 44, DIM);
		textRight(buying ? "In stock" : "Paid", x + 450, 44, DIM);
		textRight("On ship", x + 580, 44, DIM);
		Ship ship = game.playerShip();
		SolarSystem s = game.currentSystem();
		TradeItem[] items = TradeItem.values();
		for (int i = 0; i < items.length; i++) {
			float y = 66 + i * 30;
			row(x + 10, y, 620, sel == i);
			text(items[i].toXmlString(res), x + 20, y, TEXT);
			int price = buying ? game.buyPriceOf(items[i]) : game.sellPriceOf(items[i]);
			textRight(price > 0 ? price + " cr." : "--", x + 340, y, price > 0 ? TEXT : DIM);
			textRight(buying ? String.valueOf(s.getQty(items[i])) : (game.buyingPrice.get(items[i]) > 0 ? game.buyingPrice.get(items[i]) + " cr." : "--"), x + 450, y, TEXT);
			textRight(String.valueOf(ship.getCargo(items[i])), x + 580, y, TEXT);
		}
		text("Cash " + game.credits() + " cr.    Bays " + ship.filledCargoBays() + "/" + ship.totalCargoBays(), x + 20, 66 + items.length * 30 + 8, GOLD);
		footer(x, buying ? "A: buy    B: back" : "A: sell    X: jettison    B: back");
	}

	void drawWarp(float x) {
		header(x, "Warp");
		SolarSystem cur = game.currentSystem();
		text("From " + cur.name + "   Fuel " + game.playerShip().getFuel(), x + 12, 44, DIM);
		List<SolarSystem> targets = warpTargets();
		int first = Math.max(0, Math.min(sel - 6, targets.size() - 12));
		for (int i = 0; i < 12 && first + i < targets.size(); i++) {
			SolarSystem t = targets.get(first + i);
			float y = 70 + i * 30;
			row(x + 10, y, 620, sel == first + i);
			text(t.name, x + 20, y, TEXT);
			textRight(game.hasWormholeTo(t) ? "wormhole" : GameState.realDistance(cur, t) + " pc", x + 260, y, TEXT);
			if (t.visited()) {
				text(t.techLevel().toXmlString(res), x + 280, y, DIM);
				text(t.politics().toXmlString(res), x + 440, y, DIM);
			} else {
				text("Not visited yet", x + 280, y, DIM);
			}
		}
		if (targets.isEmpty()) text("No systems within fuel range. Refuel first.", x + 20, 90, WARN);
		footer(x, "A: warp    Y: prices    B: back");
	}

	void drawStatus(float x) {
		header(x, "Commander Status");
		Ship ship = game.playerShip();
		float y = 50;
		String[] lines = {
			"Name: " + game.commanderMember().name + "   (" + game.difficulty().toXmlString(res) + ")",
			"Ship: " + ship.type.toXmlString(res) + "   Hull " + ship.hull + "   Fuel " + ship.getFuel(),
			"Skills: pilot " + ship.skill(com.brucelet.spacetrader.enumtypes.Skill.PILOT) + ", fighter " + ship.skill(com.brucelet.spacetrader.enumtypes.Skill.FIGHTER)
				+ ", trader " + ship.skill(com.brucelet.spacetrader.enumtypes.Skill.TRADER) + ", engineer " + ship.skill(com.brucelet.spacetrader.enumtypes.Skill.ENGINEER),
			"Credits: " + game.credits() + "   Debt: " + game.debt(),
			"Days: " + game.days(),
			"Police record score: " + game.policeRecord() + "   Reputation score: " + game.reputation(),
			"Kills: police " + game.policeKills + ", pirates " + game.pirateKills + ", traders " + game.traderKills,
		};
		for (String l : lines) { text(l, x + 20, y, TEXT); y += 28; }
		y += 8;
		text("Cargo", x + 20, y, GOLD); y += 26;
		for (TradeItem it : TradeItem.values())
			if (ship.getCargo(it) > 0) { text(it.toXmlString(res) + ": " + ship.getCargo(it), x + 36, y, TEXT); y += 24; }
		footer(x, "A / B: back");
	}

	List<String> sysInfoActions() {
		List<String> out = new ArrayList<>();
		if (game.ship == null) return out;
		if (game.specialAvailable) out.add("Special event");
		out.add("Read the newspaper");
		if (game.mercenaryForHire()) out.add("Personnel roster (mercenary for hire)");
		out.add("Back");
		return out;
	}

	void drawSysInfo(float x) {
		SolarSystem s = game.currentSystem();
		header(x, s.name);
		String[] lines = {
			"Size: " + s.size.toXmlString(res),
			"Tech level: " + s.techLevel().toXmlString(res),
			"Government: " + s.politics().toXmlString(res),
			"Resources: " + s.specialResources.toXmlString(res),
			"Status: " + s.status().toXmlString(res),
			"Police: " + s.politics().strengthPolice.toXmlString(res) + "   Pirates: " + s.politics().strengthPirates.toXmlString(res)
				+ "   Traders: " + s.politics().strengthTraders.toXmlString(res),
		};
		float y = 50;
		for (String l : lines) { text(l, x + 30, y, TEXT); y += 26; }
		List<String> acts = sysInfoActions();
		y += 14;
		for (int i = 0; i < acts.size(); i++) {
			row(x + 60, y + i * 30, 520, sel == i);
			text(acts.get(i), x + 74, y + i * 30, TEXT);
		}
		footer(x, "A: select    B: back");
	}

	void drawPersonnel(float x) {
		header(x, "Personnel");
		float y = 50;
		text("Crew", x + 16, y, GOLD);
		y += 30;
		String[] slots = { game.crewSlotText(1), game.crewSlotText(2) };
		for (int i = 0; i < 2; i++) {
			row(x + 10, y + i * 60, 620, sel == i);
			wrap((i + 1) + ". " + slots[i], x + 20, y + i * 60, 600, TEXT);
		}
		y += 130;
		text("For hire here", x + 16, y, GOLD);
		y += 30;
		String cand = game.hireCandidateText();
		row(x + 10, y, 620, sel == 2);
		wrap(cand == null ? "Nobody is looking for work here." : cand, x + 20, y, 600, cand == null ? DIM : TEXT);
		text("Cash " + game.credits() + " cr.", x + 16, y + 90, GOLD);
		footer(x, "A: fire / hire    B: back");
	}

	void drawLines(float x, String title, List<String> lines) {
		header(x, title);
		float y = 50;
		for (String l : lines) y += wrap("- " + l, x + 20, y, VW - 40, TEXT) + 10;
		footer(x, "A / B: back");
	}

	void drawNewspaper(float x) {
		float bx = x + 40, bw = VW - 80, by = 30, bh = VH - 60;
		rect(bx, by, bw, bh, new Color(0.92f, 0.90f, 0.80f, 1));
		Color ink = new Color(0.1f, 0.1f, 0.1f, 1);
		float y = 40;
		y += wrap(newsTitle, bx + 16, y, bw - 32, new Color(0.5f, 0.1f, 0.1f, 1)) + 12;
		for (String h : newsLines) y += wrap("* " + h, bx + 16, y, bw - 32, ink) + 8;
		text("A / B: close", bx + 16, VH - 52, new Color(0.3f, 0.3f, 0.3f, 1));
	}

	// ---- equipment helpers ----

	List<Purchasable> equipmentForSale() {
		List<Purchasable> out = new ArrayList<>();
		if (game.ship == null) return out;
		out.addAll(Arrays.asList(Weapon.buyableValues()));
		out.addAll(Arrays.asList(Shield.buyableValues()));
		out.addAll(Arrays.asList(Gadget.buyableValues()));
		return out;
	}

	/** Installed equipment as {kind, slot}; kind 0 = weapon, 1 = shield, 2 = gadget. */
	List<int[]> installedEquipment() {
		List<int[]> out = new ArrayList<>();
		Ship sh = game.ship;
		if (sh == null) return out;
		for (int i = 0; i < sh.weapon.length; i++) if (sh.weapon[i] != null) out.add(new int[] { 0, i });
		for (int i = 0; i < sh.shield.length; i++) if (sh.shield[i] != null) out.add(new int[] { 1, i });
		for (int i = 0; i < sh.gadget.length; i++) if (sh.gadget[i] != null) out.add(new int[] { 2, i });
		return out;
	}

	int count(Object[] arr) {
		int n = 0;
		for (Object o : arr) if (o != null) n++;
		return n;
	}

	void drawYard(float x) {
		header(x, "Ship Yard");
		Ship sh = game.playerShip();
		int tank = (sh.getFuelTanks() - sh.getFuel()) * sh.type.costOfFuel;
		int repair = (sh.getHullStrength() - sh.hull) * sh.type.repairCosts;
		text(res.getQuantityString(R.plurals.screen_yard_range, sh.getFuel(), sh.getFuel()), x + 16, 44, TEXT);
		text(sh.getFuel() < sh.getFuelTanks() ? str(R.string.screen_yard_tank, tank) : str(R.string.screen_yard_fulltank), x + 16, 68, DIM);
		text(str(R.string.screen_yard_hull, (sh.hull * 100) / sh.getHullStrength()), x + 16, 92, TEXT);
		text(sh.hull < sh.getHullStrength() ? str(R.string.screen_yard_repair, repair) : str(R.string.screen_yard_norepair), x + 16, 116, DIM);
		boolean shipsHere = game.currentSystem().techLevel().compareTo(ShipType.FLEA.minTechLevel) >= 0;
		String[] items = {
			"Buy fuel...", "Fill the tank", "Repair hull...", "Repair fully",
			shipsHere ? "Buy a new ship" : "No ships built here",
			game.hasEscapePod() ? "You have an escape pod" : (game.canBuyPod() ? "Buy an escape pod (2000 cr.)" : "Escape pod unavailable"),
		};
		boolean[] enabled = {
			sh.getFuel() < sh.getFuelTanks(), sh.getFuel() < sh.getFuelTanks(),
			sh.hull < sh.getHullStrength(), sh.hull < sh.getHullStrength(),
			shipsHere, game.canBuyPod(),
		};
		for (int i = 0; i < items.length; i++) {
			row(x + 60, 150 + i * 30, 520, sel == i);
			text(items[i], x + 74, 150 + i * 30, enabled[i] ? TEXT : DIM);
		}
		text("Cash " + game.credits() + " cr.", x + 16, 150 + items.length * 30 + 10, GOLD);
		footer(x, "A: select    B: back");
	}

	void drawBuyEq(float x) {
		header(x, "Buy Equipment");
		List<Purchasable> items = equipmentForSale();
		for (int i = 0; i < items.size(); i++) {
			Purchasable it = items.get(i);
			int price = game.equipmentBuyPrice(it);
			float y = 50 + i * 30;
			row(x + 10, y, 620, sel == i);
			text(((XmlString) it).toXmlString(res), x + 20, y, price > 0 ? TEXT : DIM);
			textRight(price > 0 ? price + " cr." : "not sold", x + 480, y, price > 0 ? TEXT : DIM);
		}
		Ship sh = game.playerShip();
		float y = 50 + items.size() * 30 + 6;
		text("Slots: weapons " + count(sh.weapon) + "/" + sh.type.weaponSlots + "   shields " + count(sh.shield) + "/" + sh.type.shieldSlots
				+ "   gadgets " + count(sh.gadget) + "/" + sh.type.gadgetSlots, x + 20, y, DIM);
		text("Cash " + game.credits() + " cr.", x + 20, y + 26, GOLD);
		footer(x, "A: buy    B: back");
	}

	void drawSellEq(float x) {
		header(x, "Sell Equipment");
		List<int[]> items = installedEquipment();
		Ship sh = game.playerShip();
		if (items.isEmpty()) text("You have no equipment to sell.", x + 20, 60, DIM);
		for (int i = 0; i < items.size(); i++) {
			int[] e = items.get(i);
			Purchasable it = e[0] == 0 ? sh.weapon[e[1]] : e[0] == 1 ? sh.shield[e[1]] : sh.gadget[e[1]];
			float y = 50 + i * 30;
			row(x + 10, y, 620, sel == i);
			text(((XmlString) it).toXmlString(res), x + 20, y, TEXT);
			textRight(it.sellPrice() + " cr.", x + 480, y, TEXT);
		}
		footer(x, "A: sell    B: back");
	}

	void drawBank(float x) {
		header(x, "Bank");
		Ship sh = game.playerShip();
		text("Debt: " + game.debt() + " cr.     Maximum loan: " + game.maxLoanAmount() + " cr.", x + 16, 44, TEXT);
		text("Ship insurance value: " + sh.currentPriceWithoutCargo(true) + " cr.", x + 16, 68, DIM);
		text("No-claim bonus: " + Math.min(game.noClaimDays(), 90) + "%    Daily premium: " + game.insuranceMoney() + " cr.", x + 16, 92, DIM);
		String[] items = { "Get a loan...", "Pay back...", game.hasInsurance() ? "Stop insurance" : "Buy insurance" };
		for (int i = 0; i < items.length; i++) {
			row(x + 120, 140 + i * 30, 400, sel == i);
			text(items[i], x + 134, 140 + i * 30, (i == 1 && game.debt() <= 0) ? DIM : TEXT);
		}
		text("Cash " + game.credits() + " cr.", x + 16, 140 + items.length * 30 + 20, GOLD);
		footer(x, "A: select    B: back");
	}

	void drawBuyShip(float x) {
		header(x, "Buy Ship");
		ShipType[] types = ShipType.buyableValues();
		for (int i = 0; i < types.length; i++) {
			int price = game.shipPriceOf(types[i]);
			boolean have = game.playerShip().type == types[i];
			float y = 50 + i * 30;
			row(x + 10, y, 620, sel == i);
			text(types[i].toXmlString(res), x + 20, y, price == 0 ? DIM : TEXT);
			textRight(price == 0 ? "not sold" : have ? "you have one" : price + " cr.", x + 400, y, price == 0 || have ? DIM : TEXT);
			text(types[i].cargoBays + " bays", x + 430, y, DIM);
		}
		text("Cash " + game.credits() + " cr.", x + 20, 50 + types.length * 30 + 10, GOLD);
		footer(x, "A: buy    X: ship info    B: back");
	}

	void drawShipInfo(float x) {
		ShipType t = game.selectedShip();
		if (t == null) return;
		float bx = x + 100, bw = VW - 200, by = 60, bh = 340;
		rect(bx, VH - by - bh, bw, bh, PANEL);
		text(t.toXmlString(res), bx + 16, by + 12, GOLD);
		String[] lines = {
			"Size: " + t.size.toXmlString(res),
			"Cargo bays: " + t.cargoBays,
			"Weapon slots: " + t.weaponSlots,
			"Shield slots: " + t.shieldSlots,
			"Gadget slots: " + t.gadgetSlots,
			"Crew quarters: " + t.crewQuarters,
			"Range: " + t.fuelTanks + " parsecs",
			"Hull strength: " + t.hullStrength,
			"Base price: " + t.price + " cr.",
		};
		for (int i = 0; i < lines.length; i++) text(lines[i], bx + 24, by + 44 + i * 26, TEXT);
		String pic = drawableName(t.drawableId);
		if (pic != null) image(pic + "_base", bx + bw - 210, by + 60, 190, false);
		text("A / B: close", bx + 16, by + bh - 28, DIM);
	}

	// ---- galactic chart ----

	SolarSystem chartCursor;
	SolarSystem chartTarget; // non-null while the target info panel is open

	/** Screen position of a system: the 150x110 galaxy is scaled to fit the content area. */
	float chartX(float x, SolarSystem s) { return x + 30 + s.x() * 3.9f; }

	float chartY(SolarSystem s) { return 52 + s.y() * 3.55f; }

	void enterChart() {
		mode = Mode.CHART;
		chartTarget = null;
		if (chartCursor == null || game.ship == null) chartCursor = game.currentSystem();
	}

	/** Move the chart cursor to the nearest system lying in the pressed direction. */
	void chartMove(int dx, int dy) {
		SolarSystem best = null;
		float bestScore = Float.MAX_VALUE;
		for (SolarSystem s : game.solarSystems()) {
			if (s == chartCursor) continue;
			float ddx = s.x() - chartCursor.x(), ddy = s.y() - chartCursor.y();
			float along = ddx * dx + ddy * dy;
			if (along <= 0) continue;
			float across = Math.abs(ddx * dy) + Math.abs(ddy * dx);
			float score = along + across * 2.2f;
			if (score < bestScore) { bestScore = score; best = s; }
		}
		if (best != null) chartCursor = best;
	}

	void chartKey(boolean ok, boolean back, boolean alt) {
		if (chartTarget != null) {
			if (ok && game.reachable(chartTarget)) { SolarSystem t = chartTarget; chartTarget = null; game.warpTo(t, false); }
			else if (alt) { game.trackSystem(chartTarget); }
			else if (kAlt2) openPrices(chartTarget, mode);
			else if (back || ok) chartTarget = null;
			return;
		}
		if (kLeft) chartMove(-1, 0);
		if (kRight) chartMove(1, 0);
		if (kUp) chartMove(0, -1);
		if (kDown) chartMove(0, 1);
		if (ok) chartTarget = chartCursor;
		if (alt) game.trackSystem(chartCursor);
		if (kAlt2) openPrices(chartCursor, Mode.CHART);
		if (back) { mode = Mode.HUB; sel = 0; }
	}

	void drawChart(float x) {
		header(x, "Galactic Chart");
		SolarSystem cur = game.currentSystem();
		for (SolarSystem s : game.solarSystems()) {
			boolean inRange = game.reachable(s);
			Color c = s == cur ? GOLD : inRange ? new Color(0.4f, 0.9f, 0.5f, 1) : s.visited() ? new Color(0.5f, 0.6f, 0.9f, 1) : new Color(0.35f, 0.38f, 0.5f, 1);
			float sz = s == cur ? 6 : 4;
			rect(chartX(x, s) - sz / 2, VH - chartY(s) - sz / 2, sz, sz, c);
		}
		for (SolarSystem w : game.wormholes()) if (w != null) rect(chartX(x, w) - 4, VH - chartY(w) - 4, 8, 8, new Color(0.9f, 0.4f, 0.9f, 1));
		SolarSystem tr = game.trackedSystem();
		if (tr != null) rect(chartX(x, tr) - 6, VH - chartY(tr) - 6, 12, 2, WARN);
		SolarSystem c = chartCursor;
		if (c != null) {
			float cx = chartX(x, c), cy = VH - chartY(c);
			rect(cx - 9, cy - 9, 18, 1, Color.WHITE);
			rect(cx - 9, cy + 8, 18, 1, Color.WHITE);
			rect(cx - 9, cy - 9, 1, 18, Color.WHITE);
			rect(cx + 8, cy - 9, 1, 18, Color.WHITE);
			String label = c.name + (game.hasWormholeTo(c) ? "  (wormhole)" : game.inRange(c) ? "  (" + GameState.realDistance(cur, c) + " pc)" : "");
			text(label, x + 12, 42, TEXT);
		}
		if (chartTarget != null) drawChartTarget(x, cur);
		footer(x, chartTarget != null ? "A: warp    X: track    Y: prices    B: close" : "Arrows: move    A: details    X: track    Y: prices    B: back");
	}

	void drawChartTarget(float x, SolarSystem cur) {
		SolarSystem t = chartTarget;
		float bx = x + 120, bw = VW - 240, by = 130, bh = 170;
		rect(bx, VH - by - bh, bw, bh, PANEL);
		text(t.name, bx + 16, by + 12, GOLD);
		int dist = GameState.realDistance(cur, t);
		String[] lines = {
			"Distance: " + dist + " parsecs",
			game.hasWormholeTo(t) ? "Wormhole: no fuel needed, tax " + game.wormholeTaxTo(t) + " cr." : game.inRange(t) ? "Within fuel range" : "Out of fuel range (" + game.fuelRange() + " pc)",
			t.visited() ? "Tech level: " + t.techLevel().toXmlString(res) : "Not visited yet",
			t.visited() ? "Government: " + t.politics().toXmlString(res) : "",
			t.visited() ? "Size: " + t.size.toXmlString(res) : "",
			game.trackedSystem() == t ? "Tracked" : "",
		};
		float y = by + 46;
		for (String l : lines) { if (!l.isEmpty()) { text(l, bx + 24, y, TEXT); y += 26; } }
	}

	// ---- options ----

	Mode optionsReturn = Mode.TITLE;

	void drawOptions(float x) {
		header(x, "Options");
		int n = game.optionCount();
		int first = Math.max(0, Math.min(sel - 5, n - 11));
		for (int i = 0; i < 11 && first + i < n; i++) {
			int idx = first + i;
			float y = 46 + i * 34;
			row(x + 10, y, 620, sel == idx);
			text(game.optionLabel(idx), x + 20, y, TEXT);
			textRight(game.optionValue(idx), x + 610, y, GOLD);
		}
		footer(x, "A: change    B: back");
	}

	void drawHighScores(float x) {
		float bx = x + 40, bw = VW - 80, by = 40, bh = 300;
		rect(bx, VH - by - bh, bw, bh, PANEL);
		text("High Scores", bx + 16, by + 12, GOLD);
		float y = by + 46;
		for (String l : game.highScoreLines()) y += wrap(l, bx + 20, y, bw - 40, TEXT) + 6;
		text("A / B: close", bx + 16, by + bh - 28, DIM);
	}

	// ---- sprites ----

	final java.util.Map<String, Texture> textures = new java.util.HashMap<>();
	java.util.Map<Integer, String> drawableNames;

	Texture sprite(String name) {
		if (textures.containsKey(name)) return textures.get(name);
		Texture t = null;
		com.badlogic.gdx.files.FileHandle fh = Gdx.files.classpath("sprites/" + name + ".png");
		if (!fh.exists()) fh = Gdx.files.classpath("sprites/" + name + ".jpg");
		if (fh.exists()) {
			t = new Texture(fh);
			t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		}
		textures.put(name, t);
		return t;
	}

	/** Name of an R.drawable entry, e.g. R.drawable.gnat -> "gnat". */
	String drawableName(int id) {
		if (drawableNames == null) {
			drawableNames = new java.util.HashMap<>();
			for (java.lang.reflect.Field f : R.drawable.class.getFields()) {
				try { drawableNames.put(f.getInt(null), f.getName()); } catch (IllegalAccessException ignored) {}
			}
		}
		return drawableNames.get(id);
	}

	/** Draws an image scaled to the given width with its top-left corner at (x, topY); returns the drawn height. */
	float image(String name, float x, float topY, float w, boolean flip) {
		Texture t = name == null ? null : sprite(name);
		if (t == null) return 0;
		float h = w * t.getHeight() / t.getWidth();
		batch.setColor(Color.WHITE);
		batch.draw(t, x, VH - topY - h, w, h, 0, 0, t.getWidth(), t.getHeight(), flip, false);
		return h;
	}

	/** Draws a ship with its damage and shield overlays; returns the drawn height. */
	float drawShipSprite(Ship sh, float x, float topY, float w, boolean flip) {
		String n = drawableName(sh.type.drawableId);
		Texture base = n == null ? null : sprite(n + "_base");
		if (base == null) return 0;
		float h = w * base.getHeight() / base.getWidth();
		float y = VH - topY - h;
		batch.setColor(Color.WHITE);
		batch.draw(base, x, y, w, h, 0, 0, base.getWidth(), base.getHeight(), flip, false);

		int hullPc = sh.hull * 100 / sh.getHullStrength();
		int shieldPc = sh.totalShields() > 0 ? sh.totalShieldStrength() * 100 / sh.totalShields() : 0;
		int damageLevel = hullPc <= 0 ? 10000 : hullPc >= 100 ? 0 : sh.type.damageMin + (100 - hullPc) * (sh.type.damageMax - sh.type.damageMin) / 100;
		int shieldLevel = shieldPc >= 100 ? 10000 : shieldPc <= 0 ? 0 : sh.type.shieldMin + shieldPc * (sh.type.shieldMax - sh.type.shieldMin) / 100;

		Texture dmg = sprite(n + "_damage");
		if (dmg != null && damageLevel > 0) {
			float f = damageLevel / 10000f;
			int sw = Math.max(1, (int) (dmg.getWidth() * f));
			// the damage layer is revealed from the left edge of the (unflipped) image
			batch.draw(dmg, flip ? x + w * (1 - f) : x, y, w * f, h, 0, 0, sw, dmg.getHeight(), flip, false);
		}
		Texture shd = sprite(n + "_shield");
		if (shd != null && shieldLevel > 0) {
			float f = shieldLevel / 10000f;
			int sw = Math.max(1, (int) (shd.getWidth() * f));
			// the shield layer is revealed from the right edge of the (unflipped) image
			batch.draw(shd, flip ? x : x + w * (1 - f), y, w * f, h, shd.getWidth() - sw, 0, sw, shd.getHeight(), flip, false);
		}
		return h;
	}

	void drawEncounter(float x) {
		header(x, "Encounter");
		float y = 42;
		y += wrap(encDescription, x + 16, y, VW - 32, TEXT) + 6;
		y += wrap(encAction, x + 16, y, VW - 32, WARN) + 8;
		Ship me = game.playerShip(), foe = game.opponentShip();
		if (me != null && foe != null) {
			float top = Math.max(y, 112);
			float sw = 220;
			float h1 = drawShipSprite(me, x + 30, top, sw, false);
			float h2 = drawShipSprite(foe, x + VW - 30 - sw, top, sw, true);
			float hh = Math.max(h1, h2);
			if (hh == 0) hh = 20;
			// opponent type icon (police shield etc.) over the top-right corner of the opponent's ship, as on Android
			try {
				Opponent op = game.encounterOpponentType();
				String icon = op == null ? null : drawableName(op.iconId);
				if (icon != null && !icon.equals("blankicon")) image(icon, x + VW - 30 - 44 + 8, top - 12, 44, false);
			} catch (RuntimeException ignored) {}
			float ty = top + hh + 4;
			int mp = me.hull * 100 / me.getHullStrength(), fp = foe.hull * 100 / foe.getHullStrength();
			text(me.type.toXmlString(res) + "  hull " + Math.max(0, mp) + "%", x + 30, ty, TEXT);
			textRight(foe.type.toXmlString(res) + "  hull " + Math.max(0, fp) + "%", x + VW - 30, ty, TEXT);
			y = ty + 26;
		}
		for (int i = 0; i < encButtons.size(); i++) {
			row(x + 120, y + i * 26, 400, sel == i);
			text(encButtons.get(i).toXmlString(res), x + 134, y + i * 26, TEXT);
		}
		footer(x, "Up/Down: choose    A / Enter: do it");
	}

	// ---- help (converted from the Android app's help page; see tools/build_help.py) ----

	static class HelpTopic {
		String title;
		final List<String> paragraphs = new ArrayList<>();
		final List<HelpTopic> subs = new ArrayList<>();
	}

	List<HelpTopic> helpTopics;
	HelpTopic helpSection, helpPage;
	int helpSectionIndex;
	Mode helpReturn = Mode.TITLE;
	float helpScroll, helpTotal;
	List<GlyphLayout> helpLayouts;
	int helpLayoutFont = -1;

	List<HelpTopic> helpTopics() {
		if (helpTopics == null) {
			helpTopics = new ArrayList<>();
			HelpTopic sec = null, sub = null;
			for (String line : Gdx.files.classpath("help.txt").readString("UTF-8").split("\n")) {
				if (line.startsWith("#S ")) {
					sec = new HelpTopic();
					sec.title = line.substring(3);
					helpTopics.add(sec);
					sub = null;
				} else if (line.startsWith("#T ") && sec != null) {
					sub = new HelpTopic();
					sub.title = line.substring(3);
					sec.subs.add(sub);
				} else if (!line.trim().isEmpty() && sec != null) {
					(sub != null ? sub : sec).paragraphs.add(line.trim());
				}
			}
		}
		return helpTopics;
	}

	/** Entries of the second-level list: an overview page (if the section has intro text) then the sub-sections. */
	List<HelpTopic> helpEntries() {
		List<HelpTopic> out = new ArrayList<>();
		if (helpSection == null) return out;
		if (!helpSection.paragraphs.isEmpty()) {
			HelpTopic overview = new HelpTopic();
			overview.title = "Overview";
			overview.paragraphs.addAll(helpSection.paragraphs);
			out.add(overview);
		}
		out.addAll(helpSection.subs);
		return out;
	}

	void openHelp(Mode returnTo) {
		helpReturn = returnTo;
		mode = Mode.HELP;
		sel = 0;
	}

	void openHelpPage(HelpTopic page) {
		helpPage = page;
		helpScroll = 0;
		helpLayouts = null;
		mode = Mode.HELPPAGE;
	}

	void helpKey(boolean left, boolean right, boolean ok, boolean back) {
		switch (mode) {
			case HELP: {
				if (back) { mode = helpReturn; sel = 0; break; }
				if (!ok) break;
				helpSectionIndex = sel;
				helpSection = helpTopics().get(sel);
				if (helpSection.subs.isEmpty()) openHelpPage(helpSection);
				else { mode = Mode.HELPSUB; sel = 0; }
				break;
			}
			case HELPSUB: {
				if (back) { mode = Mode.HELP; sel = helpSectionIndex; break; }
				List<HelpTopic> entries = helpEntries();
				if (ok && sel < entries.size()) openHelpPage(entries.get(sel));
				break;
			}
			case HELPPAGE: {
				float view = VH - 44 - 32;
				if (kUp) helpScroll -= 30;
				if (kDown) helpScroll += 30;
				if (left) helpScroll -= view * 0.85f;
				if (right) helpScroll += view * 0.85f;
				helpScroll = Math.max(0, Math.min(helpScroll, Math.max(0, helpTotal - view)));
				if (back || ok) {
					if (helpSection != null && !helpSection.subs.isEmpty()) { mode = Mode.HELPSUB; }
					else { mode = Mode.HELP; sel = helpSectionIndex; }
				}
				break;
			}
			default: break;
		}
	}

	void drawHelpList(float x, String title, List<String> items, String hint) {
		header(x, title);
		int per = 12;
		int first = Math.max(0, Math.min(sel - per / 2, items.size() - per));
		for (int i = 0; i < per && first + i < items.size(); i++) {
			float y = 46 + i * 32;
			row(x + 40, y, 560, sel == first + i);
			text(items.get(first + i), x + 54, y, TEXT);
		}
		if (items.size() > per) textRight((sel + 1) + " / " + items.size(), x + VW - 16, 8 + 24, DIM);
		footer(x, hint);
	}

	void drawHelpPage(float x) {
		float width = VW - 56;
		if (helpLayouts == null || helpLayoutFont != fontPixelHeight) {
			helpLayoutFont = fontPixelHeight;
			helpLayouts = new ArrayList<>();
			helpTotal = 0;
			for (String para : helpPage.paragraphs) {
				GlyphLayout gl = new GlyphLayout(font, para, TEXT, width, com.badlogic.gdx.utils.Align.left, true);
				helpLayouts.add(gl);
				helpTotal += gl.height + 12;
			}
			helpTotal += 8;
		}
		float view = VH - 44 - 32;
		helpScroll = Math.max(0, Math.min(helpScroll, Math.max(0, helpTotal - view)));
		float y = 48 - helpScroll;
		for (GlyphLayout gl : helpLayouts) {
			if (y + gl.height > 30 && y < VH - 28) font.draw(batch, gl, x + 28, VH - y);
			y += gl.height + 12;
		}
		// cover text that scrolled under the header and footer bars
		rect(x, VH - 40, VW, 40, BG);
		rect(x, 0, VW, 30, BG);
		header(x, helpPage.title);
		if (helpTotal > view) {
			float bar = Math.max(24, view * view / helpTotal);
			float pos = (view - bar) * (helpScroll / (helpTotal - view));
			rect(x + VW - 8, VH - 44 - pos - bar, 4, bar, DIM);
		}
		footer(x, helpTotal > view ? "Up/Down: scroll    Left/Right: page    B: back" : "B: back");
	}

	// ---- short range chart: zoomed in on the current system (see the original drawShortRange) ----

	static final float SR_RANGE = 20f;      // systems within +-20 chart units are shown, as in the original
	static final float SR_SIZE = 410f;      // chart square in screen units
	static final float SR_DOT = 5.5f;       // system dot radius
	static final float SR_WORMHOLE_DX = 13f; // wormhole marker sits to the right of its system

	static class SrNode {
		SolarSystem system;
		boolean wormhole;
		float px, py; // screen position, top-based
	}

	SolarSystem srCursor;
	boolean srCursorWormhole;

	float srCenterX() { return left() + 10 + SR_SIZE / 2f; }

	float srCenterY() { return 40 + SR_SIZE / 2f; }

	float srScale() { return SR_SIZE / (SR_RANGE * 2 + 4); }

	void enterShortChart() {
		mode = Mode.SHORT;
		chartTarget = null;
		srCursor = game.currentSystem();
		srCursorWormhole = false;
	}

	List<SrNode> srNodes() {
		List<SrNode> out = new ArrayList<>();
		SolarSystem cur = game.currentSystem();
		float sc = srScale();
		for (SolarSystem s : game.solarSystems()) {
			if (Math.abs(s.x() - cur.x()) > SR_RANGE || Math.abs(s.y() - cur.y()) > SR_RANGE) continue;
			SrNode n = new SrNode();
			n.system = s;
			n.px = srCenterX() + (s.x() - cur.x()) * sc;
			n.py = srCenterY() + (s.y() - cur.y()) * sc;
			out.add(n);
			if (game.hasWormhole(s)) {
				SrNode w = new SrNode();
				w.system = s;
				w.wormhole = true;
				w.px = n.px + SR_WORMHOLE_DX;
				w.py = n.py;
				out.add(w);
			}
		}
		return out;
	}

	/** Move the cursor to the nearest marker lying in the pressed direction. */
	void srMove(int dx, int dy) {
		SrNode from = null;
		List<SrNode> nodes = srNodes();
		for (SrNode n : nodes) if (n.system == srCursor && n.wormhole == srCursorWormhole) from = n;
		if (from == null) return;
		SrNode best = null;
		float bestScore = Float.MAX_VALUE;
		for (SrNode n : nodes) {
			if (n == from) continue;
			float ddx = n.px - from.px, ddy = n.py - from.py;
			float along = ddx * dx + ddy * dy;
			if (along <= 0) continue;
			float across = Math.abs(ddx * dy) + Math.abs(ddy * dx);
			float score = along + across * 2f;
			if (score < bestScore) { bestScore = score; best = n; }
		}
		if (best != null) { srCursor = best.system; srCursorWormhole = best.wormhole; }
	}

	void shortKey(boolean ok, boolean back, boolean alt) {
		if (chartTarget != null) { chartKey(ok, back, alt); return; } // shares the target panel with the galaxy chart
		if (kLeft) srMove(-1, 0);
		if (kRight) srMove(1, 0);
		if (kUp) srMove(0, -1);
		if (kDown) srMove(0, 1);
		if (ok) {
			SolarSystem cur = game.currentSystem();
			if (!srCursorWormhole) chartTarget = srCursor;
			else {
				SolarSystem partner = game.wormholePartner(srCursor);
				if (srCursor == cur) chartTarget = partner;
				else showDialog(SimpleDialog.newInstance(R.string.screen_warp_unreachable_title, R.string.screen_warp_unreachable_message,
						R.string.help_wormholeoutofrange, partner, srCursor));
			}
		}
		if (alt) game.trackSystem(srCursor);
		if (kAlt2) openPrices(srCursorWormhole ? game.wormholePartner(srCursor) : srCursor, Mode.SHORT);
		if (back) { mode = Mode.HUB; sel = 0; }
	}

	// ---- little drawing helpers for the chart (positions are top-based, like text()) ----

	void rectT(float x, float topY, float w, float h, Color c) { rect(x, VH - topY - h, w, h, c); }

	void dot(float cx, float cy, float r, Color c) {
		int ri = Math.round(r);
		for (int dy = -ri; dy <= ri; dy++) {
			float half = (float) Math.sqrt(Math.max(0, r * r - dy * dy));
			rectT(cx - half, cy + dy, half * 2, 1, c);
		}
	}

	void line(float x1, float y1, float x2, float y2, Color c) {
		int steps = Math.max(1, (int) Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
		for (int i = 0; i <= steps; i++) {
			float t = i / (float) steps;
			rectT(x1 + (x2 - x1) * t - 0.75f, y1 + (y2 - y1) * t - 0.75f, 1.5f, 1.5f, c);
		}
	}

	void centerText(String s, float cx, float topY, Color c) {
		layout.setText(font, s);
		font.setColor(c);
		font.draw(batch, s, cx - layout.width / 2f, VH - topY);
	}

	void drawShortChart(float x) {
		header(x, "Short Range Chart");
		SolarSystem cur = game.currentSystem();
		float sc = srScale(), cx = srCenterX(), cy = srCenterY();
		float left = cx - SR_SIZE / 2f, top = cy - SR_SIZE / 2f;
		rectT(left, top, SR_SIZE, SR_SIZE, new Color(0.10f, 0.11f, 0.16f, 1));

		// maximum range circle
		int fuel = game.fuelRange();
		Color ring = new Color(0.75f, 0.78f, 0.9f, 1);
		if (fuel > 0) {
			float r = fuel * sc;
			for (int a = 0; a < 360; a += 1) {
				double rad = Math.toRadians(a);
				float px = cx + (float) Math.cos(rad) * r, py = cy + (float) Math.sin(rad) * r;
				if (px > left && px < left + SR_SIZE && py > top && py < top + SR_SIZE) rectT(px - 0.75f, py - 0.75f, 1.5f, 1.5f, ring);
			}
		}

		// arrow toward the tracked system
		SolarSystem tr = game.trackedSystem();
		if (tr != null) {
			float dxu = tr.x() - cur.x(), dyu = tr.y() - cur.y();
			float len = (float) Math.hypot(dxu, dyu);
			if (len > 0) {
				float ux = dxu / len, uy = dyu / len;
				float tipX = cx + ux * 37, tipY = cy + uy * 37, r = SR_DOT + 0.5f;
				line(cx - uy * r, cy + ux * r, tipX, tipY, WARN);
				line(tipX, tipY, cx + uy * r, cy - ux * r, WARN);
			}
		}

		List<SrNode> nodes = srNodes();
		// names first, then the dots, so every system stays visible
		for (SrNode n : nodes) {
			if (n.wormhole) continue;
			centerText(n.system.name, n.px, n.py - 26, n.system == cur ? GOLD : TEXT);
		}
		Color green = new Color(0.30f, 0.80f, 0.15f, 1), blue = new Color(0.25f, 0.55f, 1f, 1);
		Color orange = new Color(0.95f, 0.6f, 0.1f, 1), red = new Color(0.8f, 0.15f, 0.05f, 1);
		for (SrNode n : nodes) {
			if (n.wormhole) { dot(n.px, n.py, SR_DOT, orange); dot(n.px, n.py, SR_DOT * 0.45f, red); }
			else dot(n.px, n.py, SR_DOT, n.system.visited() ? blue : green);
		}
		// crosshair on your own system
		line(cx - 11, cy, cx + 11, cy, Color.WHITE);
		line(cx, cy - 11, cx, cy + 11, Color.WHITE);
		// cursor
		for (SrNode n : nodes) {
			if (n.system == srCursor && n.wormhole == srCursorWormhole) {
				float h = SR_DOT + 5;
				rectT(n.px - h, n.py - h, h * 2, 1.5f, Color.WHITE);
				rectT(n.px - h, n.py + h, h * 2, 1.5f, Color.WHITE);
				rectT(n.px - h, n.py - h, 1.5f, h * 2, Color.WHITE);
				rectT(n.px + h, n.py - h, 1.5f, h * 2 + 1.5f, Color.WHITE);
			}
		}

		// side panel
		float px = left + SR_SIZE + 14;
		float y = 46;
		text("You are at", px, y, DIM); y += 20;
		text(cur.name, px, y, GOLD); y += 30;
		text("Fuel range", px, y, DIM); y += 20;
		text(fuel + " parsecs", px, y, TEXT); y += 30;
		if (srCursor != null) {
			text(srCursorWormhole ? "Wormhole at" : "Selected", px, y, DIM); y += 20;
			text(srCursor.name, px, y, TEXT); y += 20;
			if (!srCursorWormhole && srCursor != cur)
				text(GameState.realDistance(cur, srCursor) + " pc" + (game.inRange(srCursor) ? "" : "  (too far)"), px, y, DIM);
			y += 30;
		}
		if (tr != null && game.showTrackedRange) {
			text("Tracking", px, y, DIM); y += 20;
			text(tr.name, px, y, WARN); y += 20;
			text(GameState.realDistance(cur, tr) + " pc away", px, y, DIM);
		}
		if (chartTarget != null) drawChartTarget(x, cur);
		footer(x, chartTarget != null ? "A: warp    X: track    Y: prices    B: close" : "Arrows: move    A: details    X: track    Y: prices    B: back");
	}

	// ---- average price list (see the original showAveragePrices) ----

	Mode apReturn = Mode.SHORT;
	SolarSystem apSystem;

	void openPrices(SolarSystem s, Mode returnTo) {
		if (s == null) return;
		apSystem = s;
		apReturn = returnTo;
		mode = Mode.AVGPRICES;
	}

	void pricesKey(boolean left, boolean right, boolean ok, boolean back) {
		if (right || left) {
			SolarSystem next = game.nextSystemWithinRange(apSystem, left);
			if (next != null) apSystem = next;
		}
		if (ok || back) mode = apReturn;
	}

	void drawPrices(float x) {
		SolarSystem s = apSystem;
		SolarSystem cur = game.currentSystem();
		header(x, "Average Prices: " + s.name);
		text(s.visited() ? s.specialResources.toXmlString(res) : str(R.string.specialresources_unknown), x + 16, 44, DIM);
		text("Bays " + game.playerShip().filledCargoBays() + "/" + game.playerShip().totalCargoBays(), x + 440, 44, DIM);
		float y = 70;
		text("Good", x + 20, y, DIM);
		textRight("Buy here", x + 300, y, DIM);
		textRight("Sells for", x + 400, y, DIM);
		textRight("Difference", x + 510, y, DIM);
		textRight("On ship", x + 610, y, DIM);
		Ship ship = game.playerShip();
		TradeItem[] items = TradeItem.values();
		for (int i = 0; i < items.length; i++) {
			float ry = 94 + i * 30;
			int price = game.averagePrice(items[i], s);
			int here = game.buyPriceOf(items[i]);
			boolean profit = game.priceIsProfit(items[i], s);
			Color c = profit ? GOLD : TEXT;
			if (profit) rect(x + 10, VH - ry - 21, 620, 22, new Color(0.16f, 0.20f, 0.10f, 1));
			text(items[i].toXmlString(res), x + 20, ry, c);
			textRight(here > 0 ? here + " cr." : "--", x + 300, ry, here > 0 ? c : DIM);
			textRight(price > 0 ? price + " cr." : "--", x + 400, ry, price > 0 ? c : DIM);
			boolean diffOk = price > 0 && here > 0;
			textRight(diffOk ? String.format("%+d cr.", price - here) : "--", x + 510, ry, diffOk ? c : DIM);
			textRight(String.valueOf(ship.getCargo(items[i])), x + 610, ry, TEXT);
		}
		text(game.pricesHidden(s) ? "Not visited yet: prices are hidden on this difficulty until you visit." : "Cash " + game.credits() + " cr.    Gold rows: sells for more there than it costs here", x + 20, 94 + items.length * 30 + 6, DIM);
		footer(x, "Left/Right: other systems    A / B: back");
	}

	void drawEnd(float x) {
		header(x, "Game Over");
		if (game.endStatus() != null) {
			// the original artwork is a tall picture with its own "YOU ARE DESTROYED" / "OK" lettering
			float imgH = image(drawableName(game.endStatus().imageId), x + 24, 40, 200, false);
			float tx = x + 250, tw = VW - 250 - 20;
			String name = game.commanderMember() != null ? game.commanderMember().name : "";
			int days = game.days();
			String status = game.endStatus().toXmlString(res);
			String summary = res.getQuantityString(R.plurals.dialog_highscores_description, days,
					status, days, game.currentWorth(), game.difficulty().toXmlString(res).toLowerCase());
			float y = 70;
			y += wrap(name, tx, y, tw, GOLD) + 10;
			y += wrap(summary, tx, y, tw, TEXT) + 16;
			text("Net worth: " + game.currentWorth() + " cr.", tx, y, DIM);
		}
		footer(x, "A / B: continue");
	}

	void drawDialog(float x, BaseDialog d) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapes.begin(ShapeRenderer.ShapeType.Filled);
		shapes.setColor(0, 0, 0, 0.6f);
		shapes.rect(-2000, -2000, 5000, 5000);
		shapes.end();
		batch.begin();

		float bx = x + 60, bw = VW - 120;
		String title = title(d), msg = message(d);
		if (d instanceof PlunderDialog || d instanceof JettisonDialog) { drawCargoDialog(x, d); return; }
		if (d instanceof ShipInfoDialog) { drawShipInfo(x); return; }
		if (d instanceof NewspaperDialog) { drawNewspaper(x); return; }
		if (d instanceof HighScoresDialog) { drawHighScores(x); return; }
		layout.setText(font, msg, TEXT, bw - 32, com.badlogic.gdx.utils.Align.left, true);
		float bh = Math.min(400, 110 + layout.height + (d instanceof InputDialog ? 40 : 0));
		float by = (VH - bh) / 2f;
		rect(bx, by, bw, bh, PANEL);
		text(title, bx + 16, VH - (by + bh) + 14, GOLD);
		wrap(msg, bx + 16, VH - (by + bh) + 44, bw - 32, TEXT);
		float fy = VH - by - 26;
		if (d instanceof InputDialog) {
			text("> " + inputText + "_", bx + 16, fy - 34, GOLD);
		}
		String hint;
		if (d instanceof SimpleDialog) hint = "A: OK";
		else if (d instanceof ConfirmDialog) hint = "A: " + label(((ConfirmDialog) d).posId, "Yes") + "    B: " + label(((ConfirmDialog) d).negId, "No");
		else if (d instanceof InputDialog) {
			InputDialog in = (InputDialog) d;
			hint = "A: " + label(in.positiveId, "OK") + (in.neutralId >= 0 ? "    X: " + label(in.neutralId, "") : "") + "    B: " + label(in.negativeId, "Cancel") + "    Up/Down/L/R: value";
		} else if (d instanceof SpecialEventDialog && !game.specialEventIsMessage()) hint = "A: Yes    B: No";
		else hint = "A: OK";
		text(hint, bx + 16, fy, DIM);
	}

	String label(int id, String def) { return id >= 0 ? res.getString(id) : def; }

	void drawCargoDialog(float x, BaseDialog d) {
		boolean plunder = d instanceof PlunderDialog;
		Ship src = plunder ? game.opponentShip() : game.playerShip();
		float bx = x + 40, bw = VW - 80, by = 24, bh = VH - 48;
		rect(bx, by, bw, bh, PANEL);
		text(plunder ? "Plunder" : "Jettison Cargo", bx + 16, 34, GOLD);
		TradeItem[] items = TradeItem.values();
		for (int i = 0; i < items.length; i++) {
			float y = 64 + i * 26;
			row(bx + 8, y, bw - 16, plunderSel == i);
			text(items[i].toXmlString(res), bx + 20, y, TEXT);
			textRight(String.valueOf(src.getCargo(items[i])), bx + bw - 24, y, TEXT);
		}
		if (plunder) {
			float y = 64 + items.length * 26;
			row(bx + 8, y, bw - 16, plunderSel == items.length);
			text("Done", bx + 20, y, TEXT);
		}
		Ship me = game.playerShip();
		text("Bays " + me.filledCargoBays() + "/" + me.totalCargoBays(), bx + 20, 64 + (items.length + 1) * 26, GOLD);
		text(plunder ? "A: take some   X: take all   B: done" : "A: dump some   X: dump all   B: close", bx + 16, VH - 24 - 34, DIM);
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapes.dispose();
		font.dispose();
		pixel.dispose();
		for (Texture t : textures.values()) if (t != null) t.dispose();
	}
}
