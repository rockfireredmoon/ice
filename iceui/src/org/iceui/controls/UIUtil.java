package org.iceui.controls;

import java.util.prefs.Preferences;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import icetone.core.BaseElement;
import icetone.extras.windows.PersistentWindow;

public class UIUtil {

	public static Vector2f getPersistantPosition(Preferences pref, String id, Vector2f defaultSize) {
		int x = pref.getInt(id + PersistentWindow.WINDOW_X, (int) defaultSize.x);
		int y = pref.getInt(id + PersistentWindow.WINDOW_Y, (int) defaultSize.y);
		return new Vector2f(x, y);
	}

	public static void defaultPosition(Preferences pref, BaseElement el, String id, Vector2f defaultPosition) {
		pref.remove(id + PersistentWindow.WINDOW_X);
		pref.remove(id + PersistentWindow.WINDOW_Y);
		position(pref, el, id, defaultPosition);
	}

	public static void position(Preferences pref, BaseElement el, String id, Vector2f defaultPosition) {
		int x = pref.getInt(id + PersistentWindow.WINDOW_X, -1);
		int y = pref.getInt(id + PersistentWindow.WINDOW_Y, -1);
		if (y + el.getHeight() >= el.getScreen().getHeight()) {
			y = (int) (el.getScreen().getHeight() - el.getHeight());
		}
		if (x + el.getWidth() >= el.getScreen().getWidth()) {
			x = (int) (el.getScreen().getWidth() - el.getWidth());
		}
		el.setPosition(x == -1 ? defaultPosition.x : x, y == -1 ? defaultPosition.y : y);
	}

	public static Vector2f getPersistantSize(Preferences pref, String id, Vector2f defaultSize) {
		int w = pref.getInt(id + PersistentWindow.WINDOW_WIDTH, (int) defaultSize.x);
		int h = pref.getInt(id + PersistentWindow.WINDOW_HEIGHT, (int) defaultSize.y);
		return new Vector2f(w, h);
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

	public static void sizeAndPosition(Preferences pref, BaseElement el, String id, Vector2f defaultSize,
			Vector2f defaultPosition) {
		int x = pref.getInt(id + PersistentWindow.WINDOW_X, -1);
		int y = pref.getInt(id + PersistentWindow.WINDOW_Y, -1);
		int h = pref.getInt(id + PersistentWindow.WINDOW_HEIGHT, -1);
		int w = pref.getInt(id + PersistentWindow.WINDOW_WIDTH, -1);
		el.setPosition(x == -1 ? defaultPosition.x : x, y == -1 ? defaultPosition.y : y);
		el.setDimensions(w == -1 ? defaultPosition.x : w, h == -1 ? defaultPosition.y : h);
	}

}
