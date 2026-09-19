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

import java.util.EnumMap;
import java.util.Map;

import com.brucelet.spacetrader.platform.SharedPreferences;

import com.brucelet.spacetrader.enumtypes.DifficultyLevel;
import com.brucelet.spacetrader.enumtypes.Gadget;
import com.brucelet.spacetrader.enumtypes.Shield;
import com.brucelet.spacetrader.enumtypes.ShipType;
import com.brucelet.spacetrader.enumtypes.Skill;
import com.brucelet.spacetrader.enumtypes.TradeItem;
import com.brucelet.spacetrader.enumtypes.Weapon;

public class Ship {

	private static final int SKILLBONUS = 3;
	private static final int CLOAKBONUS = 2;
	protected static final int UPGRADEDHULL = 50;
	
	public final ShipType type;
	private final Map<TradeItem, Integer> cargo = new EnumMap<>(TradeItem.class);
	public final Weapon[] weapon = new Weapon[3];
	public final Shield[] shield = new Shield[3];
	public final int[] shieldStrength = new int[3];
	public final Gadget[] gadget = new Gadget[3];
	public final CrewMember[] crew = new CrewMember[3];
	public int fuel;
	public int hull;
	public int tribbles;
	
	private final GameState mGameState;
	
	public Ship(GameState game, ShipType type) {
		this.type = type;
		mGameState = game;
		for (TradeItem item : TradeItem.values()) {
			cargo.put(item, 0);
		}
		fuel = getFuelTanks();
		hull = type.hullStrength;
	}
	
	public Ship(SharedPreferences prefs, String tag, GameState game) {
		mGameState = game;
		
		type = ShipType.values()[prefs.getInt(tag+"_type", 0)];
		for (TradeItem item : TradeItem.values()) {
			cargo.put(item, prefs.getInt(tag+"_cargo_"+item, 0));
		}
		for (int i = 0; i < weapon.length; i++) {
			if (prefs.contains(tag+"_weapon"+i)) {
				weapon[i] = Weapon.values()[prefs.getInt(tag+"_weapon"+i, 0)];
			}
		}
		for (int i = 0; i < shield.length; i++) {
			if (prefs.contains(tag+"_shield"+i)) {
				shield[i] = Shield.values()[prefs.getInt(tag+"_shield"+i, 0)];
			}
		}
		for (int i = 0; i < shieldStrength.length; i++) {
			if (prefs.contains(tag+"_shieldStrength"+i)) {
				shieldStrength[i] = prefs.getInt(tag+"_shieldStrength"+i, 0);
			}
		}
		for (int i = 0; i < gadget.length; i++) {
			if (prefs.contains(tag+"_gadget"+i)) {
				gadget[i] = Gadget.values()[prefs.getInt(tag+"_gadget"+i, 0)];
			}
		}
//		for (int i = 0; i < crew.length; i++) {
//			if (prefs.contains(tag+"_crew"+i)) {
//				// NB Handled by GameState
//			}
//		}
		fuel = prefs.getInt(tag+"_fuel", 0);
		hull = prefs.getInt(tag+"_hull", 0);
		tribbles = prefs.getInt(tag+"_tribbles", 0);
	}
	
	public void saveState(SharedPreferences.Editor editor, String tag) {
		editor.putInt(tag+"_type", type.ordinal());
		for (TradeItem item : TradeItem.values()) {
			editor.putInt(tag+"_cargo_"+item, cargo.get(item));
		}
		for (int i = 0; i < weapon.length; i++) {
			if (weapon[i] != null) {
				editor.putInt(tag+"_weapon"+i, weapon[i].ordinal());
			}
		}
		for (int i = 0; i < shield.length; i++) {
			if (shield[i] != null) {
				editor.putInt(tag+"_shield"+i, shield[i].ordinal());
			}
		}
		for (int i = 0; i < shieldStrength.length; i++) {
			if (shield[i] != null) {
				editor.putInt(tag+"_shieldStrength"+i, shieldStrength[i]);
			}
		}
		for (int i = 0; i < gadget.length; i++) {
			if (gadget[i] != null) {
				editor.putInt(tag+"_gadget"+i, gadget[i].ordinal());
			}
		}
		for (int i = 0; i < crew.length; i++) {
			if (crew[i] != null) {
				editor.putString(tag+"_crew"+i, crew[i].name);
			}
		}
		editor.putInt(tag+"_fuel", fuel);
		editor.putInt(tag+"_hull", hull);
		editor.putInt(tag+"_tribbles", tribbles);
	}
	
