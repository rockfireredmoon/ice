/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.Serializable;
import java.util.StringTokenizer;

public class XYZ implements Serializable {

	public long x = 0;
	public long y = 0;
	public long z = 0;

	public XYZ(String locationString) {
		StringTokenizer t = new StringTokenizer(locationString, ",");
		x = Long.parseLong(t.nextToken().trim());
		z = Long.parseLong(t.nextToken().trim());
		y = Long.parseLong(t.nextToken().trim());
	}

	@Override
	protected Object clone() {
		return new XYZ(x, y, z);
	}

	public XYZ(Long x, Long y, Long z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public XYZ(int x, int z, int y) {
		this((long) x, (long) y, (long) z);
	}

	public XYZ(long x, long z, long y) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public XYZ() {
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

	public final long getZ() {
		return z;
	}

	public final void setZ(long z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}

	public Point toXZPoint() {
		return new Point(x, y);
	}
}
