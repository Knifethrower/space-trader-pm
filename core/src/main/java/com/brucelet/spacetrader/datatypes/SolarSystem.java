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

import com.brucelet.spacetrader.enumtypes.PoliceRecord;
import com.brucelet.spacetrader.enumtypes.Politics;
import com.brucelet.spacetrader.enumtypes.Size;
import com.brucelet.spacetrader.enumtypes.SpecialEvent;
import com.brucelet.spacetrader.enumtypes.SpecialResources;
import com.brucelet.spacetrader.enumtypes.Status;
import com.brucelet.spacetrader.enumtypes.TechLevel;
import com.brucelet.spacetrader.enumtypes.TradeItem;

public class SolarSystem {

	public final String name;
	private TechLevel techLevel;		// Tech level
	private Politics politics;		// Political system
	private Status status;			// Status
	private int x;					// X-coordinate (galaxy width = 150)
	private int y;					// Y-coordinate (galaxy height = 100)
	public final SpecialResources specialResources;	// Special resources
	public final Size size;				// System size
	private final Map<TradeItem, Integer> qty = new EnumMap<>(TradeItem.class);// Quantities of tradeitems. These change very slowly over time.
	private int countDown;			// Countdown for reset of tradeitems.
	private boolean visited;			// Visited Yes or No
	private SpecialEvent special;				// Special event

	private final GameState mGameState;

	private final int startCountDown;

	public SolarSystem(GameState game, String name, TechLevel techLevel, Politics politics, Status status, int x, int y, SpecialResources specialResources, Size size) {
		mGameState = game;

		this.name = name;
		this.techLevel = techLevel;
		this.politics = politics;
		this.status = status;
		this.x = x;
		this.y = y;
		this.specialResources = specialResources;
		this.size = size;

		special = null;		
		countDown = 0;
		visited = false;

		startCountDown = 3 + mGameState.difficulty.ordinal();

		initializeTradeItems();
	}
	
	public SolarSystem(SharedPreferences prefs, String tag, GameState game) {
		mGameState = game;
		
		name = prefs.getString(tag+"_name", "");
		techLevel = TechLevel.values()[prefs.getInt(tag+"_techLevel", 0)];
		politics = Politics.values()[prefs.getInt(tag+"_politics", 0)];
		status = Status.values()[prefs.getInt(tag+"_status", 0)];
		x = prefs.getInt(tag+"_x", 0);
		y = prefs.getInt(tag+"_y", 0);
		specialResources = SpecialResources.values()[prefs.getInt(tag+"_specialResources", 0)];
		size = Size.values()[prefs.getInt(tag+"_size", 0)];
		for (TradeItem item : TradeItem.values()) {
			qty.put(item, prefs.getInt(tag+"_qty_"+item, 0));
		}
		countDown = prefs.getInt(tag+"_countdown", 0);
		visited = prefs.getBoolean(tag+"_visited", false);
		int specialIndex = prefs.getInt(tag+"_special", 0);
		special = specialIndex >= 0? SpecialEvent.values()[specialIndex] : null;

		startCountDown = 3 + mGameState.difficulty.ordinal();
	}
	
	public void saveState(SharedPreferences.Editor editor, String tag) {
		editor.putString(tag+"_name", name);
		editor.putInt(tag+"_techLevel", techLevel.ordinal());
		editor.putInt(tag+"_politics", politics.ordinal());
		editor.putInt(tag+"_status", status.ordinal());
		editor.putInt(tag+"_x", x);
		editor.putInt(tag+"_y", y);
		editor.putInt(tag+"_specialResources", specialResources.ordinal());
		editor.putInt(tag+"_size", size.ordinal());
		for (TradeItem item : TradeItem.values()) {
			editor.putInt(tag+"_qty_"+item, qty.get(item));
		}
		editor.putInt(tag+"_countDown", countDown);
		editor.putBoolean(tag+"_visited", visited);
		editor.putInt(tag+"_special", special == null? -1 : special.ordinal());
	}
	
