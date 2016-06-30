/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib.swing;

import java.awt.Color;
import org.icelib.RGB;

public class Iceswing {

	public static Color toColor(RGB rgb) {
		return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

}
