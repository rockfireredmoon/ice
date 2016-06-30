/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.Arrays;

public enum Region {

	ARMS, CHEST, LEGGINGS, BELT, COLLAR, GLOVES, BOOTS;

	public int toSortKey(BodyType bodyType) {
		switch (bodyType) {
		case ROBED:
			switch (this) {
			case BOOTS:
				return 5;
			case LEGGINGS:
				return 6;
			}
		}
		return ordinal();
	}

	public EquipType toEquipType() {
		switch (this) {
		case ARMS:
			return EquipType.ARMS;
		case BELT:
			return EquipType.BELT;
		case CHEST:
			return EquipType.CHEST;
		case COLLAR:
			return EquipType.COLLAR;
		case GLOVES:
			return EquipType.HANDS;
		case LEGGINGS:
			return EquipType.LEGS;
		case BOOTS:
			return EquipType.FEET;
		}
		throw new IllegalArgumentException();
	}

	public Appearance.ClothingType toClothingType() {
		switch (this) {
		case ARMS:
			return Appearance.ClothingType.ARMS;
		case BELT:
			return Appearance.ClothingType.BELT;
		case CHEST:
			return Appearance.ClothingType.CHEST;
		case COLLAR:
			return Appearance.ClothingType.COLLAR;
		case GLOVES:
			return Appearance.ClothingType.GLOVES;
		case LEGGINGS:
			return Appearance.ClothingType.LEGGINGS;
		case BOOTS:
			return Appearance.ClothingType.BOOTS;
		}
		throw new IllegalArgumentException();
	}
}
