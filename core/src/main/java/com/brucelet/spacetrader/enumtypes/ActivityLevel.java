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

public enum ActivityLevel implements XmlString {
	ABSENT   ( R.string.activity_absent   ),
	MINIMAL  ( R.string.activity_minimal  ),
	FEW      ( R.string.activity_few      ),
	SOME     ( R.string.activity_some     ),
	MODERATE ( R.string.activity_moderate ),
	MANY     ( R.string.activity_many     ),
	ABUNDANT ( R.string.activity_abundant ),
	SWARMS   ( R.string.activity_swarms   ),
	;

	private final int resId;

	ActivityLevel(int resId) {
		this.resId = resId;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
}
