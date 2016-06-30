/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.logging.Logger;

public enum CreatureCategory {

	ANIMAL("Animal", "yellow"), DEMON("Demon", "red"), DIVINE("Divine", "white"), DRAGONKIN("Dragonkin", "purple"), ELEMENTAL(
			"Elemental", "blue"), INANIMATE("Inanimate",
					"orange"), MAGICAL("Magical", "cyan"), MORTAL("Mortal", "green"), UNLIVING("Unliving", "grey");
	private static final Logger LOG = Logger.getLogger(CreatureCategory.class.getName());
	private String code;
	private String color;

	private CreatureCategory(String code, String color) {
		this.code = code;
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return Icelib.toEnglish(name(), true);
	}

	public static CreatureCategory fromCode(String code) {
		for (CreatureCategory type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		LOG.info(String.format("TODO: Unhandled CreatureCategory code %s", code));
		return null;
	}

	public String getIcon() {
		return "Icon-" + code + "-Portrait.png";
	}
}
