/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public abstract class AbstractConfig {
	public final static int FALSE = 0;
	public final static int TRUE = 1;
	public final static int DEFAULT = 2;

	public final static String WINDOW_LOCK = "WindowLock";

	public static Object getDefaultValue(Class<? extends AbstractConfig> clazz, String key) {
		// Try and find the default
		Object defaultValue = null;
		try {
			for (Field f : clazz.getDeclaredFields()) {
				if (f.get(null).equals(key)) {
					Field n = clazz.getDeclaredField(f.getName() + "_DEFAULT");
					defaultValue = n.get(null);
				}
			}
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static Preferences get() {
		return Preferences.userRoot().node("icescene");
	}

	public static List<String> toStringList(String val) {
		return Arrays.asList(val.split(","));
	}

	public static String toListString(Object... val) {
		return toListString(Arrays.asList(val));
	}

	public static String toListString(List<Object> val) {
		StringBuilder bui = new StringBuilder();
		for (Object o : val) {
			if (bui.length() > 0) {
				bui.append(",");
			}
			bui.append(o.toString());
		}
		return bui.toString();
	}

	public static boolean toggle(Preferences node, String key, boolean defaultValue) {
		boolean was = node.getBoolean(key, defaultValue);
		was = !was;
		node.putBoolean(key, was);
		return was;
	}
}
