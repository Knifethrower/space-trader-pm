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

public interface Purchasable extends XmlString {
	int buyPrice(TechLevel systemTechLevel, int traderSkill);
	int sellPrice();
	TechLevel techLevel();
	
	abstract class Impl {
		private Impl (){}
		
		protected static int buyPrice(int price, TechLevel itemTechLevel, TechLevel curSysTechLevel, int traderSkill) {
			return ((itemTechLevel.compareTo(curSysTechLevel) > 0) ? 0 : 
				((price * (100 - traderSkill)) / 100));
		}

		protected static int sellPrice(int price) {
			return (price * 3) / 4;
		}
	}
}
