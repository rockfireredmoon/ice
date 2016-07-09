/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.Serializable;
import java.util.List;

import org.icelib.beans.MappedList;
import org.icelib.beans.ObjectDelegate;

@SuppressWarnings("serial")
public class AbstractTemplate<K extends EntityKey, T extends AbstractTemplate<K, T, D>, D extends AbstractTemplate<K, T, D>>
		implements ObjectDelegate<D>, Comparable<T>, Serializable {

	private K key;
	private List<ColourPalette> palette = new MappedList<ColourPalette>(ColourPalette.class);
	private List<RGB> colors = new MappedList<RGB>(Color.class);
	private D delegate;

	public AbstractTemplate() {
	}

	public AbstractTemplate(K key) {
		this.key = key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public K getKey() {
		return key;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + (this.key != null ? this.key.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final T other = (T) obj;
		if (this.key != other.getKey() && (this.key == null || !this.key.equals(other.getKey()))) {
			return false;
		}
		return true;
	}

	public int compareTo(T o) {
		return key == null ? (o.getKey() == null ? 0 : 1) : (o.getKey() == null ? -1 : getKey().compareTo(o.getKey()));
	}

	@Override
	public D getDelegate() {
		return delegate;
	}

	@Override
	public void setDelegate(D delegate) {
		this.delegate = delegate;
	}

	public List<ColourPalette> getPalette() {
		return palette;
	}

	public List<RGB> getColors() {
		return colors;
	}

	@SuppressWarnings("unchecked")
	protected void configureClone(T d) {

		d.setKey((K) key.clone());

		for (ColourPalette c : palette) {
			d.getPalette().add(c.clone());
		}

		for (RGB c : colors) {
			d.getColors().add(c.clone());
		}

		d.setDelegate(delegate);

	}

	@Override
	public String toString() {
		return "AbstractTemplate [key=" + key + ", palette=" + palette + ", colors=" + colors + ", delegate=" + delegate + "]";
	}

}
