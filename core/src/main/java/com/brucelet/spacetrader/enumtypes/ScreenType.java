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

public enum ScreenType implements XmlString {
	BUY       ( R.id.screen_buy,            R.string.screen_buy,            R.string.shortcut_spinner_buy,       R.string.shortcut_buy,       true,  true,  false ),
	SELL      ( R.id.screen_sell,           R.string.screen_sell,           R.string.shortcut_spinner_sell,      R.string.shortcut_sell,      true,  true,  false ),
	YARD      ( R.id.screen_yard,           R.string.screen_yard,           R.string.shortcut_spinner_yard,      R.string.shortcut_yard,      true,  true,  false ),
	BUYEQ     ( R.id.screen_buyeq,          R.string.screen_buyeq,          R.string.shortcut_spinner_buyeq,     R.string.shortcut_buyeq,     true,  true,  false ),
	SELLEQ    ( R.id.screen_selleq,         R.string.screen_selleq,         R.string.shortcut_spinner_selleq,    R.string.shortcut_selleq,    true,  true,  false ),
	PERSONNEL ( R.id.screen_personnel,      R.string.screen_personnel,      R.string.shortcut_spinner_personnel, R.string.shortcut_personnel, true,  true,  false ),
	BANK      ( R.id.screen_bank,           R.string.screen_bank,           R.string.shortcut_spinner_bank,      R.string.shortcut_bank,      true,  true,  false ),
	INFO      ( R.id.screen_info,           R.string.screen_info,           R.string.shortcut_spinner_info,      R.string.shortcut_info,      true,  true,  false ),
	STATUS    ( R.id.screen_status,         R.string.screen_status,         R.string.shortcut_spinner_status,    R.string.shortcut_status,    true,  true,  false ),
	CHART     ( R.id.screen_chart,          R.string.screen_chart,          R.string.shortcut_spinner_chart,     R.string.shortcut_chart,     true,  true,  false ),
	WARP      ( R.id.screen_warp,           R.string.screen_warp,           R.string.shortcut_spinner_warp,      R.string.shortcut_warp,      true,  true,  false ),
	// The screens below do not appear in the dropdown menu.
	BUYSHIP   ( R.id.screen_yard_buyship,   R.string.screen_yard_buyship,   -1,                                  -1,                          false, true,  false ),
	QUESTS    ( R.id.screen_status_quests,  R.string.screen_status_quests,  -1,                                  -1,                          false, true,  false ),
	SHIP      ( R.id.screen_status_ship,    R.string.screen_status_ship,    -1,                                  -1,                          false, true,  false ),
	CARGO     ( R.id.screen_status_cargo,   R.string.screen_status_cargo,   -1,                                  -1,                          false, true,  false ),
	TARGET    ( R.id.screen_warp_target,    R.string.screen_warp_target,    -1,                                  -1,                          false, true,  false ),
	AVGPRICES ( R.id.screen_warp_avgprices, R.string.screen_warp_avgprices, -1,                                  -1,                          false, true,  false ),
	// TODO? The screens below will be removed if their corresponding ui elements are spun off into their own activities.
	TITLE     (R.id.screen_title,           R.string.screen_title,          -2,                                  -2,                          false, false, true  ),
	ENDGAME   (R.id.screen_endofgame,       R.string.screen_endofgame,      -2,                                  -2,                          false, false, true  ),
	ENCOUNTER (R.id.screen_encounter,       R.string.screen_encounter,      -2,                                  -2,                          false, false, false ),
	;

	public static ScreenType[] dropdownValues() {
		return new ScreenType[] { BUY, SELL, YARD, BUYEQ, SELLEQ, PERSONNEL, BANK, INFO, STATUS, CHART, WARP, };
	}
	public static ScreenType[] dockedValues() {
		return new ScreenType[] { BUY, SELL, YARD, BUYEQ, SELLEQ, PERSONNEL, BANK, INFO, STATUS, CHART, WARP, BUYSHIP, QUESTS, SHIP, CARGO, TARGET, AVGPRICES, };
	}
	
	public final int fragmentId;
	public final int titleId;
	public final int shortcutId;
	public final int spinnerId;
	public final boolean dropdown;
	public final boolean docked;
	public final boolean isImage;
	ScreenType(int fragmentId, int titleId, int spinnerId, int shortcutId, boolean dropdown, boolean docked, boolean isImage) {
		this.fragmentId = fragmentId;
		this.titleId = titleId;
		this.spinnerId = spinnerId;
		this.shortcutId = shortcutId;
		this.dropdown = dropdown;
		this.docked = docked;
		this.isImage = isImage;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(titleId);
	}

}
