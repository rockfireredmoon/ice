package org.icetools.modelman;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratingEnumeration<T> implements Enumeration<T> {

	private Iterator<T> iterator;

	public IteratingEnumeration(Iterator<T> iterator) {
		this.iterator = iterator;
	}
	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public T nextElement() {
		return iterator.next();
	}

}