	public Ship copy() {
		Ship copy = new Ship(mGameState, type);
		
		for (TradeItem item : TradeItem.values()) {
			copy.cargo.put(item, cargo.get(item));
		}
		for (int i = 0; i < weapon.length; i++) {
			copy.weapon[i] = weapon[i];
		}
		for (int i = 0; i < shield.length; i++) {
			copy.shield[i] = shield[i];
		}
		for (int i = 0; i < shieldStrength.length; i++) {
			copy.shieldStrength[i] = shieldStrength[i];
		}
		for (int i = 0; i < gadget.length; i++) {
			copy.gadget[i] = gadget[i];
		}
		for (int i = 0; i < crew.length; i++) {
			copy.crew[i] = crew[i];
		}
		copy.fuel = fuel;
		copy.hull = hull;
		copy.tribbles = tribbles;
		return copy;
	}
	
	
	/*
	 * The following code adapted from the original palmos source as noted.
	 */
	
	/*
	 * Cargo.c
	 */
	// *************************************************************************
	// Calculate total cargo bays
	// *************************************************************************
	public int totalCargoBays( )
	{
		int bays = type.cargoBays;
		for (Gadget g : gadget) {
			if (g == Gadget.EXTRABAYS)
				bays += 5;
		}
		if (mGameState.japoriDiseaseStatus == 1)
			bays -= 10;
		// since the quest ends when the reactor
		if (mGameState.reactorStatus > 0 && mGameState.reactorStatus < 21)
			bays -= (5 + 10 - (mGameState.reactorStatus - 1)/2);
		
		
		return bays;
	}

	// *************************************************************************
	// Calculate total filled cargo bays
	// *************************************************************************
	public int filledCargoBays(  )
	{
		int sum = 0;
		for (TradeItem item : TradeItem.values())
			sum = sum + cargo.get(item);

		return sum;
	}
	
	/*
	 * Encounter.c
	 */
	// *************************************************************************
	// Calculate Bounty
	// *************************************************************************
	public int getBounty(  )
	{
		int bounty = enemyPrice();
		
		bounty /= 200;
		bounty /= 25;	
		bounty *= 25;
		if (bounty <= 0)
			bounty = 25;
		if (bounty > 2500)
			bounty = 2500;
		
		return bounty;
	}
	
	
	// *************************************************************************
	// Calculate total possible shield strength
	// *************************************************************************
	public int totalShields(  )
	{
		int j = 0;
		for (Shield s : shield)
		{
			if (s == null)
				break;
			j += s.power;
		}

		return j;
	}


	// *************************************************************************
	// Calculate total shield strength
	// *************************************************************************
	public int totalShieldStrength(  )
	{
		int k = 0;
		for (int s : shieldStrength)
		{
			if (s < 0)
				break;
			k += s;
		}

		return k;
	}
	
	
	// *************************************************************************
	// Calculate total possible weapon strength
	// Modified to allow an upper and lower limit on which weapons work.
	// Weapons equal to or between minWeapon and maxWeapon (e.g., PULSELASERWEAPON)
	// will do damage. Use -1 to allow damage from any weapon, which is almost
	// always what you want. SjG
	// *************************************************************************
	public int totalWeapons( Weapon minWeapon, Weapon maxWeapon ) 
	{
	    int j = 0;
	    for (Weapon w : weapon)
	    {
		    if (w == null)
		        break;
		        
			if ((minWeapon != null && w.compareTo(minWeapon) < 0) ||
			    (maxWeapon != null && w.compareTo(maxWeapon) > 0))
				continue;
		        
		    j += w.power;
	    }
	    
	    return j;
	}
	
	/*
	 * Fuel.c
	 */
	// *************************************************************************
	// Determine size of fueltanks
	// *************************************************************************
	public int getFuelTanks( )
	{
		return (hasGadget(Gadget.FUELCOMPACTOR) ? 18 : type.fuelTanks);
	}


