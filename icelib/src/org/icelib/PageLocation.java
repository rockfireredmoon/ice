/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

public class PageLocation {

	public final static PageLocation UNSET = new PageLocation(-1, -1);
	public final int x;
	public final int y;

	public PageLocation(PageLocation pageLocation) {
		this.x = pageLocation.x;
		this.y = pageLocation.y;
	}

	public PageLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public PageLocation clone() {
		return new PageLocation(this);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 73 * hash + this.x;
		hash = 73 * hash + this.y;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PageLocation other = (PageLocation) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PageLocation{" + "x=" + x + ", y=" + y + '}';
	}

	public boolean isValid() {
		return x >= 0 && y >= 0;
	}

	public boolean isSet() {
		return !this.equals(UNSET);
	}

	public PageLocation east() {
		return new PageLocation(x + 1, y);
	}

	public PageLocation north() {
		return new PageLocation(x, y + 1);
	}

	public PageLocation west() {
		return new PageLocation(x - 1, y);
	}

	public PageLocation south() {
		return new PageLocation(x, y - 1);
	}
}
