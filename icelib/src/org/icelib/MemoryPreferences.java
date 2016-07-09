/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

final class MemoryPreferences extends AbstractPreferences {

	private static final String[] EMPTY_ARRAY = new String[0];

	private final Map<String, Object> map = new HashMap<>();

	MemoryPreferences() {
		super(null, "");
	}

	private MemoryPreferences(MemoryPreferences parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean isUserNode() {
		return true;
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		return map.keySet().toArray(new String[0]);
	}

	@Override
	protected String getSpi(String key) {
		Object value = map.get(key);
		return (value instanceof String) ? (String) value : null;
	}

	@Override
	protected void removeSpi(String key) {
		map.remove(key);
	}

	@Override
	protected void putSpi(String key, String value) {
		map.put(key, value);
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return EMPTY_ARRAY;
	}

	@Override
	protected AbstractPreferences childSpi(String name) {
		return new MemoryPreferences(this, name);
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
	}
}
