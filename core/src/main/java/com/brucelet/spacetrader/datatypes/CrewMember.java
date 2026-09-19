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

import com.brucelet.spacetrader.enumtypes.DifficultyLevel;
import com.brucelet.spacetrader.enumtypes.Skill;

import com.brucelet.spacetrader.platform.SharedPreferences;


public class CrewMember {

	public final String name;
	private int pilot;
	private int fighter;
	private int trader;
	private int engineer;
	private SolarSystem curSystem;
	
	private final GameState mGameState;

	public CrewMember(String name, int pilot, int fighter, int trader, int engineer, GameState game) {
		this.name = name;
		this.pilot = pilot;
		this.fighter = fighter;
		this.trader = trader;
		this.engineer = engineer;
		mGameState = game;
	}
	
	public CrewMember(SharedPreferences prefs, String tag, GameState game) {
		this.name = prefs.getString(tag+"_name", "");
		this.pilot = prefs.getInt(tag+"_pilot", 0);
		this.fighter = prefs.getInt(tag+"_fighter", 0);
		this.trader = prefs.getInt(tag+"_trader", 0);
		this.engineer = prefs.getInt(tag+"_engineer", 0);
		
//		this.curSystem = ???

		mGameState = game;
	}
	
	public void saveState(SharedPreferences.Editor editor, String tag) {
		editor.putString(tag+"_name", name);
		editor.putInt(tag+"_pilot", pilot);
		editor.putInt(tag+"_fighter", fighter);
		editor.putInt(tag+"_trader", trader);
		editor.putInt(tag+"_engineer", engineer);
		editor.putString(tag+"_curSystem", curSystem == null? "" : curSystem.name);
	}
	
	public int pilot() {
		return pilot;
	}
	public int fighter() {
		return fighter;
	}
	public int trader() {
		return trader;
	}
	public int engineer() {
		return engineer;
	}
	
	public SolarSystem curSystem() {
		return curSystem;
	}
	public void setSystem(SolarSystem system) {
		curSystem = system;
	}
	
	// NB Tried to remove this usage and explicitly call CrewMember.name, but leaving this in in case I missed some
	@Override
	public String toString() {
		return name;
	}

	
	/*
	 * The following code adapted from the original palmos source as noted.
	 */
	/*
	 * spacetrader.h
	 */
	public int hirePrice() {
		if (this == mGameState.mercenary[mGameState.mercenary.length-1] && mGameState.wildStatus == 2)
			return 0; // NB Palm version appears to accidently not ever trip this so Zeethibal still has standard wage. Using length-1 should fix this.
		return (pilot + fighter + trader + engineer) * 3;
	}
	

	/*
	 * Skill.c
	 */
	// *************************************************************************
	// Increase one of the skills of the commander
	// *************************************************************************
	public void increaseRandomSkill(  )
	{
		if (pilot >= GameState.MAXSKILL && trader >= GameState.MAXSKILL &&
				fighter >= GameState.MAXSKILL && engineer >= GameState.MAXSKILL)
			return;
		
		int oldtraderskill = mGameState.ship.skill(Skill.TRADER);
		
		boolean redo = true;
		Skill d = null;
		while (redo)
		{
			d = (GameState.getRandom( Skill.values() ));
			if ((d == Skill.PILOT    && pilot < GameState.MAXSKILL) ||
				(d == Skill.FIGHTER  && fighter < GameState.MAXSKILL) ||
				(d == Skill.TRADER   && trader < GameState.MAXSKILL) ||
				(d == Skill.ENGINEER && engineer < GameState.MAXSKILL))
				redo = false;
		}
		switch(d) {
		case PILOT:
			pilot += 1;
			break;
		case FIGHTER:
			fighter += 1;
			break;
		case TRADER:
			trader += 1;
			if (oldtraderskill != mGameState.ship.skill(Skill.TRADER))
				mGameState.recalculateBuyPrices(mGameState.curSystem());
			break;
		case ENGINEER:
			engineer += 1;
			break;
		}
	}
	
