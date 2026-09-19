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

import com.brucelet.spacetrader.R;

import com.brucelet.spacetrader.platform.Resources;

public enum NewsEvent implements XmlString {

	CAPTAINHUIEATTACKED    ( R.string.newsevent_captainhuieattacked,    false ),
	EXPERIMENTPERFORMED    ( R.string.newsevent_experimentperformed,    false ),
	CAPTAINHUIEDESTROYED   ( R.string.newsevent_captainhuiedestroyed,   false ),
	CAPTAINAHABATTACKED    ( R.string.newsevent_captainahabattacked,    false ),
	CAPTAINAHABDESTROYED   ( R.string.newsevent_captainahabdestroyed,   false ),
	CAPTAINCONRADATTACKED  ( R.string.newsevent_captainconradattacked,  false ),
	CAPTAINCONRADDESTROYED ( R.string.newsevent_captainconraddestroyed, false ),
	MONSTERKILLED          ( R.string.newsevent_monsterkilled,          false ),
	WILDARRESTED           ( R.string.newsevent_wildarrested,           false ),
	MONSTERNOTKILLED       ( R.string.newsevent_monsternotkilled,       false ),
	SCARABNOTDESTROYED     ( R.string.newsevent_scarabnotdestroyed,     false ),
	EXPERIMENTSTOPPED      ( R.string.newsevent_experimentstopped,      false ),
	EXPERIMENTNOTSTOPPED   ( R.string.newsevent_experimentnotstopped,   false ),
	DRAGONFLY              ( R.string.newsevent_dragonfly,              false ),
	SCARAB                 ( R.string.newsevent_scarab,                 false ),
	FLYBARATAS             ( R.string.newsevent_flybaratas,             false ),
	FLYMELINA              ( R.string.newsevent_flymelina,              true  ),
	FLYREGULAS             ( R.string.newsevent_flyregulas,             true  ),
	DRAGONFLYNOTDESTROYED  ( R.string.newsevent_dragonflynotdestroyed,  true  ),
	DRAGONFLYDESTROYED     ( R.string.newsevent_dragonflydestroyed,     false ),
	SCARABDESTROYED        ( R.string.newsevent_scarabdestroyed,        false ),
	MEDICINEDELIVERY       ( R.string.newsevent_medicinedelivery,       false ),
	JAPORIDISEASE          ( R.string.newsevent_japoridisease,          true  ),
	ARTIFACTDELIVERY       ( R.string.newsevent_artifactdelivery,       false ),
	JAREKGETSOUT           ( R.string.newsevent_jarekgetsout,           false ),
	WILDGETSOUT            ( R.string.newsevent_wildgetsout,            true  ),
	GEMULONRESCUED         ( R.string.newsevent_gemulonrescued,         false ),
	GEMULONNOTRESCUED      ( R.string.newsevent_gemulonnotrescued,      false ),
	ALIENINVASION          ( R.string.newsevent_alieninvasion,          true  ),
	ARRIVALVIASINGULARITY  ( R.string.newsevent_arrivalviasingularity,  false ),
	CAUGHTLITTERING        ( R.string.newsevent_caughtlittering,        true  ),
//	MOONFORSALE            ( R.string.newsevent_moonforsale,            true  ),
//	BUYTRIBBLE             ( R.string.newsevent_buytribble,             true  ),
	;
	
	public final int resId;
	public final boolean hasArgs;
	
	NewsEvent(int resId, boolean hasArgs) {
		this.resId = resId;
		this.hasArgs = hasArgs;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}
}
