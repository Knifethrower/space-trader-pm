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

public enum ShipType implements XmlString, Purchasable {

	FLEA        ( R.string.shiptype_flea,        10, 0, 0, 0, 1, 20,       4,  1,   2000,   5,  2,  25, -1, -1,  0, 1, 0, R.drawable.flea,        3403, 6438,    0,    0 ),
	GNAT        ( R.string.shiptype_gnat,        15, 1, 0, 1, 1, 14,       5,  2,  10000,  50, 28, 100,  0,  0,  0, 1, 1, R.drawable.gnat,        2768, 7142,    0,    0 ),
	FIREFLY     ( R.string.shiptype_firefly,     20, 1, 1, 1, 1, 17,       5,  3,  25000,  75, 20, 100,  0,  0,  0, 1, 1, R.drawable.firefly,     2858, 7023, 2709, 7172 ),
	MOSQUITO    ( R.string.shiptype_mosquito,    15, 2, 1, 1, 1, 13,       5,  5,  30000, 100, 20, 100,  0,  1,  0, 1, 1, R.drawable.mosquito,    2848, 7052, 2699, 7202 ),
	BUMBLEBEE   ( R.string.shiptype_bumblebee,   25, 1, 2, 2, 2, 15,       5,  7,  60000, 125, 15, 100,  1,  1,  0, 1, 2, R.drawable.bumblebee,   1875, 8134, 1727, 8283 ),
	BEETLE      ( R.string.shiptype_beetle,      50, 0, 1, 1, 3, 14,       5, 10,  80000,  50,  3,  50, -1, -1,  0, 1, 2, R.drawable.beetle,      1875, 8125, 1727, 8273 ),
	HORNET      ( R.string.shiptype_hornet,      20, 3, 2, 1, 2, 16,       6, 15, 100000, 200,  6, 150,  2,  3,  1, 2, 3, R.drawable.hornet,       983, 8928,  834, 9077 ), 
    GRASSHOPPER ( R.string.shiptype_grasshopper, 30, 2, 2, 3, 3, 15,       6, 15, 150000, 300,  2, 150,  3,  4,  2, 3, 3, R.drawable.grasshopper, 1072, 8928,  923, 9077 ),
	TERMITE     ( R.string.shiptype_termite,     60, 1, 3, 2, 3, 13,       7, 20, 225000, 300,  2, 200,  4,  5,  3, 4, 4, R.drawable.termite,      268, 9642,  120, 9791 ),
	WASP        ( R.string.shiptype_wasp,        35, 3, 2, 2, 3, 14,       7, 20, 300000, 500,  2, 200,  5,  6,  4, 5, 4, R.drawable.wasp,         358, 9642,  209, 9791 ),
	// The ships below can't be bought
	MONSTER    	( R.string.shiptype_monster,      0, 3, 0, 0, 1,  1,       8,  1, 500000,   0,  0, 500,  8,  8,  8, 1, 4, R.drawable.monster,     1072, 8839,    0,    0 ),
    DRAGONFLY	( R.string.shiptype_dragonfly,    0, 2, 3, 2, 1,  1,       8,  1, 500000,   0,  0,  10,  8,  8,  8, 1, 1, R.drawable.dragonfly,   3215, 6785,  883, 9107 ),
	MANTIS      ( R.string.shiptype_mantis,       0, 3, 1, 3, 3,  1,       8,  1, 500000,   0,  0, 300,  8,  8,  8, 1, 2, R.drawable.mantis,      2322, 7867, 2173, 8015 ),
	SCARAB      ( R.string.shiptype_scarab,      20, 2, 0, 0, 2,  1,       8,  1, 500000,   0,  0, 400,  8,  8,  8, 1, 3, R.drawable.scarab,      1102, 8720,    0,    0 ),
	BOTTLE      ( R.string.shiptype_bottle,       0, 0, 0, 0, 0,  1,       8,  1,    100,   0,  0,  10,  8,  8,  8, 1, 1, R.drawable.bottle,         0,    0,    0,    0 ),
	;

