/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import org.icesquirrel.runtime.SquirrelArray;

public class Icelib {
	public static boolean listEqualsNoOrder(List<?> l1, List<?> l2) {
		for (Object k : l1) {
			int idx = l2.indexOf(k);
			if (idx == -1 || !Objects.equals(k, l2.get(idx))) {
				return false;
			}
		}
		return true;
	}

	public static boolean mapEqualsNoOrder(Map<?, ?> m1, Map<?, ?> m2) {
		for (Object k : m1.keySet()) {
			if (m2.containsKey(k)) {
				if (!Objects.equals(m1.get(k), m2.get(k))) {
					return false;
				}
			} else
				return false;
		}
		return true;
	}

	public static String replaceAll(String findtxt, String replacetxt, String str, boolean isCaseInsensitive) {
		if (str == null) {
			return null;
		}
		if (findtxt == null || findtxt.length() == 0) {
			return str;
		}
		if (findtxt.length() > str.length()) {
			return str;
		}
		int counter = 0;
		String thesubstr = "";
		while ((counter < str.length()) && (str.substring(counter).length() >= findtxt.length())) {
			thesubstr = str.substring(counter, counter + findtxt.length());
			if (isCaseInsensitive) {
				if (thesubstr.equalsIgnoreCase(findtxt)) {
					str = str.substring(0, counter) + replacetxt + str.substring(counter + findtxt.length());
					// Failing to increment counter by replacetxt.length()
					// leaves you open
					// to an infinite-replacement loop scenario: Go to replace
					// "a" with "aa" but
					// increment counter by only 1 and you'll be replacing 'a's
					// forever.
					counter += replacetxt.length();
				} else {
					counter++; // No match so move on to the next character from
								 // which to check for a findtxt string match.
				}
			} else {
				if (thesubstr.equals(findtxt)) {
					str = str.substring(0, counter) + replacetxt + str.substring(counter + findtxt.length());
					counter += replacetxt.length();
				} else {
					counter++;
				}
			}
		}
		return str;
	}

	public static String getUrlHostAndPath(URL url) {
		StringBuilder b = new StringBuilder();
		b.append(url.getProtocol());
		b.append("://");
		if (Icelib.isNotNullOrEmpty(url.getHost())) {
			b.append(url.getHost());
		}
		if (url.getPort() > -1) {
			b.append(":");
			b.append(url.getPort());
		}
		if (url.getPath() != null) {
			b.append(url.getPath());
		}
		return b.toString();
	}

	public static String privatisePath(String path) {
		String userhome = System.getProperty("user.home");
		if (path.startsWith(userhome)) {
			return path.substring(userhome.length());
		}
		return path;
	}

	public static String toEnglish(Object object) {
		return toEnglish(object, true);
	}

	public static <T extends Comparable> List<T> sort(Collection<T> collection) {
		List<T> l = new ArrayList(collection);
		Collections.sort(l);
		return l;
	}

	public static List<RGB> toRGBList(List<String> rgbList) {
		List<RGB> rgbs = new ArrayList<RGB>();
		if (rgbList != null) {
			for (String rgb : rgbList) {
				rgbs.add(new Color(rgb));
			}
		}
		return rgbs;
	}

