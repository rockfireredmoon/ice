/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageParser {

	public enum Type {

		TEXT, LINK, COMMAND, COMMAND_ARG
	}

	public class MessageElement {

		private final Type type;
		private final String value;

		public MessageElement(Type type, String value) {
			this.type = type;
			this.value = value;
		}

		public Type getType() {
			return type;
		}

		public String getValue() {
			return value;
		}
	}

	private List<MessageElement> elements = new ArrayList<>();
	private boolean act;

	public MessageParser() {
	}

	public boolean isCommand() {
		return !elements.isEmpty() && elements.get(0).getType().equals(Type.COMMAND);
	}

	public List<MessageElement> getElements() {
		return elements;
	}

	public boolean isAct() {
		return act;
	}

	public void parse(String text) {

		// Reset
		act = false;
		elements.clear();

		// Some basic patterns
		if (text.startsWith("*") && text.endsWith("*")) {
			text = text.substring(1, text.length() - 1);
			act = true;
		} else if (text.startsWith("/me ")) {
			text = text.substring(4);
			act = true;
		} else if (text.startsWith("/")) {
			parseAsCommand(text);
			return;
		}

		// Look for links and other in text patterns
		StringBuilder bui = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			String str = text.substring(i);
			if (str.startsWith("http://") || str.startsWith("https://")) {
				appendText(bui);
				int idx = findEnd(str);
				if (idx != -1) {
					elements.add(new MessageElement(Type.LINK, str.substring(0, idx)));
					i += idx - 1;
				}
			} else {
				bui.append(text.charAt(i));
			}
		}
		appendText(bui);
	}

	private int findEnd(String text) {
		// Find end of scheme
		int idx = text.indexOf('/');
		if (idx != -1) {
			idx = text.indexOf('/', idx + 1);
			if (idx != -1) {
				// This is end of scheme, look for start of path
				int pidx = text.indexOf('/', idx + 1);
				if (pidx == -1) {
					// If no more slashes, this is just a hostname
					for (int i = idx + 1; i < text.length(); i++) {
						if (!isUrlHostChar(text.charAt(i))) {
							return i;
						}
					}
				} else {
					// Look for next non URL path character
					for (int i = pidx + 1; i < text.length(); i++) {
						if (!isUrlPathChar(text.charAt(i))) {
							return i;
						}
					}
				}
				return text.length();
			}
		}
		return -1;
	}

	private boolean isUrlHostChar(char lastChar) {
		return Arrays.asList('.', '-', '_', ':').contains(lastChar) || Character.isAlphabetic(lastChar)
				|| Character.isDigit(lastChar);
	}

	private boolean isUrlPathChar(char lastChar) {
		return Arrays.asList('/', '.', '-', '_', '~', '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', ':', '@')
				.contains(lastChar) || Character.isAlphabetic(lastChar) || Character.isDigit(lastChar);
	}

	private void appendText(StringBuilder bui) {
		if (bui.length() > 0) {
			elements.add(new MessageElement(Type.TEXT, bui.toString()));
			bui.setLength(0);
		}
	}

	private void appendArg(StringBuilder bui) {
		if (bui.length() > 0) {
			elements.add(new MessageElement(Type.COMMAND_ARG, bui.toString()));
			bui.setLength(0);
		}
	}

	private void parseAsCommand(String text) {
		// Parse as command
		String cmd = text.substring(1);
		int idx = text.indexOf(' ');
		if (idx != -1) {
			cmd = text.substring(1, idx);
		}
		elements.add(new MessageElement(Type.COMMAND, cmd));
		if (idx != -1) {
			// Parse arguments
			StringBuilder bui = new StringBuilder();
			boolean inQuote = false;
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				if (ch == '"' || ch == '\'') {
					inQuote = !inQuote;
					if (!inQuote) {
						elements.add(new MessageElement(Type.COMMAND_ARG, bui.toString()));
						bui.setLength(0);
					}
				} else if (ch == ' ' && !inQuote) {
					appendArg(bui);
				} else {
					bui.append(ch);
				}
			}
			appendArg(bui);
		}
	}
}
