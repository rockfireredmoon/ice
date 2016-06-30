/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.misc.Nullable;
import org.apache.commons.collections4.Transformer;
import org.icelib.beans.ObjectMapper.ClassKey;

/**
 * Static utilities for mapping name/value pairs to objects.
 * 
 * @see ObjectMapper
 */
public class ObjectMapping {
	private final static Map<ClassKey, Transformer<?, ?>> objectConverters = new HashMap<ClassKey, Transformer<?, ?>>();

	public static Field findField(String name, Class<?> startClass, @Nullable Class<?> exclusiveParent) {
		try {
			return startClass.getDeclaredField(name);
		} catch (NoSuchFieldException nsfe) {
			Class<?> parentClass = startClass.getSuperclass();
			if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
				return findField(name, parentClass, exclusiveParent);
			}
			return null;
		}
	}

	public static Method findMethod(String name, Class<?> startClass, @Nullable Class<?> exclusiveParent, Class<?>... paramClass) {
		if (paramClass.length == 0) {
			try {
				return startClass.getDeclaredMethod(name);
			} catch (NoSuchMethodException nsfe) {
				Class<?> parentClass = startClass.getSuperclass();
				if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
					return findMethod(name, parentClass, exclusiveParent);
				}
			}
		} else {

			for (Class<?> p : paramClass) {
				try {
					return startClass.getDeclaredMethod(name, p);
				} catch (NoSuchMethodException nsfe) {
					Class<?> parentClass = startClass.getSuperclass();
					if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
						return findMethod(name, parentClass, exclusiveParent, paramClass);
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "restriction", "rawtypes" })
	public static <V> V value(Object value, Class<V> targetClass, Object... constructorArgs) {

		Transformer<Object, V> t = (Transformer<Object, V>) objectConverters.get(new ClassKey(value.getClass(), targetClass));
		if (t != null) {
			return (V) (t.transform(value));
		}

		if (value instanceof Number) {
			if (targetClass.equals(Long.class) || targetClass.equals(long.class)) {
				return (V) ((Long) (((Number) value).longValue()));
			} else if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
				return (V) ((Double) (((Number) value).doubleValue()));
			} else if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
				return (V) ((Integer) (((Number) value).intValue()));
			} else if (targetClass.equals(Short.class) || targetClass.equals(short.class)) {
				return (V) ((Short) (((Number) value).shortValue()));
			} else if (targetClass.equals(Float.class) || targetClass.equals(float.class)) {
				return (V) ((Float) (((Number) value).floatValue()));
			} else if (targetClass.equals(Byte.class) || targetClass.equals(byte.class)) {
				return (V) ((Byte) (((Number) value).byteValue()));
			} else if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
				return (V) ((Boolean) (((Number) value).intValue() != 0));
			} else if (targetClass.equals(String.class)) {
				return (V) (value.toString());
			} else {
				throw new IllegalArgumentException(String.format("Cannot convert number %s to %s", value, targetClass));
			}
		} else if (value instanceof Boolean) {
			return (V) (((Boolean) value));
		} else if (value instanceof String) {
			if (targetClass.equals(Long.class)) {
				return (V) ((Long) Long.parseLong((String) value));
			} else if (targetClass.equals(Double.class)) {
				return (V) ((Double) Double.parseDouble((String) value));
			} else if (targetClass.equals(Integer.class)) {
				return (V) ((Integer) Integer.parseInt((String) value));
			} else if (targetClass.equals(Short.class)) {
				return (V) ((Short) Short.parseShort((String) value));
			} else if (targetClass.equals(Float.class)) {
				return (V) ((Float) Float.parseFloat((String) value));
			} else if (targetClass.equals(Byte.class)) {
				return (V) ((Byte) Byte.parseByte((String) value));
			} else if (targetClass.equals(Boolean.class)) {
				return (V) ((Boolean) Boolean.parseBoolean((String) value));
			} else if (targetClass.equals(String.class)) {
				return (V) (value.toString());
			} else if (targetClass.isEnum()) {
				for (V ev : targetClass.getEnumConstants()) {
					if (ev.toString().toLowerCase().equalsIgnoreCase(value.toString())) {
						return ev;
					}
				}
				throw new IllegalArgumentException(String.format("Unknown enum %s in %s", value, targetClass));
			} else {
				// Maybe the target has a constructor that takes a string
				try {
					Constructor<V> con = targetClass.getConstructor(String.class);
					return con.newInstance((String) value);
				} catch (NoSuchMethodException nsme) {
					// That's it, give up
					throw new IllegalArgumentException(String.format("Cannot convert string %s to %s", value, targetClass));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}

			}
		} else if (value instanceof Map) {
			/*
			 * This covers ScriptObjectMirror from Nashorn which is a list AND a
			 * map describing an object or an array. It also covers ordinary map
			 * and lists
			 */

			if (isScriptNativeArray(value)) {
				Collection<?> c = ((jdk.nashorn.api.scripting.ScriptObjectMirror) value).values();
				if (c instanceof List && MappedList.class.isAssignableFrom(targetClass)) {
					try {
						V v = getTargetInstance(value, targetClass, constructorArgs);
						for (Object o : ((List) c)) {
							((List) v).add(o);
						}
						return v;
					} catch (NoSuchMethodException e) {
						throw new IllegalArgumentException(String.format(
								"No zero-length constructor for class %s and no valid constructor args provided.", targetClass));
					} catch (InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new RuntimeException(String.format("Could not construct %s", targetClass), e);
					}
				} else if ((List.class.isAssignableFrom(targetClass) && c instanceof List)
						|| (Collection.class.isAssignableFrom(targetClass) && c instanceof Collection)) {
					return (V) c;
				} else if (Set.class.isAssignableFrom(targetClass)) {
					return (V) new LinkedHashSet<Object>(c);
				} else if (Collection.class.isAssignableFrom(targetClass)) {
					return (V) new ArrayList<Object>(c);
				} else if (targetClass.isArray()) {
					return (V) c.toArray((Object[]) Array.newInstance(targetClass.getComponentType(), c.size()));
				}
			} else if (value instanceof List && List.class.isAssignableFrom(targetClass)) {
				return (V) ((List<?>) value);
			} else if (!isScriptNativeObject(value) && Map.class.isAssignableFrom(targetClass)) {
				return (V) ((Map<?, ?>) value);
			} else {
				try {
					V v = getTargetInstance(value, targetClass, constructorArgs);
					ObjectMapper<?> m = new ObjectMapper<>(v);
					try {
						m.map((Map<String, Object>) value);
					} catch (Exception e) {
						throw new RuntimeException(String.format("Failed to map %s.", targetClass), e);
					}
					return v;
				} catch (NoSuchMethodException e) {
					throw new IllegalArgumentException(String.format(
							"No zero-length constructor for class %s and no valid constructor args provided.", targetClass));
				} catch (InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException(String.format("Could not construct %s", targetClass), e);
				}
			}
		} else if (targetClass.isAssignableFrom(value.getClass())) {
			return (V) value;
		} else if (value instanceof Collection && List.class.isAssignableFrom(targetClass)) {
			try {
				V v = getTargetInstance(value, targetClass, constructorArgs);
				((List) v).addAll((Collection) value);
				return v;
			} catch (InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(String.format("Could not construct %s", targetClass), e);
			} catch (NoSuchMethodException nse) {
			}
		}
		throw new IllegalArgumentException(String.format("Cannot convert %s (%s) to %s", value, value.getClass(), targetClass));
	}

	private static <V> V getTargetInstance(Object value, Class<V> targetClass, Object... constructorArgs)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {
		try {
			Constructor<?> c = targetClass.getConstructor();
			return (V) c.newInstance();
		} catch (NoSuchMethodException e) {
			// Might be able to construct the object using the key
			if (constructorArgs.length > 0) {
				List<Class<?>> l = new ArrayList<Class<?>>();
				for (Object o : constructorArgs) {
					l.add(o.getClass());
				}
				Constructor<?> c = targetClass.getConstructor(l.toArray(new Class<?>[0]));
				return (V) c.newInstance(constructorArgs);
			}
			throw e;
		}
	}

	@SuppressWarnings("restriction")
	public static boolean isScriptNativeArray(Object value) {
		return value instanceof jdk.nashorn.api.scripting.ScriptObjectMirror
				&& ((jdk.nashorn.api.scripting.ScriptObjectMirror) value).isArray();
	}

	@SuppressWarnings("restriction")
	public static boolean isScriptNativeObject(Object value) {
		return value instanceof jdk.nashorn.api.scripting.ScriptObjectMirror
				&& !((jdk.nashorn.api.scripting.ScriptObjectMirror) value).isArray();
	}

	public static <S, T> void addObjectConverter(Class<S> sourceClass, Class<T> targetClass, Transformer<S, T> transformer) {
		objectConverters.put(new ClassKey(sourceClass, targetClass), transformer);
	}

	private static Object copy(Object orig) {
		Object obj = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.flush();
			out.close();
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
		return obj;
	}

	// public static Object delegate(Object sourceObject, Map<String, Object>
	// newObject) {
	// // Object clone = copy(sourceObject);
	// // ObjectMapper<?> m = new ObjectMapper<>(clone);
	// // m.map(newObject);
	// // return clone;
	//
	// Object sourceCopy = cloneThroughJson(sourceObject);
	// ObjectMapper m = new ObjectMapper(sourceCopy);
	// m.map(newObject);
	// return sourceCopy;
	//
	// }

	// public static Map<String, Object> delegate(Object sourceObject,
	// Map<String, Object> newObject) {
	// Map<String, Object> newMap = new HashMap<String, Object>();
	// ObjectMap<Object> existingObject = new ObjectMap<Object>(sourceObject);
	// newMap.putAll(existingObject);
	// newMap.putAll(newObject);
	// return newMap;
	//
	// }
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object delegate(Object sourceObject, Map<String, Object> newObject) {
		if (sourceObject == null) {
			throw new IllegalArgumentException("Must provide source object.");
		}
		Object copy = copy(sourceObject);
		if (copy instanceof ObjectDelegate) {
			((ObjectDelegate) copy).setDelegate(sourceObject);
		}
		ObjectMapper<Object> m = new ObjectMapper<>(copy);
		m.map(newObject);
		return copy;
	}

	public static Iterable<Method> getMethodsUpTo(Class<?> startClass, @Nullable Class<?> exclusiveParent) {

		List<Method> currentClassFields = new ArrayList<Method>(Arrays.asList(startClass.getDeclaredMethods()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Method> parentClassFields = (List<Method>) getMethodsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	public static Iterable<Field> getFieldsUpTo(Class<?> startClass, @Nullable Class<?> exclusiveParent) {

		List<Field> currentClassFields = new ArrayList<Field>(Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

}
