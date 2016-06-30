/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Point3D implements Serializable {

	public float x;
	public float y;
	public float z;

	public Point3D() {
	}

	@Override
	public Point3D clone() {
		return new Point3D(x, y, z);
	}

	public Point3D(String locationString) {
		parseString(locationString);
	}

	public Point3D(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3D round() {
		return new Point3D(Math.round(x), Math.round(y), Math.round(z));
	}

	public Point3D roundLocal() {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		return this;
	}

	public final float getX() {
		return x;
	}

	public final Point3D setX(float x) {
		this.x = x;
		return this;
	}

	public final float getY() {
		return y;
	}

	public final Point3D setY(float y) {
		this.y = y;
		return this;
	}

	public final float getZ() {
		return z;
	}

	public final Point3D setZ(float z) {
		this.z = z;
		return this;
	}

	public void set(Point3D location) {
		this.x = location.x;
		this.y = location.y;
		this.z = location.z;
	}

	public void parseString(String value) {
		StringTokenizer t = new StringTokenizer(value, ",");
		x = Float.parseFloat(t.nextToken().trim());
		z = Float.parseFloat(t.nextToken().trim());
		y = Float.parseFloat(t.nextToken().trim());
	}

	public String toPartialLocation() {
		return getX() + "," + getZ() + "," + getY();
	}

	public Point toXZPoint() {
		return new Point((long) getX(), (long) getZ());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + Float.floatToIntBits(this.x);
		hash = 67 * hash + Float.floatToIntBits(this.y);
		hash = 67 * hash + Float.floatToIntBits(this.z);
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
		final Point3D other = (Point3D) obj;
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
			return false;
		}
		return true;
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean xzEquals(Point3D l) {
		return x == l.x && z == l.z;
	}

	@Override
	public String toString() {
		return "Point3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
	}
}
