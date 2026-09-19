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

public enum OpponentAction implements XmlString {

	INSPECTION    ( R.string.opponentaction_inspection    ),
	POSTMARIE     ( R.string.opponentaction_postmarie     ),
	HAILSURRENDER ( R.string.opponentaction_hailsurrender ),
	ATTACK        ( R.string.opponentaction_attack        ),
	FLEE          ( R.string.opponentaction_flee          ),
	IGNORE        ( R.string.opponentaction_ignore        ),
	CLOAKED       ( R.string.opponentaction_cloaked       ),
	SURRENDER     ( R.string.opponentaction_surrender     ),
	TRADE         ( R.string.opponentaction_trade         ),
	MARIECELESTE  ( R.string.opponentaction_marieceleste  ),
	MEETCAPTAIN   ( R.string.opponentaction_meetcaptain   ),
	BOTTLE        ( R.string.opponentaction_bottle        ),
	;
	
	private final int resId; 
	
	OpponentAction(int resId) {
		this.resId = resId;
	}

	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}

}
