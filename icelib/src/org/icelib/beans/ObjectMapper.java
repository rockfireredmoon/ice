/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib.beans;

import static org.icelib.beans.ObjectMapping.findMethod;
import static org.icelib.beans.ObjectMapping.getMethodsUpTo;
import static org.icelib.beans.ObjectMapping.isScriptNativeArray;
import static org.icelib.beans.ObjectMapping.value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.Transformer;

/**
 * Takes a {@link Map} of name/value pairs and builds an object from them. Both
 * primitive properties and nested objects are populated. Intended for use a
 * bridge between the scripted side of Ice* apps and the native Java side,
 * allowing both to create and access the same objects.
 * <p>
 * This class is mainly used for object creation, and currently embedded lists
 * and maps should extend {@link MappedMap} and {@link MappedList} respectively.
 * This is required because these types carry the class of the objects that they
 * will contain at runtime.
 * <p>
 * If any custom type mapping is required, a {@link Transformer} should be
 * registered using {@link #addObjectConverter(Class, Class, Transformer)}.
 *
 * @param <T>
 *            type of object to map
 */
public class ObjectMapper<T> {

	private T object;

	static class ClassKey {
		Class<?> sourceClass;
		Class<?> targetClass;

		ClassKey(Class<?> sourceClass, Class<?> targetClass) {
			super();
			this.sourceClass = sourceClass;
			this.targetClass = targetClass;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((sourceClass == null) ? 0 : sourceClass.hashCode());
			result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
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
			ClassKey other = (ClassKey) obj;
			if (sourceClass == null) {
				if (other.sourceClass != null)
					return false;
			} else if (!sourceClass.equals(other.sourceClass))
				return false;
			if (targetClass == null) {
				if (other.targetClass != null)
					return false;
			} else if (!targetClass.equals(other.targetClass))
				return false;
			return true;
		}

	}

	public ObjectMapper(T object) {
		this.object = object;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
	public T map(Map<String, Object> properties) {
		try {
			for (Map.Entry<String, Object> en : properties.entrySet()) {
				String key = en.getKey();
				Object val = en.getValue();

				// Look for a field in the object the name
				// Look for setter. First try the native class
				String camelCaseName = key.substring(0, 1).toUpperCase() + (key.length() > 1 ? key.substring(1) : "");
				String setterName = "set" + camelCaseName;
				Method m = findMethod(setterName, object.getClass(), null, val.getClass());
				if (m == null) {
					if (val instanceof Long) {
						m = findMethod(setterName, object.getClass(), null, Double.class, Integer.class, Float.class, Short.class,
								Byte.class, String.class, Boolean.class);
					} else if (val instanceof Integer) {
						m = findMethod(setterName, object.getClass(), null, Long.class, Double.class, Float.class, Short.class,
								Byte.class, String.class, Boolean.class);
					} else if (val instanceof Short) {
						m = findMethod(setterName, object.getClass(), null, Integer.class, Long.class, Double.class, Float.class,
								Byte.class, String.class, Boolean.class);
					} else if (val instanceof Byte) {
						m = findMethod(setterName, object.getClass(), null, Short.class, Integer.class, Long.class, Double.class,
								Float.class, String.class, Boolean.class);
					} else if (val instanceof Double) {
						m = findMethod(setterName, object.getClass(), null, Float.class, Long.class, Integer.class, Short.class,
								Byte.class, String.class, Boolean.class);
					} else if (val instanceof Float) {
						m = findMethod(setterName, object.getClass(), null, Double.class, Integer.class, Long.class, Short.class,
								Byte.class, String.class, Boolean.class);
					} else if (val instanceof String) {
						m = findMethod(setterName, object.getClass(), null, Double.class, Long.class, Integer.class, Float.class,
								Short.class, Byte.class, String.class, Boolean.class);
					} else if (val instanceof Boolean) {
						m = findMethod(setterName, object.getClass(), null, Double.class, Long.class, Integer.class, Float.class,
								Short.class, Byte.class, String.class);
					} else if (val instanceof Map) {
						m = findMethod(setterName, object.getClass(), null, Map.class);
						if (m == null) {
							// Find the first setter that matches the exact
							// type
							Iterable<Method> methodsUpTo = getMethodsUpTo(object.getClass(), null);
							for (Method method : methodsUpTo) {
								if (method.getName().equals(setterName) && method.getParameterTypes().length == 1
										&& method.getParameterTypes()[0].isAssignableFrom(object.getClass())) {
									m = method;
									break;
								}
							}

							/*
							 * No exact type, Find the first setter that matches
							 * the exact type
							 */
							if (m == null) {
								for (Method method : methodsUpTo) {
									if (method.getName().equals("set" + key) && method.getParameterTypes().length == 1) {
										m = method;
										break;
									}
								}
							}
						}
					}
				}

				if (m != null) {
					m.setAccessible(true);
					m.invoke(object, value(val, m.getParameterTypes()[0]));
				} else {
					/*
					 * If this is a map, and there is a getter, and it is a
					 * MappedMap, fill it up processing all the elements *
					 */
					if (isScriptNativeArray(val)) {
						val = ((jdk.nashorn.api.scripting.ScriptObjectMirror) val).values();
					}

					if (val instanceof Map) {
						Method getter = findMethod("get" + camelCaseName, object.getClass(), null);
						if (getter != null) {
							Object o = getter.invoke(object);
							if (o instanceof Map) {
								Map mm = (Map) o;
								mm.clear();
								mm.putAll((Map) val);
								continue;
							}
						}
					}

					// if(camelCaseName.equals("Colors")) {
					// System.out.println("BREAK!");
					// }
					if (val instanceof List) {
						Method getter = findMethod("get" + camelCaseName, object.getClass(), null);
						if (getter != null) {
							Object o = getter.invoke(object);
							if (o instanceof List) {
								List mm = (List<?>) o;
								mm.clear();
								mm.addAll((List) val);
								continue;
							}
						}
					}

					Field f = ObjectMapping.findField(key, object.getClass(), Object.class);
					if (f != null) {
						f.setAccessible(true);
						f.set(object, value(val, f.getType()));
					} else {
						if (object instanceof Map) {
							Map mm = (Map) object;
							mm.put(key, val);
						} else {
							throw new IllegalArgumentException(
									String.format("Could not map key %s and value %s to the object %s (object is %s)", key, val,
											toString(), object.getClass()));
						}
					}
				}

			}
		} catch (Exception e) {
			throw new RuntimeException("Could not map object.", e);
		}
		return object;
	}
}
