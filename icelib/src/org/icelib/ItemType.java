/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.logging.Logger;

public enum ItemType {

	UNKNOWN_0(0), UNKNOWN_1(1), CONTAINER(6), ARMOUR(3), WEAPON(2), CHARM(4), CONSUMABLE(5), SPECIAL(8), BASIC(7), WEAPON_PLAN(
			10), QUEST_ITEMS(9);
	private static final Logger LOG = Logger.getLogger(ItemType.class.getName());
	private int code;

	private ItemType(int code) {
		this.code = code;
	}

	public static ItemType fromCode(int code) {
		for (ItemType type : ItemType.values()) {
			if (type.code == code) {
				return type;
			}
		}
		LOG.info("TODO: Unhandle Type code " + code);
		return null;
	}

	public String toString() {
		return Icelib.toEnglish(name(), true);
	}
}
