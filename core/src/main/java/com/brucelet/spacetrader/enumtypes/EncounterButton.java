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

import com.brucelet.spacetrader.R;

import com.brucelet.spacetrader.platform.Resources;

public enum EncounterButton implements XmlString {

	ATTACK    ( R.string.encounterbutton_attack    ),
	SURRENDER ( R.string.encounterbutton_surrender ),
	IGNORE    ( R.string.encounterbutton_ignore    ),
	FLEE      ( R.string.encounterbutton_flee      ),
	SUBMIT    ( R.string.encounterbutton_submit    ),
	BRIBE     ( R.string.encounterbutton_bribe     ),
	PLUNDER   ( R.string.encounterbutton_plunder   ),
	INTERRUPT ( R.string.encounterbutton_interrupt ),
	DRINK     ( R.string.encounterbutton_drink     ),
	BOARD     ( R.string.encounterbutton_board     ),
	MEET      ( R.string.encounterbutton_meet      ),
	YIELD     ( R.string.encounterbutton_yield     ),
	TRADE     ( R.string.encounterbutton_trade     ),
	
	TRIBBLE   ( R.string.encounterbutton_tribble  ),
	;
	
	private final int resId; 
	
	EncounterButton(int resId) {
		this.resId = resId;
	}

	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}

}
