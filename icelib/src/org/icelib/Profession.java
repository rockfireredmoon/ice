/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.logging.Logger;

public enum Profession {
	KNIGHT(1), ROGUE(2), MAGE(3), DRUID(4), UNKNOWN_1(5), UNKNOWN_2(6);
	private static final Logger LOG = Logger.getLogger(Profession.class.getName());
	private int code;

	private Profession(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getIcon() {
		return "Icons/Icon-" + Icelib.toEnglish(name()) + ".png";
	}

	public String getHighlightIcon() {
		return "Icons/Icon-" + Icelib.toEnglish(name()) + "-Highlight.png";
	}

	public boolean isPlayable() {
		switch (this) {
		case KNIGHT:
		case ROGUE:
		case MAGE:
		case DRUID:
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return Icelib.toEnglish(name(), true);
	}

	public static Profession fromCode(int code) {
		for (Profession type : Profession.values()) {
			if (type.code == code) {
				return type;
			}
		}
		LOG.info("TODO: Unhandle Profession code " + code);
		return null;
	}

}
