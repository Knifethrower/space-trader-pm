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
import com.brucelet.spacetrader.enumtypes.TradeItem;
import com.brucelet.spacetrader.enumtypes.Weapon;

import com.brucelet.spacetrader.platform.AsyncTask;
import com.brucelet.spacetrader.platform.Handler;
import com.brucelet.spacetrader.ui.*;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.brucelet.spacetrader.platform.Resources;
import com.brucelet.spacetrader.platform.SharedPreferences;
import com.brucelet.spacetrader.R;
public class GameState {

	public static final String LOG_TAG = "Space Trader";

	private static final Random rng = new Random();
	
	// Debt Control
	private static final int DEBTWARNING= 75000;
	private static final int DEBTTOOLARGE= 100000;
	
	public final CrewMember[] mercenary = new CrewMember[31];
	public final SolarSystem[] solarSystem = new SolarSystem[120];
	public final SolarSystem[] wormhole = new SolarSystem[6];
	
	// The following globals are saved between sessions
	// Note that these initializations are overruled by the StartNewGame function
	public int credits = 1000;            // Current credits owned
	public int debt    = 0;               // Current Debt
	public Map<TradeItem, Integer> buyPrice = new EnumMap<>(TradeItem.class);    // Price list current system
	public Map<TradeItem, Integer> buyingPrice = new EnumMap<>(TradeItem.class); // Total price paid for trade goods
	public Map<TradeItem, Integer> sellPrice = new EnumMap<>(TradeItem.class);   // Price list current system
	public Map<ShipType, Integer> shipPrice = new EnumMap<>(ShipType.class);      // Price list current system (recalculate when buy ship screen is entered)
	public int policeKills = 0;           // Number of police ships killed
	public int traderKills = 0;           // Number of trader ships killed
	public int pirateKills = 0;           // Number of pirate ships killed
	public int policeRecordScore = 0;     // 0 = Clean record
	public int reputationScore = 0;       // 0 = Harmless
	public int monsterHull = 500;         // Hull strength of monster

	public int days = 0;                   // Number of days playing
	public SolarSystem warpSystem = null;             // Target system for warp
	public ShipType selectedShipType = null;       // Selected Ship type for Shiptype Info screen
	public int cheatCounter = 0;
	public SolarSystem galacticChartSystem = null;    // Current system on Galactic chart
	public boolean galacticChartWormhole = false;
	public Encounter encounterType = null;          // Type of current encounter
	public int curForm = 0;                // Form to return to
	public int noClaim = 0;                // Days of No-Claim
	public int leaveEmpty = 0;             // Number of cargo bays to leave empty when buying goods
	public int newsSpecialEventCount = 0;  // Simplifies tracking what Quests have just been initiated or completed for the News System. This is not important enough to get saved.
	public SolarSystem trackedSystem = null;			// The short-range chart will display an arrow towards this system if the value is not -1

	public int shortcut1 = 0;				// default shortcut 1 = Buy Cargo
	public int shortcut2 = 1;				// default shortcut 2 = Sell Cargo
	public int shortcut3 = 2;				// default shortcut 3 = Shipyard
	public int shortcut4 = 10;				// default shortcut 4 = Short Range Warp

	
	// the next two values are NOT saved between sessions -- they can only be changed via cheats.
	public int chanceOfVeryRareEncounter	= CHANCEOFVERYRAREENCOUNTER;
	public int chanceOfTradeInOrbit		= CHANCEOFTRADEINORBIT;

	public int monsterStatus = 0;       // 0 = Space monster isn't available, 1 = Space monster is in Acamar system, 2 = Space monster is destroyed
	public int dragonflyStatus = 0;     // 0 = Dragonfly not available, 1 = Go to Baratas, 2 = Go to Melina, 3 = Go to Regulas, 4 = Go to Zalkon, 5 = Dragonfly destroyed
	public int japoriDiseaseStatus = 0; // 0 = No disease, 1 = Go to Japori (always at least 10 medicine cannisters), 2 = Assignment finished or canceled
	public DifficultyLevel difficulty = DifficultyLevel.NORMAL;     // Difficulty level
	public int jarekStatus = 0;         // Ambassador Jarek 0=not delivered; 1=on board; 2=delivered
	public int invasionStatus = 0;      // Status Alien invasion of Gemulon; 0=not given yet; 1-7=days from start; 8=too late
	public int experimentStatus = 0;    // Experiment; 0=not given yet,1-11 days from start; 12=performed, 13=cancelled
	public int fabricRipProbability = 0; // if Experiment = 8, this is the probability of being warped to a random planet.
	public int veryRareEncounter = 0;     // bit map for which Very Rare Encounter(s) have taken place (see traveler.c, around line 1850)
	public int wildStatus = 0;			// Jonathan Wild: 0=not delivered; 1=on board; 2=delivered
	public int reactorStatus = 0;			// Unstable Reactor Status: 0=not encountered; 1-20=days of mission (bays of fuel left = 10 - (ReactorStatus/2); 21=delivered
	public int scarabStatus = 0;		// Scarab: 0=not given yet, 1=not destroyed, 2=destroyed, upgrade not performed, 3=destroyed, hull upgrade performed

	public boolean autoFuel = false;            // Automatically get a full tank when arriving in a new system
	public boolean autoRepair = false;          // Automatically get a full hull repair when arriving in a new system
	public int clicks = 0;                  // Distance from target system, 0 = arrived
	public boolean raided = false;              // True when the commander has been raided during the trip
	public boolean inspected = false;           // True when the commander has been inspected during the trip
	public boolean moonBought = false;          // Indicates whether a moon is available at Utopia
	public boolean escapePod = false;           // Escape Pod in ship
	public boolean insurance = false;           // Insurance bought
	public boolean alwaysIgnoreTraders = false; // Automatically ignores traders when it is safe to do so
	public boolean alwaysIgnorePolice = true;   // Automatically ignores police when it is safe to do so
	public boolean alwaysIgnorePirates = false; // Automatically ignores pirates when it is safe to do so
	public boolean alwaysIgnoreTradeInOrbit = false; // Automatically ignores Trade in Orbit when it is safe to do so
	public boolean artifactOnBoard = false;     // Alien artifact on board
	public boolean reserveMoney = false;        // Keep enough money for insurance and mercenaries
	public boolean priceDifferences = false;    // Show price differences instead of absolute prices
	public boolean aplScreen = false;           // Is true is the APL screen was last shown after the SRC
	public boolean tribbleMessage = false;      // Is true if the Ship Yard on the current system informed you about the tribbles
	public boolean alwaysInfo = false;          // Will always go from SRC to Info
	public boolean textualEncounters = false;   // Show encounters as text.
	public boolean graphicalEncounters = true;   // NB this is new, because we might have both textual and graphical encounters on at once.
	public volatile boolean autoAttack = false;			 // Auto-attack mode
	public volatile boolean autoFlee = false;			 // Auto-flee mode
	public boolean continuous = false;			 // Continuous attack/flee mode
	public boolean attackIconStatus = false;	 // Show Attack Star or not
	public boolean attackFleeing = false;		 // Continue attack on fleeing ship
	public boolean possibleToGoThroughRip = false;	// if Dr Fehler's experiment happened, we can only go through one space-time rip per warp.
	public boolean useHWButtons = false;		// by default, don't use Hardware W buttons
	public boolean newsAutoPay = false;		// by default, ask each time someone buys a newspaper
	public boolean showTrackedRange = true;	// display range when tracking a system on Short Range Chart
	public boolean justLootedMarie = false;		// flag to indicate whether player looted Marie Celeste
	public boolean arrivedViaWormhole = false;	// flag to indicate whether player arrived on current planet via wormhole
	public boolean alreadyPaidForNewspaper = false; // once you buy a paper on a system, you don't have to pay again.
	public boolean trackAutoOff = true;		// Automatically stop tracking a system when you get to it?
	public boolean remindLoans = true;			// remind you every five days about outstanding loan balances
	public boolean canSuperWarp = false;		// Do you have the Portable Singularity on board?
	public boolean gameLoaded = false;			// Indicates whether a game is loaded
	public boolean cheated = false;			// Indicates whether a cheat has been used
	public boolean litterWarning = false;		// Warning against littering has been issued.
	public boolean sharePreferences = true;	// Share preferences between switched games.
	public boolean identifyStartup = false;	// Identify commander at game start
	public boolean rectangularButtonsOn = false; // Indicates on OS 5.0 and higher whether rectangular buttons should be used.		

	public final HighScore[] hScores = new HighScore[3];
	private final NewsEvent[] newsEvents = new NewsEvent[MAXSPECIALNEWSEVENTS];

	public int acamar = -1;
	public int baratas = -1;
	public int daled = -1;
	public int devidia = -1;
	public int gemulon = -1;
	public int japori = -1;
	public int kravat = -1;
	public int melina = -1;
	public int nix = -1;
	public int og = -1;
	public int regulas = -1;
	public int sol = -1;
	public int utopia = -1;
	public int zalkon = -1;

		
	public Ship ship;
	public Ship opponent;
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
	public boolean commanderGotHit;
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


	public static final int GALAXYWIDTH = 150;
	public static final int GALAXYHEIGHT = 110;
	public static final int SHORTRANGEWIDTH = 140;
	public static final int SHORTRANGEHEIGHT = 140;
	public static final int SHORTRANGEBOUNDSX = 10;
	public static final int BOUNDSX = 5;
	public static final int BOUNDSY = 20;
	public static final int MINDISTANCE = 6;
	public static final int CLOSEDISTANCE = 13;
	public static final int WORMHOLEDISTANCE = 3;
	public static final int EXTRAERASE = 3;
	public static final int COSTMOON = 500000;
	public static final int CHANCEOFVERYRAREENCOUNTER = 5;
	public static final int CHANCEOFTRADEINORBIT = 100;

	public static final int MAXSKILL = 10;
	public static final int MAXLOAN = 25000;

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


