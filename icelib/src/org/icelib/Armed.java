/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

public enum Armed {
	UNARMED, MELEE, RANGED;

	public int toCode() {
		return this.ordinal();
	}
}
