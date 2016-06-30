package org.iceui.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icelib.AbstractConfig;
import org.icelib.Icelib;
import org.iceui.HPosition;
import org.iceui.VPosition;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.effects.Effect;

public class UIUtil {

	private final static Logger LOG = Logger.getLogger(UIUtil.class.getName());

	public static String toHexString(ColorRGBA color) {
		return toHexString(color, true);
	}

	public static String toHexString(ColorRGBA color, boolean includeAlpha) {
		if (color == null) {
			return "auto";
		}
		return "#" + toHexNumber(color, includeAlpha);
	}

	public static String toHexNumber(ColorRGBA color) {
		return toHexNumber(color, true);
	}

	public static String toHexNumber(ColorRGBA color, boolean includeAlpha) {
		return Icelib.toHexDigits((int) (color.r * 255)) + Icelib.toHexDigits((int) (color.g * 255))
				+ Icelib.toHexDigits((int) (color.b * 255)) + (includeAlpha ? Icelib.toHexDigits((int) (color.a * 255)) : "");
	}

	public static ColorRGBA fromColorString(String col) {
		return fromColorString(col, true);
	}

	public static ColorRGBA fromColorString(String col, boolean inclueAlpha) {
		if (col.startsWith("#")) {
			col = col.substring(1);
		}

		try {
			return (ColorRGBA) ColorRGBA.class.getDeclaredField(col).get(null);
		} catch (Exception e) {
			if (col.length() == 3 || (col.length() == 4 && inclueAlpha)) {
				float rh = Long.decode("#" + col.substring(0, 1)).floatValue();
				float rg = Long.decode("#" + col.substring(1, 2)).floatValue();
				float rb = Long.decode("#" + col.substring(2, 3)).floatValue();
				float ra = col.length() == 4 ? Long.decode("#" + col.substring(3, 4)).floatValue() : 0xff;
				return new ColorRGBA(rh / 16, rg / 16, rb / 16, ra / 16);
			} else if (col.length() == 6 || (col.length() == 8 && inclueAlpha)) {
				float rh = Long.decode("#" + col.substring(0, 2)).floatValue();
				float rg = Long.decode("#" + col.substring(2, 4)).floatValue();
				float rb = Long.decode("#" + col.substring(4, 6)).floatValue();
				float ra = col.length() == 8 ? Long.decode("#" + col.substring(6, 8)).floatValue() : 0xff;
				return new ColorRGBA(rh / 255, rg / 255, rb / 255, ra / 255);
			}
			throw new IllegalArgumentException("Not a colour.");
		}
	}

	public static void saveWindowPosition(Preferences pref, Element window, String id) {
		int y = window.getInitialized() ? (int) (window.getScreen().getHeight() - window.getY() - window.getHeight()) : (int)window.getY();
		pref.putInt(id + AbstractConfig.WINDOW_X, (int) window.getX());
		pref.putInt(id + AbstractConfig.WINDOW_Y, y);
	}

	public static Vector2f getPersistantPosition(Preferences pref, String id, Vector2f defaultSize) {
		int x = pref.getInt(id + AbstractConfig.WINDOW_X, (int) defaultSize.x);
		int y = pref.getInt(id + AbstractConfig.WINDOW_Y, (int) defaultSize.y);
		return new Vector2f(x, y);
	}

	public static void position(Preferences pref, Element el, String id, Vector2f defaultPosition) {
		int x = pref.getInt(id + AbstractConfig.WINDOW_X, -1);
		int y = pref.getInt(id + AbstractConfig.WINDOW_Y, -1);
		if (y + el.getHeight() >= el.getScreen().getHeight()) {
			y = (int) (el.getScreen().getHeight() - el.getHeight());
		}
		if (x + el.getWidth() >= el.getScreen().getWidth()) {
			x = (int) (el.getScreen().getWidth() - el.getWidth());
		}
		LUtil.setPosition(el, x == -1 ? defaultPosition.x : x, y == -1 ? defaultPosition.y : y);
	}

