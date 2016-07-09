/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Present a Java object as dynamic map of all it's fields and getters as map of
 * name/object pairs.
 */
public final class ObjectMap<T> implements Map<String, Object> {

	class Key {
		Field field;
		Method method;
		String name;

		Key(Field field, String name) {
			this.field = field;
			this.name = name;
		}

		Key(Method method, String name) {
			this.method = method;
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private ObjectMap getOuterType() {
			return ObjectMap.this;
		}

	}

	private Set<Key> keys;
	private Set<String> keyNames;
	private T object;

	public ObjectMap(T object) {
		this.object = object;
	}

	private void checkKeys() {
		if (keys == null) {
			keys = new LinkedHashSet<Key>();
			keyNames = new LinkedHashSet<String>();
			for (Field f : ObjectMapping.getFieldsUpTo(object.getClass(), Object.class)) {
				if ((f.getModifiers() & Modifier.PUBLIC) != 0) {
					keyNames.add(f.getName());
					f.setAccessible(true);
					keys.add(new Key(f, f.getName()));
				}
			}
			for (Method m : ObjectMapping.getMethodsUpTo(object.getClass(), Object.class)) {
				String name = m.getName();
				boolean isPublic = (m.getModifiers() & Modifier.PUBLIC) != 0;
				if (isPublic && name.startsWith("get") && name.length() > 3) {
					m.setAccessible(true);
					name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
					keyNames.add(name);
					keys.add(new Key(m, name));
				} else if (isPublic && name.startsWith("is") && name.length() > 2) {
					m.setAccessible(true);
					name = Character.toLowerCase(name.charAt(2)) + name.substring(3);
					keyNames.add(name);
					keys.add(new Key(m, name));
				}
			}
		}
	}

	@Override
	public int size() {
		return keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		checkKeys();
		return keyNames.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@Override
	public Object get(Object key) {
		for (Key k : keys) {
			if (k.name.equals(key)) {
				if (k.field != null) {
					try {
						return k.field.get(object);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						return k.method.invoke(object);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		checkKeys();
		return keyNames;
	}

	@Override
	public Collection<Object> values() {
		checkKeys();
		List<Object> vals = new ArrayList<Object>();
		for (Object k : keySet()) {
			vals.add(get(k));
		}
		return vals;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Set<Entry<String, Object>> entries = new LinkedHashSet<Map.Entry<String, Object>>();
		for (String s : keySet()) {
			entries.add(new Entry<String, Object>() {
				@Override
				public Object setValue(Object value) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Object getValue() {
					return get(s);
				}

				@Override
				public String getKey() {
					return s;
				}
			});
		}
		return entries;
	}
}