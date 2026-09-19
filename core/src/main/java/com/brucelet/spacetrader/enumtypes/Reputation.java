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

public enum Reputation implements XmlString {
	
	HARMLESS       ( R.string.reputation_harmless,          0 ),
	MOSTLYHARMLESS ( R.string.reputation_mostlyharmless,   10 ),
	POOR           ( R.string.reputation_poor,             20 ),
	AVERAGE        ( R.string.reputation_average,          40 ),
	ABOVEAVERAGE   ( R.string.reputation_aboveaverage,     80 ),
	COMPETENT      ( R.string.reputation_competent,       150 ),
	DANGEROUS      ( R.string.reputation_dangerous,       300 ),
	DEADLY         ( R.string.reputation_deadly,          600 ),
	ELITE          ( R.string.reputation_elite,          1500 ),
	;
	
	private final int resId;
	public final int score;
	
	Reputation(int resId, int minScore) {
		this.resId = resId;
		this.score = minScore;
	}
	
	public static Reputation forValue(int value) {
		Reputation out = HARMLESS;
		for (Reputation rep : Reputation.values()) {
			if (value < rep.score) {
				break;
			}
			out = rep;
		}
		return out;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
}
