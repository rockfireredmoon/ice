/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.StringTokenizer;

public class Point4D extends Point3D {
	public float w;

	public Point4D() {
	}

	public Point4D(String locationString) {
		super(locationString);
	}

	public Point4D(float x, float y, float z, float w) {
		super(x, y, z);
		this.w = w;
	}

	public float getW() {
		return w;
	}

	public void set(Point4D location) {
		super.set(location);
		this.w = location.w;
	}

	@Override
	public void parseString(String value) {
		StringTokenizer t = new StringTokenizer(value, ",");
		x = Float.parseFloat(t.nextToken().trim());
		y = Float.parseFloat(t.nextToken().trim());
		z = Float.parseFloat(t.nextToken().trim());
		w = Float.parseFloat(t.nextToken().trim());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + Float.floatToIntBits(this.x);
		hash = 67 * hash + Float.floatToIntBits(this.y);
		hash = 67 * hash + Float.floatToIntBits(this.z);
		hash = 67 * hash + Float.floatToIntBits(this.w);
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
		final Point4D other = (Point4D) obj;
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
			return false;
		}
		if (Float.floatToIntBits(this.w) != Float.floatToIntBits(other.w)) {
			return false;
		}
		return true;
	}

	public void set(float x, float y, float z, float w) {
		super.set(x, y, z);
		this.w = w;
	}

	@Override
	public String toString() {
		return "Point4D{" + "x=" + x + ", y=" + y + ", z=" + z + ",w=" + w + '}';
	}
}
