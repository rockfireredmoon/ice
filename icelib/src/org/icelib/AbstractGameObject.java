/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class AbstractGameObject<K extends Object> implements Serializable, Cloneable {

	private K id;

	public AbstractGameObject() {
	}

	public AbstractGameObject(K id) {
		setEntityId(id);
	}

	public K getEntityId() {
		return id;
	}

	public final void setEntityId(K id) {
		this.id = id;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractGameObject other = (AbstractGameObject) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public abstract void set(String name, String value, String section);
}
