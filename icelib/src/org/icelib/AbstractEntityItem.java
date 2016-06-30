/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.icesquirrel.runtime.SquirrelTable;

@SuppressWarnings("serial")
public class AbstractEntityItem<K extends EntityKey> implements Serializable, Comparable<AbstractEntityItem<K>> {

	private K key;
	private List<RGB> colors;
	private AttachmentPoint node;
	private String effect;

	public AbstractEntityItem(AbstractEntityItem<K> original) {
		this.key = original.key;
		this.colors = new ArrayList<RGB>(original.colors);
		this.node = original.node;
		this.effect = original.effect;
	}

	public AbstractEntityItem(K asset) {
		this(asset, null, null);
	}

	public AbstractEntityItem(K asset, String effect, List<RGB> colors) {
		this(asset, effect, colors, null);
	}

	public AbstractEntityItem(K asset, String effect, List<RGB> colors, AttachmentPoint node) {
		super();
		setNode(node);
		setKey(asset);
		setEffect(effect);
		this.colors = colors;
	}

	public final AttachmentPoint getNode() {
		return node;
	}

	public final AbstractEntityItem<K> setNode(AttachmentPoint node) {
		this.node = node;
		return this;
	}

	public final K getKey() {
		return key;
	}

	public final AbstractEntityItem<K> setKey(K asset) {
		this.key = asset;
		return this;
	}

	public final List<RGB> getColors() {
		return colors;
	}

	public final AbstractEntityItem<K> setColors(List<RGB> colors) {
		this.colors = colors;
		return this;
	}

	public String getEffect() {
		return effect;
	}

	public final AbstractEntityItem<K> setEffect(String effect) {
		this.effect = effect;
		return this;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.key);
		hash = 79 * hash + Objects.hashCode(this.colors);
		hash = 79 * hash + Objects.hashCode(this.node);
		hash = 79 * hash + Objects.hashCode(this.effect);
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
		final AbstractEntityItem<K> other = (AbstractEntityItem<K>) obj;
		if (!Objects.equals(this.key, other.key)) {
			return false;
		}
		if (!Objects.equals(this.colors, other.colors)) {
			return false;
		}
		if (this.node != other.node) {
			return false;
		}
		if (!Objects.equals(this.effect, other.effect)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AbstractEntityItem<K> o) {
		return key.compareTo(o.key);
	}

	public SquirrelTable toSerializable() {
		SquirrelTable to = new SquirrelTable();
		if (getNode() != null) {
			to.insert("node", getNode().name().toLowerCase());
		}
		to.insert("type", getKey().getName());
		if (getColors() != null && !getColors().isEmpty()) {
			to.insert("colors", Icelib.createColorArray(getColors()));
		}
		if (getEffect() != null) {
			to.insert("effect", getEffect());
		}
		return to;
	}

	@Override
	public String toString() {
		return "AttachmentItem{" + "asset=" + key + ", colors=" + colors + ", node=" + node + ", effect=" + effect + '}';
	}
}