/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MappedMap<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 1L;
	private Class<K> keyClass;
	private Class<? extends V> valueClass;
	protected Map<K, V> backingMap;

	public MappedMap(Class<K> keyClass, Class<? extends V> valueClass) {
		this(new LinkedHashMap<K, V>(), keyClass, valueClass);
	}

	public MappedMap(Map<K, V> backingMap, Class<K> keyClass, Class<? extends V> valueClass) {
		this.backingMap = backingMap;
		this.keyClass = keyClass;
		this.valueClass = valueClass;
	}

	@Override
	public int size() {
		return backingMap.size();
	}

	@Override
	public boolean isEmpty() {
		return backingMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		key = checkKey(key);
		return backingMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return backingMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		key = checkKey(key);
		return backingMap.get(key);
	}

	private Object checkKey(Object key) {
		if (!keyClass.isAssignableFrom(key.getClass())) {
			key = ObjectMapping.value(key, keyClass, key);
		}
		return key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K k, V v) {
		return (V) doPut(k, v);
	}

	protected Object doGet(Object k) {
		return backingMap.get(k);
	}

	protected Object doPut(Object k, Object v) {
		try {
			K key = ObjectMapping.value(k, keyClass);
			if (checkPuttable(key)) {
				V value = ObjectMapping.value(v, getValueClassForValue(v), key);
				return putToBackingMap(key, value);
			} else {
				return backingMap.get(key);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to map object.", e);
		}
	}

	protected Class<? extends V> getValueClassForValue(Object v) {
		return valueClass;
	}

	protected V putToBackingMap(K key, V value) {
		return backingMap.put(key, value);
	}

	protected boolean checkPuttable(K key) {
		return true;
	}

	@Override
	public V remove(Object key) {
		return backingMap.remove(key);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry e : m.entrySet()) {
			doPut(e.getKey(), e.getValue());
		}
	}

	@Override
	public void clear() {
		backingMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return backingMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return backingMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return backingMap.entrySet();
	}

	public Class<K> getKeyClass() {
		return keyClass;
	}

	public Class<? extends V> getValueClass() {
		return valueClass;
	}

}
