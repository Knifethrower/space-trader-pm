/*
 *     Copyright (C) 2014 Russell Wolf, All Rights Reserved
 *     
 *     Based on code by Pieter Spronck
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     You can contact the author at spacetrader@brucelet.com
 */
package com.brucelet.spacetrader.datatypes;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brucelet.spacetrader.Application;
import com.brucelet.spacetrader.BaseDialog;
import com.brucelet.spacetrader.BaseDialog.Builder;
import com.brucelet.spacetrader.BaseScreen;
import com.brucelet.spacetrader.BuyEqScreen;
import com.brucelet.spacetrader.BuyScreen;
import com.brucelet.spacetrader.BuyShipScreen;
import com.brucelet.spacetrader.ChartFindDialog;
import com.brucelet.spacetrader.ChartScreen;
import com.brucelet.spacetrader.CheatDialog;
import com.brucelet.spacetrader.ConfirmDialog;
import com.brucelet.spacetrader.EncounterScreen;
import com.brucelet.spacetrader.HighScoresDialog;
import com.brucelet.spacetrader.InputDialog;
import com.brucelet.spacetrader.InputDialog.OnNegativeListener;
import com.brucelet.spacetrader.InputDialog.OnNeutralListener;
import com.brucelet.spacetrader.InputDialog.OnPositiveListener;
import com.brucelet.spacetrader.JettisonDialog;
import com.brucelet.spacetrader.MainActivity;
import com.brucelet.spacetrader.NewGameDialog;
import com.brucelet.spacetrader.NewspaperDialog;
import com.brucelet.spacetrader.OnCancelListener;
import com.brucelet.spacetrader.OnConfirmListener;
import com.brucelet.spacetrader.OptionsDialog;
import com.brucelet.spacetrader.PlunderDialog;
import com.brucelet.spacetrader.QuestsCheatDialog;
import com.brucelet.spacetrader.R;
import com.brucelet.spacetrader.SellEqScreen;
import com.brucelet.spacetrader.SellScreen;
import com.brucelet.spacetrader.ShipInfoDialog;
import com.brucelet.spacetrader.SimpleDialog;
import com.brucelet.spacetrader.SpecialEventDialog;
import com.brucelet.spacetrader.StatusShipScreen;
import com.brucelet.spacetrader.VeryRareCheatDialog;
import com.brucelet.spacetrader.WarpPricesScreen;
import com.brucelet.spacetrader.WarpScreen;
import com.brucelet.spacetrader.WarpSubScreen;
import com.brucelet.spacetrader.WarpSystemPagerAdapter;
import com.brucelet.spacetrader.WarpTargetCostDialog;
import com.brucelet.spacetrader.enumtypes.DifficultyLevel;
import com.brucelet.spacetrader.enumtypes.Encounter;
import com.brucelet.spacetrader.enumtypes.EncounterButton;
import com.brucelet.spacetrader.enumtypes.EndStatus;
import com.brucelet.spacetrader.enumtypes.EquipmentType;
import com.brucelet.spacetrader.enumtypes.Gadget;
import com.brucelet.spacetrader.enumtypes.NewsEvent;
import com.brucelet.spacetrader.enumtypes.Opponent;
import com.brucelet.spacetrader.enumtypes.OpponentAction;
import com.brucelet.spacetrader.enumtypes.PoliceRecord;
import com.brucelet.spacetrader.enumtypes.Politics;
import com.brucelet.spacetrader.enumtypes.Purchasable;
import com.brucelet.spacetrader.enumtypes.Reputation;
import com.brucelet.spacetrader.enumtypes.ScreenType;
import com.brucelet.spacetrader.enumtypes.SellOperation;
import com.brucelet.spacetrader.enumtypes.Shield;
import com.brucelet.spacetrader.enumtypes.ShipType;
import com.brucelet.spacetrader.enumtypes.Size;
import com.brucelet.spacetrader.enumtypes.Skill;
import com.brucelet.spacetrader.enumtypes.SpecialEvent;
import com.brucelet.spacetrader.enumtypes.SpecialResources;
import com.brucelet.spacetrader.enumtypes.Status;
import com.brucelet.spacetrader.enumtypes.TechLevel;
import com.brucelet.spacetrader.enumtypes.ThemeType;
import com.brucelet.spacetrader.enumtypes.TradeItem;
import com.brucelet.spacetrader.enumtypes.Weapon;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class GameState {

	public static final String LOG_TAG = "Space Trader";

	private static final Random rng = new Random();
	private final MainActivity mGameManager;
	
	// Debt Control
	private static final int DEBTWARNING= 75000;
	private static final int DEBTTOOLARGE= 100000;
	
	final CrewMember[] mercenary = new CrewMember[31];
	final SolarSystem[] solarSystem = new SolarSystem[120];
	final SolarSystem[] wormhole = new SolarSystem[6];
	
	// The following globals are saved between sessions
	// Note that these initializations are overruled by the StartNewGame function
	int credits = 1000;            // Current credits owned
	int debt    = 0;               // Current Debt
	Map<TradeItem, Integer> buyPrice = new EnumMap<>(TradeItem.class);    // Price list current system
	Map<TradeItem, Integer> buyingPrice = new EnumMap<>(TradeItem.class); // Total price paid for trade goods
	Map<TradeItem, Integer> sellPrice = new EnumMap<>(TradeItem.class);   // Price list current system
	Map<ShipType, Integer> shipPrice = new EnumMap<>(ShipType.class);      // Price list current system (recalculate when buy ship screen is entered)
	int policeKills = 0;           // Number of police ships killed
	int traderKills = 0;           // Number of trader ships killed
	int pirateKills = 0;           // Number of pirate ships killed
	int policeRecordScore = 0;     // 0 = Clean record
	int reputationScore = 0;       // 0 = Harmless
	int monsterHull = 500;         // Hull strength of monster

	int days = 0;                   // Number of days playing
	SolarSystem warpSystem = null;             // Target system for warp
	ShipType selectedShipType = null;       // Selected Ship type for Shiptype Info screen
	int cheatCounter = 0;
	SolarSystem galacticChartSystem = null;    // Current system on Galactic chart
	boolean galacticChartWormhole = false;
	Encounter encounterType = null;          // Type of current encounter
	int curForm = 0;                // Form to return to
	int noClaim = 0;                // Days of No-Claim
	int leaveEmpty = 0;             // Number of cargo bays to leave empty when buying goods
	int newsSpecialEventCount = 0;  // Simplifies tracking what Quests have just been initiated or completed for the News System. This is not important enough to get saved.
	SolarSystem trackedSystem = null;			// The short-range chart will display an arrow towards this system if the value is not -1

	int shortcut1 = 0;				// default shortcut 1 = Buy Cargo
	int shortcut2 = 1;				// default shortcut 2 = Sell Cargo
	int shortcut3 = 2;				// default shortcut 3 = Shipyard
	int shortcut4 = 10;				// default shortcut 4 = Short Range Warp

	
	// the next two values are NOT saved between sessions -- they can only be changed via cheats.
	int chanceOfVeryRareEncounter	= CHANCEOFVERYRAREENCOUNTER;
	int chanceOfTradeInOrbit		= CHANCEOFTRADEINORBIT;

	int monsterStatus = 0;       // 0 = Space monster isn't available, 1 = Space monster is in Acamar system, 2 = Space monster is destroyed
	int dragonflyStatus = 0;     // 0 = Dragonfly not available, 1 = Go to Baratas, 2 = Go to Melina, 3 = Go to Regulas, 4 = Go to Zalkon, 5 = Dragonfly destroyed
	int japoriDiseaseStatus = 0; // 0 = No disease, 1 = Go to Japori (always at least 10 medicine cannisters), 2 = Assignment finished or canceled
	DifficultyLevel difficulty = DifficultyLevel.NORMAL;     // Difficulty level
	int jarekStatus = 0;         // Ambassador Jarek 0=not delivered; 1=on board; 2=delivered
	int invasionStatus = 0;      // Status Alien invasion of Gemulon; 0=not given yet; 1-7=days from start; 8=too late
	int experimentStatus = 0;    // Experiment; 0=not given yet,1-11 days from start; 12=performed, 13=cancelled
	int fabricRipProbability = 0; // if Experiment = 8, this is the probability of being warped to a random planet.
	int veryRareEncounter = 0;     // bit map for which Very Rare Encounter(s) have taken place (see traveler.c, around line 1850)
	int wildStatus = 0;			// Jonathan Wild: 0=not delivered; 1=on board; 2=delivered
	int reactorStatus = 0;			// Unstable Reactor Status: 0=not encountered; 1-20=days of mission (bays of fuel left = 10 - (ReactorStatus/2); 21=delivered
	int scarabStatus = 0;		// Scarab: 0=not given yet, 1=not destroyed, 2=destroyed, upgrade not performed, 3=destroyed, hull upgrade performed

	boolean autoFuel = false;            // Automatically get a full tank when arriving in a new system
	boolean autoRepair = false;          // Automatically get a full hull repair when arriving in a new system
	int clicks = 0;                  // Distance from target system, 0 = arrived
	boolean raided = false;              // True when the commander has been raided during the trip
	boolean inspected = false;           // True when the commander has been inspected during the trip
	boolean moonBought = false;          // Indicates whether a moon is available at Utopia
	boolean escapePod = false;           // Escape Pod in ship
	boolean insurance = false;           // Insurance bought
	boolean alwaysIgnoreTraders = false; // Automatically ignores traders when it is safe to do so
	boolean alwaysIgnorePolice = true;   // Automatically ignores police when it is safe to do so
	boolean alwaysIgnorePirates = false; // Automatically ignores pirates when it is safe to do so
	boolean alwaysIgnoreTradeInOrbit = false; // Automatically ignores Trade in Orbit when it is safe to do so
	boolean artifactOnBoard = false;     // Alien artifact on board
	boolean reserveMoney = false;        // Keep enough money for insurance and mercenaries
	boolean priceDifferences = false;    // Show price differences instead of absolute prices
	boolean aplScreen = false;           // Is true is the APL screen was last shown after the SRC
	boolean tribbleMessage = false;      // Is true if the Ship Yard on the current system informed you about the tribbles
	boolean alwaysInfo = false;          // Will always go from SRC to Info
	boolean textualEncounters = false;   // Show encounters as text.
	boolean graphicalEncounters = true;   // NB this is new, because we might have both textual and graphical encounters on at once.
	volatile boolean autoAttack = false;			 // Auto-attack mode
	volatile boolean autoFlee = false;			 // Auto-flee mode
	boolean continuous = false;			 // Continuous attack/flee mode
	boolean attackIconStatus = false;	 // Show Attack Star or not
	boolean attackFleeing = false;		 // Continue attack on fleeing ship
	boolean possibleToGoThroughRip = false;	// if Dr Fehler's experiment happened, we can only go through one space-time rip per warp.
	boolean useHWButtons = false;		// by default, don't use Hardware W buttons
	boolean newsAutoPay = false;		// by default, ask each time someone buys a newspaper
	boolean showTrackedRange = true;	// display range when tracking a system on Short Range Chart
	boolean justLootedMarie = false;		// flag to indicate whether player looted Marie Celeste
	boolean arrivedViaWormhole = false;	// flag to indicate whether player arrived on current planet via wormhole
	boolean alreadyPaidForNewspaper = false; // once you buy a paper on a system, you don't have to pay again.
	boolean trackAutoOff = true;		// Automatically stop tracking a system when you get to it?
	boolean remindLoans = true;			// remind you every five days about outstanding loan balances
	boolean canSuperWarp = false;		// Do you have the Portable Singularity on board?
	boolean gameLoaded = false;			// Indicates whether a game is loaded
	boolean cheated = false;			// Indicates whether a cheat has been used
	boolean litterWarning = false;		// Warning against littering has been issued.
	boolean sharePreferences = true;	// Share preferences between switched games.
	boolean identifyStartup = false;	// Identify commander at game start
	boolean rectangularButtonsOn = false; // Indicates on OS 5.0 and higher whether rectangular buttons should be used.		

	final HighScore[] hScores = new HighScore[3];
	private final NewsEvent[] newsEvents = new NewsEvent[MAXSPECIALNEWSEVENTS];

	int acamar = -1;
	int baratas = -1;
	int daled = -1;
	int devidia = -1;
	int gemulon = -1;
	int japori = -1;
	int kravat = -1;
	int melina = -1;
	int nix = -1;
	int og = -1;
	int regulas = -1;
	int sol = -1;
	int utopia = -1;
	int zalkon = -1;

		
	Ship ship;
	Ship opponent;
	private final Ship monster = new Ship(this, ShipType.MONSTER);
	private final Ship scarab = new Ship(this, ShipType.SCARAB);
	private final Ship dragonfly = new Ship(this, ShipType.DRAGONFLY);
	{
		monster.weapon[0] = Weapon.MILITARY;
		monster.weapon[1] = Weapon.MILITARY;
		monster.weapon[2] = Weapon.MILITARY;

		scarab.weapon[0] = Weapon.MILITARY;
		scarab.weapon[1] = Weapon.MILITARY;
		
		dragonfly.weapon[0] = Weapon.MILITARY;
		dragonfly.weapon[1] = Weapon.PULSE;
		dragonfly.shield[0] = Shield.LIGHTNING;
		dragonfly.shield[1] = Shield.LIGHTNING;
		dragonfly.shield[2] = Shield.LIGHTNING;
		dragonfly.shieldStrength[0] = Shield.LIGHTNING.power;
		dragonfly.shieldStrength[1] = Shield.LIGHTNING.power;
		dragonfly.shieldStrength[2] = Shield.LIGHTNING.power;
		dragonfly.gadget[0] = Gadget.AUTOREPAIRSYSTEM;
		dragonfly.gadget[1] = Gadget.TARGETINGSYSTEM;
	}

	private int narcs;
	private boolean playerShipNeedsUpdate;
	private boolean opponentShipNeedsUpdate;
	private boolean opponentGotHit;
	boolean commanderGotHit;
	private EndStatus endStatus = null;
	private volatile EncounterButton prevEncounterAction;

	private boolean recallScreens;
	private boolean volumeScroll;
	private boolean zoomGalaxy;
	private boolean trackLongPress;
	private boolean encounterAnim;
	private boolean extraShortcuts;
	private boolean randomQuestSystems;
	private boolean developerMode;

	private ValueAnimator playerFlashAnimator;
	private ValueAnimator opponentFlashAnimator;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private class ShipFlashUpdateListener implements ValueAnimator.AnimatorUpdateListener {
		private ImageView shipView;
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			if (shipView != null) {
				shipView.getDrawable().setColorFilter((int) animation.getAnimatedValue(), PorterDuff.Mode.SRC_ATOP);
			}
		}
	}
	private ShipFlashUpdateListener playerFlashUpdateListener;
	private ShipFlashUpdateListener opponentFlashUpdateListener;

	public static final int GALAXYWIDTH = 150;
	public static final int GALAXYHEIGHT = 110;
	static final int SHORTRANGEWIDTH = 140;
	static final int SHORTRANGEHEIGHT = 140;
	static final int SHORTRANGEBOUNDSX = 10;
	static final int BOUNDSX = 5;
	static final int BOUNDSY = 20;
	static final int MINDISTANCE = 6;
	static final int CLOSEDISTANCE = 13;
	static final int WORMHOLEDISTANCE = 3;
	static final int EXTRAERASE = 3;
	static final int COSTMOON = 500000;
	static final int CHANCEOFVERYRAREENCOUNTER = 5;
	static final int CHANCEOFTRADEINORBIT = 100;

	static final int MAXSKILL = 10;
	static final int MAXLOAN = 25000;

	private static final byte ALREADYMARIE= 1;
	private static final byte ALREADYAHAB = 2;
	private static final byte ALREADYCONRAD = 4;
	private static final byte ALREADYHUIE = 8;
	private static final byte ALREADYBOTTLEOLD = 16;
	private static final byte ALREADYBOTTLEGOOD = 32;

	private static final int FABRICRIPINITIALPROBABILITY = 25;
	private static final int MAXTRIBBLES = 100000;
	private static final int MAXMASTHEADS = 3;
	private static final int MAXSTORIES = 4;
	private static final int MAXSPECIALNEWSEVENTS = 5;
	private static final int STORYPROBABILITY = 50/TechLevel.values().length;	// NB this is a strange way to set this value.

	private static final int MAXWEAPONTYPE = Weapon.buyableValues().length;
	private static final int MAXSHIELDTYPE = Shield.buyableValues().length;
	private static final int MAXGADGETTYPE = Gadget.buyableValues().length;
	private static final int MAXRANGE = 20;

	private final Paint chartStroke = new Paint();
	private final Paint chartText = new Paint();
	private void initializePaints() {		
		chartStroke.setStyle(Style.STROKE);
		chartStroke.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.line_width));
		chartStroke.setStrokeCap(Cap.ROUND);
		chartStroke.setStrokeJoin(Join.MITER);
		chartStroke.setAntiAlias(true);
		
		TypedValue tv = new TypedValue();
		mGameManager.getTheme().resolveAttribute(R.attr.strokeColor, tv, true);
		int strokeColor = getResources().getColor(tv.resourceId);
		chartStroke.setColor(strokeColor);
		
		chartText.setTextAlign(Align.CENTER);
		chartText.setAntiAlias(true);
		
		mGameManager.getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, tv, true);
		TypedArray ta = mGameManager.getTheme().obtainStyledAttributes(tv.resourceId, new int[] {android.R.attr.textSize});
		ta.getValue(0, tv);
		chartText.setTextSize(tv.getDimension(getResources().getDisplayMetrics()));
		ta.recycle();
		
		mGameManager.getTheme().resolveAttribute(android.R.attr.textColorPrimary, tv, true);
		int textColor = getResources().getColor(tv.resourceId);
		chartText.setColor(textColor);
	}
	
	public GameState(MainActivity gm) {
		mGameManager = gm;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			playerFlashAnimator = ValueAnimator.ofInt(0x00000000, 0xffff0000, 0x00000000).setDuration(200);
			playerFlashAnimator.setEvaluator(new ArgbEvaluator());
//			playerFlashAnimator.setInterpolator(AnimationUtils.loadInterpolator(mGameManager, android.R.interpolator.accelerate_decelerate));
			playerFlashUpdateListener = new ShipFlashUpdateListener();
			playerFlashAnimator.addUpdateListener(playerFlashUpdateListener);

			opponentFlashAnimator = ValueAnimator.ofInt(0x00000000, 0xffff0000, 0x00000000).setDuration(200);
			opponentFlashAnimator.setEvaluator(new ArgbEvaluator());
//			opponentFlashAnimator.setInterpolator(AnimationUtils.loadInterpolator(mGameManager, android.R.interpolator.accelerate_decelerate));
			opponentFlashUpdateListener = new ShipFlashUpdateListener();
			opponentFlashAnimator.addUpdateListener(opponentFlashUpdateListener);
		}
	}

	public void saveState(SharedPreferences.Editor editor) {
		editor.putInt("credits", credits);
		editor.putInt("debt", debt);
		for (TradeItem item : TradeItem.values()) {
			editor.putInt("buyPrice_"+item, buyPrice.get(item));
			editor.putInt("buyingPrice_"+item, buyingPrice.get(item));
			editor.putInt("sellPrice_"+item, sellPrice.get(item));
		}
		for (ShipType type : ShipType.buyableValues()) {
			editor.putInt("shipPrice_"+type, shipPrice.get(type));
		}
		editor.putInt("policeKills", policeKills);
		editor.putInt("traderKills", traderKills);
		editor.putInt("pirateKills", pirateKills);
		editor.putInt("policeRecordScore", policeRecordScore);
		editor.putInt("reputationScore", reputationScore);
		editor.putInt("monsterHull", monsterHull);

		editor.putInt("days", days);
		editor.putString("warpSystem", warpSystem.name);
		editor.putInt("selectedShipType", selectedShipType == null? -1 : selectedShipType.ordinal());
		editor.putInt("cheatCounter", cheatCounter);
		editor.putString("galacticChartSystem", galacticChartSystem == null? "" : galacticChartSystem.name);
		editor.putBoolean("galacticChartWormhole", galacticChartWormhole);
		editor.putInt("encounterType", encounterType == null? -1 : encounterType.ordinal());
		editor.putInt("encounterOpponent", encounterType == null? -1 : encounterType.opponentType().ordinal());
		editor.putInt("curForm", curForm);
		editor.putInt("noClaim", noClaim);
		editor.putInt("leaveEmpty", leaveEmpty);
		editor.putInt("newsSpecialEventCount", newsSpecialEventCount);
		editor.putString("trackedSystem", trackedSystem == null? "" : trackedSystem.name);

		editor.putInt("shortcut1", shortcut1);
		editor.putInt("shortcut2", shortcut2);
		editor.putInt("shortcut3", shortcut3);
		editor.putInt("shortcut4", shortcut4);

		editor.putInt("monsterStatus", monsterStatus);
		editor.putInt("dragonflyStatus", dragonflyStatus);
		editor.putInt("japoriDiseaseStatus", japoriDiseaseStatus);
		editor.putInt("difficulty", difficulty == null? -1 : difficulty.ordinal());
		editor.putInt("jarekStatus", jarekStatus);
		editor.putInt("invasionStatus", invasionStatus);
		editor.putInt("experimentStatus", experimentStatus);
		editor.putInt("fabricRipProbability", fabricRipProbability);
		editor.putInt("veryRareEncounter", veryRareEncounter);
		editor.putInt("wildStatus", wildStatus);
		editor.putInt("reactorStatus", reactorStatus);
		editor.putInt("scarabStatus", scarabStatus);

		editor.putBoolean("autoFuel", autoFuel);
		editor.putBoolean("autoRepair", autoRepair);
		editor.putInt("clicks", clicks);
		editor.putBoolean("raided", raided);
		editor.putBoolean("inspected", inspected);
		editor.putBoolean("moonBought", moonBought);
		editor.putBoolean("escapePod", escapePod);
		editor.putBoolean("insurance", insurance);
		editor.putBoolean("alwaysIgnoreTraders", alwaysIgnoreTraders);
		editor.putBoolean("alwaysIgnorePolice", alwaysIgnorePolice);
		editor.putBoolean("alwaysIgnorePirates", alwaysIgnorePirates);
		editor.putBoolean("alwaysIgnoreTradeInOrbit", alwaysIgnoreTradeInOrbit);
		editor.putBoolean("artifactOnBoard", artifactOnBoard);
		editor.putBoolean("reserveMoney", reserveMoney);
		editor.putBoolean("priceDifferences", priceDifferences);
		editor.putBoolean("aplScreen", aplScreen);
		editor.putBoolean("tribbleMessage", tribbleMessage);
		editor.putBoolean("alwaysInfo", alwaysInfo);
		editor.putBoolean("textualEncounters", textualEncounters);
		editor.putBoolean("graphicalEncounters", graphicalEncounters);
		editor.putBoolean("continuous", continuous);
		editor.putBoolean("attackFleeing", attackFleeing);
		editor.putBoolean("possibleToGoThroughRip", possibleToGoThroughRip);
		editor.putBoolean("useHWButtons", useHWButtons);
		editor.putBoolean("newsAutoPay", newsAutoPay);
		editor.putBoolean("showTrackedRange", showTrackedRange);
		editor.putBoolean("justLootedMarie", justLootedMarie);
		editor.putBoolean("arrivedViaWormhole", arrivedViaWormhole);
		editor.putBoolean("alreadyPaidForNewspaper", alreadyPaidForNewspaper);
		editor.putBoolean("trackAutoOff", trackAutoOff);
		editor.putBoolean("remindLoans", remindLoans);
		editor.putBoolean("canSuperWarp", canSuperWarp);
		editor.putBoolean("gameLoaded", gameLoaded);
		editor.putBoolean("cheated", cheated);
		editor.putBoolean("litterWarning", litterWarning);
		editor.putBoolean("sharePreferences", sharePreferences);
		editor.putBoolean("identifyStartup", identifyStartup);
		editor.putBoolean("rectangularButtonsOn", rectangularButtonsOn);	

		editor.putInt("acamar", acamar);
		editor.putInt("baratas", baratas);
		editor.putInt("daled", daled);
		editor.putInt("devidia", devidia);
		editor.putInt("gemulon", gemulon);
		editor.putInt("japori", japori);
		editor.putInt("kravat", kravat);
		editor.putInt("melina", melina);
		editor.putInt("nix", nix);
		editor.putInt("og", og);
		editor.putInt("regulas", regulas);
		editor.putInt("sol", sol);
		editor.putInt("utopia", utopia);
		editor.putInt("zalkon", zalkon);

		editor.putBoolean("opponentGotHit", opponentGotHit);
		editor.putBoolean("commanderGotHit", commanderGotHit);

		editor.putBoolean("volumeScroll", volumeScroll);
		editor.putBoolean("recallScreens", recallScreens);
		editor.putBoolean("zoomGalaxy", zoomGalaxy);
		editor.putBoolean("trackLongPress", trackLongPress);
		editor.putBoolean("encounterAnim", encounterAnim);
		editor.putBoolean("extraShortcuts", extraShortcuts);

		editor.putBoolean("randomQuestSystems", randomQuestSystems);

		editor.putBoolean("developerMode", developerMode);
		
		if (ship != null) {
			ship.saveState(editor, "ship");
		}
		if (opponent != null) {
			opponent.saveState(editor, "opponent");
		}

		for (int i = 0; i < solarSystem.length; i++) {
			solarSystem[i].saveState(editor, "system"+i);
		}
		for (int i = 0; i < wormhole.length; i++) {
			editor.putString("wormhole"+i, wormhole[i].name);
		}
		for (int i = 0; i < mercenary.length; i++) {
			mercenary[i].saveState(editor, "mercenary"+i);
		}
		
		for (int i = 0; i < hScores.length; i++) {
			if (hScores[i] != null) {
				hScores[i].saveState(editor, "hScore"+i);
			}
			editor.putBoolean("hScore"+i+"_null", hScores[i] == null);
		}
		
		if (endStatus != null) editor.putInt("endStatus", endStatus.ordinal());
		
		// If we're saving state while auto actions on the Encounter screen are active, disable then and redraw the UI for if we come back.
		clearButtonAction();
	}
	
	public void loadState(SharedPreferences prefs) {
		credits = prefs.getInt("credits", credits);
		debt = prefs.getInt("debt", debt);
		for (TradeItem item : TradeItem.values()) {
			buyPrice.put(item, prefs.getInt("buyPrice_"+item, 0));
			buyingPrice.put(item, prefs.getInt("buyingPrice_"+item, 0));
			sellPrice.put(item, prefs.getInt("sellPrice_"+item, 0));
		}
		for (ShipType type : ShipType.buyableValues()) {
			shipPrice.put(type, prefs.getInt("shipPrice_"+type, 0));
		}
		policeKills = prefs.getInt("policeKills", policeKills);
		traderKills = prefs.getInt("traderKills", traderKills);
		pirateKills = prefs.getInt("pirateKills", pirateKills);
		policeRecordScore = prefs.getInt("policeRecordScore", policeRecordScore);
		reputationScore = prefs.getInt("reputationScore", reputationScore);
		monsterHull = prefs.getInt("monsterHull", monsterHull);

		days = prefs.getInt("days", days);
		selectedShipType = ShipType.values()[prefs.getInt("selectedShipType",0)];
		cheatCounter = prefs.getInt("cheatCounter", cheatCounter);

		int oppTypeIndex = prefs.getInt("encounterOpponent", -1);
		int encTypeIndex = prefs.getInt("encounterType", -1);
		
		if (encTypeIndex < 0) {
			encounterType = null;
		} else {
			switch (Opponent.values()[oppTypeIndex]) {
			case POLICE:
				encounterType = Encounter.Police.values()[encTypeIndex];
				break;
			case PIRATE:
				encounterType = Encounter.Pirate.values()[encTypeIndex];
				break;
			case TRADER:
				encounterType = Encounter.Trader.values()[encTypeIndex];
				break;
			case DRAGONFLY:
				encounterType = Encounter.Dragonfly.values()[encTypeIndex];
				break;
			case MONSTER:
				encounterType = Encounter.Monster.values()[encTypeIndex];
				break;
			case MANTIS:
				encounterType = Encounter.Mantis.values()[encTypeIndex];
				break;
			case SCARAB:
				encounterType = Encounter.Scarab.values()[encTypeIndex];
				break;
			case FAMOUSCAPTAIN:
			case BOTTLE:
			case MARIECELESTE:
			case POSTMARIE:
				encounterType = Encounter.VeryRare.values()[encTypeIndex];
				break;
			default:
				encounterType = null;
				break;
			}
		}

		curForm = prefs.getInt("curForm", curForm);
		noClaim = prefs.getInt("noClaim", noClaim);
		leaveEmpty = prefs.getInt("leaveEmpty", leaveEmpty);
		newsSpecialEventCount = prefs.getInt("newsSpecialEventCount", newsSpecialEventCount);

		shortcut1 = prefs.getInt("shortcut1", shortcut1);
		shortcut2 = prefs.getInt("shortcut2", shortcut2);
		shortcut3 = prefs.getInt("shortcut3", shortcut3);
		shortcut4 = prefs.getInt("shortcut4", shortcut4);

		monsterStatus = prefs.getInt("monsterStatus", monsterStatus);
		dragonflyStatus = prefs.getInt("dragonflyStatus", dragonflyStatus);
		japoriDiseaseStatus = prefs.getInt("japoriDiseaseStatus", japoriDiseaseStatus);
		difficulty = DifficultyLevel.values()[prefs.getInt("difficulty", difficulty.ordinal())];
		jarekStatus = prefs.getInt("jarekStatus", jarekStatus);
		invasionStatus = prefs.getInt("invasionStatus", invasionStatus);
		experimentStatus = prefs.getInt("experimentStatus", experimentStatus);
		fabricRipProbability = prefs.getInt("fabricRipProbability", fabricRipProbability);
		veryRareEncounter = prefs.getInt("veryRareEncounter", veryRareEncounter);
		wildStatus = prefs.getInt("wildStatus", wildStatus);
		reactorStatus = prefs.getInt("reactorStatus", reactorStatus);
		scarabStatus = prefs.getInt("scarabStatus", scarabStatus);

		autoFuel = prefs.getBoolean("autoFuel", autoFuel);
		autoRepair = prefs.getBoolean("autoRepair", autoRepair);
		clicks = prefs.getInt("clicks", clicks);
		raided = prefs.getBoolean("raided", raided);
		inspected = prefs.getBoolean("inspected", inspected);
		moonBought = prefs.getBoolean("moonBought", moonBought);
		escapePod = prefs.getBoolean("escapePod", escapePod);
		insurance = prefs.getBoolean("insurance", insurance);
		alwaysIgnoreTraders = prefs.getBoolean("alwaysIgnoreTraders", alwaysIgnoreTraders);
		alwaysIgnorePolice = prefs.getBoolean("alwaysIgnorePolice", alwaysIgnorePolice);
		alwaysIgnorePirates = prefs.getBoolean("alwaysIgnorePirates", alwaysIgnorePirates);
		alwaysIgnoreTradeInOrbit = prefs.getBoolean("alwaysIgnoreTradeInOrbit", alwaysIgnoreTradeInOrbit);
		artifactOnBoard = prefs.getBoolean("artifactOnBoard", artifactOnBoard);
		reserveMoney = prefs.getBoolean("reserveMoney", reserveMoney);
		priceDifferences = prefs.getBoolean("priceDifferences", priceDifferences);
		aplScreen = prefs.getBoolean("aplScreen", aplScreen);
		tribbleMessage = prefs.getBoolean("tribbleMessage", tribbleMessage);
		alwaysInfo = prefs.getBoolean("alwaysInfo", alwaysInfo);
		textualEncounters = prefs.getBoolean("textualEncounters", textualEncounters);
		graphicalEncounters = prefs.getBoolean("graphicalEncounters", graphicalEncounters);
		continuous = prefs.getBoolean("continuous", continuous);
		attackFleeing = prefs.getBoolean("attackFleeing", attackFleeing);
		possibleToGoThroughRip = prefs.getBoolean("possibleToGoThroughRip", possibleToGoThroughRip);
		useHWButtons = prefs.getBoolean("useHWButtons", useHWButtons);
		newsAutoPay = prefs.getBoolean("newsAutoPay", newsAutoPay);
		showTrackedRange = prefs.getBoolean("showTrackedRange", showTrackedRange);
		justLootedMarie = prefs.getBoolean("justLootedMarie", justLootedMarie);
		arrivedViaWormhole = prefs.getBoolean("arrivedViaWormhole", arrivedViaWormhole);
		alreadyPaidForNewspaper = prefs.getBoolean("alreadyPaidForNewspaper", alreadyPaidForNewspaper);
		trackAutoOff = prefs.getBoolean("trackAutoOff", trackAutoOff);
		remindLoans = prefs.getBoolean("remindLoans", remindLoans);
		canSuperWarp = prefs.getBoolean("canSuperWarp", canSuperWarp);
		gameLoaded = prefs.getBoolean("gameLoaded", gameLoaded);
		cheated = prefs.getBoolean("cheated", cheated);
		litterWarning = prefs.getBoolean("litterWarning", litterWarning);
		sharePreferences = prefs.getBoolean("sharePreferences", sharePreferences);
		identifyStartup = prefs.getBoolean("identifyStartup", identifyStartup);
		rectangularButtonsOn = prefs.getBoolean("rectangularButtonsOn", rectangularButtonsOn);	

		acamar = prefs.getInt("acamar", acamar);
		baratas = prefs.getInt("baratas", baratas);
		daled = prefs.getInt("daled", daled);
		devidia = prefs.getInt("devidia", devidia);
		gemulon = prefs.getInt("gemulon", gemulon);
		japori = prefs.getInt("japori", japori);
		kravat = prefs.getInt("kravat", kravat);
		melina = prefs.getInt("melina", melina);
		nix = prefs.getInt("nix", nix);
		og = prefs.getInt("og", og);
		regulas = prefs.getInt("regulas", regulas);
		sol = prefs.getInt("sol", sol);
		utopia = prefs.getInt("utopia", utopia);
		zalkon = prefs.getInt("zalkon", zalkon);

		opponentGotHit = prefs.getBoolean("opponentGotHit", opponentGotHit);
		commanderGotHit = prefs.getBoolean("commanderGotHit", commanderGotHit);	

		recallScreens = prefs.getBoolean("recallScreens", recallScreens);
		volumeScroll = prefs.getBoolean("volumeScroll", volumeScroll);
		zoomGalaxy = prefs.getBoolean("zoomGalaxy", zoomGalaxy);
		trackLongPress = prefs.getBoolean("trackLongPress", trackLongPress);
		encounterAnim = prefs.getBoolean("encounterAnim", encounterAnim);
		extraShortcuts = prefs.getBoolean("extraShortcuts", extraShortcuts);

		randomQuestSystems = prefs.getBoolean("randomQuestSystems", randomQuestSystems);
		
		developerMode = Application.DEVELOPER_MODE && prefs.getBoolean("developerMode", developerMode);

		for (int i = 0; i < solarSystem.length; i++) {
			solarSystem[i] = new SolarSystem(prefs, "system"+i, this);
		}
		for (int i = 0; i < wormhole.length; i++) {
			String name = prefs.getString("wormhole"+i, "");
			for (SolarSystem system : solarSystem) {
				if (system.name.equals(name)) {
					wormhole[i] = system;
					break;
				}
			}
		}
		for (int i = 0; i < mercenary.length; i++) {
			mercenary[i] = new CrewMember(prefs, "mercenary"+i, this);
			String curSystem = prefs.getString("mercenary"+i+"_curSystem","");
			for (SolarSystem system : solarSystem) {
				if (system.name.equals(curSystem)) {
					mercenary[i].setSystem(system);
				}
			}
		}
		Log.d("loadState()", "nameCommander()="+nameCommander());
		
		if (prefs.contains("ship_type")) {
			ship = new Ship(prefs, "ship", this);
		}
		if (prefs.contains("opponent_type")) {
			opponent = new Ship(prefs, "opponent", this);
		}
		if (ship != null) {
			for (int i = 0; i < ship.crew.length; i++) {
				for (CrewMember merc : mercenary) {
					if (prefs.contains("ship_crew"+i) && prefs.getString("ship_crew"+i, "").equals(merc.name)) {
						ship.crew[i] = merc;
					}
				}
			}
		}
		if (opponent != null) {
			for (int i = 0; i < opponent.crew.length; i++) {
				for (CrewMember merc : mercenary) {
					if (prefs.contains("opponent_crew"+i) && prefs.getString("opponent_crew"+i, "").equals(merc.name)) {
						opponent.crew[i] = merc;
					}
				}
			}
		}
		
		warpSystem = curSystem();
		galacticChartSystem = null;
		trackedSystem = null;
		for (SolarSystem system : solarSystem) {
			if (system.name.equals(prefs.getString("warpSystem", ""))) {
				warpSystem = system;
			}
			if (system.name.equals(prefs.getString("galacticChartSystem", ""))) {
				galacticChartSystem = system;
			}
			if (system.name.equals(prefs.getString("trackedSystem", ""))) {
				trackedSystem = system;
			}
		}
		
		for (int i = 0; i < hScores.length; i++) {
			if (!prefs.getBoolean("hScore"+i+"_null", true)) {
				hScores[i] = new HighScore(prefs, "hScore"+i);
			}
		}
		galacticChartWormhole = prefs.getBoolean("galacticChartWormhole", false);
		
		if (prefs.contains("endStatus")) endStatus = EndStatus.values()[prefs.getInt("endStatus", 0)];
		
	}
	
	private Resources getResources() {
		return mGameManager.getResources();
	}

	private volatile boolean stop;
	private OnCancelListener newStopper(final CountDownLatch latch) {
		return new OnCancelListener() {
			@Override
			public void onCancel() {
				stop = true; 
				unlock(latch);
			}
		};
	}
	
	private static OnConfirmListener newUnlocker(final CountDownLatch latch) {
		return new OnConfirmListener() {
			@Override
			public void onConfirm() {
				unlock(latch);
			}
		};
	}
	
	private static CountDownLatch newLatch() {
		return new CountDownLatch(1);
	}
	
	private static void unlock(CountDownLatch latch) {
		latch.countDown();
	}
	
	private static void lock(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private void copyPreference(SharedPreferences oldPrefs, SharedPreferences.Editor newPrefs, String key, int value) {
		newPrefs.putInt(key, oldPrefs.getInt(key, value));
	}
	private void copyPreference(SharedPreferences oldPrefs, SharedPreferences.Editor newPrefs, String key, boolean value) {
		newPrefs.putBoolean(key, oldPrefs.getBoolean(key, value));
	}
	
	public void copyPrefs(SharedPreferences oldPrefs, SharedPreferences.Editor newPrefs) {
		copyPreference(oldPrefs, newPrefs, "shortcut1", shortcut1);
		copyPreference(oldPrefs, newPrefs, "shortcut2", shortcut2);
		copyPreference(oldPrefs, newPrefs, "shortcut3", shortcut3);
		copyPreference(oldPrefs, newPrefs, "shortcut4", shortcut4);
		
		copyPreference(oldPrefs, newPrefs, "leaveEmpty", leaveEmpty);
		copyPreference(oldPrefs, newPrefs, "autoRepair", autoRepair);
		copyPreference(oldPrefs, newPrefs, "leaveEmpty", leaveEmpty);
		copyPreference(oldPrefs, newPrefs, "alwaysIgnoreTraders", alwaysIgnoreTraders);
		copyPreference(oldPrefs, newPrefs, "alwaysIgnorePolice", alwaysIgnorePolice);
		copyPreference(oldPrefs, newPrefs, "alwaysIgnorePirates", alwaysIgnorePirates);
		copyPreference(oldPrefs, newPrefs, "alwaysIgnoreTradeInOrbit", alwaysIgnoreTradeInOrbit);
		copyPreference(oldPrefs, newPrefs, "reserveMoney", reserveMoney);
		copyPreference(oldPrefs, newPrefs, "alwaysInfo", alwaysInfo);
		copyPreference(oldPrefs, newPrefs, "continuous", continuous);
		copyPreference(oldPrefs, newPrefs, "attackFleeing", attackFleeing);
		copyPreference(oldPrefs, newPrefs, "newsAutoPay", newsAutoPay);
		copyPreference(oldPrefs, newPrefs, "showTrackedRange", showTrackedRange);
		copyPreference(oldPrefs, newPrefs, "trackAutoOff", trackAutoOff);
		copyPreference(oldPrefs, newPrefs, "remindLoans", remindLoans);
		copyPreference(oldPrefs, newPrefs, "sharePreferences", sharePreferences);
		copyPreference(oldPrefs, newPrefs, "identifyStartup", identifyStartup);
		copyPreference(oldPrefs, newPrefs, "textualEncounters", textualEncounters);
		copyPreference(oldPrefs, newPrefs, "graphicalEncounters", graphicalEncounters);
		copyPreference(oldPrefs, newPrefs, "volumeScroll", volumeScroll);
		copyPreference(oldPrefs, newPrefs, "recallScreens", recallScreens);
		copyPreference(oldPrefs, newPrefs, "zoomGalaxy", zoomGalaxy);
		copyPreference(oldPrefs, newPrefs, "trackLongPress", trackLongPress);
		copyPreference(oldPrefs, newPrefs, "encounterAnim", encounterAnim);
		copyPreference(oldPrefs, newPrefs, "extraShortcuts", extraShortcuts);
		copyPreference(oldPrefs, newPrefs, "developerMode", developerMode);
	}

	public String nameCommander() {
		return commander().name;
//		return commander() != null? commander().name : "???";
	}
	
	public boolean recallScreens() {
		return recallScreens;
	}
	public boolean volumeScroll() {
		return volumeScroll;
	}
	public boolean zoomGalaxy() {
		return zoomGalaxy;
	}
	public boolean trackLongPress() {
		return trackLongPress;
	}
	public boolean extraShortcuts() {
		return extraShortcuts;
	}
	public boolean identifyStartup() {
		return identifyStartup;
	}
	public boolean sharePreferences() {
		return sharePreferences;
	}
	
	public int getShortcut(int shortcut) {
		switch(shortcut) {
		case 1:
			return shortcut1;
		case 2:
			return shortcut2;
		case 3:
			return shortcut3;
		case 4:
			return shortcut4;
		default:
			throw new IllegalArgumentException();
		}
	}
	public void setShortcut(int shortcut, int screen) {
		switch(shortcut) {
		case 1:
			shortcut1 = screen;
			break;
		case 2:
			shortcut2 = screen;
			break;
		case 3:
			shortcut3 = screen;
			break;
		case 4:
			shortcut4 = screen;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public boolean developerMode() {
		return Application.DEVELOPER_MODE && developerMode;
	}


	/*
	 * The following code adapted from the original palmos source as noted.
	 */
	/*
	 * spacetrader.h
	 */
	private static final int MAX_WORD = 65535;
	
	static int min ( int a, int b ) { return a <= b? a : b; }
	static int max ( int a, int b ) { return a >= b? a : b; }
	
	static int getRandom( int maxVal ) {
		if (maxVal < 2) {
			return 0;
		}
		return rng.nextInt(maxVal);
//		return rng.nextInt() % maxVal;
	}
	// This is a little cleaner sometimes for enums and other things that aren't ints anymore.
	static <E> E getRandom(E[] array) {
		return getRandom(array, 0, array.length);
	}
	static <E> E getRandom(E[] array, int start) {
		return getRandom(array, start, array.length);
	}
	static <E> E getRandom(E[] array, int start, int end) {
		if (start >= end || end > array.length) throw new IllegalArgumentException();
		
		int index = start + getRandom(end-start);
		return array[index];
	}
	
	static int abs ( int a ) { return ((a) < 0 ? (-(a)) : (a)); }
	static int sqr ( int a ) { return ((a) * (a)); }
	CrewMember commander() { return mercenary[0]; }
	SolarSystem curSystem() { return commander().curSystem(); }
	
	/*
	 * AppHandleEvent.c
	 */
	public void switchToNew(String defaultName) {
		identifyStartup = true;
		difficulty = DifficultyLevel.NORMAL;
		mercenary[0] = new CrewMember(defaultName, 1, 1, 1, 1, this);
		mGameManager.showDialogFragment(NewGameDialog.newInstance());
	}
	
	public void showOptions()
	{
		// NB some missing options here that don't exist in this version, and some new ones that don't exist in palm.
		
		BaseDialog dialog = mGameManager.findDialogByClass(OptionsDialog.class);

		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_fulltank)).setChecked(autoFuel);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_fullhull)).setChecked(autoRepair);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_traders)).setChecked(alwaysIgnoreTraders);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_police)).setChecked(alwaysIgnorePolice);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_pirates)).setChecked(alwaysIgnorePirates);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_dealing)).setChecked(alwaysIgnoreTradeInOrbit);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_warpcosts)).setChecked(reserveMoney);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_chartinfo)).setChecked(alwaysInfo);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_contattack)).setChecked(continuous);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_contattflee)).setChecked(attackFleeing);
		dialog.setViewTextById(R.id.dialog_options_bays, R.string.format_number, leaveEmpty);
		
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_news)).setChecked(newsAutoPay);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_range)).setChecked(showTrackedRange);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_track)).setChecked(trackAutoOff);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_loans)).setChecked(remindLoans);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_shareprefs)).setChecked(sharePreferences);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_identify)).setChecked(identifyStartup);
		
		// We treat textual encounters differently than the original because we can have hybrid textual/graphical view
		RadioGroup encGroup = (RadioGroup) dialog.getDialog().findViewById(R.id.dialog_options_encounterstyle);
		if (textualEncounters && graphicalEncounters) {
			encGroup.check(R.id.dialog_options_encounterstyle_both);
		} else if (textualEncounters) {
			encGroup.check(R.id.dialog_options_encounterstyle_textual);
		} else if (graphicalEncounters) {
			encGroup.check(R.id.dialog_options_encounterstyle_graphical);
		} else {
			encGroup.clearCheck();
		}
		
		// New options in android version
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_volumescroll)).setChecked(volumeScroll);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_recallscreens)).setChecked(recallScreens);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_zoomgalaxy)).setChecked(zoomGalaxy);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_tracklongpress)).setChecked(trackLongPress);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_encounteranim)).setChecked(encounterAnim);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_extrashortcuts)).setChecked(extraShortcuts);
		
		dialog.setViewVisibilityById(R.id.dialog_options_developermode, Application.DEVELOPER_MODE, false);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_developermode)).setChecked(developerMode);
		
		
		ThemeType theme = mGameManager.getThemeType();
		RadioGroup themeGroup = (RadioGroup) dialog.getDialog().findViewById(R.id.dialog_options_theme);
		switch (theme) {
		case HOLO_PALM:
			themeGroup.check(R.id.dialog_options_theme_palm);
			break;
		case HOLO_DARK:
			themeGroup.check(R.id.dialog_options_theme_dark);
			break;
		case HOLO_LIGHT:
			themeGroup.check(R.id.dialog_options_theme_light);
			break;
		case MATERIAL_LIGHT:
			themeGroup.check(R.id.dialog_options_theme_material_light);
			break;
		case MATERIAL_DARK:
			themeGroup.check(R.id.dialog_options_theme_material_dark);
			break;
		default:
			themeGroup.clearCheck();
			break;
		}
	}
	
	public void dismissOptions()
	{	
		BaseDialog dialog = mGameManager.findDialogByClass(OptionsDialog.class);

		try {
			leaveEmpty = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_options_bays)).getText().toString());
		} catch (NumberFormatException e) {
			leaveEmpty = 0;
		}
		autoFuel = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_fulltank)).isChecked();
		autoRepair = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_fullhull)).isChecked();
		alwaysIgnoreTraders = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_traders)).isChecked();
		alwaysIgnorePolice = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_police)).isChecked();
		alwaysIgnorePirates = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_pirates)).isChecked();
		alwaysIgnoreTradeInOrbit = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_ignore_dealing)).isChecked();
		reserveMoney = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_warpcosts)).isChecked();
		alwaysInfo = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_chartinfo)).isChecked();
		continuous = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_contattack)).isChecked();
		attackFleeing = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_contattflee)).isChecked();
		
		newsAutoPay = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_news)).isChecked();
		showTrackedRange = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_range)).isChecked();
		trackAutoOff = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_track)).isChecked();
		remindLoans = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_loans)).isChecked();
		sharePreferences = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_shareprefs)).isChecked();
		identifyStartup = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_identify)).isChecked();
		
		int encounterStyle = ((RadioGroup) dialog.getDialog().findViewById(R.id.dialog_options_encounterstyle)).getCheckedRadioButtonId();
		switch (encounterStyle) {
		case R.id.dialog_options_encounterstyle_both:
			textualEncounters = true;
			graphicalEncounters = true;
			break;
		case R.id.dialog_options_encounterstyle_textual:
			textualEncounters = true;
			graphicalEncounters = false;
			break;
		case R.id.dialog_options_encounterstyle_graphical:
		default:
			textualEncounters = false;
			graphicalEncounters = true;
			break;
		}

		volumeScroll = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_volumescroll)).isChecked();
		recallScreens = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_recallscreens)).isChecked();
		zoomGalaxy = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_zoomgalaxy)).isChecked();
		trackLongPress = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_tracklongpress)).isChecked();
		encounterAnim = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_encounteranim)).isChecked();
		extraShortcuts = ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_extrashortcuts)).isChecked();
		mGameManager.refreshExtraShortcuts();
		
		developerMode = Application.DEVELOPER_MODE && ((CheckBox) dialog.getDialog().findViewById(R.id.dialog_options_developermode)).isChecked();

		// New: Check if theme has changed
		ThemeType theme = mGameManager.getThemeType();

		RadioGroup rg = (RadioGroup) dialog.getDialog().findViewById(R.id.dialog_options_theme);
		ThemeType newTheme;
		switch (rg.getCheckedRadioButtonId()) {
		case R.id.dialog_options_theme_palm:
			newTheme = ThemeType.HOLO_PALM;
			break;
		case R.id.dialog_options_theme_dark:
			newTheme = ThemeType.HOLO_DARK;
			break;
		case R.id.dialog_options_theme_light:
		default:
			newTheme = ThemeType.HOLO_LIGHT;
			break;
		case R.id.dialog_options_theme_material_light:
			newTheme = ThemeType.MATERIAL_LIGHT;
			break;
		case R.id.dialog_options_theme_material_dark:
			newTheme = ThemeType.MATERIAL_DARK;
			break;
		}
		
		if (theme != newTheme) {
			mGameManager.setNewTheme(newTheme);
		}

		dialog.dismiss();
	}

	/*
	 * Bank.c
	 */
	// *************************************************************************
	// Maximum loan
	// *************************************************************************
	public int maxLoan( )
	{
		return policeRecordScore >= PoliceRecord.CLEAN.score ? 
				min( MAXLOAN, max( 1000, ((currentWorth() / 10) / 500) * 500 ) ) : 500;
	}
	
	
	// *************************************************************************
	// Lending money
	// *************************************************************************
	public void getLoan( int loan )
	{

		int amount = min( maxLoan() - debt, loan );
		credits += amount;
		debt += amount;
	}


	// *************************************************************************
	// Paying back
	// *************************************************************************
	public void payBack( int cash )
	{
		int amount;

		amount = min( debt, cash );
		amount = min( amount, credits );
		credits -= amount;
		debt -= amount;
	}
	
	// *************************************************************************
	// Show the Bank screen
	// *************************************************************************
	public void showBank(  )
	{
		final BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.BANK) return;

		screen.setViewVisibilityById(R.id.screen_bank_loan_pay, debt > 0);
		screen.setViewTextById(R.id.screen_bank_ins_buy, insurance? R.string.screen_bank_ins_stop : R.string.screen_bank_ins_buy);

		screen.setViewTextById(R.id.screen_bank_loan_debt, R.string.format_credits, debt);

		screen.setViewTextById(R.id.screen_bank_loan_max, R.string.format_credits, maxLoan());

		screen.setViewTextById(R.id.screen_bank_ins_ship, R.string.format_credits, ship.currentPriceWithoutCargo(true));		

		screen.setViewTextById(
				R.id.screen_bank_ins_noclaim,
				(noClaim >= 90? R.string.screen_bank_ins_maxnoclaim : R.string.format_percent),	// NB this was == instead of >= in original which was a bug.
				min(noClaim, 90)
				);

		screen.setViewTextById(R.id.screen_bank_ins_cost, R.string.format_dailycost, insuranceMoney());

		screen.setViewTextById(R.id.screen_bank_credits, R.string.format_cash, credits);
		
//		// Screenshot override
//		screen.setViewVisibilityById(R.id.screen_bank_loan_pay, false);
//		screen.setViewTextById(R.id.screen_bank_ins_buy, R.string.screen_bank_ins_buy);
//		screen.setViewTextById(R.id.screen_bank_loan_debt, R.string.format_credits, 0);
//		screen.setViewTextById(R.id.screen_bank_loan_max, R.string.format_credits, 25000);
//		screen.setViewTextById(R.id.screen_bank_ins_ship, R.string.format_credits, 44908);
//		screen.setViewTextById(R.id.screen_bank_ins_noclaim, R.string.format_percent, 0);
//		screen.setViewTextById(R.id.screen_bank_ins_cost, R.string.format_dailycost, 0);
//		screen.setViewTextById(R.id.screen_bank_credits, R.string.format_cash, 389304);
		
	}
	
	// *************************************************************************
	// Handling of events on the Bank screen
	// *************************************************************************
	public void bankFormHandleEvent(int buttonId)
	{
		if (buttonId == R.id.screen_bank_loan_get)
		{
			if (debt >= maxLoan())
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_bank_loan_toohigh_title, R.string.screen_bank_loan_toohigh_message, R.string.help_debttoohigh));
				return;
			}

			InputDialog getLoanDialog = InputDialog.newInstance(
					R.string.screen_bank_loan_get_title,
					R.string.screen_bank_loan_get_message,
					R.string.generic_ok,
					R.string.generic_maximum,
					R.string.generic_nothing,
					R.string.help_getloan,
					new OnPositiveListener() {
						@Override
						public void onClickPositiveButton(int value) {
							getLoan(value);
							showBank();
						}
					},
					new OnNeutralListener() {
						@Override
						public void onClickNeutralButton() {
							getLoan(MAXLOAN);
							showBank();
						}
					},
					maxLoan() - debt);
			mGameManager.showDialogFragment(getLoanDialog);

		}
		else if (buttonId == R.id.screen_bank_loan_pay)
		{
			if (debt <= 0)
			{
				// NB this dialog exists in the original source, but it should never appear because the button won't be drawn.
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_bank_loan_nodebt_title, R.string.screen_bank_loan_nodebt_message, R.string.help_nodebt));
				return;
			}

			InputDialog payLoanDialog = InputDialog.newInstance(
					R.string.screen_bank_loan_pay_title,
					R.string.screen_bank_loan_pay_message,
					R.string.generic_ok,
					R.string.generic_everything,
					R.string.generic_nothing,
					R.string.help_payback,
					new OnPositiveListener() {
						@Override
						public void onClickPositiveButton(int value) {
							payBack(value);
							showBank();
						}
					},
					new OnNeutralListener() {
						@Override
						public void onClickNeutralButton() {
							payBack(debt);
							showBank();
						}
					},
					debt);
			mGameManager.showDialogFragment(payLoanDialog);
		}
		else if (buttonId == R.id.screen_bank_ins_buy && !insurance)
		{
			if (!escapePod)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_bank_ins_nopod, R.string.screen_bank_ins_useless, R.string.help_noescapepod, false));
				return;
			}
			
			insurance = true;
		}			
		else if (buttonId == R.id.screen_bank_ins_buy && insurance)
		{
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.screen_bank_ins_stop, 
					R.string.screen_bank_ins_stopquery, 
					R.string.help_stopinsurance,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
							insurance = false;
							noClaim = 0;
							showBank();
						}
					},
					null,
					false));
		}			
		showBank();
	}

	/*
	 * BuyEquipEvent.c
	 */
	// *************************************************************************
	// Draw an item on the screen
	// *************************************************************************
	private void drawItem( Purchasable item )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.BUYEQ) return;
			
		int price = item.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER));
		if (price > 0)
			screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(item), R.string.format_credits, price);
		else
			screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(item), R.string.generic_notsold);

	}
	
	public void drawBuyEquipmentForm()
	{

		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.BUYEQ) return;
		
		for (int i=0; i<MAXWEAPONTYPE+MAXSHIELDTYPE+MAXGADGETTYPE; ++i)
		{
			if (i < MAXWEAPONTYPE)
			{
				Weapon weapon = Weapon.buyableValues()[i];
				screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(weapon), weapon.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)) > 0);
			}	
			else if (i < MAXWEAPONTYPE + MAXSHIELDTYPE)
			{
				Shield shield = Shield.buyableValues()[ i-MAXWEAPONTYPE ];
				screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(shield), shield.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)) > 0);
			}	
			else
			{
				Gadget gadget = Gadget.buyableValues()[ i-MAXWEAPONTYPE-MAXSHIELDTYPE ];
				screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(gadget), gadget.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)) > 0);
			}
		}

		for (Weapon weapon : Weapon.buyableValues())
			drawItem( weapon );
		for (Shield shield : Shield.buyableValues())
			drawItem( shield );
		for (Gadget gadget : Gadget.buyableValues())
			drawItem( gadget );

		screen.setViewTextById(R.id.screen_buyeq_credits, R.string.format_cash, credits);
		
//		// Screenshot override
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Weapon.PULSE), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Weapon.PULSE), R.string.format_credits, 1800);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Weapon.BEAM), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Weapon.BEAM), R.string.format_credits, 11250);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Weapon.MILITARY), false);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Weapon.MILITARY), R.string.generic_notsold);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Shield.ENERGY), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Shield.ENERGY), R.string.format_credits, 4500);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Shield.REFLECTIVE), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Shield.REFLECTIVE), R.string.format_credits, 18000);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Gadget.EXTRABAYS), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Gadget.EXTRABAYS), R.string.format_credits, 2250);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Gadget.AUTOREPAIRSYSTEM), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Gadget.AUTOREPAIRSYSTEM), R.string.format_credits, 6750);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Gadget.NAVIGATINGSYSTEM), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Gadget.NAVIGATINGSYSTEM), R.string.format_credits, 13500);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Gadget.TARGETINGSYSTEM), true);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Gadget.TARGETINGSYSTEM), R.string.format_credits, 22500);
//		screen.setViewVisibilityById(BuyEqScreen.BUTTON_IDS.get(Gadget.CLOAKINGDEVICE), false);
//		screen.setViewTextById(BuyEqScreen.PRICE_IDS.get(Gadget.CLOAKINGDEVICE), R.string.generic_notsold);
//		screen.setViewTextById(R.id.screen_buyeq_credits, R.string.format_cash, 378511);

		
	}

	// *************************************************************************
	// Handling of the events of the Buy Equipment form.
	// *************************************************************************
	public void buyEquipmentFormHandleEvent( int buttonId )
	{
		Purchasable item = null;
		for (Weapon weapon : Weapon.buyableValues()) {
			if (BuyEqScreen.BUTTON_IDS.get(weapon) == buttonId) {
				item = weapon;
				break;
			}
		}
		for (Shield shield : Shield.buyableValues()) {
			if (BuyEqScreen.BUTTON_IDS.get(shield) == buttonId) {
				item = shield;
				break;
			}
		}
		for (Gadget gadget : Gadget.buyableValues()) {
			if (BuyEqScreen.BUTTON_IDS.get(gadget) == buttonId) {
				item = gadget;
				break;
			}
		}
		
		if ( item instanceof Weapon)
		{
			buyItem( ship.type.weaponSlots, 
					ship.weapon, 
					item.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)),
					item.toXmlString(getResources()),
					item );
		}

		if ( item instanceof Shield)
		{
			buyItem( ship.type.shieldSlots, 
					ship.shield, 
					item.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)),
					item.toXmlString(getResources()),
					item );
		}

		if ( item instanceof Gadget)
		{
			if (ship.hasGadget((Gadget)item) && Gadget.EXTRABAYS != item)
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_notuseful, R.string.screen_buyeq_dialog_notuseful_message, R.string.help_nomoreofitem));
			else
			{
				buyItem( ship.type.gadgetSlots, 
						ship.gadget, 
						item.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)),
						item.toXmlString(getResources()),
						item );
			}
		}

	}
	
	
	/*
	 * BuyShipEvent.c
	 */
	// *************************************************************************
	// Create a new ship.
	// *************************************************************************
	private void createShip( ShipType index )
	{
		// NB this looks different from original because most of the 
		// functionality is now handled by Ship class constructor.
		ship = new Ship(this, index);
		ship.crew[0] = commander();
		for (TradeItem item : TradeItem.values()) {
			buyingPrice.put(item, 0);
		}
		
	}


	// *************************************************************************
	// Buy a new ship.
	// *************************************************************************
	private void buyShip( ShipType index )
	{
		CrewMember[] crew = ship.crew;
		createShip( index );
		for (int i = 1; i < ship.crew.length; i++) {
			ship.crew[i] = crew[i];
		}
		credits -= shipPrice.get(index);
		if (scarabStatus == 3)
			scarabStatus = 0;
	}
	
	
	// *************************************************************************
	// Determine Ship Prices depending on tech level of current system.
	// *************************************************************************
	public void determineShipPrices( )
	{
		for (ShipType type : ShipType.buyableValues())
		{
			if (type.minTechLevel.compareTo(curSystem().techLevel()) <= 0)
			{
				int price = type.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER)) - ship.currentPrice( false );
				if (price == 0) 
					price = 1;

				shipPrice.put(type, price);
			}
			else
				shipPrice.put(type, 0);
		}
	}
	
	// *************************************************************************
	// You get a Flea
	// *************************************************************************
	private void createFlea(  )
	{
		createShip( ShipType.FLEA );
		
		escapePod = false;
		insurance = false;
		noClaim = 0;
	}
	
	public void drawBuyShipForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.BUYSHIP) return;
		
		determineShipPrices();
		for (ShipType type : ShipType.buyableValues())
		{
			screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(type), !(shipPrice.get(type) == 0 || ship.type == type));
		}

		for (ShipType type : ShipType.buyableValues())
		{
			if (shipPrice.get(type) == 0)
				screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(type), R.string.generic_notsold);
			else if (ship.type == type)
				screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(type), R.string.screen_yard_buyship_gotone);
			else 
				screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(type), R.string.format_credits, shipPrice.get(type));
		}
		screen.setViewTextById(R.id.screen_yard_buyship_credits, R.string.format_cash, credits);

		if (ship.tribbles > 0 && !tribbleMessage)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_yard_buyship_tribbles_title, R.string.screen_yard_buyship_tribbles_message, R.string.help_shipnotworthmuch));
			tribbleMessage = true;
		}	
		
//		// Screenshot override
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.FLEA), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.GNAT), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.FIREFLY), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.MOSQUITO), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.BUMBLEBEE), false);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.BEETLE), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.HORNET), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.GRASSHOPPER), true);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.TERMITE), false);
//		screen.setViewVisibilityById(BuyShipScreen.BUY_IDS.get(ShipType.WASP), false);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.FLEA), R.string.format_credits, -43200);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.GNAT), R.string.format_credits, -36000);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.FIREFLY), R.string.format_credits, -22500);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.MOSQUITO), R.string.format_credits, -18000);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.BUMBLEBEE), R.string.screen_yard_buyship_gotone);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.BEETLE), R.string.format_credits, 27000);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.HORNET), R.string.format_credits, 45000);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.GRASSHOPPER), R.string.format_credits, 90000);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.TERMITE), R.string.generic_notsold);
//		screen.setViewTextById(BuyShipScreen.PRICE_IDS.get(ShipType.WASP), R.string.generic_notsold);
//		screen.setViewTextById(R.id.screen_yard_buyship_credits, R.string.format_cash, 378511);
		
	}

	// *************************************************************************
	// Handling of the events of the Buy Ship form.
	// *************************************************************************
	public void buyShipFormHandleEvent( int buttonId )
	{
		ShipType type = null;
		for (ShipType i : ShipType.buyableValues()) {
			if (BuyShipScreen.INFO_IDS.get(i) == buttonId || BuyShipScreen.BUY_IDS.get(i) == buttonId) {
				type = i;
				break;
			}
		}
		selectedShipType = type;
		if (BuyShipScreen.INFO_IDS.containsValue(buttonId))
		{
			mGameManager.showDialogFragment(ShipInfoDialog.newInstance());
		}
		else if (BuyShipScreen.BUY_IDS.containsValue(buttonId))
		{
			new BuyShipTask().execute();
		}
	}
	
	private class BuyShipTask extends AsyncTask<Void, Void, Void> {
		private int extra = 0;
		private boolean hasLightning = false;
		private boolean hasCompactor = false;
		private boolean hasMorganLaser = false;
		private boolean addLightning = false;
		private boolean addCompactor = false;
		private boolean addMorganLaser = false;

		@Override
		protected Void doInBackground(Void... arg0) {
			int j = 0;
			for (int i=0; i<ship.crew.length; ++i)
				if (ship.crew[i] != null)
					++j;
			// NB two new checks here so that Jarek and Wild are part of the crew total. In original it was possible to downgrade to a ship with two quarters when one of these passengers and a mercenary were present.
			if (jarekStatus == 1)
				++j;
			if (wildStatus == 1)
				++j;
			if (shipPrice.get(selectedShipType) == 0)
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_yard_buyship_notavailable_title, 
						R.string.screen_yard_buyship_notavailable_message, 
						R.string.help_itemnotsold)); // NB Not quite sure if the help text here is correct, but that's ok because the dialog shouldn't ever appear anyway since the button won't be drawn.
			else if ((shipPrice.get(selectedShipType) >= 0) &&
					(debt > 0))
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_youreindebt_title, 
						R.string.dialog_youreindebt_message,
						R.string.help_youreindebt));
			else if (shipPrice.get(selectedShipType) > toSpend())
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_yard_buyship_notenoughmoney_title, 
						R.string.screen_yard_buyship_notenoughmoney_message,
						R.string.help_cantbuyship));
			// NB a new check here if both Wild and Jarek are on board
			else if ((jarekStatus == 1) && (wildStatus == 1) && (selectedShipType.crewQuarters < 3))
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_passengerneedsquarters_title, 
						R.string.dialog_special_passengerneedsquarters_message, 
						R.string.help_passengersneedsquarters,
						getResources().getString(R.string.dialog_special_passenger_both, getResources().getString(R.string.dialog_special_passenger_jarek), getResources().getString(R.string.dialog_special_passenger_wild))));
			else if ((jarekStatus == 1) && (selectedShipType.crewQuarters < 2))
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_passengerneedsquarters_title, 
						R.string.dialog_special_passengerneedsquarters_message, 
						R.string.help_jarekneedsquarters,
						getResources().getString(R.string.dialog_special_passenger_jarek)));
			else if ((wildStatus == 1) && (selectedShipType.crewQuarters < 2))
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_passengerneedsquarters_title, 
						R.string.dialog_special_passengerneedsquarters_message, 
						R.string.help_jarekneedsquarters,
						getResources().getString(R.string.dialog_special_passenger_wild)));
			else if (reactorStatus > 0 && reactorStatus < 21)
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_shipwithreactor_title, 
						R.string.dialog_special_shipwithreactor_message,
						R.string.help_cantsellshipwithreactor));
			else
			{	

				extra = 0;
				hasLightning = false;
				hasCompactor = false;
				hasMorganLaser = false;
				addLightning = false;
				addCompactor = false;
				addMorganLaser = false;

				// NB added else statements so that hasEquip vars and extra are only modified if ship we are switching to has slots.
				if (ship.hasShield(Shield.LIGHTNING))
				{

					if (selectedShipType.shieldSlots == 0)
					{
						// can't transfer the Lightning Shields. How often would this happen?
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_yard_buyship_canttransferslot_title,
								R.string.screen_yard_buyship_canttransferslot_message,
								R.string.help_canttransfer,
								newUnlocker(latch),
								selectedShipType,
								Shield.LIGHTNING,
								EquipmentType.SHIELD));
						lock(latch);
					}
					else
					{
						hasLightning = true;
						extra += 30000;
					}
				}

				if (ship.hasGadget(Gadget.FUELCOMPACTOR))
				{
					if (selectedShipType.gadgetSlots == 0)
					{
						// can't transfer the Fuel Compactor
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_yard_buyship_canttransferslot_title,
								R.string.screen_yard_buyship_canttransferslot_message,
								R.string.help_canttransfer,
								newUnlocker(latch),
								selectedShipType,
								Gadget.FUELCOMPACTOR,
								EquipmentType.GADGET));
						lock(latch);
					}
					else
					{
						hasCompactor = true;
						extra += 20000;
					}
				}

				if (ship.hasWeapon(Weapon.MORGAN, true))
				{
					if (selectedShipType.weaponSlots == 0)
					{
						// can't transfer the Laser
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_yard_buyship_canttransferslot_title,
								R.string.screen_yard_buyship_canttransferslot_message,
								R.string.help_canttransfer,
								newUnlocker(latch),
								selectedShipType,
								Weapon.MORGAN,
								EquipmentType.WEAPON));
						lock(latch);
					}
					else
					{
						extra += 33333;
						hasMorganLaser = true;
					}
				}

				if (shipPrice.get(selectedShipType) + extra > toSpend())
				{
					CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_yard_buyship_notenoughmoney_title,
							R.string.screen_yard_buyship_notenoughmoney_specialmessage,
							R.string.help_cantbuyship,
							newUnlocker(latch)));
					lock(latch);
				}

				extra = 0;

				// NB modified statements in following so that we add item price as well as extra cost when comparing to toSpend().
				// This should prevent the bug in the original where the player could get negative credits here.
				if (hasLightning && selectedShipType.shieldSlots > 0)
				{
					if (shipPrice.get(selectedShipType) + extra + 30000 <= toSpend())
					{
						final CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(ConfirmDialog.newInstance(
								R.string.screen_yard_buyship_transferequip_title,
								R.string.screen_yard_buyship_transferequip_message, 
								R.string.help_transferlightningshield,
								new OnConfirmListener() {
									
									@Override
									public void onConfirm() {
										addLightning = true;
										extra += 30000;
										unlock(latch);
									}
								},
								new OnCancelListener() {
									
									@Override
									public void onCancel() {
										unlock(latch);
									}
								},
								Shield.LIGHTNING,
								getResources().getString(R.string.screen_yard_buyship_lightningshield),
								30000));
						lock(latch);
					}
					else
					{
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_yard_buyship_notransfer_title,
								R.string.screen_yard_buyship_notransfer_message,
								R.string.help_canttransferall,
								newUnlocker(latch),
								Shield.LIGHTNING));
						lock(latch);
					}
				}

				if (hasCompactor && selectedShipType.gadgetSlots > 0)
				{
					if (shipPrice.get(selectedShipType) + extra + 20000 <= toSpend())
					{
						final CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(ConfirmDialog.newInstance(
								R.string.screen_yard_buyship_transferequip_title,
								R.string.screen_yard_buyship_transferequip_message, 
								R.string.help_transferfuelcompactor,
								new OnConfirmListener() {
									
									@Override
									public void onConfirm() {
										addCompactor = true;
										extra += 20000;
										unlock(latch);
									}
								},
								new OnCancelListener() {
									
									@Override
									public void onCancel() {
										unlock(latch);
									}
								},
								Gadget.FUELCOMPACTOR,
								getResources().getString(R.string.screen_yard_buyship_fuelcompactor),
								20000));
						lock(latch);
					}
					else
					{
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_yard_buyship_notransfer_title,
								R.string.screen_yard_buyship_notransfer_message,
								R.string.help_canttransferall,
								newUnlocker(latch),
								Gadget.FUELCOMPACTOR));
						lock(latch);
					}
				}

				if (hasMorganLaser && selectedShipType.weaponSlots > 0)
				{
					if (shipPrice.get(selectedShipType) + extra + 33333 <= toSpend())
					{
						final CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(ConfirmDialog.newInstance(
								R.string.screen_yard_buyship_transferequip_title,
								R.string.screen_yard_buyship_transferequip_message, 
								R.string.help_transfermorganslaser,
								new OnConfirmListener() {
									
									@Override
									public void onConfirm() {
										addMorganLaser = true;
										extra += 33333;
										unlock(latch);
									}
								},
								new OnCancelListener() {
									
									@Override
									public void onCancel() {
										unlock(latch);
									}
								},
								Weapon.MORGAN,
								getResources().getString(R.string.screen_yard_buyship_morganslaser),
								33333));
						lock(latch);
					}
					else
					{
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_yard_buyship_notransfer_title,
								R.string.screen_yard_buyship_notransfer_message,
								R.string.help_canttransferall,
								newUnlocker(latch),
								Weapon.MORGAN));
						lock(latch);
					}
					
				}

				if (j > selectedShipType.crewQuarters) {
					CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_yard_buyship_toomanycrew_title, 
							R.string.screen_yard_buyship_toomanycrew_message,
							R.string.help_toomanycrewmembers,
							newUnlocker(latch)));
					lock(latch);
				}
				else
				{
					int buyMessageId;
					if (addCompactor || addLightning || addMorganLaser)
					{
						buyMessageId = R.string.screen_yard_buyship_buy_extramessage;
					}
					else
					{
						buyMessageId = R.string.screen_yard_buyship_buy_message;
					}

					final boolean fAddCompactor = addCompactor;
					final boolean fAddLightning = addLightning;
					final boolean fAddMorganLaser = addMorganLaser;
					final CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(ConfirmDialog.newInstance(
							R.string.screen_yard_buyship_buy_title, 
							buyMessageId, 
							R.string.help_tradeship,
							new OnConfirmListener() {

								@Override
								public void onConfirm() {
									buyShip( selectedShipType );
									credits -= extra;
									if (fAddCompactor)
										ship.gadget[0] = Gadget.FUELCOMPACTOR;
									if (fAddLightning)
										ship.shield[0] = Shield.LIGHTNING;
									if (fAddMorganLaser)
										ship.weapon[0] = Weapon.MORGAN;
									ship.tribbles = 0;		
								
									unlock(latch);
								}
							},
							new OnCancelListener() {
								
								@Override
								public void onCancel() {
									unlock(latch);
								}
							},
							ship.type,
							selectedShipType));
					lock(latch);
				}
			}
			
			return null;
		}
		
		@Override
		public void onPostExecute(Void result) {
			drawBuyShipForm();
		}

	}
	
	/*
	 * Cargo.c
	 */
	// *************************************************************************
	// Let the commander indicate how many he wants to sell or dump
	// Operation is SELLCARGO or DUMPCARGO
	// *************************************************************************
	public void getAmountToSell( final TradeItem item, final SellOperation operation  )
	{
		int titleId;
		if (operation == SellOperation.SELL)
		{
			titleId = R.string.format_sellitem;
		}
		else
		{
			titleId = R.string.format_discarditem;
		}

		int messageId;
		Object[] args;
		if (operation == SellOperation.SELL)
		{
			if (buyingPrice.get(item) / ship.getCargo(item) > sellPrice.get(item))
			{
				messageId = R.string.screen_sell_sellquery;
				args = new Object[] {
						item,
						ship.getCargo(item),
						sellPrice.get(item),
						buyingPrice.get(item) / ship.getCargo(item),
						getResources().getString(R.string.screen_sell_loss),
						(buyingPrice.get(item) / ship.getCargo(item) - sellPrice.get(item))
				};
			}
			else if (buyingPrice.get(item) / ship.getCargo(item) < sellPrice.get(item))
			{
				messageId = R.string.screen_sell_sellquery;
				args = new Object[] {
						item,
						ship.getCargo(item),
						sellPrice.get(item),
						buyingPrice.get(item) / ship.getCargo(item),
						getResources().getString(R.string.screen_sell_profit),
						sellPrice.get(item) - (buyingPrice.get(item) / ship.getCargo(item))
				};
			}
			else
			{
				messageId = R.string.screen_sell_sellnoprofitquery;
				args = new Object[] {
						item,
						ship.getCargo(item),
						sellPrice.get(item),
						buyingPrice.get(item) / ship.getCargo(item),
				};
			}
		}
		else if (operation == SellOperation.DUMP)
		{
			messageId = R.string.screen_sell_dumpquery;
			args = new Object[] {
					item,
					min(ship.getCargo(item), toSpend()/ (5 * (difficulty.ordinal() + 1))),
					buyingPrice.get(item) / ship.getCargo(item),
					(5 * (difficulty.ordinal() + 1))
			};
		}
		else
		{
			messageId = R.string.dialog_jettison_query;
			args = new Object[] {
					item,
					ship.getCargo(item),
					buyingPrice.get(item) / ship.getCargo(item)
			};
		}
		
		mGameManager.showDialogFragment(InputDialog.newInstance(
				titleId, 
				messageId,
				R.string.generic_ok,
				R.string.generic_all,
				R.string.generic_none,
				R.string.help_amounttosell,
				new OnPositiveListener() {
					
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) sellCargo(item, value, operation);
					}
				},
				new OnNeutralListener() {
					
					@Override
					public void onClickNeutralButton() {
						sellCargo(item, 999, operation);
					}
				},
				args));

	}
	
	// *************************************************************************
	// Determines if a given ship is carrying items that can be bought or sold
	// in a specified system.
	// *************************************************************************
	public boolean hasTradeableItems (Ship sh, boolean sell)
	{
		boolean ret = false;
		for (TradeItem item : TradeItem.values())
		{
			// trade only if trader is selling and the item has a buy price on the
			// local system, or trader is buying, and there is a sell price on the
			// local system.
			boolean thisRet = false;
			if (sh.getCargo(item) > 0 && sell && buyPrice.get(item) > 0)
				thisRet = true;
			else if (sh.getCargo(item) > 0 && !sell && sellPrice.get(item) > 0)
				thisRet = true;
				
			// Criminals can only buy or sell illegal goods, Noncriminals cannot buy
			// or sell such items.
			if (policeRecordScore < PoliceRecord.DUBIOUS.score && item != TradeItem.FIREARMS && item != TradeItem.NARCOTICS)
			    thisRet = false;
			else if (policeRecordScore >= PoliceRecord.DUBIOUS.score && (item == TradeItem.FIREARMS || item == TradeItem.NARCOTICS))
			    thisRet = false;
			    
			if (thisRet)
				ret = true;

		}
		
		return ret;
	}
	
	// *************************************************************************
	// Returns the index of a trade good that is on a given ship that can be
	// sold in a given system.
	// *************************************************************************
	public TradeItem getRandomTradeableItem (Ship sh, boolean sell)
	{
		boolean looping = true;
		int i=0;
		TradeItem item = null;
		
		while (looping && i < 10) 
		{
			item = getRandom(TradeItem.values());
			// It's not as ugly as it may look! If the ship has a particulat item, the following
			// conditions must be met for it to be tradeable:
			// if the trader is buying, there must be a valid sale price for that good on the local system
			// if the trader is selling, there must be a valid buy price for that good on the local system
			// if the player is criminal, the good must be illegal
			// if the player is not criminal, the good must be legal 
			if ( (sh.getCargo(item) > 0 && sell && buyPrice.get(item) > 0) &&
			     ((policeRecordScore < PoliceRecord.DUBIOUS.score && (item == TradeItem.FIREARMS || item == TradeItem.NARCOTICS)) ||
			      (policeRecordScore >= PoliceRecord.DUBIOUS.score && item != TradeItem.FIREARMS && item != TradeItem.NARCOTICS)) )
				looping = false;
			else if ( (sh.getCargo(item) > 0 && !sell &&  sellPrice.get(item) > 0)  &&
			     ((policeRecordScore < PoliceRecord.DUBIOUS.score && (item == TradeItem.FIREARMS || item == TradeItem.NARCOTICS)) ||
			      (policeRecordScore >= PoliceRecord.DUBIOUS.score && item != TradeItem.FIREARMS && item != TradeItem.NARCOTICS)) )
				looping = false;
			// alles klar?
			else
			{
				item = null;
				i++;
			}
		}
		// if we didn't succeed in picking randomly, we'll pick sequentially. We can do this, because
		// this routine is only called if there are tradeable goods.
		if (item == null)
		{
			item = TradeItem.values()[0];
			looping = true;
			while (looping)
			{
				// see lengthy comment above.
				if ( (((sh.getCargo(item) > 0 && sell && buyPrice.get(item) > 0)) ||
				    ((sh.getCargo(item) > 0 && !sell &&  sellPrice.get(item) > 0))) &&
			     	((policeRecordScore < PoliceRecord.DUBIOUS.score && (item == TradeItem.FIREARMS || item == TradeItem.NARCOTICS)) ||
			      	(policeRecordScore >= PoliceRecord.DUBIOUS.score && item != TradeItem.FIREARMS && item != TradeItem.NARCOTICS)) )
				    
				{
					looping = false;
				}
				else
				{
					int j = item.ordinal();
					j++;
					if (j == TradeItem.values().length)
					{
						// this should never happen!
						looping = false;
					} else {
						item = TradeItem.values()[j];
					}
				}
			}
		}
		return item;
	}
	
	
	
	// *************************************************************************
	// Let the commander indicate how many he wants to buy
	// *************************************************************************
	public void getAmountToBuy( final TradeItem item )       // used in Traveler.c also
	{

		if (buyPrice.get(item) <= 0 || curSystem().getQty(item) <= 0)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_buy_notavailable_title,
					R.string.screen_buy_notavailable_message,
					R.string.help_nothingavailable));
			return;
		}

		int count = min( toSpend() / buyPrice.get(item), curSystem().getQty(item) );
		int queryRes;
		if (count <= 0)
			queryRes = R.string.screen_buy_query_none;
		else if (count < 1000)
		{
			queryRes = R.string.screen_buy_query;
		}
		else
			queryRes = R.string.screen_buy_query_many;

		mGameManager.showDialogFragment(InputDialog.newInstance(
				R.string.format_buyitem,
				queryRes,
				R.string.generic_ok, 
				R.string.generic_all, 
				R.string.generic_none, 
				R.string.help_amounttobuy,
				new OnPositiveListener() {
					
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) buyCargo(item, value);
					}
				}, 
				new OnNeutralListener() {
					
					@Override
					public void onClickNeutralButton() {
						buyCargo(item, 999);
					}
				}, 
				item,
				buyPrice.get(item),
				min( toSpend() / buyPrice.get(item), curSystem().getQty(item) )				
				));


	}	
	
	
	public void drawPlunderForm()
	{
		BaseDialog dialog = mGameManager.findDialogByClass(PlunderDialog.class);
		for (TradeItem item : TradeItem.values())
		{
			dialog.setViewTextById(PlunderDialog.AMOUNT_IDS.get(item), R.string.format_number, opponent.getCargo(item));
		}
		dialog.setViewTextById(R.id.dialog_plunder_bays, R.string.format_bays, ship.filledCargoBays(), ship.totalCargoBays());
	}

	// *************************************************************************
	// Handling the plundering of a trader.
	// Cannot be moved without moving all functions that use QtyBuf to same file
	// *************************************************************************
	public void plunderFormHandleEvent( int buttonId )
	{
		TradeItem item = null;
		for (TradeItem i : TradeItem.values()) {
			if (PlunderDialog.ALL_IDS.get(i) == buttonId || PlunderDialog.AMOUNT_IDS.get(i) == buttonId) {
				item = i;
				break;
			}
		}
		
		if (PlunderDialog.ALL_IDS.containsValue(buttonId))
			plunderCargo( item, 999 );
		else if (PlunderDialog.AMOUNT_IDS.containsValue(buttonId))
		{
			if (opponent.getCargo(item) <= 0)
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_notavailable_title, R.string.dialog_plunder_nothing, R.string.help_victimdoesnthaveany));
			else
			{
				getAmountToPlunder( item );
			}
		}
		else if (buttonId == R.id.dialog_plunder_dump)
		{
			mGameManager.showDialogFragment(JettisonDialog.newInstance(null));
		}
		else
		{
			if (encounterType == Encounter.VeryRare.MARIECELESTE && ship.getCargo(TradeItem.NARCOTICS) > narcs)
				justLootedMarie = true;
			travel();
		}
	}
	
	
	public void drawBuyCargoForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.BUY) return;
		
		for (TradeItem item : TradeItem.values())
		{
			screen.setViewVisibilityById(BuyScreen.AMOUNT_IDS.get(item), buyPrice.get(item) > 0);
			screen.setViewVisibilityById(BuyScreen.MAX_IDS.get(item), buyPrice.get(item) > 0);

			if (buyPrice.get(item) > 0)
				screen.setViewTextById(BuyScreen.PRICE_IDS.get(item), R.string.format_credits, buyPrice.get(item));
			else
				screen.setViewTextById(BuyScreen.PRICE_IDS.get(item), R.string.generic_notsold);

			screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(item), R.string.format_number, curSystem().getQty(item));
		}
		screen.setViewTextById(R.id.screen_buy_bays, R.string.format_bays, ship.filledCargoBays(), ship.totalCargoBays());
		screen.setViewTextById(R.id.screen_buy_credits, R.string.format_cash, credits);

//		// Screenshot override
//		for (TradeItem item : TradeItem.values())
//		{
//			screen.setViewVisibilityById(BuyScreen.AMOUNT_IDS.get(item), true);
//			screen.setViewVisibilityById(BuyScreen.MAX_IDS.get(item), true);
//		}
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.WATER), R.string.format_number, 18);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.FURS), R.string.format_number, 1);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.FOOD), R.string.format_number, 40);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.ORE), R.string.format_number, 39);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.GAMES), R.string.format_number, 10);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.FIREARMS), R.string.format_number, 24);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.MEDICINE), R.string.format_number, 47);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.MACHINERY), R.string.format_number, 15);
//		screen.setViewVisibilityById(BuyScreen.AMOUNT_IDS.get(TradeItem.NARCOTICS), false);
//		screen.setViewVisibilityById(BuyScreen.MAX_IDS.get(TradeItem.NARCOTICS), false);
//		screen.setViewTextById(BuyScreen.AMOUNT_IDS.get(TradeItem.ROBOTS), R.string.format_number, 9);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.WATER), R.string.format_credits, 46);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.FURS), R.string.format_credits, 312);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.FOOD), R.string.format_credits, 99);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.ORE), R.string.format_credits, 474);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.GAMES), R.string.format_credits, 188);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.FIREARMS), R.string.format_credits, 738);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.MEDICINE), R.string.format_credits, 532);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.MACHINERY), R.string.format_credits, 970);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.NARCOTICS), R.string.generic_notsold);
//		screen.setViewTextById(BuyScreen.PRICE_IDS.get(TradeItem.ROBOTS), R.string.format_credits, 4075);
//		screen.setViewTextById(R.id.screen_buy_bays, R.string.format_bays, 30,30);
//		screen.setViewTextById(R.id.screen_buy_credits, R.string.format_cash, 197761);
		

	}

	// *************************************************************************
	// Handling the events of the Buy Cargo form.
	// Cannot be moved without moving all functions that use QtyBuf to same file
	// *************************************************************************
	public void buyCargoFormHandleEvent(int buttonId)
	{
		TradeItem item = null;
		for (TradeItem i : TradeItem.values()) {
			if (BuyScreen.MAX_IDS.get(i) == buttonId || BuyScreen.AMOUNT_IDS.get(i) == buttonId) {
				item = i;
				break;
			}
		}
		
		if (BuyScreen.MAX_IDS.containsValue(buttonId))
			buyCargo( item, 999 );
		else
		{
			getAmountToBuy(item);
		}
	}
	
	public void drawSellCargoForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.SELL) return;
		
		for (TradeItem item : TradeItem.values())
		{
			View amt = screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(item));
			if (sellPrice.get(item) <= 0)
			{
				amt.getBackground().setAlpha(0);
				amt.setClickable(false);
				screen.setViewTextById(SellScreen.ALL_IDS.get(item), R.string.generic_dump);
			}
			else
			{
				amt.getBackground().setAlpha(255);
				amt.setClickable(true);
				screen.setViewTextById(SellScreen.ALL_IDS.get(item), R.string.generic_all);
			}
		}
		
		for (TradeItem item : TradeItem.values())
		{
			if (ship.getCargo(item) > 0 && sellPrice.get(item) > buyingPrice.get(item) / ship.getCargo(item)) {
				((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(item))).setTypeface( Typeface.DEFAULT_BOLD );
			} else {
				((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(item))).setTypeface( Typeface.DEFAULT );
			}
			
			if (sellPrice.get(item) > 0)
				screen.setViewTextById(SellScreen.PRICE_IDS.get(item), R.string.format_credits, sellPrice.get(item));
			else
				screen.setViewTextById(SellScreen.PRICE_IDS.get(item), R.string.generic_notrade);
				
			screen.setViewTextById(SellScreen.AMOUNT_IDS.get(item), R.string.format_number, ship.getCargo(item));
		}

		screen.setViewTextById(R.id.screen_sell_bays, R.string.format_bays, ship.filledCargoBays(), ship.totalCargoBays());
		screen.setViewTextById(R.id.screen_sell_credits, R.string.format_cash, credits);
		
//		// Screenshot override
//		TypedValue tv = new TypedValue();
//		mGameManager.getTheme().resolveAttribute(R.attr.squareButtonDrawable, tv, true);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.WATER)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.FURS)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.FOOD)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.ORE)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.GAMES)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.FIREARMS)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.MEDICINE)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.MACHINERY)).setBackgroundResource(tv.resourceId);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.NARCOTICS)).setBackgroundResource(0);
//		screen.getView().findViewById(SellScreen.AMOUNT_IDS.get(TradeItem.ROBOTS)).setBackgroundResource(tv.resourceId);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.WATER), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.FURS), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.FOOD), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.ORE), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.GAMES), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.FIREARMS), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.MEDICINE), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.MACHINERY), R.string.generic_all);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.NARCOTICS), R.string.generic_dump);
//		screen.setViewTextById(SellScreen.ALL_IDS.get(TradeItem.ROBOTS), R.string.generic_all);
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.WATER))).setTypeface( Typeface.DEFAULT_BOLD );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.FURS))).setTypeface( Typeface.DEFAULT_BOLD );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.FOOD))).setTypeface( Typeface.DEFAULT );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.ORE))).setTypeface( Typeface.DEFAULT_BOLD );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.GAMES))).setTypeface( Typeface.DEFAULT );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.FIREARMS))).setTypeface( Typeface.DEFAULT );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.MEDICINE))).setTypeface( Typeface.DEFAULT );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.MACHINERY))).setTypeface( Typeface.DEFAULT );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.NARCOTICS))).setTypeface( Typeface.DEFAULT );
//		((TextView) screen.getView().findViewById(SellScreen.LABEL_IDS.get(TradeItem.ROBOTS))).setTypeface( Typeface.DEFAULT );
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.WATER), R.string.format_credits, 45);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.FURS), R.string.format_credits, 303);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.FOOD), R.string.format_credits, 97);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.ORE), R.string.format_credits, 461);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.GAMES), R.string.format_credits, 183);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.FIREARMS), R.string.format_credits, 717);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.MEDICINE), R.string.format_credits, 517);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.MACHINERY), R.string.format_credits, 942);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.NARCOTICS), R.string.generic_notrade);
//		screen.setViewTextById(SellScreen.PRICE_IDS.get(TradeItem.ROBOTS), R.string.format_credits, 3957);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.WATER), R.string.format_number, 19);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.FURS), R.string.format_number, 1);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.FOOD), R.string.format_number, 0);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.ORE), R.string.format_number, 10);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.GAMES), R.string.format_number, 0);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.FIREARMS), R.string.format_number, 0);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.MEDICINE), R.string.format_number, 0);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.MACHINERY), R.string.format_number, 0);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.NARCOTICS), R.string.format_number, 0);
//		screen.setViewTextById(SellScreen.AMOUNT_IDS.get(TradeItem.ROBOTS), R.string.format_number, 0);
//		screen.setViewTextById(R.id.screen_sell_bays, R.string.format_bays, 30, 30);
//		screen.setViewTextById(R.id.screen_sell_credits, R.string.format_cash, 197761);
		
	}
	
	// *************************************************************************
	// Handling the events of the Sell Cargo form.
	// Cannot be moved without moving all functions that use QtyBuf to same file
	// *************************************************************************
	public void sellCargoFormHandleEvent( int buttonId )
	{
		TradeItem item = null;
		for (TradeItem i : TradeItem.values()) {
			if (SellScreen.ALL_IDS.get(i) == buttonId || SellScreen.AMOUNT_IDS.get(i) == buttonId) {
				item = i;
				break;
			}
		}
		
		if (SellScreen.ALL_IDS.containsValue(buttonId) && sellPrice.get(item) > 0)
		{
			sellCargo( item, 999, SellOperation.SELL );
		}
		else if (SellScreen.ALL_IDS.containsValue(buttonId))
		{
			if (ship.getCargo(item) <= 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
				return;
			}
			getAmountToSell( item, SellOperation.DUMP );
			drawSellCargoForm();
		}
		else if (sellPrice.get(item) > 0)
		{
			if (ship.getCargo(item) <= 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_nogoods, R.string.screen_sell_nogoods_message, R.string.help_nothingforsale));
				return;
			}
			if (sellPrice.get(item) <= 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_notinterested, R.string.screen_sell_notinterested_message, R.string.help_notinterested));
				return;
			}
			getAmountToSell( item, SellOperation.SELL );
			drawSellCargoForm();
		}
	}
	
	
	// *************************************************************************
	// Show contents of Dump cargo form.
	// *************************************************************************
	public void showDumpCargo(  )
	{
		BaseDialog dialog = mGameManager.findDialogByClass(JettisonDialog.class);
		
		for (TradeItem item : TradeItem.values())
		{
			dialog.setViewTextById(JettisonDialog.AMOUNT_IDS.get(item), R.string.format_number, ship.getCargo(item));
		}
		dialog.setViewVisibilityById(R.id.dialog_plunder_dump, false);
		dialog.setViewTextById(R.id.dialog_plunder_bays, R.string.format_bays, ship.filledCargoBays(), ship.totalCargoBays());
	}
		
	// *************************************************************************
	// Handling the events of the Discard Cargo form.
	// Cannot be moved without moving all functions that use QtyBuf to same file
	// *************************************************************************
	public void discardCargoFormHandleEvent( int buttonId )
	{
		TradeItem item = null;
		for (TradeItem i : TradeItem.values()) {
			if (JettisonDialog.ALL_IDS.get(i) == buttonId || JettisonDialog.AMOUNT_IDS.get(i) == buttonId) {
				item = i;
				break;
			}
		}

		if (JettisonDialog.ALL_IDS.containsValue(buttonId))
		{
			if (ship.getCargo(item) <= 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
				return;
			}
			final TradeItem fItem = item;
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_dumpall_title,
					R.string.dialog_dumpall_message,
					R.string.help_dumpall,
					new OnConfirmListener() {
						
						@Override
						public void onConfirm() {
							sellCargo( fItem, 999, SellOperation.JETTISON );
						}
					},
					null,
					item,
					buyingPrice.get(item)));
			
		}
		else
		{
			if (ship.getCargo(item) <= 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
				return;
			}
			getAmountToSell( item, SellOperation.JETTISON );
		}
	}
	
	
	// *************************************************************************
	// Plunder amount of cargo
	// *************************************************************************
	public void plunderCargo( TradeItem item, int amount )
	{
		
		if (opponent.getCargo(item) <= 0)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_notavailable_title, R.string.dialog_plunder_nothing, R.string.help_victimdoesnthaveany));
			return;
		}

		if (ship.totalCargoBays() - ship.filledCargoBays() <= 0)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_noemptybays_title, R.string.screen_buy_noemptybays_message, R.string.help_noemptybays));
			return;
		}
		
		int toPlunder = min( amount, opponent.getCargo(item) );
		toPlunder = min( toPlunder, ship.totalCargoBays() - ship.filledCargoBays() );
		
		ship.addCargo(item, toPlunder);
		opponent.addCargo(item, -toPlunder);
		
		mGameManager.findDialogByClass(PlunderDialog.class).onRefreshDialog();
	}
	
	
	// *************************************************************************
	// Let the commander indicate how many he wants to plunder
	// *************************************************************************
	public void getAmountToPlunder( final TradeItem item )
	{
		mGameManager.showDialogFragment(InputDialog.newInstance(
				R.string.dialog_plunder_title, 
				R.string.dialog_plunder_query, 
				R.string.generic_ok,
				R.string.generic_all,
				R.string.generic_none,
				R.string.help_amounttoplunder,
				new OnPositiveListener() {
					
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) plunderCargo(item, value); 
					}
				}, 
				new OnNeutralListener() {
					
					@Override
					public void onClickNeutralButton() {
						plunderCargo(item, 999);
					}
				}, 
				item,
				opponent.getCargo(item)));
	}
	
	
	// *************************************************************************
	// Buy amount of cargo
	// *************************************************************************
	public void buyCargo( TradeItem item, int amount )
	{
		int toBuy;

		if (debt > DEBTTOOLARGE)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_debttoolarge_title, R.string.screen_buy_debttoolarge_message, R.string.help_debttoolargeforbuy));
			return;
		}

		if (curSystem().getQty(item) <= 0 || buyPrice.get(item) <= 0)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_nothingavailable_title, R.string.screen_buy_nothingavailable_message, R.string.help_nothingavailable));
			return;
		}

		if (ship.totalCargoBays() - ship.filledCargoBays() - leaveEmpty <= 0)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_noemptybays_title, R.string.screen_buy_noemptybays_message, R.string.help_noemptybays));
			return;
		}

		if (toSpend() < buyPrice.get(item) )
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_notenoughmoney_title, R.string.screen_buy_notenoughmoney_message, R.string.help_cantafford));
			return;
		}

		toBuy = min( amount, curSystem().getQty(item) );
		toBuy = min( toBuy, ship.totalCargoBays() - ship.filledCargoBays() - leaveEmpty );
		toBuy = min( toBuy, toSpend() / buyPrice.get(item) );

		ship.addCargo(item, toBuy);
		credits -= toBuy * buyPrice.get(item);
		buyingPrice.put(item, buyingPrice.get(item) + toBuy * buyPrice.get(item));
		curSystem().addQty(item, -toBuy);
		
		if (mGameManager.getCurrentScreenType() == ScreenType.BUY) {
			drawBuyCargoForm();
		} else if (mGameManager.getCurrentScreenType() == ScreenType.AVGPRICES) {
			showAveragePrices();
			setAdapterSystems(((WarpSubScreen)mGameManager.getCurrentScreen()).getPagerAdapter());
		}

	}
	
	
	// *************************************************************************
	// Sell or Jettison amount of cargo
	// Operation is SELLCARGO, DUMPCARGO, or JETTISONCARGO
	// *************************************************************************
	public void sellCargo( final TradeItem item, final int amount, final SellOperation operation )
	{

		if (ship.getCargo(item) <= 0)
		{
			if (operation == SellOperation.SELL)
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_nogoods, R.string.screen_sell_nogoods_message, R.string.help_nothingforsale));
			else {
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
			}
			return;
		}
		
		if (sellPrice.get(item) <= 0 && operation == SellOperation.SELL)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_sell_notinterested, R.string.screen_sell_notinterested_message, R.string.help_notinterested));
			return;
		}
		
		if (operation == SellOperation.JETTISON)
		{
			if (policeRecordScore > PoliceRecord.DUBIOUS.score && !litterWarning)
			{
				litterWarning = true;
				
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.dialog_spacelittering_title, 
						R.string.dialog_spacelittering_message, 
						R.string.help_spacelittering,
						new OnConfirmListener(	) {
					@Override
					public void onConfirm() {
						// Just call sell again. Since litterWarning is now true we won't trip this dialog a separate time.
						// Not exactly how the original code works but it should have the same effect.
						sellCargo(item, amount, operation);
					}
				}, null));
				return;
			}
		}

		int toSell = min( amount, ship.getCargo(item) );
		
		if (operation == SellOperation.DUMP)
		{
			toSell = min(toSell, toSpend() / 5 * (difficulty.ordinal() + 1));
		}
		
		buyingPrice.put(item, 
				(buyingPrice.get(item) * (ship.getCargo(item) - toSell)) / ship.getCargo(item)
				);
		ship.addCargo(item, -toSell);
		if (operation == SellOperation.SELL)
			credits += toSell * sellPrice.get(item);
		if (operation == SellOperation.DUMP)
			credits -= toSell * 5 * (difficulty.ordinal() + 1);
		if (operation == SellOperation.JETTISON)
		{
			if (getRandom( 10 ) < difficulty.ordinal() + 1)
			{
				if (policeRecordScore > PoliceRecord.DUBIOUS.score)
					policeRecordScore = PoliceRecord.DUBIOUS.score;
				else
					--policeRecordScore;
				addNewsEvent(NewsEvent.CAUGHTLITTERING);
			}
		}
		
		if (operation == SellOperation.SELL || operation == SellOperation.DUMP)
		{
			drawSellCargoForm();
		}
		else
		{
			showDumpCargo();
		}

	}
	
	// *************************************************************************
	// Buy an item: Slots is the number of slots, Item is the array in the
	// Ship record which contains the item type, Price is the costs,
	// Name is the name of the item and ItemIndex is the item type number
	// *************************************************************************
	private void buyItem( final int slots, final Purchasable[] item, final int price, final String name, final Purchasable itemIndex )
	{
		final int firstEmptySlot = getFirstEmptySlot( slots, item );

		if (price <= 0)
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buy_notavailable_title, R.string.screen_buy_notavailable_message, R.string.help_nothingavailable));
		else if (debt > 0)
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_youreindebt_title, R.string.dialog_youreindebt_message, R.string.help_youreindebt));
		else if (price > toSpend())
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_money, R.string.screen_buyeq_dialog_money_message, R.string.help_cantbuyitem));
		else if (firstEmptySlot < 0)
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
		else
		{
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.format_buyitem,
					R.string.screen_buyeq_buyquery,
					R.string.help_buyitem,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
							item[firstEmptySlot] = itemIndex;
							credits -= price;

							mGameManager.getCurrentScreen().setViewTextById(R.id.screen_buyeq_credits, R.string.format_cash, credits);
						}
					},
					null,
					itemIndex,
					price));

		}
	}
	
	// *************************************************************************
	// Drawing the Sell Equipment screen.
	// *************************************************************************
	public void drawSellEquipment(  )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.SELLEQ) return;
		
		screen.setViewVisibilityById(R.id.screen_selleq_weapon_empty, ship.weapon[0] == null);
		screen.setViewVisibilityById(R.id.screen_selleq_weapon_notempty, ship.weapon[0] != null);
		
		for (int i=0; i<ship.weapon.length; ++i)
		{
			
			screen.setViewVisibilityById(SellEqScreen.WEAPON_IDS.get(i), ship.weapon[i] != null);
			if (ship.weapon[i] == null)
			{
				continue;
			}
			
			screen.setViewTextById(SellEqScreen.WEAPON_TYPE_IDS.get(i), ship.weapon[i]);

			screen.setViewTextById(SellEqScreen.WEAPON_PRICE_IDS.get(i), R.string.format_credits, ship.weapon[i].sellPrice());
		}

		screen.setViewVisibilityById(R.id.screen_selleq_shield_empty, ship.shield[0] == null);
		screen.setViewVisibilityById(R.id.screen_selleq_shield_notempty, ship.shield[0] != null);

		for (int i=0; i<ship.shield.length; ++i)
		{
			screen.setViewVisibilityById(SellEqScreen.SHIELD_IDS.get(i), ship.shield[i] != null);
			if (ship.shield[i] == null)
			{
				continue;
			}
			
			screen.setViewTextById(SellEqScreen.SHIELD_TYPE_IDS.get(i), ship.shield[i]);

			screen.setViewTextById(SellEqScreen.SHIELD_PRICE_IDS.get(i), R.string.format_credits, ship.shield[i].sellPrice());
		}

		screen.setViewVisibilityById(R.id.screen_selleq_gadget_empty, ship.gadget[0] == null);
		screen.setViewVisibilityById(R.id.screen_selleq_gadget_notempty, ship.gadget[0] != null);
		
		for (int i=0; i<ship.gadget.length; ++i)
		{
			screen.setViewVisibilityById(SellEqScreen.GADGET_IDS.get(i), ship.gadget[i] != null);
			if (ship.gadget[i] == null)
			{
				continue;
			}
			
			screen.setViewTextById(SellEqScreen.GADGET_TYPE_IDS.get(i), ship.gadget[i]);

			screen.setViewTextById(SellEqScreen.GADGET_PRICE_IDS.get(i), R.string.format_credits, ship.gadget[i].sellPrice());
		}
		
		screen.setViewTextById(R.id.screen_selleq_credits, R.string.format_cash, credits);
	}
	
	/*
	 * CmdrStatusEvent.c
	 */
	// *************************************************************************
	// Show the Commander Status screen
	// *************************************************************************
	public void showCommanderStatus(  )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.STATUS) return;
		
		screen.setViewTextById(R.id.screen_status_name, commander().name);
		
		screen.setViewTextById(R.id.screen_status_pilot, R.string.format_skills, commander().pilot(), ship.skill(Skill.PILOT));
		screen.setViewTextById(R.id.screen_status_fighter, R.string.format_skills, commander().fighter(), ship.skill(Skill.FIGHTER));
		screen.setViewTextById(R.id.screen_status_trader, R.string.format_skills, commander().trader(), ship.skill(Skill.TRADER));
		screen.setViewTextById(R.id.screen_status_engineer, R.string.format_skills, commander().engineer(), ship.skill(Skill.ENGINEER));

		screen.setViewTextById(R.id.screen_status_kills, R.string.format_number, pirateKills + policeKills + traderKills);

		screen.setViewTextById(R.id.screen_status_police, PoliceRecord.forValue(policeRecordScore));

		screen.setViewTextById(R.id.screen_status_rep, Reputation.forValue(reputationScore));

		screen.setViewTextById(R.id.screen_status_diff, difficulty);

		// NB this was always plural in original (ie "1 days")
		screen.setViewTextById(R.id.screen_status_time, getResources().getQuantityString(R.plurals.format_days, days, days));

		screen.setViewTextById(R.id.screen_status_cash, R.string.format_credits, credits);

		screen.setViewTextById(R.id.screen_status_debt, R.string.format_credits, debt);

		screen.setViewTextById(R.id.screen_status_worth, R.string.format_credits, currentWorth());

		screen.setViewVisibilityById(R.id.screen_status_cheat, cheatCounter == 3);
		
		
		cheatCounter = 0;
		
//		// Screenshot override
//		screen.setViewTextById(R.id.screen_status_name, "Jameson");
//		screen.setViewTextById(R.id.screen_status_pilot, R.string.format_skills, 10, 13);
//		screen.setViewTextById(R.id.screen_status_fighter, R.string.format_skills, 8, 11);
//		screen.setViewTextById(R.id.screen_status_trader, R.string.format_skills, 1, 10);
//		screen.setViewTextById(R.id.screen_status_engineer, R.string.format_skills, 1, 9);
//		screen.setViewTextById(R.id.screen_status_kills, R.string.format_number, 2);
//		screen.setViewTextById(R.id.screen_status_police, PoliceRecord.LAWFUL);
//		screen.setViewTextById(R.id.screen_status_rep, Reputation.HARMLESS);
//		screen.setViewTextById(R.id.screen_status_diff, DifficultyLevel.NORMAL);
//		screen.setViewTextById(R.id.screen_status_time, getResources().getQuantityString(R.plurals.format_days, 9, 9));
//		screen.setViewTextById(R.id.screen_status_cash, R.string.format_credits, 202570);
//		screen.setViewTextById(R.id.screen_status_debt, R.string.format_credits, 0);
//		screen.setViewTextById(R.id.screen_status_worth, R.string.format_credits, 433195);
	}
	
	// *************************************************************************
	// Handling of events on the Commander Status screen
	// *************************************************************************
	public void commanderStatusFormHandleEvent(int buttonId)
	{
		if (buttonId == R.id.screen_status_cheat) {
			CheatDialog dialog = CheatDialog.newInstance();
			mGameManager.showDialogFragment(dialog);

			return;
		}

		
		if (buttonId == R.id.screen_status_quests_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.QUESTS);
		}
		else if (buttonId == R.id.screen_status_cargo_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.CARGO);
		}
		else
		{
			mGameManager.setCurrentScreenType(ScreenType.SHIP);
		}
	}
	
	// New function to handle cheat form display (originally in CommanderStatusFormHandleEvent())
	public void showCheatDialog() {
		BaseDialog dialog = mGameManager.findDialogByClass(CheatDialog.class);
		
		dialog.setViewTextById(R.id.dialog_cheat_credits, R.string.format_number, credits);
		
		dialog.setViewTextById(R.id.dialog_cheat_debt, R.string.format_number, debt);
		
		dialog.setViewTextById(R.id.dialog_cheat_reputation, R.string.format_number, reputationScore);
		
		dialog.setViewTextById(R.id.dialog_cheat_record, R.string.format_number, abs(policeRecordScore));

		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_negative)).setChecked(policeRecordScore < 0);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_moon)).setChecked(moonBought);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_lightning)).setChecked(ship.hasShield(Shield.LIGHTNING));
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_fuelcompactor)).setChecked(ship.hasGadget(Gadget.FUELCOMPACTOR));
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_morgan)).setChecked(ship.hasWeapon(Weapon.MORGAN, true));
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_singularity)).setChecked(canSuperWarp);
		
		// NB this is a new addition
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_hull)).setChecked(scarabStatus == 3);
	}
	
	// New function to handle cheat form dismissal (originally in CommanderStatusFormHandleEvent())
	public void cheatDialogDismiss() {
		BaseDialog dialog = mGameManager.findDialogByClass(CheatDialog.class);
		
		try {
			credits = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_cheat_credits)).getText().toString());
		} 
		catch (NumberFormatException e) {}

		try {
			debt = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_cheat_debt)).getText().toString());
		} 
		catch (NumberFormatException e) {}

		try {
			reputationScore = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_cheat_reputation)).getText().toString());
		} 
		catch (NumberFormatException e) {}
		
		try {
			policeRecordScore = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_cheat_record)).getText().toString());
		} 
		catch (NumberFormatException e) {}

		if ( ((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_negative)).isChecked() )
			policeRecordScore = -policeRecordScore;

		moonBought = ((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_moon)).isChecked();

		if (((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_lightning)).isChecked()
				&& !ship.hasShield(Shield.LIGHTNING) && ship.type.shieldSlots > 0)
			ship.shield[0] = Shield.LIGHTNING;
		else if (!((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_lightning)).isChecked()
				&& ship.hasShield(Shield.LIGHTNING))
		{
			for (int i=0; i<ship.shield.length; ++i)
				if (ship.shield[i] == Shield.LIGHTNING)
					ship.shield[i] = Shield.values()[1];	// NB This is really bizarre behavior but that's what's in the original code.
		}

		if (((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_fuelcompactor)).isChecked()
				&& !ship.hasGadget(Gadget.FUELCOMPACTOR) && ship.type.gadgetSlots > 0)
			ship.gadget[0] = Gadget.FUELCOMPACTOR;
		else if (!((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_fuelcompactor)).isChecked()
				&& ship.hasGadget(Gadget.FUELCOMPACTOR))
		{
			for (int i=0; i<ship.gadget.length; ++i)
				if (ship.gadget[i] == Gadget.FUELCOMPACTOR)
					ship.gadget[i] = Gadget.values()[1];	// NB This is really bizarre behavior but that's what's in the original code.
		}
		if (((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_morgan)).isChecked()
				&& !ship.hasWeapon(Weapon.MORGAN, true) && ship.type.weaponSlots > 0)
			ship.weapon[0] = Weapon.MORGAN;
		else if (!((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_morgan)).isChecked()
				&& ship.hasWeapon(Weapon.MORGAN, true))
		{
			for (int i=0; i<ship.weapon.length; ++i)
				if (ship.weapon[i] == Weapon.MORGAN)
					ship.weapon[i] = Weapon.values()[1];	// NB This is really bizarre behavior but that's what's in the original code.
		}

		canSuperWarp = ((CheckBox)dialog.getDialog().findViewById(R.id.dialog_cheat_singularity)).isChecked();
		
		// NB this is a new addition so that the scarab strengthened hull can be activated from the cheat menu.
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_cheat_hull)).isChecked())
			scarabStatus = 3;

		showCommanderStatus();
	}

	
	/*
	 * Encounter.c
	 */
	// *************************************************************************
	// Buttons on the encounter screen
	// *************************************************************************
	public void encounterButtons(  )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;

		screen.setViewVisibilityById(R.id.screen_encounter_continuous_interrupt, autoAttack || autoFlee);
		screen.setViewVisibilityById(R.id.screen_encounter_continuous_ticker, autoAttack || autoFlee);
		if (autoAttack || autoFlee)
		{
			boolean l = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
			attackIconStatus = !attackIconStatus;
			if (attackIconStatus)
				((ImageView) screen.getView().findViewById(R.id.screen_encounter_continuous_ticker)).setImageResource(l? R.drawable.continuous0l : R.drawable.continuous0);
			else
				((ImageView) screen.getView().findViewById(R.id.screen_encounter_continuous_ticker)).setImageResource(l? R.drawable.continuous1l : R.drawable.continuous1);
		}
		
		// NB We treat surrender button differently from others because it's wider, so it gets a special layout.
		screen.setViewVisibilityById(R.id.screen_encounter_surrender, false);
		for (int i = 0; i < EncounterScreen.BUTTONS.size(); i++) {
			EncounterButton button = encounterType.button(i+1);
			if (button == EncounterButton.SURRENDER) {
				screen.setViewVisibilityById(R.id.screen_encounter_surrender, true);
			}
			else if (button != null) {
				screen.setViewTextById(EncounterScreen.BUTTONS.get(i), button);
			}
			screen.setViewVisibilityById(EncounterScreen.BUTTONS.get(i), button != null && button != EncounterButton.SURRENDER);
		}

		screen.setViewVisibilityById(R.id.screen_encounter_playership_header, textualEncounters && !graphicalEncounters, false);
		screen.setViewVisibilityById(R.id.screen_encounter_enemyship_header, textualEncounters && !graphicalEncounters, false);
	}

	
	// *************************************************************************
	// Display on the encounter screen what the next action will be
	// *************************************************************************
	private void encounterDisplayNextAction( boolean firstDisplay )
	{
		// Most of the original logic in this method has been offloaded into string resources through the Encounter enum.

		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;
		
		if (firstDisplay && encounterType == Encounter.Police.ATTACK && 
				policeRecordScore > PoliceRecord.CRIMINAL.score) {
			screen.setViewTextById(R.id.screen_encounter_enemyaction, R.string.opponentaction_hailsurrender);
		} else if (encounterType.action() == OpponentAction.IGNORE && ship.cloaked(opponent)) {
			screen.setViewTextById(R.id.screen_encounter_enemyaction, R.string.opponentaction_cloaked);
		} else {
			screen.setViewTextById(R.id.screen_encounter_enemyaction, encounterType.action());
		}
	}

		
	// *************************************************************************
	// Show the ship stats on the encounter screen
	// *************************************************************************
	private void showShip( Ship sh, boolean commandersShip )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;

		int hullPc = sh.hull * 100 / sh.getHullStrength();
		int shieldPc = 0;
		if (sh.totalShields() > 0)
			shieldPc = sh.totalShieldStrength() * 100 / sh.totalShields();
		
		int shipViewId;
		int shipTypeViewId;
		int hullViewId;
		int shieldViewId;
		if (commandersShip) {
			shipViewId = R.id.screen_encounter_playership_image;
			shipTypeViewId = R.id.screen_encounter_playership_type;
			hullViewId = R.id.screen_encounter_playership_hull;
			shieldViewId = R.id.screen_encounter_playership_shields;
		} else {
			shipViewId = R.id.screen_encounter_enemyship_image;
			shipTypeViewId = R.id.screen_encounter_enemyship_type;
			hullViewId = R.id.screen_encounter_enemyship_hull;
			shieldViewId = R.id.screen_encounter_enemyship_shields;
		}
		if ((commandersShip && commanderOffScreen) || (!commandersShip && opponentOffScreen)) {
			((ImageView)screen.getView().findViewById(shipViewId)).setAlpha(0);
		}
		screen.setViewVisibilityById(shipViewId, graphicalEncounters, false);
		screen.setViewVisibilityById(shipTypeViewId, textualEncounters && !graphicalEncounters, false);
		screen.setViewVisibilityById(hullViewId, textualEncounters, false);
		screen.setViewVisibilityById(shieldViewId, textualEncounters, false);


		if (textualEncounters)
		{

			screen.setViewTextById(shipTypeViewId, sh.type);

			int hullTextId;
			if (encounterType.opponentType() == Opponent.MONSTER)
				hullTextId = R.string.screen_encounter_hide;
			else
				hullTextId = R.string.screen_encounter_hull;

			screen.setViewTextById(hullViewId, hullTextId, hullPc);

			int shieldTextId;
			if (sh.shield[0] == null)
				shieldTextId = R.string.screen_encounter_noshields;
			else
				shieldTextId = R.string.screen_encounter_shields;
			screen.setViewTextById(shieldViewId, shieldTextId, shieldPc);
		}
		if (graphicalEncounters)
		{
			ImageView shipView = (ImageView) screen.getView().findViewById(shipViewId);

			// Rotation of the enemy ship (NB not in original)
			shipRotation(shipView, sh, commandersShip);

			shipView.setImageResource(sh.type.drawableId);

			int damageLevel = hullPc <= 0? 10000 : hullPc >= 100? 0 :
				sh.type.damageMin + (100 - hullPc) * (sh.type.damageMax - sh.type.damageMin) / 100;
			int shieldLevel = shieldPc >= 100? 10000 : shieldPc <= 0? 0 :
				sh.type.shieldMin + shieldPc * (sh.type.shieldMax - sh.type.shieldMin) / 100;

			Log.d("showShip()", "Showing "+(commandersShip? "player" : "opponent")+" with damageLevel="+damageLevel+" and shieldLevel="+shieldLevel);

			LayerDrawable shipLayers = (LayerDrawable) shipView.getDrawable();
			Drawable shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
			Drawable damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
			damage.setLevel(damageLevel);
			shield.setLevel(shieldLevel);

			if (!commandersShip)
			{
				int iconId = encounterType.opponentType().iconId;
				if (sh.type == ShipType.MANTIS)
						iconId = Opponent.MANTIS.iconId;
				((ImageView) screen.getView().findViewById(R.id.screen_encounter_icon)).setImageResource(iconId);

			}
		}
	}

	private void shipRotation(ImageView shipView, Ship sh, boolean commandersShip) {
//		boolean fleeing = commandersShip? prevEncounterAction == EncounterButton.FLEE : encounterType.action() == OpponentAction.FLEE;
//		if (!commandersShip ^ isRtl() ^ fleeing) {
		if (!commandersShip ^ isRtl()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				shipRotationHoneycomb(shipView);
			} else {
				shipRotationBase(shipView, sh, 100);
			}
		}
	}

	private void shipRotationBase(final ImageView shipView, final Ship sh, final int delay) {

		// Attempt to calculate how much the image is already scaled by the xml.
		float l = shipView.getLeft();
		float r = shipView.getRight();
		float t = shipView.getTop();
		float b = shipView.getBottom();

		Drawable shipDrawable = ResourcesCompat.getDrawable(getResources(), sh.type.drawableId, shipView.getContext().getTheme());
		float iw = shipDrawable.getIntrinsicWidth();
		float ih = shipDrawable.getIntrinsicHeight();

		float sx = (r - l) / iw;
		float sy = (b - t) / ih;


		if (sx == 0 || sy == 0) {
			Log.w(GameState.LOG_TAG, "Compatibility ship rotation failure. Will re-try in "+delay+"ms");
			shipView.postDelayed(new Runnable() {

				@Override
				public void run() {
					shipRotationBase(shipView, sh, 2*delay);
				}
			}, delay);
		}

		// Construct a new transformation matrix for the imageview, which scales as the xml and also rotates 180 deg.
		Matrix matrix = new Matrix();
		matrix.preScale(sx, sy);
		matrix.preRotate(180f, iw / 2, ih / 2);

		shipView.setScaleType(ImageView.ScaleType.MATRIX);
		shipView.setImageMatrix(matrix);

		// Also reverse horizontal margin/padding
		shipView.setPadding(shipView.getPaddingRight(), shipView.getPaddingTop(), shipView.getPaddingLeft(), shipView.getPaddingBottom());
		ViewGroup.LayoutParams params = shipView.getLayoutParams();
		if (params instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
			marginParams.setMargins(marginParams.rightMargin, marginParams.topMargin, marginParams.leftMargin, marginParams.bottomMargin);
			shipView.setLayoutParams(marginParams);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void shipRotationHoneycomb(ImageView shipView) {
		shipView.setRotation(180);
	}

	private void animateAttack(final boolean commanderUnderAttack, final boolean hit, final boolean destroyed) {
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;

		final ImageView shipView = (ImageView) screen.getView().findViewById(commanderUnderAttack? R.id.screen_encounter_playership_image : R.id.screen_encounter_enemyship_image);

		// We're not in the main thread so we must post instead of running this code here.
		shipView.post(new Runnable() {

			@Override
			public void run() {
				shipView.clearAnimation();
				int animId =
						destroyed?	commanderUnderAttack? R.anim.destroyed_player : R.anim.destroyed_opponent  :
						hit? 		commanderUnderAttack? R.anim.hit_player : R.anim.hit_opponent  :
									commanderUnderAttack? R.anim.miss_player : R.anim.miss_opponent;
				// Basic shake or dodge animation
				Animation anim = AnimationUtils.loadAnimation(mGameManager, animId);
				shipView.startAnimation(anim);

				// Flash red
				if (hit) {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// TODO do this in a way that requires less initialization
						ValueAnimator valueAnimator = commanderUnderAttack? playerFlashAnimator : opponentFlashAnimator;
						valueAnimator.setCurrentPlayTime(0);
						ShipFlashUpdateListener shipFlashUpdateListener = commanderUnderAttack? playerFlashUpdateListener : opponentFlashUpdateListener;
						shipFlashUpdateListener.shipView = shipView;
						valueAnimator.start();

						anim.setAnimationListener(new Animation.AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								shipView.getDrawable().setColorFilter(0x00000000, PorterDuff.Mode.SRC_ATOP);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}
						});
					} else {
						// Old android versions cannot use ValueAnimator so we simply flash on/off instead
						shipView.postDelayed(new Runnable() {
							@Override
							public void run() {
								shipView.getDrawable().setColorFilter(0xcccc0000, PorterDuff.Mode.SRC_ATOP);
							}
						}, 60);

						shipView.postDelayed(new Runnable() {
							@Override
							public void run() {
								shipView.getDrawable().setColorFilter(0x00000000, PorterDuff.Mode.SRC_ATOP);
							}
						}, 140);
					}
				}

				// Hide destroyed ship after fade-out
				if (destroyed) {
					anim.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							shipView.setAlpha(0);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}
					});
				}
			}
		});


	}


	private void animateEnterExit(final boolean commandersShip, final boolean enter) {
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;

		final ImageView shipView = (ImageView) screen.getView().findViewById(commandersShip? R.id.screen_encounter_playership_image : R.id.screen_encounter_enemyship_image);
		final boolean left = commandersShip ^ isRtl();

		final int animId = enter? left? R.anim.enter_left : R.anim.enter_right : left? R.anim.exit_left : R.anim.exit_right;
		final Animation anim = AnimationUtils.loadAnimation(mGameManager, animId);
		anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				shipView.setAlpha(255);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (commandersShip) {
					commanderOffScreen = !enter;
					playerShipNeedsUpdate = true;
				} else {
					opponentOffScreen = !enter;
					opponentShipNeedsUpdate = true;
				}
				encounterDisplayShips();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		shipView.post(new Runnable() {
			@Override
			public void run() {
				shipView.clearAnimation();
				shipView.startAnimation(anim);
			}
		});
	}
	
	// *************************************************************************
	// Display on the encounter screen the ships (and also wipe it)
	// *************************************************************************
	private void encounterDisplayShips(  )
	{
	    if (opponentShipNeedsUpdate)
	    {
	    	opponentShipNeedsUpdate = false;
			showShip( opponent, false );
	    }
	    if (playerShipNeedsUpdate)
	    {
	    	playerShipNeedsUpdate = false;
			showShip( ship, true );
	    }
	}
	
	
	// *************************************************************************
	// Your escape pod ejects you
	// *************************************************************************
	private void escapeWithPod(  )
	{
		new EscapePodTask().execute();
	}
	
	private class EscapePodTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			CountDownLatch latch;
			
			latch = newLatch();
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_encounter_escapepodactivate_title,
					R.string.screen_encounter_escapepodactivate_message,
					R.string.help_escapepodactivated,
					newUnlocker(latch)));
			lock(latch);
			
			if (scarabStatus == 3)
				scarabStatus = 0;

			arrival();

			if (reactorStatus > 0 && reactorStatus < 21)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_reactordestroyed_title,
						R.string.screen_encounter_reactordestroyed_message,
						R.string.help_reactorselfdestruct,
						newUnlocker(latch)));
				lock(latch);
				reactorStatus = 0;
			}

			if (japoriDiseaseStatus == 1)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_antidotedestroyed_title,
						R.string.screen_encounter_antidotedestroyed_message,
						R.string.help_antidotedestroyed,
						newUnlocker(latch),
						solarSystem[japori].name
						));
				lock(latch);
				japoriDiseaseStatus = 0;
			}

			if (artifactOnBoard)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_artifactnotsaved_title,
						R.string.screen_encounter_artifactnotsaved_message,
						R.string.help_artifactnotsaved,
						newUnlocker(latch)));
				lock(latch);
				artifactOnBoard = false;
			}

			if (jarekStatus == 1)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_jarektakenhome_title,
						R.string.screen_encounter_jarektakenhome_message,
						R.string.help_jarektakenhome,
						newUnlocker(latch),
						solarSystem[devidia]));
				lock(latch);
				jarekStatus = 0;
			}

			if (wildStatus == 1)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_wildarrested_title,
						R.string.screen_encounter_wildarrested_message,
						R.string.help_wildarrested,
						newUnlocker(latch)));
				lock(latch);
				policeRecordScore += PoliceRecord.CAUGHTWITHWILDSCORE;
				addNewsEvent(NewsEvent.WILDARRESTED);
				wildStatus = 0;
			}

			if (ship.tribbles > 0)
			{
				// NB No idea why this called TribbleSurvivedAlert in original
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_tribblessurvived_title,
						R.string.screen_encounter_tribblessurvived_message,
						R.string.help_tribblesurvived,
						newUnlocker(latch)));
				lock(latch);
				ship.tribbles = 0;
			}
			
			// NB This is a new message if Marie narcotics are lost (Customs will no longer approach you)
			if (justLootedMarie)
			{
//				latch = newLatch();
//				mGameManager.showDialogFragment(SimpleDialog.creator(
//						R.string.screen_encounter_mariegoodslost_title,
//						R.string.screen_encounter_mariegoodslost_message,
//						newUnlocker(latch)));
//				lock(latch);
				justLootedMarie = false;
			}
			

			if (insurance)
			{
				credits += ship.currentPriceWithoutCargo( true );
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_insurancepays_title,
						R.string.screen_encounter_insurancepays_message,
						R.string.help_insurancepays,
						newUnlocker(latch)));
				lock(latch);
			}

			if (credits > 500)
				credits -= 500;
			else
			{
				debt += (500 - credits);
				credits = 0;
			}

			incDays( 3 );	

			createFlea();
			latch = newLatch();
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_encounter_fleabuilt_title,
					R.string.screen_encounter_fleabuilt_message,
					R.string.help_fleabuilt,
					newUnlocker(latch)));
			lock(latch);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mGameManager.setCurrentScreenType(ScreenType.INFO);
			mGameManager.clearBackStack();
			
			// NB Now autosaving on arrival:
			mGameManager.autosave();
		}
		
	}
	
	// *************************************************************************
	// You get arrested
	// *************************************************************************
	private void arrested(  )
	{
		new ArrestedTask().execute();
	}
	
	private class ArrestedTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			CountDownLatch latch;
			
			int fine = ((1 + (((currentWorth() * min( 80, -policeRecordScore )) / 100) / 500)) * 500);
			if (wildStatus == 1)
			{
				fine *= 1.05;
			}
			int imprisonment = max( 30, -policeRecordScore );

			latch = newLatch();
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_encounter_arrested_title, 
					R.string.screen_encounter_arrested_message,
					R.string.help_arrested,
					newUnlocker(latch)));
			lock(latch);

			latch = newLatch();
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_encounter_convicted_title, 
					R.string.screen_encounter_convicted_message,
					R.string.help_conviction,
					newUnlocker(latch),
					imprisonment,
					fine));
			lock(latch);

			if (ship.getCargo(TradeItem.NARCOTICS) > 0 || ship.getCargo(TradeItem.FIREARMS) > 0)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_impounded_title, 
						R.string.screen_encounter_impounded_message,
						R.string.help_impound,
						newUnlocker(latch)));
				lock(latch);
				ship.clearCargo(TradeItem.NARCOTICS);
				ship.clearCargo(TradeItem.FIREARMS);
			}

			if (insurance)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_stopinsurance_title, 
						R.string.screen_encounter_stopinsurance_message,
						R.string.help_insurancelost,
						newUnlocker(latch)));
				lock(latch);
				insurance = false;
				noClaim = 0;
			}

			if (ship.crew[1] != null)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_mercenariesleave_title, 
						R.string.screen_encounter_mercenariesleave_message,
						R.string.help_mercenariesleave,
						newUnlocker(latch)));
				lock(latch);
				for (int i=1; i<ship.crew.length; ++i)
					ship.crew[i] = null;
			}

			if (japoriDiseaseStatus == 1)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_antidoteremoved_title, 
						R.string.screen_encounter_antidoteremoved_message,
						R.string.help_antidoteremoved,
						newUnlocker(latch),
						solarSystem[japori].name));
				lock(latch);
				japoriDiseaseStatus = 2;
			}

			if (jarekStatus == 1)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_jarektakenhome_title,
						R.string.screen_encounter_jarektakenhome_message,
						R.string.help_jarektakenhome,
						newUnlocker(latch)));
				lock(latch);
				jarekStatus = 0;
			}

			if (wildStatus == 1)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_wildarrested_title,
						R.string.screen_encounter_wildarrested_message,
						R.string.help_wildarrested,
						newUnlocker(latch)));
				lock(latch);
				addNewsEvent(NewsEvent.WILDARRESTED);
				wildStatus = 0;
			}

			if (reactorStatus > 0 && reactorStatus < 21)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_policeconfiscatereactor_title,
						R.string.screen_encounter_policeconfiscatereactor_message,
						R.string.help_reactortaken,
						newUnlocker(latch)));
				lock(latch);
				reactorStatus = 0; 
			}
			
			// NB This is a new check if Marie narcotics are impounded (Customs will no longer approach you)
			if (justLootedMarie)
			{
//				latch = newLatch();
//				mGameManager.showDialogFragment(SimpleDialog.creator(
//						R.string.screen_encounter_mariegoodsimpounded_title,
//						R.string.screen_encounter_mariegoodsimpounded_message,
//						newUnlocker(latch)));
//				lock(latch);
				justLootedMarie = false;
			}
			
			arrival();

			incDays( imprisonment );

			if (credits >= fine)
				credits -= fine;
			else
			{
				credits += ship.currentPrice(true);

				if (credits >= fine)
					credits -= fine;
				else
					credits = 0;

				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_shipsold_title,
						R.string.screen_encounter_shipsold_message,
						R.string.help_shipsold,
						newUnlocker(latch)));
				lock(latch);

				if (ship.tribbles > 0)
				{
					latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_tribblessold_title,
							R.string.screen_encounter_tribblessold_message,
							R.string.help_tribblessold,
							newUnlocker(latch)));
					lock(latch);
					ship.tribbles = 0;
				}

				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_fleareceived_title,
						R.string.screen_encounter_fleareceived_message,
						R.string.help_fleareceived,
						newUnlocker(latch)));
				lock(latch);

				createFlea();
			}
			
			policeRecordScore = PoliceRecord.DUBIOUS.score;

			if (debt > 0)
			{
				if (credits >= debt)
				{
					credits -= debt;
					debt = 0;
				}
				else
				{
					debt -= credits;
					credits = 0;
				}
			}
			
			for (int i=0; i<imprisonment; ++i)
				payInterest();
			
			return null;

		}
		
		@Override
		protected void onPostExecute(Void result) {
			mGameManager.setCurrentScreenType(ScreenType.INFO);
			mGameManager.clearBackStack();
			
			// NB Now autosaving on arrival:
			mGameManager.autosave();
		}
		
	}

	private boolean commanderOffScreen;
	private boolean opponentOffScreen;

	public void drawEncounterForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;

		prevEncounterAction = null;

		encounterButtons();
		
		playerShipNeedsUpdate=true;
		opponentShipNeedsUpdate=true;

		if (encounterAnim) {
			((ImageView) screen.getView().findViewById(R.id.screen_encounter_enemyship_image)).setAlpha(0);
			animateEnterExit(false, true);
			if (commanderOffScreen) {
				((ImageView) screen.getView().findViewById(R.id.screen_encounter_playership_image)).setAlpha(0);
				animateEnterExit(true, true);
			} else {
				// NB This avoids an odd bug where hull/shield levels don't display correctly when opponent and player are the same ship type.
				final ImageView shipView = (ImageView) screen.getView().findViewById(R.id.screen_encounter_playership_image);
				shipView.setAlpha(0);
				shipView.post(new Runnable() {
					@Override
					public void run() {
						shipView.setAlpha(255);
					}
				});
			}
		}

		encounterDisplayShips();
		encounterDisplayNextAction( true );

		if (encounterType == Encounter.VeryRare.POSTMARIEPOLICE)
		{
			screen.setViewTextById(R.id.screen_encounter_description, R.string.screen_encounter_description_customs);
			((TextView) screen.getView().findViewById(R.id.screen_encounter_description)).setMinLines(1);
			((TextView) screen.getView().findViewById(R.id.screen_encounter_enemyaction)).setMinLines(3);
		}
		else
		{
			((TextView) screen.getView().findViewById(R.id.screen_encounter_description)).setMinLines(2);
			((TextView) screen.getView().findViewById(R.id.screen_encounter_enemyaction)).setMinLines(2);
			String opponentType = encounterType.opponentType().toXmlStringInit(getResources());
			if (opponent.type == ShipType.MANTIS) {
				opponentType = Opponent.MANTIS.toXmlStringInit(getResources());
			}
			String opponentShip = opponent.type.toXmlString(getResources()).toLowerCase(Locale.getDefault());
			if (encounterType == Encounter.VeryRare.MARIECELESTE) {
				opponentShip = getResources().getString(R.string.opponent_ship);
			} else if (encounterType.opponentType() == Opponent.FAMOUSCAPTAIN) {
				if (encounterType == Encounter.VeryRare.CAPTAINAHAB) {
					opponentType = getResources().getString(R.string.opponent_initial_famouscaptain);	// For some reason, the original code calls Ahab "the famous Captain Ahab" while the others just go by "Captain"
					opponentShip = getResources().getString(R.string.opponent_ahab);
				}
				if (encounterType == Encounter.VeryRare.CAPTAINCONRAD) {
					opponentShip = getResources().getString(R.string.opponent_conrad);
				}
				if (encounterType == Encounter.VeryRare.CAPTAINHUIE) {
					opponentShip = getResources().getString(R.string.opponent_huie);
				}
			}
			String description = getResources().getQuantityString(R.plurals.screen_encounter_description_initial, clicks, clicks, warpSystem, opponentType, opponentShip);
			screen.setViewTextById(R.id.screen_encounter_description, description);
		}		
		
		int d = sqrt( ship.tribbles/250 );
		d = min(d, EncounterScreen.TRIBBLES.size());
		for (int id : EncounterScreen.TRIBBLES)
		{
			screen.setViewVisibilityById(id, false);
		}
		for (int i=0; i<d; ++i)
		{
			final int id = EncounterScreen.TRIBBLES.get(getRandom(EncounterScreen.TRIBBLES.size()));
			randomizeTribblePosition(id, 0);
		}
		
//		// Screenshot override
//		screen.setViewVisibilityById(R.id.screen_encounter_playership_header, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_playership_type, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_playership_image, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_playership_shields, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_playership_hull, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_enemyship_header, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_enemyship_type, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_enemyship_image, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_enemyship_shields, false, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_enemyship_hull, false, false);
//		((TextView) screen.getView().findViewById(R.id.screen_encounter_description)).setMinLines(2);
//		((TextView) screen.getView().findViewById(R.id.screen_encounter_enemyaction)).setMinLines(2);
//
//		ImageView shipView = (ImageView)screen.getView().findViewById(R.id.screen_encounter_playership_image);
//		shipView.setImageResource(ShipType.GNAT.drawableId);
//		LayerDrawable shipLayers = (LayerDrawable) shipView.getDrawable();
//		Drawable shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
//		Drawable damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
//		damage.setLevel(4000);
//		shield.setLevel(0);
//		shipView = (ImageView)screen.getView().findViewById(R.id.screen_encounter_enemyship_image);
//		shipView.setImageResource(ShipType.HORNET.drawableId);
//		shipLayers = (LayerDrawable) shipView.getDrawable();
//		shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
//		damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
//		damage.setLevel(0);
//		shield.setLevel(7500);
//		((ImageView) screen.getView().findViewById(R.id.screen_encounter_icon)).setImageResource(Opponent.PIRATE.iconId);
//		screen.setViewVisibilityById(R.id.screen_encounter_button1, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button2, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button3, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_button4, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_surrender, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_continuous_interrupt, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_continuous_ticker, false);
//		screen.setViewTextById(R.id.screen_encounter_button1, EncounterButton.ATTACK.toXmlString(getResources()));
//		screen.setViewTextById(R.id.screen_encounter_button2, EncounterButton.FLEE.toXmlString(getResources()));
//		String description = getResources().getQuantityString(R.plurals.screen_encounter_description_initial, 15, 15, getResources().getString(R.string.solarsystem_montor), Opponent.PIRATE.toXmlStringInit(getResources()), ShipType.HORNET.toXmlString(getResources()).toLowerCase(Locale.getDefault()));
//		screen.setViewTextById(R.id.screen_encounter_description, description);
//		screen.setViewTextById(R.id.screen_encounter_enemyaction, OpponentAction.ATTACK);
//
//		ImageView shipView = (ImageView)screen.getView().findViewById(R.id.screen_encounter_playership_image);
//		shipView.setImageResource(ShipType.GRASSHOPPER.drawableId);
//		LayerDrawable shipLayers = (LayerDrawable) shipView.getDrawable();
//		Drawable shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
//		Drawable damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
//		damage.setLevel(0);
//		shield.setLevel(10000);
//		shipView = (ImageView)screen.getView().findViewById(R.id.screen_encounter_enemyship_image);
//		shipView.setImageResource(ShipType.WASP.drawableId);
//		shipLayers = (LayerDrawable) shipView.getDrawable();
//		shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
//		damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
//		damage.setLevel(0);
//		shield.setLevel(8750);
//		((ImageView) screen.getView().findViewById(R.id.screen_encounter_icon)).setImageResource(Opponent.POLICE.iconId);
//		screen.setViewVisibilityById(R.id.screen_encounter_button1, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button2, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button3, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button4, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_surrender, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_continuous_interrupt, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_continuous_ticker, false);
//		screen.setViewTextById(R.id.screen_encounter_button1, EncounterButton.ATTACK.toXmlString(getResources()));
//		screen.setViewTextById(R.id.screen_encounter_button2, EncounterButton.FLEE.toXmlString(getResources()));
//		screen.setViewTextById(R.id.screen_encounter_button3, EncounterButton.SUBMIT.toXmlString(getResources()));
//		screen.setViewTextById(R.id.screen_encounter_button4, EncounterButton.BRIBE.toXmlString(getResources()));
//		String description = getResources().getQuantityString(R.plurals.screen_encounter_description_initial, 7, 7, getResources().getString(R.string.solarsystem_jarada), Opponent.POLICE.toXmlStringInit(getResources()), ShipType.WASP.toXmlString(getResources()).toLowerCase(Locale.getDefault()));
//		screen.setViewTextById(R.id.screen_encounter_description, description);
//		screen.setViewTextById(R.id.screen_encounter_enemyaction, OpponentAction.INSPECTION);
//
//		ImageView shipView = (ImageView)screen.getView().findViewById(R.id.screen_encounter_playership_image);
//		shipView.setImageResource(ShipType.BUMBLEBEE.drawableId);
//		LayerDrawable shipLayers = (LayerDrawable) shipView.getDrawable();
//		Drawable shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
//		Drawable damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
//		damage.setLevel(0);
//		shield.setLevel(0);
//		shipView = (ImageView)screen.getView().findViewById(R.id.screen_encounter_enemyship_image);
//		shipView.setImageResource(ShipType.FLEA.drawableId);
//		shipLayers = (LayerDrawable) shipView.getDrawable();
//		shield = shipLayers.findDrawableByLayerId(R.id.drawable_shield);
//		damage = shipLayers.findDrawableByLayerId(R.id.drawable_damage);
//		damage.setLevel(0);
//		shield.setLevel(0);
//		((ImageView) screen.getView().findViewById(R.id.screen_encounter_icon)).setImageResource(Opponent.TRADER.iconId);
//		screen.setViewVisibilityById(R.id.screen_encounter_button1, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button2, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button3, true);
//		screen.setViewVisibilityById(R.id.screen_encounter_button4, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_surrender, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_continuous_interrupt, false);
//		screen.setViewVisibilityById(R.id.screen_encounter_continuous_ticker, false);
//		screen.setViewTextById(R.id.screen_encounter_button1, EncounterButton.ATTACK.toXmlString(getResources()));
//		screen.setViewTextById(R.id.screen_encounter_button2, EncounterButton.IGNORE.toXmlString(getResources()));
//		screen.setViewTextById(R.id.screen_encounter_button3, EncounterButton.TRADE.toXmlString(getResources()));
//		String description = getResources().getQuantityString(R.plurals.screen_encounter_description_initial, 20, 20, getResources().getString(R.string.solarsystem_exo), Opponent.TRADER.toXmlStringInit(getResources()), ShipType.FLEA.toXmlString(getResources()).toLowerCase(Locale.getDefault()));
//		screen.setViewTextById(R.id.screen_encounter_description, description);
//		screen.setViewTextById(R.id.screen_encounter_enemyaction, OpponentAction.TRADE);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void randomizeTribblePosition(final int tribbleId, final int tries) {
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;
		final View tribble = screen.getView().findViewById(tribbleId);

		final int sw = screen.getView().getWidth();
		final int sh = screen.getView().getHeight();
		final int tw = tribble.getWidth();
		final int th = tribble.getHeight();
		
		// If views don't have dimensions yet, wait a bit and try again. TODO I'd love to find a slightly cleaner way to do this...
		if (tries < 10 && (sw == 0 || sh == 0 || tw == 0 || th == 0)) {
			tribble.postDelayed(new Runnable() {
				@Override
				public void run() {
					Log.w(GameState.LOG_TAG, "Compatibility tribble position failure. Will re-try in 100ms");
					randomizeTribblePosition(tribbleId, tries+1);
				}
			}, 100);
			return;
		}
		
		if (VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
		{
			tribble.setX(rng.nextFloat() * (sw - tw));
			tribble.setY(rng.nextFloat() * (sh - th));
			tribble.setVisibility(View.VISIBLE);
		}
		else
		{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tribble.getLayoutParams();
			params.leftMargin = getRandom(sw - tw);
			params.topMargin = getRandom(sh - th);
			params.rightMargin = 0;
			params.bottomMargin = 0;
			
			tribble.setLayoutParams(params);
			tribble.setVisibility(View.VISIBLE);
		}
	}
	
	// *************************************************************************
	// Encounter screen Event Handler
	// *************************************************************************
	public void encounterFormHandleEvent ( final int buttonId )
	{
		if (encounterButtonRunning) return;
		encounterButtonRunning = true;
		
		EncounterButton button;
		switch (buttonId) {
		case R.id.screen_encounter_button1:
			button = encounterType.button(1);
			break;
		case R.id.screen_encounter_button2:
			button = encounterType.button(2);
			break;
		case R.id.screen_encounter_button3:
			button = encounterType.button(3);
			break;
		case R.id.screen_encounter_button4:
			button = encounterType.button(4);
			break;
		case R.id.screen_encounter_surrender:
			button = EncounterButton.SURRENDER;
			break;
		case R.id.screen_encounter_continuous_interrupt:
			button = EncounterButton.INTERRUPT;
			break;
		default:
			if (EncounterScreen.TRIBBLES.contains(buttonId)) {
				button = EncounterButton.TRIBBLE;
			}
			else throw new IllegalArgumentException();
			break;
		}

		autoHandler.removeCallbacksAndMessages(null);
		runningTask = new EncounterButtonTask();
		runningTask.execute(button);
		
		if (EncounterScreen.TRIBBLES.contains(buttonId)) {
			Log.d("tribble", "click");
    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
    				R.string.screen_encounter_squeek_title, 
    				R.string.screen_encounter_squeek_message,
    				R.string.help_squeek,
					new OnConfirmListener() {
						
						@Override
						public void onConfirm() {
							Log.d("tribble", "dismiss");
							randomizeTribblePosition(buttonId, 0);
						}
					}));
		}
	}
	
	
	
	// Some helpers for the EncounterButtonTask
	private enum Result {
		TRAVEL,
		REFRESH,
		NOTHING,
		DEAD,
	}
	
	public void clearButtonAction() {
    	autoHandler.removeCallbacksAndMessages(null);
		runningTask = null;
    	autoTask = null;
		autoAttack = false;
		autoFlee = false;

		if (mGameManager.getCurrentScreenType() == ScreenType.ENCOUNTER) {
			autoHandler.post(autoClear);
		}

	}
	
	private boolean encounterButtonRunning;
	private EncounterButtonTask runningTask;
	private EncounterButtonTask autoTask;
	private final Handler autoHandler = new Handler();
	private final Runnable autoRun = new Runnable() {
		@Override
		public void run() {
			autoTask = new EncounterButtonTask();
			if (autoAttack) {
				autoTask.execute(EncounterButton.ATTACK);
			}
			if (autoFlee) {
				autoTask.execute(EncounterButton.FLEE);
			}
		}
	};
	private final Runnable autoClear = new Runnable() {
		@Override
		public void run() {
			BaseScreen screen = mGameManager.getCurrentScreen();
			if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;
			screen.setViewVisibilityById(R.id.screen_encounter_continuous_interrupt, false);
			screen.setViewVisibilityById(R.id.screen_encounter_continuous_ticker, false);
		}
	};
	
	private class EncounterButtonTask extends AsyncTask<EncounterButton, Void, Result> {
//		private boolean autoAttack;
//		private boolean autoFlee;

//		private boolean playerShipNeedsUpdate = false;
//		private boolean opponentShipNeedsUpdate = false;
		
		private boolean commanderFlees;
		
//		private volatile boolean redrawButtons;
		
		private Encounter prevEncounterType;

		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected Result doInBackground(EncounterButton... params) {
			EncounterButton action = params[0];
			if (action == null) {
				throw new IllegalArgumentException();
			}

			GameState.this.prevEncounterAction = action;
			return doEncounterButton(action);
		}
		
		@Override
		protected void onProgressUpdate(Void... params) {
			encounterDisplayShips();
		}
				
		@Override
		protected void onPostExecute(final Result result) {

			encounterButtons();
			encounterDisplayShips();
			
			switch (result) {
			case TRAVEL:
				travel();
				break;
			case DEAD:
				showEndGameScreen(EndStatus.KILLED);
				break;
			case REFRESH:				
				// TODO stick this in a separate method somewhere for cleaner organization?
				BaseScreen screen = mGameManager.getCurrentScreen();
				if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENCOUNTER) return;
				
				String opponentType = encounterType.opponentType().toXmlStringUpdate(getResources());
				if (opponent.type == ShipType.MANTIS) {
					opponentType = Opponent.MANTIS.toXmlStringUpdate(getResources());
				}
				
				String description = "";
				if (commanderGotHit)
				{
					description += getResources().getString(R.string.screen_encounter_description_opponenthit, opponentType);
				}

				if (!(prevEncounterType == Encounter.Police.FLEE || prevEncounterType == Encounter.Trader.FLEE ||
						prevEncounterType == Encounter.Pirate.FLEE) && !commanderGotHit)
				{
					if (description.length() > 0) description += "\n";
					description += getResources().getString(R.string.screen_encounter_description_opponentmiss, opponentType);
				}

				if (opponentGotHit)
				{
					if (description.length() > 0) description += "\n";
					description += getResources().getString(R.string.screen_encounter_description_commanderhit, opponentType);
				}

				if (!commanderFlees && !opponentGotHit)
				{
					if (description.length() > 0) description += "\n";
					description += getResources().getString(R.string.screen_encounter_description_commandermiss, opponentType);
				}

				if (prevEncounterType == Encounter.Police.FLEE || prevEncounterType == Encounter.Trader.FLEE ||
						prevEncounterType == Encounter.Pirate.FLEE)	
				{
					if (description.length() > 0) description += "\n";
					description += getResources().getString(R.string.screen_encounter_description_opponentnoescape, opponentType);
				}

				if (commanderFlees)
				{
					if (description.length() > 0) description += "\n";
					description += getResources().getString(R.string.screen_encounter_description_commandernoescape, opponentType);
				}

				TextView textView = (TextView) screen.getView().findViewById(R.id.screen_encounter_description);
				textView.setText(description);
				int l = textView.getLineCount();
				if (l > 2 || l <= 0) {
					Log.d("Encounter description","Too many lines! Converting '\\n' to ' '");
					description = description.replace('\n', ' ');
					Log.d("Encounter description","description="+description);
					textView.setText(description);
				}
				
				encounterDisplayNextAction(false);
				
				break;
			case NOTHING:
				break;
			default:
				// This should never happen
				break;
			}
			
			if (autoAttack || autoFlee) {
				autoHandler.postDelayed(autoRun, 1000);
			}
			
			encounterButtonRunning = false;
			runningTask = null;
		}
		
		private Result doEncounterButton(final EncounterButton action) {
			stop = false;
//			redrawButtons = false;
			
			if (action == EncounterButton.TRIBBLE)
			{
//		    	if (autoAttack || autoFlee)
//	    			redrawButtons = true;
				autoAttack = false;
				autoFlee = false;
//	    		if (redrawButtons)
//	    			publishProgress();
	    	
//				// NB Moved this dialog to encounterFormHandleEvent()
//	    		CountDownLatch latch = newLatch();
//	    		mGameManager.showDialogFragment(SimpleDialog.creator(
//	    				R.string.screen_encounter_squeek_title, 
//	    				R.string.screen_encounter_squeek_message,
//	    				R.string.help_squeek,
//						newUnlocker(latch)));
//	    		lock(latch);
	    		return Result.NOTHING;
			}

		    if ((action == EncounterButton.ATTACK)) // Attack
		    {
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();

		    	if (ship.totalWeapons(null, null) <= 0)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_noweapons_title, 
		    				R.string.screen_encounter_noweapons_message,
		    				R.string.help_noweapons,
							newUnlocker(latch)));
		    		lock(latch);
		    		return Result.NOTHING;
		    	}

		    	if (encounterType == Encounter.Police.INSPECTION && ship.getCargo(TradeItem.FIREARMS) <= 0 &&
		    			ship.getCargo(TradeItem.NARCOTICS) <= 0)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_noillegal_title, 
		    				R.string.screen_encounter_noillegal_message,
		    				R.string.screen_encounter_noillegal_yes,
		    				R.string.screen_encounter_noillegal_no,
		    				R.string.help_suretofleeorbribe,
		    				newUnlocker(latch),
		    				newStopper(latch)));
					lock(latch);
					
					if (stop) return Result.NOTHING;
		    	}

		    	if (encounterType.opponentType() == Opponent.POLICE || encounterType == Encounter.VeryRare.POSTMARIEPOLICE)
		    	{

		    		if (policeRecordScore > PoliceRecord.CRIMINAL.score) {
		    			CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    					R.string.screen_encounter_attackpolice_title, 
		    					R.string.screen_encounter_attackpolice_message,
		    					R.string.help_attackbyaccident,
		    					newUnlocker(latch),
		    					newStopper(latch)));
		    			lock(latch);
		    			if (stop) return Result.NOTHING;

		    			policeRecordScore = PoliceRecord.CRIMINAL.score;
		    		}

		    		policeRecordScore += PoliceRecord.ATTACKPOLICESCORE;

		    		if (encounterType == Encounter.Police.IGNORE || encounterType == Encounter.Police.INSPECTION
		    				|| encounterType == Encounter.VeryRare.POSTMARIEPOLICE
		    				)
		    		{
		    			encounterType = Encounter.Police.ATTACK;
		    		}
		    	}
		    	else if (encounterType.opponentType() == Opponent.PIRATE)
		    	{
		    		if (encounterType == Encounter.Pirate.IGNORE)
		    			encounterType = Encounter.Pirate.ATTACK;
		    	}
		    	else if (encounterType.opponentType() == Opponent.TRADER)
		    	{
		    		if (encounterType == Encounter.Trader.IGNORE 
		    				|| encounterType == Encounter.Trader.BUY || encounterType == Encounter.Trader.SELL
		    				)
		    		{
		    			if (policeRecordScore >= PoliceRecord.CLEAN.score)
		    			{
		    				CountDownLatch latch = newLatch();
				    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
				    				R.string.screen_encounter_attacktrader_title, 
				    				R.string.screen_encounter_attacktrader_message,
				    				R.string.help_attacktrader,
				    				newUnlocker(latch),
				    				newStopper(latch)));
							lock(latch);
							if (stop) return Result.NOTHING;
		    				policeRecordScore = PoliceRecord.DUBIOUS.score;
		    			}
		    			else
		    				policeRecordScore += PoliceRecord.ATTACKTRADERSCORE;
		    		}
		    		if (encounterType != Encounter.Trader.FLEE)
		    		{
		    			if (opponent.totalWeapons(null, null) <= 0)
		    				encounterType = Encounter.Trader.FLEE;
		    			else if (getRandom( Reputation.ELITE.score ) <= (reputationScore * 10) / (1 + opponent.type.ordinal()))
		    				encounterType = Encounter.Trader.FLEE;
		    			else
		    				encounterType = Encounter.Trader.ATTACK;
		    		}
		    	}
		    	else if (encounterType.opponentType() == Opponent.MONSTER)
		    	{
		    		if (encounterType == Encounter.Monster.IGNORE)
		    			encounterType = Encounter.Monster.ATTACK;
		    	}
		    	else if (encounterType.opponentType() == Opponent.DRAGONFLY)
		    	{
		    		if (encounterType == Encounter.Dragonfly.IGNORE)
		    			encounterType = Encounter.Dragonfly.ATTACK;
		    	}
		    	else if (encounterType.opponentType() == Opponent.SCARAB)
		    	{
		    		if (encounterType == Encounter.Scarab.IGNORE)
		    			encounterType = Encounter.Scarab.ATTACK;
		    	}
		    	else if (encounterType.opponentType() == Opponent.FAMOUSCAPTAIN)
		    	{
		    		if (encounterType != Encounter.VeryRare.FAMOUSCAPATTACK) {
			    		CountDownLatch latch = newLatch();
			    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
			    				R.string.screen_encounter_suretoattackfamous_title, 
			    				R.string.screen_encounter_suretoattackfamous_message,
			    				R.string.help_attackgreatcaptain,
			    				newUnlocker(latch),
			    				newStopper(latch)));
						lock(latch);
						
						if (stop) return Result.NOTHING;
		    		}
		    		if (policeRecordScore > PoliceRecord.VILLAIN.score)
		    			policeRecordScore = PoliceRecord.VILLAIN.score;
		    		policeRecordScore += PoliceRecord.ATTACKTRADERSCORE;
		    		if (encounterType == Encounter.VeryRare.CAPTAINHUIE)
		    			addNewsEvent(NewsEvent.CAPTAINHUIEATTACKED);
		    		else if (encounterType == Encounter.VeryRare.CAPTAINAHAB)
		    			addNewsEvent(NewsEvent.CAPTAINAHABATTACKED);
		    		else if (encounterType == Encounter.VeryRare.CAPTAINCONRAD)
		    			addNewsEvent(NewsEvent.CAPTAINCONRADATTACKED);

		    		encounterType = Encounter.VeryRare.FAMOUSCAPATTACK;

		    	}
		    	if (continuous)
		    		autoAttack = true;
		    	if (executeAction( false ))
		    		return Result.REFRESH;
		    	if (ship.hull <= 0)
		    		return Result.DEAD;
		    }					
		    else if ((action == EncounterButton.FLEE)) // Flee
		    {			
//		    	if (autoAttack || autoFlee)
//	    			redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//	    		if (redrawButtons)
//	    			publishProgress();

		    	if (encounterType == Encounter.Police.INSPECTION && ship.getCargo(TradeItem.FIREARMS) <= 0 &&
		    			ship.getCargo(TradeItem.NARCOTICS) <= 0  && wildStatus != 1 && (reactorStatus == 0 || reactorStatus == 21)
		    			)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_noillegal_title, 
		    				R.string.screen_encounter_noillegal_message,
		    				R.string.screen_encounter_noillegal_yes,
		    				R.string.screen_encounter_noillegal_no,
		    				R.string.help_suretofleeorbribe,
		    				newUnlocker(latch),
		    				newStopper(latch)));
					lock(latch);
					
					if (stop) return Result.NOTHING;
		    	}

		    	if (encounterType == Encounter.Police.INSPECTION)
		    	{
		    		encounterType = Encounter.Police.ATTACK;
		    		if (policeRecordScore > PoliceRecord.DUBIOUS.score)
		    			policeRecordScore = PoliceRecord.DUBIOUS.score - (difficulty.compareTo(DifficultyLevel.NORMAL) < 0 ? 0 : 1);
		    		else
		    			policeRecordScore += PoliceRecord.FLEEFROMINSPECTION;
		    	}
		    	else if (encounterType == Encounter.VeryRare.POSTMARIEPOLICE)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_suretofleepostmarie_title,
		    				R.string.screen_encounter_suretofleepostmarie_message,
		    				R.string.help_fleepostmarie,
		    				newUnlocker(latch),
		    				newStopper(latch)));
		    		lock(latch);

		    		if (stop) return Result.NOTHING;

		    		encounterType = Encounter.Police.ATTACK;
	    			if (policeRecordScore >= PoliceRecord.CRIMINAL.score)
	    				policeRecordScore = PoliceRecord.CRIMINAL.score;
	    			else
	    				policeRecordScore += PoliceRecord.ATTACKPOLICESCORE;

		    	}

		    	if (continuous)
		    		autoFlee = true;
		    	if (executeAction( true ))
		    		return Result.REFRESH;
		    	if (ship.hull <= 0)
		    		return Result.DEAD;
		    }
		    else if (action == EncounterButton.IGNORE) // Ignore
		    {			
		    	// Only occurs when opponent either ignores you or flees, so just continue
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();
		    }		
		    else if (action == EncounterButton.TRADE) // Trade in Orbit
		    {	
		    	if (encounterType == Encounter.Trader.BUY)
		    	{				
		    		final TradeItem item = getRandomTradeableItem (ship, false);
		    		int price = sellPrice.get(item);
		    		
		    		if (item == TradeItem.NARCOTICS || item == TradeItem.FIREARMS)
		    		{
		    			if (getRandom(100) <= 45)
		    				price *= 0.8;
		    			else
		    				price *= 1.1;
		    		}
		    		else
		    		{
		    			if (getRandom(100) <= 10)
		    				price *= 0.9;
		    			else
		    				price *= 1.1;
		    		}

		    		price /= item.roundOff;
		    		++price;
		    		price *= item.roundOff;
		    		if (price < item.minTradePrice)
		    			price = item.minTradePrice;
		    		if (price > item.maxTradePrice)
		    			price = item.maxTradePrice;
		    		
		    		sellPrice.put(item, price);

		    		final int fPrice = price;
		    		final CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(InputDialog.newInstance(
		    				R.string.dialog_tradeinorbit_title, 
		    				R.string.dialog_tradeinorbit_buymessage,
		    				R.string.generic_ok,
		    				R.string.generic_all,
		    				R.string.generic_none,
		    				R.string.help_tradeinorbit,
		    				new OnPositiveListener() {
								
								@Override
								public void onClickPositiveButton(int value) {
				    				int amount = max(0, min(ship.getCargo(item), value));
				    				sellInOrbit(item, amount, fPrice, latch);
								}
							},
		    				new OnNeutralListener() {
								
								@Override
								public void onClickNeutralButton() {
									int amount = ship.getCargo(item);
					    			sellInOrbit(item, amount, fPrice, latch);
								}
							},
							new OnNegativeListener() {
								
								@Override
								public void onClickNegativeButton() {
									unlock(latch);
								}
							},
							item,
							price,
							ship.getCargo(item),
							buyingPrice.get(item) / ship.getCargo(item)
							));
		    		lock(latch);
		    	}
		    	else if (encounterType == Encounter.Trader.SELL)
		    	{				
		    		final TradeItem item = getRandomTradeableItem (opponent, true);

			    	Log.d("Trade bug", "Item is "+item);
		    		int price = buyPrice.get(item);
		    		if (item == TradeItem.NARCOTICS || item == TradeItem.FIREARMS)
		    		{
		    			if (getRandom(100) <= 45)
		    				price *= 1.1;
		    			else
		    				price *= 0.8;
		    		}
		    		else
		    		{
		    			if (getRandom(100) <= 10)
		    				price *= 1.1;
		    			else
		    				price *= 0.9;
		    		}

		    		price /= item.roundOff;
		    		price *= item.roundOff;
		    		if (price < item.minTradePrice)
		    			price = item.minTradePrice;
		    		if (price > item.maxTradePrice)
		    			price = item.maxTradePrice;

		    		buyPrice.put(item, price);
		    		
		    		final int fPrice = price;
		    		final CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(InputDialog.newInstance(
		    				R.string.dialog_tradeinorbit_title, 
		    				R.string.dialog_tradeinorbit_sellmessage,
		    				R.string.generic_ok,
		    				R.string.generic_all,
		    				R.string.generic_none,
		    				R.string.help_tradeinorbit,
		    				new OnPositiveListener() {
								
								@Override
								public void onClickPositiveButton(int value) {
				    				int amount = max(0, min(opponent.getCargo(item), value));
					    			buyInOrbit(item, amount, fPrice, latch);
								}
							},
		    				new OnNeutralListener() {
								
								@Override
								public void onClickNeutralButton() {
									int amount = min(opponent.getCargo(item), (ship.totalCargoBays()-ship.filledCargoBays()));
					    			buyInOrbit(item, amount, fPrice, latch);
								}
							},
							new OnNegativeListener() {
								
								@Override
								public void onClickNegativeButton() {
									unlock(latch);
								}
							},
							item,
							price,
							opponent.getCargo(item),
							credits / price
							));
		    		lock(latch);
		    	}
		    }			
		    else if (action == EncounterButton.YIELD) // Yield Narcotics from Marie Celeste
		    {	

    			if (wildStatus == 1)
			    {
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_surrender_title, 
		    				R.string.screen_encounter_surrender_extra,
		    				R.string.help_wanttosurrender,
		    				newUnlocker(latch),
		    				newStopper(latch),
		    				mGameManager.getResources().getString(R.string.screen_encounter_surrender_wildonboard),
    						mGameManager.getResources().getString(R.string.screen_encounter_surrender_wildarrested)
		    				));
					lock(latch);
					if (stop) return Result.NOTHING;
				}
				else if (reactorStatus > 0 && reactorStatus < 21)
				{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_surrender_title, 
		    				R.string.screen_encounter_surrender_extra,
		    				R.string.help_wanttosurrender,
		    				newUnlocker(latch),
		    				newStopper(latch),
		    				mGameManager.getResources().getString(R.string.screen_encounter_surrender_reactoronboard),
    						mGameManager.getResources().getString(R.string.screen_encounter_surrender_reactortaken)
		    				));
					lock(latch);
					if (stop) return Result.NOTHING;
				}

		    	if (wildStatus == 1 || (reactorStatus > 0 && reactorStatus < 21))
		    	{
		    		arrested();
		    	}
		    	else
		    	{			
//		    		// NB added some new logic here. If you dumped your illegal goods after the Marie encounter, then Customs Police can't do anything.
//		    		// However, this angers them, so if you're a criminal they will arrest you anyway.
//		    		if (ship.getCargo(TradeItem.FIREARMS) == 0 && ship.getCargo(TradeItem.NARCOTICS) == 0)
//		    		{
//		    			if (policeRecordScore >= PoliceRecord.DUBIOUS.score) 
//		    			{
//				    		CountDownLatch latch = newLatch();
//				    		mGameManager.showDialogFragment(SimpleDialog.creator(
//				    				R.string.screen_encounter_contrabandnotfound_title,
//				    				R.string.screen_encounter_contrabandnotfound_message,
//				    				newUnlocker(latch)));
//							lock(latch);
//		    			}
//		    			else 
//		    			{
//				    		CountDownLatch latch = newLatch();
//				    		mGameManager.showDialogFragment(SimpleDialog.creator(
//				    				R.string.screen_encounter_contrabandnotfound_title,
//				    				R.string.screen_encounter_contrabandnotfound_arrestedmessage,
//				    				newUnlocker(latch)));
//							lock(latch);
//							
//							arrested();
//		    			}
//		    		}
//		    		else
		    		{
		    			// This is the generic case in the original code.
		    			
			    		// Police Record becomes dubious, if it wasn't already.
			    		if (policeRecordScore > PoliceRecord.DUBIOUS.score)
			    			policeRecordScore = PoliceRecord.DUBIOUS.score;
			    		ship.clearCargo(TradeItem.NARCOTICS);
			    		ship.clearCargo(TradeItem.FIREARMS);

			    		CountDownLatch latch = newLatch();
			    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
			    				R.string.screen_encounter_yieldnarcotics_title,
			    				R.string.screen_encounter_yieldnarcotics_message,
			    				R.string.help_customspoliceconfiscated,
			    				newUnlocker(latch)));
						lock(latch);
		    		}
		    		
		    	}
		    }	
		    
		    else if (action == EncounterButton.SURRENDER) // Surrender
		    {
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();

		    	if (opponent.type == ShipType.MANTIS)
		    	{
		    		if (artifactOnBoard)
		    		{
			    		CountDownLatch latch = newLatch();
			    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
			    				R.string.screen_encounter_wanttosurrendertoaliens_title,
			    				R.string.screen_encounter_wanttosurrendertoaliens_message,
			    				R.string.help_wanttosurrendertoaliens,
			    				newUnlocker(latch),
			    				newStopper(latch)));
						lock(latch);
						
						if (stop) return Result.NOTHING;
		    			
						latch = newLatch();
			    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
			    				R.string.screen_encounter_artifactstolen_title,
			    				R.string.screen_encounter_artifactstolen_message,
			    				R.string.help_artifactstolen,
			    				newUnlocker(latch)));
						lock(latch);
	    				artifactOnBoard = false;

		    		}
		    		else
		    		{
		    			// NB This message is somewhat out-of-character when encountered by aliens who have invaded Gemulon
			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_nosurrender_title, 
		    					R.string.screen_encounter_nosurrender_message,
		    					R.string.help_nosurrender,
								newUnlocker(latch)));
			    		lock(latch);
		    			return Result.NOTHING;
		    		}
		    	}
		    	else if (encounterType.opponentType() == Opponent.POLICE)
		    	{
		    		if (policeRecordScore <= PoliceRecord.PSYCHOPATH.score)
		    		{
			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_nosurrender_title, 
		    					R.string.screen_encounter_nosurrender_message,
		    					R.string.help_nosurrender,
								newUnlocker(latch)));
			    		lock(latch);
		    			return Result.NOTHING;
		    		}
		    		else
		    		{
		    			if (wildStatus == 1)
					    {
				    		CountDownLatch latch = newLatch();
				    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
				    				R.string.screen_encounter_surrender_title, 
				    				R.string.screen_encounter_surrender_extra,
				    				R.string.help_wanttosurrender,
				    				newUnlocker(latch),
				    				newStopper(latch),
				    				mGameManager.getResources().getString(R.string.screen_encounter_surrender_wildonboard),
		    						mGameManager.getResources().getString(R.string.screen_encounter_surrender_wildarrested)
				    				));
							lock(latch);
							if (stop) return Result.NOTHING;
						}
						else if (reactorStatus > 0 && reactorStatus < 21)
						{
				    		CountDownLatch latch = newLatch();
				    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
				    				R.string.screen_encounter_surrender_title, 
				    				R.string.screen_encounter_surrender_extra,
				    				R.string.help_wanttosurrender,
				    				newUnlocker(latch),
				    				newStopper(latch),
				    				mGameManager.getResources().getString(R.string.screen_encounter_surrender_reactoronboard),
		    						mGameManager.getResources().getString(R.string.screen_encounter_surrender_reactortaken)
				    				));
							lock(latch);
							if (stop) return Result.NOTHING;
						}
						else
						{
				    		CountDownLatch latch = newLatch();
				    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
				    				R.string.screen_encounter_surrender_title, 
				    				R.string.screen_encounter_surrender_message,
				    				R.string.help_wanttosurrender,
				    				newUnlocker(latch),
				    				newStopper(latch)));
							lock(latch);
							if (stop) return Result.NOTHING;
						}
					
						arrested();
						return Result.NOTHING;

		    		}
		    	}
		    	else
		    	{
		    		raided = true;

		    		int totalCargo = 0;
		    		for (TradeItem item : TradeItem.values())
		    			totalCargo += ship.getCargo(item);
		    		if (totalCargo <= 0)
		    		{
		    			int blackmail = min( 25000, max( 500, currentWorth() / 20 ) );
		    			if (credits >= blackmail)
		    				credits -= blackmail;
		    			else
		    			{
		    				debt += (blackmail - credits);
		    				credits = 0;
		    			}

			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_lootnocargo_title,
		    					R.string.screen_encounter_lootnocargo_message,
		    					R.string.help_piratesfindnocargo,
								newUnlocker(latch)));
			    		lock(latch);
		    		}		
		    		else
		    		{	

		    			// NB it's interesting that the original code explicitly checks here if the pirates have room, but doesn't do so for traders.
		    			int bays = opponent.type.cargoBays;
		    			for (int i=0; i<opponent.gadget.length; ++i)
		    				if (opponent.gadget[i] == Gadget.EXTRABAYS)
		    					bays += 5;
		    			for (TradeItem item : TradeItem.values())
		    				bays -= opponent.getCargo(item);

		    			// Pirates steal everything					
		    			if (bays >= totalCargo)
		    			{
		    				for (TradeItem item : TradeItem.values())
		    				{
		    					ship.clearCargo(item);
		    					buyingPrice.put(item, 0);
		    				}
		    			}		
		    			else
		    			{		
		    				// Pirates steal a lot
		    				while (bays > 0)
		    				{
		    					TradeItem item = getRandom( TradeItem.values() );
		    					if (ship.getCargo(item) > 0)
		    					{
		    						buyingPrice.put(item, (buyingPrice.get(item) * (ship.getCargo(item) - 1)) / ship.getCargo(item) );
		    						ship.addCargo(item, -1);
		    						--bays;
		    					}
		    				}
		    			}

			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_looting_title,
		    					R.string.screen_encounter_looting_message,
		    					R.string.help_piratesplunder,
								newUnlocker(latch)));
			    		lock(latch);
		    		}
		    		if ((wildStatus == 1) && (opponent.type.crewQuarters > 1))
		    		{
		    			// Wild hops onto Pirate Ship
		    			wildStatus = 0;
			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_wildgoeswithpirates_title,
		    					R.string.screen_encounter_wildgoeswithpirates_message,
		    					R.string.help_wildswitchesships,
								newUnlocker(latch)));
			    		lock(latch);
		    		}
		    		else if (wildStatus == 1)
		    		{
		    			// no room on pirate ship
			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_wildstaysaboard_title,
		    					R.string.screen_encounter_wildstaysaboard_message,
		    					R.string.help_wildstaysaboard,
								newUnlocker(latch)));
			    		lock(latch);
		    		}
		    		if (reactorStatus > 0 && reactorStatus < 21)
		    		{
		    			// pirates puzzled by reactor
			    		CountDownLatch latch = newLatch();
		    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    					R.string.screen_encounter_piratesdontstealreactor_title,
		    					R.string.screen_encounter_piratesdontstealreactor_message,
		    					R.string.help_reactornottaken,
								newUnlocker(latch)));
			    		lock(latch);
		    		}
		    	}
		    }
		    else if (action == EncounterButton.BRIBE) // Bribe
		    {			
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();

		    	if (warpSystem.politics().bribeLevel <= 0)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_nobribe_title,
		    				R.string.screen_encounter_nobribe_message,
		    				R.string.help_cantbebribed,
							newUnlocker(latch)));
		    		lock(latch);
		    		return Result.NOTHING;
		    	}
		    	
		    	if (encounterType == Encounter.VeryRare.POSTMARIEPOLICE)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_nobribe_title,
		    				R.string.screen_encounter_mariecantbebribed_message,
		    				R.string.help_mariecantbribe,
							newUnlocker(latch)));
		    		lock(latch);
		    		return Result.NOTHING;
		    	}

		    	if (encounterType == Encounter.Police.INSPECTION && ship.getCargo(TradeItem.FIREARMS) <= 0 &&
		    			ship.getCargo(TradeItem.NARCOTICS) <= 0 && wildStatus != 1)
		    	{
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_noillegal_title, 
		    				R.string.screen_encounter_noillegal_message,
		    				R.string.screen_encounter_noillegal_yes,
		    				R.string.screen_encounter_noillegal_no,
		    				R.string.help_suretofleeorbribe,
		    				newUnlocker(latch),
		    				newStopper(latch)));
					lock(latch);
					
					if (stop) return Result.NOTHING;
		    	}

		    	// Bribe depends on how easy it is to bribe the police and commander's current worth
		    	int bribe = currentWorth() / 
		    			((10 + 5 * (DifficultyLevel.IMPOSSIBLE.ordinal() - difficulty.ordinal())) * warpSystem.politics().bribeLevel);
		    	if (bribe % 100 != 0)
		    		bribe += (100 - (bribe % 100));
		    	if (wildStatus == 1 || (reactorStatus > 0 && reactorStatus < 21))
		    	{
		    		if (difficulty.compareTo(DifficultyLevel.NORMAL) <= 0)
		    			bribe *= 2;
		    		else
		    			bribe *= 3;
		    	}
		    	bribe = max( 100, min( bribe, 10000 ) );
		    	final int fBribe = bribe;
	    		final CountDownLatch latch = newLatch();
		    	mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    			R.string.screen_encounter_bribe_title, 
		    			R.string.screen_encounter_bribe_message, 
		    			R.string.screen_encounter_bribe_yes,
		    			R.string.screen_encounter_bribe_no,
		    			R.string.help_bribe,
		    			new OnConfirmListener() {
							@Override
							public void onConfirm() {
					    		if (credits < fBribe)
					    		{
						    		stop = true;
					    			mGameManager.showDialogFragment(SimpleDialog.newInstance(
						    			R.string.screen_encounter_cantaffordbribe_title, 
						    			R.string.screen_encounter_cantaffordbribe_message,
						    			R.string.help_nomoneyforbribe,
						    			newUnlocker(latch)
										));
					    		} else {
					    			credits -= fBribe;
						    		unlock(latch);
					    		}
							}
						},
						newStopper(latch),
						fBribe));
	    		lock(latch);

		    	if (stop) return Result.NOTHING;
		    	else return Result.TRAVEL;
		    }
		    else if (action == EncounterButton.SUBMIT) // Submit
		    {
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();
		    	
		    	if (encounterType == Encounter.Police.INSPECTION && (ship.getCargo(TradeItem.FIREARMS) > 0 ||
		    			ship.getCargo(TradeItem.NARCOTICS) > 0 || wildStatus == 1 || (reactorStatus > 1 && reactorStatus < 21)))
		    	{
		    		String illegalGoodsString;
		    		String arrestedString = "";
		    		if (wildStatus == 1)
		    		{
		    			if (ship.getCargo(TradeItem.FIREARMS) > 0 ||ship.getCargo(TradeItem.NARCOTICS) > 0)
		    			{
		    				illegalGoodsString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_wildgoods);
		    			}
		    			else
		    			{
		    				illegalGoodsString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_wild);
		    			}
		    			arrestedString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_arrested);
		    		}
		    		else if (reactorStatus > 0 && reactorStatus < 21)
		    		{
		    			if (ship.getCargo(TradeItem.FIREARMS) > 0 ||ship.getCargo(TradeItem.NARCOTICS) > 0)
		    			{
		    				illegalGoodsString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_reactorgoods);
		    			}
		    			else
		    			{
		    				illegalGoodsString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_reactor);
		    			}
		    			arrestedString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_arrested);
		    		}
		    		else
		    		{
	    				illegalGoodsString = mGameManager.getResources().getString(R.string.screen_encounter_illegal_goods);
		    		}
		    		
			    	CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_illegal_title, 
		    				R.string.screen_encounter_illegal_message,
		    				R.string.screen_encounter_illegal_yes,
		    				R.string.generic_no,
		    				R.string.help_suretosubmit,
		    				newUnlocker(latch),
		    				newStopper(latch),
		    				illegalGoodsString,
		    				arrestedString));
		    		lock(latch);
		    		if (stop) return Result.NOTHING;
		    		
		    	}
		    	
		    	if ((ship.getCargo(TradeItem.FIREARMS) > 0) || (ship.getCargo(TradeItem.NARCOTICS) > 0))
		    	{
		    		// If you carry illegal goods, they are impounded and you are fined
		    		ship.clearCargo(TradeItem.FIREARMS);
		    		buyingPrice.put(TradeItem.FIREARMS, 0);
		    		ship.clearCargo(TradeItem.NARCOTICS);
		    		buyingPrice.put(TradeItem.NARCOTICS, 0);
		    		int fine = currentWorth() / ((DifficultyLevel.IMPOSSIBLE.ordinal()+2-difficulty.ordinal()) * 10);
		    		if (fine % 50 != 0)
		    			fine += (50 - (fine % 50));
		    		fine = max( 100, min( fine, 10000 ) );
		    		if (credits >= fine)
		    			credits -= fine;
		    		else
		    		{
		    			debt += (fine - credits);
		    			credits = 0;
		    		}
		    		
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_illegalfound_title, 
		    				R.string.screen_encounter_illegalfound_message,
		    				R.string.help_illegalgoods,
		    				newUnlocker(latch),
		    				fine));
		    		lock(latch);
		    		policeRecordScore += PoliceRecord.TRAFFICKING;
		    	}
		    	else if (wildStatus != 1)
		    	{
		    		// If you aren't carrying illegal goods, the police will increase your lawfulness record
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_nothingfound_title, 
		    				R.string.screen_encounter_nothingfound_message,
		    				R.string.help_noillegalgoods,
		    				newUnlocker(latch)));
		    		lock(latch);
		    		policeRecordScore -= PoliceRecord.TRAFFICKING;
		    	}
		    	
		    	if (wildStatus == 1)
		    	{
		    		// Jonathan Wild Captured, and your status damaged.
		    		arrested();
		    		return Result.NOTHING;
		    	}
		    	if (reactorStatus > 0 && reactorStatus < 21)
		    	{
		    		// Police confiscate the Reactor.
		    		// Of course, this can only happen if somehow your
		    		// Police Score gets repaired while you have the
		    		// reactor on board -- otherwise you'll be arrested
		    		// before we get to this point. (no longer true - 25 August 2002)
		    		CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_policeconfiscatereactor_title, 
		    				R.string.screen_encounter_policeconfiscatereactor_message,
		    				R.string.help_reactortaken,
		    				newUnlocker(latch)));
		    		lock(latch);
		    		reactorStatus = 0;
		    	}
		    	
		    	return Result.TRAVEL;
		    }		
		    else if (action == EncounterButton.PLUNDER) // Plunder
		    {
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();

		    	if (encounterType.opponentType() == Opponent.TRADER)
		    		policeRecordScore += PoliceRecord.PLUNDERTRADERSCORE;
		    	else
		    		policeRecordScore += PoliceRecord.PLUNDERPIRATESCORE;

		    	mGameManager.showDialogFragment(PlunderDialog.newInstance());
		    	return Result.NOTHING;
		    }
		    else if (action == EncounterButton.INTERRUPT) // Interrupt automatic attack/flight
		    {
//		    	if (autoAttack || autoFlee)
//		    		redrawButtons = true;
		    	autoAttack = false;
		    	autoFlee = false;
//		    	if (redrawButtons)
//		    		publishProgress();
		    	
		    	clearButtonAction();
		    	
		    	return Result.REFRESH;
		    }
		    else if (action == EncounterButton.MEET) // Meet with Famous Captain
		    {
		    	if (encounterType == Encounter.VeryRare.CAPTAINAHAB)
		    	{
		    		// Trade a reflective shield for skill points in piloting?
			    	CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_engagecaptainahab_title,
		    				R.string.screen_encounter_engagecaptainahab_message,
		    				R.string.help_tradecaptainahab,
		    				newUnlocker(latch),
		    				newStopper(latch)));
		    		lock(latch);
		    		if (stop) return Result.TRAVEL;
		    		
		    		// remove the last reflective shield
		    		int i=ship.shield.length - 1;
		    		while (i >= 0)
		    		{
		    			if (ship.shield[i] == Shield.REFLECTIVE)
		    			{
		    				for (int m=i+1; m<ship.shield.length; ++m)
		    				{
		    					ship.shield[m-1] = ship.shield[m];
		    					ship.shieldStrength[m-1] = ship.shieldStrength[m];
		    				}
		    				ship.shield[ship.shield.length-1] = null;
		    				ship.shieldStrength[ship.shield.length-1] = 0;
		    				i = -1;
		    			}
		    			i--;
		    		}
		    		// add points to piloting skill
		    		// two points if you're on beginner-normal, one otherwise
		    		commander().famousCaptainSkillIncrease(Skill.PILOT);
//		    		if (difficulty.compareTo(DifficultyLevel.HARD) < 0)
//		    			commander().pilot += 2;
//		    		else
//		    			commander().pilot += 1;
//
//		    		if (commander().pilot > MAXSKILL)
//		    		{
//		    			commander().pilot = MAXSKILL;
//		    		}
		    		latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_trainingcompleted_title, 
		    				R.string.screen_encounter_trainingcompleted_message,
		    				R.string.help_training,
		    				newUnlocker(latch)));
		    		lock(latch);
		    	}
		    	else if (encounterType == Encounter.VeryRare.CAPTAINCONRAD)
		    	{
		    		// Trade a military laser for skill points in engineering?
			    	CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_engagecaptainconrad_title,
		    				R.string.screen_encounter_engagecaptainconrad_message,
		    				R.string.help_tradecaptainconrad,
		    				newUnlocker(latch),
		    				newStopper(latch)));
		    		lock(latch);
		    		if (stop) return Result.TRAVEL;
		    		
		    		// remove the last military laser
		    		int i=ship.weapon.length - 1;
		    		while (i>=0)
		    		{
		    			if (ship.weapon[i] == Weapon.MILITARY)
		    			{
		    				for (int m=i+1; m<ship.weapon.length; ++m)
		    				{
		    					ship.weapon[m-1] = ship.weapon[m];
		    				}
		    				ship.weapon[ship.weapon.length-1] = null;
		    				i = -1;
		    			}
		    			i--;
		    		}
		    		// add points to engineering skill
		    		// two points if you're on beginner-normal, one otherwise
		    		commander().famousCaptainSkillIncrease(Skill.ENGINEER);
//		    		if (difficulty.compareTo(DifficultyLevel.HARD) < 0)
//		    			commander().engineer += 2;
//		    		else
//		    			commander().engineer += 1;
//
//		    		if (commander().engineer > MAXSKILL)
//		    		{
//		    			commander().engineer = MAXSKILL;
//		    		}
		    		latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_trainingcompleted_title, 
		    				R.string.screen_encounter_trainingcompleted_message,
		    				R.string.help_training,
		    				newUnlocker(latch)));
		    		lock(latch);

		    	}
		    	else if (encounterType == Encounter.VeryRare.CAPTAINHUIE)
		    	{
		    		// Trade a military laser for skill points in engineering?
			    	CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_engagecaptainhuie_title,
		    				R.string.screen_encounter_engagecaptainhuie_message, 
		    				R.string.help_tradecaptainhuie,
		    				newUnlocker(latch),
		    				newStopper(latch)));
		    		lock(latch);
		    		if (stop) return Result.TRAVEL;
		    		
		    		// remove the last military laser
		    		int i=ship.weapon.length - 1;
		    		while (i>=0)
		    		{
		    			if (ship.weapon[i] == Weapon.MILITARY)
		    			{
		    				for (int m=i+1; m<ship.weapon.length; ++m)
		    				{
		    					ship.weapon[m-1] = ship.weapon[m];
		    				}
		    				ship.weapon[ship.weapon.length-1] = null;
		    				i = -1;
		    			}
		    			i--;
		    		}
		    		// add points to trading skill
		    		// two points if you're on beginner-normal, one otherwise
		    		commander().famousCaptainSkillIncrease(Skill.TRADER);
//		    		if (difficulty.compareTo(DifficultyLevel.HARD) < 0)
//		    			commander().trader += 2;
//		    		else
//		    			commander().trader += 1;
//
//		    		if (commander().trader > MAXSKILL)
//		    		{
//		    			commander().trader = MAXSKILL;
//		    		}
		    		recalculateBuyPrices(curSystem());
		    		latch = newLatch();
		    		mGameManager.showDialogFragment(SimpleDialog.newInstance(
		    				R.string.screen_encounter_trainingcompleted_title, 
		    				R.string.screen_encounter_trainingcompleted_message,
		    				R.string.help_training,
		    				newUnlocker(latch)));
		    		lock(latch);
		    	}
		    }
		    else if (action == EncounterButton.BOARD) // Board Marie Celeste
		    {
		    	if (encounterType == Encounter.VeryRare.MARIECELESTE)
		    	{
		    		// take the cargo of the Marie Celeste?
			    	CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_engagemarie_title,
		    				R.string.screen_encounter_engagemarie_message, 
		    				R.string.help_lootmarieceleste,
		    				newUnlocker(latch),
		    				newStopper(latch)));
		    		lock(latch);
		    		if (stop) return Result.TRAVEL;
		    		
		    		narcs = ship.getCargo(TradeItem.NARCOTICS);
		    		mGameManager.showDialogFragment(PlunderDialog.newInstance());
		    		return Result.NOTHING;
		    	}
		    }		
		    else if (action == EncounterButton.DRINK) // Drink Tonic?
		    {
		    	if (encounterType == Encounter.VeryRare.BOTTLEGOOD)
		    	{
		    		// Quaff the good bottle of Skill Tonic?
		    		final CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.dialog_engagebottle_title,
		    				R.string.dialog_engagebottle_message, 
		    				R.string.dialog_engagebottle_pos,
		    				R.string.generic_no,
		    				R.string.help_drinkoldtonic,
		    				new OnConfirmListener() {
								
								@Override
								public void onConfirm() {
									// two points if you're on beginner-normal, one otherwise
									commander().increaseRandomSkill();
									if (difficulty.compareTo(DifficultyLevel.HARD) < 0)
										commander().increaseRandomSkill();
									
									mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_drink_title, R.string.dialog_gooddrink_message, R.string.help_drankgoodskilltonic, newUnlocker(latch)));
								}
							},
							newStopper(latch)));
		    		lock(latch);
		    		

		    	}
		    	else if (encounterType == Encounter.VeryRare.BOTTLEOLD)
		    	{
		    		// Quaff the out of date bottle of Skill Tonic?
		    		final CountDownLatch latch = newLatch();
		    		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
		    				R.string.dialog_engagebottle_title,
		    				R.string.dialog_engagebottle_message, 
		    				R.string.dialog_engagebottle_pos,
		    				R.string.generic_no,
		    				R.string.help_drinkoldtonic,
		    				new OnConfirmListener() {
								
								@Override
								public void onConfirm() {
					    			commander().tonicTweakRandomSkill();
					    			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_drink_title, R.string.dialog_strangedrink_message, R.string.help_drankoldskilltonic, newUnlocker(latch)));
								}
							},
							newStopper(latch)));
		    		lock(latch);

		    	}
		    }
		    
		    return Result.TRAVEL;
		}
		
		// new Trade In Orbit methods because android version needs to call these from two different places to mimic original application flow.
		
		// Player sells to trader (Encounter.Trader.Buy)
		private void sellInOrbit(TradeItem item, int amount, int price, CountDownLatch latch) {
//			amount = min( amount, opponent.type.cargoBays );
			amount = min (amount, opponent.totalCargoBays() - opponent.filledCargoBays());	// NB this is more accurate that original since we have these functions for all ships now
			buyingPrice.put(item, buyingPrice.get(item)*(ship.getCargo(item)-amount)/ship.getCargo(item));
			ship.addCargo(item, -amount);
			opponent.addCargo(item, amount);
			credits += amount * price;
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_tradecompleted_title,
					R.string.dialog_tradecompleted_buymessage,
					-1, // NB original has no help text here.
					newUnlocker(latch),
					item));
		}
				
		// player buys from trader (Encounter.Trader.Sell)
		private void buyInOrbit(TradeItem item, int amount, int price, CountDownLatch latch) {
			amount = min ( amount, (credits / buyPrice.get(item)));
			ship.addCargo(item, amount);
			opponent.addCargo(item, -amount);
			buyingPrice.put(item, buyingPrice.get(item) + (amount * price));
			credits -= amount * price;
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_tradecompleted_title,
					R.string.dialog_tradecompleted_sellmessage,
					-1, // NB original has no help text here.
					newUnlocker(latch),
					item));
		}
		
		

		
		// *************************************************************************
		// You can pick up cannisters left by a destroyed ship
		// *************************************************************************
		private void scoop(  )
		{

			// Chance 50% to pick something up on Normal level, 33% on Hard level, 25% on
			// Impossible level, and 100% on Easy or Beginner
			if (difficulty.compareTo(DifficultyLevel.NORMAL) >= 0)
				if (getRandom( difficulty.ordinal() ) != 1)
					return;
			
			// More chance to pick up a cheap good
			TradeItem d = getRandom( TradeItem.values() );
			if (d.ordinal() >= 5)
				d = getRandom( TradeItem.values() );
			
			final TradeItem item = d;
			final CountDownLatch latch = newLatch();
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_scoop_title, 
					R.string.dialog_scoop_message,
					R.string.dialog_scoop_pos,
					R.string.dialog_scoop_neg,
					R.string.help_pickcannister,
					new OnConfirmListener() {

						@Override
						public void onConfirm() {
							
							if (ship.filledCargoBays() >= ship.totalCargoBays())
							{
								mGameManager.showDialogFragment(ConfirmDialog.newInstance(
										R.string.dialog_scoopnoroom_title, 
										R.string.dialog_scoopnoroom_message,
										R.string.dialog_scoopnoroom_pos,
										R.string.dialog_scoopnoroom_neg,
										R.string.help_pickcannister,
										new OnConfirmListener() {
											
											@Override
											public void onConfirm() {
												mGameManager.showDialogFragment(JettisonDialog.newInstance(
														new OnConfirmListener() {
															
															@Override
															public void onConfirm() {
																scoop2(item, latch);
															}
														}
														)); 
											}
										}, 
										new OnCancelListener() {
											
											@Override
											public void onCancel() {
												scoop2(item, latch);
											}
										}));
							} else {
								scoop2(item, latch);
							}
						}
					}, 
					newStopper(latch),
					item));
			lock(latch);
			
		}
		
		// The second half of the original Scoop() method is broken off here so it can be called from multiple locations.
		private void scoop2(final TradeItem item, final CountDownLatch latch) {
			if (ship.filledCargoBays() < ship.totalCargoBays()) {
				ship.addCargo(item, 1);
				unlock(latch);
			}
			else
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_noscoop_title, 
						R.string.dialog_noscoop_message, 
						R.string.help_nodumpnoscoop,
						new OnConfirmListener() {
					@Override
					public void onConfirm() {
						unlock(latch);
					}
				}));
		}
		
		
		// *************************************************************************
		// An attack: Attacker attacks Defender, Flees indicates if Defender is fleeing
		// *************************************************************************
		private boolean executeAttack( Ship attacker, Ship defender, boolean flees, boolean commanderUnderAttack )
		{

			// On beginner level, if you flee, you will escape unharmed.
			if (difficulty == DifficultyLevel.BEGINNER && commanderUnderAttack && flees)
				return false;

			// Fighterskill attacker is pitted against pilotskill defender; if defender
			// is fleeing the attacker has a free shot, but the chance to hit is smaller
			if (getRandom( attacker.skill(Skill.FIGHTER) + defender.type.size.ordinal() ) < 
				(flees ? 2 : 1) * getRandom( 5 + (defender.skill(Skill.PILOT) >> 1) ))
				// Misses
				return false;

			int damage;
			if (attacker.totalWeapons(null, null) <= 0)
				damage = 0;
			else if (defender.type == ShipType.SCARAB)
			{
				if (attacker.totalWeapons( Weapon.PULSE, Weapon.PULSE ) <= 0 &&
					attacker.totalWeapons( Weapon.MORGAN, Weapon.MORGAN ) <= 0)
					damage = 0;
				else
					damage =  getRandom( ((attacker.totalWeapons( Weapon.PULSE, Weapon.PULSE ) +
							attacker.totalWeapons( Weapon.MORGAN, Weapon.MORGAN )) * (100 + 2*attacker.skill(Skill.ENGINEER)) / 100) );
			}
			else
				damage = getRandom( (attacker.totalWeapons(null, null) * (100 + 2*attacker.skill(Skill.ENGINEER)) / 100) );

			if (damage <= 0)
				return false;

			// Reactor on board -- damage is boosted!
			if (commanderUnderAttack && reactorStatus > 0 && reactorStatus < 21)
			{
				if (difficulty.compareTo(DifficultyLevel.NORMAL) < 0)
					damage *= 1 + (difficulty.ordinal() + 1)*0.25;
				else
					damage *= 1 + (difficulty.ordinal() + 1)*0.33;
			}
			
			// First, shields are depleted
			for (int i=0; i<defender.shield.length; ++i)
			{
				if (defender.shield[i] == null)
					break;
				if (damage <= defender.shieldStrength[i])
				{
					defender.shieldStrength[i] -= damage;
					damage = 0;
					break;
				}
				damage -= defender.shieldStrength[i];
				defender.shieldStrength[i] = 0;
			}

			int prevDamage = damage;
			
			// If there still is damage after the shields have been depleted, 
			// this is subtracted from the hull, modified by the engineering skill
			// of the defender.
			if (damage > 0)
			{
				damage -= getRandom( defender.skill(Skill.ENGINEER) );
				if (damage <= 0)
					damage = 1;
				// At least 2 shots on Normal level are needed to destroy the hull 
				// (3 on Easy, 4 on Beginner, 1 on Hard or Impossible). For opponents,
				// it is always 2.
				if (commanderUnderAttack && scarabStatus == 3)
					damage = min( damage, (ship.getHullStrength()/
						(commanderUnderAttack ? max( 1, (DifficultyLevel.IMPOSSIBLE.ordinal()-difficulty.ordinal()) ) : 2)) );
				else
					damage = min( damage, (defender.type.hullStrength/
						(commanderUnderAttack ? max( 1, (DifficultyLevel.IMPOSSIBLE.ordinal()-difficulty.ordinal()) ) : 2)) );
				defender.hull -= damage;
				if (defender.hull < 0)
					defender.hull = 0;
			}

			if (damage != prevDamage)
			{
				if (commanderUnderAttack)
				{
					playerShipNeedsUpdate = true;
				}
				else
				{
					opponentShipNeedsUpdate = true;
				}
			}

			return true;
		}
		
		// *************************************************************************
		// A fight round
		// Return value indicates whether fight continues into another round
		// *************************************************************************
		private boolean executeAction( boolean commanderFlees )
		{
			this.commanderFlees = commanderFlees;
			
			int opponentHull = opponent.hull;
			int shipHull = ship.hull;
			
			commanderGotHit = false;
			// Fire shots
			if (encounterType == Encounter.Pirate.ATTACK || encounterType == Encounter.Police.ATTACK ||
				encounterType == Encounter.Trader.ATTACK || encounterType == Encounter.Monster.ATTACK ||
				encounterType == Encounter.Dragonfly.ATTACK || encounterType == Encounter.VeryRare.POSTMARIEPOLICE ||
				encounterType == Encounter.Scarab.ATTACK || encounterType == Encounter.VeryRare.FAMOUSCAPATTACK
				)
			{
				commanderGotHit = executeAttack( opponent, ship, commanderFlees, true );
				if (encounterAnim) animateAttack(true, commanderGotHit, ship.hull <= 0); // NB Animation is a new addition
			}

			opponentGotHit = false;
			
			if (!commanderFlees)
			{
				if (encounterType == Encounter.Police.FLEE || encounterType == Encounter.Trader.FLEE ||
						encounterType == Encounter.Pirate.FLEE)	
				{
					opponentGotHit = executeAttack( ship, opponent, true, false );
				}
				else
				{
					opponentGotHit = executeAttack( ship, opponent, false, false );
				}
				if (encounterAnim) animateAttack(false, opponentGotHit, opponent.hull <= 0); // NB Animation is a new addition
			}

			if (commanderGotHit)
			{
				playerShipNeedsUpdate = true;
			}
			if (opponentGotHit)
			{
				 opponentShipNeedsUpdate = true;
			}

			// Determine whether someone gets destroyed
			if (ship.hull <= 0 && opponent.hull <= 0)
			{
				autoAttack = false;
				autoFlee = false;
				publishProgress();
			
				if (escapePod)
				{
					escapeWithPod();
					return( true );
				}
				else
				{
		    		CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_bothlose_title, 
							R.string.screen_encounter_bothlose_message,
							R.string.help_bothdestroyed,
							newUnlocker(latch)));
		    		lock(latch);
		    		
				}
				return false;
			}
			else if (opponent.hull <= 0)
			{
				autoAttack = false;
				autoFlee = false;
				publishProgress();
						
				if (encounterType.opponentType() == Opponent.PIRATE && opponent.type != ShipType.MANTIS && policeRecordScore >= PoliceRecord.DUBIOUS.score)
				{
		    		CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_bounty_title, 
							R.string.screen_encounter_bounty_message,
							R.string.help_bounty,
							newUnlocker(latch),
							opponent.type,
							opponent.getBounty()));
		    		lock(latch);
				}
				else
				{
		    		CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_win_title,
							R.string.screen_encounter_win_message,
							R.string.help_opponentdestroyed,
							newUnlocker(latch)));
		    		lock(latch);
				}
				if (encounterType.opponentType() == Opponent.POLICE)
				{
					++policeKills;
					policeRecordScore += PoliceRecord.KILLPOLICESCORE;
				}
				else if (encounterType.opponentType() == Opponent.FAMOUSCAPTAIN)
				{
					if (reputationScore < Reputation.DANGEROUS.score)
					{
						reputationScore = Reputation.DANGEROUS.score;
					}
					else
					{
						reputationScore += 100;
					}
					// bump news flag from attacked to ship destroyed
					switch (latestNewsEvent()) {
					case CAPTAINAHABATTACKED:
						replaceNewsEvent(latestNewsEvent(), NewsEvent.CAPTAINAHABDESTROYED);
						break;
					case CAPTAINCONRADATTACKED:
						replaceNewsEvent(latestNewsEvent(), NewsEvent.CAPTAINCONRADDESTROYED);
						break;
					case CAPTAINHUIEATTACKED:
						replaceNewsEvent(latestNewsEvent(), NewsEvent.CAPTAINHUIEDESTROYED);
						break;
					default:
						// Do nothing. This shouldn't ever come up.
						break;
						
					}
					
				}
				else if (encounterType.opponentType() == Opponent.PIRATE)
				{
					if (opponent.type != ShipType.MANTIS)
					{
						if (policeRecordScore >= PoliceRecord.DUBIOUS.score) // NB added this check to match when the bounty dialog appears.
							credits += opponent.getBounty();
						
						policeRecordScore += PoliceRecord.KILLPIRATESCORE;
						scoop();
					}
					++pirateKills;
				}
				else if (encounterType.opponentType() == Opponent.TRADER)
				{
					++traderKills;
					policeRecordScore += PoliceRecord.KILLTRADERSCORE;
					scoop();
				}
				else if (encounterType.opponentType() == Opponent.MONSTER)
				{
					++pirateKills;
					policeRecordScore += PoliceRecord.KILLPIRATESCORE;
					monsterStatus = 2;
				}
				else if (encounterType.opponentType() == Opponent.DRAGONFLY)
				{
					++pirateKills;
					policeRecordScore += PoliceRecord.KILLPIRATESCORE;
					dragonflyStatus = 5;
				}
				else if (encounterType.opponentType() == Opponent.SCARAB)
				{
					++pirateKills;
					policeRecordScore += PoliceRecord.KILLPIRATESCORE;
					scarabStatus = 2;
				}
				reputationScore += 1 + (opponent.type.ordinal()>>1);
				return false;
			}
			else if (ship.hull <= 0)
			{
				autoAttack = false;
				autoFlee = false;
				publishProgress();
			
				if (escapePod)
				{
					escapeWithPod();
					return( true );
				}
				else
				{
		    		CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_lose_title, 
							R.string.screen_encounter_lose_message,
							R.string.help_shipdestroyed,
							newUnlocker(latch)));
		    		lock(latch);
				}
				return false;
			}
			
			// Determine whether someone gets away.
			if (commanderFlees)
			{
				if (difficulty == DifficultyLevel.BEGINNER)
				{
					autoAttack = false;
					autoFlee = false;

					if (encounterAnim) animateEnterExit(true, false); // NB Animation is a new addition

		    		CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_escaped_title,
							R.string.screen_encounter_escaped_message,
							R.string.help_youescaped,
							newUnlocker(latch)));
		    		lock(latch);
					
					if (encounterType.opponentType() == Opponent.MONSTER)
						monsterHull = opponent.hull;

					return false;
				}
				else if ((getRandom( 7 ) + (ship.skill(Skill.PILOT) / 3)) * 2 >= 
					getRandom( opponent.skill(Skill.PILOT) ) * (2 + difficulty.ordinal()))
				{
					autoAttack = false;
					autoFlee = false;

					if (encounterAnim) animateEnterExit(true, false); // NB Animation is a new addition

					if (commanderGotHit)
					{
						publishProgress();

						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_encounter_escaped_title,
								R.string.screen_encounter_escapedhit_message,
								R.string.help_youescaped,
								newUnlocker(latch)));
			    		lock(latch);
					}
					else {
						CountDownLatch latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_encounter_escaped_title,
								R.string.screen_encounter_escaped_message,
								R.string.help_youescaped,
								newUnlocker(latch)));
			    		lock(latch);
					}
					
					if (encounterType.opponentType() == Opponent.MONSTER)
						monsterHull = opponent.hull;
						
					return false;
				}
			}
			else if (encounterType == Encounter.Police.FLEE || encounterType == Encounter.Trader.FLEE ||
				encounterType == Encounter.Pirate.FLEE || encounterType == Encounter.Trader.SURRENDER ||
				encounterType == Encounter.Pirate.SURRENDER)	
			{
				if (getRandom( ship.skill(Skill.PILOT) ) * 4 <= 
					getRandom( (7 + (opponent.skill(Skill.PILOT) / 3))) * 2)
				{
					autoAttack = false;
					autoFlee = false;

					publishProgress();

					if (encounterAnim) animateEnterExit(false, false); // NB Animation is a new addition
					
		    		CountDownLatch latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_opponentescaped_title,
							R.string.screen_encounter_opponentescaped_message,
							R.string.help_opponentescaped,
							newUnlocker(latch)));
		    		lock(latch);
					return false;
				}
			}
			
			// Determine whether the opponent's actions must be changed
			prevEncounterType = encounterType;
			
			if (opponent.hull < opponentHull)
			{
				if (encounterType.opponentType() == Opponent.POLICE)
				{
					if (opponent.hull < opponentHull >> 1)
						if (ship.hull < shipHull >> 1)
						{
							if (getRandom( 10 ) > 5)
								encounterType = Encounter.Police.FLEE;
						}	
						else
							encounterType = Encounter.Police.FLEE;
				}
				else if (encounterType == Encounter.VeryRare.POSTMARIEPOLICE)
				{
					encounterType = Encounter.Police.ATTACK;
				}
				else if (encounterType.opponentType() == Opponent.PIRATE)
				{
					if (opponent.hull < (opponentHull * 2) / 3)
					{
						if (ship.hull < (shipHull * 2) / 3)
						{
							if (getRandom( 10 ) > 3)
								encounterType = Encounter.Pirate.FLEE;
						}
						else
						{
							encounterType = Encounter.Pirate.FLEE;
							if (getRandom( 10 ) > 8 && opponent.type.ordinal() < ShipType.buyableValues().length)
								encounterType = Encounter.Pirate.SURRENDER;
						}
					}
				}
				else if (encounterType.opponentType() == Opponent.TRADER)
				{
					if (opponent.hull < (opponentHull * 2) / 3)
					{
						if (getRandom( 10 ) > 3)
							encounterType = Encounter.Trader.SURRENDER;
						else
							encounterType = Encounter.Trader.FLEE;
					}
					else if (opponent.hull < (opponentHull * 9) / 10)
					{
						if (ship.hull < (shipHull * 2) / 3)
						{
							// If you get damaged a lot, the trader tends to keep shooting
							if (getRandom( 10 ) > 7)
								encounterType = Encounter.Trader.FLEE;
						}
						else if (ship.hull < (shipHull * 9) / 10)
						{
							if (getRandom( 10 ) > 3)
								encounterType = Encounter.Trader.FLEE;
						}
						else
							encounterType = Encounter.Trader.FLEE;
					}
				}
			}


//			if (encounterAnim) {
//				if (prevEncounterType.action() != OpponentAction.FLEE && encounterType.action() == OpponentAction.FLEE) {
//					animateFlee(false);
//				}
//			}


			if (prevEncounterType != encounterType)
			{
				if (!(attackFleeing &&	// NB Original used autoAttack instead of attackFleeing here, which was why that option did nothing.
					(encounterType == Encounter.Trader.FLEE || encounterType == Encounter.Pirate.FLEE || encounterType == Encounter.Police.FLEE)))
					autoAttack = false;
				autoFlee = false;
			}
			
//			publishProgress();

			return true;
		}
		
	}

	
	/*
	 * Fuel.c
	 */
	// *************************************************************************
	// Buy Fuel for Amount credits
	// *************************************************************************
	public void buyFuel( int amount )
	{
		int maxFuel = (ship.getFuelTanks() - ship.getFuel()) * ship.type.costOfFuel;
		if (amount > maxFuel)
			amount = maxFuel;
		if (amount > credits)
			amount = credits;
			
		int parsecs = amount / ship.type.costOfFuel;
		
		ship.fuel += parsecs;
		credits -= parsecs * ship.type.costOfFuel;
	}
	
	/*
	 * Math.c
	 */
	// *************************************************************************
	// Temporary implementation of square root
	// *************************************************************************
	// NB Just outsource to Math package instead of original logic
	public static int sqrt( int a )
	{
		return (int) Math.round(Math.sqrt(a));
	}

	// *************************************************************************
	// Square of the distance between two solar systems
	// *************************************************************************
	public static int sqrDistance( SolarSystem a, SolarSystem b )
	{
		return (sqr( a.x() - b.x() ) + sqr( a.y() - b.y() ));
	}

	// *************************************************************************
	// Distance between two solar systems
	// *************************************************************************
	public static int realDistance(  SolarSystem a, SolarSystem b )
	{
		return (sqrt( sqrDistance( a, b ) ));
	}
	
	
	// *************************************************************************
	// Pieter's new random functions, tweaked a bit by SjG
	// *************************************************************************
	// NB these are used in randomly generating newspaper in a deterministic way

	private static final int DEFSEEDX = 521288629;
	private static final int DEFSEEDY = 362436069;

	private int seedX = DEFSEEDX;
	private int seedY = DEFSEEDY;

	private int getRandom2(int maxVal)
	{
		int out = (rand() % maxVal);
		if (out < 0) {
			out += maxVal;
		}
		return out;
	}

	private int rand()
	{
	   final int a = 18000;
	   final int b = 30903;

	   seedX = a*(seedX&MAX_WORD) + (seedX>>16);
	   seedY = b*(seedY&MAX_WORD) + (seedY>>16);

	   return ((seedX<<16) + (seedY&MAX_WORD));
	}

	private void randSeed( int seed1, int seed2 )
	{
	   if (seed1 > 0)
	       seedX = seed1;   /* use default seeds if parameter is 0 */
	   else
	       seedX = DEFSEEDX;

	   if (seed2 > 0)
	       seedY = seed2;
	   else
	       seedY = DEFSEEDY;
	} 


	/*
	 * Money.c
	 */
	// *************************************************************************
	// Current worth of commander
	// *************************************************************************
	public int currentWorth( )
	{
		return ship.currentPrice(false) + credits - debt + (moonBought ? COSTMOON : 0);
	}
	

	// *************************************************************************
	// Pay interest on debt
	// *************************************************************************
	public void payInterest(  )
	{
		if (debt > 0)
		{
			int incDebt = max( 1, debt / 10 );
			if (credits > incDebt)
				credits -= incDebt;
			else 
			{
				debt += (incDebt - credits);
				credits = 0;
			}
		}
	}
	
	
	/*
	 * OtherEvent.c
	 */
	public void drawSpecialCargoForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.CARGO) return;
				
		screen.setViewVisibilityById(R.id.screen_status_cargo_tribbles, ship.tribbles > 0, false);
		if (ship.tribbles > 0)
		{
			if (ship.tribbles >= MAXTRIBBLES)
				screen.setViewTextById(R.id.screen_status_cargo_tribbles, R.string.screen_status_cargo_manytribbles);
			else
			{
				screen.setViewTextById(
						R.id.screen_status_cargo_tribbles, 
						getResources().getQuantityString(R.plurals.screen_status_cargo_tribbles, ship.tribbles, ship.tribbles)
						);
			}
		}

		screen.setViewVisibilityById(R.id.screen_status_cargo_antidote, japoriDiseaseStatus == 1, false);
		screen.setViewVisibilityById(R.id.screen_status_cargo_artifact, artifactOnBoard, false);
		screen.setViewVisibilityById(R.id.screen_status_cargo_hagglingcomputer, jarekStatus == 2, false);

		screen.setViewVisibilityById(R.id.screen_status_cargo_reactor, (reactorStatus > 0 && reactorStatus < 21), false);
		screen.setViewVisibilityById(R.id.screen_status_cargo_reactorfuel, (reactorStatus > 0 && reactorStatus < 21), false);
		if (reactorStatus > 0 && reactorStatus < 21)
		{
			int fuel = 10 - ((reactorStatus - 1) / 2);
			screen.setViewTextById(
					R.id.screen_status_cargo_reactorfuel,
					getResources().getQuantityString(R.plurals.screen_status_cargo_reactorfuel, fuel, fuel)
			);
		}
		screen.setViewVisibilityById(R.id.screen_status_cargo_singularity, canSuperWarp, false);

		boolean specialCargo = 
				ship.tribbles > 0 || 
				japoriDiseaseStatus == 1 || 
				artifactOnBoard || 
				jarekStatus == 2 || 
				(reactorStatus > 0 && reactorStatus < 21) ||
				canSuperWarp;
		screen.setViewVisibilityById(R.id.screen_status_cargo_default, !specialCargo, false);

	}
	
	
	public void specialCargoFormHandleEvent( int buttonId ) {

		if (buttonId == R.id.screen_status_back_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.STATUS);
		}
		else if (buttonId == R.id.screen_status_ship_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.SHIP);
		}
		else if (buttonId == R.id.screen_status_quests_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.QUESTS);
		}
		
	}
	
	
	/*
	 * QuestEvent.c
	 */
	// Returns number of open quests.
	private int openQuests(  )
	{
		int r = 0;
		
		if (monsterStatus == 1)
			++r;

		if (dragonflyStatus >= 1 && dragonflyStatus <= 4)
			++r;
		else if (solarSystem[zalkon].special() == SpecialEvent.INSTALLLIGHTNINGSHIELD)
			++r;

		if (japoriDiseaseStatus == 1)
			++r;

		if (artifactOnBoard)
			++r;

		if (wildStatus == 1)
			++r;

		if (jarekStatus == 1)
			++r;

		if (invasionStatus >= 1 && invasionStatus < 7)
			++r;
		else if (solarSystem[gemulon].special() == SpecialEvent.GETFUELCOMPACTOR)
			++r;

		if (experimentStatus >= 1 && experimentStatus < 11)
			++r;

		if (reactorStatus >= 1 && reactorStatus < 21)
			++r;

		if (solarSystem[nix].special() == SpecialEvent.GETSPECIALLASER)
			++r;

		if (scarabStatus == 1)
			++r;
				
		if (ship.tribbles > 0)
			++r;
				
		if (moonBought)
			++r;
			
		return r;
	}
	
	public void drawQuestsForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.QUESTS) return;

		screen.setViewVisibilityById(R.id.screen_status_quests_monster, monsterStatus == 1, false);
		screen.setViewTextById(R.id.screen_status_quests_monster, R.string.screen_status_quests_monster, solarSystem[acamar]);
		
		screen.setViewVisibilityById(R.id.screen_status_quests_dragonfly,
				(dragonflyStatus >= 1 && dragonflyStatus <= 4) || solarSystem[zalkon].special() == SpecialEvent.INSTALLLIGHTNINGSHIELD, false);
		switch (dragonflyStatus) {
		case 1:
			screen.setViewTextById(R.id.screen_status_quests_dragonfly, R.string.screen_status_quests_dragonfly, solarSystem[baratas]);
			break;
		case 2:
			screen.setViewTextById(R.id.screen_status_quests_dragonfly, R.string.screen_status_quests_dragonfly, solarSystem[melina]);
			break;
		case 3:
			screen.setViewTextById(R.id.screen_status_quests_dragonfly, R.string.screen_status_quests_dragonfly, solarSystem[regulas]);
			break;
		case 4:
			screen.setViewTextById(R.id.screen_status_quests_dragonfly, R.string.screen_status_quests_dragonfly, solarSystem[zalkon]);
			break;
		default:
			screen.setViewTextById(R.id.screen_status_quests_dragonfly, R.string.screen_status_quests_lightningshield, solarSystem[zalkon]);
			break;
		}
		
		screen.setViewVisibilityById(R.id.screen_status_quests_disease, japoriDiseaseStatus == 1, false);
		screen.setViewTextById(R.id.screen_status_quests_disease, R.string.screen_status_quests_disease, solarSystem[japori]);


		screen.setViewVisibilityById(R.id.screen_status_quests_artifact, artifactOnBoard, false);


		screen.setViewVisibilityById(R.id.screen_status_quests_wild, wildStatus == 1, false);
		screen.setViewTextById(R.id.screen_status_quests_wild, R.string.screen_status_quests_wild, solarSystem[kravat]);

		screen.setViewVisibilityById(R.id.screen_status_quests_jarek, jarekStatus == 1, false);
		screen.setViewTextById(R.id.screen_status_quests_jarek, R.string.screen_status_quests_jarek, solarSystem[devidia]);

		// I changed this, and the reused the code in the Experiment quest.
		// I think it makes more sense to display the time remaining in
		// this fashion. SjG 10 July 2002
		screen.setViewVisibilityById(R.id.screen_status_quests_invasion,
				(invasionStatus >= 1 && invasionStatus < 7) || solarSystem[gemulon].special() == SpecialEvent.GETFUELCOMPACTOR, false);
		if (invasionStatus >= 1 && invasionStatus < 7)
		{
			int days = 7 - invasionStatus;
			screen.setViewTextById(
					R.id.screen_status_quests_invasion, 
					getResources().getQuantityString(R.plurals.screen_status_quests_invasion, days, days, solarSystem[gemulon])
					);
		}
		else if (solarSystem[gemulon].special() == SpecialEvent.GETFUELCOMPACTOR)
		{
			screen.setViewTextById(R.id.screen_status_quests_invasion, R.string.screen_status_quests_fuelcompactor, solarSystem[gemulon]);
		}

		screen.setViewVisibilityById(R.id.screen_status_quests_experiment, (experimentStatus >= 1 && experimentStatus < 11), false);
		if (experimentStatus >= 1 && experimentStatus < 11)
		{
			int days = 11 - experimentStatus;
			screen.setViewTextById(
					R.id.screen_status_quests_experiment, 
					getResources().getQuantityString(R.plurals.screen_status_quests_experiment, days, days, solarSystem[daled])
					);
		}

		screen.setViewVisibilityById(R.id.screen_status_quests_reactor, (reactorStatus >= 1 && reactorStatus < 11), false);
		if (reactorStatus >= 1 && reactorStatus < 21)
		{
			if (reactorStatus < 2)
			{
				screen.setViewTextById(R.id.screen_status_quests_reactor, R.string.screen_status_quests_reactor, solarSystem[nix]);
			}
			else
			{
				screen.setViewTextById(R.id.screen_status_quests_reactor, R.string.screen_status_quests_reactor2, solarSystem[nix]);
			}
		}

		if (solarSystem[nix].special() == SpecialEvent.GETSPECIALLASER)
		{
			screen.setViewTextById(R.id.screen_status_quests_reactor, R.string.screen_status_quests_speciallaser, solarSystem[nix]);
		}

		screen.setViewVisibilityById(R.id.screen_status_quests_scarab, scarabStatus == 1, false);

		screen.setViewVisibilityById(R.id.screen_status_quests_tribbles, ship.tribbles > 0, false);
		

		screen.setViewVisibilityById(R.id.screen_status_quests_moon, moonBought, false);
		screen.setViewTextById(R.id.screen_status_quests_moon, R.string.screen_status_quests_moon, solarSystem[utopia]);
		
		screen.setViewVisibilityById(R.id.screen_status_quests_default, openQuests() == 0, false);
	}
	
	
	public void questsFormHandleEvent( int buttonId ) {

		if (buttonId == R.id.screen_status_back_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.STATUS);
		}
		else if (buttonId == R.id.screen_status_ship_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.SHIP);
		}
		else if (buttonId == R.id.screen_status_cargo_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.CARGO);
		}
		
	}
	/*
	 * SellEquipEvent.c
	 */
	// *************************************************************************
	// Handling of the events of the Sell Equipment form.
	// *************************************************************************
	public void sellEquipmentFormHandleEvent( final int buttonId )
	{
		
		mGameManager.showDialogFragment(ConfirmDialog.newInstance(
				R.string.generic_sell,
				R.string.screen_selleq_sellquery,
				R.string.help_sellitem,
				new OnConfirmListener() {
					
					@Override
					public void onConfirm() {
						boolean sale = true;
						
						if (SellEqScreen.WEAPON_BUTTON_IDS.contains(buttonId))
						{
							int index = -1;
							for (int i = 0; i < SellEqScreen.WEAPON_BUTTON_IDS.size(); i++) {
								if (buttonId == SellEqScreen.WEAPON_BUTTON_IDS.get(i)) {
									index = i;
								}
							}
							credits += ship.weapon[index].sellPrice();
							for (int i=index+1; i<ship.weapon.length; i++)
								ship.weapon[i-1] = ship.weapon[i];
							ship.weapon[ship.weapon.length-1] = null;
						}
						
						if (SellEqScreen.SHIELD_BUTTON_IDS.contains(buttonId))
						{
							int index = -1;
							for (int i = 0; i < SellEqScreen.SHIELD_BUTTON_IDS.size(); i++) {
								if (buttonId == SellEqScreen.SHIELD_BUTTON_IDS.get(i)) {
									index = i;
								}
							}
							credits += ship.shield[index].sellPrice();
							for (int i=index+1; i<ship.shield.length; i++)
							{
								ship.shield[i-1] = ship.shield[i];
								ship.shieldStrength[i-1] = ship.shieldStrength[i];
							}
							ship.shield[ship.shield.length-1] = null;
							ship.shieldStrength[ship.shieldStrength.length-1] = 0;
						}
						
						if (SellEqScreen.GADGET_BUTTON_IDS.contains(buttonId))
						{
							int index = -1;
							for (int i = 0; i < SellEqScreen.GADGET_BUTTON_IDS.size(); i++) {
								if (buttonId == SellEqScreen.GADGET_BUTTON_IDS.get(i)) {
									index = i;
								}
							}
							
							if (ship.gadget[index] == Gadget.EXTRABAYS)
							{
								if (ship.filledCargoBays() > ship.totalCargoBays() - 5)
								{
									mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_selleq_cargobaysfull_title, R.string.screen_selleq_cargobaysfull_message, R.string.help_cargobaysfull));
									sale = false;
								}
							}
							
							if (sale)
							{
								credits += ship.gadget[index].sellPrice();
								for (int i=index+1; i<ship.gadget.length; i++)
									ship.gadget[i-1] = ship.gadget[i];
								ship.gadget[ship.gadget.length-1] = null;
							}
						}
						
						if (sale)
							drawSellEquipment();
					}
				},
				null));
	}

	
	/*
	 * ShipEvent.c
	 */
	public void drawCurrentShipForm()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.SHIP) return;

		if (scarabStatus == 3)
		{
			screen.setViewTextById(R.id.screen_status_ship_type, R.string.screen_status_ship_hardened, ship.type);
		}
		else
			screen.setViewTextById(R.id.screen_status_ship_type, ship.type);

		
		for (Weapon type : Weapon.values())
		{
			int j = 0;
			for (Weapon weapon : ship.weapon)
			{
				if (weapon == type)
					++j;
			}
			screen.setViewVisibilityById(StatusShipScreen.EQUIPMENT_IDS.get(type), j > 0, false);
			if (j > 0)
			{
				screen.setViewTextById(StatusShipScreen.EQUIPMENT_IDS.get(type), type.toXmlPluralString(j, getResources()));
			}
		}

		for (Shield type : Shield.values())
		{
			int j = 0;
			for (Shield shield : ship.shield)
			{
				if (shield == type)
					++j;
			}
			screen.setViewVisibilityById(StatusShipScreen.EQUIPMENT_IDS.get(type), j > 0, false);
			if (j > 0)
			{
				screen.setViewTextById(StatusShipScreen.EQUIPMENT_IDS.get(type), type.toXmlPluralString(j, getResources()));
			}
		}
		for (Gadget type : Gadget.values())
		{
			int j = 0;
			for (Gadget gadget : ship.gadget)
			{
				if (gadget == type)
					++j;
			}
			screen.setViewVisibilityById(StatusShipScreen.EQUIPMENT_IDS.get(type), j > 0, false);
			if (j > 0)
			{
				if (type == Gadget.EXTRABAYS)
				{
					screen.setViewTextById(StatusShipScreen.EQUIPMENT_IDS.get(type), R.string.gadget_extrabays_generic, j*5);
				}
				else
				{
					screen.setViewTextById(StatusShipScreen.EQUIPMENT_IDS.get(type), getResources().getString(type.resId).toLowerCase(Locale.getDefault()));
				}
			}
		}

		screen.setViewVisibilityById(R.id.screen_status_ship_equip_escapepod, escapePod, false);

		screen.setViewVisibilityById(R.id.screen_status_ship_unfilled_layout, ship.anyEmptySlots(), false);
		if (ship.anyEmptySlots())
		{		
			// NB this bit added to mimic the fact that in original code, the unfilled items header overwrote the equipment header if the ship had no equipment
			boolean displayEquip = false;
			for (Purchasable item : StatusShipScreen.EQUIPMENT_IDS.keySet()) {
				if ( (item instanceof Weapon && ship.hasWeapon((Weapon)item, true)) ||
						(item instanceof Shield && ship.hasShield((Shield)item)) ||
						(item instanceof Gadget && ship.hasGadget((Gadget)item)) ||
						escapePod) 
					displayEquip = true;
			}
			screen.setViewVisibilityById(R.id.screen_status_ship_equip_layout, displayEquip, false);
			
			int firstEmptySlot;
			firstEmptySlot = getFirstEmptySlot(ship.type.weaponSlots, ship.weapon);
			screen.setViewVisibilityById(R.id.screen_status_ship_unfilled_weapons, firstEmptySlot >= 0, false);
			if (firstEmptySlot >= 0)
			{
				int slots = ship.type.weaponSlots - firstEmptySlot;
				screen.setViewTextById(R.id.screen_status_ship_unfilled_weapons,
						getResources().getQuantityString(R.plurals.screen_status_ship_unfilled_weapons, slots, slots)
				);
			}

			firstEmptySlot = getFirstEmptySlot(ship.type.shieldSlots, ship.shield);
			screen.setViewVisibilityById(R.id.screen_status_ship_unfilled_shields, firstEmptySlot >= 0, false);
			if (firstEmptySlot >= 0)
			{
				int slots = ship.type.shieldSlots - firstEmptySlot;
				screen.setViewTextById(R.id.screen_status_ship_unfilled_shields,
						getResources().getQuantityString(R.plurals.screen_status_ship_unfilled_shields, slots, slots)
				);
			}

			firstEmptySlot = getFirstEmptySlot(ship.type.gadgetSlots, ship.gadget);
			screen.setViewVisibilityById(R.id.screen_status_ship_unfilled_gadgets, firstEmptySlot >= 0, false);
			if (firstEmptySlot >= 0)
			{
				int slots = ship.type.gadgetSlots - firstEmptySlot;
				screen.setViewTextById(R.id.screen_status_ship_unfilled_gadgets,
						getResources().getQuantityString(R.plurals.screen_status_ship_unfilled_gadgets, slots, slots)
				);
			}

		}
		
	}
	
	
	// *************************************************************************
	// Event handler for the Current Ship screen
	// ********************************************************************
	public void currentShipFormHandleEvent( int buttonId )
	{

		if (buttonId == R.id.screen_status_back_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.STATUS);
		}
		else if (buttonId == R.id.screen_status_quests_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.QUESTS);
		}
		else if (buttonId == R.id.screen_status_cargo_button)
		{
			mGameManager.setCurrentScreenType(ScreenType.CARGO);
		}
		
	}
		
	
	/*
	 * ShiptypeInfoEvent.c
	 */
	public void drawShiptypeInfoForm()
	{
		BaseDialog dialog = mGameManager.findDialogByClass(ShipInfoDialog.class);

		dialog.setViewTextById(R.id.screen_yard_buyship_info_name, selectedShipType);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_size, selectedShipType.size);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_cargo, R.string.format_number, selectedShipType.cargoBays);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_weapon, R.string.format_number, selectedShipType.weaponSlots);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_shield, R.string.format_number, selectedShipType.shieldSlots);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_gadget, R.string.format_number, selectedShipType.gadgetSlots);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_crew, R.string.format_number, selectedShipType.crewQuarters);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_range, R.string.format_parsecs, selectedShipType.fuelTanks);
		dialog.setViewTextById(R.id.screen_yard_buyship_info_hull, R.string.format_number, selectedShipType.hullStrength);
		
		ImageView shipView = (ImageView) dialog.getDialog().findViewById(R.id.screen_yard_buyship_info_image);
		shipView.setImageResource(selectedShipType.drawableId);
	}
	
	/*
	 * Shipyard.c
	 */
	// *************************************************************************
	// Let the commander indicate how much he wants to spend on repairs
	// *************************************************************************
	public void getAmountForRepairs(  )
	{
		mGameManager.showDialogFragment(InputDialog.newInstance(
				R.string.screen_yard_buyrepairs,
				R.string.screen_yard_repairquery,
				R.string.generic_ok,
				R.string.generic_maximum,
				R.string.generic_nothing,
				R.string.help_buyrepairs,
				new OnPositiveListener() {
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) {
							buyRepairs(value);
							showShipYard();
						}
					}
				}, 
				new OnNeutralListener() {

					public void onClickNeutralButton() {
						buyRepairs(ship.getHullStrength()*ship.type.repairCosts);
						showShipYard();
					}
				}));
	}	

	// *************************************************************************
	// Let the commander indicate how much he wants to spend on fuel
	// *************************************************************************
	public void getAmountForFuel(  )
	{
		mGameManager.showDialogFragment(InputDialog.newInstance(
				R.string.screen_yard_fuelbutton, 
				R.string.screen_yard_fuelquery, 
				R.string.generic_ok, 
				R.string.generic_maximum, 
				R.string.generic_nothing, 
				R.string.help_buyfuel,
				new OnPositiveListener() {
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) {
							buyFuel(value);
							showShipYard();
						}
					}
				}, 
				new OnNeutralListener() {

					public void onClickNeutralButton() {
						buyFuel(ship.getFuelTanks()*ship.type.costOfFuel);
						showShipYard();
					}
				}));
	}	


	// *************************************************************************
	// Display the Ship Yard form.
	// Modified by SRA 04/19/01 - DisplayTradeCredits if Enabled
	// *************************************************************************
	public void showShipYard(  )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.YARD) return;

		screen.setViewVisibilityById(R.id.screen_yard_fuelbutton, ship.getFuel() < ship.getFuelTanks());
		screen.setViewVisibilityById(R.id.screen_yard_fullfuelbutton, ship.getFuel() < ship.getFuelTanks());

		screen.setViewVisibilityById(R.id.screen_yard_repairbutton, ship.hull < ship.getHullStrength());
		screen.setViewVisibilityById(R.id.screen_yard_fullrepairbutton, ship.hull < ship.getHullStrength());

		if (curSystem().techLevel().compareTo(ShipType.FLEA.minTechLevel) >= 0)
		{
			screen.setViewTextById(R.id.screen_yard_shipsbutton, R.string.screen_yard_buyship);
		}
		else
		{
			screen.setViewTextById(R.id.screen_yard_shipsbutton, R.string.screen_yard_noshipsbutton);
		}
		screen.setViewVisibilityById(R.id.screen_yard_podbutton, 
				!(escapePod || toSpend() < 2000 || curSystem().techLevel().compareTo(ShipType.values()[0].minTechLevel) < 0));

		screen.setViewTextById(R.id.screen_yard_range, getResources().getQuantityString(R.plurals.screen_yard_range, ship.getFuel(), ship.getFuel()));
		
		if (ship.getFuel() < ship.getFuelTanks())
		{
			screen.setViewTextById(R.id.screen_yard_tank, R.string.screen_yard_tank, (ship.getFuelTanks() - ship.getFuel()) * ship.type.costOfFuel);		
		}
		else
			screen.setViewTextById(R.id.screen_yard_tank, R.string.screen_yard_fulltank);		

		screen.setViewTextById(R.id.screen_yard_hull, R.string.screen_yard_hull, (ship.hull * 100) / ship.getHullStrength());
		
		if (ship.hull < ship.getHullStrength())
		{
			screen.setViewTextById(R.id.screen_yard_repair, R.string.screen_yard_repair, 
					(ship.getHullStrength() - ship.hull) * ship.type.repairCosts);
		}
		else
			screen.setViewTextById(R.id.screen_yard_repair, R.string.screen_yard_norepair);

		if (curSystem().techLevel().compareTo(ShipType.values()[0].minTechLevel) >= 0)
			screen.setViewTextById(R.id.screen_yard_ships, R.string.screen_yard_ships);
		else
			screen.setViewTextById(R.id.screen_yard_ships, R.string.screen_yard_noships);
		
		screen.setViewTextById(R.id.screen_yard_credits, R.string.format_cash, credits);

		if (escapePod)
			screen.setViewTextById(R.id.screen_yard_buypod, R.string.screen_yard_havepod);
		else if (curSystem().techLevel().compareTo(ShipType.FLEA.minTechLevel) < 0)
			screen.setViewTextById(R.id.screen_yard_buypod, R.string.screen_yard_nopod);
		else if (toSpend() < 2000)
			screen.setViewTextById(R.id.screen_yard_buypod, R.string.screen_yard_cantaffordpod);
		else
			screen.setViewTextById(R.id.screen_yard_buypod, R.string.screen_yard_buypod);
		
//		// Screenshot override
//		screen.setViewVisibilityById(R.id.screen_yard_fuelbutton, true);
//		screen.setViewVisibilityById(R.id.screen_yard_fullfuelbutton, true);
//		screen.setViewVisibilityById(R.id.screen_yard_repairbutton, true);
//		screen.setViewVisibilityById(R.id.screen_yard_fullrepairbutton, true);
//		screen.setViewTextById(R.id.screen_yard_shipsbutton, R.string.screen_yard_noshipsbutton);
//		screen.setViewVisibilityById(R.id.screen_yard_podbutton, false);
//		screen.setViewTextById(R.id.screen_yard_range, getResources().getQuantityString(R.plurals.screen_yard_range, 5, 5));
//		screen.setViewTextById(R.id.screen_yard_tank, R.string.screen_yard_tank, 70);
//		screen.setViewTextById(R.id.screen_yard_hull, R.string.screen_yard_hull, 78);
//		screen.setViewTextById(R.id.screen_yard_repair, R.string.screen_yard_repair, 22);
//		screen.setViewTextById(R.id.screen_yard_ships, R.string.screen_yard_noships);
//		screen.setViewTextById(R.id.screen_yard_credits, R.string.format_cash, 378304);
//		screen.setViewTextById(R.id.screen_yard_buypod, R.string.screen_yard_havepod);
		
		
	    

	}
	// *************************************************************************
	// Repair Ship for Amount credits
	// *************************************************************************
	private void buyRepairs( int amount )
	{
		int maxRepairs = (ship.getHullStrength() - ship.hull) * 
			ship.type.repairCosts;
		if (amount > maxRepairs)
			amount = maxRepairs;
		if (amount > credits)
			amount = credits;
			
		int percentage = amount / ship.type.repairCosts;
		
		ship.hull += percentage;
		credits -= percentage * ship.type.repairCosts;
	}

	// *************************************************************************
	// Ship Yard Form Event Handler.
	// *************************************************************************
	public void shipYardFormHandleEvent( int buttonId )
	{

		switch (buttonId)
		{
		case R.id.screen_yard_fuelbutton:
			getAmountForFuel();
			showShipYard();
			break;

		case R.id.screen_yard_fullfuelbutton:
			buyFuel( ship.getFuelTanks()*ship.type.costOfFuel );
			showShipYard();
			break;
			
		case R.id.screen_yard_repairbutton:
			getAmountForRepairs();
			showShipYard();
			break;

		case R.id.screen_yard_fullrepairbutton:
			buyRepairs( ship.getHullStrength()*ship.type.repairCosts );
			showShipYard();
			break;
			
		case R.id.screen_yard_shipsbutton:
			mGameManager.setCurrentScreenType(ScreenType.BUYSHIP);
			break;

		case R.id.screen_yard_podbutton:
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.screen_yard_buypod_title, 
					R.string.screen_yard_buypod_query, 
					R.string.help_buyescapepod,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
							escapePod = true;
							credits -= 2000;
							showShipYard();
						}
					}, null));
			break;
		}
	}
	
	/*
	 * Skill.c
	 */
	// *************************************************************************
	// After changing the trader skill, buying prices must be recalculated.
	// Revised to be callable on an arbitrary Solar System
	// *************************************************************************
	void recalculateBuyPrices( SolarSystem system )
	{
		for (TradeItem item : TradeItem.values())
		{
			if (system.techLevel().compareTo( item.techProduction) < 0 )
				buyPrice.put(item, 0);
			else if (((item == TradeItem.NARCOTICS) && (!system.politics().drugsOK)) ||
					((item == TradeItem.FIREARMS) &&	(!system.politics().firearmsOK)))
				buyPrice.put(item, 0);
			else
			{
				if (policeRecordScore < PoliceRecord.DUBIOUS.score)
					buyPrice.put(item, (sellPrice.get(item) * 100) / 90 );
				else 
					buyPrice.put(item, sellPrice.get(item));
				// BuyPrice = SellPrice + 1 to 12% (depending on trader skill (minimum is 1, max 12))
				buyPrice.put(item, (buyPrice.get(item) * (103 + (MAXSKILL - ship.skill(Skill.TRADER))) / 100) );
				if (buyPrice.get(item) <= sellPrice.get(item))
					buyPrice.put(item, sellPrice.get(item) + 1 );
			}
		}
	}
		
	// *************************************************************************
	// After erasure of police record, selling prices must be recalculated
	// *************************************************************************
	private void recalculateSellPrices(  )
	{
		for (TradeItem item : TradeItem.values())
			sellPrice.put(item, (sellPrice.get(item) * 100) / 90);
	}
	
	// *************************************************************************
	// Random mercenary skill
	// *************************************************************************
	private static int randomSkill() {
		return 1 + getRandom( 5 ) + getRandom( 6 );
	}
	
	
	
	/*
	 * SpecialEvent.c
	 */
	public void drawSpecialEventForm(Builder builder)
	{
		String sysName;	// NB The special event text may reference a specific system which is now randomizable so we must explicitly pick it out.
		switch (curSystem().special()) {
		case FLYBARATAS:
			sysName = solarSystem[melina].name;
			break;
		case FLYMELINA:
			sysName = solarSystem[regulas].name;
			break;
		case FLYREGULAS:
			sysName = solarSystem[zalkon].name;
			break;
		case MOONBOUGHT:
		case MOONFORSALE:
			sysName = solarSystem[utopia].name;
			break;
		case SPACEMONSTER:
			sysName = solarSystem[acamar].name;
			break;
		case DRAGONFLY:
			sysName = solarSystem[baratas].name;
			break;
		case JAPORIDISEASE:
			sysName = solarSystem[japori].name;
			break;
		case AMBASSADORJAREK:
		case JAREKGETSOUT:
			sysName = solarSystem[devidia].name;
			break;
		case ALIENINVASION:
		case GEMULONINVADED:
		case GEMULONRESCUED:
			sysName = solarSystem[gemulon].name;
			break;
		case EXPERIMENT:
			sysName = solarSystem[daled].name;
			break;
		case TRANSPORTWILD:
			sysName = solarSystem[kravat].name;
			break;
		case GETREACTOR:
			sysName = solarSystem[nix].name;
			break;
		default:
			sysName = null;
		}
		
		builder.setTitle(curSystem().special().titleId, sysName);
		if (curSystem().special().justAMessage) {
			builder.setPositiveButton(R.string.generic_ok);
		} else {
			builder.setPositiveButton(R.string.generic_yes);
			builder.setNegativeButton(R.string.generic_no);
		}
		
		builder.setMessage(curSystem().special().questStringId, sysName);
	}
	
	public void specialEventFormHandleEvent( int unused )
	{
		boolean handled = false;
		int firstEmptySlot;
		
		if (toSpend() < curSystem().special().price)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_notenough_title,
					R.string.dialog_notenough_message,
					R.string.help_notenoughforevent));
			handled = true;
			return;
		}

		credits -= curSystem().special().price;

		switch (curSystem().special())
		{

		case GETREACTOR:
			if (ship.filledCargoBays() > ship.totalCargoBays() - 15)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_notenoughbays_title, R.string.dialog_notenoughbays_message, R.string.help_notenoughbays));
				handled = true;
				break;
			}
			else if (wildStatus == 1)
			{
				
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.screen_warp_wildwontstayonboard_title, 
						R.string.screen_warp_wildwontstayonboard_message, 
						R.string.screen_warp_wildwontgo_pos,
						R.string.generic_cancel,
						R.string.help_wildwontgowithreactor,
						new OnConfirmListener() {
							
							@Override
							public void onConfirm() {
								mGameManager.showDialogFragment(SimpleDialog.newInstance(
										R.string.screen_warp_wildleavesship_title, 
										R.string.screen_warp_wildleavesship_message,
										R.string.help_wildleaves, 
										new OnConfirmListener() {
											
											@Override
											public void onConfirm() {
												mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_reactor_title, R.string.dialog_special_reactor_message, R.string.help_reactoronboard));
												reactorStatus = 1;
											}
										},
										curSystem().name));
								wildStatus = 0;
							}
						}, 
						null,
						curSystem().name));

			}
			else {
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_reactor_title, R.string.dialog_special_reactor_message, R.string.help_reactoronboard));
				reactorStatus = 1;
			}
			break;

		case REACTORDELIVERED:
			curSystem().setSpecial(SpecialEvent.GETSPECIALLASER);
			reactorStatus = 21;
			handled = true;
			break;	

		case MONSTERKILLED:
			break;

		case SCARAB:
			scarabStatus = 1;
			break;

		case SCARABDESTROYED:
			scarabStatus = 2;
			curSystem().setSpecial(SpecialEvent.GETHULLUPGRADED);
			handled = true;
			break;	

		case GETHULLUPGRADED:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_hullupgrade_title, R.string.dialog_special_hullupgrade_message, R.string.help_hullreinforced));
			ship.hull += Ship.UPGRADEDHULL;
			scarabStatus = 3;
			handled = true;
			break;	

		case EXPERIMENT:
			experimentStatus = 1;
			break;

		case EXPERIMENTSTOPPED:
			experimentStatus = 13;
			canSuperWarp = true;
			break;

		case EXPERIMENTNOTSTOPPED:
			break;

		case ARTIFACTDELIVERY:
			artifactOnBoard = false;
			break;

		case ALIENARTIFACT:
			artifactOnBoard = true;
			break;

		case FLYBARATAS:
		case FLYMELINA:
		case FLYREGULAS:
			++dragonflyStatus;
			break;

		case DRAGONFLYDESTROYED:
			curSystem().setSpecial(SpecialEvent.INSTALLLIGHTNINGSHIELD);
			handled = true;
			break;

		case GEMULONRESCUED:
			curSystem().setSpecial(SpecialEvent.GETFUELCOMPACTOR);
			invasionStatus = 0;
			handled = true;
			break;

		case MEDICINEDELIVERY:
			japoriDiseaseStatus = 2;
			commander().increaseRandomSkill();
			commander().increaseRandomSkill();
			break;

		case MOONFORSALE:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_special_moonbought_title, 
					R.string.dialog_special_moonbought_message, 
					R.string.help_moonbought, 
					solarSystem[utopia].name));
			moonBought = true;
			break;

		case MOONBOUGHT:
			// Game end!
			showEndGameScreen(EndStatus.MOON);
			return;

		case SKILLINCREASE:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_skillincrease_title, R.string.dialog_special_skillincrease_message, R.string.help_skillincrease));
			commander().increaseRandomSkill();
			break;

		case TRIBBLE:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_youhaveatribble_title, R.string.dialog_special_youhaveatribble_message, R.string.help_youhaveatribble));
			ship.tribbles = 1;
			break;

		case BUYTRIBBLE:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_beamovertribbles_title, R.string.dialog_special_beamovertribbles_message, R.string.help_beamovertribbles));
			credits += (ship.tribbles >> 1);
			ship.tribbles = 0;
			break;

		case ERASERECORD:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_cleanrecord_title, R.string.dialog_special_cleanrecord_message, R.string.help_cleanrecord));
			policeRecordScore = PoliceRecord.CLEAN.score;
			recalculateSellPrices();
			break;

		case SPACEMONSTER:
			monsterStatus = 1;
			for (SolarSystem system : solarSystem)
				if (system.special() == SpecialEvent.SPACEMONSTER)
					system.setSpecial(null);
			break;

		case DRAGONFLY:
			dragonflyStatus = 1;
			for (SolarSystem system : solarSystem)
				if (system.special() == SpecialEvent.DRAGONFLY)
					system.setSpecial(null);
			break;

		case AMBASSADORJAREK:
			if (ship.crew[ship.type.crewQuarters-1] != null)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_noquartersavailable_title, 
						R.string.dialog_special_noquartersavailable_message, 
						R.string.help_noquartersforjarek,
						mGameManager.getResources().getString(R.string.dialog_special_passenger_jarek)));
				handled = true;
				break;
			}
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_special_passengertakenonboard_title, 
					R.string.dialog_special_passengertakenonboard_message, 
					R.string.help_jarektakenonboard,
					mGameManager.getResources().getString(R.string.dialog_special_passenger_jarek)));
			jarekStatus = 1;
			break;

		case TRANSPORTWILD:

			if (ship.crew[ship.type.crewQuarters-1] != null)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_noquartersavailable_title, 
						R.string.dialog_special_noquartersavailable_message, 
						R.string.help_noquartersforjarek,
						mGameManager.getResources().getString(R.string.dialog_special_passenger_wild)));
				handled = true;
				break;
			}
			if (!ship.hasWeapon(Weapon.BEAM, false))
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_wildwontgetaboard_title, R.string.dialog_special_wildwontgetaboard_message, R.string.help_wildwontgo));
				handled = true;
				break;
			}
			if (reactorStatus > 0 && reactorStatus < 21)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_wildafraidofreactor_title, R.string.dialog_special_wildafraidofreactor_message, R.string.help_wildwontgowithreactor));
				handled = true;
				break;
			}
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_special_passengertakenonboard_title, 
					R.string.dialog_special_passengertakenonboard_message, 
					R.string.help_jarektakenonboard,
					mGameManager.getResources().getString(R.string.dialog_special_passenger_wild)));
			wildStatus = 1;
			break;


		case ALIENINVASION:
			invasionStatus = 1;
			break;

		case JAREKGETSOUT:
			jarekStatus = 2;
			recalculateBuyPrices(curSystem());
			break;

		case WILDGETSOUT:
			wildStatus = 2;
			// Zeethibal has a 10 in player's lowest score, an 8
			// in the next lowest score, and 5 elsewhere.
			int pilot = 5;
			int fighter = 5;
			int trader = 5;
			int engineer = 5;
			switch (ship.nthLowestSkill(1))
			{
			case PILOT:
				pilot = 10;
				break;
			case FIGHTER:
				fighter = 10;
				break;
			case TRADER:
				trader = 10;
				break;
			case ENGINEER:
				engineer = 10;
				break;
			}
			switch (ship.nthLowestSkill(2))
			{
			case PILOT:
				pilot = 8;
				break;
			case FIGHTER:
				fighter = 8;
				break;
			case TRADER:
				trader = 8;
				break;
			case ENGINEER:
				engineer = 8;
				break;
			}
			
			mercenary[mercenary.length-1] = new CrewMember(mercenary[mercenary.length-1].name, pilot, fighter, trader, engineer, this);
			mercenary[mercenary.length-1].setSystem(solarSystem[kravat]);

			if (policeRecordScore < PoliceRecord.CLEAN.score)
				policeRecordScore = PoliceRecord.CLEAN.score;
			break;


		case CARGOFORSALE:
			mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_sealedcanisters_title, R.string.dialog_special_sealedcanisters_message, R.string.help_sealedcannisters));
			TradeItem item = getRandom( TradeItem.values() );
			ship.addCargo(item, 3);
			buyingPrice.put(item, buyingPrice.get(item) + curSystem().special().price);
			break;

		case INSTALLLIGHTNINGSHIELD:
			firstEmptySlot = getFirstEmptySlot( ship.type.shieldSlots, ship.shield );
			if (firstEmptySlot < 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
				handled = true;
			}
			else
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_lightningshield_title, R.string.dialog_special_lightningshield_message, R.string.help_lightningshield));
				ship.shield[firstEmptySlot] = Shield.LIGHTNING;
				ship.shieldStrength[firstEmptySlot] = Shield.LIGHTNING.power;
			}
			break;

		case GETSPECIALLASER:
			firstEmptySlot = getFirstEmptySlot( ship.type.weaponSlots, ship.weapon );
			if (firstEmptySlot < 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
				handled = true;
			}
			else
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_morganlaser_title, R.string.dialog_special_morganlaser_message, R.string.help_morganlaserinstall));
				ship.weapon[firstEmptySlot] = Weapon.MORGAN;
			}
			break;

		case GETFUELCOMPACTOR:
			firstEmptySlot = getFirstEmptySlot( ship.type.gadgetSlots, ship.gadget );
			if (firstEmptySlot < 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
				handled = true;
			}
			else
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_special_fuelcompactor_title, R.string.dialog_special_fuelcompactor_message, R.string.help_fuelcompactor));
				ship.gadget[firstEmptySlot] = Gadget.FUELCOMPACTOR;
				ship.fuel = ship.getFuelTanks();
			}
			break;

		case JAPORIDISEASE:
			if (ship.filledCargoBays() > ship.totalCargoBays() - 10)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.dialog_notenoughbays_title, R.string.dialog_notenoughbays_message, R.string.help_notenoughbays));
				handled = true;
			}
			else
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_special_antidote_title, 
						R.string.dialog_special_antidote_message, 
						R.string.help_antidote, 
						solarSystem[japori].name));
				japoriDiseaseStatus = 1;

				handled = true;	// NB the original omits this line. It's been added back here so that the quest can be started again, as implied by the dialog text.
			}
			break;
		default:
			break;
		}
		
		if (!handled)				
			curSystem().setSpecial(null);
		
		drawSystemInformationForm();
	}
	
	

	/*
	 * SystemInfoEvent.c
	 */
	// *************************************************************************
	// Determine which mercenary is for hire in the current system
	// *************************************************************************
	public CrewMember getForHire() {
		CrewMember forHire = null;

		for (CrewMember merc : mercenary)
		{
			if (merc == ship.crew[0] || merc == ship.crew[1] || merc == ship.crew[2])
				continue;
			if (merc.curSystem() == curSystem())
			{
				forHire = merc;
				break;
			}
		}
		return forHire;
	}
	
	// *************************************************************************
	// Drawing the Personnel Roster screen
	// *************************************************************************
	// NB slightly more verbose than original because we manually do mercs 1 and 2 since we use text fields instead of coordinates
	public void drawPersonnelRoster(  )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.PERSONNEL) return;
		
		// NB we add this new check so that we display correctly if both Jarek and Wild are on board
		if (ship.type.crewQuarters == 3 && jarekStatus == 1 && wildStatus == 1) {
			screen.setViewTextById(R.id.screen_personnel_merc1_empty, R.string.screen_personnel_wild);
		}
		
		else if (ship.type.crewQuarters == 2 &&  (jarekStatus == 1 || wildStatus == 1))
		{
			if (jarekStatus == 1)
				screen.setViewTextById(R.id.screen_personnel_merc1_empty, R.string.screen_personnel_jarek);
			else
				screen.setViewTextById(R.id.screen_personnel_merc1_empty, R.string.screen_personnel_wild);
			
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_stats, false);
		}
		
		else if (ship.type.crewQuarters <= 1)
		{
			screen.setViewTextById(R.id.screen_personnel_merc1_empty, R.string.screen_personnel_noquarters);
			
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_stats, false);
		}
		
		else if (ship.crew[1] == null)
		{
			screen.setViewTextById(R.id.screen_personnel_merc1_empty, R.string.screen_personnel_vacancy);
			
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_stats, false);
		}
		
		else {
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_empty_layout, false);
			screen.setViewVisibilityById(R.id.screen_personnel_merc1_stats, true);
			
			screen.setViewTextById(R.id.screen_personnel_merc1_name, ship.crew[1].name);
			screen.setViewTextById(R.id.screen_personnel_merc1_price, R.string.format_dailycost, ship.crew[1].hirePrice());
			screen.setViewTextById(R.id.screen_personnel_merc1_pilot, R.string.screen_personnel_pilot, ship.crew[1].pilot());
			screen.setViewTextById(R.id.screen_personnel_merc1_fighter, R.string.screen_personnel_fighter, ship.crew[1].fighter());
			screen.setViewTextById(R.id.screen_personnel_merc1_trader, R.string.screen_personnel_trader, ship.crew[1].trader());
			screen.setViewTextById(R.id.screen_personnel_merc1_engineer, R.string.screen_personnel_engineer, ship.crew[1].engineer());
		}
		
		
		
		if (ship.type.crewQuarters == 3 &&  (jarekStatus == 1 || wildStatus == 1))	
		{
			if (jarekStatus == 1)
				screen.setViewTextById(R.id.screen_personnel_merc2_empty, R.string.screen_personnel_jarek);
			else
				screen.setViewTextById(R.id.screen_personnel_merc2_empty, R.string.screen_personnel_wild);

			screen.setViewVisibilityById(R.id.screen_personnel_merc2_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_stats, false);
		}
		
		else if (ship.type.crewQuarters <= 2)
		{
			screen.setViewTextById(R.id.screen_personnel_merc2_empty, R.string.screen_personnel_noquarters);
			
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_stats, false);
		}
		
		else if (ship.crew[2] == null)
		{
			screen.setViewTextById(R.id.screen_personnel_merc2_empty, R.string.screen_personnel_vacancy);
			
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_stats, false);
		}
		
		else {
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_empty_layout, false);
			screen.setViewVisibilityById(R.id.screen_personnel_merc2_stats, true);
			
			screen.setViewTextById(R.id.screen_personnel_merc2_name, ship.crew[2].name);
			screen.setViewTextById(R.id.screen_personnel_merc2_price, R.string.format_dailycost, ship.crew[2].hirePrice());
			screen.setViewTextById(R.id.screen_personnel_merc2_pilot, R.string.screen_personnel_pilot, ship.crew[2].pilot());
			screen.setViewTextById(R.id.screen_personnel_merc2_fighter, R.string.screen_personnel_fighter, ship.crew[2].fighter());
			screen.setViewTextById(R.id.screen_personnel_merc2_trader, R.string.screen_personnel_trader, ship.crew[2].trader());
			screen.setViewTextById(R.id.screen_personnel_merc2_engineer, R.string.screen_personnel_engineer, ship.crew[2].engineer());
		}

		CrewMember forHire = getForHire();
		if (forHire == null)
		{
			screen.setViewVisibilityById(R.id.screen_personnel_merc3_empty_layout, true);
			screen.setViewVisibilityById(R.id.screen_personnel_merc3_stats, false);
		}
		else
		{	
			screen.setViewVisibilityById(R.id.screen_personnel_merc3_empty_layout, false);
			screen.setViewVisibilityById(R.id.screen_personnel_merc3_stats, true);
			
			screen.setViewTextById(R.id.screen_personnel_merc3_name, forHire.name);
			screen.setViewTextById(R.id.screen_personnel_merc3_price, R.string.format_dailycost, forHire.hirePrice());
			screen.setViewTextById(R.id.screen_personnel_merc3_pilot, R.string.screen_personnel_pilot, forHire.pilot());
			screen.setViewTextById(R.id.screen_personnel_merc3_fighter, R.string.screen_personnel_fighter, forHire.fighter());
			screen.setViewTextById(R.id.screen_personnel_merc3_trader, R.string.screen_personnel_trader, forHire.trader());
			screen.setViewTextById(R.id.screen_personnel_merc3_engineer, R.string.screen_personnel_engineer, forHire.engineer());
		}

		screen.setViewTextById(R.id.screen_personnel_credits, R.string.format_cash, credits);
		
//		// Screenshot override
//		screen.setViewVisibilityById(R.id.screen_personnel_merc1_empty_layout, false);
//		screen.setViewVisibilityById(R.id.screen_personnel_merc1_stats, true);
//		screen.setViewTextById(R.id.screen_personnel_merc1_name, R.string.mercenary_crystal);
//		screen.setViewTextById(R.id.screen_personnel_merc1_price, R.string.format_dailycost, 72);
//		screen.setViewTextById(R.id.screen_personnel_merc1_pilot, R.string.screen_personnel_pilot, 3);
//		screen.setViewTextById(R.id.screen_personnel_merc1_fighter, R.string.screen_personnel_fighter, 5);
//		screen.setViewTextById(R.id.screen_personnel_merc1_trader, R.string.screen_personnel_trader, 10);
//		screen.setViewTextById(R.id.screen_personnel_merc1_engineer, R.string.screen_personnel_engineer, 6);
//		screen.setViewVisibilityById(R.id.screen_personnel_merc2_empty_layout, true);
//		screen.setViewVisibilityById(R.id.screen_personnel_merc2_stats, false);
//		screen.setViewTextById(R.id.screen_personnel_merc2_empty, R.string.screen_personnel_vacancy);
//		screen.setViewVisibilityById(R.id.screen_personnel_merc3_empty_layout, false);
//		screen.setViewVisibilityById(R.id.screen_personnel_merc3_stats, true);
//		screen.setViewTextById(R.id.screen_personnel_merc3_name, R.string.mercenary_chiti);
//		screen.setViewTextById(R.id.screen_personnel_merc3_price, R.string.format_dailycost, 78);
//		screen.setViewTextById(R.id.screen_personnel_merc3_pilot, R.string.screen_personnel_pilot, 8);
//		screen.setViewTextById(R.id.screen_personnel_merc3_fighter, R.string.screen_personnel_fighter, 6);
//		screen.setViewTextById(R.id.screen_personnel_merc3_trader, R.string.screen_personnel_trader, 6);
//		screen.setViewTextById(R.id.screen_personnel_merc3_engineer, R.string.screen_personnel_engineer, 6);
//		screen.setViewTextById(R.id.screen_personnel_credits, R.string.format_cash, 202093);
		
	}
	
	// *************************************************************************
	// Add a news event flag
	// *************************************************************************
	void addNewsEvent(NewsEvent eventFlag)
	{
		if (newsSpecialEventCount < MAXSPECIALNEWSEVENTS - 1)
			newsEvents[newsSpecialEventCount++] = eventFlag;
	}


	// *************************************************************************
	// replace a news event flag with another
	// *************************************************************************
	void replaceNewsEvent(NewsEvent originalEventFlag, NewsEvent replacementEventFlag)
	{
		
		if (originalEventFlag == null)
		{
			addNewsEvent(replacementEventFlag);
		}
		else
		{
			for (int i=0;i<newsSpecialEventCount; i++)
			{
				if (newsEvents[i] == originalEventFlag)
					newsEvents[i] = replacementEventFlag;
			}
		}
	}

	// *************************************************************************
	// Reset news event flags
	// *************************************************************************
	void resetNewsEvents()
	{
		newsSpecialEventCount = 0;
	}

	// *************************************************************************
	// get most recently addded news event flag
	// *************************************************************************
	NewsEvent latestNewsEvent()
	{
		if (newsSpecialEventCount == 0)
			return null;
		else
			return newsEvents[newsSpecialEventCount - 1];
	}


	// *************************************************************************
	// Query news event flags
	// *************************************************************************
	boolean isNewsEvent(NewsEvent eventFlag)
	{
		for (int i=0;i<newsSpecialEventCount; i++)
		{
			if (newsEvents[i] == eventFlag)
				return true;
		}
		return false;
	}
	
	
	public void drawSystemInformationForm()
	{
		// Check this first, because we use it twice: once for showing special button, and once for adding related news events.
		boolean showSpecial;
		int openQ = openQuests();
		if ((curSystem().special() == null) || 
				(curSystem().special() == SpecialEvent.BUYTRIBBLE && ship.tribbles <= 0) ||
				(curSystem().special() == SpecialEvent.ERASERECORD && policeRecordScore >= PoliceRecord.DUBIOUS.score) ||
				(curSystem().special() == SpecialEvent.CARGOFORSALE && (ship.filledCargoBays() > ship.totalCargoBays() - 3)) ||
				((curSystem().special() == SpecialEvent.DRAGONFLY || curSystem().special() == SpecialEvent.JAPORIDISEASE ||
				curSystem().special() == SpecialEvent.ALIENARTIFACT || curSystem().special() == SpecialEvent.AMBASSADORJAREK ||
				curSystem().special() == SpecialEvent.EXPERIMENT) && (policeRecordScore < PoliceRecord.DUBIOUS.score)) ||
				(curSystem().special() == SpecialEvent.TRANSPORTWILD && (policeRecordScore >= PoliceRecord.DUBIOUS.score)) ||
				(curSystem().special() == SpecialEvent.GETREACTOR && (policeRecordScore >= PoliceRecord.DUBIOUS.score || reputationScore < Reputation.AVERAGE.score || reactorStatus != 0)) ||
				(curSystem().special() == SpecialEvent.REACTORDELIVERED && !(reactorStatus > 0 && reactorStatus < 21)) ||
				(curSystem().special() == SpecialEvent.MONSTERKILLED && monsterStatus < 2) ||
				(curSystem().special() == SpecialEvent.EXPERIMENTSTOPPED && !(experimentStatus >= 1 && experimentStatus < 12)) ||
				(curSystem().special() == SpecialEvent.FLYBARATAS && dragonflyStatus < 1) ||
				(curSystem().special() == SpecialEvent.FLYMELINA && dragonflyStatus < 2) ||
				(curSystem().special() == SpecialEvent.FLYREGULAS && dragonflyStatus < 3) ||
				(curSystem().special() == SpecialEvent.DRAGONFLYDESTROYED && dragonflyStatus < 5) ||
				(curSystem().special() == SpecialEvent.SCARAB && (reputationScore < Reputation.AVERAGE.score || scarabStatus != 0)) ||
				(curSystem().special() == SpecialEvent.SCARABDESTROYED && scarabStatus != 2) ||
				(curSystem().special() == SpecialEvent.GETHULLUPGRADED && scarabStatus != 2) ||
				(curSystem().special() == SpecialEvent.MEDICINEDELIVERY && japoriDiseaseStatus != 1) ||
				(curSystem().special() == SpecialEvent.JAPORIDISEASE && (japoriDiseaseStatus != 0)) ||
				(curSystem().special() == SpecialEvent.ARTIFACTDELIVERY && !artifactOnBoard) ||
				(curSystem().special() == SpecialEvent.JAREKGETSOUT && jarekStatus != 1) ||
				(curSystem().special() == SpecialEvent.WILDGETSOUT && wildStatus != 1) ||
				(curSystem().special() == SpecialEvent.GEMULONRESCUED && !(invasionStatus >= 1 && invasionStatus <= 7)) ||
				(curSystem().special() == SpecialEvent.MOONFORSALE && (moonBought || currentWorth() < (COSTMOON * 4) / 5)) ||
				(curSystem().special() == SpecialEvent.MOONBOUGHT && moonBought != true))
			showSpecial = false;
		else if (openQ > 3 &&
		(curSystem().special() == SpecialEvent.TRIBBLE ||
		curSystem().special() == SpecialEvent.SPACEMONSTER ||
		curSystem().special() == SpecialEvent.DRAGONFLY ||
		curSystem().special() == SpecialEvent.JAPORIDISEASE ||
		curSystem().special() == SpecialEvent.ALIENARTIFACT ||
		curSystem().special() == SpecialEvent.AMBASSADORJAREK ||
		curSystem().special() == SpecialEvent.ALIENINVASION ||
		curSystem().special() == SpecialEvent.EXPERIMENT ||
		curSystem().special() == SpecialEvent.TRANSPORTWILD ||
		curSystem().special() == SpecialEvent.GETREACTOR ||
		curSystem().special() == SpecialEvent.SCARAB))
			showSpecial = false;
		else
			showSpecial = true;
		
		// Moved this from HandleEvent to here because we don't handle opening events the way the palm version did
		if (curSystem().special() == SpecialEvent.MONSTERKILLED && monsterStatus == 2)
			addNewsEvent(NewsEvent.MONSTERKILLED);
		else if (curSystem().special() == SpecialEvent.DRAGONFLY && showSpecial) // NB original omitted the showSpecial check here so the news story might display when the special button didn't
			addNewsEvent(NewsEvent.DRAGONFLY);
		else if (curSystem().special() == SpecialEvent.SCARAB && showSpecial) // NB original omitted the showSpecial check here so the news story might display when the special button didn't
			addNewsEvent(NewsEvent.SCARAB);
		else if (curSystem().special() == SpecialEvent.SCARABDESTROYED && scarabStatus == 2)
			addNewsEvent(NewsEvent.SCARABDESTROYED);
		else if (curSystem().special() == SpecialEvent.FLYBARATAS && dragonflyStatus == 1)
			addNewsEvent(NewsEvent.FLYBARATAS);
		else if (curSystem().special() == SpecialEvent.FLYMELINA && dragonflyStatus == 2)
			addNewsEvent(NewsEvent.FLYMELINA);
		else if (curSystem().special() == SpecialEvent.FLYREGULAS && dragonflyStatus == 3)
			addNewsEvent(NewsEvent.FLYREGULAS);
		else if (curSystem().special() == SpecialEvent.DRAGONFLYDESTROYED && dragonflyStatus == 5)
			addNewsEvent(NewsEvent.DRAGONFLYDESTROYED);
		else if (curSystem().special() == SpecialEvent.MEDICINEDELIVERY && japoriDiseaseStatus == 1)
			addNewsEvent(NewsEvent.MEDICINEDELIVERY);
		else if (curSystem().special() == SpecialEvent.ARTIFACTDELIVERY && artifactOnBoard)
			addNewsEvent(NewsEvent.ARTIFACTDELIVERY);
		else if (curSystem().special() == SpecialEvent.JAPORIDISEASE && japoriDiseaseStatus == 0)
			addNewsEvent(NewsEvent.JAPORIDISEASE);
		else if (curSystem().special() == SpecialEvent.JAREKGETSOUT && jarekStatus == 1)
			addNewsEvent(NewsEvent.JAREKGETSOUT);
		else if (curSystem().special() == SpecialEvent.WILDGETSOUT && wildStatus == 1)
			addNewsEvent(NewsEvent.WILDGETSOUT);
		else if (curSystem().special() == SpecialEvent.GEMULONRESCUED && invasionStatus > 0 && invasionStatus < 8)
			addNewsEvent(NewsEvent.GEMULONRESCUED);
		else if (curSystem().special() == SpecialEvent.ALIENINVASION)
			addNewsEvent(NewsEvent.ALIENINVASION);
		else if (curSystem().special() == SpecialEvent.EXPERIMENTSTOPPED && experimentStatus > 0 && experimentStatus < 12)
			addNewsEvent(NewsEvent.EXPERIMENTSTOPPED);
		else if (curSystem().special() == SpecialEvent.EXPERIMENTNOTSTOPPED)
			addNewsEvent(NewsEvent.EXPERIMENTNOTSTOPPED);
		
		// These two headlines were added manually in the original but are treated as NewsEvents now
		else if (curSystem().special() == SpecialEvent.DRAGONFLYDESTROYED && dragonflyStatus == 4)
			addNewsEvent(NewsEvent.DRAGONFLYNOTDESTROYED);
		else if (curSystem().special() == SpecialEvent.GEMULONINVADED)
			addNewsEvent(NewsEvent.GEMULONNOTRESCUED);

		curSystem().visit();


		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.INFO) return;
		if (developerMode) screen.setViewVisibilityById(R.id.screen_info_traderlayout, true);
		
		screen.setViewTextById(R.id.screen_info_name, curSystem().name);
		screen.setViewTextById(R.id.screen_info_tech, curSystem().techLevel());
		screen.setViewTextById(R.id.screen_info_gov, curSystem().politics());
		screen.setViewTextById(R.id.screen_info_resources, curSystem().specialResources);
		screen.setViewTextById(R.id.screen_info_status, R.string.screen_info_status_default, curSystem().status());
		screen.setViewTextById(R.id.screen_info_size, curSystem().size);
		screen.setViewTextById(R.id.screen_info_police, curSystem().politics().strengthPolice);
		screen.setViewTextById(R.id.screen_info_pirates, curSystem().politics().strengthPirates);
		screen.setViewTextById(R.id.screen_info_traders, curSystem().politics().strengthTraders);
		screen.setViewVisibilityById(R.id.screen_info_special, showSpecial);
		screen.setViewVisibilityById(R.id.screen_info_merc, getForHire() != null);
		
//		// Screenshot override
//		screen.setViewTextById(R.id.screen_info_name, R.string.solarsystem_hades);
//		screen.setViewTextById(R.id.screen_info_tech, TechLevel.PREAGRICULTURAL);
//		screen.setViewTextById(R.id.screen_info_gov, Politics.FEUDAL);
//		screen.setViewTextById(R.id.screen_info_resources, SpecialResources.MINERALRICH);
//		screen.setViewTextById(R.id.screen_info_status, R.string.screen_info_status_default, Status.DROUGHT);
//		screen.setViewTextById(R.id.screen_info_size, Size.LARGE);
//		screen.setViewTextById(R.id.screen_info_police, ActivityLevel.MINIMAL);
//		screen.setViewTextById(R.id.screen_info_pirates, ActivityLevel.ABUNDANT);
//		screen.setViewVisibilityById(R.id.screen_info_special, false);
//		screen.setViewVisibilityById(R.id.screen_info_merc, false);
		

	}

	// *************************************************************************
	// Handling of events on the System Information screen
	// *************************************************************************
	public void systemInformationFormHandleEvent(int buttonId)
	{

		if (buttonId == R.id.screen_info_special)
		{
			mGameManager.showDialogFragment(SpecialEventDialog.newInstance());
		}
		else if (buttonId == R.id.screen_info_merc)
		{
			mGameManager.setCurrentScreenType(ScreenType.PERSONNEL);
		}
		else if (buttonId == R.id.screen_info_news)
		{
			final int price = difficulty.ordinal() + 1;
			if (!alreadyPaidForNewspaper && toSpend() < price )
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_info_cantaffordpaper_title, R.string.screen_info_cantaffordpaper_message, R.string.help_cantbuynewspaper, price));
				return;
			}
			else
			{	
				OnConfirmListener readNewspaperListener = new OnConfirmListener() {
					
					@Override
					public void onConfirm() {
						if (!alreadyPaidForNewspaper)
						{
							credits -= price;
							alreadyPaidForNewspaper = true;
						}
						mGameManager.showDialogFragment(NewspaperDialog.newInstance());
					}
				};
				
				if (!newsAutoPay && !alreadyPaidForNewspaper)
					mGameManager.showDialogFragment(ConfirmDialog.newInstance(
							R.string.screen_info_buynewspaper_title,
							R.string.screen_info_buynewspaper_message,
							R.string.screen_info_buynewspaper_pos,
							R.string.screen_info_buynewspaper_neg,
							R.string.help_buypaper,
							readNewspaperListener,
							null,
							price));
				else readNewspaperListener.onConfirm();
			}
		}
	}
	
	private int headlineCount = 0;
	private void displayHeadline(int stringId, Object... args) {
		if (headlineCount > MAXSTORIES) return;
		int lineId = NewspaperDialog.HEADLINE_IDS.get(headlineCount++);
		mGameManager.findDialogByClass(NewspaperDialog.class).setViewTextById(lineId, stringId, args);
		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(lineId, true);
	}
	private void displayHeadline(String string, Object... args) {
		if (headlineCount > MAXSTORIES) return;
		int lineId = NewspaperDialog.HEADLINE_IDS.get(headlineCount++);
		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(lineId, true);
		mGameManager.findDialogByClass(NewspaperDialog.class).setViewTextById(lineId, String.format(string, args));
	}
	
	public String newspaperTitle() {
	    int sysIndex = 0;
	    for (SolarSystem system : solarSystem) {
	    	if (system == curSystem()) {	// Original uses warpSystem instead of curSystem() here which is weird and causes masthead to change when warpSystem changes.
	    		break;
	    	}
	    	sysIndex++;
	    }
		String title = getResources().getStringArray(curSystem().politics().mastheadId)[sysIndex % MAXMASTHEADS];
		return String.format(title, curSystem());
	}
	
	public void drawNewspaperForm()
	{
		headlineCount = 0;
//		BaseDialog dialog = mGameManager.findDialogByClass(NewspaperDialog.class);
		
	    boolean realNews = false;

	    int sysIndex = 0;
	    for (SolarSystem system : solarSystem) {
	    	if (system == curSystem()) {	// Original uses warpSystem instead of curSystem() here which is weird and causes masthead to change when warpSystem changes.
	    		break;
	    	}
	    	sysIndex++;
	    }
//		String title = getResources().getStringArray(curSystem().politics().mastheadId)[sysIndex % MAXMASTHEADS];
//		dialog.getDialog().setTitle(String.format(title, curSystem()));
					
		randSeed( sysIndex, days );

		// Special Events get to go first, crowding out other news
		for (NewsEvent event : NewsEvent.values()) {
			if (isNewsEvent(event))
			{
				// NB Unlike original, we need to sub in specific system names for variable quest systems.
				// Otherwise the enum and string resources handle all the text.
				if (event == NewsEvent.CAUGHTLITTERING) {
					// Handled later because this appears after other stories
				} else if (event.hasArgs) {
					SolarSystem system;					
					switch (event) {
					case FLYMELINA:
						system = solarSystem[melina];
						break;
					case FLYREGULAS:
						system = solarSystem[regulas];
						break;
					case DRAGONFLYNOTDESTROYED:
						system = solarSystem[zalkon];
						break;
					case JAPORIDISEASE:
						system = solarSystem[japori];
						break;
					case WILDGETSOUT:
						system = solarSystem[kravat];
						break;
					case ALIENINVASION:
						system = solarSystem[gemulon];
						break;
					default:
						throw new IllegalArgumentException();
					}
					
					displayHeadline(event.resId, system);
				}
				else{
					displayHeadline(event.resId);
				}
			}
		}

		// local system status information
		if (curSystem().status() != Status.UNEVENTFUL)
		{
			displayHeadline(curSystem().status().localHeadlineId);
		}
		
		// character-specific news.
		if (policeRecordScore <= PoliceRecord.VILLAIN.score)
		{
			int j = getRandom2(4);
			displayHeadline(getResources().getStringArray(R.array.headline_villain)[j], commander().name, curSystem());
		}

		if (policeRecordScore >= PoliceRecord.HERO.score) // NB changed == to >= so that this works as apparently intended.
		{
			int j = getRandom2(3);
			displayHeadline(getResources().getStringArray(R.array.headline_hero)[j], commander().name);
		}
		
		// caught littering?
		if  (isNewsEvent(NewsEvent.CAUGHTLITTERING))
		{
			displayHeadline(R.string.newsevent_caughtlittering, commander().name);
		}

		
		// and now, finally, useful news (if any)
		// base probability of a story showing up is (50 / MAXTECHLEVEL) * Current Tech Level
		// This is then modified by adding 10% for every level of play less than Impossible
		for (SolarSystem system : solarSystem)
		{
			if (system != curSystem() &&
			    ((realDistance(curSystem(), system) <= ship.type.fuelTanks)
			    ||
			    (wormholeExists( curSystem(), system )))	// NB this is useful but unrealistic. It's from the original so it stays.
			    )
			    
			{
				// Special stories that always get shown: moon, millionaire
				if (system.special() == SpecialEvent.MOONFORSALE)
				{
					displayHeadline(R.string.newsevent_moonforsale, system, solarSystem[utopia]);
				}
				if (system.special() == SpecialEvent.BUYTRIBBLE)
				{
					displayHeadline(R.string.newsevent_buytribble, system);
				}
				
				// And not-always-shown stories
				if ( system.status() != Status.UNEVENTFUL &&		// NB original checked uneventful in parent if statement. This led to a subtle bug where moon/tribble stories don't appear for systems with uneventful status.
						(getRandom2(100) <= STORYPROBABILITY * curSystem().techLevel().ordinal() + 10 * (5 - difficulty.ordinal())) )
				{
					int j = getRandom2(6);
					displayHeadline(getResources().getStringArray(R.array.headline_remote)[j], getResources().getString(system.status().remoteHeadlineId), system);
					realNews = true;
				}
			}
		}
		
		// if there's no useful news, we throw up at least one
		// headline from our canned news list.
		if (! realNews)
		{
			boolean[] shown = new boolean[MAXSTORIES];
			for (int i=0; i <=getRandom2(MAXSTORIES); i++)
			{
				int j = getRandom2(MAXSTORIES);
				if (!shown[j] && headlineCount < newsEvents.length) 
				{
					displayHeadline(getResources().getStringArray(curSystem().politics().headlineId)[j]);
					shown[j] = true;
				}
			}
		}
		
//		// Screenshot override
//		mGameManager.getCurrentScreen().setViewTextById(R.id.screen_info_gov, Politics.THEOCRACY);
//		mGameManager.getCurrentScreen().setViewTextById(R.id.screen_info_tech, TechLevel.AGRICULTURAL);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setTitle(getResources().getStringArray(R.array.masthead_theocracy)[0]);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewTextById(NewspaperDialog.HEADLINE_IDS.get(0), R.string.headline_local_war);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewTextById(NewspaperDialog.HEADLINE_IDS.get(1), String.format(getResources().getStringArray(R.array.headline_remote)[3], getResources().getString(R.string.headline_remote_cropfailure), getResources().getString(R.string.solarsystem_cestus)));
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewTextById(NewspaperDialog.HEADLINE_IDS.get(2), String.format(getResources().getStringArray(R.array.headline_remote)[1], getResources().getString(R.string.headline_remote_drought), getResources().getString(R.string.solarsystem_hades)));
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewTextById(NewspaperDialog.HEADLINE_IDS.get(3), String.format(getResources().getStringArray(R.array.headline_remote)[5], getResources().getString(R.string.headline_remote_war), getResources().getString(R.string.solarsystem_sol)));
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(NewspaperDialog.HEADLINE_IDS.get(0), true);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(NewspaperDialog.HEADLINE_IDS.get(1), true);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(NewspaperDialog.HEADLINE_IDS.get(2), true);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(NewspaperDialog.HEADLINE_IDS.get(3), true);
//		mGameManager.findDialogByClass(NewspaperDialog.class).setViewVisibilityById(NewspaperDialog.HEADLINE_IDS.get(4), false, false);

	}


	// *****************************************************************
	// Handling of the events of the Personnel Roster form
	// *****************************************************************
	public void personnelRosterFormHandleEvent( int buttonId )
	{
		
		final int oldTraderSkill = ship.skill(Skill.TRADER);
		
		if (buttonId == R.id.screen_personnel_merc1_fire) 
		{
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.screen_personnel_firemercenary_title,
					R.string.screen_personnel_firemercenary_message,
					R.string.help_firemercenary,
					new OnConfirmListener() {
						
						@Override
						public void onConfirm() {
							ship.crew[1] = ship.crew[2];
							ship.crew[2] = null;
							personnelRosterRedraw(oldTraderSkill);
						}
					},
					null,
					ship.crew[1]));
		}
		else if (buttonId == R.id.screen_personnel_merc2_fire) 
		{
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.screen_personnel_firemercenary_title,
					R.string.screen_personnel_firemercenary_message,
					R.string.help_firemercenary,
					new OnConfirmListener() {
						
						@Override
						public void onConfirm() {
							ship.crew[2] = null;
							personnelRosterRedraw(oldTraderSkill);
						}
					},
					null));
		}
		else if (buttonId == R.id.screen_personnel_merc3_hire) 
		{
			CrewMember forHire = getForHire();

			int firstFree = -1;
			if (ship.crew[1] == null)
				firstFree = 1;
			else if (ship.crew[2] == null)
				firstFree = 2;

			if ((firstFree < 0) ||
					(ship.availableQuarters() <= firstFree))
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_personnel_dialog_noquarters, R.string.screen_personnel_dialog_noquarters_message, R.string.help_nofreequarters));
			}
			else
			{
				ship.crew[firstFree] = forHire;
				personnelRosterRedraw(oldTraderSkill);
			}
		}
	}
	private void personnelRosterRedraw(int oldTraderSkill) {
		drawPersonnelRoster();
		if (oldTraderSkill != ship.skill(Skill.TRADER))
			recalculateBuyPrices(curSystem());
	}
	
	/*
	 * Traveler.c
	 */
	// *************************************************************************
	// Money to pay for insurance
	// *************************************************************************
	public int insuranceMoney(  )
	{
		if (!insurance)
			return 0;
		else
			return (max( 1, (((ship.currentPriceWithoutCargo( true ) * 5) / 2000) * 
					(100 - min( noClaim, 90 )) / 100) ));
	}
	
	// *************************************************************************
	// Standard price calculation
	// *************************************************************************
	public static int standardPrice( TradeItem good, Size size, TechLevel tech, Politics government, SpecialResources resources )
	{
		int price;

		if (((good == TradeItem.NARCOTICS) && (!government.drugsOK)) ||
				((good == TradeItem.FIREARMS) &&	(!government.firearmsOK)))
			return 0 ;

		// Determine base price on techlevel of system
		price = good.priceLowTech + (tech.ordinal() * (good.priceInc));

		// If a good is highly requested, increase the price
		if (government.wanted == good)
			price = (price * 4) / 3;	

		// High trader activity decreases prices
		price = (price * (100 - (2 * government.strengthTraders.ordinal()))) / 100;

		// Large system = high production decreases prices
		price = (price * (100 - size.ordinal())) / 100;

		// Special resources price adaptation		
		if (resources != SpecialResources.NOSPECIALRESOURCES)
		{
			if (good.cheapResource != null)
				if (resources == good.cheapResource)
					price = (price * 3) / 4;
			if (good.expensiveResource != null)
				if (resources == good.expensiveResource)
					price = (price * 4) / 3;
		}

		// If a system can't use something, its selling price is zero.
		if (tech.compareTo(good.techUsage) < 0)
			return 0;

		if (price < 0)
			return 0;

		return price;
	}

	// *************************************************************************
	// What you owe the mercenaries daily
	// *************************************************************************
	public int mercenaryMoney(  )
	{
		int toPay = 0;
		for (CrewMember merc : ship.crew) {
			if (merc != null && merc != commander()) {
				toPay += merc.hirePrice();
			}
		}
		return toPay;
	}
	
	// *************************************************************************
	// Calculate wormhole tax to be paid between systems a and b
	// *************************************************************************
	public int wormholeTax( SolarSystem a, SolarSystem b )
	{
		if (wormholeExists( a, b ))
			return( ship.type.costOfFuel * 25 );

		return 0;
	}
	
	
	// *************************************************************************
	// Initializing the high score table
	// *************************************************************************
	public void initHighScores ()
	{
		for (int i=0; i<hScores.length; ++i)
		{
			hScores[i] = null;
		}
	}
	
	
	// *************************************************************************
	// Handling of endgame: highscore table
	// *************************************************************************
	// NB no longer takes endStatus as input since it's an instance field now.
	public void endOfGame()
	{
		final HighScore highScore = new HighScore( commander().name, endStatus, days, currentWorth(), difficulty );
		final int a = highScore.score;
		
		boolean scored = false;
		int i = 0;
		while (i<hScores.length)
		{
			
			int b =	hScores[i] == null? 0 : hScores[i].score;

			if (hScores[i] == null || (a > b) || (a == b && currentWorth() > hScores[i].worth) ||
				(a == b && currentWorth() == hScores[i].worth && days > hScores[i].days)
				)
			{

				if (!(gameLoaded || cheated)) {
					for (int j=hScores.length-1; j>i; --j)
					{
						hScores[j] = hScores[j-1];
					}

					hScores[i] = highScore;
				}
				
				scored = true;
				
				break;
			}

			++i;
		}

		final int scoreTextId;
		if (scored && gameLoaded)
		{
			scoreTextId = R.string.dialog_finalscore_loaded;
		}
		else if (scored && cheated)
		{
			scoreTextId = R.string.dialog_finalscore_cheated;
		}
		else if (scored)
		{
			scoreTextId = R.string.dialog_finalscore_highscore;
		}
		else
		{
			scoreTextId = R.string.dialog_finalscore_nohighscore;
		}
		
		final boolean fScored = scored;
		BaseDialog dialog = SimpleDialog.newInstance(
				R.string.dialog_finalscore_title, 
				scoreTextId,
				R.string.help_highscore,
				new OnConfirmListener() {
					@Override
					public void onConfirm() {
						if (fScored && !(gameLoaded || cheated))
							viewHighScores();

						mGameManager.setCurrentScreenType(ScreenType.TITLE);
					}
				},
				(a / 50),
				((a%50) / 5)
				);
//		dialog.setCancelable(false);.
		mGameManager.showDialogFragment(dialog);
		
		mGameManager.autosave();
		
	}
	

	public void showEndGameScreen(EndStatus endStatus)
	{
		this.endStatus = endStatus;
		mGameManager.setCurrentScreenType(ScreenType.ENDGAME);
	}
	
	public void drawEndGameScreen()
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.ENDGAME) return;
		((ImageView) screen.getView().findViewById(R.id.screen_endofgame_image)).setImageResource(endStatus.imageId);
	}
	
	
	// *************************************************************************
	// Determine prices in specified system (changed from Current System) SjG
	// *************************************************************************
	public void determinePrices( SolarSystem system )
	{
		for (TradeItem item : TradeItem.values())
		{
			buyPrice.put(item,  standardPrice( item, system.size, system.techLevel(),
					system.politics(), system.specialResources ) );

			if (buyPrice.get(item) <= 0)
			{
				buyPrice.put(item, 0);
				sellPrice.put(item, 0);
				continue;
			}

			// In case of a special status, adapt price accordingly
			if (item.doublePriceStatus != null)
				if (system.status() == item.doublePriceStatus)
					buyPrice.put(item, (buyPrice.get(item) * 3) >> 1 );

			// Randomize price a bit
			buyPrice.put(item, buyPrice.get(item) + getRandom(item.variance) - getRandom(item.variance));

			// Should never happen
			if (buyPrice.get(item) <= 0)
			{
				buyPrice.put(item, 0);
				sellPrice.put(item, 0);
				continue;
			}

			sellPrice.put(item, buyPrice.get(item));
			if (policeRecordScore < PoliceRecord.DUBIOUS.score)
			{
				// Criminals have to pay off an intermediary
				sellPrice.put(item, (sellPrice.get(item) * 90) / 100);
			}
		}

		recalculateBuyPrices(system);
	}
	
	
	// *************************************************************************
	// Execute a warp command
	// *************************************************************************
	public boolean doWarp( boolean viaSingularity )
	{

		// if Wild is aboard, make sure ship is armed!
		if (wildStatus == 1)
		{	
			if (! ship.hasWeapon(Weapon.BEAM, false))
			{
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.screen_warp_wildwontgo_title, 
						R.string.screen_warp_wildwontgo_message, 
						R.string.screen_warp_wildwontgo_pos,
						R.string.generic_cancel,
						R.string.help_wildwontgo,
						new OnConfirmListener() {
							
							@Override
							public void onConfirm() {
								mGameManager.showDialogFragment(SimpleDialog.newInstance(
										R.string.screen_warp_wildleavesship_title, 
										R.string.screen_warp_wildleavesship_message, 
										R.string.help_wildleaves,
										curSystem().name));
								wildStatus = 0;
							}
						}, 
						null,
						curSystem().name));
				return false;
			}
		}

		// Check for Large Debt
		if (debt > DEBTTOOLARGE)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_warp_debttoolarge_title, 
					R.string.screen_warp_debttoolarge_message,
					R.string.help_debttoolargefortravel));
			return false;
		}

		// Check for enough money to pay Mercenaries    
		if (mercenaryMoney() > credits)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_warp_mustpaymercenaries_title, 
					R.string.screen_warp_mustpaymercenaries_message,
					R.string.help_mustpaymercenaries));
			return false;
		}

		// Check for enough money to pay Insurance
		if (insurance)
		{
			if (insuranceMoney() + mercenaryMoney() > credits)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_warp_cantpayinsurance_title, 
						R.string.screen_warp_cantpayinsurance_message,
						R.string.help_cantpayinsurance));
				return false;
			}
		}

		// Check for enough money to pay Wormhole Tax 					
		if (insuranceMoney() + mercenaryMoney() + 
				wormholeTax( curSystem(), warpSystem ) > credits)
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.screen_warp_cantpaywormhole_title, 
					R.string.screen_warp_cantpaywormhole_message,
					R.string.help_cantpaywormhole));
			return false;
		}

		if (! viaSingularity)
		{
			credits -= wormholeTax( curSystem(), warpSystem );
			credits -= mercenaryMoney();						
			credits -= insuranceMoney();
		}

		for (int i=0; i<ship.shield.length; ++i)
		{
			if (ship.shield[i] == null)
				break;
			ship.shieldStrength[i] = ship.shield[i].power;
		}

		int distance;
		curSystem().resetCountDown();
		if (wormholeExists( curSystem(), warpSystem ) || viaSingularity)
		{
			distance = 0;
			arrivedViaWormhole = true;
		}
		else
		{
			distance = realDistance( curSystem(), warpSystem );
			ship.fuel -= min( distance, ship.getFuel() );
			arrivedViaWormhole = false;
		}

		resetNewsEvents();

		if (!viaSingularity)
		{
			// normal warp.
			payInterest();
			incDays( 1 );
			if (insurance)
				++noClaim;
		}
		else
		{
			// add the singularity news story
			addNewsEvent(NewsEvent.ARRIVALVIASINGULARITY);
		}

		clicks = 21;
		raided = false;
		inspected = false;
		litterWarning = false;
		monsterHull = (monsterHull * 105) / 100;
		if (monsterHull > ShipType.MONSTER.hullStrength)
			monsterHull = ShipType.MONSTER.hullStrength;
		if (days%3 == 0)
		{
			if (policeRecordScore > PoliceRecord.CLEAN.score)
				--policeRecordScore;
		}
		if (policeRecordScore < PoliceRecord.DUBIOUS.score)
			if (difficulty.compareTo(DifficultyLevel.NORMAL) <= 0)
				++policeRecordScore;
			else if (days%difficulty.ordinal() == 0)
				++policeRecordScore;

		possibleToGoThroughRip=true;

		travel();
		
		return true;
	}

	// *************************************************************************
	// Show the target system form
	// *************************************************************************
	public void showExecuteWarp(  )
	{
		aplScreen = false;

		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.TARGET) return;
		
////		screen.setViewVisibilityById(R.id.screen_warp_target_traders_layout, developerMode, false);
////		screen.setViewTextById(R.id.screen_warp_target_traders, warpSystem.politics().strengthTraders);
		
		((ViewGroup)screen.getView().findViewById(R.id.screen_warp_target_pagerspacer)).getChildAt(0).setVisibility(developerMode? View.VISIBLE : View.GONE);
		
		screen.setViewTextById(R.id.screen_warp_toggle, R.string.screen_warp_avgprices_button);
		
		int distance = 0;
//		
//		screen.setViewTextById(R.id.screen_warp_target_name, warpSystem.name);
//		screen.setViewTextById(R.id.screen_warp_target_tech, warpSystem.techLevel());
//		screen.setViewTextById(R.id.screen_warp_target_gov, warpSystem.politics());
//		screen.setViewTextById(R.id.screen_warp_target_size, warpSystem.size);
//					
		if (wormholeExists( curSystem(), warpSystem ))
		{
//			screen.setViewTextById(R.id.screen_warp_target_distance, R.string.screen_warp_target_distance_wormhole);
		}
		else
		{
			distance = realDistance(curSystem(), warpSystem);
//			screen.setViewTextById(R.id.screen_warp_target_distance, R.string.format_parsecs, distance);
		}
//
//		screen.setViewTextById(R.id.screen_warp_target_police, warpSystem.politics().strengthPolice);
//		screen.setViewTextById(R.id.screen_warp_target_pirates, warpSystem.politics().strengthPirates);
//		int cost = insuranceMoney() + mercenaryMoney() + (debt < 0 ? 
//				max( debt / 10, 1 ) : 0 ) + wormholeTax( curSystem(), warpSystem );
//		screen.setViewTextById(R.id.screen_warp_target_cost, R.string.format_credits, cost);
//
		if (wormholeExists( curSystem(), warpSystem ))
		{
			screen.setViewVisibilityById(R.id.screen_warp_warp, true);
			screen.setViewVisibilityById(R.id.screen_warp_toggle, true);
//			screen.setViewVisibilityById(R.id.screen_warp_target_toofar, false);
		}
		else if (distance > ship.getFuel())
		{
			screen.setViewVisibilityById(R.id.screen_warp_warp, false);
			screen.setViewVisibilityById(R.id.screen_warp_toggle, false);
//			screen.setViewVisibilityById(R.id.screen_warp_target_toofar, true);
		}
		else if (distance <= 0)
		{
			screen.setViewVisibilityById(R.id.screen_warp_warp, false);
			screen.setViewVisibilityById(R.id.screen_warp_toggle, false);
//			screen.setViewVisibilityById(R.id.screen_warp_target_toofar, false);
		}
		else
		{
			screen.setViewVisibilityById(R.id.screen_warp_warp, true);
			screen.setViewVisibilityById(R.id.screen_warp_toggle, true);
//			screen.setViewVisibilityById(R.id.screen_warp_target_toofar, false);
		}
//			
//		screen.setViewVisibilityById(R.id.screen_warp_target_cost_specific,
//				(wormholeExists( curSystem(), warpSystem ) || insurance || debt > 0 || ship.crew[1] != null));

//		WarpSystemPagerAdapter adapter = ((WarpTargetScreen)screen).getPagerAdapter();
//		if (adapter == null) return;
//		setAdapterSystems(adapter);
		
//		// Screenshot override
//		screen.setViewTextById(R.id.screen_warp_toggle, R.string.screen_warp_avgprices_button);
//		screen.setViewVisibilityById(R.id.screen_warp_warp, true);
//		screen.setViewVisibilityById(R.id.screen_warp_toggle, true);
	}
	
	public void showExecuteWarpPage(SolarSystem system, View page) {
		page.findViewById(R.id.screen_warp_target_traders).setVisibility(developerMode? View.VISIBLE : View.GONE);
		page.findViewById(R.id.screen_warp_target_traders_header).setVisibility(developerMode? View.VISIBLE : View.GONE);
		((TextView) page.findViewById(R.id.screen_warp_target_traders)).setText(system.politics().strengthTraders.toXmlString(getResources()));
		
		int distance = 0;

		((TextView) page.findViewById(R.id.screen_warp_target_name)).setText(system.name);
		((TextView) page.findViewById(R.id.screen_warp_target_tech)).setText(system.techLevel().toXmlString(getResources()));
		((TextView) page.findViewById(R.id.screen_warp_target_gov)).setText(system.politics().toXmlString(getResources()));
		((TextView) page.findViewById(R.id.screen_warp_target_size)).setText(system.size.toXmlString(getResources()));
					
		if (wormholeExists( curSystem(), system ))
		{
			((TextView) page.findViewById(R.id.screen_warp_target_distance)).setText(R.string.screen_warp_target_distance_wormhole);
		}
		else
		{
			distance = realDistance(curSystem(), system);
			((TextView) page.findViewById(R.id.screen_warp_target_distance)).setText(getResources().getString(R.string.format_parsecs, distance));
		}

		((TextView) page.findViewById(R.id.screen_warp_target_police)).setText(system.politics().strengthPolice.toXmlString(getResources()));
		((TextView) page.findViewById(R.id.screen_warp_target_pirates)).setText(system.politics().strengthPirates.toXmlString(getResources()));
		int cost = insuranceMoney() + mercenaryMoney() + (debt < 0 ? 
				max( debt / 10, 1 ) : 0 ) + wormholeTax( curSystem(), system );
		((TextView) page.findViewById(R.id.screen_warp_target_cost)).setText(getResources().getString(R.string.format_credits, cost));

		if (wormholeExists( curSystem(), system ))
		{
//			page.findViewById(R.id.screen_warp_warp).setVisibility(View.VISIBLE);
//			page.findViewById(R.id.screen_warp_toggle).setVisibility(View.VISIBLE);
			page.findViewById(R.id.screen_warp_target_toofar).setVisibility(View.INVISIBLE);
		}
		else if (distance > ship.getFuel())
		{
//			page.findViewById(R.id.screen_warp_warp).setVisibility(View.INVISIBLE);
//			page.findViewById(R.id.screen_warp_toggle).setVisibility(View.INVISIBLE);
			page.findViewById(R.id.screen_warp_target_toofar).setVisibility(View.VISIBLE);
		}
		else if (distance <= 0)
		{
//			page.findViewById(R.id.screen_warp_warp).setVisibility(View.INVISIBLE);
//			page.findViewById(R.id.screen_warp_toggle).setVisibility(View.INVISIBLE);
			page.findViewById(R.id.screen_warp_target_toofar).setVisibility(View.INVISIBLE);
		}
		else
		{
//			page.findViewById(R.id.screen_warp_warp).setVisibility(View.VISIBLE);
//			page.findViewById(R.id.screen_warp_toggle).setVisibility(View.VISIBLE);
			page.findViewById(R.id.screen_warp_target_toofar).setVisibility(View.INVISIBLE);
		}
		
		if (wormholeExists( curSystem(), system ) || insurance || debt > 0 || ship.crew[1] != null)
		{
			page.findViewById(R.id.screen_warp_target_cost_specific).setVisibility(View.VISIBLE);
		}
		else
		{
			page.findViewById(R.id.screen_warp_target_cost_specific).setVisibility(View.INVISIBLE);
		}
		
//		// Screenshot override
//		((TextView) page.findViewById(R.id.screen_warp_target_name)).setText(R.string.solarsystem_montor);
//		((TextView) page.findViewById(R.id.screen_warp_target_tech)).setText(TechLevel.MEDIEVAL.toXmlString(getResources()));
//		((TextView) page.findViewById(R.id.screen_warp_target_gov)).setText(Politics.ANARCHY.toXmlString(getResources()));
//		((TextView) page.findViewById(R.id.screen_warp_target_size)).setText(Size.MEDIUM.toXmlString(getResources()));
//		((TextView) page.findViewById(R.id.screen_warp_target_distance)).setText(getResources().getString(R.string.format_parsecs, 10));
//		((TextView) page.findViewById(R.id.screen_warp_target_police)).setText(Politics.ANARCHY.strengthPolice.toXmlString(getResources()));
//		((TextView) page.findViewById(R.id.screen_warp_target_pirates)).setText(Politics.ANARCHY.strengthPirates.toXmlString(getResources()));
//		((TextView) page.findViewById(R.id.screen_warp_target_cost)).setText(getResources().getString(R.string.format_credits, 0));
//		page.findViewById(R.id.screen_warp_target_toofar).setVisibility(View.INVISIBLE);
//		page.findViewById(R.id.screen_warp_target_cost_specific).setVisibility(View.INVISIBLE);
		
	}

	// *************************************************************************
	// Draws the list of skill points on the New Commander screen
	// *************************************************************************
	public void newCommanderDrawSkills(  )
	{
		BaseDialog dialog = mGameManager.findDialogByClass(NewGameDialog.class);

		dialog.setViewTextById(R.id.dialog_newgame_difficultypicker_value, difficulty);

		int pilot = commander().pilot();
		dialog.setViewTextById(R.id.dialog_newgame_pilotpicker_value, pilot < 10? R.string.format_spacedigit : R.string.format_number, pilot);

		int fighter = commander().fighter();
		dialog.setViewTextById(R.id.dialog_newgame_fighterpicker_value, fighter < 10? R.string.format_spacedigit : R.string.format_number, fighter);

		int trader = commander().trader();
		dialog.setViewTextById(R.id.dialog_newgame_traderpicker_value, trader < 10? R.string.format_spacedigit : R.string.format_number, trader);

		int engineer = commander().engineer();
		dialog.setViewTextById(R.id.dialog_newgame_engineerpicker_value, engineer < 10? R.string.format_spacedigit : R.string.format_number, engineer);

		int remaining = 2*MAXSKILL - commander().pilot() - commander().fighter() -
				commander().trader() - commander().engineer();
		dialog.setViewTextById(R.id.dialog_newgame_totalpoints, remaining < 10? R.string.format_spacedigit : R.string.format_number, remaining);
	}

	// *************************************************************************
	// Determine next system withing range
	// *************************************************************************
	public SolarSystem nextSystemWithinRange( SolarSystem current, boolean back) {
		
		int i;
		for (i = 0; i < solarSystem.length; i++) {
			if (solarSystem[i] == current) break;
		}
		int init = i;
		
		if (back) --i; else ++i;
		
		while (true)
		{
			if (i < 0)
				i = solarSystem.length - 1;
			else if (i >= solarSystem.length)
				i = 0;
			if (i == init)
				break;
				
			if (wormholeExists( curSystem(), solarSystem[i] ))
				return solarSystem[i];
			else if (realDistance( curSystem(), solarSystem[i] ) <= ship.getFuel() &&
				realDistance( curSystem(), solarSystem[i] ) > 0)
				return solarSystem[i];

			if (back) --i; else ++i;
		}
		
		return null;
	}
	
	public void scrollWarpSystem(boolean back) {
		SolarSystem nextWarpSystem = nextSystemWithinRange(warpSystem, back);
		warpSystem = nextWarpSystem != null? nextWarpSystem : warpSystem;
	}

	// *************************************************************************
	// Show the average prices list
	// *************************************************************************
	public void showAveragePrices(  )
	{
		aplScreen = true;

		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.AVGPRICES) return;
		screen.setViewVisibilityById(R.id.screen_warp_avgprices_lowerspacer, developerMode, false);
		screen.setViewTextById(R.id.screen_warp_avgprices_credits, R.string.format_cash, credits);

		screen.setViewTextById(R.id.screen_warp_toggle, R.string.screen_warp_target_button);

//		if (warpSystem.visited())
//			screen.setViewTextById(R.id.screen_warp_avgprices_resources, warpSystem.specialResources);
//		else
//			screen.setViewTextById(R.id.screen_warp_avgprices_resources, R.string.specialresources_unknown);
//		screen.setViewTextById(R.id.screen_warp_avgprices_name, warpSystem.name);
//
		if (priceDifferences)
		{
			screen.setViewTextById(R.id.screen_warp_avgprices_diffbutton, R.string.screen_warp_avgprices_abs);
		}
		else
		{
			screen.setViewTextById(R.id.screen_warp_avgprices_diffbutton, R.string.screen_warp_avgprices_diff);
		}

		screen.setViewTextById(R.id.screen_warp_avgprices_bays, R.string.format_bays, ship.filledCargoBays(), ship.totalCargoBays());
				
//		for (TradeItem item : TradeItem.values())
//		{
//			int price = standardPrice( item, warpSystem.size, 
//					warpSystem.techLevel(), warpSystem.politics(), 
//					(warpSystem.visited()? warpSystem.specialResources : SpecialResources.NOSPECIALRESOURCES ));
//				
//			if (price > buyPrice.get(item) && buyPrice.get(item) > 0 && curSystem().getQty(item) > 0)
//				((TextView) screen.getView().findViewById(WarpPricesScreen.LABEL_IDS.get(item))).setTypeface(Typeface.DEFAULT_BOLD);
//			else
//				((TextView) screen.getView().findViewById(WarpPricesScreen.LABEL_IDS.get(item))).setTypeface(Typeface.DEFAULT);
//
//			int formatId = (priceDifferences? R.string.format_signedcredits : R.string.format_credits);
//			int priceText = (priceDifferences? price - buyPrice.get(item) : price);;
//			if (price <= 0 || (priceDifferences && buyPrice.get(item) <= 0))
//				screen.setViewTextById(WarpPricesScreen.PRICE_IDS.get(item), R.string.screen_warp_avgprices_null);
//			else
//			{
//				screen.setViewTextById(WarpPricesScreen.PRICE_IDS.get(item), formatId, priceText);
//			}
//		}			

//		WarpSystemPagerAdapter adapter = ((WarpPricesScreen)screen).getPagerAdapter();
//		if (adapter == null) return;
//		setAdapterSystems(adapter);
		
//		// Screenshot override
//		screen.setViewTextById(R.id.screen_warp_toggle, R.string.screen_warp_target_button);
//		screen.setViewTextById(R.id.screen_warp_avgprices_diffbutton, R.string.screen_warp_avgprices_abs);
//		screen.setViewTextById(R.id.screen_warp_avgprices_bays, R.string.format_bays, 1, 30);
//		screen.setViewTextById(R.id.screen_warp_avgprices_credits, R.string.format_cash, 378304);
		
		
	}
	
	public void showAveragePricesPage(SolarSystem system, View page) {
		if (system.visited())
			((TextView) page.findViewById(R.id.screen_warp_avgprices_resources)).setText(system.specialResources.toXmlString(getResources()));
		else
			((TextView) page.findViewById(R.id.screen_warp_avgprices_resources)).setText(R.string.specialresources_unknown);
		
		((TextView) page.findViewById(R.id.screen_warp_avgprices_name)).setText(system.name);

		for (TradeItem item : TradeItem.values())
		{
			int price = standardPrice( item, system.size,
					system.techLevel(), system.politics(),
					(system.visited()? system.specialResources : SpecialResources.NOSPECIALRESOURCES ));

			if (price > buyPrice.get(item) && buyPrice.get(item) > 0 && curSystem().getQty(item) > 0) {
				((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(item))).setTypeface(Typeface.DEFAULT_BOLD);
//				TypedValue tv = new TypedValue();
//				mGameManager.getTheme().resolveAttribute(R.attr.colorAccent, tv, true);
//				ColorStateList color = getResources().getColorStateList(tv.resourceId);
//				((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(item))).setTextColor(color);
//				((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(item))).setTextColor(color);
			}
			else {
				((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(item))).setTypeface(Typeface.DEFAULT);
//				TypedValue tv = new TypedValue();
//				mGameManager.getTheme().resolveAttribute(R.attr.pressableTextColor, tv, true);
//				ColorStateList color = getResources().getColorStateList(tv.resourceId);
//				((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(item))).setTextColor(color);
//				((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(item))).setTextColor(color);
			}

			int formatId = (priceDifferences? R.string.format_signedcredits : R.string.format_credits);
			int priceText = (priceDifferences? price - buyPrice.get(item) : price);
			if (price <= 0 || (priceDifferences && buyPrice.get(item) <= 0))
				((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(item))).setText(R.string.screen_warp_avgprices_null);
			else
			{
				((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(item))).setText(getResources().getString(formatId, priceText));
			}

		}
		
//		// Screenshot override
//		((TextView) page.findViewById(R.id.screen_warp_avgprices_resources)).setText(R.string.specialresources_unknown);
//		((TextView) page.findViewById(R.id.screen_warp_avgprices_name)).setText(R.string.solarsystem_exo);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.WATER))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.FURS))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.FOOD))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.ORE))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.GAMES))).setTypeface(Typeface.DEFAULT_BOLD);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.FIREARMS))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.MEDICINE))).setTypeface(Typeface.DEFAULT_BOLD);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.MACHINERY))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.NARCOTICS))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.LABEL_IDS.get(TradeItem.ROBOTS))).setTypeface(Typeface.DEFAULT);
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.WATER))).setText(getResources().getString(R.string.format_signedcredits, -10));
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.FURS))).setText(getResources().getString(R.string.format_signedcredits, -30));
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.FOOD))).setText(getResources().getString(R.string.format_signedcredits, -13));
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.ORE))).setText(R.string.screen_warp_avgprices_null);
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.GAMES))).setText(getResources().getString(R.string.format_signedcredits, +19));
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.FIREARMS))).setText(getResources().getString(R.string.format_signedcredits, -182));
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.MEDICINE))).setText(getResources().getString(R.string.format_signedcredits, +36));
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.MACHINERY))).setText(R.string.screen_warp_avgprices_null);
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.NARCOTICS))).setText(R.string.screen_warp_avgprices_null);
//		((TextView) page.findViewById(WarpPricesScreen.PRICE_IDS.get(TradeItem.ROBOTS))).setText(R.string.screen_warp_avgprices_null);
		
	}
	
	// *************************************************************************
	// Generate an opposing ship
	// *************************************************************************
	private void generateOpponent( Opponent opp )
	{
		// NB doing this instead of original code which generates a trader and overwrites as a bottle.
		if (opp == Opponent.BOTTLE) {
			opponent = new Ship(this, ShipType.BOTTLE);
			return;
		}
		
		
		int tries = 1;
		
		if (opp == Opponent.FAMOUSCAPTAIN)
		{
			// we just fudge for the Famous Captains' Ships...;
			opponent = new Ship(this, ShipType.WASP);
			
			for (int i=0;i<opponent.shield.length;i++)
			{
				opponent.shield[i] = Shield.REFLECTIVE; 
				opponent.shieldStrength[i]= Shield.REFLECTIVE.power;
			}
			for (int i=0;i<opponent.weapon.length;i++)
			{
				opponent.weapon[i] = Weapon.MILITARY; 
			}
			opponent.gadget[0]=Gadget.TARGETINGSYSTEM;
			opponent.gadget[1]=Gadget.AUTOREPAIRSYSTEM;
			opponent.hull = ShipType.WASP.hullStrength;

			// these guys are bad-ass!
			opponent.crew[0] = new CrewMember("", 
					MAXSKILL, 
					MAXSKILL, 
					MAXSKILL, 
					MAXSKILL, 
					this);
			return;
		}

		if (opp == Opponent.MANTIS)
			tries = 1+difficulty.ordinal();

		
		// The police will try to hunt you down with better ships if you are 
		// a villain, and they will try even harder when you are considered to
		// be a psychopath (or are transporting Jonathan Wild)
		
		if (opp == Opponent.POLICE)
		{
			if (policeRecordScore < PoliceRecord.VILLAIN.score && wildStatus != 1)
				tries = 3;
			else if (policeRecordScore < PoliceRecord.PSYCHOPATH.score || wildStatus == 1)
				tries = 5;
			tries = max( 1, tries + difficulty.ordinal() - DifficultyLevel.NORMAL.ordinal() );
		}

		// Pirates become better when you get richer
		if (opp == Opponent.PIRATE)
		{
			tries = 1 + (currentWorth() / 100000);
			tries = max( 1, tries + difficulty.ordinal() - DifficultyLevel.NORMAL.ordinal() );
		}
			
		int j = 0;
		int opponentType;
		if (opp == Opponent.TRADER)
			opponentType = 0;
		else
			opponentType = 1;

		int k = (difficulty.compareTo(DifficultyLevel.NORMAL) >= 0? 
				difficulty.ordinal() - DifficultyLevel.NORMAL.ordinal() : 0);

		while (j < tries)
		{
			boolean redo = true;
			int i = 0;
			while (redo)
			{
				int d = getRandom( 100 );
				i = 0;
				int sum = ShipType.FLEA.occurrence;

				while (sum < d)
				{
					if (i >= ShipType.buyableValues().length-1)
						break;
					++i;
					sum += ShipType.values()[i].occurrence;
				}

				if (opp == Opponent.POLICE && (ShipType.values()[i].police == null || 
					warpSystem.politics().strengthPolice.ordinal() + k < ShipType.values()[i].police.ordinal() ))
					continue;

				if (opp == Opponent.PIRATE && (ShipType.values()[i].pirates == null || 
					warpSystem.politics().strengthPirates.ordinal() + k < ShipType.values()[i].pirates.ordinal() ))
					continue;

				if (opp == Opponent.TRADER && (ShipType.values()[i].traders == null || 
					warpSystem.politics().strengthTraders.ordinal() + k < ShipType.values()[i].traders.ordinal() ))
					continue;

				redo = false;
			}
		
			if (i > opponentType)
				opponentType = i;
			++j;
		}

		if (opp == Opponent.MANTIS)
			opponentType = ShipType.MANTIS.ordinal();
		else	
			tries = max( 1, (currentWorth() / 150000) + difficulty.ordinal() - DifficultyLevel.NORMAL.ordinal() );
		
		
		opponent = new Ship(this, ShipType.values()[opponentType]);

		// Determine the gadgets
		int d;
		if (opponent.type.gadgetSlots <= 0)
			d = 0;
		else if (difficulty.compareTo(DifficultyLevel.HARD) <= 0)
		{
			d = getRandom( opponent.type.gadgetSlots + 1 );
			if (d < opponent.type.gadgetSlots)
				if (tries > 4)
					++d;
				else if (tries > 2)
					d += getRandom( 2 );
		}
		else
			d = opponent.type.gadgetSlots;
		for (int i=0; i<d; ++i)
		{
			int e = 0;
			int f = 0;
			while (e < tries)
			{
				k = getRandom( 100 );
				j = 0;
				int sum = Gadget.buyableValues()[0].chance;
				while (k < sum)
				{
					if (j >= MAXGADGETTYPE - 1)
						break;
					++j;
					sum += Gadget.buyableValues()[j].chance;
				}
				if (!opponent.hasGadget(Gadget.buyableValues()[j]))
					if (j > f)
						f = j;
				++e;
			}
			opponent.gadget[i] = Gadget.buyableValues()[f];
		}
		for (int i=d; i<opponent.gadget.length; ++i)
			opponent.gadget[i] = null;

		// Determine the number of cargo bays
		int bays = opponent.totalCargoBays();

		// Fill the cargo bays
		for (TradeItem item : TradeItem.values())
			opponent.clearCargo(item);

		if (bays > 5)
		{
			int sum;
			if (difficulty.compareTo(DifficultyLevel.NORMAL) >= 0)
			{
				int m = 3 + getRandom( bays - 5 );
				sum = min( m, 15 );
			}
			else
				sum = bays;
			if (opp == Opponent.POLICE)
				sum = 0;
			if (opp == Opponent.PIRATE)
			{
				if (difficulty.compareTo(DifficultyLevel.NORMAL) < 0)
					sum = (sum * 4) / 5;
				else
					sum = sum / difficulty.ordinal();
			}
			if (sum < 1)
				sum = 1;
			
			int i = 0;
			while (i < sum)
			{
				j = getRandom( TradeItem.values().length );
				k = 1 + getRandom( 10 - j );
				if (i + k > sum)
					k = sum - i;
				opponent.addCargo(TradeItem.values()[j], k);
				i += k;
			}
		}

		// Fill the fuel tanks
		opponent.fuel = opponent.type.fuelTanks;
		
		// No tribbles on board
		opponent.tribbles = 0;
				
		// Fill the weapon slots (if possible, at least one weapon)
		if (opponent.type.weaponSlots <= 0)
			d = 0;
		else if (opponent.type.weaponSlots <= 1)
			d = 1;
		else if (difficulty.compareTo(DifficultyLevel.HARD) <= 0)
		{
			d = 1 + getRandom( opponent.type.weaponSlots );
			if (d < opponent.type.weaponSlots)
				if (tries > 4 && difficulty.compareTo(DifficultyLevel.HARD) >= 0)
					++d;
				else if (tries > 3 || difficulty.compareTo(DifficultyLevel.HARD) >= 0)
					d += getRandom( 2 );
		}
		else
			d = opponent.type.weaponSlots;
		for (int i=0; i<d; ++i)
		{
			int e = 0;
			int f = 0;
			while (e < tries)
			{
				k = getRandom( 100 );
				j = 0;
				int sum = Weapon.buyableValues()[0].chance;
				while (k < sum)
				{
					if (j >= MAXWEAPONTYPE - 1)
						break;
					++j;
					sum += Weapon.buyableValues()[j].chance;
				}
				if (j > f)
					f = j;
				++e;
			}
			opponent.weapon[i] = Weapon.buyableValues()[f];
		}
		for (int i=d; i<opponent.gadget.length; ++i)
			opponent.gadget[i] = null;

		// Fill the shield slots
		if (opponent.type.shieldSlots <= 0)
			d = 0;
		else if (difficulty.compareTo(DifficultyLevel.HARD) <= 0)
		{
			d = getRandom( opponent.type.shieldSlots + 1 );
			if (d < opponent.type.shieldSlots)
				if (tries > 3)
					++d;
				else if (tries > 1)
					d += getRandom( 2 );
		}
		else
			d = opponent.type.shieldSlots;
		for (int i=0; i<d; ++i)
		{
			int e = 0;
			int f = 0;
			
			while (e < tries)
			{
				k = getRandom( 100 );
				j = 0;
				int sum = Shield.buyableValues()[0].chance;
				while (k < sum)
				{
					if (j >= MAXSHIELDTYPE - 1)
						break;
					++j;
					sum += Shield.buyableValues()[j].chance;
				}
				if (j > f)
					f = j;
				++e;
			}
			opponent.shield[i] = Shield.buyableValues()[f];

			j = 0;
			k = 0;
			while (j < 5)
			{
				e = 1 + getRandom( opponent.shield[i].power );
				if (e > k)
					k = e;
				++j;
			}
			opponent.shieldStrength[i] = k;			
		}
		for (int i=d; i<opponent.shield.length; ++i)
		{
			opponent.shield[i] = null;
			opponent.shieldStrength[i] = 0;
		}

		// Set hull strength
		int i = 0;
		k = 0;
		// If there are shields, the hull will probably be stronger
		if (opponent.shield[0] != null && getRandom( 10 ) <= 7)
			opponent.hull = opponent.type.hullStrength;
		else
		{
			while (i < 5)
			{
				d = 1 + getRandom( opponent.type.hullStrength );
				if (d > k)
					k = d;
				++i;
			}
			opponent.hull = k;			
		}

		if (opp == Opponent.MANTIS || opp == Opponent.FAMOUSCAPTAIN)
			opponent.hull = opponent.type.hullStrength;


		// Set the crew. These may be duplicates, or even equal to someone aboard
		// the commander's ship, but who cares, it's just for the skills anyway.
		opponent.crew[0] = new CrewMember("",
				1 + getRandom(MAXSKILL),
				1 + getRandom(MAXSKILL),
				1 + getRandom(MAXSKILL),
				(warpSystem == solarSystem[kravat] && wildStatus == 1 && (getRandom(10)<difficulty.ordinal() + 1))?
						MAXSKILL : 1 + getRandom(MAXSKILL),
				this);

		if (difficulty.compareTo(DifficultyLevel.HARD) <= 0)
		{
			d = 1 + getRandom( opponent.type.crewQuarters );
			if (difficulty.compareTo(DifficultyLevel.HARD) >= 0 && d < opponent.type.crewQuarters)
				++d;
		}
		else
			d = opponent.type.crewQuarters;
		for (i=1; i<d; ++i)
			opponent.crew[i] = getRandom( mercenary );
		for (i=d; i<opponent.crew.length; ++i)
			opponent.crew[i] = null;
	}

	// *************************************************************************
	// Money available to spend
	// *************************************************************************
	public int toSpend( )
	{
		if (!reserveMoney)
			return credits;
		return max( 0,  credits - mercenaryMoney() - insuranceMoney()
//				- wormholeTax(curSystem(), warpSystem)	// NB Should this be here? (not in original)
				);
	}
	
	// *************************************************************************
	// View high scores
	// *************************************************************************
	public void viewHighScores(  ) {
		mGameManager.showDialogFragment(HighScoresDialog.newInstance());
	}
	
	public void showHighScores(  )
	{
		BaseDialog dialog = mGameManager.findDialogByClass(HighScoresDialog.class);
		
		for (int i = 0; i < hScores.length; i++)
		{
			if (hScores[i] == null)
			{
				dialog.setViewTextById(HighScoresDialog.NAME_IDS.get(i), R.string.dialog_highscores_empty);
				dialog.setViewTextById(HighScoresDialog.PCT_IDS.get(i), "");
				dialog.setViewTextById(HighScoresDialog.DESC_IDS.get(i), "");
				continue;
			}
			
			dialog.setViewTextById(HighScoresDialog.NAME_IDS.get(i), hScores[i].name);
			
			int score = hScores[i].score;
			dialog.setViewTextById(HighScoresDialog.PCT_IDS.get(i), R.string.dialog_highscores_percent, (score / 50), ((score%50) / 5));
			
			String description = getResources().getQuantityString(R.plurals.dialog_highscores_description, hScores[i].days, 
					hScores[i].status.toXmlString(getResources()), hScores[i].days, hScores[i].worth, hScores[i].difficulty.toXmlString(getResources()).toLowerCase(Locale.getDefault()));
			dialog.setViewTextById(HighScoresDialog.DESC_IDS.get(i), description);
		}
		
//		// Screenshot override
//		dialog.setViewTextById(HighScoresDialog.NAME_IDS.get(0), R.string.name_commander);
//		dialog.setViewTextById(HighScoresDialog.PCT_IDS.get(0), R.string.dialog_highscores_percent, 46, 9);
//		dialog.setViewTextById(HighScoresDialog.DESC_IDS.get(0), getResources().getQuantityString(R.plurals.dialog_highscores_description, 17,
//				EndStatus.KILLED.toXmlString(getResources()), 17, 435468, DifficultyLevel.NORMAL.toXmlString(getResources()).toLowerCase(Locale.getDefault())));
//		dialog.setViewTextById(HighScoresDialog.NAME_IDS.get(1), R.string.name_commander);
//		dialog.setViewTextById(HighScoresDialog.PCT_IDS.get(1), R.string.dialog_highscores_percent, 35, 5);
//		dialog.setViewTextById(HighScoresDialog.DESC_IDS.get(1), getResources().getQuantityString(R.plurals.dialog_highscores_description, 39,
//				EndStatus.RETIRED.toXmlString(getResources()), 39, 312055, DifficultyLevel.NORMAL.toXmlString(getResources()).toLowerCase(Locale.getDefault())));
//		dialog.setViewTextById(HighScoresDialog.NAME_IDS.get(2), R.string.name_commander);
//		dialog.setViewTextById(HighScoresDialog.PCT_IDS.get(2), R.string.dialog_highscores_percent, 1, 0);
//		dialog.setViewTextById(HighScoresDialog.DESC_IDS.get(2), getResources().getQuantityString(R.plurals.dialog_highscores_description, 1,
//				EndStatus.RETIRED.toXmlString(getResources()), 1, 9974, DifficultyLevel.NORMAL.toXmlString(getResources()).toLowerCase(Locale.getDefault())));
		
	}	

	
	// *************************************************************************
	// Start a new game
	// *************************************************************************
	private void startNewGame()
	{
		
		// Initialize Galaxy
		String[] systemNames = getResources().getStringArray(R.array.solar_system_name);
		for (int i = 0; i < solarSystem.length; )
		{
			int x, y;
			if (i < wormhole.length)
			{
				// Place the first system somewhere in the centre
				x = (((CLOSEDISTANCE>>1) - 
						getRandom( CLOSEDISTANCE )) + ((GALAXYWIDTH * (1 + 2*(i%3)))/6));		
				y = (((CLOSEDISTANCE>>1) - 
						getRandom( CLOSEDISTANCE )) + ((GALAXYHEIGHT * (i < 3 ? 1 : 3))/4));		
			}
			else
			{
				x = (1 + getRandom( GALAXYWIDTH - 2 ));		
				y = (1 + getRandom( GALAXYHEIGHT - 2 ));		
			}

			boolean closeFound = false;
			boolean redo = false;
			if (i >= wormhole.length)
			{
				for (int j=0; j<i; ++j)
				{
					//  Minimum distance between any two systems not to be accepted
					if (sqr(solarSystem[j].x() - x) + sqr(solarSystem[j].y() - y) <= sqr( MINDISTANCE + 1 )) 
					{
						redo = true;
						break;
					}

					// There should be at least one system which is closeby enough
					if (sqr(solarSystem[j].x() - x) + sqr(solarSystem[j].y() - y) < sqr( CLOSEDISTANCE )) 
						closeFound = true;
				}
			}
			if (redo)
				continue;
			if ((i >= wormhole.length) && !closeFound)
				continue;

			TechLevel techLevel = getRandom(TechLevel.values());
			Politics politics = getRandom(Politics.values());
			if (politics.minTechLevel.compareTo(techLevel) > 0)
				continue;
			if (politics.maxTechLevel.compareTo(techLevel) < 0)
				continue;

			SpecialResources specialResources;
			if (getRandom( 5 ) >= 3)
				specialResources = getRandom(SpecialResources.values(), 1);
			else
				specialResources = SpecialResources.NOSPECIALRESOURCES;

			Size size = getRandom(Size.values());

			Status status;
			if (getRandom( 100 ) < 15)
				status = getRandom(Status.values(), 1);
			else			
				status = Status.UNEVENTFUL;

			String name = systemNames[i];
			
			solarSystem[i] = new SolarSystem(this, name, techLevel, politics, status, x, y, specialResources, size);
			if (i < wormhole.length)
			{		
				wormhole[i] = solarSystem[i];
			}

			++i;
		}
		
		// Randomize the system locations a bit more, otherwise the systems with the first
		// names in the alphabet are all in the centre
		for (int i=0; i<solarSystem.length; ++i)
		{
			int d = 0;
			while (d < wormhole.length)
			{
				if (wormhole[d] == solarSystem[i])
					break;
				++d;
			}
			int j = getRandom( solarSystem.length );
			if (wormholeExists( solarSystem[j], null ))
				continue;
			solarSystem[i].swapLocation(solarSystem[j]);
			if (d < wormhole.length)
				wormhole[d] = solarSystem[j];
		}

		// Randomize wormhole order
		for (int i=0; i<wormhole.length; ++i)
		{
			int j = getRandom( wormhole.length );
			SolarSystem s = wormhole[i];
			wormhole[i] = wormhole[j];
			wormhole[j] = s;
		}
		

		if (randomQuestSystems) {
			// This randomizes quest systems which were static in the original.
			acamar = -1;
			baratas = -1;
			daled = -1;
			devidia = -1;
			gemulon = -1;
			japori = -1;
			kravat = -1;
			melina = -1;
			nix = -1;
			og = -1;
			regulas = -1;
			sol = -1;
			utopia = -1;
			zalkon = -1;

			// Some systems still don't change.
			for (int i = 0; i < solarSystem.length; i++) {
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_og))) {
					og = i;
				}
				else if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_sol))) {
					sol = i;
				}
				else if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_utopia))) {
					utopia = i;
				}
			}

			// For the rest, we randomize. HashSet indices will keep track of planets we've already selected.
			HashSet<Integer> indices = new HashSet<>();
			indices.add(og);
			indices.add(sol);
			indices.add(utopia);

			while (acamar < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					acamar = index;
					indices.add(acamar);
				}
			}
			while (baratas < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					baratas = index;
					indices.add(baratas);
				}
			}
			while (daled < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					daled = index;
					indices.add(daled);
				}
			}
			while (devidia < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					devidia = index;
					indices.add(devidia);
				}
			}
			while (gemulon < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					gemulon = index;
					indices.add(gemulon);
				}
			}
			while (japori < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					japori = index;
					indices.add(japori);
				}
			}
			while (kravat < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					kravat = index;
					indices.add(kravat);
				}
			}
			while (melina < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					melina = index;
					indices.add(melina);
				}
			}
			while (nix < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					nix = index;
					indices.add(nix);
				}
			}
			while (regulas < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					regulas = index;
					indices.add(regulas);
				}
			}
			while (zalkon < 0) {
				int index = getRandom(solarSystem.length);
				if (!indices.contains(index)) {
					zalkon = index;
					indices.add(zalkon);
				}
			}

			Log.d("Quest Systems", "Classic Acamar is now "+solarSystem[acamar].name);
			Log.d("Quest Systems", "Classic Baratas is now "+solarSystem[baratas].name);
			Log.d("Quest Systems", "Classic Daled is now "+solarSystem[daled].name);
			Log.d("Quest Systems", "Classic Devidia is now "+solarSystem[devidia].name);
			Log.d("Quest Systems", "Classic Gemulon is now "+solarSystem[gemulon].name);
			Log.d("Quest Systems", "Classic Japori is now "+solarSystem[japori].name);
			Log.d("Quest Systems", "Classic Kravat is now "+solarSystem[kravat].name);
			Log.d("Quest Systems", "Classic Melina is now "+solarSystem[melina].name);
			Log.d("Quest Systems", "Classic Nix is now "+solarSystem[nix].name);
			Log.d("Quest Systems", "Classic Regulas is now "+solarSystem[regulas].name);
			Log.d("Quest Systems", "Classic Zalkon is now "+solarSystem[zalkon].name);
		} else {
			// This sets quest systems as in the original palm version
			for (int i = 0; i < solarSystem.length; i++) {
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_acamar))) {
					acamar = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_baratas))) {
					baratas = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_daled))) {
					daled = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_devidia))) {
					devidia = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_gemulon))) {
					gemulon = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_japori))) {
					japori = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_kravat))) {
					kravat = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_melina))) {
					melina = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_nix))) {
					nix = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_og))) {
					og = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_regulas))) {
					regulas = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_sol))) {
					sol = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_utopia))) {
					utopia = i;
				}
				if (solarSystem[i].name.equals(getResources().getString(R.string.solarsystem_zalkon))) {
					zalkon = i;
				}
			}
		}

		// Initialize mercenary list
//		String prevName;
//		if (mercenary[0] == null || mercenary[0].name == null || mercenary[0].name.length() <= 0) {
//			prevName = getResources().getString(R.string.name_commander);
//		} else {
//			prevName = mercenary[0].name;
//		}
//		mercenary[0] = new CrewMember(prevName, 1, 1, 1, 1, this);

		for (int i = 1; i < mercenary.length; )
		{

			mercenary[i] = new CrewMember(getResources().getStringArray(R.array.mercenary_name)[i], 
					randomSkill(),
					randomSkill(),
					randomSkill(),
					randomSkill(),
					this);
			
			mercenary[i].setSystem(getRandom(solarSystem));

			boolean redo = false;
			for (int j=1; j<i; ++j)
			{
				// Not more than one mercenary per system
				if (mercenary[j].curSystem() == mercenary[i].curSystem())
				{
					redo = true;
					break;
				}
			}
			// can't have another mercenary on Kravat, since Zeethibal could be there
			if (mercenary[i].curSystem() == solarSystem[kravat])
				redo = true;
			if (redo)
				continue;

			++i;
		}
		
		// special individuals: Zeethibal, Jonathan Wild's Nephew
		mercenary[mercenary.length-1].setSystem(null);

		// Place special events
		solarSystem[acamar].setSpecial(SpecialEvent.MONSTERKILLED);
		solarSystem[baratas].setSpecial(SpecialEvent.FLYBARATAS);
		solarSystem[melina].setSpecial(SpecialEvent.FLYMELINA);
		solarSystem[regulas].setSpecial(SpecialEvent.FLYREGULAS);
		solarSystem[zalkon].setSpecial(SpecialEvent.DRAGONFLYDESTROYED);
		solarSystem[japori].setSpecial(SpecialEvent.MEDICINEDELIVERY);
		solarSystem[utopia].setSpecial(SpecialEvent.MOONBOUGHT);
		solarSystem[devidia].setSpecial(SpecialEvent.JAREKGETSOUT);
		solarSystem[kravat].setSpecial(SpecialEvent.WILDGETSOUT);
						
		// Assign a wormhole location endpoint for the Scarab.
		// It's possible that ALL wormhole destinations are already
		// taken. In that case, we don't offer the Scarab quest.
		// NB added some braces in here to limit overused variable scope since I'm changing ints to objects in some cases.
		boolean freeWormhole = false;
		{
			int k = 0;
			int wh = getRandom( wormhole.length );
			while (wormhole[wh].special() != null &&
					wh != gemulon && wh != daled && wh != nix && k < 20)
			{
				wh = getRandom( wormhole.length );
				k++;
			}
			if (k < 20)
			{
				freeWormhole = true;
				wormhole[wh].setSpecial(SpecialEvent.SCARABDESTROYED);
				Log.d("startNewGame()", "Setting special event "+getResources().getString(SpecialEvent.SCARABDESTROYED.titleId)+" at "+solarSystem[wh].name);
			}
		}
		{
			int d = 999;
			int k = -1;
			for (int i = 0; i < solarSystem.length; i++)
			{
				SolarSystem system = solarSystem[i];
				int j = realDistance( solarSystem[nix], system );
				if (j >= 70 && j < d && system.special() == null &&
						d != gemulon && d!= daled)
				{
					k = i;
					d = j;
				}
			}
			if (k >= 0)
			{
				solarSystem[k].setSpecial(SpecialEvent.GETREACTOR);
				solarSystem[nix].setSpecial(SpecialEvent.REACTORDELIVERED);
				Log.d("startNewGame()", "Setting special event "+getResources().getString(SpecialEvent.GETREACTOR.titleId)+" at "+solarSystem[k].name);
			}
		}
		boolean noArtifact = false;
		{
			int i = 0;
			while (i < solarSystem.length)
			{
				int d = 1 + (getRandom( solarSystem.length - 1 ));
				if (solarSystem[d].special() == null && solarSystem[d].techLevel().ordinal() >= TechLevel.values().length-1 &&
						d != gemulon && d != daled)
				{
					solarSystem[d].setSpecial(SpecialEvent.ARTIFACTDELIVERY);
					Log.d("startNewGame()", "Setting special event "+getResources().getString(SpecialEvent.ARTIFACTDELIVERY.titleId)+" at "+solarSystem[d].name);
					break;
				}
				++i;
			}
			if (i >= solarSystem.length)
				noArtifact = true;
		}
		{
			int d = 999;
			int k = -1;
			for (int i=0; i<solarSystem.length; ++i)
			{
				int j = realDistance( solarSystem[gemulon], solarSystem[i] );
				if (j >= 70 && j < d && solarSystem[i].special() == null &&
						k != daled && k!= gemulon)
				{
					k = i;
					d = j;
				}
			}
			if (k >= 0)
			{
				solarSystem[k].setSpecial(SpecialEvent.ALIENINVASION);
				solarSystem[gemulon].setSpecial(SpecialEvent.GEMULONRESCUED);
				Log.d("startNewGame()", "Setting special event "+getResources().getString(SpecialEvent.ALIENINVASION.titleId)+" at "+solarSystem[k].name);
			}
		}
		{
			int d = 999;
			int k = -1;
			for (int i=0; i<solarSystem.length; ++i)
			{
				int j = realDistance( solarSystem[daled], solarSystem[i] );
				if (j >= 70 && j < d && solarSystem[i].special() == null)
				{
					k = i;
					d = j;
				}
			}
			if (k >= 0)
			{
				solarSystem[k].setSpecial(SpecialEvent.EXPERIMENT);
				solarSystem[daled].setSpecial(SpecialEvent.EXPERIMENTSTOPPED);
				Log.d("startNewGame()", "Setting special event "+getResources().getString(SpecialEvent.EXPERIMENT.titleId)+" at "+solarSystem[k].name);
			}
		}
		// NB Unlike original, we're looping though everything here. This is ok because we're only doing stuff if occurrence > 0.
		for (SpecialEvent event : SpecialEvent.values())
		{			
			for (int j=0; j<event.occurrence; ++j)
			{
				if (event == SpecialEvent.ALIENARTIFACT && noArtifact) continue;
				boolean redo = true;
				while (redo)
				{
					int d = 1 + getRandom( solarSystem.length - 1 );
					if (solarSystem[d].special() == null) 
					{
						if (freeWormhole || event != SpecialEvent.SCARAB) {
							solarSystem[d].setSpecial(event);
							Log.d("startNewGame()", "Setting special event "+getResources().getString(event.titleId)+" at "+solarSystem[d].name);
						}
						redo = false;
					}
				}
			}
		}

		// Initialize Commander
		for (int i=0; i<200; ++i)
		{
			commander().setSystem(getRandom(solarSystem));
			if (curSystem().special() != null)
				continue;

			// Seek at least an agricultural planet as startplanet (but not too hi-tech)
			if ((i < 100) && ((curSystem().techLevel().ordinal() <= 0) ||
					(curSystem().techLevel().ordinal() >= 6)))
				continue;

			// Make sure at least three other systems can be reached
			int d = 0;
			for (int j=0; j<solarSystem.length; ++j)
			{
				if (solarSystem[j] == curSystem())
					continue;
				if (realDistance( solarSystem[j], curSystem() ) <= ShipType.values()[1].fuelTanks )
				{
					++d;
					if (d >= 3)
						break;
				}
			}
			if (d < 3)
				continue;

			break;
		}

		credits = 1000;
		debt = 0;
		days = 0;
		warpSystem = curSystem();
		policeKills = 0; 
		traderKills = 0; 
		pirateKills = 0; 
		policeRecordScore = 0;
		reputationScore = 0;
		monsterStatus = 0;
		dragonflyStatus = 0;
		scarabStatus = 0;
		japoriDiseaseStatus = 0;
		moonBought = false;
		monsterHull = ShipType.MONSTER.hullStrength;
		escapePod = false;
		insurance = false;
		remindLoans = true;
		noClaim = 0;
		artifactOnBoard = false;
		for (TradeItem item : TradeItem.values()) {
			buyingPrice.put(item, 0);
		}
		for (ShipType type : ShipType.buyableValues()) {
			shipPrice.put(type, 0);
		}
		tribbleMessage = false;
		jarekStatus = 0;
		invasionStatus = 0;
		experimentStatus = 0;
		fabricRipProbability = 0;
		possibleToGoThroughRip = false;
		arrivedViaWormhole = false;
		veryRareEncounter = 0;
		resetNewsEvents();
		wildStatus = 0;
		reactorStatus = 0;
		trackedSystem = null;
		showTrackedRange = true;
		justLootedMarie = false;
		chanceOfVeryRareEncounter = CHANCEOFVERYRAREENCOUNTER;
		alreadyPaidForNewspaper = false;
		canSuperWarp = false;
		gameLoaded = false;
		cheated = false;
		
		endStatus = null;

		// Initialize Ship
		ship = new Ship(this, ShipType.GNAT);
		ship.crew[0] = commander();
		ship.weapon[0] = Weapon.PULSE;

	}

	// *************************************************************************
	// Increase Days (used in Encounter Module as well)
	// *************************************************************************
	private void incDays( final int amount )
	{
		
		// Moved this check to front, so that if recursive call happens we don't increment days variable twice.
		if (experimentStatus > 0 && experimentStatus < 12)
		{
			experimentStatus += amount;
			if (experimentStatus > 11)
			{
				fabricRipProbability = FABRICRIPINITIALPROBABILITY;
				solarSystem[daled].setSpecial(SpecialEvent.EXPERIMENTNOTSTOPPED);
				// in case Amount > 1
				experimentStatus = 12;
				

				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_experimentperformed_title, 
						R.string.dialog_experimentperformed_message,
						-1, // NB original has no help text here.
						new OnConfirmListener() {
							
							@Override
							public void onConfirm() {
								addNewsEvent(NewsEvent.EXPERIMENTPERFORMED);			
								incDays(amount);
							}
						}));

				return;
			}
		}
		else if (experimentStatus == 12 && fabricRipProbability > 0)
		{
			fabricRipProbability -= amount;
		}
		
		
		days += amount;

		if (invasionStatus > 0 && invasionStatus < 8)
		{
			invasionStatus += amount;
			if (invasionStatus >= 8)
			{
				solarSystem[gemulon].invade();
			}
		}


		if (reactorStatus > 0 && reactorStatus < 21)
		{
			reactorStatus += amount;
			if (reactorStatus > 20)
				reactorStatus = 20;

		}
	}
	
	// *************************************************************************
	// Travelling to the target system
	// *************************************************************************
	private void travel(  ) 
	{
		clearButtonAction();
	
		boolean pirate = false;
		boolean trader = false;
		boolean police = false;
		boolean mantis = false;
		boolean haveMilitaryLaser = ship.hasWeapon(Weapon.MILITARY, true);
		boolean haveReflectiveShield = ship.hasShield(Shield.REFLECTIVE);
			
		// if timespace is ripped, we may switch the warp system here.
		if (possibleToGoThroughRip &&
		    experimentStatus == 12 && fabricRipProbability > 0 &&
		    (getRandom(100) < fabricRipProbability || fabricRipProbability == 25)
		    )
		{
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					R.string.dialog_fabricrip_title, 
					R.string.dialog_fabricrip_message,
					R.string.help_impound, // NB yes this is apparently correct.
					new OnConfirmListener() {
						
						@Override
						public void onConfirm() {
							possibleToGoThroughRip = false;
							travel();
						}
					}));
			warpSystem = getRandom(solarSystem);
			return;
		}
			
		possibleToGoThroughRip=false;
		
		int startClicks = clicks;
		--clicks;
		
		while (clicks > 0)
		{
			// Engineer may do some repairs
			int repairs = getRandom( ship.skill(Skill.ENGINEER) ) >> 1;
			ship.hull += repairs;
			if (ship.hull > ship.getHullStrength())
			{
				repairs = ship.hull - ship.getHullStrength();
				ship.hull = ship.getHullStrength();
			}
			else
				repairs = 0;
			
			// Shields are easier to repair
			repairs = 2 * repairs;
			for (int i=0; i<ship.shield.length; ++i)
			{
				if (ship.shield[i] == null)
					break;
				ship.shieldStrength[i] += repairs;
				if (ship.shieldStrength[i] > ship.shield[i].power)
				{
					repairs = ship.shieldStrength[i] - ship.shield[i].power;
					ship.shieldStrength[i] = ship.shield[i].power;
				}
				else
					repairs = 0;
			}
		
			// Encounter with space monster
			if ((clicks == 1) && (warpSystem == solarSystem[acamar]) && (monsterStatus == 1))
			{
				opponent = monster.copy();
				opponent.hull = monsterHull;
				opponent.crew[0] = new CrewMember("", 
						8 + difficulty.ordinal(),
						8 + difficulty.ordinal(),
						1,
						1 + difficulty.ordinal(),
						this);
				if (ship.cloaked(opponent))
					encounterType = Encounter.Monster.IGNORE;
				else
					encounterType = Encounter.Monster.ATTACK;
				mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
				return;
			}
			
			// Encounter with the stolen Scarab
			if (clicks == 20 && warpSystem.special() == SpecialEvent.SCARABDESTROYED &&
				scarabStatus == 1 && arrivedViaWormhole)
			{
				opponent = scarab.copy();
				opponent.crew[0] = new CrewMember("", 
						5 + difficulty.ordinal(),
						6 + difficulty.ordinal(),
						1,
						6 + difficulty.ordinal(),
						this);
				if (ship.cloaked(opponent))
					encounterType = Encounter.Scarab.IGNORE;
				else
					encounterType = Encounter.Scarab.ATTACK;
				mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
				return;
			} 
			// Encounter with stolen Dragonfly
			if ((clicks == 1) && (warpSystem == solarSystem[zalkon]) && (dragonflyStatus == 4))
			{
				opponent = dragonfly.copy();
				opponent.crew[0] = new CrewMember("", 
						4 + difficulty.ordinal(),
						6 + difficulty.ordinal(),
						1,
						6 + difficulty.ordinal(),
						this);
				if (ship.cloaked(opponent))
					encounterType = Encounter.Dragonfly.IGNORE;
				else
					encounterType = Encounter.Dragonfly.ATTACK;
				mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
				return;
			}
			
			if (warpSystem == solarSystem[gemulon] && invasionStatus > 7)
			{
				if (getRandom( 10 ) > 4)
					mantis = true;
			}
			else
			{
				// Check if it is time for an encounter
				int encounterTest = getRandom( 44 - (2 * difficulty.ordinal()) );
				
				// encounters are half as likely if you're in a flea.
				if (ship.type == ShipType.FLEA)
					encounterTest *= 2;
				
				if (encounterTest < warpSystem.politics().strengthPirates.ordinal() &&
					!raided) // When you are already raided, other pirates have little to gain
					pirate = true;
				else if (encounterTest < 
						warpSystem.politics().strengthPirates.ordinal() + 
						warpSystem.strengthPolice(policeRecordScore))
					// StrengthPolice adapts itself to your criminal record: you'll
					// encounter more police if you are a hardened criminal.
					police = true;
				else if (encounterTest < 
						warpSystem.politics().strengthPirates.ordinal() + 
						warpSystem.strengthPolice(policeRecordScore) +
						warpSystem.politics().strengthTraders.ordinal())
					trader = true;
				else if (wildStatus == 1 && warpSystem == solarSystem[kravat])
				{
					// if you're coming in to Kravat & you have Wild onboard, there'll be swarms o' cops.
					int rareEncounter = getRandom(100);
					if (difficulty.compareTo(DifficultyLevel.EASY) <= 0 && rareEncounter < 25)
					{
						police = true;
					}
					else if (difficulty == DifficultyLevel.NORMAL && rareEncounter < 33)
					{
						police = true;
					}
					else if (difficulty.compareTo(DifficultyLevel.NORMAL) > 0 && rareEncounter < 50)
					{
						police = true;
					}
				}	
				if (!(trader || police || pirate))
					if (artifactOnBoard && getRandom( 20 ) <= 3)
						mantis = true;
			}
				
			// Encounter with police
			if (police)
			{
				generateOpponent( Opponent.POLICE );
				encounterType = Encounter.Police.IGNORE;

				// If you are cloaked, they don't see you
				if (ship.cloaked(opponent))
					encounterType = Encounter.Police.IGNORE;
				else if (policeRecordScore < PoliceRecord.DUBIOUS.score)
				{
					// If you're a criminal, the police will tend to attack
					if (opponent.totalWeapons(null, null) <= 0)
					{
						if (opponent.cloaked(ship))
							encounterType = Encounter.Police.IGNORE;
						else
							encounterType = Encounter.Police.FLEE;
					}
					if (reputationScore < Reputation.AVERAGE.score)
						encounterType = Encounter.Police.ATTACK;
					else if (getRandom( Reputation.ELITE.score ) > (reputationScore / (1 + opponent.type.ordinal())))
						encounterType = Encounter.Police.ATTACK;
					else if (opponent.cloaked(ship))
						encounterType = Encounter.Police.IGNORE;
					else
						encounterType = Encounter.Police.FLEE;
				}
				else if (policeRecordScore >= PoliceRecord.DUBIOUS.score && 
						policeRecordScore < PoliceRecord.CLEAN.score && !inspected)
				{
					// If you're reputation is dubious, the police will inspect you
					encounterType = Encounter.Police.INSPECTION;
					inspected = true;
				}
				else if (policeRecordScore < PoliceRecord.LAWFUL.score)
				{
					// If your record is clean, the police will inspect you with a chance of 10% on Normal
					if (getRandom( 12 - difficulty.ordinal() ) < 1 && !inspected)
					{
						encounterType = Encounter.Police.INSPECTION;
						inspected = true;
					}
				}
				else
				{
					// If your record indicates you are a lawful trader, the chance on inspection drops to 2.5%
					if (getRandom( 40 ) == 1 && !inspected)
					{
						encounterType = Encounter.Police.INSPECTION;
						inspected = true;
					}
				}

				// if you're suddenly stuck in a lousy ship, Police won't flee even if you
				// have a fearsome reputation.
				if (encounterType == Encounter.Police.FLEE && opponent.type.compareTo(ship.type) > 0)
				{
					if (policeRecordScore < PoliceRecord.DUBIOUS.score)
					{
						encounterType = Encounter.Police.ATTACK;
					}
					else
					{
						encounterType = Encounter.Police.INSPECTION;
					}
				}
				
				// If they ignore you and you can't see them, the encounter doesn't take place
				if (encounterType == Encounter.Police.IGNORE && opponent.cloaked(ship))
					{
					--clicks;
					continue;
				}


				// If you automatically don't want to confront someone who ignores you, the
				// encounter may not take place
				if (alwaysIgnorePolice && (encounterType == Encounter.Police.IGNORE || 
						encounterType == Encounter.Police.FLEE))
				{
					--clicks;
					continue;
				}
				
				mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
				return;
			}
			// Encounter with pirate
			else if (pirate || mantis)
			{
				if (mantis)
					generateOpponent( Opponent.MANTIS );
				else
					generateOpponent( Opponent.PIRATE );

				// If you have a cloak, they don't see you
				if (ship.cloaked(opponent))
					encounterType = Encounter.Pirate.IGNORE;

				// Pirates will mostly attack, but they are cowardly: if your rep is too high, they tend to flee
				else if (opponent.type.ordinal() >= 7 ||
						getRandom( Reputation.ELITE.score ) > (reputationScore * 4) / (1 + opponent.type.ordinal()))
					encounterType = Encounter.Pirate.ATTACK;
				else
					encounterType = Encounter.Pirate.FLEE;

				if (mantis)
					encounterType = Encounter.Pirate.ATTACK;

				// if Pirates are in a better ship, they won't flee, even if you have a very scary
				// reputation.
				if (encounterType == Encounter.Pirate.FLEE && opponent.type.compareTo(ship.type) > 0)
				{
					encounterType = Encounter.Pirate.ATTACK;
				}
				
				
				// If they ignore you or flee and you can't see them, the encounter doesn't take place
				if ((encounterType == Encounter.Pirate.IGNORE || encounterType == Encounter.Pirate.FLEE) && 
						opponent.cloaked(ship))
				{
					--clicks;
					continue;
				}
				if (alwaysIgnorePirates && (encounterType == Encounter.Pirate.IGNORE ||
						encounterType == Encounter.Pirate.FLEE))
				{
					--clicks;
					continue;
				}
				mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
				return;
			}
			// Encounter with trader
			else if (trader)
			{	
				generateOpponent( Opponent.TRADER );
				encounterType = Encounter.Trader.IGNORE;
				// If you are cloaked, they don't see you
				if (ship.cloaked(opponent))
					encounterType = Encounter.Trader.IGNORE;
				// If you're a criminal, traders tend to flee if you've got at least some reputation
				else if (policeRecordScore <= PoliceRecord.CRIMINAL.score)
				{
					if (getRandom( Reputation.ELITE.score ) <= (reputationScore * 10) / (1 + opponent.type.ordinal()))
					{
						if (opponent.cloaked(ship))
							encounterType = Encounter.Trader.IGNORE;
						else
							encounterType = Encounter.Trader.FLEE;
					}
				}
				
				// Will there be trade in orbit?
				if (encounterType == Encounter.Trader.IGNORE && (getRandom(1000) < chanceOfTradeInOrbit))
				{
					if (ship.filledCargoBays() < ship.totalCargoBays() && hasTradeableItems(opponent, true))
						encounterType = Encounter.Trader.SELL;
					
//					// we fudge on whether the trader has capacity to carry the stuff he's buying.
//					if (hasTradeableItems(ship, false) && encounterType != Encounter.Trader.SELL)
					// In Java, we don't need to fudge this because we can check opponent's cargo bays. (Not that this couldn't have been done longhand in the original, as it was when being looted by pirates)
					if (hasTradeableItems(ship, false) && encounterType != Encounter.Trader.SELL && (opponent.filledCargoBays() < opponent.totalCargoBays()))
						encounterType = Encounter.Trader.BUY;
				}
				
				// If they ignore you and you can't see them, the encounter doesn't take place
				if ( (encounterType == Encounter.Trader.IGNORE || encounterType == Encounter.Trader.FLEE
						|| encounterType == Encounter.Trader.SELL || encounterType == Encounter.Trader.BUY)
						&& opponent.cloaked(ship) )
				{
					--clicks;
					continue;
				}
				// pay attention to user's prefs with regard to ignoring traders
				if (alwaysIgnoreTraders && (encounterType == Encounter.Trader.IGNORE ||
						encounterType == Encounter.Trader.FLEE))
				{

					--clicks;
					continue;
				}
				// pay attention to user's prefs with regard to ignoring trade in orbit
				if (alwaysIgnoreTradeInOrbit && (encounterType == Encounter.Trader.BUY ||
						encounterType == Encounter.Trader.SELL))
				{	
					--clicks;
					continue;
				}

				mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
				return;
			}
			// Very Rare Random Events:
			// 1. Encounter the abandoned Marie Celeste, which you may loot.
			// 2. Captain Ahab will trade your Reflective Shield for skill points in Piloting.
			// 3. Captain Conrad will trade your Military Laser for skill points in Engineering.
			// 4. Captain Huie will trade your Military Laser for points in Trading.
			// 5. Encounter an out-of-date bottle of Captain Marmoset's Skill Tonic. This
			//    will affect skills depending on game difficulty level.
			// 6. Encounter a good bottle of Captain Marmoset's Skill Tonic, which will invoke
			//    IncreaseRandomSkill one or two times, depending on game difficulty.
			else if ((days > 10) && (getRandom(1000) < chanceOfVeryRareEncounter ))
			{
				Encounter.VeryRare rareEncounter = getRandom(Encounter.VeryRare.values());

				switch (rareEncounter)
				{
				case MARIECELESTE:
					if ((veryRareEncounter & ALREADYMARIE) == 0)
					{
						veryRareEncounter += ALREADYMARIE;
						encounterType = Encounter.VeryRare.MARIECELESTE;
						generateOpponent(Opponent.TRADER);
						for (TradeItem item : TradeItem.values())
						{
							opponent.clearCargo(item);
						}
						opponent.addCargo(TradeItem.NARCOTICS, min(opponent.type.cargoBays, 5));
						mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
						return;
					}
					break;

				case CAPTAINAHAB:
					if (haveReflectiveShield && commander().pilot() < 10 &&
							policeRecordScore > PoliceRecord.CRIMINAL.score &&
							(veryRareEncounter & ALREADYAHAB) == 0)
					{
						veryRareEncounter += ALREADYAHAB;
						encounterType = Encounter.VeryRare.CAPTAINAHAB;
						generateOpponent( Opponent.FAMOUSCAPTAIN );
						mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
						return;
					}
					break;

				case CAPTAINCONRAD:
					if (haveMilitaryLaser && commander().engineer() < 10 &&
							policeRecordScore > PoliceRecord.CRIMINAL.score &&
							(veryRareEncounter & ALREADYCONRAD) == 0)
					{
						veryRareEncounter += ALREADYCONRAD;
						encounterType = Encounter.VeryRare.CAPTAINCONRAD;
						generateOpponent( Opponent.FAMOUSCAPTAIN );
						mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
						return;
					}
					break; 

				case CAPTAINHUIE:
					if (haveMilitaryLaser && commander().trader() < 10 &&
							policeRecordScore > PoliceRecord.CRIMINAL.score &&
							(veryRareEncounter & ALREADYHUIE) == 0)
					{
						veryRareEncounter = veryRareEncounter | ALREADYHUIE;
						encounterType = Encounter.VeryRare.CAPTAINHUIE;
						generateOpponent( Opponent.FAMOUSCAPTAIN );
						mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
						return;
					}
					break;
				case BOTTLEOLD:
					if  ((veryRareEncounter & ALREADYBOTTLEOLD) == 0)
					{
						veryRareEncounter = veryRareEncounter | ALREADYBOTTLEOLD;
						encounterType = Encounter.VeryRare.BOTTLEOLD;
						generateOpponent( Opponent.BOTTLE );
						mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
						return;
					}
					break;
				case BOTTLEGOOD:
					if  ((veryRareEncounter & ALREADYBOTTLEGOOD) == 0)
					{
						veryRareEncounter = veryRareEncounter | ALREADYBOTTLEGOOD;
						encounterType = Encounter.VeryRare.BOTTLEGOOD;
						generateOpponent( Opponent.BOTTLE );
						mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
						return;
					}
					break;
				default:
					break;
				}
			}
					
			--clicks;
		}
		
		// ah, just when you thought you were gonna get away with it...
		if (justLootedMarie)
		{			
			generateOpponent( Opponent.POLICE );
			encounterType = Encounter.VeryRare.POSTMARIEPOLICE;
			justLootedMarie = false;
			clicks++;
			mGameManager.setCurrentScreenType(ScreenType.ENCOUNTER);
			return;
		}
		
		new ArrivalTask().execute(startClicks);
		
	}
	
	private class ArrivalTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int startClicks = params[0];
			
			// Arrival in the target system
			CountDownLatch latch = newLatch();
			mGameManager.showDialogFragment(SimpleDialog.newInstance(
					(startClicks > 20 ? R.string.screen_encounter_uneventful_title : R.string.screen_encounter_arrival_title), 
					(startClicks > 20 ? R.string.screen_encounter_uneventful_message : R.string.screen_encounter_arrival_message),
					(startClicks > 20 ? R.string.help_uneventfultrip : R.string.help_arrival),
					newUnlocker(latch)));
			lock(latch);
			
			// Check for Large Debt - 06/30/01 SRA 
			if (debt >= DEBTWARNING ) {
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_largedebt_title,
						R.string.screen_encounter_largedebt_message,
						R.string.help_debtwarning,
						newUnlocker(latch),
						debt));
				lock(latch);
			}

			// Debt Reminder
			if (debt > 0 && remindLoans && days % 5 == 0)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_loanreminder_title,
						R.string.screen_encounter_loanreminder_message,
						R.string.help_loanreminder,
						newUnlocker(latch),
						debt));
				lock(latch);
			}
			
			arrival();

			// Reactor warnings:	
			// now they know the quest has a time constraint!
			if (reactorStatus == 2)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_reactorconsume_title,
						R.string.screen_encounter_reactorconsume_message,
						R.string.help_reactorusingfuel,
						newUnlocker(latch)));
				lock(latch);
			}
			// better deliver it soon!
			else if (reactorStatus == 16)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_reactornoise_title,
						R.string.screen_encounter_reactornoise_message,
						R.string.help_reactorusingfuel,
						newUnlocker(latch)));
				lock(latch);
			}
			// last warning!
			else if (reactorStatus == 18)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_reactorsmoke_title,
						R.string.screen_encounter_reactorsmoke_message,
						R.string.help_reactorusingfuel,
						newUnlocker(latch)));
				lock(latch);
			}
			
			if (reactorStatus == 20)
			{
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_reactormeltdown_title,
						R.string.screen_encounter_reactormeltdown_message,
						R.string.help_reactorselfdestruct,
						newUnlocker(latch)));
				lock(latch);
				reactorStatus = 0;
				if (escapePod)
				{
					escapeWithPod();
					return null;
				}
				else
				{
					latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_lose_title,
							R.string.screen_encounter_lose_message,
							R.string.help_shipdestroyed,
							newUnlocker(latch)
							));
					lock(latch);

					showEndGameScreen(EndStatus.KILLED);
					return null;
				}
				
			}

			if (trackAutoOff && trackedSystem == curSystem())
			{
				trackedSystem = null;
			}

			boolean foodOnBoard = false;
			int previousTribbles = ship.tribbles;
			
			if (ship.tribbles > 0 && reactorStatus > 0 && reactorStatus < 21)
			{
				ship.tribbles /= 2;
				if (ship.tribbles < 10)
				{
					ship.tribbles = 0;

					latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_tribblesallirradiated_title,
							R.string.screen_encounter_tribblesallirradiated_message,
							R.string.help_irradiatedtribbles,
							newUnlocker(latch)
							));
					lock(latch);
				}
				else
				{
					latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_tribblesirradiated_title,
							R.string.screen_encounter_tribblesirradiated_message,
							R.string.help_irradiatedtribbles,
							newUnlocker(latch)
							));
					lock(latch);
				}
			}
			else if (ship.tribbles > 0 && ship.getCargo(TradeItem.NARCOTICS) > 0)
			{
				ship.tribbles = 1 + getRandom( 3 );
				int j = 1 + getRandom( 3 );
				int i = min( j, ship.getCargo(TradeItem.NARCOTICS) );
				buyingPrice.put(TradeItem.NARCOTICS, (buyingPrice.get(TradeItem.NARCOTICS) * 
					(ship.getCargo(TradeItem.NARCOTICS) - i)) / ship.getCargo(TradeItem.NARCOTICS));
				ship.addCargo(TradeItem.NARCOTICS, -i);
				ship.addCargo(TradeItem.FURS, +i);
				
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_tribblesatenarcotics_title,
						R.string.screen_encounter_tribblesatenarcotics_message,
						R.string.help_tribblesatenarcotics,
						newUnlocker(latch)
						));
				lock(latch);
			}
			else if (ship.tribbles > 0 && ship.getCargo(TradeItem.FOOD) > 0)
			{
				ship.tribbles += 100 + getRandom( ship.getCargo(TradeItem.FOOD) * 100 );
				int i = getRandom( ship.getCargo(TradeItem.FOOD) );
				buyingPrice.put(TradeItem.FOOD, (buyingPrice.get(TradeItem.FOOD) * i) / ship.getCargo(TradeItem.FOOD));
				ship.clearCargo(TradeItem.FOOD);
				ship.addCargo(TradeItem.FOOD, i);
				
				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_tribblesatefood_title,
						R.string.screen_encounter_tribblesatefood_message,
						R.string.help_tribblesatefood,
						newUnlocker(latch)
						));
				lock(latch);
				foodOnBoard = true;
			}
	
			if (ship.tribbles > 0 && ship.tribbles < MAXTRIBBLES)
				ship.tribbles += 1 + getRandom( max( 1, (ship.tribbles >> (foodOnBoard ? 0 : 1)) ) );
				
			if (ship.tribbles > MAXTRIBBLES)
				ship.tribbles = MAXTRIBBLES;
	
			if ((previousTribbles < 100 && ship.tribbles >= 100) ||
				(previousTribbles < 1000 && ship.tribbles >= 1000) ||
				(previousTribbles < 10000 && ship.tribbles >= 10000) ||
				(previousTribbles < 50000 && ship.tribbles >= 50000))
			{
				int tribbleMessageResId;
				if (ship.tribbles >= MAXTRIBBLES)
					tribbleMessageResId = R.string.screen_encounter_tribblesonboard_message_many;
				else
					tribbleMessageResId = R.string.screen_encounter_tribblesonboard_message;

				latch = newLatch();
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.screen_encounter_tribblesonboard_title,
						tribbleMessageResId,
						R.string.help_tribblenotice,
						newUnlocker(latch),
						ship.tribbles
						));
				lock(latch);
			}
			
			tribbleMessage = false;

			ship.hull += getRandom( ship.skill(Skill.ENGINEER) );
			if (ship.hull > ship.getHullStrength())
				ship.hull = ship.getHullStrength();

			boolean tryAutoRepair = true;
			if (autoFuel)
			{	
				buyFuel( ship.getFuelTanks()*ship.type.costOfFuel );
				if (ship.getFuel() < ship.getFuelTanks())
				{
					if (autoRepair && ship.hull < ship.getHullStrength())
					{
						latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_encounter_notanksorrepairs_title, 
								R.string.screen_encounter_notanksorrepairs_message,
								R.string.help_nofulltanksorrepairs,
								newUnlocker(latch)));
						lock(latch);
						tryAutoRepair = false;
					}
					else {
						latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_encounter_notanks_title, 
								R.string.screen_encounter_notanks_message,
								R.string.help_nofulltanks,
								newUnlocker(latch)));
						lock(latch);
					}
				}
			}

			if (autoRepair && tryAutoRepair)
			{	
				buyRepairs( ship.getHullStrength()*ship.type.repairCosts );
				if (ship.hull < ship.getHullStrength()) {
					latch = newLatch();
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_encounter_norepairs_title, 
							R.string.screen_encounter_norepairs_message,
							R.string.help_nofullrepairs,
							newUnlocker(latch)));
					lock(latch);
				}
			}
			
		    /* This Easter Egg gives the commander a Lighting Shield */
			if (curSystem() == solarSystem[og])
			{
				int i = 0;
				boolean easterEgg = false;
				for (TradeItem item : TradeItem.values())		
				{
					if (ship.getCargo(item) != 1)
						break;
					++i;
				}
				if (i >= TradeItem.values().length)
			    {
					
					int firstEmptySlot = getFirstEmptySlot( ship.type.shieldSlots, ship.shield );
		           
		            if (firstEmptySlot >= 0)
		            {
		            	// NB moved this here instead of displaying before checking firstEmptySlot. Now we only see the dialog if we have space and something happens.
						latch = newLatch();
						mGameManager.showDialogFragment(SimpleDialog.newInstance(
								R.string.screen_encounter_egg_title,
								R.string.screen_encounter_egg_message,
								R.string.help_egg,
								newUnlocker(latch)));
						lock(latch);
		            	
				      	ship.shield[firstEmptySlot] = Shield.LIGHTNING;  
					  	ship.shieldStrength[firstEmptySlot] = Shield.LIGHTNING.power;
				      	easterEgg = true;
				    }
				      
				      
				    if (easterEgg)
				    {
					  	for (TradeItem item : TradeItem.values())
					    {
						 	ship.clearCargo(item);
						 	buyingPrice.put(item, 0);
						}
		            }			
				}
			}
			
			// It seems a glitch may cause cargo bays to become negative - no idea how...
			for (TradeItem item : TradeItem.values()) {
				if (ship.getCargo(item) < 0)
					ship.clearCargo(item);
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mGameManager.setCurrentScreenType(ScreenType.INFO);
			mGameManager.clearBackStack();
			
			// NB Now autosaving on arrival:
			mGameManager.autosave();
		}

	}

	// *************************************************************************
	// Returns true if there exists a wormhole from a to b. 
	// If b < 0, then return true if there exists a wormhole 
	// at all from a.
	// *************************************************************************
	public boolean wormholeExists( SolarSystem a, SolarSystem b )
	{
		int i;

		i = 0;
		while (i < wormhole.length)
		{
			if (wormhole[i] == a)
				break;
			++i;
		}

		if (i < wormhole.length)
		{
			if (b == null)
				return true;
			else if (i < wormhole.length - 1)
			{
				if (wormhole[i+1] == b)
					return true;
			}
			else if (wormhole[0] == b)
				return true;

		}

		return false;
	}
	

	// *************************************************************************
	// Standard handling of arrival
	// *************************************************************************
	public void arrival(  )
	{
		commander().setSystem(warpSystem);
		for (SolarSystem system : solarSystem)
		{
			system.shuffleStatus();
			system.changeQuantities();
		}
		determinePrices(curSystem());
		alreadyPaidForNewspaper = false;

	}
	

	// *************************************************************************
	// Determine first empty slot, return -1 if none
	// *************************************************************************
	private static int getFirstEmptySlot( int slots, Object[] item )
	{
		int firstEmptySlot = -1;
		for (int j=0; j<slots; ++j)
		{
			if (item[j] == null)
			{
				firstEmptySlot = j;
				break;
			}							
		}
		
		return firstEmptySlot;
	}
	
	public void showNewGameDialog() {
		BaseDialog dialog = mGameManager.findDialogByClass(NewGameDialog.class);
		
		commander().initializeSkills();
		
		String name = commander().name;
		if (name == null || name.length() <= 0) {
			name = getResources().getString(R.string.name_commander);
		}
		dialog.setViewTextById(R.id.dialog_newgame_name, name);
		
		((CheckBox)dialog.getDialog().findViewById(R.id.dialog_newgame_randomsystems)).setChecked(randomQuestSystems);
		
		newCommanderDrawSkills();
	}

	// *************************************************************************
	// Handling of the New Commander
	// *************************************************************************
	public void newCommanderFormHandleEvent(int buttonId)
	{
		BaseDialog dialog = mGameManager.findDialogByClass(NewGameDialog.class);
			
		// Tapping of one of the skill increase or decrease buttons
		switch (buttonId)
		{
		case AlertDialog.BUTTON_POSITIVE:	// NB moved ok button handling to here
			if (2*MAXSKILL - commander().pilot() - commander().fighter() -
					commander().trader() - commander().engineer() > 0)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_newgame_morepoints, 
						R.string.dialog_newgame_morepoints_message, 
						R.string.help_moreskillpoints));
				return;
			}
			String name = ((EditText)dialog.getDialog().findViewById(R.id.dialog_newgame_name)).getText().toString().trim();
			
			// NB this is a new check to verify that name is not blank. 
			if (name.length() <= 0) {
				mGameManager.showDialogFragment(SimpleDialog.newInstance(
						R.string.dialog_newgame_noname, 
						R.string.dialog_newgame_noname_message,
						R.string.help_noname));
				return;
			}

//			commander().name = name;
			mercenary[0] = new CrewMember(name, commander().pilot(), commander().fighter(), commander().trader(), commander().engineer(), this);
			
			
//			int pilot = commander().pilot();
//			int fighter = commander().fighter();
//			int trader = commander().trader();
//			int engineer = commander().engineer();
//			SolarSystem curSystem = curSystem();
			
			randomQuestSystems = ((CheckBox)dialog.getDialog().findViewById(R.id.dialog_newgame_randomsystems)).isChecked();
			startNewGame();// NB in original this happens before dialog displays instead of on dismissal. We don't do that here for historical reasons and also so we can read in randomQuestsSystems before generating galaxy.

//			mercenary[0] = new CrewMember(commander().name, pilot, fighter, trader, engineer, this);
//			mercenary[0].setSystem(curSystem);
			
			determinePrices(curSystem());

			if (difficulty.compareTo(DifficultyLevel.NORMAL) < 0)
				if (curSystem().special() == null)
					curSystem().setSpecial(SpecialEvent.LOTTERYWINNER);

			((ImageView) mGameManager.getCurrentScreen().getView().findViewById(R.id.screen_title_image)).setImageDrawable(null);
			mGameManager.setCurrentScreenType(ScreenType.INFO);
			mGameManager.clearBackStack();
			mGameManager.autosave();
			dialog.dismiss();

			return;
		
		case R.id.dialog_newgame_difficultypicker_minus:
			difficulty = difficulty.prev();
			break;

		case R.id.dialog_newgame_difficultypicker_plus:
			difficulty = difficulty.next();
			break;

		case R.id.dialog_newgame_pilotpicker_minus:
			if (commander().pilot() > 1)
				commander().decreaseSkill(Skill.PILOT);
			break;

		case R.id.dialog_newgame_pilotpicker_plus:
			if (commander().pilot() < MAXSKILL)
				if (2*MAXSKILL - commander().pilot() - commander().fighter() -
						commander().trader() - commander().engineer() > 0)
					commander().increaseSkill(Skill.PILOT);
			break;

		case R.id.dialog_newgame_fighterpicker_minus:
			if (commander().fighter() > 1)
				commander().decreaseSkill(Skill.FIGHTER);
			break;

		case R.id.dialog_newgame_fighterpicker_plus:
			if (commander().fighter() < MAXSKILL)
				if (2*MAXSKILL - commander().pilot() - commander().fighter() -
						commander().trader() - commander().engineer() > 0)
					commander().increaseSkill(Skill.FIGHTER);
			break;

		case R.id.dialog_newgame_traderpicker_minus:
			if (commander().trader() > 1)
				commander().decreaseSkill(Skill.TRADER);
			break;

		case R.id.dialog_newgame_traderpicker_plus:
			if (commander().trader() < MAXSKILL)
				if (2*MAXSKILL - commander().pilot() - commander().fighter() -
						commander().trader() - commander().engineer() > 0)
					commander().increaseSkill(Skill.TRADER);
			break;

		case R.id.dialog_newgame_engineerpicker_minus:
			if (commander().engineer() > 1)
				commander().decreaseSkill(Skill.ENGINEER);
			break;

		case R.id.dialog_newgame_engineerpicker_plus:
			if (commander().engineer() < MAXSKILL)
				if (2*MAXSKILL - commander().pilot() - commander().fighter() -
						commander().trader() - commander().engineer() > 0)
					commander().increaseSkill(Skill.ENGINEER);
			break;
		}
		newCommanderDrawSkills();
	}

	// *************************************************************************
	// Handling of the Average Prices form
	// *************************************************************************
	public void averagePricesFormHandleEvent(int buttonId)
	{
//		BaseDialog dialog = mGameManager.findDialogByClass(WarpPopupDialog.class);
		
//		if (WarpPricesScreen.LABEL_IDS.containsValue(buttonId) ||
//				WarpPricesScreen.PRICE_IDS.containsValue(buttonId))
		if (WarpPricesScreen.CLICKABLE_IDS.containsValue(buttonId))
		{
			for (TradeItem item : TradeItem.values())
			{
//				if (WarpPricesScreen.LABEL_IDS.get(item) == buttonId || WarpPricesScreen.PRICE_IDS.get(item) == buttonId)
				if (WarpPricesScreen.CLICKABLE_IDS.get(item) == buttonId)
				{
					Log.d("averagePrices","Clicked on "+item.toXmlString(getResources()));
					getAmountToBuy(item);
					return;
				}
			}
		}

		switch (buttonId)
		{
		case R.id.screen_warp_prev:
		case R.id.screen_warp_next:
//			SolarSystem system = nextSystemWithinRange( warpSystem, buttonId == R.id.screen_warp_prev );
//			if (system != null)
//			{
//				warpSystem = system;
//				showAveragePrices();
//			}
			ViewPager pager =  ((WarpSubScreen) mGameManager.getCurrentScreen()).getPager();
			int position = (buttonId == R.id.screen_warp_prev? WarpSystemPagerAdapter.POSITION_PREV : WarpSystemPagerAdapter.POSITION_NEXT);
			pager.setCurrentItem(position);
			break;

		case R.id.screen_warp_warp:
			doWarp(false);
			break;

		case R.id.screen_warp_toggle:
			aplScreen = false;
//			showExecuteWarp();
			mGameManager.setCurrentScreenType(ScreenType.TARGET);
			break;

		case R.id.screen_warp_avgprices_diffbutton:
			priceDifferences = !priceDifferences;
			mGameManager.getCurrentScreen().onRefreshScreen();
			break;

		default:
			mGameManager.setCurrentScreenType(ScreenType.WARP);
			break;
		}
	}


	// *************************************************************************
	// Handling of the Execute Warp form
	// *************************************************************************
	public void executeWarpFormHandleEvent(int buttonId)
	{
//		BaseDialog dialog = mGameManager.findDialogByClass(WarpPopupDialog.class);

		switch (buttonId)
		{
		case R.id.screen_warp_prev:
		case R.id.screen_warp_next:
//			SolarSystem system = nextSystemWithinRange( warpSystem, buttonId == R.id.screen_warp_prev );
//			if (system != null)
//			{
//				warpSystem = system;
//				showExecuteWarp();
//			}
			ViewPager pager =  ((WarpSubScreen) mGameManager.getCurrentScreen()).getPager();
			int position = (buttonId == R.id.screen_warp_prev? WarpSystemPagerAdapter.POSITION_PREV : WarpSystemPagerAdapter.POSITION_NEXT);
			pager.setCurrentItem(position);
			break;

		// Warp	to another system. This can only be selected if the warp is indeed possible		
		case R.id.screen_warp_warp:
			doWarp(false);
			break;

		case R.id.screen_warp_toggle:
			aplScreen = true;
//			showAveragePrices();
			mGameManager.setCurrentScreenType(ScreenType.AVGPRICES);
			break;

		case R.id.screen_warp_target_cost_specific:
			mGameManager.showDialogFragment(WarpTargetCostDialog.newInstance());
			break;

		default:
			mGameManager.setCurrentScreenType(ScreenType.WARP);
			break;
		}
				
	}
	
	// NB Adding a new method for filling out the cost specification dialog
	public void showSpecificationForm()
	{
		BaseDialog dialog = mGameManager.findDialogByClass(WarpTargetCostDialog.class);
		
		int total = 0;

		dialog.setViewTextById(R.id.screen_warp_target_cost_specific_merc, R.string.format_credits, mercenaryMoney());
		total += mercenaryMoney();
		dialog.setViewTextById(R.id.screen_warp_target_cost_specific_ins, R.string.format_credits, insuranceMoney());
		total += insuranceMoney();

		dialog.setViewTextById(R.id.screen_warp_target_cost_specific_tax, R.string.format_credits, wormholeTax(curSystem(), warpSystem));
		total += wormholeTax(curSystem(), warpSystem);

		int incDebt = 0;
		if (debt > 0)
		{
			incDebt = max( 1, debt / 10 );
			total += incDebt;
		}
		dialog.setViewTextById(R.id.screen_warp_target_cost_specific_int, R.string.format_credits, incDebt);

		dialog.setViewTextById(R.id.screen_warp_target_cost_specific_total, R.string.format_credits, total);
	}
	
	
	/*
	 * WarpFormEvent.c
	 * NB A bunch of stuff changed here to more naturally handle android graphics
	 */
	// *************************************************************************
	// Draw the short range chart
	// *************************************************************************
	public void drawShortRange( Canvas canvas ) {

		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.WARP) return;

		final float RADIUS = 8 * getResources().getDisplayMetrics().density;
		final float SEL_CROSS = 1.5f;
		final int TEXT_OFFSET = 2;
		final float WORMHOLE_OFFSET = 2.5f;
		final float ARROW_WIDTH = 1f;
		final float ARROW_LENGTH = 6.25f;

		boolean l = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
		final Drawable defaultDrawable = ResourcesCompat.getDrawable(getResources(), l? R.drawable.warpsysteml : R.drawable.warpsystem, mGameManager.getTheme());
		final Drawable visitedDrawable = ResourcesCompat.getDrawable(getResources(), l? R.drawable.warpsystemvl : R.drawable.warpsystemv, mGameManager.getTheme());
		final Drawable wormholeDrawable = ResourcesCompat.getDrawable(getResources(), l? R.drawable.warpsystemwl : R.drawable.warpsystemw, mGameManager.getTheme());

		initializePaints();

		int cw = canvas.getWidth();
		float scale = cw * 1f / 54;	// NB 54 is the width of the original screen in grid units

		// Centre of chart
		float xs = cw/2;
		float ys = cw/2;
		float delta = ship.getFuel()*scale;

		// Draw the maximum range circle
		if (ship.getFuel() > 0)
			canvas.drawCircle(xs, ys, delta, chartStroke);

		if (warpSystem != null &&
				(abs( warpSystem.x() - curSystem().x() ) <= MAXRANGE) &&
				(abs( warpSystem.y() - curSystem().y() ) <= MAXRANGE))
		{
			float x = (warpSystem.x() - curSystem().x())*scale + cw/2;
			float y = (warpSystem.y() - curSystem().y())*scale + cw/2;
			canvas.drawLine(x, y-SEL_CROSS*RADIUS, x, y+SEL_CROSS*RADIUS, chartStroke);
			canvas.drawLine(x-SEL_CROSS*RADIUS, y, x+SEL_CROSS*RADIUS, y, chartStroke);
		}

		// show the tracked system (if any)
		if (trackedSystem != null)
		{
			int distToTracked = realDistance(curSystem(), trackedSystem);
			if (distToTracked > 0)
			{
				float drawDistToTracked = distToTracked*scale;
				float dX = (ARROW_LENGTH*RADIUS * (curSystem().x() - trackedSystem.x())*scale / drawDistToTracked);
				float dY = (ARROW_LENGTH*RADIUS * (curSystem().y() - trackedSystem.y())*scale / drawDistToTracked);
				float dY3 = -(ARROW_WIDTH*RADIUS * (curSystem().x() - trackedSystem.x())*scale / drawDistToTracked);
				float dX3 =  (ARROW_WIDTH*RADIUS * (curSystem().y() - trackedSystem.y())*scale / drawDistToTracked);

				canvas.drawLine(
						(+dX3) + cw/2,
						(+dY3) + cw/2,
						(-dX) + cw/2,
						(-dY) + cw/2,
						chartStroke);
				canvas.drawLine(
						(-dX) + cw/2,
						(-dY) + cw/2,
						(-dX3) + cw/2,
						(-dY3) + cw/2,
						chartStroke);
			}
		}


		// Two loops: first draw the names and then the systems. The names may
		// overlap and the systems may be drawn on the names, but at least every
		// system is visible.
		for (int j=0; j<2; ++j)
		{
			for (SolarSystem system : solarSystem)
			{
				if ((abs( system.x() - curSystem().x() ) <= MAXRANGE) &&
						(abs( system.y() - curSystem().y() ) <= MAXRANGE))
				{
					float x = (system.x() - curSystem().x())*scale + cw/2;
					float y = (system.y() - curSystem().y())*scale + cw/2;
					if (j == 1)
					{
//						if (system == warpSystem)
//						{
//							canvas.drawLine(x, y-SEL_CROSS*RADIUS, x, y+SEL_CROSS*RADIUS, chartStroke);
//							canvas.drawLine(x-SEL_CROSS*RADIUS, y, x+SEL_CROSS*RADIUS, y, chartStroke);
//						}
						Drawable d = system.visited()? visitedDrawable : defaultDrawable;
						d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
						d.draw(canvas);
						if (wormholeExists( system, null ))
						{
							x = x + WORMHOLE_OFFSET*RADIUS;
							if (wormholeExists( system, warpSystem ))
							{
								canvas.drawLine(x, y-SEL_CROSS*RADIUS, x, y+SEL_CROSS*RADIUS, chartStroke);
								canvas.drawLine(x-SEL_CROSS*RADIUS, y, x+SEL_CROSS*RADIUS, y, chartStroke);
							}
							d = wormholeDrawable;
							d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
							d.draw(canvas);
						}
					}
					else
					{
						canvas.drawText(system.name,
								(system.x() - curSystem().x())*scale + cw/2,
								(system.y() - curSystem().y() - TEXT_OFFSET)*scale + cw/2,
								chartText);
					}
				}
			}
		}

		// if they're tracking, and they want range info:
		if (trackedSystem != null && showTrackedRange)
		{
			screen.setViewVisibilityById(R.id.screen_warp_tracked, true);
			screen.setViewTextById(R.id.screen_warp_tracked, R.string.screen_warp_distance, realDistance(curSystem(), trackedSystem), trackedSystem.name);
		}
		else
			screen.setViewVisibilityById(R.id.screen_warp_tracked, false);
		

//		// Screenshot override
//		float x=cw/2, y=cw/2;
//		canvas.drawCircle(x, y, 15*scale, chartStroke);
//		canvas.drawLine(x, y-SEL_CROSS*RADIUS, x, y+SEL_CROSS*RADIUS, chartStroke);
//		canvas.drawLine(x-SEL_CROSS*RADIUS, y, x+SEL_CROSS*RADIUS, y, chartStroke);
//		canvas.drawText(getResources().getString(R.string.solarsystem_courteney), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-15*scale;
//		y=cw/2+1*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_andevian), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-16*scale;
//		y=cw/2+8*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_davlos), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-9*scale;
//		y=cw/2+-8*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_sol), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-3*scale;
//		y=cw/2+-12*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_jarada), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-15*scale;
//		y=cw/2+-16*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_korma), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-19*scale;
//		y=cw/2+-8*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_montor), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+-13*scale;
//		y=cw/2+18*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_festen), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+2*scale;
//		y=cw/2+13*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_cestus), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+10*scale;
//		y=cw/2+7*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_hades), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+19*scale;
//		y=cw/2+16*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_drema), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+5*scale;
//		y=cw/2+-5*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_exo), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+14*scale;
//		y=cw/2+-11*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_xerxes), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+9*scale;
//		y=cw/2+-16*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_tarchannen), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2+20*scale;
//		y=cw/2+-20*scale;
//		canvas.drawText(getResources().getString(R.string.solarsystem_iralius), x, y - TEXT_OFFSET*scale, chartText);
//		x=cw/2;
//		y=cw/2;
//		Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.warpsystemvl, mGameManager.getTheme());
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+-15*scale;
//		y=cw/2+1*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+-16*scale;
//		y=cw/2+8*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+-9*scale;
//		y=cw/2+-8*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+-3*scale;
//		y=cw/2+-12*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+-15*scale;
//		y=cw/2+-16*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+-19*scale;
//		y=cw/2+-8*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		d = ResourcesCompat.getDrawable(getResources(), R.drawable.warpsysteml, mGameManager.getTheme());
//		x=cw/2+-13*scale;
//		y=cw/2+18*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+2*scale;
//		y=cw/2+13*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+10*scale;
//		y=cw/2+7*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+19*scale;
//		y=cw/2+16*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+5*scale;
//		y=cw/2+-5*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+14*scale;
//		y=cw/2+-11*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+9*scale;
//		y=cw/2+-16*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		x=cw/2+20*scale;
//		y=cw/2+-20*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
//		d = ResourcesCompat.getDrawable(getResources(), R.drawable.warpsystemwl, mGameManager.getTheme());
//		x=cw/2+-15*scale + WORMHOLE_OFFSET*RADIUS;
//		y=cw/2+1*scale;
//		d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
//		d.draw(canvas);
		
		
	}
	
	public void showGalaxy()
	{
		galacticChartSystem = curSystem();
		galacticChartWormhole = false;
	}

	// *************************************************************************
	// Draw the galactic chart, with system Index selected.
	// *************************************************************************
	public void drawGalaxy( Canvas canvas )
	{
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.CHART) return;
	
		
		final float RADIUS = 4f * getResources().getDisplayMetrics().density;
		final float SEL_OUTER = 1.75f;
		final float SEL_INNER = 1f;
		final float WORMHOLE_OFFSET = 2f;
		
		// NB now caching these ahead of time to see if it helps occasional graphical glitches
		boolean l = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
		final Drawable defaultDrawable = ResourcesCompat.getDrawable(getResources(), l? R.drawable.chartsysteml : R.drawable.chartsystem, mGameManager.getTheme());
		final Drawable visitedDrawable = ResourcesCompat.getDrawable(getResources(), l? R.drawable.chartsystemvl : R.drawable.chartsystemv, mGameManager.getTheme());
		final Drawable wormholeDrawable = ResourcesCompat.getDrawable(getResources(), l? R.drawable.chartsystemwl : R.drawable.chartsystemw, mGameManager.getTheme());
		
		initializePaints();

		int cw = screen.getView().findViewById(R.id.screen_chart_chartview).getWidth();
		int ch = screen.getView().findViewById(R.id.screen_chart_chartview).getHeight();
		float scale = cw * 1f / (1.1f * GALAXYWIDTH);
//		int cw = canvas.getWidth();
//		int ch = canvas.getHeight();
//		float scale = cw * 1f / (1.1f * GALAXYWIDTH);
		
		if (ship.getFuel() > 0)
			canvas.drawCircle((curSystem().x() - GALAXYWIDTH/2)*scale + cw/2, (curSystem().y() - GALAXYHEIGHT/2)*scale + ch/2, (ship.getFuel())*scale, chartStroke);

		if (galacticChartSystem != null)
		{
			float x = (galacticChartSystem.x() - GALAXYWIDTH/2)*scale + cw/2;
			float y = (galacticChartSystem.y() - GALAXYHEIGHT/2)*scale + ch/2;
			canvas.drawLine(
					x + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS,
					y - SEL_OUTER*RADIUS,
					x + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS,
					y + SEL_OUTER*RADIUS,
					chartStroke
			);
			canvas.drawLine(
					x + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS - SEL_OUTER*RADIUS,
					y,
					x + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS + SEL_OUTER*RADIUS,
					y,
					chartStroke
			);
		}

		if (trackedSystem != null)
		{
			float x = (trackedSystem.x() - GALAXYWIDTH/2)*scale + cw/2;
			float y = (trackedSystem.y() - GALAXYHEIGHT/2)*scale + ch/2;
			canvas.drawLine(
					x - SEL_OUTER*RADIUS,
					y + SEL_OUTER*RADIUS,
					x - SEL_INNER*RADIUS,
					y + SEL_INNER*RADIUS,
					chartStroke
			);
			canvas.drawLine(
					x + SEL_OUTER*RADIUS,
					y - SEL_OUTER*RADIUS,
					x + SEL_INNER*RADIUS,
					y - SEL_INNER*RADIUS,
					chartStroke
			);
			canvas.drawLine(
					x + SEL_OUTER*RADIUS,
					y + SEL_OUTER*RADIUS,
					x + SEL_INNER*RADIUS,
					y + SEL_INNER*RADIUS,
					chartStroke
			);
			canvas.drawLine(
					x - SEL_OUTER*RADIUS,
					y - SEL_OUTER*RADIUS,
					x - SEL_INNER*RADIUS,
					y - SEL_INNER*RADIUS,
					chartStroke
			);
		}

		SolarSystem wormholeOut = null;
		if (galacticChartWormhole)
		{
			for (int i = 0; i < wormhole.length; i++)
			{
				if (galacticChartSystem == wormhole[i])
				{
					wormholeOut = wormhole[(i+1)%wormhole.length];
					canvas.drawLine(
							(wormhole[i].x() - GALAXYWIDTH/2)*scale + WORMHOLE_OFFSET*RADIUS + cw/2,
							(wormhole[i].y() - GALAXYHEIGHT/2)*scale + ch/2,
							(wormholeOut.x() - GALAXYWIDTH/2)*scale + cw/2,
							(wormholeOut.y() - GALAXYHEIGHT/2)*scale + ch/2,
							chartStroke);
				}
			}
		}

		for (SolarSystem system : solarSystem)
		{

//			if (system == galacticChartSystem)
//			{
//				canvas.drawLine(
//						(system.x() - GALAXYWIDTH/2)*scale + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS + cw/2,
//						(system.y() - GALAXYHEIGHT/2)*scale - SEL_OUTER*RADIUS + ch/2,
//						(system.x() - GALAXYWIDTH/2)*scale + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS + cw/2,
//						(system.y() - GALAXYHEIGHT/2)*scale + SEL_OUTER*RADIUS + ch/2,
//						chartStroke
//						);
//				canvas.drawLine(
//						(system.x() - GALAXYWIDTH/2)*scale + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS - SEL_OUTER*RADIUS + cw/2,
//						(system.y() - GALAXYHEIGHT/2)*scale + ch/2,
//						(system.x() - GALAXYWIDTH/2)*scale + (galacticChartWormhole? WORMHOLE_OFFSET : 0)*RADIUS + SEL_OUTER*RADIUS + cw/2,
//						(system.y() - GALAXYHEIGHT/2)*scale + ch/2,
//						chartStroke
//						);
//			}
			float x = (system.x() - GALAXYWIDTH/2)*scale + cw/2;
			float y = (system.y() - GALAXYHEIGHT/2)*scale + ch/2;
			Drawable d = system.visited()? visitedDrawable : defaultDrawable;
			d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
			d.draw(canvas);

//			if (system1

			if (wormholeExists( system, null ))
			{
				x += WORMHOLE_OFFSET*RADIUS;
				d = wormholeDrawable;
				d.setBounds((int)(x - RADIUS), (int)(y - RADIUS), (int)(x + RADIUS), (int)(y + RADIUS));
				d.draw(canvas);
			}
		}

		if (!galacticChartWormhole)
		{
			screen.setViewTextById(R.id.screen_chart_systemname, galacticChartSystem.name);
			screen.setViewTextById(R.id.screen_chart_distance, R.string.format_parsecs,
					(int) GameState.realDistance(curSystem(), galacticChartSystem));
			screen.setViewTextById(R.id.screen_chart_description, R.string.screen_chart_description,
					galacticChartSystem.size,
					galacticChartSystem.techLevel(),
					galacticChartSystem.politics()
					);
	    }
	    else
	    {
			screen.setViewTextById(R.id.screen_chart_systemname, R.string.screen_chart_wormhole);
			screen.setViewTextById(R.id.screen_chart_distance, "");
			screen.setViewTextById(R.id.screen_chart_description, R.string.screen_chart_wormhole_description,
					galacticChartSystem.name,
					wormholeOut.name
					);
	    }

		screen.setViewVisibilityById(R.id.screen_chart_jump, canSuperWarp);
		
//		// Screenshot override
//		canvas.drawCircle((10f - GALAXYWIDTH/2)*scale + cw/2, (67f - GALAXYHEIGHT/2)*scale + ch/2, 15f*scale, chartStroke);
//		canvas.drawLine((18f - GALAXYWIDTH/2)*scale + cw/2,(58f - GALAXYHEIGHT/2)*scale - SEL_OUTER*RADIUS + ch/2,(18f - GALAXYWIDTH/2)*scale + cw/2,(58f - GALAXYHEIGHT/2)*scale + SEL_OUTER*RADIUS + ch/2,chartStroke);
//		canvas.drawLine((18f - GALAXYWIDTH/2)*scale - SEL_OUTER*RADIUS + cw/2,(58f - GALAXYHEIGHT/2)*scale + ch/2,(18f - GALAXYWIDTH/2)*scale + SEL_OUTER*RADIUS + cw/2,(58f - GALAXYHEIGHT/2)*scale + ch/2,chartStroke);
//		float x,y;
//		Drawable d;
//		d = ResourcesCompat.getDrawable(getResources(), R.drawable.chartsystemvl, mGameManager.getTheme());
//		x=10;y=67;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=22;y=83;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=14;y=82;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=18;y=74;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=28;y=74;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=21;y=90;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=16;y=97;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		d = ResourcesCompat.getDrawable(getResources(), R.drawable.chartsysteml, mGameManager.getTheme());
//		x=22;y=21;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=73;y=22;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=75;y=87;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=119;y=24;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=123;y=86;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=18;y=58;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=2;y=93;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=8;y=92;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=24;y=100;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=2;y=73;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=22;y=66;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=27;y=46;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=15;y=45;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=7;y=41;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=28;y=39;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=13;y=32;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=3;y=27;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=10;y=24;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=17;y=26;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=30;y=25;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=32;y=15;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=8;y=11;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=19;y=10;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=30;y=6;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=35;y=1;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=40;y=10;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=40;y=24;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=36;y=51;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=49;y=3;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=50;y=14;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=48;y=37;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=47;y=46;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=53;y=30;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=60;y=10;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=61;y=20;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=61;y=35;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=69;y=3;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=71;y=10;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=69;y=28;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=67;y=41;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=78;y=16;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=80;y=29;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=78;y=36;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=86;y=1;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=86;y=14;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=86;y=38;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=93;y=7;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=94;y=29;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=102;y=11;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=104;y=32;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=99;y=41;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=109;y=10;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=111;y=24;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=114;y=31;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=117;y=15;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=124;y=6;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=128;y=20;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=126;y=36;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=131;y=1;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=132;y=28;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=140;y=17;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=142;y=28;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=143;y=37;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=34;y=70;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=37;y=82;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=39;y=95;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=39;y=104;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=42;y=77;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=44;y=57;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=46;y=66;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=47;y=89;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=51;y=58;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=51;y=71;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=56;y=98;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=57;y=62;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=59;y=55;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=62;y=71;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=63;y=84;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=66;y=51;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=68;y=79;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=69;y=63;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=73;y=54;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=80;y=50;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=85;y=55;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=86;y=75;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=107;y=49;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=71;y=95;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=74;y=102;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=79;y=80;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=80;y=94;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=85;y=102;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=90;y=84;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=90;y=95;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=95;y=68;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=97;y=93;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=98;y=75;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=103;y=87;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=104;y=67;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=105;y=74;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=108;y=94;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=112;y=85;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=114;y=60;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=114;y=73;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=116;y=92;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=124;y=101;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=125;y=57;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=131;y=61;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=131;y=79;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=134;y=93;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=135;y=85;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=139;y=71;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=147;y=87;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		d = ResourcesCompat.getDrawable(getResources(), R.drawable.chartsystemwl, mGameManager.getTheme());
//		x=22;y=83;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=22;y=21;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=73;y=22;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=75;y=87;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=119;y=24;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		x=123;y=86;d.setBounds((int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS - RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 - RADIUS), (int)((x - GALAXYWIDTH/2)*scale + cw/2 + WORMHOLE_OFFSET*RADIUS + RADIUS), (int)((y - GALAXYHEIGHT/2)*scale + ch/2 + RADIUS));d.draw(canvas);
//		screen.setViewTextById(R.id.screen_chart_systemname,R.string.solarsystem_ventax);
//		screen.setViewTextById(R.id.screen_chart_distance, R.string.format_parsecs, 13);
//		screen.setViewTextById(R.id.screen_chart_description, R.string.screen_chart_description, Size.HUGE, TechLevel.MEDIEVAL, Politics.FEUDAL);
//		screen.setViewVisibilityById(R.id.screen_chart_jump, false);

	}

	// *************************************************************************
	// Events of the short range chart
	// *************************************************************************
	public boolean warpFormHandleEvent(float x, float y)
	{
		final float RADIUS = 8 * getResources().getDisplayMetrics().density;
		final float SEL_RADIUS_FACTOR = 3f;
		final float WORMHOLE_OFFSET = 2.5f;

		WarpScreen screen = (WarpScreen) mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.WARP) return false;

		int cw = screen.getView().findViewById(R.id.screen_warp_warpview).getWidth();
		float scale = cw * 1f / 54; // NB 54 is the width of the original screen in grid units
		SolarSystem oldSystem = warpSystem;
		
		boolean isWormhole = false;
		SolarSystem system = null;
		double dist = SEL_RADIUS_FACTOR * RADIUS;
		for (SolarSystem s : solarSystem)
		{
			float xp = (s.x() - curSystem().x())*scale + cw/2;
			float yp = (s.y() - curSystem().y())*scale + cw/2;
			double d = Math.hypot(xp - x, yp - y);
			if (d < dist)
			{
				dist = d;
				system = s;
			}
		}
	
		for (SolarSystem s : wormhole)
		{
			float xp = (s.x() - curSystem().x())*scale + WORMHOLE_OFFSET*RADIUS + cw/2;
			float yp = (s.y() - curSystem().y())*scale + cw/2;
			double d = Math.hypot(xp - x, yp - y);
			if (d < dist)
			{
				dist = d;
				system = s;
				isWormhole = true;
			}
		}
		
		if (system != null)
		{
			if (isWormhole) {
				SolarSystem newSystem = null;
				for (SolarSystem s : wormhole)
				{
					if (wormholeExists(system, s))
					{
						newSystem = s;
						break;
					}
				}
				
				if (curSystem() != system)
				{
					mGameManager.showDialogFragment(SimpleDialog.newInstance(
							R.string.screen_warp_unreachable_title, 
							R.string.screen_warp_unreachable_message, 
							R.string.help_wormholeoutofrange,
							newSystem, 
							system));
					return false;
				}
				else
					warpSystem = newSystem;
			}
			else
				warpSystem = system;
			
			screen.getWarpView().invalidate();
			if (!alwaysInfo && aplScreen && ((realDistance( curSystem(), warpSystem ) <= ship.getFuel() &&
					realDistance( curSystem(), warpSystem ) > 0) || isWormhole))
				mGameManager.setCurrentScreenType(ScreenType.AVGPRICES);
			else
			{
				aplScreen = false;
				mGameManager.setCurrentScreenType(ScreenType.TARGET);
			}

		}
		
		return warpSystem != oldSystem;
	}

	// *************************************************************************
	// Handling of events on Galactic Chart
	// *************************************************************************
	public void galacticChartFormHandleEvent(int buttonId)
	{

		// Find System
		if (buttonId == R.id.screen_chart_jump)
		{
			if (trackedSystem == null)
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_chart_nosystemselected_title, R.string.screen_chart_nosystemselected_message, R.string.help_singularity));
				return;
			}
			else if (trackedSystem == curSystem())
			{
				mGameManager.showDialogFragment(SimpleDialog.newInstance(R.string.screen_chart_nojumpcursystem_title, R.string.screen_chart_nojumpcursystem_message, R.string.help_nojumptocursystem));
				return;
			}
			else 
			{
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.screen_chart_usesingularity_title, 
						R.string.screen_chart_usesingularity_message, 
						R.string.screen_chart_usesingularity_pos,  
						R.string.screen_chart_usesingularity_neg, 
						R.string.help_singularity,
						new OnConfirmListener() {

							@Override
							public void onConfirm() {
								warpSystem = trackedSystem;
								canSuperWarp = false;
								doWarp(true);
							}
						}, 
						null, 
						trackedSystem));
			}
		}
		else if (buttonId == R.id.screen_chart_find)
		{
			mGameManager.showDialogFragment(ChartFindDialog.newInstance());
		}
	}	

	// Booleans to track whether we've dragged to change the galactic chart system.
	private boolean downOnChartSystem = false;
	private boolean moveOnChartSystem = false;


	// Separate function for touch events takes in coordinates instead of a button.
	public boolean galacticChartFormHandleEvent(float x, float y, int action)
	{
		boolean up = action == ChartScreen.UP;
		boolean down = action == ChartScreen.DOWN;
		boolean move = action == ChartScreen.MOVE;
		boolean longPress = action == ChartScreen.LONG;

		final float RADIUS = 4f * getResources().getDisplayMetrics().density;
		final float SEL_RADIUS_FACTOR = 4f;
		final float WORMHOLE_OFFSET = 2f;

		final ChartScreen screen = (ChartScreen) mGameManager.getCurrentScreen();
		if (screen == null || screen.getView() == null || screen.getType() != ScreenType.CHART) return false;

		int cw = screen.getView().findViewById(R.id.screen_chart_chartview).getWidth();
		int ch = screen.getView().findViewById(R.id.screen_chart_chartview).getHeight();
		float scale = cw * 1f / (1.1f * GALAXYWIDTH);
		SolarSystem oldSystem = galacticChartSystem;
		
		boolean isWormhole = false;
		SolarSystem system = null;
		double dist = SEL_RADIUS_FACTOR * RADIUS;
		for (SolarSystem s : solarSystem)
		{
			float xp = (s.x() - GALAXYWIDTH/2)*scale + cw/2;
			float yp = (s.y() - GALAXYHEIGHT/2)*scale + ch/2;
			double d = Math.hypot(xp - x, yp - y);
			if (d < dist)
			{
				dist = d;
				system = s;
			}
		}

		for (SolarSystem s : wormhole)
		{
			float xp = (s.x() - GALAXYWIDTH/2)*scale + WORMHOLE_OFFSET*RADIUS + cw/2;
			float yp = (s.y() - GALAXYHEIGHT/2)*scale + ch/2;
			double d = Math.hypot(xp - x, yp - y);
			if (d < dist)
			{
				dist = d;
				system = s;
				isWormhole = true;
			}
		}

		if (isWormhole)
		{
			galacticChartWormhole = true;
			galacticChartSystem = system;
			screen.getView().findViewById(R.id.screen_chart_chartview).invalidate();
			return true;
		}

		final SolarSystem fSystem = system;
		boolean showDialogs = trackLongPress? longPress : (up && downOnChartSystem && moveOnChartSystem);
		if (fSystem != null)
		{
			if (showDialogs && fSystem == trackedSystem)
			{
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.screen_chart_track_title, 
						R.string.screen_chart_track_stop, 
						R.string.help_tracksystem,
						new OnConfirmListener() {

					@Override
					public void onConfirm() {
						trackedSystem = null;
						screen.getView().findViewById(R.id.screen_chart_chartview).invalidate();
					}
				},
				null, 
				fSystem));
			}
			else if (showDialogs && fSystem == galacticChartSystem && !galacticChartWormhole)
			{
				if (trackedSystem == null)
				{
					mGameManager.showDialogFragment(ConfirmDialog.newInstance(
							R.string.screen_chart_track_title, 
							R.string.screen_chart_track_start, 
							R.string.help_tracksystem,
							new OnConfirmListener() {

						@Override
						public void onConfirm() {
							trackedSystem = fSystem;
							screen.getView().findViewById(R.id.screen_chart_chartview).invalidate();
						}
					},
					null, 
					fSystem));
				}
				else
				{
					mGameManager.showDialogFragment(ConfirmDialog.newInstance(
							R.string.screen_chart_track_title, 
							R.string.screen_chart_track_switch, 
							R.string.help_tracksystem,
							new OnConfirmListener() {

						@Override
						public void onConfirm() {
							trackedSystem = fSystem;
							screen.getView().findViewById(R.id.screen_chart_chartview).invalidate();
						}
					},
					null,
					trackedSystem,
					fSystem));
				}
			}

			if (down) {
				downOnChartSystem = (fSystem == galacticChartSystem && !galacticChartWormhole);
				moveOnChartSystem = downOnChartSystem;
			} else if (move) {
				moveOnChartSystem = (downOnChartSystem && moveOnChartSystem && fSystem == galacticChartSystem && !galacticChartWormhole);
			} else if (up || longPress) {
				downOnChartSystem = moveOnChartSystem = false;
			}
			galacticChartSystem = fSystem;
			galacticChartWormhole = false;
			screen.getView().findViewById(R.id.screen_chart_chartview).invalidate();
		}
		
		return oldSystem != galacticChartSystem || (longPress && fSystem != null);
	}
	
	// Moved Find dialog handling to new functions here
	private void showCheatConfirm(OnConfirmListener listener) {
		if (!cheated && !developerMode) {
			mGameManager.showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_disablescoring_title, 
					R.string.dialog_disablescoring_cheatmessage, 
					R.string.help_disablescoringcheat, 
					listener, 
					null));
		}
		else
		{
			listener.onConfirm();
		}
	}
	
	public void findDialogHandleEvent(int unused)
	{
		BaseDialog dialog = mGameManager.findDialogByClass(ChartFindDialog.class);
		final String[] systemnames = getResources().getStringArray(R.array.solar_system_name);
		Arrays.sort(systemnames); // Protection against future non-alphabetic insertion into the system names list
		
		final CharSequence findSystem = ((EditText) dialog.getDialog().findViewById(R.id.screen_chart_find_value)).getText();
		
		if (findSystem.length() == 0)
		{
			dialog.dismiss();
			return;
		}
		
		boolean track = ((CheckBox) dialog.getDialog().findViewById(R.id.screen_chart_find_check)).isChecked();

		if ("Moolah".contentEquals(findSystem))
		{
			showCheatConfirm(new OnConfirmListener() {
						@Override
						public void onConfirm() {
							if (!developerMode) cheated = true;
							credits += 100000;
						}
					});
		}
		else if ("Very rare".contentEquals(findSystem))
		{
			showCheatConfirm(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					if (!developerMode) cheated = true;
					mGameManager.showDialogFragment(VeryRareCheatDialog.newInstance());
				}
			});
		}
		else if ("Cheetah".contentEquals(findSystem))
		{
			showCheatConfirm(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					if (!developerMode) cheated = true;
					cheatCounter = 3;
				}
			});
		}
		else if (findSystem.length() > 3 && "Go ".contentEquals(findSystem.subSequence(0, 3)))
		{
			showCheatConfirm(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					if (!developerMode) cheated = true;
					
					CharSequence findSystemSub = findSystem.subSequence(3, findSystem.length());
					String findName = "";
					for (String name : systemnames) {
						if (name.compareToIgnoreCase(findSystemSub.toString()) >= 0) {
							findName = name;
							break;
						}
					}
					
					SolarSystem goSystem = null;
					for (SolarSystem system : solarSystem) {
						if (system.name.equals(findName)) {
							goSystem = system;
							break;
						}
					}
					
					if (goSystem != null)
					{
						commander().setSystem(goSystem);
						galacticChartSystem = goSystem;
					}
					
				}
			});
		}
		else if ("Quests".contentEquals(findSystem))
		{
			showCheatConfirm(new OnConfirmListener() {
				@Override
				public void onConfirm() {
					if (!developerMode) cheated = true;
					mGameManager.showDialogFragment(QuestsCheatDialog.newInstance());
				}
			});
		}
	
		else if ("Timewarp".contentEquals(findSystem))
		{
			OnConfirmListener loadGame = new OnConfirmListener() {
				@Override
				public void onConfirm() {
					if (mGameManager.loadSnapshot())
					{
						gameLoaded = true;
					}
				}
			};
			if (!gameLoaded)
			{
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.dialog_disablescoring_title,
						R.string.dialog_disablescoring_message,
						R.string.help_disablescoring,
						loadGame,
						null));
			}
			else
			{
				mGameManager.showDialogFragment(ConfirmDialog.newInstance(
						R.string.dialog_reallyload_title,
						R.string.dialog_reallyload_message,
						R.string.help_reallyload,
						loadGame,
						null));
			}
		}
		else
		{
			String findName = "";
			for (String name : systemnames) {
				if (name.compareToIgnoreCase(findSystem.toString()) >= 0) {
					findName = name;
					break;
				}
			}
			
			galacticChartSystem = null;
			for (SolarSystem system : solarSystem) {
				if (system.name.equals(findName)) {
					galacticChartSystem = system;
					break;
				}
			}
			if (galacticChartSystem == null)
			{
				galacticChartSystem = curSystem();
			}
			else if (track)
			{
				trackedSystem = galacticChartSystem;
			}
			
			galacticChartWormhole = false;
		}
		
		mGameManager.getCurrentScreen().onRefreshScreen();
		dialog.dismiss();
	}

	public void showVeryRareCheat() 
	{
		BaseDialog dialog = mGameManager.findDialogByClass(VeryRareCheatDialog.class);

		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_marie)).setChecked((veryRareEncounter & ALREADYMARIE) > 0);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_huie)).setChecked((veryRareEncounter & ALREADYHUIE) > 0);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_ahab)).setChecked((veryRareEncounter & ALREADYAHAB) > 0);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_conrad)).setChecked((veryRareEncounter & ALREADYCONRAD) > 0);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_goodtonic)).setChecked((veryRareEncounter & ALREADYBOTTLEGOOD) > 0);
		((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_badtonic)).setChecked((veryRareEncounter & ALREADYBOTTLEOLD) > 0);

		dialog.setViewTextById(R.id.dialog_veryrarecheat_chances_encounter, R.string.format_number, chanceOfVeryRareEncounter);
		dialog.setViewTextById(R.id.dialog_veryrarecheat_chances_trade, R.string.format_number, chanceOfTradeInOrbit);
	}
	
	public void veryRareCheatHandleEvent()
	{
		BaseDialog dialog = mGameManager.findDialogByClass(VeryRareCheatDialog.class);

		try {
			chanceOfVeryRareEncounter = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_chances_encounter)).getText().toString());
		} 
		catch (NumberFormatException e) {}
		try {
			chanceOfTradeInOrbit = Integer.parseInt(((EditText) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_chances_trade)).getText().toString());
		} 
		catch (NumberFormatException e) {}

		veryRareEncounter = 0;
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_marie)).isChecked())
			veryRareEncounter |= ALREADYMARIE;
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_huie)).isChecked())
			veryRareEncounter |= ALREADYHUIE;
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_ahab)).isChecked())
			veryRareEncounter |= ALREADYAHAB;
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_conrad)).isChecked())
			veryRareEncounter |= ALREADYCONRAD;
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_goodtonic)).isChecked())
			veryRareEncounter |= ALREADYBOTTLEGOOD;
		if (((CheckBox) dialog.getDialog().findViewById(R.id.dialog_veryrarecheat_happened_badtonic)).isChecked())
			veryRareEncounter |= ALREADYBOTTLEOLD;	

		dialog.dismiss();
	}
	
	public void showQuestsCheat()
	{
		BaseDialog dialog = mGameManager.findDialogByClass(QuestsCheatDialog.class);
		
		for (SolarSystem system : solarSystem)
		{
			if (system.special() == null)
				continue;
			
			switch (system.special())
			{
			case DRAGONFLY:
				dialog.setViewTextById(R.id.dialog_questscheat_dragonfly, system.name);
				break;
			case SPACEMONSTER:
				dialog.setViewTextById(R.id.dialog_questscheat_monster, system.name);
				break;
			case JAPORIDISEASE:
				dialog.setViewTextById(R.id.dialog_questscheat_disease, system.name);
				break;
			case ALIENARTIFACT:
				dialog.setViewTextById(R.id.dialog_questscheat_artifact_title, R.string.dialog_questscheat_artifact);
				dialog.setViewTextById(R.id.dialog_questscheat_artifact, system.name);
				break;
			case ARTIFACTDELIVERY:
				if (artifactOnBoard)
				{
					dialog.setViewTextById(R.id.dialog_questscheat_artifact_title, R.string.dialog_questscheat_berger);
					dialog.setViewTextById(R.id.dialog_questscheat_artifact, system.name);
				}
				break;
			case TRIBBLE:
				dialog.setViewTextById(R.id.dialog_questscheat_tribbles, system.name);
				break;
			case GETREACTOR:
				dialog.setViewTextById(R.id.dialog_questscheat_reactor, system.name);
				break;
			case AMBASSADORJAREK:
				dialog.setViewTextById(R.id.dialog_questscheat_jarek, system.name);
				break;
			case ALIENINVASION:
				dialog.setViewTextById(R.id.dialog_questscheat_invasion, system.name);
				break;
			case EXPERIMENT:
				dialog.setViewTextById(R.id.dialog_questscheat_experiment, system.name);
				break;
			case TRANSPORTWILD:
				dialog.setViewTextById(R.id.dialog_questscheat_wild, system.name);
				break;
			case SCARAB:
				dialog.setViewTextById(R.id.dialog_questscheat_scarab, system.name);
				break;
			case SCARABDESTROYED:
				if (scarabStatus > 0 && scarabStatus < 2)
				{
					dialog.setViewTextById(R.id.dialog_questscheat_scarab, system.name);
				}
				break;
			default:
				break;
			}
		}
	}
	/*
	 * NB End adapted code
	 */
	
	
	// New function for volume key handling (formally inside of event handlers for Chart screen and Warp subscreens
	public boolean scrollSystem(boolean down) {
		BaseScreen screen = mGameManager.getCurrentScreen();
		if (screen == null) return false;
		ScreenType type = screen.getType();

		switch (type) {
		case TARGET:
		case AVGPRICES:
			screen.getView().findViewById(down? R.id.screen_warp_prev : R.id.screen_warp_next).performClick();
			return true;
		case CHART:
			int current = 0;
			for (int i = 0; i < solarSystem.length; i++) {
				if (solarSystem[i] == galacticChartSystem) {
					current = i;
					break;
				}
			}
			
			current += (down? 1 : -1);
			if (current >= solarSystem.length) current = 0;
			if (current < 0) current = solarSystem.length - 1;
			
			galacticChartSystem = solarSystem[current];
			galacticChartWormhole = false;
			screen.onRefreshScreen();
			
			return true;
		}
		
		
		
		return false;
	}
	
	private String marieSystem;
	// Help dialog building takes place here so it can see special system names
	public String getHelpText(int resId) {
		if (resId > 0) {
			
			String message;
			
			// Some special treatment of certain help texts which need to change based on variable system names
			switch (resId) {
			// Nix
			case R.string.help_reactoronboard:
			case R.string.help_cantsellshipwithreactor:
				message = getResources().getString(resId, solarSystem[nix]);
				break;
				
			// Japori
			case R.string.help_antidote:
			case R.string.help_antidotedestroyed:
			case R.string.help_antidoteremoved:
				message = getResources().getString(resId, solarSystem[japori]);
				break;
				
			// Originally, Marie Celeste had always recently visited Lowry, but just for fun we'll randomize that too. Wonder if anyone will notice...
			case R.string.help_lootmarieceleste:
				if (!randomQuestSystems) {
					marieSystem = getResources().getString(R.string.solarsystem_lowry);
				} else if (marieSystem == null) {
					marieSystem = getRandom(solarSystem).name;
				}
				message = getResources().getString(resId, marieSystem);
				break;
				
			default:
				message = getResources().getString(resId);
			}

			return message;
		}
		return "";
	}
	
	
	// Populating PagerAdapters for Warp subscreens
	public void setAdapterSystems(WarpSystemPagerAdapter adapter) {
		SolarSystem next = nextSystemWithinRange(warpSystem, false);
		SolarSystem prev = nextSystemWithinRange(warpSystem, true);
		if (next == null || prev == null) {
			next = prev = warpSystem;
		}
		adapter.setSystems(warpSystem, prev, next);
	}
	

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private boolean isRtl() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
	}

}