	public static void saveWindowSize(Preferences pref, Element window, String id) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Window %s now at %d,%d and is %d x %d", id, (int) window.getX(), (int) window.getY(),
					(int) window.getWidth(), (int) window.getHeight()));
		}
		pref.putInt(id + AbstractConfig.WINDOW_WIDTH, (int) window.getWidth());
		pref.putInt(id + AbstractConfig.WINDOW_HEIGHT, (int) window.getHeight());
	}

	public static Vector2f getPersistantSize(Preferences pref, String id, Vector2f defaultSize) {
		int w = pref.getInt(id + AbstractConfig.WINDOW_WIDTH, (int) defaultSize.x);
		int h = pref.getInt(id + AbstractConfig.WINDOW_HEIGHT, (int) defaultSize.y);
		return new Vector2f(w, h);
	}

	public static void saveWindowPositionAndSize(Preferences pref, Element window, String id) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Saving position and size of %s (%s @ %s)", id, window.getDimensions(), window.getPosition()));
		}
		saveWindowSize(pref, window, id);
		saveWindowPosition(pref, window, id);
	}

	public static void dump(Spatial n, int indent) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < indent * 4; i++) {
			b.append(' ');
		}
		b.append(n.getName());
		b.append("    (");
		b.append(n.getClass().getName());
		b.append(")");
		System.err.println(b);
		if (n instanceof Node) {
			for (Spatial c : ((Node) n).getChildren()) {
				dump(c, indent + 1);
			}
		}
	}

	public static void sizeAndPosition(Preferences pref, Element el, String id, Vector2f defaultSize, Vector2f defaultPosition) {
		int x = pref.getInt(id + AbstractConfig.WINDOW_X, -1);
		int y = pref.getInt(id + AbstractConfig.WINDOW_Y, -1);
		int h = pref.getInt(id + AbstractConfig.WINDOW_HEIGHT, -1);
		int w = pref.getInt(id + AbstractConfig.WINDOW_WIDTH, -1);
		el.setPosition(x == -1 ? defaultPosition.x : x, y == -1 ? defaultPosition.y : y);
		el.setDimensions(w == -1 ? defaultPosition.x : w, h == -1 ? defaultPosition.y : h);
	}

	public static float getDefaultVertical(int offset, VPosition defaultVertical, ElementManager screen, Vector2f windowSize) {
		float y = 0;
		switch (defaultVertical) {
		case TOP:
			y = offset;
			break;
		case BOTTOM:
			y = screen.getHeight() - windowSize.y - offset;
			break;
		case MIDDLE:
			y = (int) ((screen.getHeight() - windowSize.y) / 2.0F);
			break;
		}
		return y;
	}

	public static boolean isWindowPositionSaved(Preferences pref, String id) {
		return pref.getFloat(id + AbstractConfig.WINDOW_X, Integer.MIN_VALUE) != Integer.MIN_VALUE
				&& pref.getFloat(id + AbstractConfig.WINDOW_Y, Integer.MIN_VALUE) != Integer.MIN_VALUE;
	}

	public static boolean isWindowSizeSaved(Preferences pref, String id) {
		return pref.getFloat(id + AbstractConfig.WINDOW_WIDTH, Integer.MIN_VALUE) != Integer.MIN_VALUE
				&& pref.getFloat(id + AbstractConfig.WINDOW_HEIGHT, Integer.MIN_VALUE) != Integer.MIN_VALUE;
	}

	public static Vector2f getWindowPosition(Preferences pref, ElementManager screen, String id, Vector2f defaultWindowSize) {
		return getWindowPosition(pref, screen, id, defaultWindowSize, 0, HPosition.CENTER, VPosition.MIDDLE);
	}

	public static Vector2f getWindowPosition(Preferences pref, ElementManager screen, String id, Vector2f defaultWindowSize,
			int offset, HPosition defaultHorizontal, VPosition defaultVertical) {
		Vector2f windowSize = getWindowSize(pref, screen, id, defaultWindowSize);
		float x = Integer.MIN_VALUE;
		float y = Integer.MIN_VALUE;
		if (id != null) {
			x = pref.getFloat(id + AbstractConfig.WINDOW_X, Integer.MIN_VALUE);
			y = pref.getFloat(id + AbstractConfig.WINDOW_Y, Integer.MIN_VALUE);
		}
		if (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE) {
			x = getDefaultHorizontal(offset, defaultHorizontal, screen, windowSize);
			y = getDefaultVertical(offset, defaultVertical, screen, windowSize);
		}
		if (x < 0) {
			x = 0;
		} else if (x + windowSize.x > screen.getWidth()) {
			x = screen.getWidth() - windowSize.x;
		}
		if (y < 0) {
			y = 0;
		} else if (y + windowSize.y > screen.getHeight()) {
			y = screen.getHeight() - windowSize.y;
		}
		return new Vector2f(x, y);
	}

	public static Vector2f getDefaultPosition(int offset, HPosition defaultHorizontal, VPosition defaultVertical,
			ElementManager screen, Vector2f windowSize) {
		return new Vector2f(getDefaultHorizontal(offset, defaultHorizontal, screen, windowSize), getDefaultVertical(offset,
				defaultVertical, screen, windowSize));
	}

	public static float getDefaultHorizontal(int offset, HPosition defaultHorizontal, ElementManager screen, Vector2f windowSize) {
		float x = 0;
		switch (defaultHorizontal) {
		case LEFT:
			x = offset;
			break;
		case RIGHT:
			x = screen.getWidth() - windowSize.x - offset;
			break;
		case CENTER:
			x = (int) ((screen.getWidth() - windowSize.x) / 2.0F);
			break;
		}
		return x;
	}

	public static Vector2f getCenter(ElementManager screen, Vector2f windowSize) {
		return new Vector2f(getDefaultHorizontal(0, HPosition.CENTER, screen, windowSize), getDefaultVertical(0, VPosition.MIDDLE,
				screen, windowSize));
	}

	public static void center(ElementManager screen, Element el) {
		final Vector2f center = getCenter(screen, el.getDimensions());
		// ControlUtil.setBounds(el, center.x, center.y, el.getWidth(),
		// el.getHeight());
		el.setPosition(center.x, center.y);
	}

	public static Vector2f getWindowSize(Preferences pref, ElementManager screen, String id, Vector2f defaultWindowSize) {
		if (id == null) {
			return defaultWindowSize;
		}
		return new Vector2f(pref.getFloat(id + AbstractConfig.WINDOW_WIDTH, defaultWindowSize.x), pref.getFloat(id
				+ AbstractConfig.WINDOW_HEIGHT, defaultWindowSize.y));
	}

	public static void cleanUpWindow(Element aThis) {
		// A hack for windows that use effects, they may not be removed from
		// scene yet when they are shown again
		if (aThis.getScreen().getElementById(aThis.getUID()) != null) {
			LOG.warning(String.format("Removing existing window element with UID of %s", aThis.getUID()));
			final Element olEl = aThis.getScreen().getElementById(aThis.getUID());
			for (Effect.EffectEvent evt : Effect.EffectEvent.values()) {
				Effect fx = aThis.getEffect(evt);
				if (fx != null) {
					fx.setIsActive(false);
				}
			}
			aThis.getScreen().removeElement(olEl);
		}
	}

	public static void toFront(Element layer) {
		List<Element> l = new ArrayList<Element>();
		while (layer != null) {
			l.add(layer);
			layer = layer.getElementParent();
		}
		Collections.reverse(l);
		for (Element e : l) {
			e.getScreen().updateZOrder(e);
			// e.bringToFront();
		}
	}
}
