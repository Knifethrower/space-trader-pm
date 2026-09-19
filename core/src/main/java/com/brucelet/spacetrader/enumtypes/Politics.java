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

import static com.brucelet.spacetrader.enumtypes.TradeItem.*;

public enum Politics implements XmlString {

	ANARCHY      ( R.string.politics_anarchy,      0, 0, 7, 1, 0, 5, 7, true,  true,  FOOD,      R.array.masthead_anarchy,      R.array.headline_anarchy      ),
	CAPITALIST   ( R.string.politics_capitalist,   2, 3, 2, 7, 4, 7, 1, true,  true,  ORE,       R.array.masthead_capitalist,   R.array.headline_capitalist   ),
	COMMUNIST    ( R.string.politics_communist,    6, 6, 4, 4, 1, 5, 5, true,  true,  null,      R.array.masthead_communist,    R.array.headline_communist    ),
	CONFEDERACY  ( R.string.politics_confederacy,  5, 4, 3, 5, 1, 6, 3, true,  true,  GAMES,     R.array.masthead_confederacy,  R.array.headline_confederacy  ),
	CORPORATE    ( R.string.politics_corporate,    2, 6, 2, 7, 4, 7, 2, true,  true,  ROBOTS,    R.array.masthead_corporate,    R.array.headline_corporate    ),
	CYBERNETIC   ( R.string.politics_cybernetic,   0, 7, 7, 5, 6, 7, 0, false, false, ORE,       R.array.masthead_cybernetic,   R.array.headline_cybernetic   ),
	DEMOCRACY    ( R.string.politics_democracy,    4, 3, 2, 5, 3, 7, 2, true,  true,  GAMES,     R.array.masthead_democracy,    R.array.headline_democracy    ),
	DICTATORSHIP ( R.string.politics_dictatorship, 3, 4, 5, 3, 0, 7, 2, true,  true,  null,      R.array.masthead_dictatorship, R.array.headline_dictatorship ),
	FASCIST      ( R.string.politics_fascist,      7, 7, 7, 1, 4, 7, 0, false, true,  MACHINERY, R.array.masthead_fascist,      R.array.headline_fascist      ),
	FEUDAL       ( R.string.politics_feudal,       1, 1, 6, 2, 0, 3, 6, true,  true,  FIREARMS,  R.array.masthead_feudal,       R.array.headline_feudal       ),
	MILITARY     ( R.string.politics_military,     7, 7, 0, 6, 2, 7, 0, false, true,  ROBOTS,    R.array.masthead_military,     R.array.headline_military     ),
	MONARCHY     ( R.string.politics_monarchy,     3, 4, 3, 4, 0, 5, 4, true,  true,  MEDICINE,  R.array.masthead_monarchy,     R.array.headline_monarchy     ),
	PACIFIST     ( R.string.politics_pacifist,     7, 2, 1, 5, 0, 3, 1, true,  false, null,      R.array.masthead_pacifist,     R.array.headline_pacifist     ),
	SOCIALIST    ( R.string.politics_socialist,    4, 2, 5, 3, 0, 5, 6, true,  true,  null,      R.array.masthead_socialist,    R.array.headline_socialist    ),
	SATORI       ( R.string.politics_satori,       0, 1, 1, 1, 0, 1, 0, false, false, null,      R.array.masthead_satori,       R.array.headline_satori       ),
	TECHNOCRACY  ( R.string.politics_technocracy,  1, 6, 3, 6, 4, 7, 2, true,  true,  WATER,     R.array.masthead_technocracy,  R.array.headline_technocracy  ),
	THEOCRACY    ( R.string.politics_theocracy,    5, 6, 1, 4, 0, 4, 0, true,  true,  NARCOTICS, R.array.masthead_theocracy,    R.array.headline_theocracy    ),
	;
	
	public final int resId;
	public final int reactionIllegal;		// Reaction level of illegal goods 0 = total acceptance (determines how police reacts if they find you carry them)
	public final ActivityLevel strengthPolice;	// Strength level of police force 0 = no police (determines occurrence rate)
	public final ActivityLevel strengthPirates;	// Strength level of pirates 0 = no pirates
	public final ActivityLevel strengthTraders;	// Strength levcel of traders 0 = no traders
	public final TechLevel minTechLevel;	// Mininum tech level needed
	public final TechLevel maxTechLevel;	// Maximum tech level where this is found
	public final int bribeLevel;			// Indicates how easily someone can be bribed 0 = unbribeable/high bribe costs
	public final boolean drugsOK;			// Drugs can be traded (if not, people aren't interested or the governemnt is too strict)
	public final boolean firearmsOK;		// Firearms can be traded (if not, people aren't interested or the governemnt is too strict)
	public final TradeItem wanted;			// Tradeitem requested in particular in this type of government
	
	public final int mastheadId;
	public final int headlineId;
	
	Politics(
			int resId,
			int reactionIllegal,
			int strengthPolice,
			int strengthPirates,
			int strengthTraders,
			int minTechLevel,
			int maxTechLevel,
			int bribeLevel,
			boolean drugsOK,
			boolean firearmsOK,
			TradeItem wanted,
			int mastheadId,
			int headlineId
	) {
		this.resId = resId;
		this.reactionIllegal = reactionIllegal;
		this.strengthPolice = ActivityLevel.values()[strengthPolice];
		this.strengthPirates = ActivityLevel.values()[strengthPirates];
		this.strengthTraders = ActivityLevel.values()[strengthTraders];
		this.minTechLevel = TechLevel.values()[minTechLevel];
		this.maxTechLevel = TechLevel.values()[maxTechLevel];
		this.bribeLevel = bribeLevel;
		this.drugsOK = drugsOK;
		this.firearmsOK = firearmsOK;
		this.wanted = wanted;
		this.mastheadId = mastheadId;
		this.headlineId = headlineId;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
	
}
