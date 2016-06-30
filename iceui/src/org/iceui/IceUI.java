package org.iceui;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.icelib.Color;
import org.icelib.Icelib;
import org.icelib.Point3D;
import org.icelib.Point4D;
import org.icelib.RGB;
import org.icelib.XYZ;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;

public class IceUI {

	public static ColorRGBA getColourPreference(Preferences prefs, String key, ColorRGBA defaultColour) {
		return IceUI.toRGBA(new Color(prefs.get(key, Icelib.toHexString(IceUI.fromRGBA(defaultColour)))));
	}

	public static short getDifference(int target, int source) {
		return (short) Math.round((Math.atan2(Math.sin((float) (target - source) * FastMath.DEG_TO_RAD),
				Math.cos((float) (target - source) * FastMath.DEG_TO_RAD)) * FastMath.RAD_TO_DEG));
	}

	public static short getDegrees(Quaternion q) {
		float[] anglesTemp = new float[3];
		q.toAngles(anglesTemp);
		short deg = (short) (anglesTemp[1] * FastMath.RAD_TO_DEG);
		if (deg < 0) {
			deg = (short) (360 + deg);
		}
		return deg;
	}

	/**
	 * 
	 * @param location
	 *            the value of location
	 */
	public static Vector3f toVector3f(XYZ location) {
		return new Vector3f(location.x, location.y, location.z);
	}

	/**
	 * 
	 * @param location
	 *            the value of location
	 */
	public static Vector3f toVector3f(Point3D location) {
		return new Vector3f(location.x, location.y, location.z);
	}

	/**
	 * 
	 * @param col
	 *            the value of col
	 */
	public static Vector4f toVector4fColor(RGB col) {
		return new Vector4f((float) col.getRed() / 255.0F, (float) col.getGreen() / 255.0F, (float) col.getBlue() / 255.0F, 1.0F);
	}

	/**
	 * 
	 * @param fogColor
	 *            the value of fogColor
	 */
	public static RGB fromRGBA(ColorRGBA fogColor) {
		return fogColor == null ? null : new Color((int) (fogColor.getRed() * 255), (int) (fogColor.getGreen() * 255), (int) (fogColor.getBlue() * 255),
				(int) (fogColor.getAlpha() * 255));
	}

	/**
	 * 
	 * @param rgb
	 *            the value of rgb
	 */
	public static Vector4f rgbToVector4f(RGB rgb) {
		return rgbToVector4f(rgb, true);
	}

	/**
	 * 
	 * @param rgb
	 *            the value of rgb
	 * @param includeAlpha
	 *            the value of includeAlpha
	 */
	public static Vector4f rgbToVector4f(RGB rgb, boolean includeAlpha) {
		return new Vector4f((float) rgb.getRed() / 255.0F, (float) rgb.getGreen() / 255.0F, (float) rgb.getBlue() / 255.0F,
				includeAlpha ? ((float) rgb.getAlpha() / 255.0F) : 1.0F);
	}

	/**
	 * 
	 * @param loc
	 *            the value of loc
	 */
	public static Point3D toPoint3D(Vector3f loc) {
		return new Point3D(loc.x, loc.y, loc.z);
	}

	/**
	 * 
	 * @param loc
	 *            the value of loc
	 */
	public static Point3D toLocation(Vector3f loc, Point3D point) {
		point.x = loc.x;
		point.y = loc.y;
		point.z = loc.z;
		return point;
	}

	/**
	 * 
	 * @param loc
	 *            the value of loc
	 */
	public static XYZ toXYZ(Vector3f loc) {
		return new XYZ((int) loc.x, (int) loc.y, (int) loc.z);
	}

	/**
	 * 
	 * @param rgba1
	 *            the value of rgba1
	 * @param rgba2
	 *            the value of rgba2
	 * @return the boolean
	 */
	public static boolean colorClose(ColorRGBA rgba1, ColorRGBA rgba2) {
		if ((rgba1 == null && rgba2 != null) || (rgba1 != null && rgba2 == null)) {
			return false;
		}
		return (int) (rgba1.r * 255.0F) == (int) (rgba2.r * 255.0F) && (int) (rgba1.g * 255.0F) == (int) (rgba2.g * 255.0F)
				&& (int) (rgba1.b * 255.0F) == (int) (rgba2.b * 255.0F) && (int) (rgba1.a * 255.0F) == (int) (rgba2.a * 255.0F);
	}