	public static final boolean DEVELOPER_MODE = false;

	private GameUI ui = GameUI.NONE;

	public void setUI(GameUI ui) { this.ui = ui; }

	/** Stops any auto attack/flee. */
	public void clearButtonAction() {
		autoAttack = false;
		autoFlee = false;
	}

	public void showEndGameScreen(EndStatus endStatus)
	{
		this.endStatus = endStatus;
		ui.setScreen(ScreenType.ENDGAME);
	}

	public EndStatus endStatus() { return endStatus; }

	// ---- Frontend-facing API (added for the port; not in the Android original) ----

	public GameUI ui() { return ui; }

	public DifficultyLevel difficulty() { return difficulty; }

	/**
	 * Begin a new game. Skill points must total at most 2 * MAXSKILL (the new-game dialog enforces the same rule).
	 * Mirrors the confirm branch of the original newCommanderFormHandleEvent.
	 */
	public void newGame(String name, int pilot, int fighter, int trader, int engineer, DifficultyLevel level, boolean randomQuestSystems) {
		difficulty = level;
		mercenary[0] = new CrewMember(name, pilot, fighter, trader, engineer, this);
		this.randomQuestSystems = randomQuestSystems;
		startNewGame();
		determinePrices(curSystem());

		if (difficulty.compareTo(DifficultyLevel.NORMAL) < 0)
			if (curSystem().special() == null)
				curSystem().setSpecial(SpecialEvent.LOTTERYWINNER);

		ui.setScreen(ScreenType.INFO);
		ui.clearBackStack();
		ui.autosave();
	}

	/** Set the warp target and begin the trip (fuel, days, interest, then the encounter loop). */
	public boolean warpTo(SolarSystem target, boolean viaSingularity) {
		warpSystem = target;
		return doWarp(viaSingularity);
	}

	public SolarSystem[] solarSystems() { return solarSystem; }

	public SolarSystem[] wormholes() { return wormhole; }

	public SolarSystem currentSystem() { return curSystem(); }

	public CrewMember commanderMember() { return commander(); }

	// ---- Encounter presentation helpers ----

	/** Call when the encounter screen is first shown, before reading the texts below. */
	public void encounterScreenEntered() {
		prevEncounterAction = null;
	}

	/** Opening description of an encounter (first-display text). */
	public String encounterInitialDescription() {
		if (encounterType == Encounter.VeryRare.POSTMARIEPOLICE)
			return getResources().getString(R.string.screen_encounter_description_customs);
		String opponentType = encounterType.opponentType().toXmlStringInit(getResources());
		if (opponent.type == ShipType.MANTIS) {
			opponentType = Opponent.MANTIS.toXmlStringInit(getResources());
		}
		String opponentShip = opponent.type.toXmlString(getResources()).toLowerCase(Locale.getDefault());
		if (encounterType == Encounter.VeryRare.MARIECELESTE) {
			opponentShip = getResources().getString(R.string.opponent_ship);
		} else if (encounterType.opponentType() == Opponent.FAMOUSCAPTAIN) {
			if (encounterType == Encounter.VeryRare.CAPTAINAHAB) {
				opponentType = getResources().getString(R.string.opponent_initial_famouscaptain);
				opponentShip = getResources().getString(R.string.opponent_ahab);
			}
			if (encounterType == Encounter.VeryRare.CAPTAINCONRAD) {
				opponentShip = getResources().getString(R.string.opponent_conrad);
			}
			if (encounterType == Encounter.VeryRare.CAPTAINHUIE) {
				opponentShip = getResources().getString(R.string.opponent_huie);
			}
		}
		return getResources().getQuantityString(R.plurals.screen_encounter_description_initial, clicks, clicks, warpSystem, opponentType, opponentShip);
	}

	/** What the opponent is about to do, for the encounter screen. */
	public String encounterNextAction(boolean firstDisplay) {
		if (firstDisplay && encounterType == Encounter.Police.ATTACK && policeRecordScore > PoliceRecord.CRIMINAL.score) {
			return getResources().getString(R.string.opponentaction_hailsurrender);
		} else if (encounterType.action() == OpponentAction.IGNORE && ship.cloaked(opponent)) {
			return getResources().getString(R.string.opponentaction_cloaked);
		}
		return encounterType.action().toXmlString(getResources());
	}

	public Ship playerShip() { return ship; }

	public Ship opponentShip() { return opponent; }

	public int tribbleCount() { return ship.tribbles; }

	public boolean autoAttackOn() { return autoAttack || autoFlee; }

	// ---- Plunder / jettison ----

