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


public interface Encounter {

	Opponent opponentType();
	EncounterButton button(int which);
	OpponentAction action();
	
	// Enum methods specified in interface so they can be accessed by interface types.
	int ordinal();
	String name();

	enum Police implements Encounter {
		INSPECTION( // Police asks to submit for inspection
				OpponentAction.INSPECTION,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				EncounterButton.SUBMIT,
				EncounterButton.BRIBE),
		IGNORE(		// Police just ignores you
				OpponentAction.IGNORE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		ATTACK(		// Police attacks you (sometimes on sight)
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				EncounterButton.SURRENDER,
				null),
		FLEE(		// Police is fleeing
				OpponentAction.FLEE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		;

		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Police(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}

		@Override
		public Opponent opponentType() {
			return Opponent.POLICE;
		}
		
	}

	enum Pirate implements Encounter {
		ATTACK(		// Pirate attacks
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				EncounterButton.SURRENDER,
				null),				
		FLEE(		// Pirate flees
				OpponentAction.FLEE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		IGNORE(		// Pirate ignores you (because of cloak)
				OpponentAction.IGNORE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		SURRENDER(	// Pirate surrenders
				OpponentAction.SURRENDER,
				EncounterButton.ATTACK,
				EncounterButton.PLUNDER,
				null,
				null),	
		;
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Pirate(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}
		
		@Override
		public Opponent opponentType() {
			return Opponent.PIRATE;
		}

	}

	enum Trader implements Encounter {
		IGNORE(		// Trader passes
				OpponentAction.IGNORE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		FLEE(		// Trader flees
				OpponentAction.FLEE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		ATTACK(		// Trader is attacking (after being provoked)
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				null,
				null),				
		SURRENDER(	// Trader surrenders 
				OpponentAction.SURRENDER,
				EncounterButton.ATTACK,
				EncounterButton.PLUNDER,
				null,
				null),
		SELL(		// Trader will sell products in orbit
						OpponentAction.TRADE,
						EncounterButton.ATTACK,
						EncounterButton.IGNORE,
						EncounterButton.TRADE,
						null),
		BUY(		// Trader will sell products in orbit
				OpponentAction.TRADE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				EncounterButton.TRADE,
				null),
//		NOTRADE,	// Player has declined to transact with Trader (NB this appears to be unused in original)
		;
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Trader(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}
		
		@Override
		public Opponent opponentType() {
			return Opponent.TRADER;
		}
	}

	// Space Monster Actions
	enum Monster implements Encounter {
		ATTACK(
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				null,
				null),		
		IGNORE(
				OpponentAction.IGNORE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		;
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Monster(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}
		
		@Override
		public Opponent opponentType() {
			return Opponent.MONSTER;
		}
	}

	// Dragonfly Actions
	enum Dragonfly implements Encounter {
		ATTACK(
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				null,
				null),		
		IGNORE(
				OpponentAction.IGNORE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		;
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Dragonfly(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}
		
		@Override
		public Opponent opponentType() {
			return Opponent.DRAGONFLY;
		}
	}

	enum Mantis implements Encounter {
		ATTACK(
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				EncounterButton.SURRENDER,
				null),
		;
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Mantis(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}
		
		@Override
		public Opponent opponentType() {
			return Opponent.MANTIS;
		}
	}

	// Scarab Actions
	enum Scarab implements Encounter {
		ATTACK(
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				EncounterButton.SURRENDER,
				null),		
		IGNORE(
				OpponentAction.IGNORE,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				null,
				null),
		;
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		Scarab(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}

		@Override
		public Opponent opponentType() {
			return Opponent.SCARAB;
		}
	}

	enum VeryRare implements Encounter {

		// Famous Captain
		FAMOUSCAPATTACK (
				OpponentAction.ATTACK,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				null,
				null),
		CAPTAINAHAB (
				OpponentAction.MEETCAPTAIN,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				EncounterButton.MEET,
				null),
		CAPTAINCONRAD (
				OpponentAction.MEETCAPTAIN,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				EncounterButton.MEET,
				null),
		CAPTAINHUIE (
				OpponentAction.MEETCAPTAIN,
				EncounterButton.ATTACK,
				EncounterButton.IGNORE,
				EncounterButton.MEET,
				null),

		// Other Special Encounters
		MARIECELESTE (
				OpponentAction.MARIECELESTE,
				EncounterButton.BOARD,
				EncounterButton.IGNORE,
				null,
				null),
		BOTTLEOLD (
				OpponentAction.BOTTLE,
				EncounterButton.DRINK,
				EncounterButton.IGNORE,
				null,
				null),
		BOTTLEGOOD (
				OpponentAction.BOTTLE,
				EncounterButton.DRINK,
				EncounterButton.IGNORE,
				null,
				null),
		POSTMARIEPOLICE (
				OpponentAction.POSTMARIE,
				EncounterButton.ATTACK,
				EncounterButton.FLEE,
				EncounterButton.YIELD,
				EncounterButton.BRIBE),
		;
		
		private EncounterButton mButton1;
		private EncounterButton mButton2;
		private EncounterButton mButton3;
		private EncounterButton mButton4;
		private OpponentAction mAction;
		
		VeryRare(OpponentAction a, EncounterButton b1, EncounterButton b2, EncounterButton b3, EncounterButton b4) {
			mAction = a;
			mButton1 = b1;
			mButton2 = b2;
			mButton3 = b3;
			mButton4 = b4;
		}

		@Override
		public EncounterButton button(int i) { 
			switch (i) {
			case 1: return mButton1;
			case 2: return mButton2;
			case 3: return mButton3;
			case 4: return mButton4;
			}
			return null;
		}
		
		@Override
		public OpponentAction action() {
			return mAction;
		}

		@Override
		public Opponent opponentType() {
			switch (this) {
			case BOTTLEOLD:
			case BOTTLEGOOD:
				return Opponent.BOTTLE;
			case MARIECELESTE:
				return Opponent.MARIECELESTE;
			case POSTMARIEPOLICE:
				return Opponent.POSTMARIE;
			case CAPTAINAHAB:
			case CAPTAINCONRAD:
			case CAPTAINHUIE:
				return Opponent.FAMOUSCAPTAIN;
			default:
				return null;
			}
		}
	}

}