	/*
	 * The following code adapted from the original palmos source as noted.
	 */
	/*
	 * spacetrader.h
	 */
	public int strengthPolice(int policeRecordScore) {
		return (policeRecordScore < PoliceRecord.PSYCHOPATH.score? 3 :
			policeRecordScore < PoliceRecord.VILLAIN.score? 2 : 1)
			* politics.strengthPolice.ordinal();
	}
	
	
	/*
	 * Traveler.c
	 */
	// *************************************************************************
	// Initialize qunatities of trade items of system Index
	// *************************************************************************
	private void initializeTradeItems()
	{
		for (TradeItem item : TradeItem.values())
		{
			if (((item == TradeItem.NARCOTICS) &&
					(!politics.drugsOK)) ||
					((item == TradeItem.FIREARMS) &&
							(!politics.firearmsOK)) ||
							(techLevel.compareTo(item.techProduction)) < 0)
			{
				clearQty(item);
				continue;
			}

			setQty(item,  
					((9 + GameState.getRandom( 5 )) - 
							GameState.abs( item.techTopProduction.ordinal() - techLevel.ordinal() )) * 
							(1 + size.ordinal())	// NB Original had a bug here where it used the index of the tradeitem instead of the system to check the size. (In the language of Traveler.c, it used SolarSystem[i] instead of SolarSystem[index]) 
					);

			// Because of the enormous profits possible, there shouldn't be too many robots or narcotics available
			if (item == TradeItem.ROBOTS || item == TradeItem.NARCOTICS)
				setQty(item, ((getQty(item) * (5 - mGameState.difficulty.ordinal())) / (6 - mGameState.difficulty.ordinal())) + 1 );

			if (item.cheapResource != null)
				if (specialResources == item.cheapResource)
					setQty(item, (getQty(item) * 4) / 3 );


			if (item.expensiveResource != null)
				if (specialResources == item.expensiveResource)
					setQty(item, (getQty(item) * 3) >> 2 );

			if (item.doublePriceStatus != null)
				if (status == item.doublePriceStatus)
					setQty(item, getQty(item) / 5 );

			addQty(item, -GameState.getRandom(10) + GameState.getRandom(10));

			if (getQty(item) < 0)
				clearQty(item);
		}
	}

	
	// *************************************************************************
	// Status of solar systems may change over time. Used on Arrival to a System
	// *************************************************************************
	public void shuffleStatus(  )
	{
		if (status != Status.UNEVENTFUL) {
			if (GameState.getRandom(100) < 15)
				status = Status.UNEVENTFUL;
		}
		else if (GameState.getRandom(100) < 15) {
			status = GameState.getRandom(Status.values(), 1);
		}
	}
	
	
	// *************************************************************************
	// After a warp, the quantities of items available form a system change
	// slightly. After the countdown of a solar system has reached zero, the 
	// quantities are reset. This ensures that it isn't really worth the commander's
	// while to just travel between two neighboring systems.
	// *************************************************************************
	public void changeQuantities( )
	{
		if (countDown > 0)
		{
			--countDown;
			if (countDown > startCountDown)
				countDown = startCountDown;
			else if (countDown <= 0)
				initializeTradeItems();
			else
			{
				for (TradeItem item : TradeItem.values())
				{
					if (((item == TradeItem.NARCOTICS) &&
							(!politics.drugsOK)) ||
							((item == TradeItem.FIREARMS) &&
									(!politics.firearmsOK)) ||
									(techLevel.compareTo(item.techProduction) < 0))
					{
						clearQty(item);
						continue;
					}
					else
					{
						addQty(item, GameState.getRandom(5) - GameState.getRandom(5));
						if (getQty(item) < 0)
							clearQty(item);
					}
				}
			}
		}
	}
	/*
	 * NB End adapted code.
	 */
	
	// New function to swap coordinates
	public void swapLocation(SolarSystem other) {
		int otherX = other.x;
		int otherY = other.y;
		other.x = this.x;
		other.y = this.y;
		this.x = otherX;
		this.y = otherY;
	}
	
	public int x() {
		return x;
	}
	public int y() {
		return y;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getQty(TradeItem item) {
		return qty.get(item);
	}

	public void setQty(TradeItem item, int amount) {
		qty.put(item, amount);
	}

	public void addQty(TradeItem item, int amount) {
		qty.put(item, qty.get(item) + amount);
		if (qty.get(item) < 0) qty.put(item, 0);
	}

	public void clearQty(TradeItem item) {
		qty.put(item, 0);
	}

	public boolean visited() {
		return visited;
	}

	public void visit() {
		visited = true;
	}
	
	public Status status() {
		return status;
	}
	
	public TechLevel techLevel() {
		return techLevel;
	}
	
	public Politics politics() {
		return politics;
	}
	
	public SpecialEvent special() {
		return special;
	}
	
	public void setSpecial(SpecialEvent special) {
		this.special = special;
	}
	
	// Called if Gemulon is invaded
	public void invade() {
		special = SpecialEvent.GEMULONINVADED;
		techLevel = TechLevel.PREAGRICULTURAL;
		politics = Politics.ANARCHY;
	}
	
	public void resetCountDown() {
		countDown = startCountDown;
	}
	


}