	/** Ask how many of an opponent's cargo item to plunder. */
	public void plunderPick(TradeItem item) {
		if (opponent.getCargo(item) <= 0)
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_notavailable_title, R.string.dialog_plunder_nothing, R.string.help_victimdoesnthaveany));
		else
			getAmountToPlunder(item);
	}

	public void plunderAll(TradeItem item) { plunderCargo(item, 999); }

	/** Finished plundering: continue the trip. */
	public void plunderDone() {
		if (encounterType == Encounter.VeryRare.MARIECELESTE && ship.getCargo(TradeItem.NARCOTICS) > narcs)
			justLootedMarie = true;
		travel();
	}

	public void dumpPick(TradeItem item) {
		if (ship.getCargo(item) <= 0)
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
		else
			getAmountToSell(item, SellOperation.JETTISON);
	}

	public void dumpAll(final TradeItem item) {
		if (ship.getCargo(item) <= 0) {
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
			return;
		}
		ui.showDialog(ConfirmDialog.newInstance(
				R.string.dialog_dumpall_title,
				R.string.dialog_dumpall_message,
				R.string.help_dumpall,
				new OnConfirmListener() {
					@Override
					public void onConfirm() {
						sellCargo(item, 999, SellOperation.JETTISON);
					}
				},
				null,
				item,
				buyingPrice.get(item)));
	}

	// ---- Simple status accessors ----

	public int credits() { return credits; }
	public int debt() { return debt; }
	public int days() { return days; }
	public int buyPriceOf(TradeItem item) { return buyPrice.get(item); }
	public int sellPriceOf(TradeItem item) { return sellPrice.get(item); }
	public int policeRecord() { return policeRecordScore; }
	public int reputation() { return reputationScore; }

	// ---- Persistence (single save slot, properties file) ----

	/** Writes the whole game to prefs; returns false if the file could not be written. */
	public boolean save(SharedPreferences prefs) {
		SharedPreferences.Editor e = prefs.edit();
		e.clear(); // drop keys of earlier states (e.g. endStatus from a finished game, opponent from an old encounter)
		saveState(e);
		return e.commit();
	}

	public void load(SharedPreferences prefs) {
		loadState(prefs);
	}

	// ---- Bank ----

	public int maxLoanAmount() { return maxLoan(); }
	public int noClaimDays() { return noClaim; }
	public boolean hasInsurance() { return insurance; }
	public boolean hasEscapePod() { return escapePod; }

	public void bankGetLoan() {
		if (debt >= maxLoan()) {
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_bank_loan_toohigh_title, R.string.screen_bank_loan_toohigh_message, R.string.help_debttoohigh));
			return;
		}
		ui.showDialog(InputDialog.newInstance(
				R.string.screen_bank_loan_get_title,
				R.string.screen_bank_loan_get_message,
				R.string.generic_ok,
				R.string.generic_maximum,
				R.string.generic_nothing,
				R.string.help_getloan,
				new InputDialog.OnPositiveListener() {
					@Override public void onClickPositiveButton(int value) { getLoan(value); ui.stateChanged(); }
				},
				new InputDialog.OnNeutralListener() {
					@Override public void onClickNeutralButton() { getLoan(MAXLOAN); ui.stateChanged(); }
				},
				maxLoan() - debt));
	}

	public void bankPayBack() {
		if (debt <= 0) {
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_bank_loan_nodebt_title, R.string.screen_bank_loan_nodebt_message, R.string.help_nodebt));
			return;
		}
		ui.showDialog(InputDialog.newInstance(
				R.string.screen_bank_loan_pay_title,
				R.string.screen_bank_loan_pay_message,
				R.string.generic_ok,
				R.string.generic_everything,
				R.string.generic_nothing,
				R.string.help_payback,
				new InputDialog.OnPositiveListener() {
					@Override public void onClickPositiveButton(int value) { payBack(value); ui.stateChanged(); }
				},
				new InputDialog.OnNeutralListener() {
					@Override public void onClickNeutralButton() { payBack(debt); ui.stateChanged(); }
				},
				debt));
	}

	public void bankToggleInsurance() {
		if (!insurance) {
			if (!escapePod) {
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_bank_ins_nopod, R.string.screen_bank_ins_useless, R.string.help_noescapepod, false));
				return;
			}
			insurance = true;
		} else {
			ui.showDialog(ConfirmDialog.newInstance(
					R.string.screen_bank_ins_stop,
					R.string.screen_bank_ins_stopquery,
					R.string.help_stopinsurance,
					new OnConfirmListener() {
						@Override public void onConfirm() { insurance = false; noClaim = 0; ui.stateChanged(); }
					},
					null,
					false));
		}
	}

	// ---- Equipment ----

	public int equipmentBuyPrice(Purchasable item) {
		return item.buyPrice(curSystem().techLevel(), ship.skill(Skill.TRADER));
	}

	public void buyEquipment(Purchasable item) {
		if (item instanceof Weapon) {
			buyItem(ship.type.weaponSlots, ship.weapon, equipmentBuyPrice(item), item.toXmlString(getResources()), item);
		} else if (item instanceof Shield) {
			buyItem(ship.type.shieldSlots, ship.shield, equipmentBuyPrice(item), item.toXmlString(getResources()), item);
		} else if (item instanceof Gadget) {
			if (ship.hasGadget((Gadget) item) && Gadget.EXTRABAYS != item)
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_notuseful, R.string.screen_buyeq_dialog_notuseful_message, R.string.help_nomoreofitem));
			else
				buyItem(ship.type.gadgetSlots, ship.gadget, equipmentBuyPrice(item), item.toXmlString(getResources()), item);
		}
	}

	/** Sell the equipment in slot {@code index} of the given kind (0 = weapon, 1 = shield, 2 = gadget). */
	public void sellEquipment(final int kind, final int index) {
		ui.showDialog(ConfirmDialog.newInstance(
				R.string.generic_sell,
				R.string.screen_selleq_sellquery,
				R.string.help_sellitem,
				new OnConfirmListener() {
					@Override
					public void onConfirm() {
						if (kind == 0) {
							credits += ship.weapon[index].sellPrice();
							for (int i = index + 1; i < ship.weapon.length; i++) ship.weapon[i - 1] = ship.weapon[i];
							ship.weapon[ship.weapon.length - 1] = null;
						} else if (kind == 1) {
							credits += ship.shield[index].sellPrice();
							for (int i = index + 1; i < ship.shield.length; i++) {
								ship.shield[i - 1] = ship.shield[i];
								ship.shieldStrength[i - 1] = ship.shieldStrength[i];
							}
							ship.shield[ship.shield.length - 1] = null;
							ship.shieldStrength[ship.shieldStrength.length - 1] = 0;
						} else {
							if (ship.gadget[index] == Gadget.EXTRABAYS && ship.filledCargoBays() > ship.totalCargoBays() - 5) {
								ui.showDialog(SimpleDialog.newInstance(R.string.screen_selleq_cargobaysfull_title, R.string.screen_selleq_cargobaysfull_message, R.string.help_cargobaysfull));
								return;
							}
							credits += ship.gadget[index].sellPrice();
							for (int i = index + 1; i < ship.gadget.length; i++) ship.gadget[i - 1] = ship.gadget[i];
							ship.gadget[ship.gadget.length - 1] = null;
						}
						ui.stateChanged();
					}
				},
				null));
	}

	// ---- Shipyard ----

	public void yardFuel(boolean full) {
		if (full) buyFuel(ship.getFuelTanks() * ship.type.costOfFuel);
		else getAmountForFuel();
	}

	public void yardRepair(boolean full) {
		if (full) buyRepairs(ship.getHullStrength() * ship.type.repairCosts);
		else getAmountForRepairs();
	}

	public boolean canBuyPod() {
		return !(escapePod || toSpend() < 2000 || curSystem().techLevel().compareTo(ShipType.values()[0].minTechLevel) < 0);
	}

	public void yardBuyPod() {
		ui.showDialog(ConfirmDialog.newInstance(
				R.string.screen_yard_buypod_title,
				R.string.screen_yard_buypod_query,
				R.string.help_buyescapepod,
				new OnConfirmListener() {
					@Override public void onConfirm() { escapePod = true; credits -= 2000; ui.stateChanged(); }
				}, null));
	}

	/** Called when the buy-ship screen opens: refreshes prices and shows the tribble warning once. */
	public void enterBuyShip() {
		determineShipPrices();
		if (ship.tribbles > 0 && !tribbleMessage) {
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_yard_buyship_tribbles_title, R.string.screen_yard_buyship_tribbles_message, R.string.help_shipnotworthmuch));
			tribbleMessage = true;
		}
	}

	public int shipPriceOf(ShipType type) { return shipPrice.get(type); }

	public void showShipInfo(ShipType type) {
		selectedShipType = type;
		ui.showDialog(ShipInfoDialog.newInstance());
	}

	public ShipType selectedShip() { return selectedShipType; }

	public void buyShipType(ShipType type) {
		selectedShipType = type;
		new BuyShipTask().execute();
	}

	// ---- System information / special events ----

	/** Set by systemInformationEntered(): whether the special-event button should be offered here. */
	public boolean specialAvailable;

	/** Call whenever the docked system screen is (re)entered; records news and marks the system visited. */
	public void systemInformationEntered() { drawSystemInformationForm(); }

	public boolean mercenaryForHire() { return getForHire() != null; }

	private String specialEventSystemName() {
		switch (curSystem().special()) {
		case FLYBARATAS: return solarSystem[melina].name;
		case FLYMELINA: return solarSystem[regulas].name;
		case FLYREGULAS: return solarSystem[zalkon].name;
		case MOONBOUGHT:
		case MOONFORSALE: return solarSystem[utopia].name;
		case SPACEMONSTER: return solarSystem[acamar].name;
		case DRAGONFLY: return solarSystem[baratas].name;
		case JAPORIDISEASE: return solarSystem[japori].name;
		case AMBASSADORJAREK:
		case JAREKGETSOUT: return solarSystem[devidia].name;
		case ALIENINVASION:
		case GEMULONINVADED:
		case GEMULONRESCUED: return solarSystem[gemulon].name;
		case EXPERIMENT: return solarSystem[daled].name;
		case TRANSPORTWILD: return solarSystem[kravat].name;
		case GETREACTOR: return solarSystem[nix].name;
		default: return null;
		}
	}

	public String specialEventTitle() {
		String sys = specialEventSystemName();
		return sys == null ? getResources().getString(curSystem().special().titleId) : getResources().getString(curSystem().special().titleId, sys);
	}

	public String specialEventMessage() {
		String sys = specialEventSystemName();
		return sys == null ? getResources().getString(curSystem().special().questStringId) : getResources().getString(curSystem().special().questStringId, sys);
	}

	public boolean specialEventIsMessage() { return curSystem().special().justAMessage; }

	/** Show the special event of the current system as a dialog (yes/no or plain message). */
	public void showSpecialEvent() { ui.showDialog(SpecialEventDialog.newInstance()); }

	/** The player accepted the special event (or dismissed a plain message). */
	public void specialEventAccept() { specialEventFormHandleEvent(0); }

	// ---- Newspaper ----

	public void readNewspaper() {
		final int price = difficulty.ordinal() + 1;
		if (!alreadyPaidForNewspaper && toSpend() < price) {
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_info_cantaffordpaper_title, R.string.screen_info_cantaffordpaper_message, R.string.help_cantbuynewspaper));
			return;
		}
		OnConfirmListener read = new OnConfirmListener() {
			@Override
			public void onConfirm() {
				if (!alreadyPaidForNewspaper) {
					credits -= price;
					alreadyPaidForNewspaper = true;
				}
				ui.showDialog(NewspaperDialog.newInstance());
			}
		};
		if (!newsAutoPay && !alreadyPaidForNewspaper)
			ui.showDialog(ConfirmDialog.newInstance(
					R.string.screen_info_buynewspaper_title,
					R.string.screen_info_buynewspaper_message,
					R.string.screen_info_buynewspaper_pos,
					R.string.screen_info_buynewspaper_neg,
					R.string.help_buypaper,
					read,
					null,
					price));
		else read.onConfirm();
	}

	// ---- Personnel ----

	public String crewSlotText(int slot) {
		if (slot == 1) {
			if (ship.type.crewQuarters == 3 && jarekStatus == 1 && wildStatus == 1) return getResources().getString(R.string.screen_personnel_wild);
			if (ship.type.crewQuarters == 2 && (jarekStatus == 1 || wildStatus == 1))
				return getResources().getString(jarekStatus == 1 ? R.string.screen_personnel_jarek : R.string.screen_personnel_wild);
			if (ship.type.crewQuarters <= 1) return getResources().getString(R.string.screen_personnel_noquarters);
			if (ship.crew[1] == null) return getResources().getString(R.string.screen_personnel_vacancy);
			return crewStats(ship.crew[1]);
		}
		if (ship.type.crewQuarters == 3 && (jarekStatus == 1 || wildStatus == 1))
			return getResources().getString(jarekStatus == 1 ? R.string.screen_personnel_jarek : R.string.screen_personnel_wild);
		if (ship.type.crewQuarters <= 2) return getResources().getString(R.string.screen_personnel_noquarters);
		if (ship.crew[2] == null) return getResources().getString(R.string.screen_personnel_vacancy);
		return crewStats(ship.crew[2]);
	}

	/** True when the given crew slot (1 or 2) holds a real hired crew member who can be fired. */
	public boolean crewSlotHasMercenary(int slot) {
		return ship.crew[slot] != null && crewSlotText(slot).equals(crewStats(ship.crew[slot]));
	}

	public String crewStats(CrewMember c) {
		return c.name + " - " + getResources().getString(R.string.format_dailycost, c.hirePrice()) + " - "
				+ getResources().getString(R.string.screen_personnel_pilot, c.pilot()) + ", "
				+ getResources().getString(R.string.screen_personnel_fighter, c.fighter()) + ", "
				+ getResources().getString(R.string.screen_personnel_trader, c.trader()) + ", "
				+ getResources().getString(R.string.screen_personnel_engineer, c.engineer());
	}

	public String hireCandidateText() {
		CrewMember c = getForHire();
		return c == null ? null : crewStats(c);
	}

	public void personnelFire(final int slot) {
		final int oldTraderSkill = ship.skill(Skill.TRADER);
		ui.showDialog(ConfirmDialog.newInstance(
				R.string.screen_personnel_firemercenary_title,
				R.string.screen_personnel_firemercenary_message,
				R.string.help_firemercenary,
				new OnConfirmListener() {
					@Override
					public void onConfirm() {
						if (slot == 1) ship.crew[1] = ship.crew[2];
						ship.crew[2] = null;
						if (oldTraderSkill != ship.skill(Skill.TRADER)) recalculateBuyPrices(curSystem());
						ui.stateChanged();
					}
				},
				null,
				ship.crew[slot]));
	}

	public void personnelHire() {
		int oldTraderSkill = ship.skill(Skill.TRADER);
		CrewMember forHire = getForHire();
		if (forHire == null) return;
		int firstFree = -1;
		if (ship.crew[1] == null) firstFree = 1;
		else if (ship.crew[2] == null) firstFree = 2;
		if (firstFree < 0 || ship.availableQuarters() <= firstFree) {
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_personnel_dialog_noquarters, R.string.screen_personnel_dialog_noquarters_message, R.string.help_nofreequarters));
		} else {
			ship.crew[firstFree] = forHire;
			if (oldTraderSkill != ship.skill(Skill.TRADER)) recalculateBuyPrices(curSystem());
			ui.stateChanged();
		}
	}

	// ---- Quests and special cargo (text versions of the status sub-screens) ----

	public java.util.List<String> questLines() {
		java.util.List<String> out = new java.util.ArrayList<>();
		Resources r = getResources();
		if (monsterStatus == 1) out.add(r.getString(R.string.screen_status_quests_monster, solarSystem[acamar]));
		if ((dragonflyStatus >= 1 && dragonflyStatus <= 4) || solarSystem[zalkon].special() == SpecialEvent.INSTALLLIGHTNINGSHIELD) {
			switch (dragonflyStatus) {
			case 1: out.add(r.getString(R.string.screen_status_quests_dragonfly, solarSystem[baratas])); break;
			case 2: out.add(r.getString(R.string.screen_status_quests_dragonfly, solarSystem[melina])); break;
			case 3: out.add(r.getString(R.string.screen_status_quests_dragonfly, solarSystem[regulas])); break;
			case 4: out.add(r.getString(R.string.screen_status_quests_dragonfly, solarSystem[zalkon])); break;
			default: out.add(r.getString(R.string.screen_status_quests_lightningshield, solarSystem[zalkon])); break;
			}
		}
		if (japoriDiseaseStatus == 1) out.add(r.getString(R.string.screen_status_quests_disease, solarSystem[japori]));
		if (artifactOnBoard) out.add(r.getString(R.string.screen_status_quests_artifact));
		if (wildStatus == 1) out.add(r.getString(R.string.screen_status_quests_wild, solarSystem[kravat]));
		if (jarekStatus == 1) out.add(r.getString(R.string.screen_status_quests_jarek, solarSystem[devidia]));
		if (invasionStatus >= 1 && invasionStatus < 7) {
			int d = 7 - invasionStatus;
			out.add(r.getQuantityString(R.plurals.screen_status_quests_invasion, d, d, solarSystem[gemulon]));
		} else if (solarSystem[gemulon].special() == SpecialEvent.GETFUELCOMPACTOR) {
			out.add(r.getString(R.string.screen_status_quests_fuelcompactor, solarSystem[gemulon]));
		}
		if (experimentStatus >= 1 && experimentStatus < 11) {
			int d = 11 - experimentStatus;
			out.add(r.getQuantityString(R.plurals.screen_status_quests_experiment, d, d, solarSystem[daled]));
		}
		if (solarSystem[nix].special() == SpecialEvent.GETSPECIALLASER) {
			out.add(r.getString(R.string.screen_status_quests_speciallaser, solarSystem[nix]));
		} else if (reactorStatus >= 1 && reactorStatus < 21) {
			out.add(r.getString(reactorStatus < 2 ? R.string.screen_status_quests_reactor : R.string.screen_status_quests_reactor2, solarSystem[nix]));
		}
		if (scarabStatus == 1) out.add(r.getString(R.string.screen_status_quests_scarab));
		if (ship.tribbles > 0) out.add(r.getString(R.string.screen_status_quests_tribbles));
		if (moonBought) out.add(r.getString(R.string.screen_status_quests_moon, solarSystem[utopia]));
		if (out.isEmpty()) out.add(r.getString(R.string.screen_status_quests_default));
		return out;
	}

	public java.util.List<String> specialCargoLines() {
		java.util.List<String> out = new java.util.ArrayList<>();
		Resources r = getResources();
		if (ship.tribbles > 0) {
			if (ship.tribbles >= MAXTRIBBLES) out.add(r.getString(R.string.screen_status_cargo_manytribbles));
			else out.add(r.getQuantityString(R.plurals.screen_status_cargo_tribbles, ship.tribbles, ship.tribbles));
		}
		if (japoriDiseaseStatus == 1) out.add(r.getString(R.string.screen_status_cargo_antidote));
		if (artifactOnBoard) out.add(r.getString(R.string.screen_status_cargo_artifact));
		if (jarekStatus == 2) out.add(r.getString(R.string.screen_status_cargo_hagglingcomputer));
		if (reactorStatus > 0 && reactorStatus < 21) {
			out.add(r.getString(R.string.screen_status_cargo_reactor));
			int fuel = 10 - ((reactorStatus - 1) / 2);
			out.add(r.getQuantityString(R.plurals.screen_status_cargo_reactorfuel, fuel, fuel));
		}
		if (canSuperWarp) out.add(r.getString(R.string.screen_status_cargo_singularity));
		if (out.isEmpty()) out.add(r.getString(R.string.screen_status_cargo_default));
		return out;
	}

	// ---- Retiring, game over, high scores ----

	public boolean isGameOver() { return endStatus != null; }

	/** True when a game (not yet over) is loaded or in progress. */
	public boolean hasActiveGame() { return ship != null && endStatus == null; }

	public void retire() {
		ui.showDialog(ConfirmDialog.newInstance(
				R.string.dialog_retire_title,
				R.string.dialog_retire_message,
				R.string.help_retire,
				new OnConfirmListener() {
					@Override public void onConfirm() { showEndGameScreen(EndStatus.RETIRED); }
				}, null));
	}

	public java.util.List<String> highScoreLines() {
		java.util.List<String> out = new java.util.ArrayList<>();
		Resources r = getResources();
		for (int i = 0; i < hScores.length; i++) {
			if (hScores[i] == null) continue; // unused slots are simply not listed
			HighScore h = hScores[i];
			int score = h.score;
			out.add((i + 1) + ". " + h.name + "  " + r.getString(R.string.dialog_highscores_percent, score / 50, (score % 50) / 5));
			out.add("    " + r.getQuantityString(R.plurals.dialog_highscores_description, h.days,
					h.status.toXmlString(r), h.days, h.worth, h.difficulty.toXmlString(r).toLowerCase(Locale.getDefault())));
		}
		if (out.isEmpty()) out.add("No scores yet. Finish a game to set one.");
		return out;
	}

	// ---- Options ----

	private static final int[] OPTION_LABELS = {
		R.string.dialog_options_fulltank, R.string.dialog_options_fullhull,
		R.string.dialog_options_ignore_police, R.string.dialog_options_ignore_pirates, R.string.dialog_options_ignore_traders,
		R.string.dialog_options_ignore_dealing, R.string.dialog_options_warpcosts,
		R.string.dialog_options_contattack, R.string.dialog_options_contattflee,
		R.string.dialog_options_news, R.string.dialog_options_loans,
		R.string.dialog_options_range, R.string.dialog_options_track,
	};

	public int optionCount() { return OPTION_LABELS.length + 1; }

	public String optionLabel(int i) {
		if (i >= OPTION_LABELS.length) return getResources().getString(R.string.dialog_options_bays);
		String label = getResources().getString(OPTION_LABELS[i]);
		if (i >= 2 && i <= 4) return "Always ignore when safe: " + label;
		return label;
	}

	/** Current value as text: on/off for switches, the number of bays for the last option. */
	public String optionValue(int i) {
		if (i >= OPTION_LABELS.length) return String.valueOf(leaveEmpty);
		return optionFlag(i) ? "On" : "Off";
	}

	private boolean optionFlag(int i) {
		switch (i) {
		case 0: return autoFuel;
		case 1: return autoRepair;
		case 2: return alwaysIgnorePolice;
		case 3: return alwaysIgnorePirates;
		case 4: return alwaysIgnoreTraders;
		case 5: return alwaysIgnoreTradeInOrbit;
		case 6: return reserveMoney;
		case 7: return continuous;
		case 8: return attackFleeing;
		case 9: return newsAutoPay;
		case 10: return remindLoans;
		case 11: return showTrackedRange;
		default: return trackAutoOff;
		}
	}

	public void optionToggle(int i) {
		switch (i) {
		case 0: autoFuel = !autoFuel; break;
		case 1: autoRepair = !autoRepair; break;
		case 2: alwaysIgnorePolice = !alwaysIgnorePolice; break;
		case 3: alwaysIgnorePirates = !alwaysIgnorePirates; break;
		case 4: alwaysIgnoreTraders = !alwaysIgnoreTraders; break;
		case 5: alwaysIgnoreTradeInOrbit = !alwaysIgnoreTradeInOrbit; break;
		case 6: reserveMoney = !reserveMoney; break;
		case 7: continuous = !continuous; break;
		case 8: attackFleeing = !attackFleeing; break;
		case 9: newsAutoPay = !newsAutoPay; break;
		case 10: remindLoans = !remindLoans; break;
		case 11: showTrackedRange = !showTrackedRange; break;
		case 12: trackAutoOff = !trackAutoOff; break;
		default:
			ui.showDialog(InputDialog.newInstance(
					R.string.dialog_options_bays, -1,
					R.string.generic_ok, -1, R.string.generic_cancel, -1,
					new InputDialog.OnPositiveListener() {
						@Override public void onClickPositiveButton(int value) { leaveEmpty = value; }
					}, null));
		}
	}

	// ---- Chart helpers ----

	public SolarSystem trackedSystem() { return trackedSystem; }

	public void trackSystem(SolarSystem s) { trackedSystem = (trackedSystem == s) ? null : s; }

	public int fuelRange() { return ship.getFuel(); }

	public boolean inRange(SolarSystem s) { return s != curSystem() && realDistance(curSystem(), s) <= ship.getFuel(); }

	public boolean hasWormholeTo(SolarSystem s) { return wormholeExists(curSystem(), s); }

	/** The opponent type of the current encounter (for the icon), Mantis-adjusted. */
	public Opponent encounterOpponentType() {
		if (encounterType == null) return null;
		if (opponent != null && opponent.type == ShipType.MANTIS) return Opponent.MANTIS;
		return encounterType.opponentType();
	}

	/** In fuel range, or joined to the current system by its wormhole (no fuel needed, but a tax is due). */
	public boolean reachable(SolarSystem s) { return inRange(s) || (s != curSystem() && wormholeExists(curSystem(), s)); }

	/** Tax charged for using the wormhole between the current system and s. */
	public int wormholeTaxTo(SolarSystem s) { return wormholeTax(curSystem(), s); }

	/** True when this system has a wormhole. */
	public boolean hasWormhole(SolarSystem s) { return wormholeExists(s, null); }

	/** The system at the other end of s's wormhole, or null. */
	public SolarSystem wormholePartner(SolarSystem s) {
		for (SolarSystem w : wormhole) if (w != null && wormholeExists(s, w)) return w;
		return null;
	}

	/** Estimated selling price of a good in system s, from its size, tech level, government and (if visited) resources. */
	public int averagePrice(TradeItem item, SolarSystem s) {
		if (pricesHidden(s)) return 0; // shown as "--" by the front end
		return standardPrice(item, s.size, s.techLevel(), s.politics(),
				s.visited() ? s.specialResources : SpecialResources.NOSPECIALRESOURCES);
	}

	/** True when the good sells for more in s than it costs here and can be bought here (the original shows these in bold). */
	public boolean priceIsProfit(TradeItem item, SolarSystem s) {
		int price = averagePrice(item, s);
		return price > buyPrice.get(item) && buyPrice.get(item) > 0 && curSystem().getQty(item) > 0;
	}

	/** On Hard and Impossible the price list only shows systems you have already visited. */
	public boolean pricesHidden(SolarSystem s) {
		return !s.visited() && difficulty.compareTo(DifficultyLevel.HARD) >= 0;
	}

	private Resources getResources() { return Resources.get(); }

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
	


	/*
	 * The following code adapted from the original palmos source as noted.
	 */
	/*
	 * spacetrader.h
	 */
	private static final int MAX_WORD = 65535;
	
	public static int min ( int a, int b ) { return a <= b? a : b; }
	public static int max ( int a, int b ) { return a >= b? a : b; }
	
	public static int getRandom( int maxVal ) {
		if (maxVal < 2) {
			return 0;
		}
		return rng.nextInt(maxVal);
//		return rng.nextInt() % maxVal;
	}
	// This is a little cleaner sometimes for enums and other things that aren't ints anymore.
	public static <E> E getRandom(E[] array) {
		return getRandom(array, 0, array.length);
	}
	public static <E> E getRandom(E[] array, int start) {
		return getRandom(array, start, array.length);
	}
	public static <E> E getRandom(E[] array, int start, int end) {
		if (start >= end || end > array.length) throw new IllegalArgumentException();
		
		int index = start + getRandom(end-start);
		return array[index];
	}
	
	public static int abs ( int a ) { return ((a) < 0 ? (-(a)) : (a)); }
	public static int sqr ( int a ) { return ((a) * (a)); }
	public CrewMember commander() { return mercenary[0]; }
	public SolarSystem curSystem() { return commander().curSystem(); }

	// Pieter's new random functions, tweaked a bit by SjG
	// *************************************************************************
	// NB these are used in randomly generating newspaper in a deterministic way

	private static final int DEFSEEDX = 521288629;
	private static final int DEFSEEDY = 362436069;

	private int seedX = DEFSEEDX;
	private int seedY = DEFSEEDY;

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
			ui.encounterAutoModeCleared();
		}
	};
	

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
		int selType = prefs.getInt("selectedShipType", -1); selectedShipType = selType < 0 ? null : ShipType.values()[selType];
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
		
		developerMode = DEVELOPER_MODE && prefs.getBoolean("developerMode", developerMode);

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

	public boolean developerMode() {
		return DEVELOPER_MODE && developerMode;
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

	/*
	 * Skill.c
	 */
	// *************************************************************************
	// After changing the trader skill, buying prices must be recalculated.
	// Revised to be callable on an arbitrary Solar System
	// *************************************************************************
	public void recalculateBuyPrices( SolarSystem system )
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
	// Add a news event flag
	// *************************************************************************
	public void addNewsEvent(NewsEvent eventFlag)
	{
		if (newsSpecialEventCount < MAXSPECIALNEWSEVENTS - 1)
			newsEvents[newsSpecialEventCount++] = eventFlag;
	}

	// *************************************************************************
	// replace a news event flag with another
	// *************************************************************************
	public void replaceNewsEvent(NewsEvent originalEventFlag, NewsEvent replacementEventFlag)
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
	public void resetNewsEvents()
	{
		newsSpecialEventCount = 0;
	}

	// *************************************************************************
	// get most recently addded news event flag
	// *************************************************************************
	public NewsEvent latestNewsEvent()
	{
		if (newsSpecialEventCount == 0)
			return null;
		else
			return newsEvents[newsSpecialEventCount - 1];
	}

	// *************************************************************************
	// Query news event flags
	// *************************************************************************
	public boolean isNewsEvent(NewsEvent eventFlag)
	{
		for (int i=0;i<newsSpecialEventCount; i++)
		{
			if (newsEvents[i] == eventFlag)
				return true;
		}
		return false;
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
				ui.showDialog(ConfirmDialog.newInstance(
						R.string.screen_warp_wildwontgo_title, 
						R.string.screen_warp_wildwontgo_message, 
						R.string.screen_warp_wildwontgo_pos,
						R.string.generic_cancel,
						R.string.help_wildwontgo,
						new OnConfirmListener() {
							
							@Override
							public void onConfirm() {
								ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(SimpleDialog.newInstance(
					R.string.screen_warp_debttoolarge_title, 
					R.string.screen_warp_debttoolarge_message,
					R.string.help_debttoolargefortravel));
			return false;
		}

		// Check for enough money to pay Mercenaries    
		if (mercenaryMoney() > credits)
		{
			ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(SimpleDialog.newInstance(
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
				

				ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(SimpleDialog.newInstance(
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
				ui.setScreen(ScreenType.ENCOUNTER);
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
				ui.setScreen(ScreenType.ENCOUNTER);
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
				ui.setScreen(ScreenType.ENCOUNTER);
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
				
				ui.setScreen(ScreenType.ENCOUNTER);
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
				ui.setScreen(ScreenType.ENCOUNTER);
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

				ui.setScreen(ScreenType.ENCOUNTER);
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
						ui.setScreen(ScreenType.ENCOUNTER);
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
						ui.setScreen(ScreenType.ENCOUNTER);
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
						ui.setScreen(ScreenType.ENCOUNTER);
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
						ui.setScreen(ScreenType.ENCOUNTER);
						return;
					}
					break;
				case BOTTLEOLD:
					if  ((veryRareEncounter & ALREADYBOTTLEOLD) == 0)
					{
						veryRareEncounter = veryRareEncounter | ALREADYBOTTLEOLD;
						encounterType = Encounter.VeryRare.BOTTLEOLD;
						generateOpponent( Opponent.BOTTLE );
						ui.setScreen(ScreenType.ENCOUNTER);
						return;
					}
					break;
				case BOTTLEGOOD:
					if  ((veryRareEncounter & ALREADYBOTTLEGOOD) == 0)
					{
						veryRareEncounter = veryRareEncounter | ALREADYBOTTLEGOOD;
						encounterType = Encounter.VeryRare.BOTTLEGOOD;
						generateOpponent( Opponent.BOTTLE );
						ui.setScreen(ScreenType.ENCOUNTER);
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
			ui.setScreen(ScreenType.ENCOUNTER);
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
			ui.showDialog(SimpleDialog.newInstance(
					(startClicks > 20 ? R.string.screen_encounter_uneventful_title : R.string.screen_encounter_arrival_title), 
					(startClicks > 20 ? R.string.screen_encounter_uneventful_message : R.string.screen_encounter_arrival_message),
					(startClicks > 20 ? R.string.help_uneventfultrip : R.string.help_arrival),
					newUnlocker(latch)));
			lock(latch);
			
			// Check for Large Debt - 06/30/01 SRA 
			if (debt >= DEBTWARNING ) {
				latch = newLatch();
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
						R.string.screen_encounter_reactorsmoke_title,
						R.string.screen_encounter_reactorsmoke_message,
						R.string.help_reactorusingfuel,
						newUnlocker(latch)));
				lock(latch);
			}
			
			if (reactorStatus == 20)
			{
				latch = newLatch();
				ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
								R.string.screen_encounter_notanksorrepairs_title, 
								R.string.screen_encounter_notanksorrepairs_message,
								R.string.help_nofulltanksorrepairs,
								newUnlocker(latch)));
						lock(latch);
						tryAutoRepair = false;
					}
					else {
						latch = newLatch();
						ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
			ui.setScreen(ScreenType.INFO);
			ui.clearBackStack();
			
			// NB Now autosaving on arrival:
			ui.autosave();
		}

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
			ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
//				ui.showDialog(SimpleDialog.creator(
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
				ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(SimpleDialog.newInstance(
					R.string.screen_encounter_fleabuilt_title,
					R.string.screen_encounter_fleabuilt_message,
					R.string.help_fleabuilt,
					newUnlocker(latch)));
			lock(latch);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			ui.setScreen(ScreenType.INFO);
			ui.clearBackStack();
			
			// NB Now autosaving on arrival:
			ui.autosave();
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
			ui.showDialog(SimpleDialog.newInstance(
					R.string.screen_encounter_arrested_title, 
					R.string.screen_encounter_arrested_message,
					R.string.help_arrested,
					newUnlocker(latch)));
			lock(latch);

			latch = newLatch();
			ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
//				ui.showDialog(SimpleDialog.creator(
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
				ui.showDialog(SimpleDialog.newInstance(
						R.string.screen_encounter_shipsold_title,
						R.string.screen_encounter_shipsold_message,
						R.string.help_shipsold,
						newUnlocker(latch)));
				lock(latch);

				if (ship.tribbles > 0)
				{
					latch = newLatch();
					ui.showDialog(SimpleDialog.newInstance(
							R.string.screen_encounter_tribblessold_title,
							R.string.screen_encounter_tribblessold_message,
							R.string.help_tribblessold,
							newUnlocker(latch)));
					lock(latch);
					ship.tribbles = 0;
				}

				latch = newLatch();
				ui.showDialog(SimpleDialog.newInstance(
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
			ui.setScreen(ScreenType.INFO);
			ui.clearBackStack();
			
			// NB Now autosaving on arrival:
			ui.autosave();
		}
		
	}

	// *************************************************************************
	// Encounter screen Event Handler
	// *************************************************************************
	/** Player pressed an encounter button (attack, flee, surrender, ...). */
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

	// Some helpers for the EncounterButtonTask
	private enum Result {
		TRAVEL,
		REFRESH,
		NOTHING,
		DEAD,
	}

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
			ui.encounterChanged();
		}
				
		@Override
		protected void onPostExecute(final Result result) {

			ui.encounterChanged();
			
			switch (result) {
			case TRAVEL:
				travel();
				break;
			case DEAD:
				showEndGameScreen(EndStatus.KILLED);
				break;
			case REFRESH:				
				// TODO stick this in a separate method somewhere for cleaner organization?
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

				ui.showEncounterDescription(description);
				ui.encounterChanged();
				
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
//	    		ui.showDialog(SimpleDialog.creator(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    			ui.showDialog(ConfirmDialog.newInstance(
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
				    		ui.showDialog(ConfirmDialog.newInstance(
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
			    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(InputDialog.newInstance(
		    				R.string.dialog_tradeinorbit_title, 
		    				R.string.dialog_tradeinorbit_buymessage,
		    				R.string.generic_ok,
		    				R.string.generic_all,
		    				R.string.generic_none,
		    				R.string.help_tradeinorbit,
		    				new InputDialog.OnPositiveListener() {
								
								@Override
								public void onClickPositiveButton(int value) {
				    				int amount = max(0, min(ship.getCargo(item), value));
				    				sellInOrbit(item, amount, fPrice, latch);
								}
							},
		    				new InputDialog.OnNeutralListener() {
								
								@Override
								public void onClickNeutralButton() {
									int amount = ship.getCargo(item);
					    			sellInOrbit(item, amount, fPrice, latch);
								}
							},
							new InputDialog.OnNegativeListener() {
								
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
		    		ui.showDialog(InputDialog.newInstance(
		    				R.string.dialog_tradeinorbit_title, 
		    				R.string.dialog_tradeinorbit_sellmessage,
		    				R.string.generic_ok,
		    				R.string.generic_all,
		    				R.string.generic_none,
		    				R.string.help_tradeinorbit,
		    				new InputDialog.OnPositiveListener() {
								
								@Override
								public void onClickPositiveButton(int value) {
				    				int amount = max(0, min(opponent.getCargo(item), value));
					    			buyInOrbit(item, amount, fPrice, latch);
								}
							},
		    				new InputDialog.OnNeutralListener() {
								
								@Override
								public void onClickNeutralButton() {
									int amount = min(opponent.getCargo(item), (ship.totalCargoBays()-ship.filledCargoBays()));
					    			buyInOrbit(item, amount, fPrice, latch);
								}
							},
							new InputDialog.OnNegativeListener() {
								
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
		    		ui.showDialog(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_surrender_title, 
		    				R.string.screen_encounter_surrender_extra,
		    				R.string.help_wanttosurrender,
		    				newUnlocker(latch),
		    				newStopper(latch),
		    				getResources().getString(R.string.screen_encounter_surrender_wildonboard),
    						getResources().getString(R.string.screen_encounter_surrender_wildarrested)
		    				));
					lock(latch);
					if (stop) return Result.NOTHING;
				}
				else if (reactorStatus > 0 && reactorStatus < 21)
				{
		    		CountDownLatch latch = newLatch();
		    		ui.showDialog(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_surrender_title, 
		    				R.string.screen_encounter_surrender_extra,
		    				R.string.help_wanttosurrender,
		    				newUnlocker(latch),
		    				newStopper(latch),
		    				getResources().getString(R.string.screen_encounter_surrender_reactoronboard),
    						getResources().getString(R.string.screen_encounter_surrender_reactortaken)
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
//				    		ui.showDialog(SimpleDialog.creator(
//				    				R.string.screen_encounter_contrabandnotfound_title,
//				    				R.string.screen_encounter_contrabandnotfound_message,
//				    				newUnlocker(latch)));
//							lock(latch);
//		    			}
//		    			else 
//		    			{
//				    		CountDownLatch latch = newLatch();
//				    		ui.showDialog(SimpleDialog.creator(
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
			    		ui.showDialog(SimpleDialog.newInstance(
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
			    		ui.showDialog(ConfirmDialog.newInstance(
			    				R.string.screen_encounter_wanttosurrendertoaliens_title,
			    				R.string.screen_encounter_wanttosurrendertoaliens_message,
			    				R.string.help_wanttosurrendertoaliens,
			    				newUnlocker(latch),
			    				newStopper(latch)));
						lock(latch);
						
						if (stop) return Result.NOTHING;
		    			
						latch = newLatch();
			    		ui.showDialog(SimpleDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
				    		ui.showDialog(ConfirmDialog.newInstance(
				    				R.string.screen_encounter_surrender_title, 
				    				R.string.screen_encounter_surrender_extra,
				    				R.string.help_wanttosurrender,
				    				newUnlocker(latch),
				    				newStopper(latch),
				    				getResources().getString(R.string.screen_encounter_surrender_wildonboard),
		    						getResources().getString(R.string.screen_encounter_surrender_wildarrested)
				    				));
							lock(latch);
							if (stop) return Result.NOTHING;
						}
						else if (reactorStatus > 0 && reactorStatus < 21)
						{
				    		CountDownLatch latch = newLatch();
				    		ui.showDialog(ConfirmDialog.newInstance(
				    				R.string.screen_encounter_surrender_title, 
				    				R.string.screen_encounter_surrender_extra,
				    				R.string.help_wanttosurrender,
				    				newUnlocker(latch),
				    				newStopper(latch),
				    				getResources().getString(R.string.screen_encounter_surrender_reactoronboard),
		    						getResources().getString(R.string.screen_encounter_surrender_reactortaken)
				    				));
							lock(latch);
							if (stop) return Result.NOTHING;
						}
						else
						{
				    		CountDownLatch latch = newLatch();
				    		ui.showDialog(ConfirmDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
		    			ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    	ui.showDialog(ConfirmDialog.newInstance(
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
					    			ui.showDialog(SimpleDialog.newInstance(
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
		    				illegalGoodsString = getResources().getString(R.string.screen_encounter_illegal_wildgoods);
		    			}
		    			else
		    			{
		    				illegalGoodsString = getResources().getString(R.string.screen_encounter_illegal_wild);
		    			}
		    			arrestedString = getResources().getString(R.string.screen_encounter_illegal_arrested);
		    		}
		    		else if (reactorStatus > 0 && reactorStatus < 21)
		    		{
		    			if (ship.getCargo(TradeItem.FIREARMS) > 0 ||ship.getCargo(TradeItem.NARCOTICS) > 0)
		    			{
		    				illegalGoodsString = getResources().getString(R.string.screen_encounter_illegal_reactorgoods);
		    			}
		    			else
		    			{
		    				illegalGoodsString = getResources().getString(R.string.screen_encounter_illegal_reactor);
		    			}
		    			arrestedString = getResources().getString(R.string.screen_encounter_illegal_arrested);
		    		}
		    		else
		    		{
	    				illegalGoodsString = getResources().getString(R.string.screen_encounter_illegal_goods);
		    		}
		    		
			    	CountDownLatch latch = newLatch();
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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

		    	ui.showDialog(PlunderDialog.newInstance());
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
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
		    		ui.showDialog(SimpleDialog.newInstance(
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
		    		ui.showDialog(ConfirmDialog.newInstance(
		    				R.string.screen_encounter_engagemarie_title,
		    				R.string.screen_encounter_engagemarie_message, 
		    				R.string.help_lootmarieceleste,
		    				newUnlocker(latch),
		    				newStopper(latch)));
		    		lock(latch);
		    		if (stop) return Result.TRAVEL;
		    		
		    		narcs = ship.getCargo(TradeItem.NARCOTICS);
		    		ui.showDialog(PlunderDialog.newInstance());
		    		return Result.NOTHING;
		    	}
		    }		
		    else if (action == EncounterButton.DRINK) // Drink Tonic?
		    {
		    	if (encounterType == Encounter.VeryRare.BOTTLEGOOD)
		    	{
		    		// Quaff the good bottle of Skill Tonic?
		    		final CountDownLatch latch = newLatch();
		    		ui.showDialog(ConfirmDialog.newInstance(
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
									
									ui.showDialog(SimpleDialog.newInstance(R.string.dialog_drink_title, R.string.dialog_gooddrink_message, R.string.help_drankgoodskilltonic, newUnlocker(latch)));
								}
							},
							newStopper(latch)));
		    		lock(latch);
		    		

		    	}
		    	else if (encounterType == Encounter.VeryRare.BOTTLEOLD)
		    	{
		    		// Quaff the out of date bottle of Skill Tonic?
		    		final CountDownLatch latch = newLatch();
		    		ui.showDialog(ConfirmDialog.newInstance(
		    				R.string.dialog_engagebottle_title,
		    				R.string.dialog_engagebottle_message, 
		    				R.string.dialog_engagebottle_pos,
		    				R.string.generic_no,
		    				R.string.help_drinkoldtonic,
		    				new OnConfirmListener() {
								
								@Override
								public void onConfirm() {
					    			commander().tonicTweakRandomSkill();
					    			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_drink_title, R.string.dialog_strangedrink_message, R.string.help_drankoldskilltonic, newUnlocker(latch)));
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
			ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(ConfirmDialog.newInstance(
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
								ui.showDialog(ConfirmDialog.newInstance(
										R.string.dialog_scoopnoroom_title, 
										R.string.dialog_scoopnoroom_message,
										R.string.dialog_scoopnoroom_pos,
										R.string.dialog_scoopnoroom_neg,
										R.string.help_pickcannister,
										new OnConfirmListener() {
											
											@Override
											public void onConfirm() {
												ui.showDialog(JettisonDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(
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
				if (encounterAnim) ui.animateAttack(true, commanderGotHit, ship.hull <= 0); // NB Animation is a new addition
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
				if (encounterAnim) ui.animateAttack(false, opponentGotHit, opponent.hull <= 0); // NB Animation is a new addition
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
					ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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

					if (encounterAnim) ui.animateEnterExit(true, false); // NB Animation is a new addition

		    		CountDownLatch latch = newLatch();
					ui.showDialog(SimpleDialog.newInstance(
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

					if (encounterAnim) ui.animateEnterExit(true, false); // NB Animation is a new addition

					if (commanderGotHit)
					{
						publishProgress();

						CountDownLatch latch = newLatch();
						ui.showDialog(SimpleDialog.newInstance(
								R.string.screen_encounter_escaped_title,
								R.string.screen_encounter_escapedhit_message,
								R.string.help_youescaped,
								newUnlocker(latch)));
			    		lock(latch);
					}
					else {
						CountDownLatch latch = newLatch();
						ui.showDialog(SimpleDialog.newInstance(
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

					if (encounterAnim) ui.animateEnterExit(false, false); // NB Animation is a new addition
					
		    		CountDownLatch latch = newLatch();
					ui.showDialog(SimpleDialog.newInstance(
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
	// Buy amount of cargo
	// *************************************************************************
	public void buyCargo( TradeItem item, int amount )
	{
		int toBuy;

		if (debt > DEBTTOOLARGE)
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_debttoolarge_title, R.string.screen_buy_debttoolarge_message, R.string.help_debttoolargeforbuy));
			return;
		}

		if (curSystem().getQty(item) <= 0 || buyPrice.get(item) <= 0)
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_nothingavailable_title, R.string.screen_buy_nothingavailable_message, R.string.help_nothingavailable));
			return;
		}

		if (ship.totalCargoBays() - ship.filledCargoBays() - leaveEmpty <= 0)
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_noemptybays_title, R.string.screen_buy_noemptybays_message, R.string.help_noemptybays));
			return;
		}

		if (toSpend() < buyPrice.get(item) )
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_notenoughmoney_title, R.string.screen_buy_notenoughmoney_message, R.string.help_cantafford));
			return;
		}

		toBuy = min( amount, curSystem().getQty(item) );
		toBuy = min( toBuy, ship.totalCargoBays() - ship.filledCargoBays() - leaveEmpty );
		toBuy = min( toBuy, toSpend() / buyPrice.get(item) );

		ship.addCargo(item, toBuy);
		credits -= toBuy * buyPrice.get(item);
		buyingPrice.put(item, buyingPrice.get(item) + toBuy * buyPrice.get(item));
		curSystem().addQty(item, -toBuy);
		
		if (ui.currentScreen() == ScreenType.BUY) {
			ui.stateChanged();
		} else if (ui.currentScreen() == ScreenType.AVGPRICES) {
			ui.stateChanged();
			
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
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_sell_nogoods, R.string.screen_sell_nogoods_message, R.string.help_nothingforsale));
			else {
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_sell_nodumpgoods, R.string.screen_sell_nogoods_message, R.string.help_dumpitem));
			}
			return;
		}
		
		if (sellPrice.get(item) <= 0 && operation == SellOperation.SELL)
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_sell_notinterested, R.string.screen_sell_notinterested_message, R.string.help_notinterested));
			return;
		}
		
		if (operation == SellOperation.JETTISON)
		{
			if (policeRecordScore > PoliceRecord.DUBIOUS.score && !litterWarning)
			{
				litterWarning = true;
				
				ui.showDialog(ConfirmDialog.newInstance(
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
			ui.stateChanged();
		}
		else
		{
			ui.stateChanged();
		}

	}

	// *************************************************************************
	// Let the commander indicate how many he wants to buy
	// *************************************************************************
	public void getAmountToBuy( final TradeItem item )       // used in Traveler.c also
	{

		if (buyPrice.get(item) <= 0 || curSystem().getQty(item) <= 0)
		{
			ui.showDialog(SimpleDialog.newInstance(
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

		ui.showDialog(InputDialog.newInstance(
				R.string.format_buyitem,
				queryRes,
				R.string.generic_ok, 
				R.string.generic_all, 
				R.string.generic_none, 
				R.string.help_amounttobuy,
				new InputDialog.OnPositiveListener() {
					
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) buyCargo(item, value);
					}
				}, 
				new InputDialog.OnNeutralListener() {
					
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
		
		ui.showDialog(InputDialog.newInstance(
				titleId, 
				messageId,
				R.string.generic_ok,
				R.string.generic_all,
				R.string.generic_none,
				R.string.help_amounttosell,
				new InputDialog.OnPositiveListener() {
					
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) sellCargo(item, value, operation);
					}
				},
				new InputDialog.OnNeutralListener() {
					
					@Override
					public void onClickNeutralButton() {
						sellCargo(item, 999, operation);
					}
				},
				args));

	}

	// *************************************************************************
	// Plunder amount of cargo
	// *************************************************************************
	public void plunderCargo( TradeItem item, int amount )
	{
		
		if (opponent.getCargo(item) <= 0)
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_notavailable_title, R.string.dialog_plunder_nothing, R.string.help_victimdoesnthaveany));
			return;
		}

		if (ship.totalCargoBays() - ship.filledCargoBays() <= 0)
		{
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_noemptybays_title, R.string.screen_buy_noemptybays_message, R.string.help_noemptybays));
			return;
		}
		
		int toPlunder = min( amount, opponent.getCargo(item) );
		toPlunder = min( toPlunder, ship.totalCargoBays() - ship.filledCargoBays() );
		
		ship.addCargo(item, toPlunder);
		opponent.addCargo(item, -toPlunder);
		
		ui.stateChanged();
	}

	// *************************************************************************
	// Let the commander indicate how many he wants to plunder
	// *************************************************************************
	public void getAmountToPlunder( final TradeItem item )
	{
		ui.showDialog(InputDialog.newInstance(
				R.string.dialog_plunder_title, 
				R.string.dialog_plunder_query, 
				R.string.generic_ok,
				R.string.generic_all,
				R.string.generic_none,
				R.string.help_amounttoplunder,
				new InputDialog.OnPositiveListener() {
					
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) plunderCargo(item, value); 
					}
				}, 
				new InputDialog.OnNeutralListener() {
					
					@Override
					public void onClickNeutralButton() {
						plunderCargo(item, 999);
					}
				}, 
				item,
				opponent.getCargo(item)));
	}

	/*
	 * Shipyard.c
	 */
	// *************************************************************************
	// Let the commander indicate how much he wants to spend on repairs
	// *************************************************************************
	public void getAmountForRepairs(  )
	{
		ui.showDialog(InputDialog.newInstance(
				R.string.screen_yard_buyrepairs,
				R.string.screen_yard_repairquery,
				R.string.generic_ok,
				R.string.generic_maximum,
				R.string.generic_nothing,
				R.string.help_buyrepairs,
				new InputDialog.OnPositiveListener() {
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) {
							buyRepairs(value);
							ui.stateChanged();
						}
					}
				}, 
				new InputDialog.OnNeutralListener() {

					public void onClickNeutralButton() {
						buyRepairs(ship.getHullStrength()*ship.type.repairCosts);
						ui.stateChanged();
					}
				}));
	}	

	// *************************************************************************
	// Let the commander indicate how much he wants to spend on fuel
	// *************************************************************************
	public void getAmountForFuel(  )
	{
		ui.showDialog(InputDialog.newInstance(
				R.string.screen_yard_fuelbutton, 
				R.string.screen_yard_fuelquery, 
				R.string.generic_ok, 
				R.string.generic_maximum, 
				R.string.generic_nothing, 
				R.string.help_buyfuel,
				new InputDialog.OnPositiveListener() {
					@Override
					public void onClickPositiveButton(int value) {
						if (value > 0) {
							buyFuel(value);
							ui.stateChanged();
						}
					}
				}, 
				new InputDialog.OnNeutralListener() {

					public void onClickNeutralButton() {
						buyFuel(ship.getFuelTanks()*ship.type.costOfFuel);
						ui.stateChanged();
					}
				}));
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
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buy_notavailable_title, R.string.screen_buy_notavailable_message, R.string.help_nothingavailable));
		else if (debt > 0)
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_youreindebt_title, R.string.dialog_youreindebt_message, R.string.help_youreindebt));
		else if (price > toSpend())
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_money, R.string.screen_buyeq_dialog_money_message, R.string.help_cantbuyitem));
		else if (firstEmptySlot < 0)
			ui.showDialog(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
		else
		{
			ui.showDialog(ConfirmDialog.newInstance(
					R.string.format_buyitem,
					R.string.screen_buyeq_buyquery,
					R.string.help_buyitem,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
							item[firstEmptySlot] = itemIndex;
							credits -= price;

						}
					},
					null,
					itemIndex,
					price));

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
				ui.showDialog(SimpleDialog.newInstance(
						R.string.screen_yard_buyship_notavailable_title, 
						R.string.screen_yard_buyship_notavailable_message, 
						R.string.help_itemnotsold)); // NB Not quite sure if the help text here is correct, but that's ok because the dialog shouldn't ever appear anyway since the button won't be drawn.
			else if ((shipPrice.get(selectedShipType) >= 0) &&
					(debt > 0))
				ui.showDialog(SimpleDialog.newInstance(
						R.string.dialog_youreindebt_title, 
						R.string.dialog_youreindebt_message,
						R.string.help_youreindebt));
			else if (shipPrice.get(selectedShipType) > toSpend())
				ui.showDialog(SimpleDialog.newInstance(
						R.string.screen_yard_buyship_notenoughmoney_title, 
						R.string.screen_yard_buyship_notenoughmoney_message,
						R.string.help_cantbuyship));
			// NB a new check here if both Wild and Jarek are on board
			else if ((jarekStatus == 1) && (wildStatus == 1) && (selectedShipType.crewQuarters < 3))
				ui.showDialog(SimpleDialog.newInstance(
						R.string.dialog_special_passengerneedsquarters_title, 
						R.string.dialog_special_passengerneedsquarters_message, 
						R.string.help_passengersneedsquarters,
						getResources().getString(R.string.dialog_special_passenger_both, getResources().getString(R.string.dialog_special_passenger_jarek), getResources().getString(R.string.dialog_special_passenger_wild))));
			else if ((jarekStatus == 1) && (selectedShipType.crewQuarters < 2))
				ui.showDialog(SimpleDialog.newInstance(
						R.string.dialog_special_passengerneedsquarters_title, 
						R.string.dialog_special_passengerneedsquarters_message, 
						R.string.help_jarekneedsquarters,
						getResources().getString(R.string.dialog_special_passenger_jarek)));
			else if ((wildStatus == 1) && (selectedShipType.crewQuarters < 2))
				ui.showDialog(SimpleDialog.newInstance(
						R.string.dialog_special_passengerneedsquarters_title, 
						R.string.dialog_special_passengerneedsquarters_message, 
						R.string.help_jarekneedsquarters,
						getResources().getString(R.string.dialog_special_passenger_wild)));
			else if (reactorStatus > 0 && reactorStatus < 21)
				ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(ConfirmDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(ConfirmDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
						ui.showDialog(ConfirmDialog.newInstance(
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
						ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(SimpleDialog.newInstance(
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
					ui.showDialog(ConfirmDialog.newInstance(
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
			ui.stateChanged();
		}

	}

	public void specialEventFormHandleEvent( int unused )
	{
		boolean handled = false;
		int firstEmptySlot;
		
		if (toSpend() < curSystem().special().price)
		{
			ui.showDialog(SimpleDialog.newInstance(
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
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_notenoughbays_title, R.string.dialog_notenoughbays_message, R.string.help_notenoughbays));
				handled = true;
				break;
			}
			else if (wildStatus == 1)
			{
				
				ui.showDialog(ConfirmDialog.newInstance(
						R.string.screen_warp_wildwontstayonboard_title, 
						R.string.screen_warp_wildwontstayonboard_message, 
						R.string.screen_warp_wildwontgo_pos,
						R.string.generic_cancel,
						R.string.help_wildwontgowithreactor,
						new OnConfirmListener() {
							
							@Override
							public void onConfirm() {
								ui.showDialog(SimpleDialog.newInstance(
										R.string.screen_warp_wildleavesship_title, 
										R.string.screen_warp_wildleavesship_message,
										R.string.help_wildleaves, 
										new OnConfirmListener() {
											
											@Override
											public void onConfirm() {
												ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_reactor_title, R.string.dialog_special_reactor_message, R.string.help_reactoronboard));
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
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_reactor_title, R.string.dialog_special_reactor_message, R.string.help_reactoronboard));
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
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_hullupgrade_title, R.string.dialog_special_hullupgrade_message, R.string.help_hullreinforced));
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
			ui.showDialog(SimpleDialog.newInstance(
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
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_skillincrease_title, R.string.dialog_special_skillincrease_message, R.string.help_skillincrease));
			commander().increaseRandomSkill();
			break;

		case TRIBBLE:
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_youhaveatribble_title, R.string.dialog_special_youhaveatribble_message, R.string.help_youhaveatribble));
			ship.tribbles = 1;
			break;

		case BUYTRIBBLE:
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_beamovertribbles_title, R.string.dialog_special_beamovertribbles_message, R.string.help_beamovertribbles));
			credits += (ship.tribbles >> 1);
			ship.tribbles = 0;
			break;

		case ERASERECORD:
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_cleanrecord_title, R.string.dialog_special_cleanrecord_message, R.string.help_cleanrecord));
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
				ui.showDialog(SimpleDialog.newInstance(
						R.string.dialog_special_noquartersavailable_title, 
						R.string.dialog_special_noquartersavailable_message, 
						R.string.help_noquartersforjarek,
						getResources().getString(R.string.dialog_special_passenger_jarek)));
				handled = true;
				break;
			}
			ui.showDialog(SimpleDialog.newInstance(
					R.string.dialog_special_passengertakenonboard_title, 
					R.string.dialog_special_passengertakenonboard_message, 
					R.string.help_jarektakenonboard,
					getResources().getString(R.string.dialog_special_passenger_jarek)));
			jarekStatus = 1;
			break;

		case TRANSPORTWILD:

			if (ship.crew[ship.type.crewQuarters-1] != null)
			{
				ui.showDialog(SimpleDialog.newInstance(
						R.string.dialog_special_noquartersavailable_title, 
						R.string.dialog_special_noquartersavailable_message, 
						R.string.help_noquartersforjarek,
						getResources().getString(R.string.dialog_special_passenger_wild)));
				handled = true;
				break;
			}
			if (!ship.hasWeapon(Weapon.BEAM, false))
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_wildwontgetaboard_title, R.string.dialog_special_wildwontgetaboard_message, R.string.help_wildwontgo));
				handled = true;
				break;
			}
			if (reactorStatus > 0 && reactorStatus < 21)
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_wildafraidofreactor_title, R.string.dialog_special_wildafraidofreactor_message, R.string.help_wildwontgowithreactor));
				handled = true;
				break;
			}
			ui.showDialog(SimpleDialog.newInstance(
					R.string.dialog_special_passengertakenonboard_title, 
					R.string.dialog_special_passengertakenonboard_message, 
					R.string.help_jarektakenonboard,
					getResources().getString(R.string.dialog_special_passenger_wild)));
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
			ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_sealedcanisters_title, R.string.dialog_special_sealedcanisters_message, R.string.help_sealedcannisters));
			TradeItem item = getRandom( TradeItem.values() );
			ship.addCargo(item, 3);
			buyingPrice.put(item, buyingPrice.get(item) + curSystem().special().price);
			break;

		case INSTALLLIGHTNINGSHIELD:
			firstEmptySlot = getFirstEmptySlot( ship.type.shieldSlots, ship.shield );
			if (firstEmptySlot < 0)
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
				handled = true;
			}
			else
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_lightningshield_title, R.string.dialog_special_lightningshield_message, R.string.help_lightningshield));
				ship.shield[firstEmptySlot] = Shield.LIGHTNING;
				ship.shieldStrength[firstEmptySlot] = Shield.LIGHTNING.power;
			}
			break;

		case GETSPECIALLASER:
			firstEmptySlot = getFirstEmptySlot( ship.type.weaponSlots, ship.weapon );
			if (firstEmptySlot < 0)
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
				handled = true;
			}
			else
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_morganlaser_title, R.string.dialog_special_morganlaser_message, R.string.help_morganlaserinstall));
				ship.weapon[firstEmptySlot] = Weapon.MORGAN;
			}
			break;

		case GETFUELCOMPACTOR:
			firstEmptySlot = getFirstEmptySlot( ship.type.gadgetSlots, ship.gadget );
			if (firstEmptySlot < 0)
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.screen_buyeq_dialog_slots, R.string.screen_buyeq_dialog_slots_message, R.string.help_notenoughslots));
				handled = true;
			}
			else
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_special_fuelcompactor_title, R.string.dialog_special_fuelcompactor_message, R.string.help_fuelcompactor));
				ship.gadget[firstEmptySlot] = Gadget.FUELCOMPACTOR;
				ship.fuel = ship.getFuelTanks();
			}
			break;

		case JAPORIDISEASE:
			if (ship.filledCargoBays() > ship.totalCargoBays() - 10)
			{
				ui.showDialog(SimpleDialog.newInstance(R.string.dialog_notenoughbays_title, R.string.dialog_notenoughbays_message, R.string.help_notenoughbays));
				handled = true;
			}
			else
			{
				ui.showDialog(SimpleDialog.newInstance(
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


		specialAvailable = showSpecial;
		
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

	private int headlineCount = 0;
	private final java.util.List<String> headlines = new java.util.ArrayList<>();
	private void displayHeadline(int stringId, Object... args) {
		if (headlineCount > MAXSTORIES) return;
		headlineCount++;
		headlines.add(getResources().getString(stringId, args));
	}
	private void displayHeadline(String string, Object... args) {
		if (headlineCount > MAXSTORIES) return;
		headlineCount++;
		headlines.add(String.format(string, args));
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

	/** Builds the headlines for the newspaper of the current system. */
	public java.util.List<String> newspaperHeadlines()
	{
		headlineCount = 0;
		headlines.clear();
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
		return new java.util.ArrayList<>(headlines);
		
//		// Screenshot override
//////		mGameManager.findDialogByClass(NewspaperDialog.class).setTitle(getResources().getStringArray(R.array.masthead_theocracy)[0]);
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

						ui.setScreen(ScreenType.TITLE);
					}
				},
				(a / 50),
				((a%50) / 5)
				);
//		dialog.setCancelable(false);.
		ui.showDialog(dialog);
		
		ui.autosave();
		
	}

	// *************************************************************************
	// View high scores
	// *************************************************************************
	public void viewHighScores(  ) {
		ui.showDialog(HighScoresDialog.newInstance());
	}

}
