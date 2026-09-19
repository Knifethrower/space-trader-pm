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

public interface XmlString {
	String toXmlString(Resources res);

	interface Plural {
		String toXmlPluralString(int amount, Resources res);
	}

	class Static {

		public static void processXmlArguements(Object[] args, Resources res) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof XmlString) {
					args[i] = ((XmlString) args[i]).toXmlString(res);
				}
			}
		}

	}

}

