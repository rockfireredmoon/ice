/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Point implements Serializable {

	public long x;
	public long y;

	public Point() {
	}

	public Point(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point location) {
		x = location.x;
		y = location.y;
	}

	public final long getX() {
		return x;
	}

	public final void setX(long x) {
		this.x = x;
	}

	public final long getY() {
		return y;
	}

	public final void setY(long y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (x ^ (x >>> 32));
		result = prime * result + (int) (y ^ (y >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Point other = (Point) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}
}