	// *************************************************************************
	// Determine fuel in tank
	// *************************************************************************
	public int getFuel()
	{
		return GameState.min( fuel, getFuelTanks() );
	}
	
	
	/*
	 * ShipPrice.c
	 */
	// *************************************************************************
	// Determine value of ship
	// *************************************************************************
	public int enemyPrice( )
	{
		int curPrice;
		
		curPrice = type.price;
		for (int i=0; i<weapon.length; ++i)
			if (weapon[i] != null)
				curPrice += weapon[i].price;
		for (int i=0; i<shield.length; ++i)
			if (shield[i] != null)
				curPrice += shield[i].price;
		// Gadgets aren't counted in the price, because they are already taken into account in
		// the skill adjustment of the price.
				
		curPrice = curPrice * (2 * skill(Skill.PILOT) + skill(Skill.ENGINEER) + 3 * skill(Skill.FIGHTER))	/ 60;
				
		return curPrice;
	}
	
	// *************************************************************************
	// Determine value of current ship, including equipment.
	// *************************************************************************
	public int currentPriceWithoutCargo(boolean forInsurance) {
		int curPrice = 
			// Trade-in value is three-fourths the original price
			((type.price * (tribbles > 0 && !forInsurance? 1 : 3)) / 4)
			// subtract repair costs
			- (getHullStrength() - hull) * type.repairCosts 
			// subtract costs to fill tank with fuel
			- (getFuelTanks() - getFuel()) * type.costOfFuel // NB original uses type.fuelTanks instead of getFuelTanks() so the value of the extra fuel due to the compactor is not computed correctly.
			;
		// Add 2/3 of the price of each item of equipment
		for (Weapon w : weapon)
			if (w != null)
				curPrice += w.sellPrice();
		for (Shield s : shield)
			if (s != null)
				curPrice += s.sellPrice();
		for (Gadget g : gadget)
			if (g != null)
				curPrice += g.sellPrice();
				
		return curPrice;
	}
	
	
	// *************************************************************************
	// Determine value of current ship, including goods and equipment.
	// *************************************************************************
	public int currentPrice( boolean forInsurance ) {
		int curPrice = currentPriceWithoutCargo(forInsurance);
		
		for (TradeItem item : TradeItem.values()) {
			curPrice += mGameState.buyingPrice.get(item);
		}
		
		return curPrice;
	}
	
	
	/*
	 * Shipyard.c
	 */
	// *************************************************************************
	// Determine ship's hull strength
	// *************************************************************************
	public int getHullStrength()
	{
		if (this.equals(mGameState.ship) && mGameState.scarabStatus == 3)
			return type.hullStrength + UPGRADEDHULL;
		else
			return type.hullStrength;
	}
	
	
	/*
	 * Skill.c
	 */
	// *************************************************************************
	// NthLowest Skill. Returns skill with the nth lowest score
	// (i.e., 2 is the second worst skill). If there is a tie, it will return
	// in the order of Pilot, Fighter, Trader, Engineer.
	// *************************************************************************
	public Skill nthLowestSkill( int n )
	{
		int i = 0, lower = 1;
		Skill retVal = null;
		boolean looping = true;
		while (looping)
		{
			if (crew[0].pilot() == i)
			{
				if (lower == n)
				{
					looping = false;
					retVal = Skill.PILOT;
				}
				
				lower++;
			}
			if (crew[0].fighter() == i)
			{
				if (lower == n)
				{
					looping = false;
					retVal = Skill.FIGHTER;
				}
				
				lower++;
			}
			if (crew[0].trader() == i)
			{
				if (lower == n)
				{
					looping = false;
					retVal = Skill.TRADER;
				}
				
				lower++;
			}
			if (crew[0].engineer() == i)
			{
				if (lower == n)
				{
					looping = false;
					retVal = Skill.ENGINEER;
				}
				
				lower++;
			}
			i++;
		}
		return retVal;
	}
	

