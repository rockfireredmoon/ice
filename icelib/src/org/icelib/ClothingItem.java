/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.List;
import java.util.Objects;

import org.icelib.Appearance.ClothingType;

@SuppressWarnings("serial")
public class ClothingItem extends AbstractEntityItem<ClothingTemplateKey> {

	private ClothingType type;

	public ClothingItem(ClothingType type, ClothingTemplateKey asset, String effect, List<RGB> colors) {
		super(asset, effect, colors);
		this.type = type;
	}

	public final ClothingType getType() {
		return type;
	}

	public final void setType(ClothingType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.type);
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
		final ClothingItem other = (ClothingItem) obj;
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AbstractEntityItem<ClothingTemplateKey> o) {
		if (o instanceof ClothingItem) {
			ClothingItem ci = (ClothingItem) o;
			return getType().compareTo(ci.getType());
		}
		return super.compareTo(o);
	}

	// @Override
	// public int compareTo(AbstractEntityItem<ClothingItem> o) {
	// return getType().compareTo(((ClothingItem) o).getType());
	// }

}