	public static String toEnglish(Object o, boolean name) {
		if (o == null) {
			return "";
		}
		String str = String.valueOf(o);
		boolean newWord = true;
		StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			ch = Character.toLowerCase(ch);
			if (ch == '_') {
				ch = ' ';
			}
			if (ch == ' ') {
				newWord = true;
			}
			if (newWord) {
				ch = Character.toUpperCase(ch);
				newWord = false;
			}
			newStr.append(ch);
		}
		return newStr.toString();
	}

	public static String toHexDigits(int value) {
		return String.format("%02x", value);
	}

	public static String toHexString(RGB color) {
		if (color == null) {
			return "auto";
		}
		return "#" + toHexNumber(color);
	}

	public static String toHexNumber(RGB color) {
		return toHexNumber(color, false);
	}

	public static String toHexNumber(RGB color, boolean includeAlpha) {
		return toHexDigits(color.getRed()) + toHexDigits(color.getGreen()) + toHexDigits(color.getBlue())
				+ (includeAlpha ? toHexDigits(color.getAlpha()) : "");
	}

	public static SquirrelArray createColorArray(List<? extends RGB> colors) {
		SquirrelArray a = new SquirrelArray();
		for (RGB rgb : colors) {
			a.add(Icelib.toHexNumber(rgb).toLowerCase());
		}
		return a;
	}

	public static boolean matches(Object value, Object match) {
		if (isNullOrEmpty(match)) {
			return true;
		}
		return value instanceof String ? nonNull((String) value).toLowerCase().contains(((String) match).toLowerCase())
				: value.equals(match);
	}

	public static boolean matches(String value, String match) {
		if (isNullOrEmpty(match)) {
			return true;
		}
		return nonNull(value).toLowerCase().contains(match.toLowerCase());
	}

	public static String removeTrailingSlashes(String str) {
		while (str.endsWith("/") || str.endsWith("\\")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public static String getBasename(String name) {
		int idx = name.indexOf(".");
		if (idx != -1) {
			name = name.substring(0, idx);
		}
		return name;
	}

	public static boolean notMatches(Object value, Object match) {
		return !matches(value, match);
	}

	public static boolean notMatches(String value, String match) {
		return !matches(value, match);
	}

	public static boolean isNotNullOrEmpty(String sval) {
		return !isNullOrEmpty(sval);
	}

	public static String getDirname(String value) {
		int idx = value.lastIndexOf('/');
		return idx == -1 ? null : value.substring(0, idx);
	}

	public static boolean isNullOrEmpty(String sval) {
		return sval == null || sval.equals("");
	}

	public static boolean isNullOrEmpty(Object sval) {
		return sval == null || (sval instanceof String && sval.equals(""))
				|| (sval instanceof Number && ((Number) sval).doubleValue() == 0);
	}

	public static String getBaseFilename(String name) {
		return getBasename(getFilename(name));
	}

	public static String getFilename(String name) {
		int idx = name.lastIndexOf("/");
		if (idx != -1) {
			name = name.substring(idx + 1);
		}
		return name;
	}

	public static String nonNull(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	public static List<Double> toDoubleList(String listText) {
		while (listText.endsWith(";")) {
			listText = listText.substring(0, listText.length() - 1);
		}
		listText = listText.trim();
		List<Double> l = new ArrayList<Double>();
		if (Icelib.isNullOrEmpty(listText)) {
			l.add(0.0);
		} else {
			for (String s : listText.split(",")) {
				l.add(Double.parseDouble(s.trim()));
			}
		}
		return l;
	}

	public static String toCommaSeparatedList(Collection selectedFiles) {
		return toSeparatedList(selectedFiles, ",");
	}

	public static List<Integer> toIntegerList(String listText) {
		List<Integer> l = new ArrayList<Integer>();
		for (String s : listText.split(",")) {
			l.add(Integer.parseInt(s.trim()));
		}
		return l;
	}

	public static List<Long> toLongList(String listText) {
		while (listText.endsWith(";")) {
			listText = listText.substring(0, listText.length() - 1);
		}
		listText = listText.trim();
		List<Long> l = new ArrayList<Long>();
		if (Icelib.isNullOrEmpty(listText)) {
			l.add(0L);
		} else {
			for (String s : listText.split(",")) {
				l.add(Long.parseLong(s.trim()));
			}
		}
		return l;
	}

	public static Long toLong(String value, Long defaultValue) {
		value = value.trim();
		if (value.equals("")) {
			return defaultValue;
		}
		return Long.parseLong(value);
	}

	static float approximate(float value, int precision) {
		float approx = (float) (Math.round(value * (10 ^ precision))) / (float) (10 ^ precision);
		return approx;
	}

	public static String toSeparatedList(int start, int len, String separator, Object... values) {
		return toSeparatedList(Arrays.asList(values).subList(start, start + 1), separator);
	}

	public static String toSeparatedList(Collection list, String separator) {
		StringBuilder bui = new StringBuilder();
		if (list != null) {
			for (Object o : list) {
				if (bui.length() > 0) {
					bui.append(separator);
				}
				bui.append(String.valueOf(o));
			}
		}
		return bui.toString();
	}

	public static String formatElapsedTime(long timeLogged) {
		long time = timeLogged / 1000;
		int seconds = (int) (time % 60);
		int minutes = (int) ((time % 3600) / 60);
		int hours = (int) (time / 3600);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static String toNumberName(int i) {
		switch (i) {
		case 1:
			return "first";
		case 2:
			return "second";
		case 3:
			return "third";
		case 4:
			return "fourth";
		case 5:
			return "fifth";
		case 6:
			return "sixth";
		case 7:
			return "seventh";
		case 8:
			return "eighth";
		case 9:
			return "ninth";
		case 10:
			return "tenth";
		default:
			throw new IllegalArgumentException();
		}
	}

	public static String format(double number) {
		if ((long) number == number) {
			return String.valueOf((long) number);
		}
		return String.valueOf(number);
	}

	public static String toCommaSeparatedList(Object[] selectedFiles) {
		return Icelib.toCommaSeparatedList(Arrays.asList(selectedFiles));
	}

	public static long count(String string, String search) {
		int r = -1;
		int c = 0;
		do {
			r = string.indexOf(search, r + 1);
			if (r != -1) {
				c++;
			}
		} while (r != -1);
		return c;
	}

	public static String toMultilineText(String[] warpTo) {
		return warpTo == null ? "" : toSeparatedList(Arrays.asList(warpTo), "\n");
	}

	public static String trimDisplay(String text, int max) {
		if (max > 3 && text.length() > (max - 3)) {
			return text.substring(0, max - 3) + "...";
		}
		return text;
	}

	public static String trimTail(String text) {
		char[] c = text.toCharArray();
		for (int i = c.length - 1; i >= 0; i--) {
			char ch = c[i];
			if (ch != ' ' && ch != '\r' && ch != '\n' && ch != '\t') {
				return text.substring(0, i + 1);
			}
		}
		return "";
	}

	public static boolean close(float val1, float val2, int precision) {
		return approximate(val1, precision) == approximate(val2, precision);
	}

	public static boolean close(float val1, float val2) {
		return close(val1, val2, 5);
	}

	public static long parseElapsedTime(String timeLogged) {
		StringTokenizer t = new StringTokenizer(timeLogged, ":");
		int hours = Integer.parseInt(t.nextToken());
		int minutes = Integer.parseInt(t.nextToken());
		int seconds = Integer.parseInt(t.nextToken());
		return (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
	}

	public static String toBooleanString(boolean value) {
		return value ? "1" : "0";
	}

	public static void trace() {
		try {
			throw new Exception();
		} catch (Exception e) {
			System.err.print(DateFormat.getDateTimeInstance().format(new Date()) + " ");
			e.printStackTrace(System.out);
		}
	}

	public static String camelToEnglish(String text) {
		return camelToEnglish(text, false);
	}

	public static String englishToCamel(String text) {
		StringBuilder bui = new StringBuilder();
		boolean nextIsWord = false;
		for (Character c : text.toCharArray()) {
			if (c == ' ') {
				nextIsWord = true;
			} else {
				if (nextIsWord) {
					bui.append(Character.toUpperCase(c));
					nextIsWord = false;
				} else {
					bui.append(c);
				}
			}
		}
		return bui.toString();
	}

	public static String camelToEnglish(String text, boolean name) {
		StringBuilder bui = new StringBuilder();
		boolean word = true;
		for (Character c : text.toCharArray()) {
			if (Character.isUpperCase(c)) {
				if (bui.length() > 0) {
					bui.append(' ');
				}
				if (name) {
					bui.append(Character.toUpperCase(c));
				} else {
					bui.append(c);
				}
			} else {
				bui.append(c);
			}
		}
		return bui.toString();
	}

	public static String debugString(String string) {
		StringBuilder bui = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\r') {
				bui.append("\\r");
			} else if (c == '\n') {
				bui.append("\\n");
			} else {
				bui.append(c);
			}
		}
		return bui.toString();
	}

	public static String toSeparatedList(String[] args, int i, int i0) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		 // change
																		 // body
																		 // of
																		 // generated
																		 // methods,
																		 // choose
																		 // Tools
																		 // |
																		 // Templates.
	}

	public static void dumpTrace() {
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isUrl(String location) {
		return location.startsWith("http://") || location.startsWith("https://") || location.startsWith("file:///");
	}

	public static String encodeAssetUrlValue(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decodeAssetUrlValue(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void removeMe(String message, Object... args) {
		System.err.println(String.format("[REMOVEME] " + message, args));
	}

	public static float getSpeedFactor(int speed) {
		return ((float) speed + 100f) / 100f;
	}

	public static float rot2rad(short rot) {
		return (short) ((float) rot * (Math.PI * 2) / 256.0);
	}

	public static short rad2rot(float rad) {
		return (short) ((float) rad * 256.0 / (Math.PI * 2));
	}

}
