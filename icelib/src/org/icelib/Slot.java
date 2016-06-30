/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.logging.Logger;

public enum Slot {

	WEAPON_1(0), WEAPON_2(1), WEAPON_3(2), HEAD(3), NECK(4), SHOULDERS(5), CHEST(6), ARMS(7), HANDS(8), WAIST(9), LEGS(10), FEET(
			11), RING_1(12), RING_2(13), AMULET(14), BAG_1(19), BAG_2(20), BAG_3(21), BAG_4(
					22), RED_CHARM(27), GREEN_CHARM(28), BLUE_CHARM(29), ORANGE_CHARM(30), YELLOW_CHARM(31), PURPLE_CHARM(32);
	private int code;
	private final static Logger LOG = Logger.getLogger(Slot.class.getName());

	private Slot(int code) {
		this.code = code;
	}

	public AttachmentPoint toDefaultAttachmentPoint(EquipType.Side side) {
		switch (this) {
		case HEAD:
			return AttachmentPoint.HELMET;
		case ARMS:
			switch (side) {
			case NA:
			case LEFT:
				return AttachmentPoint.LEFT_FOREARM;
			case RIGHT:
				return AttachmentPoint.RIGHT_FOREARM;
			}
			break;
		case WAIST:
			return AttachmentPoint.BACK_PACK;
		case HANDS:
			switch (side) {
			case NA:
			case LEFT:
				return AttachmentPoint.LEFT_HAND;
			case RIGHT:
				return AttachmentPoint.RIGHT_HAND;
			}
			break;
		case LEGS:
			switch (side) {
			case NA:
			case LEFT:
				return AttachmentPoint.LEFT_CALF;
			case RIGHT:
				return AttachmentPoint.RIGHT_CALF;
			}
			break;
		case SHOULDERS:
			switch (side) {
			case NA:
			case LEFT:
				return AttachmentPoint.LEFT_SHOULDER;
			case RIGHT:
				return AttachmentPoint.RIGHT_SHOULDER;
			}
			break;
		case WEAPON_2:
			switch (side) {
			case NA:
			case RIGHT:
				return AttachmentPoint.RIGHT_HAND;
			case LEFT:
				return AttachmentPoint.LEFT_HAND;
			}
			break;
		case WEAPON_1:
			switch (side) {
			case NA:
			case LEFT:
				return AttachmentPoint.LEFT_HAND;
			case RIGHT:
				return AttachmentPoint.RIGHT_HAND;
			}
			break;
		case WEAPON_3:
			switch (side) {
			case LEFT:
				return AttachmentPoint.LEFT_HAND;
			case NA:
			case RIGHT:
				return AttachmentPoint.RIGHT_HAND;
			}
			break;
		}
		throw new IllegalArgumentException("Cannot convert equip type " + this
				+ " to default attachment point. Suggests this type of item is not an attachment.");
	}

	public EquipType toEquipType() {
		switch (this) {
		case HEAD:
			return EquipType.HEAD;
		case NECK:
			return EquipType.COLLAR;
		case SHOULDERS:
			return EquipType.SHOULDERS;
		case CHEST:
			return EquipType.CHEST;
		case ARMS:
			return EquipType.ARMS;
		case HANDS:
			return EquipType.HANDS;
		case WAIST:
			return EquipType.BELT;
		case LEGS:
			return EquipType.LEGS;
		case FEET:
			return EquipType.FEET;
		}
		throw new UnsupportedOperationException();
	}

	public boolean isBasic() {
		return !isContainer() && !isCharm();
	}

	public boolean isContainer() {
		switch (this) {
		case BAG_1:
		case BAG_2:
		case BAG_3:
		case BAG_4:
			return true;
		}
		return false;
	}

	public boolean isCharm() {
		switch (this) {
		case RED_CHARM:
		case PURPLE_CHARM:
		case ORANGE_CHARM:
		case YELLOW_CHARM:
		case GREEN_CHARM:
		case BLUE_CHARM:
			return true;
		}
		return false;
	}

	public static Slot toBagSlot(int slot) {
		switch (slot) {
		case 1:
			return BAG_1;
		case 2:
			return BAG_2;
		case 3:
			return BAG_3;
		case 4:
			return BAG_4;
		}
		throw new IllegalArgumentException("Not a bag slot number.");
	}

	@Override
	public String toString() {
		return Icelib.toEnglish(name(), true);
	}

	public static Slot fromCode(int code) {
		for (Slot type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		LOG.info("TODO: Unhandle Slot code " + code);
		return null;
	}

	public int getCode() {
		return code;
	}

	public Region toRegion() {
		switch (this) {
		case CHEST:
			return Region.CHEST;
		case ARMS:
			return Region.ARMS;
		case WAIST:
			return Region.BELT;
		case FEET:
			return Region.BOOTS;
		case NECK:
			return Region.COLLAR;
		case HANDS:
			return Region.GLOVES;
		case LEGS:
			return Region.LEGGINGS;
		}
		throw new IllegalArgumentException(String.format("Equipment type %s not appropriate for a Region", this));
	}
}