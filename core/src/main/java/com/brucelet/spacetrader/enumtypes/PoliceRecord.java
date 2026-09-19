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

public enum PoliceRecord implements XmlString {
	MINIMUM    ( R.string.policerecord_minimum,    -100 ),
	PSYCHOPATH ( R.string.policerecord_psychopath,  -70 ),
	VILLAIN    ( R.string.policerecord_villain,     -30 ),
	CRIMINAL   ( R.string.policerecord_criminal,    -10 ),
	DUBIOUS    ( R.string.policerecord_dubious,      -5 ),
	CLEAN      ( R.string.policerecord_clean,         0 ),
	LAWFUL     ( R.string.policerecord_lawful,        5 ),
	TRUSTED    ( R.string.policerecord_trusted,      10 ),
	HELPER     ( R.string.policerecord_helper,       25 ),
	HERO       ( R.string.policerecord_hero,         75 ),
	;
	
	public static final int ATTACKPOLICESCORE   = -3;
	public static final int KILLPOLICESCORE     = -6;
	public static final int CAUGHTWITHWILDSCORE	= -4;
	public static final int ATTACKTRADERSCORE   = -2;
	public static final int PLUNDERTRADERSCORE  = -2;
	public static final int KILLTRADERSCORE     = -4;
	public static final int ATTACKPIRATESCORE   =  0;
	public static final int KILLPIRATESCORE     =  1;
	public static final int PLUNDERPIRATESCORE  = -1;
	public static final int TRAFFICKING         = -1;
	public static final int FLEEFROMINSPECTION  = -2;
	public static final int TAKEMARIENARCOTICS	= -4;
	
	private final int resId;
	public final int score;
	
	PoliceRecord(int resId, int score) {
		this.resId = resId;
		this.score = score;
	}
	
	public static PoliceRecord forValue(int value) {
		PoliceRecord out = MINIMUM;
		for (PoliceRecord rec : PoliceRecord.values()) {
			if (value < rec.score) {
				break;
			}
			out = rec;
		}
		return out;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
}
