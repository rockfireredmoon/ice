
/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.Serializable;

public interface RGB extends Serializable, Comparable<RGB> {

	int getRed();

	int getGreen();

	int getBlue();

	int getAlpha();

	RGB clone();
}