	/**
	 * 
	 * @param rgb
	 *            the value of rgb
	 */
	public static ColorRGBA toRGBA(RGB rgb) {
		return new ColorRGBA((float) rgb.getRed() / 255.0F, (float) rgb.getGreen() / 255.0F, (float) rgb.getBlue() / 255.0F,
				rgb.getAlpha() == -1 ? 1.0F : (float) rgb.getAlpha() / 255.0F);
	}

	public static Vector3f roundLocal(Vector3f v) {
		v.x = Math.round(v.x);
		v.y = Math.round(v.y);
		v.z = Math.round(v.z);
		return v;
	}

	public static Vector2f roundLocal(Vector2f v) {
		v.x = Math.round(v.x);
		v.y = Math.round(v.y);
		return v;
	}

	public static Vector2f floor(Vector2f v) {
		Vector2f v2 = new Vector2f();
		v2.x = (float) Math.floor(v.x);
		v2.y = (float) Math.floor(v.y);
		return v2;
	}

	public static Vector2f floorLocal(Vector2f v) {
		v.x = (float) Math.floor(v.x);
		v.y = (float) Math.floor(v.y);
		return v;
	}

	public static Vector2f toVector2fXZ(Vector3f v) {
		return new Vector2f(v.x, v.z);
	}

	public static Vector2f toVector2fXZ(Point3D l) {
		return new Vector2f(l.x, l.z);
	}

	public static Quaternion toQ(Point4D p) {
		return new Quaternion(p.x, p.y, p.z, p.w);
	}

	public static Point4D toPoint4D(Quaternion localRotation) {
		return new Point4D(localRotation.getX(), localRotation.getY(), localRotation.getZ(), localRotation.getW());
	}

	public static void setColourPreferences(Preferences prefs, String key, ColorRGBA newColor) {
		prefs.put(key, Icelib.toHexString(IceUI.fromRGBA(newColor)));
	}

	public static Vector3f toEulerDegrees(Quaternion q, float snap) {
		float[] ang = q.toAngles(null);
		Vector3f rotVal = new Vector3f(ang[0] * FastMath.RAD_TO_DEG, ang[1] * FastMath.RAD_TO_DEG, ang[2] * FastMath.RAD_TO_DEG);
		if (rotVal.x < 0) {
			rotVal.x = rotVal.x + 360;
		}
		if (snap > 0) {
			rotVal.x = Math.round(rotVal.x * snap) / snap;
		}
		if (rotVal.x == 360) {
			rotVal.x = 0;
		}
		if (rotVal.y < 0) {
			rotVal.y = rotVal.y + 360;
		}
		if (snap > 0) {
			rotVal.y = Math.round(rotVal.y * snap) / snap;
		}
		if (rotVal.y == 360) {
			rotVal.y = 0;
		}
		if (rotVal.z < 0) {
			rotVal.z = rotVal.z + 360;
		}
		if (snap > 0) {
			rotVal.z = (int) Math.round(rotVal.z * snap) / snap;
		}
		if (rotVal.z == 360) {
			rotVal.z = 0;
		}
		return rotVal;
	}

	public static Quaternion fromEulerDegrees(Vector3f v) {
		float[] radAng = { v.x * FastMath.DEG_TO_RAD, v.y * FastMath.DEG_TO_RAD, v.z * FastMath.DEG_TO_RAD };
		Quaternion newVal = new Quaternion();
		newVal.fromAngles(radAng);
		return newVal;
	}

	public static List<ColorRGBA> toRGBAList(List<? extends RGB> palette) {
		List<ColorRGBA> l = new ArrayList<ColorRGBA>(palette.size());
		for (RGB r : palette) {
			l.add(toRGBA(r));
		}
		return l;
	}

	public static float[] saveFrustum(Camera cam) {
		return new float[] { cam.getFrustumBottom(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumNear(),
				cam.getFrustumRight(), cam.getFrustumTop() };
	}

	public static void restoreFrustum(float[] f, Camera cam) {
		cam.setFrustumBottom(f[0]);
		cam.setFrustumFar(f[1]);
		cam.setFrustumLeft(f[2]);
		cam.setFrustumNear(f[3]);
		cam.setFrustumRight(f[4]);
		cam.setFrustumTop(f[4]);
	}
}
