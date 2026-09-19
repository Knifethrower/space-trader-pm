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
package com.brucelet.spacetrader.enumtypes;

import com.brucelet.spacetrader.platform.Resources;

import com.brucelet.spacetrader.R;
import com.brucelet.spacetrader.enumtypes.TechLevel;

import static com.brucelet.spacetrader.enumtypes.SpecialResources.*;
import static com.brucelet.spacetrader.enumtypes.Status.*;

public enum TradeItem implements XmlString {
	WATER     ( R.string.tradeitem_water,     0, 0, 2,   30,   +3,   4, DROUGHT,       LOTSOFWATER,    DESERT,        30,   50,   1 ),
	FURS      ( R.string.tradeitem_furs, 	  0, 0, 0,  250,  +10,  10, COLD,          RICHFAUNA,      LIFELESS,     230,  280,   5 ),
	FOOD      ( R.string.tradeitem_food, 	  1, 0, 1,  100,   +5,   5, CROPFAILURE,   RICHSOIL,       POORSOIL,      90,  160,   5 ),
	ORE       ( R.string.tradeitem_ore, 	  2, 2, 3,  350,  +20,  10, WAR,           MINERALRICH,    MINERALPOOR,  350,  420,  10 ),
	GAMES     ( R.string.tradeitem_games,     3, 1, 6,  250,  -10,   5, BOREDOM,       ARTISTIC,       null,         160,  270,   5 ),
	FIREARMS  ( R.string.tradeitem_firearms,  3, 1, 5, 1250,  -75, 100, WAR,           WARLIKE,        null,         600, 1100,  25 ),
	MEDICINE  ( R.string.tradeitem_medicine,  4, 1, 6,  650,  -20,  10, PLAGUE,        LOTSOFHERBS,    null,         400,  700,  25 ),
	MACHINERY ( R.string.tradeitem_machinery, 4, 3, 5,  900,  -30,   5, LACKOFWORKERS, null,           null,         600,  800,  25 ),
	NARCOTICS ( R.string.tradeitem_narcotics, 5, 0, 5, 3500, -125, 150, BOREDOM,       WEIRDMUSHROOMS, null,        2000, 3000,  50 ),
	ROBOTS    ( R.string.tradeitem_robots,    6, 4, 7, 5000, -150, 100, LACKOFWORKERS, null,           null,        3500, 5000, 100 ),
	;
	
	private final int resId;
	public final TechLevel techProduction; 			// Tech level needed for production
	public final TechLevel techUsage;				// Tech level needed to use
	public final TechLevel techTopProduction;		// Tech level which produces this item the most
	public final int priceLowTech;					// Medium price at lowest tech level			
	public final int priceInc;						// Price increase per tech level
	public final int variance;						// Max percentage above or below calculated price
	public final Status doublePriceStatus;			// Price increases considerably when this event occurs
	public final SpecialResources cheapResource;	// When this resource is available, this trade item is cheap
	public final SpecialResources expensiveResource;// When this resource is available, this trade item is expensive
	public final int minTradePrice;					// Minimum price to buy/sell in orbit
	public final int maxTradePrice;					// Minimum price to buy/sell in orbit
	public final int roundOff;						// Roundoff price for trade in orbit
	
	
	TradeItem(
			int resId,
			int techProduction,
			int techUsage,
			int techTopProduction,
			int priceLowTech,
			int priceInc,
			int variance,
			Status doublePriceStatus,
			SpecialResources cheapResource,
			SpecialResources expensiveResource,
			int minTradePrice,
			int maxTradePrice,
			int roundOff
	) {
		this.resId = resId;
		this.techProduction = TechLevel.values()[techProduction];
		this.techUsage = TechLevel.values()[techUsage];
		this.techTopProduction = TechLevel.values()[techTopProduction];
		this.priceLowTech = priceLowTech;			
		this.priceInc = priceInc;
		this.variance = variance;
		this.doublePriceStatus = doublePriceStatus;
		this.cheapResource = cheapResource;
		this.expensiveResource = expensiveResource;
		this.minTradePrice = minTradePrice;
		this.maxTradePrice = maxTradePrice;
		this.roundOff = roundOff;
		
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
	

}
