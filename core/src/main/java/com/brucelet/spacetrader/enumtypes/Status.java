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

public enum Status implements XmlString {

	UNEVENTFUL    ( R.string.status_uneventful,    -1,                                     -1                                      ), // Uneventful
	WAR           ( R.string.status_war,           R.string.headline_local_war,           R.string.headline_remote_war           ), // Ore and Weapons in demand
	PLAGUE        ( R.string.status_plague,        R.string.headline_local_plague,        R.string.headline_remote_plague        ), // Medicine in demand
	DROUGHT       ( R.string.status_drought,       R.string.headline_local_drought,       R.string.headline_remote_drought       ), // Water in demand
	BOREDOM       ( R.string.status_boredom,       R.string.headline_local_boredom,       R.string.headline_remote_boredom       ), // Games and Narcotics in demand
	COLD          ( R.string.status_cold,          R.string.headline_local_cold,          R.string.headline_remote_cold          ), // Furs in demand
	CROPFAILURE   ( R.string.status_cropfailure,   R.string.headline_local_cropfailure,   R.string.headline_remote_cropfailure   ), // Food in demand
	LACKOFWORKERS ( R.string.status_lackofworkers, R.string.headline_local_lackofworkers, R.string.headline_remote_lackofworkers ), // Machinery and Robots in demand
	;

	public final int resId;
	public final int localHeadlineId;
	public final int remoteHeadlineId;
	
	Status(int resId, int localHeadlineId, int remoteHeadlineId) {
		this.resId = resId;
		this.localHeadlineId = localHeadlineId;
		this.remoteHeadlineId = remoteHeadlineId;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
}
