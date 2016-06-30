/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MappedList<K> implements List<K>, Serializable {

	private static final long serialVersionUID = 1L;
	private List<K> backingList;
	private Class<? extends K> valueClass;

	public MappedList(Class<? extends K> valueClass) {
		this(new ArrayList<K>(), valueClass);
	}

	public MappedList(List<K> backingList, Class<? extends K> valueClass) {
		this.backingList = backingList;
		this.valueClass = valueClass;
	}

	@Override
	public int size() {
		return backingList.size();
	}

	@Override
	public boolean isEmpty() {
		return backingList.isEmpty();
	}

	@Override
	public boolean add(K v) {
		return backingList.add(ObjectMapping.value(v, valueClass));
	}

	@Override
	public void clear() {
		backingList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return backingList.contains(ObjectMapping.value(o, valueClass));
	}

	@Override
	public Iterator<K> iterator() {
		return backingList.iterator();
	}

	@Override
	public Object[] toArray() {
		return backingList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return backingList.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return backingList.remove(ObjectMapping.value(o, valueClass));
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backingList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		for (K k : c) {
			backingList.add(ObjectMapping.value(k, valueClass));
		}
		return c.size() > 0;
	}

	@Override
	public boolean addAll(int index, Collection<? extends K> c) {
		for (K k : c) {
			add(index++, ObjectMapping.value(k, valueClass));
		}
		return c.size() > 0;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return backingList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return backingList.retainAll(c);
	}

	@Override
	public K get(int index) {
		return backingList.get(index);
	}

	@Override
	public K set(int index, K element) {
		return backingList.set(index, ObjectMapping.value(element, valueClass));
	}

	@Override
	public void add(int index, K element) {
		backingList.add(index, ObjectMapping.value(element, valueClass));
	}

	@Override
	public K remove(int index) {
		return backingList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return backingList.indexOf(ObjectMapping.value(o, valueClass));
	}

	@Override
	public int lastIndexOf(Object o) {
		return backingList.lastIndexOf(ObjectMapping.value(o, valueClass));
	}

	@Override
	public ListIterator<K> listIterator() {
		return backingList.listIterator();
	}

	@Override
	public ListIterator<K> listIterator(int index) {
		return backingList.listIterator(index);
	}

	@Override
	public List<K> subList(int fromIndex, int toIndex) {
		return backingList.subList(fromIndex, toIndex);
	}

}
