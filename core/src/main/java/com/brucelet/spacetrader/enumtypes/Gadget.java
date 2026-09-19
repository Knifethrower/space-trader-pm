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

public enum Gadget implements XmlString, Purchasable {
	
	EXTRABAYS        ( R.string.gadget_extrabays,          2500, 4, 35 ), // 5 extra holds
	AUTOREPAIRSYSTEM ( R.string.gadget_autorepair,         7500, 5, 20 ), // Increases engineer's effectivity
	NAVIGATINGSYSTEM ( R.string.gadget_navigating,        15000, 6, 20 ), // Increases pilot's effectivity
	TARGETINGSYSTEM  ( R.string.gadget_targeting,         25000, 6, 20 ), // Increases fighter's effectivity
	CLOAKINGDEVICE   ( R.string.gadget_cloaking,         100000, 7,  5 ), // If you have a good engineer, nor pirates nor police will notice you
	// The gadgets below can't be bought
	FUELCOMPACTOR    ( R.string.gadget_fuelcompactor,     30000, 8,  0 ),
	;
	
	private static final Gadget[] BUYABLE_VALUES = { EXTRABAYS, AUTOREPAIRSYSTEM, NAVIGATINGSYSTEM, TARGETINGSYSTEM, CLOAKINGDEVICE };
	
	public final int resId;
	public final int price;
	public final TechLevel techLevel;
	public final int chance; // Chance that this is fitted in a slot
	
	Gadget(int resId, int price, int techLevel, int chance) {
		this.resId = resId;
		this.price = price;
		
		this.techLevel = 
				(techLevel >= 0 && techLevel < TechLevel.values().length)? TechLevel.values()[techLevel] : null;
				
		this.chance = chance;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}

	@Override
	public int buyPrice(TechLevel systemTechLevel, int traderSkill) {
		return Impl.buyPrice(price, techLevel, systemTechLevel, traderSkill);
	}

	@Override
	public int sellPrice() {
		return Purchasable.Impl.sellPrice(price);
	}

	@Override
	public TechLevel techLevel() {
		return techLevel;
	}

	public static Gadget[] buyableValues() {
		return BUYABLE_VALUES.clone();
	}
}
