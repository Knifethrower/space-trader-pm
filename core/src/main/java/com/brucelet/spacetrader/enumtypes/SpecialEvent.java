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

public enum SpecialEvent {
	DRAGONFLYDESTROYED     ( R.string.specialevent_dragonflydestroyed_title,     R.string.specialevent_dragonflydestroyed_message,          0, 0,  true ),
	FLYBARATAS             ( R.string.specialevent_flybaratas_title,             R.string.specialevent_flybaratas_message,                  0, 0,  true ),
	FLYMELINA              ( R.string.specialevent_flymelina_title,              R.string.specialevent_flymelina_message,                   0, 0,  true ),
	FLYREGULAS             ( R.string.specialevent_flyregulas_title,             R.string.specialevent_flyregulas_message,                  0, 0,  true ),
	MONSTERKILLED          ( R.string.specialevent_monsterkilled_title,          R.string.specialevent_monsterkilled_message,          -15000, 0,  true ),
	MEDICINEDELIVERY       ( R.string.specialevent_medicinedelivery_title,       R.string.specialevent_medicinedelivery_message,            0, 0,  true ),
	MOONBOUGHT             ( R.string.specialevent_moonbought_title,             R.string.specialevent_moonbought_message,                  0, 0, false ),
	// ----- fixed locations precede
	MOONFORSALE            ( R.string.specialevent_moonforsale_title,            R.string.specialevent_moonforsale_message,            500000, 4, false ),
	SKILLINCREASE          ( R.string.specialevent_skillincrease_title,          R.string.specialevent_skillincrease_message,            3000, 3, false ),
	TRIBBLE                ( R.string.specialevent_tribble_title,                R.string.specialevent_tribble_message,                  1000, 1, false ),
	ERASERECORD            ( R.string.specialevent_eraserecord_title,            R.string.specialevent_eraserecord_message,              5000, 3, false ),
	BUYTRIBBLE             ( R.string.specialevent_buytribble_title,             R.string.specialevent_buytribble_message,                  0, 3, false ),
	SPACEMONSTER           ( R.string.specialevent_spacemonster_title,           R.string.specialevent_spacemonster_message,                0, 1,  true ),
	DRAGONFLY              ( R.string.specialevent_dragonfly_title,              R.string.specialevent_dragonfly_message,                   0, 1,  true ),
	CARGOFORSALE           ( R.string.specialevent_cargoforsale_title,           R.string.specialevent_cargoforsale_message,             1000, 3, false ),
	INSTALLLIGHTNINGSHIELD ( R.string.specialevent_installlightningshield_title, R.string.specialevent_installlightningshield_message,      0, 0, false ),
	JAPORIDISEASE          ( R.string.specialevent_japoridisease_title,          R.string.specialevent_japoridisease_message,               0, 1, false ),
	LOTTERYWINNER          ( R.string.specialevent_lotterywinner_title,          R.string.specialevent_lotterywinner_message,           -1000, 0,  true ),
	ARTIFACTDELIVERY       ( R.string.specialevent_artifactdelivery_title,       R.string.specialevent_artifactdelivery_message,       -20000, 0,  true ),
	ALIENARTIFACT          ( R.string.specialevent_alienartifact_title,          R.string.specialevent_alienartifact_message,               0, 1, false ),
	AMBASSADORJAREK        ( R.string.specialevent_ambassadorjarek_title,        R.string.specialevent_ambassadorjarek_message,             0, 1, false ),
	ALIENINVASION          ( R.string.specialevent_alieninvasion_title,          R.string.specialevent_alieninvasion_message,               0, 0,  true ),
	GEMULONINVADED         ( R.string.specialevent_gemuloninvaded_title,         R.string.specialevent_gemuloninvaded_message,              0, 0,  true ),
	GETFUELCOMPACTOR       ( R.string.specialevent_getfuelcompactor_title,       R.string.specialevent_getfuelcompactor_message,            0, 0, false ),
	EXPERIMENT             ( R.string.specialevent_experiment_title,             R.string.specialevent_experiment_message,                  0, 0,  true ),
	TRANSPORTWILD          ( R.string.specialevent_transportwild_title,          R.string.specialevent_transportwild_message,               0, 1, false ),
	GETREACTOR             ( R.string.specialevent_getreactor_title,             R.string.specialevent_getreactor_message,                  0, 0, false ),
	GETSPECIALLASER        ( R.string.specialevent_getspeciallaser_title,        R.string.specialevent_getspeciallaser_message,             0, 0, false ),
	SCARAB                 ( R.string.specialevent_scarab_title,                 R.string.specialevent_scarab_message,                      0, 1,  true ),
	GETHULLUPGRADED        ( R.string.specialevent_gethullupgraded_title,        R.string.specialevent_gethullupgraded_message,             0, 0, false ),
	// ------ fixed locations follow
	SCARABDESTROYED        ( R.string.specialevent_scarabdestroyed_title,        R.string.specialevent_scarabdestroyed_message,             0, 0,  true ),
	REACTORDELIVERED       ( R.string.specialevent_reactordelivered_title,       R.string.specialevent_reactordelivered_message,            0, 0,  true ),
	JAREKGETSOUT           ( R.string.specialevent_jarekgetsout_title,           R.string.specialevent_jarekgetsout_message,                0, 0,  true ),
	GEMULONRESCUED         ( R.string.specialevent_gemulonrescued_title,         R.string.specialevent_gemulonrescued_message,              0, 0,  true ),
	EXPERIMENTSTOPPED      ( R.string.specialevent_experimentstopped_title,      R.string.specialevent_experimentstopped_message,           0, 0,  true ),
	EXPERIMENTNOTSTOPPED   ( R.string.specialevent_experimentnotstopped_title,   R.string.specialevent_experimentnotstopped_message,        0, 0,  true ),
	WILDGETSOUT            ( R.string.specialevent_wildgetsout_title,            R.string.specialevent_wildgetsout_message,                 0, 0,  true ),
	;

	public final int titleId;
	public final int questStringId;
    public final int price;
    public final int occurrence;
    public final boolean justAMessage;
    
    SpecialEvent(int titleId, int questStringId, int price, int occurance, boolean justAMessage) {
        this.titleId = titleId;
        this.questStringId = questStringId;
        this.price = price;
        this.occurrence = occurance;
        this.justAMessage = justAMessage;
    }
}
