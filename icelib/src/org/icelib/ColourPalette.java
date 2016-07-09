/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import org.icelib.beans.MappedList;

public class ColourPalette extends MappedList<Color> {
	private static final long serialVersionUID = 1L;

	private String key;

	public ColourPalette(String key) {
		super(Color.class);
		this.key = key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public ColourPalette clone() {
		ColourPalette p = new ColourPalette(key);
		for (Color c : this) {
			p.add(c.clone());
		}
		return p;
	}
}