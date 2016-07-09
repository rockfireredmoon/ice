/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

public class ClothingTemplateKey extends EntityKey {

	private static final long serialVersionUID = 1L;

	public ClothingTemplateKey() {
		super();
	}

	public ClothingTemplateKey(String name) {
		super(name);
	}

	public ClothingTemplateKey clone() {
		return new ClothingTemplateKey(getName());
	}

	@Override
	public void setName(String name) {
		int typeIdx = name.indexOf('-');
		if (typeIdx == -1) {
			typeIdx = name.indexOf('.');
			if (typeIdx == -1) {
				// A template key
				type = Type.ARMOR;
				template = name;
				item = null;
				return;
			}
		}
		type = Type.valueOf(name.substring(0, typeIdx).toUpperCase());
		item = name.substring(typeIdx + 1);
		template = null;
	}

}