	// *************************************************************************
	// Determines whether a certain gadget is on board
	// *************************************************************************
	public boolean hasGadget(Gadget g) {
		for (Gadget gad : gadget) {
			if (g == gad) return true;
		}
		return false;
	}
	
	// *************************************************************************
	// Determines whether a certain shield type is on board
	// *************************************************************************
	public boolean hasShield(Shield s) {
		for (Shield sh : shield) {
			if (s == sh) return true;
		}
		return false;
	}
	
	// *************************************************************************
	// Determines whether a certain weapon type is on board. If exactCompare is
	// false, then better weapons will also return TRUE
	// *************************************************************************
	public boolean hasWeapon(Weapon w, boolean exactCompare) {
		for (Weapon weap : weapon) {
			if (weap == null)
				continue;
			if (w == weap || (weap.power > w.power && !exactCompare))
				return true;
		}
		return false;
	}
	
	
	/*
	 * SystemInfoEvent.c
	 */
	// *************************************************************************
	// Return available crew quarters
	// *************************************************************************
	public int availableQuarters() {
		return type.crewQuarters- (mGameState.jarekStatus == 1 ? 1 : 0) - (mGameState.wildStatus == 1 ? 1 : 0);
	}
	
	/*
	 * Traveler.c
	 */
	// *************************************************************************
	// Determine if ship is cloaked
	// *************************************************************************
	public boolean cloaked( Ship opp )
	{
		return (hasGadget(Gadget.CLOAKINGDEVICE) && (skill(Skill.ENGINEER) > opp.skill(Skill.ENGINEER)));
	}
	
	
	// *************************************************************************
	// Determine if there are any empty slots.
	// *************************************************************************
	public boolean anyEmptySlots(  )
	{
		for (int j=0; j<type.weaponSlots; ++j)
		{
			if (weapon[j] == null)
			{
				return true;
			}							
		}
		for (int j=0; j<type.shieldSlots; ++j)
		{
			if (shield[j] == null)
			{
				return true;
			}							
		}
		for (int j=0; j<type.gadgetSlots; ++j)
		{
			if (gadget[j] == null)
			{
				return true;
			}							
		}
		
		return false;
	}
	/*
	 * NB End adapted code.
	 */
	
	public int getCargo(TradeItem item) {
		return cargo.get(item);
	}
	
	public void addCargo(TradeItem item, int amount) {
		cargo.put(item, cargo.get(item) + amount);
		if (cargo.get(item) < 0) cargo.put(item, 0);
	}
	
	public void clearCargo(TradeItem item) {
		cargo.put(item, 0);
	}
	
	// This is an amalgam of the original pilotSkill(), fighterSkill(), traderSkill(), and engineerSkill() functions from Skill.c.
	public int skill(Skill s) {
		int maxSkill = -1;
		for (CrewMember c : crew) {
			if (c == null) continue;
			int skill = -1;
			switch (s) {
			case PILOT:
				skill = c.pilot();
				break;
			case FIGHTER:
				skill = c.fighter();
				break;
			case TRADER:
				skill = c.trader();
				break;
			case ENGINEER:
				skill = c.engineer();
				break;
			}
			if (skill > maxSkill) maxSkill = skill;
		}
		
		switch (s) {
		case PILOT:
			if (hasGadget(Gadget.NAVIGATINGSYSTEM)) {
				maxSkill += SKILLBONUS;
			}
			if (hasGadget(Gadget.CLOAKINGDEVICE)) {
				maxSkill += CLOAKBONUS;
			}
			break;
		case FIGHTER:
			if (hasGadget(Gadget.TARGETINGSYSTEM)) {
				maxSkill += SKILLBONUS;
			}
			break;
		case TRADER:
			if (mGameState.jarekStatus >= 2)
				++maxSkill;
			break;
		case ENGINEER:
			if (hasGadget(Gadget.AUTOREPAIRSYSTEM)) {
				maxSkill += SKILLBONUS;
			}
			break;
		}
		
		if (mGameState.difficulty.compareTo(DifficultyLevel.NORMAL) < 0) maxSkill++;
		if (mGameState.difficulty.compareTo(DifficultyLevel.HARD) > 0) maxSkill--;
		if (maxSkill < 1) maxSkill = 1;
		return maxSkill;
	}
}