	// *************************************************************************
	// Decrease one of the skills of the commander
	// *************************************************************************
	private void decreaseRandomSkill( int amount )
	{
		// NB original performed the wrong check here so that a player with all skills greater than MAXSKILL would not see decrease.
		// It couldn't occur in practice because not many things cause skill decrease, but in theory a player with all high skills could have gotten stuck in an infinite loop.
		while (pilot <=amount && trader <=amount &&
				fighter <=amount && engineer <=amount)
			amount--;
		if (amount <= 0)
			return;
		
		int oldtraderskill = mGameState.ship.skill(Skill.TRADER);
		
		boolean redo = true;
		Skill d = null;
		while (redo)
		{
			d = (GameState.getRandom( Skill.values() ));
			if ((d == Skill.PILOT    && pilot > amount) ||
				(d == Skill.FIGHTER  && fighter > amount) ||
				(d == Skill.TRADER   && trader > amount) ||
				(d == Skill.ENGINEER && engineer > amount))
				redo = false;
		}
		switch(d) {
		case PILOT:
			pilot -= amount;
			break;
		case FIGHTER:
			fighter -= amount;
			break;
		case TRADER:
			trader -= amount;
			if (oldtraderskill != mGameState.ship.skill(Skill.TRADER))
				mGameState.recalculateBuyPrices(mGameState.curSystem());
			break;
		case ENGINEER:
			engineer -= amount;
			break;
		}
	}
	
	
	
	// *************************************************************************
	// Randomly tweak one of the skills of the commander
	// *************************************************************************
	public void tonicTweakRandomSkill( )
	{
		int oldPilot = pilot;
		int oldFighter = fighter;
		int oldTrader = trader;
		int oldEngineer = engineer;
		
		if (mGameState.difficulty.compareTo(DifficultyLevel.HARD) < 0)
		{
			// add one to a random skill, subtract one from a random skill
			while (	oldPilot == pilot &&
				oldFighter == fighter &&
				oldTrader == trader &&
				oldEngineer == engineer)
			{
				increaseRandomSkill();
				decreaseRandomSkill(1);
			}
		}
		else
		{
			// add one to two random skills, subtract three from one random skill
			increaseRandomSkill();
			increaseRandomSkill();
			decreaseRandomSkill(3);
		}
	}
	
	/*
	 * NB End adapted code.
	 */

	// New function needed when meeting famous captains since GameState cannot directly modify skills
	public void famousCaptainSkillIncrease(Skill skill) {
		int current;
		switch (skill) {
		case PILOT:
			current = pilot;
			break;
		case FIGHTER:
			current = fighter;
			break;
		case TRADER:
			current = trader;
			break;
		case ENGINEER:
			current = engineer;
			break;
		default:
			throw new IllegalArgumentException();
		}

		// add points to skill
		// two points if you're on beginner-normal, one otherwise
		if (mGameState.difficulty.compareTo(DifficultyLevel.HARD) < 0)
			current += 2;
		else
			current += 1;

		if (current > GameState.MAXSKILL)
		{
			current = GameState.MAXSKILL;
		}
		
		switch (skill) {
		case PILOT:
			pilot = current;
			break;
		case FIGHTER:
			fighter = current;
			break;
		case TRADER:
			trader = current;
			break;
		case ENGINEER:
			engineer = current;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	// New functions needed to adjust skills in new commander screen.
	public void increaseSkill(Skill skill) {
		switch (skill) {
		case PILOT:
			++pilot;
			break;
		case FIGHTER:
			++fighter;
			break;
		case TRADER:
			++trader;
			break;
		case ENGINEER:
			++engineer;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public void decreaseSkill(Skill skill) {
		switch (skill) {
		case PILOT:
			--pilot;
			break;
		case FIGHTER:
			--fighter;
			break;
		case TRADER:
			--trader;
			break;
		case ENGINEER:
			--engineer;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public void initializeSkills() {
		pilot = fighter = trader = engineer = 1;
	}
		
	

}
