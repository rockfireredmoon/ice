/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public abstract class AbstractCreature extends AbstractGameObject<Long> {

	private final static Logger LOG = Logger.getLogger(AbstractCreature.class.getName());
	private int speed = 0;
	private int strength;
	private int dexterity;
	private int constitution;
	private int psyche;
	private int spirit;
	private boolean willRegen;
	private boolean mightRegen;
	private int offhandWeaponDamage;
	private int castingSetbackChance;
	private int channelingBreakChance;
	private int level;
	private String displayName;
	private Appearance appearance = new Appearance();
	private CreatureCategory creatureCategory = CreatureCategory.ANIMAL;
	private int damageResistMelee;
	private int damageResistFire;
	private int damageResistFrost;
	private int damageResistMystic;
	private int damageResistDeath;
	private int armour;
	private Profession profession;
	private Appearance eqAppearance;
	private String subName;
	/*
	 * The following are transient and not part of the creature itself, but
	 * provided
	 * by either the player location, or spawn packages
	 */

	public AbstractCreature() {
		super();
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("ID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("strength")) {
			strength = Integer.parseInt(value);
		} else if (name.equals("dexterity")) {
			dexterity = Integer.parseInt(value);
		} else if (name.equals("constitution")) {
			constitution = Integer.parseInt(value);
		} else if (name.equals("psyche")) {
			psyche = Integer.parseInt(value);
		} else if (name.equals("spirit")) {
			spirit = Integer.parseInt(value);
		} else if (name.equals("will_regen")) {
			willRegen = "1".equals(value);
		} else if (name.equals("might_regen")) {
			mightRegen = "1".equals(value);
		} else if (name.equals("damage_resist_melee")) {
			damageResistMelee = Integer.parseInt(value);
		} else if (name.equals("damage_resist_fire")) {
			damageResistFire = Integer.parseInt(value);
		} else if (name.equals("damage_resist_frost")) {
			damageResistFrost = Integer.parseInt(value);
		} else if (name.equals("damage_resist_mystic")) {
			damageResistMystic = Integer.parseInt(value);
		} else if (name.equals("damage_resist_death")) {
			damageResistDeath = Integer.parseInt(value);
		} else if (name.equals("offhand_weapon_damage")) {
			offhandWeaponDamage = Integer.parseInt(value);
		} else if (name.equals("casting_setback_chance")) {
			castingSetbackChance = Integer.parseInt(value);
		} else if (name.equals("channeling_break_chance")) {
			channelingBreakChance = Integer.parseInt(value);
		} else if (name.equals("appearance")) {
			try {
				appearance = new Appearance(value);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to parse appearance.", e);
			}
		} else if (name.equals("eq_appearance")) {
			try {
				eqAppearance = new Appearance(value);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (name.equals("level")) {
			level = Integer.parseInt(value);
		} else if (name.equals("display_name")) {
			displayName = value;
		} else if (name.equals("profession")) {
			profession = Profession.fromCode(Integer.parseInt(value));
		} else if (name.equals("creature_category")) {
			creatureCategory = CreatureCategory.fromCode(value);
		} else if (!name.equals("") || !value.equals("")) {
			LOG.info("TODO: " + getClass().getSimpleName() + " (Unhandled property " + name + " = " + value);
		}
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	/**
	 * Return the actual equipped items
	 */
	public abstract List<Long> getEquippedItems();

	// public PageLocation getTile() {
	// final Zone zone1 = getZone();
	// if(zone1 == null) {
	// return null;
	// }
	// final TerrainTemplateConfiguration terrainConfig =
	// zone1.getTerrainConfig();
	// return terrainConfig.getTile(new Vector2f(location.x, location.z));
	// }

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public final int getStrength() {
		return strength;
	}

	public final Appearance getEqAppearance() {
		return eqAppearance;
	}

	public final void setEqAppearance(Appearance eqAppearance) {
		this.eqAppearance = eqAppearance;
	}

	public final void setStrength(int strength) {
		this.strength = strength;
	}

	public final int getDexterity() {
		return dexterity;
	}

	public final void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public final int getConstitution() {
		return constitution;
	}

	public final void setConstitution(int constitution) {
		this.constitution = constitution;
	}

	public int getArmour() {
		return armour;
	}

	public void setArmour(int armour) {
		this.armour = armour;
	}

	public final int getPsyche() {
		return psyche;
	}

	public final void setPsyche(int psyche) {
		this.psyche = psyche;
	}

	public final int getSpirit() {
		return spirit;
	}

	public final void setSpirit(int spirit) {
		this.spirit = spirit;
	}

	public final boolean isWillRegen() {
		return willRegen;
	}

	public final void setWillRegen(boolean willRegen) {
		this.willRegen = willRegen;
	}

	public final boolean isMightRegen() {
		return mightRegen;
	}

	public final void setMightRegen(boolean mightRegen) {
		this.mightRegen = mightRegen;
	}

	public final int getOffhandWeaponDamage() {
		return offhandWeaponDamage;
	}

	public final void setOffhandWeaponDamage(int offhandWeaponDamage) {
		this.offhandWeaponDamage = offhandWeaponDamage;
	}

	public final int getCastingSetbackChance() {
		return castingSetbackChance;
	}

	public final void setCastingSetbackChance(int castingSetbackChance) {
		this.castingSetbackChance = castingSetbackChance;
	}

	public final int getChannelingBreakChance() {
		return channelingBreakChance;
	}

	public final void setChannelingBreakChance(int channelingBreakChance) {
		this.channelingBreakChance = channelingBreakChance;
	}

	public final int getLevel() {
		return level;
	}

	public final void setLevel(int level) {
		this.level = level;
	}

	public final String getDisplayName() {
		return displayName;
	}

	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public final Appearance getAppearance() {
		return appearance;
	}

	public final void setAppearance(Appearance appearance) {
		this.appearance = appearance;
	}

	public final CreatureCategory getCreatureCategory() {
		return creatureCategory;
	}

	public final void setCreatureCategory(CreatureCategory creatureCategory) {
		this.creatureCategory = creatureCategory;
	}

	public final int getDamageResistMelee() {
		return damageResistMelee;
	}

	public final void setDamageResistMelee(int damageResistMelee) {
		this.damageResistMelee = damageResistMelee;
	}

	public final int getDamageResistFire() {
		return damageResistFire;
	}

	public final void setDamageResistFire(int damageResistFire) {
		this.damageResistFire = damageResistFire;
	}

	public final int getDamageResistFrost() {
		return damageResistFrost;
	}

	public final void setDamageResistFrost(int damageResistFrost) {
		this.damageResistFrost = damageResistFrost;
	}

	public final int getDamageResistMystic() {
		return damageResistMystic;
	}

	public final void setDamageResistMystic(int damageResistMystic) {
		this.damageResistMystic = damageResistMystic;
	}

	public final int getDamageResistDeath() {
		return damageResistDeath;
	}

	public final void setDamageResistDeath(int damageResistDeath) {
		this.damageResistDeath = damageResistDeath;
	}

	public final Profession getProfession() {
		return profession;
	}

	public final void setProfession(Profession profession) {
		this.profession = profession;
	}

	public String toString() {
		return displayName == null ? "<Id " + getEntityId() + ">" : displayName;
	}

	public String getIcon() {
		CreatureCategory cat = getCreatureCategory();
		if (cat == null) {
			cat = CreatureCategory.INANIMATE;
		}
		return cat.getIcon();
	}
}