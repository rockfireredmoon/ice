package org.icelib;

import org.icelib.Color;
import org.icelib.RGB;

public enum CreatureCategory {
	PLAYER("Player", Color.DARK_GREEN), ANIMAL("Animal", Color.YELLOW), DEMON("Demon", Color.RED), DIVINE("Divine",
			Color.WHITE), DRAGONKIN("Dragonkin", Color.MAGENTA), ELEMENTAL("Elemental",
					Color.BLUE), INANIMATE("Inanimate", Color.ORANGE), MAGICAL("Magical",
							Color.CYAN), MORTAL("Mortal", Color.GREEN), UNLIVING("Unliving", Color.GREY);

	public static CreatureCategory fromCode(String code) {
		for (CreatureCategory type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid creature category.");
	}

	private String code;
	private RGB color;

	private CreatureCategory(String code, RGB color) {
		this.code = code;
		this.color = color;
	}

	public String getCode() {
		return code;
	}

	public RGB getColor() {
		return color;
	}

	public String getIcon() {
		return "Icon-" + code + "-Portrait.png";
	}

	@Override
	public String toString() {
		return code;
	}
}