	private static final ShipType[] BUYABLE_VALUES = { FLEA, GNAT, FIREFLY, MOSQUITO, BUMBLEBEE, BEETLE, HORNET, GRASSHOPPER, TERMITE, WASP };

	private final int resId;
	public final int cargoBays;		// Number of cargo bays
	public final int weaponSlots;	// Number of lasers possible
	public final int shieldSlots;	// Number of shields possible
	public final int gadgetSlots;	// Number of gadgets possible (e.g. docking computers)
	public final int crewQuarters;	// Number of crewmembers possible
	public final int fuelTanks;		// Each tank contains enough fuel to travel 10 parsecs
	public final TechLevel minTechLevel;	// Minimum tech level needed to build ship
	public final int costOfFuel;	// Cost to fill one tank with fuel
	public final int price;			// Base ship cost
	public final int bounty;		// Base bounty
	public final int occurrence;	// Percentage of the ships you meet
	public final int hullStrength;	// Hull strength
	public final ActivityLevel police;	// Encountered as police with at least this strength
	public final ActivityLevel pirates;	// idem Pirates
	public final ActivityLevel traders;	// idem Traders
	public final int repairCosts;	// Repair costs for 1 point of hull strength.
	public final Size size;			// Determines how easy it is to hit this ship
	
	public final int drawableId;	// Resource id of ship image (layerdrawable)
	public final int damageMin;		// Minimum level for damage display
	public final int damageMax;		// Maximum level for damage display
	public final int shieldMin;		// Minimum level for shield display
	public final int shieldMax;		// Maximum level for shield display
	
	ShipType(
			int resId,
			int cargoBays,
			int weaponSlots,
			int shieldSlots,
			int gadgetSlots,
			int crewQuarters,
			int fuelTanks,
			int minTechLevel,
			int costOfFuel,
			int price,
			int bounty,
			int occurrence,
			int hullStrength,
			int police,
			int pirates,
			int traders,
			int repairCosts,
			int size,
			int drawableId,
			int damageMin,
			int damageMax,
			int shieldMin,
			int shieldMax
	) {
		this.resId = resId;
		this.cargoBays = cargoBays;
		this.weaponSlots = weaponSlots;
		this.shieldSlots = shieldSlots;
		this.gadgetSlots = gadgetSlots;
		this.crewQuarters = crewQuarters;
		this.fuelTanks = fuelTanks;
		
		this.minTechLevel = 
				(minTechLevel >= 0 && minTechLevel < TechLevel.values().length)? TechLevel.values()[minTechLevel] : null;
		
		this.costOfFuel = costOfFuel;
		this.price = price;
		this.bounty = bounty;
		this.occurrence = occurrence;
		this.hullStrength = hullStrength;

		this.police  = (police  >= 0 && police  < ActivityLevel.values().length)? ActivityLevel.values()[police ] : null;
		this.pirates = (pirates >= 0 && pirates < ActivityLevel.values().length)? ActivityLevel.values()[pirates] : null;
		this.traders = (traders >= 0 && traders < ActivityLevel.values().length)? ActivityLevel.values()[traders] : null;

		this.repairCosts = repairCosts;
		this.size = Size.values()[size];
		
		this.drawableId = drawableId;
		this.damageMin = damageMin;
		this.damageMax = damageMax;
		this.shieldMin = shieldMin;
		this.shieldMax = shieldMax;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}

	@Override
	public int buyPrice(TechLevel systemTechLevel, int traderSkill) {
		return Impl.buyPrice(price, minTechLevel, systemTechLevel, traderSkill);
	}

	@Override
	public int sellPrice() {
		return Impl.sellPrice(price);
	}

	@Override
	public TechLevel techLevel() {
		return minTechLevel;
	}

	public static ShipType[] buyableValues() {
		return BUYABLE_VALUES.clone();
	}

}
