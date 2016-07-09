/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.Arrays;

public enum ChannelType {

	REGION, SAY, TELL, TRADE, DAMAGE, PARTY, CLAN, CUSTOM, SYSTEM, GM, ERROR, EMOTE, FRIENDS;

	public static ChannelType fromCode(String channelName) {
		if (channelName.equals("rc")) {
			return REGION;
		} else if (channelName.equals("t")) {
			return TELL;
		} else if (channelName.equals("clan")) {
			return CLAN;
		} else if (channelName.equals("party")) {
			return PARTY;
		} else if (channelName.equals("s")) {
			return SAY;
		} else if (channelName.equals("tc")) {
			return TRADE;
		} else if (channelName.equals("gm")) {
			return GM;
		} else if (channelName.equals("emote")) {
			return EMOTE;
		} else if (channelName.equals("*SysChat")) {
			return SYSTEM;
		}
		throw new IllegalArgumentException("No channel for " + channelName);
	}

	public RGB getColor() {
		switch (this) {
		case REGION:
			return new Color("#aaaaaa");
		case TELL:
			return new Color("#ff00f0");
		case TRADE:
			return new Color("#f0ff00");
		case DAMAGE:
			return new Color("#ff0000");
		case PARTY:
			return new Color("#00bcde");
		case CLAN:
			return new Color("#29d245");
		case CUSTOM:
			return new Color("#ff9600");
		case SYSTEM:
			return new Color("#f0ff00");
		case GM:
			return new Color("#ff9081");
		case ERROR:
			return new Color("#ff0000");
		case EMOTE:
			return new Color("#ffff00");
		case FRIENDS:
			return new Color("#5555ff");
		default:
			return Color.WHITE;
		}
	}

	public boolean hasPart() {
		switch (this) {
		case REGION:
		case TELL:
		case TRADE:
		case EMOTE:
		case GM:
			return true;
		}
		return false;
	}

	public boolean hasSubChannel() {
		switch (this) {
		case TELL:
			return true;
		}
		return false;
	}

	public boolean isChat() {
		return Arrays.asList(REGION, SAY, TELL, TRADE, PARTY, CLAN, CUSTOM, GM).contains(this);
	}

	public boolean isPrimary() {
		return Arrays.asList(REGION, SAY, TRADE, PARTY, CLAN).contains(this);
	}

	public String toCode() {
		switch (this) {
		case REGION:
			return "rc";
		case CLAN:
			return "clan";
		case TELL:
			return "t";
		case PARTY:
			return "party";
		case SAY:
			return "s";
		case TRADE:
			return "tc";
		case GM:
			return "gm";
		case EMOTE:
			return "emote";
		default:
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public String format() {
		String s = toCode();
		if (hasPart()) {
			s += "/";
		}
		return s;
	}

	public static ChannelType parseChannelName(String channel) {
		String[] a = channel.split("/");
		return fromCode(a[0]);
	}

	public static String parsePart(String channel) {
		String[] a = channel.split("/");
		return a.length > 1 ? a[1] : null;
	}

}
