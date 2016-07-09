/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.ArrayList;
import java.util.List;

public class Color implements RGB {

	public final static Color WHITE = new Color(255, 255, 255);
	public final static Color BLACK = new Color(0, 0, 0);
	public final static Color DARK_GREEN = new Color(0, 128, 0);
	public final static Color RED = new Color(255, 0, 0);
	public final static Color GREEN = new Color(0, 255, 0);
	public final static Color BLUE = new Color(0, 0, 255);
	public final static Color DARK_BLUE = new Color(0, 0, 128);
	public final static Color YELLOW = new Color(255, 255, 0);
	public final static Color MAGENTA = new Color(255, 0, 255);
	public final static Color CYAN = new Color(0, 255, 255);
	public final static Color DARK_RED = new Color(128, 0, 0);

	public static List<RGB> randomList(int count) {
		List<RGB> r = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			r.add(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
		}
		return r;
	}

	private int red;
	private int green;
	private int blue;
	private int alpha = 255;

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alpha;
		result = prime * result + blue;
		result = prime * result + green;
		result = prime * result + red;
		return result;
	}

	public Color clone() {
		return new Color(red, green, blue, alpha);
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
		Color other = (Color) obj;
		if (alpha != other.alpha) {
			return false;
		}
		if (blue != other.blue) {
			return false;
		}
		if (green != other.green) {
			return false;
		}
		if (red != other.red) {
			return false;
		}
		return true;
	}

	public Color(int red, int green, int blue, int alpha) {
		super();
		assert red > -1;
		assert green > -1;
		assert blue > -1;
		assert alpha > -1;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color(String hex) {
		while (hex.startsWith("#")) {
			hex = hex.substring(1);
		}
		if (hex.length() == 3) {
			red = Integer.parseInt(hex.substring(0, 1), 16) * 16;
			green = Integer.parseInt(hex.substring(1, 2), 16) * 16;
			blue = Integer.parseInt(hex.substring(2, 3), 16) * 16;
		} else if (hex.length() == 6) {
			red = Integer.parseInt(hex.substring(0, 2), 16);
			green = Integer.parseInt(hex.substring(2, 4), 16);
			blue = Integer.parseInt(hex.substring(4, 6), 16);
		} else if (hex.length() == 8) {
			red = Integer.parseInt(hex.substring(0, 2), 16);
			green = Integer.parseInt(hex.substring(2, 4), 16);
			blue = Integer.parseInt(hex.substring(4, 6), 16);
			alpha = Integer.parseInt(hex.substring(6, 8), 16);
		} else {
			throw new IllegalArgumentException(hex + " not a hex colour");
		}
		assert red > -1;
		assert green > -1;
		assert blue > -1;
		assert alpha > -1;
	}

	public int getBlue() {
		return blue;
	}

	public int getGreen() {
		return green;
	}

	public int getRed() {
		return red;
	}

	public int getAlpha() {
		return alpha;
	}

	@Override
	public int compareTo(RGB o) {
		int i = Integer.valueOf(getRed()).compareTo(Integer.valueOf(o.getRed()));
		if (i == 0) {
			i = Integer.valueOf(getGreen()).compareTo(Integer.valueOf(o.getGreen()));
			if (i == 0) {
				i = Integer.valueOf(getBlue()).compareTo(Integer.valueOf(o.getBlue()));
				if (i == 0) {
					i = Integer.valueOf(getAlpha()).compareTo(Integer.valueOf(o.getAlpha()));
				}
			}
		}
		return i;
	}

	public static List<RGB> roughenRGB(List<RGB> r, int divisor) {

		for (int i = r.size() - 1; i >= 0; i--) {
			r.set(i, roughenRGB(r.get(i), divisor));
		}
		return r;
	}

	public static List<RGB> roughenHSV(List<RGB> r, float factor) {
		for (int i = r.size() - 1; i >= 0; i--) {
			r.set(i, roughenHSV(r.get(i), factor));
		}
		return r;
	}

	public static RGB roughenRGB(RGB r, int divisor) {
		return new Color((int) (r.getRed() / divisor) * divisor, (int) (r.getGreen() / divisor) * divisor,
				(int) (r.getBlue() / divisor) * divisor);
	}

	public static RGB roughenHSV(RGB r, float factor) {
		float[] hsv = new float[3];
		java.awt.Color.RGBtoHSB(r.getRed(), r.getGreen(), r.getBlue(), hsv);
		hsv[0] = (hsv[0] / factor) * factor;
		hsv[1] = (hsv[1] / factor) * factor;
		hsv[2] = (hsv[2] / factor) * factor;
		java.awt.Color c = java.awt.Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
		return new Color(c.getRed(), c.getGreen(), c.getBlue());
	}

	@Override
	public String toString() {
		return "[" + red + "," + green + "," + blue + "," + alpha + "]";
	}

	public static int same(List<RGB> l, List<RGB> inner, float factor) {
		List<RGB> innerCopy = new ArrayList<RGB>(inner);
		int count = 0;
		for (RGB c : l) {
			c = roughenHSV(c, factor);
			for (RGB x : innerCopy) {
				x = roughenHSV(x, factor);
				if (x.equals(c)) {
					count++;
					break;
				}
			}
		}
		return count;
	}
}
