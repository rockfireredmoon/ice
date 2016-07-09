/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.Serializable;

public class EntityKey implements Comparable<EntityKey>, Serializable {

	private static final long serialVersionUID = 1L;

	public enum Type {

		ITEM, PARTICLE, ARMOR, DEFAULT
	}

	protected Type type = Type.DEFAULT;
	protected String template;
	protected String item;

	public EntityKey() {
	}

	/**
	 * Construct a new attachable key. The key is split up into it's type, item
	 * and template.
	 * 
	 * @param name
	 *            the name of the attachable (i.e. the base name of this
	 *            attachable part)
	 */
	public EntityKey(String name) {
		setName(name);
	}

	public void setName(String name) {
		int typeIdx = name.indexOf('-');
		if (typeIdx == -1) {
			typeIdx = name.indexOf('.');
			if (typeIdx == -1) {
				throw new IllegalArgumentException(name + " is invalid template key.");
			}
		}
		type = Type.valueOf(name.substring(0, typeIdx).toUpperCase());

		if (type == Type.ITEM) {
			item = name.substring(typeIdx + 1);
		} else {
			name = name.substring(typeIdx + 1);

			int itemIdx = name.lastIndexOf('-');
			if (itemIdx == -1) {
				item = name;
			} else {
				item = name.substring(0, itemIdx);
				template = name.substring(itemIdx + 1);
			}
		}
	}

	/**
	 * Get the path the attachment data files are contained in, i.e the item
	 * path. For example, <strong>Armor/Armor-Base1A</strong> would be the path
	 * for the attachment that has a mesh path of
	 * <strong>Armor/Armor-Base1A/Armor-Base1A-Right_Pauldron.mesh.xml</strong>.
	 * 
	 * @return attachable's path
	 */
	public String getPath() {
		switch (type) {
		case ITEM:
			if (template == null) {
				return String.format("%1$s/%1$s-%2$s", Icelib.toEnglish(type), item);
			} else {
				return String.format("%1$s/%1$s-%2$s-%3$s", Icelib.toEnglish(type), item, template);
			}
		default:
			return String.format("%1$s/%1$s-%2$s", Icelib.toEnglish(type), item);
		}
	}

	/**
	 * Get the <strong>Item name </strong>. For example, for an attachment that
	 * has that has a mesh path of
	 * <strong>Armor/Armor-Base1A/Armor-Base1A-Right_Pauldron.mesh.xml</strong>,
	 * the item name would be <strong>Base1A</strong> (i.e. the directory name
	 * the attachments files are contained in without the Armor- type prefix).
	 * 
	 * @return item name
	 */
	public String getItem() {
		return item;
	}

	/**
	 * Get the type of attachment.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Get the <strong>Template</strong>. For example, for an attachment that
	 * has that has a mesh path of
	 * <strong>Armor/Armor-Base1A/Armor-Base1A-Right_Pauldron.mesh.xml</strong>,
	 * the template would be <strong>Right_Pauldron</strong> (i.e. the mesh name
	 * without the type or prefix).
	 * 
	 * @return template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Get the full name of this attachable, as was used to contruct the
	 * attachable's key For example, for an attachment that has that has a mesh
	 * path of
	 * <strong>Armor/Armor-Base1A/Armor-Base1A-Right_Pauldron.mesh.xml</strong>,
	 * the name would be <strong>Armor-Base1A-Right_Pauldron</strong> (i.e. the
	 * directory name the attachments files are contained in without the Armor-
	 * type prefix).
	 * 
	 * @return name
	 */
	public String getName() {
		switch (type) {
		case ITEM:
			if (template == null)
				return Icelib.toEnglish(type) + "-" + item;
			else
				return Icelib.toEnglish(type) + "-" + item + "-" + template;
		case ARMOR:
		case PARTICLE:
			if (template == null)
				return Icelib.toEnglish(type) + "-" + item;
			else
				return Icelib.toEnglish(type) + "-" + item + "-" + template;
		default:
			return template;
		}
	}

	/**
	 * Get the <strong>Full Item name </strong>. This is the same as
	 * {@link #getItem()} but with the type prefix. For example, for an
	 * attachment that has that has a mesh path of
	 * <strong>Armor/Armor-Base1A/Armor-Base1A-Right_Pauldron.mesh.xml</strong>,
	 * the full item name would be <strong>Armor-Base1A</strong> (i.e. the
	 * directory name the attachments files are contained in).
	 * 
	 * @return full item name
	 */
	public String getItemName() {
		return Icelib.toEnglish(type) + "-" + item;
	}

	public EntityKey clone() {
		EntityKey k = new EntityKey();
		k.type = type;
		k.template = template;
		k.item = item;
		return k;
	}

	@Override
	public String toString() {
		return getClass() + " [type=" + type + ", template=" + template + ", item=" + item + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!EntityKey.class.isAssignableFrom(obj.getClass()))
			return false;
		EntityKey other = (EntityKey) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@Override
	public int compareTo(EntityKey o) {
		int t = type == null ? (o.type == null ? 0 : -1) : (o.type == null ? 1 : type.compareTo(o.type));
		if (t == 0) {
			t = item == null ? (o.item == null ? 0 : -1) : (o.item == null ? 1 : item.compareTo(o.item));
			// t = (item == null ? "" : item).compareTo(o.item == null ? "" :
			// o.item);
		}
		if (t == 0) {
			t = template == null ? (o.template == null ? 0 : -1) : (o.template == null ? 1 : template.compareTo(o.template));
			// t = (item == null ? "" : item).compareTo(o.template == null ? ""
			// : o.template);
		}
		return t;
	}
}