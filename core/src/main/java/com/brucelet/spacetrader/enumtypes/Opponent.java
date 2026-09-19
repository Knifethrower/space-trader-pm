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

public enum Opponent { // Doesn't implement XmlString to avoid ambiguity between two choices of String.

	POLICE        ( R.string.opponent_initial_police,       R.string.opponent_update_police,    R.drawable.police    ),
	PIRATE        ( R.string.opponent_initial_pirate,       R.string.opponent_update_pirate,    R.drawable.pirate    ),
	TRADER        ( R.string.opponent_initial_trader,       R.string.opponent_update_trader,    R.drawable.trader    ),
	DRAGONFLY     ( R.string.opponent_initial_dragonfly,    R.string.opponent_update_dragonfly, R.drawable.blankicon ),
	MONSTER       ( R.string.opponent_initial_monster,      R.string.opponent_update_monster,   R.drawable.blankicon ),
	MANTIS        ( R.string.opponent_initial_mantis,       R.string.opponent_update_mantis,    R.drawable.alien     ),
	SCARAB        ( R.string.opponent_initial_scarab,       R.string.opponent_update_scarab,    R.drawable.blankicon ),
	FAMOUSCAPTAIN ( R.string.opponent_initial_captain,      R.string.opponent_update_captain,   R.drawable.special   ),
	MARIECELESTE  ( R.string.opponent_initial_marieceleste, R.string.opponent_update_other,     R.drawable.blankicon ),
	POSTMARIE     ( R.string.opponent_initial_postmarie,    R.string.opponent_update_other,     R.drawable.blankicon ),
	BOTTLE        ( R.string.opponent_initial_bottle,       R.string.opponent_update_other,     R.drawable.blankicon ),
	;

	public final int initResId;
	public final int updateResId;
	public final int iconId;
	
	Opponent(int initResId, int updateResId, int iconId) {
		this.initResId = initResId;
		this.updateResId = updateResId;
		this.iconId = iconId;
	}
	
	public String toXmlStringInit(Resources res) {
		return res.getString(initResId);
	}
	
	public String toXmlStringUpdate(Resources res) {
		return res.getString(updateResId);
	}
}
