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

import com.brucelet.spacetrader.platform.SharedPreferences;

import com.brucelet.spacetrader.enumtypes.DifficultyLevel;
import com.brucelet.spacetrader.enumtypes.EndStatus;

public class HighScore {
	
	public final String name;
	public final EndStatus status; // 0 = killed, 1 = Retired, 2 = Bought moon
	public final int days;
	public final int worth;
	public final DifficultyLevel difficulty;
	public final int score;

	public HighScore(String name, EndStatus status, int days, int worth, DifficultyLevel difficulty) {
		this.name = name;
		this.status = status;
		this.days = days;
		this.worth = worth;
		this.difficulty = difficulty;
		
		this.score = getScore();
	}
	
	public HighScore(SharedPreferences prefs, String tag) {
		this.name = prefs.getString(tag+"_name", null);
		this.status = EndStatus.values()[prefs.getInt(tag+"_status", 0)];
		this.days = prefs.getInt(tag+"_days", 0);
		this.worth = prefs.getInt(tag+"_worth", 0);
		this.difficulty= DifficultyLevel.values()[prefs.getInt(tag+"_difficulty", 0)];

		this.score = getScore();
	}
	
	public void saveState(SharedPreferences.Editor editor, String tag) {
		editor.putString(tag+"_name", name);
		editor.putInt(tag+"_status", status.ordinal());
		editor.putInt(tag+"_days", days);
		editor.putInt(tag+"_worth", worth);
		editor.putInt(tag+"_difficulty", difficulty.ordinal());
	}
	
	/*
	 * Traveler.c
	 */
	// *************************************************************************
	// Calculate the score
	// *************************************************************************
	private int getScore()
	{

		int worth = (this.worth < 1000000 ? this.worth : 1000000 + ((this.worth - 1000000) / 10) );

		switch (status) {
		case KILLED:
			return (difficulty.ordinal()+1)*((worth * 90) / 50000);
		case RETIRED:
			return (difficulty.ordinal()+1)*((worth * 95) / 50000);
		case MOON:
			int d = ((difficulty.ordinal()+1)*100) - days;
			if (d < 0)
				d = 0;
			return (difficulty.ordinal()+1)*((worth + (d * 1000)) / 500);
		default:
			return 0;
		}
	}
	
}
