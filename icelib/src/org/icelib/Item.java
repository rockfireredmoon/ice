/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Item extends AbstractGameObject<Long> {

	private final static Logger LOG = Logger.getLogger(Item.class.getName());
	// public final static int COLOR_DIVISOR = 32;
	public final static float COLOR_DIVISOR = 10.0f;

	public boolean hasAnyBonuses() {
		return bonusConstitution > 0 || bonusDexterity > 0 || bonusStrength > 0 || bonusPsyche > 0 || bonusSpirit > 0
				|| bonusConstitution > 0;
	}

	public enum WeaponType {

		UNKNOWN(0), SMALL(1), ONE_HAND(2), TWO_HAND(3), POLE(4), WAND(5), BOW(6), THROW(7), ARCANE_TOTEM(8);
		private int code;

		private WeaponType(int code) {
			this.code = code;
		}

		public static WeaponType fromCode(int code) {
			for (WeaponType type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			LOG.info("TODO: Unhandle WeaponType code " + code);
			return null;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}

		public int getCode() {
			return code;
		}
	}

	public enum SpecialItemType {

		REAGENT_GENERATOR(1), ITEM_GRINDER(2), XP_TOME(3), SPEED_TOME(4);
		private int code;

		private SpecialItemType(int code) {
			this.code = code;
		}

		public static SpecialItemType fromCode(int code) {
			for (SpecialItemType type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			LOG.info("TODO: Unhandle SpecialItemType code " + code);
			return null;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	public enum BindingType {

		NORMAL(0), PICKUP(1), EQUIP(2);
		private int code;

		private BindingType(int code) {
			this.code = code;
		}

		public static BindingType fromCode(int code) {
			for (BindingType type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			LOG.info("TODO: Unhandle BindingType code " + code);
			return null;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	public enum Quality {

		COMMON(1, new Color(0x20, 0x20, 0x20)), UNCOMMON(2, new Color(0, 212, 0)), RARE(3, new Color(0, 0x66, 0xff)), EPIC(4,
				new Color(0x72, 0x23, 0x98)), LEGENDARY(5, new Color(0xff, 0xe4, 0x00)), ARTIFACT(6, new Color(0xff, 0x44, 0x11));
		private int code;
		private RGB color;

		private Quality(int code) {
			this(code, Color.WHITE);
		}

		private Quality(int code, RGB color) {
			this.code = code;
			this.color = color;
		}

		public RGB getColor() {
			return color;
		}

		public int getCode() {
			return code;
		}

		public static Quality fromCode(int code) {
			for (Quality type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			LOG.info("TODO: Unhandle Quality code " + code);
			return null;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	public enum ArmourType {

		UNKNOWN(0), CLOTH(1), LIGHT(2), MEDIUM(3), HEAVY(4), SHIELD(5);
		private int code;

		private ArmourType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static ArmourType fromCode(int code) {
			for (ArmourType type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			LOG.info("TODO: Unhandle ArmourType code " + code);
			return null;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	protected String displayName;
	protected ItemType type = ItemType.UNKNOWN_0;
	protected String icon1;
	protected String icon2;
	protected ItemAppearance appearance;
	protected int containerSlots;
	protected int level;
	protected BindingType bindingType;
	protected EquipType equipType;
	protected Quality quality;
	protected int minUseLevel;
	protected short ownershipRestriction;
	protected ArmourType armourType;
	protected long armourResistMelee;
	protected long armourResistFire;
	protected long armourResistFrost;
	protected long armourResistMystic;
	protected long armourResistDeath;
	protected WeaponType weaponType;
	protected SpecialItemType specialItemType;
	// Value?
	protected long value;
	protected int valueType;
	// Not sure -
	protected String subText;
	protected String flavorText;
	protected int ivMax1;
	protected int ivType1;
	protected int ivMax2;
	protected int ivType2;
	// Weapon plans
	protected long resultItemId;
	protected long keyComponentId;
	protected List<Long> craftItemIds = new ArrayList<Long>();
	// Damage
	protected long weaponDamageMin;
	protected long weaponDamageMax;
	// Bonuses
	protected long bonusDexterity;
	protected long bonusStrength;
	protected long bonusPsyche;
	protected long bonusSpirit;
	protected long bonusConstitution;
	// Potion, scrolls etc
	protected long useAbilityId;
	// Charm
	protected float healingMod;
	protected float meleeHitMod;
	protected float meleeCritMod;
	protected float regenHealthMod;
	protected float castSpeedMod;
	protected float attackSpeedMod;
	protected float blockMod;
	protected float parryMod;
	protected float runSpeedMod;
	protected float magicHitMod;
	protected float magicCritMod;

	// TODO Not used in game - get rid of?
	protected List<RGB> itemColours;
	protected List<RGB> roughItemColours;
	protected String normalisedName;

	public Item() {
		super();
	}

	public void set(String name, String value, String section) {
		if (name.equals("mID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("mType")) {
			type = ItemType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mDisplayName")) {
			displayName = value;
		} else if (name.equals("mContainerSlots")) {
			containerSlots = Integer.parseInt(value);
		} else if (name.equals("mIcon")) {
			String[] icons = value.split("\\|");
			icon1 = icons[0];
			if (icons.length > 1) {
				icon2 = icons[1];
			}
		} else if (name.equals("mBindingType")) {
			bindingType = BindingType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mEquipType")) {
			equipType = EquipType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mFlavorText")) {
			flavorText = value;
		} else if (name.equals("mValue")) {
			this.value = Long.parseLong(value);
		} else if (name.equals("mValueType")) {
			this.valueType = Integer.parseInt(value);
		} else if (name.equals("mLevel")) {
			level = Integer.parseInt(value);
		} else if (name.equals("mMinUseLevel")) {
			minUseLevel = Integer.parseInt(value);
		} else if (name.equals("mQualityLevel")) {
			quality = Quality.fromCode(Integer.parseInt(value));
		} else if (name.equals("mOwnershipRestriction")) {
			ownershipRestriction = Byte.valueOf(value);
		} else if (name.equals("mArmorType")) {
			armourType = ArmourType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mSpecialItemType")) {
			specialItemType = SpecialItemType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mWeaponType")) {
			weaponType = WeaponType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mAppearance")) {
			try {
				appearance = new ItemAppearance(value);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to parse appearance. ", e);
			}
		} else if (name.equals("mArmorResistMelee")) {
			armourResistMelee = Integer.parseInt(value);
		} else if (name.equals("mArmorResistFire")) {
			armourResistFire = Integer.parseInt(value);
		} else if (name.equals("mArmorResistFrost")) {
			armourResistFrost = Integer.parseInt(value);
		} else if (name.equals("mArmorResistMystic")) {
			armourResistMystic = Integer.parseInt(value);
		} else if (name.equals("mArmorResistDeath")) {
			armourResistDeath = Integer.parseInt(value);
		} else if (name.equals("mBonusConstitution")) {
			bonusConstitution = Integer.parseInt(value);
		} else if (name.equals("mBonusSpirit")) {
			bonusSpirit = Integer.parseInt(value);
		} else if (name.equals("mBonusDexterity")) {
			bonusDexterity = Integer.parseInt(value);
		} else if (name.equals("mBonusPsyche")) {
			bonusPsyche = Integer.parseInt(value);
		} else if (name.equals("mBonusStrength")) {
			bonusStrength = Integer.parseInt(value);
		} else if (name.equals("resultItemId")) {
			resultItemId = Long.parseLong(value);
		} else if (name.equals("keyComponentId")) {
			keyComponentId = Long.parseLong(value);
		} else if (name.equals("craftItemDefId")) {
			craftItemIds.add(Long.parseLong(value));
		} else if (name.equals("mWeaponDamageMin")) {
			weaponDamageMin = Integer.parseInt(value);
		} else if (name.equals("mWeaponDamageMax")) {
			weaponDamageMax = Integer.parseInt(value);
		} else if (name.equals("mIvType1")) {
			ivType1 = Integer.parseInt(value);
		} else if (name.equals("mIvMax1")) {
			ivMax1 = Integer.parseInt(value);
		} else if (name.equals("mIvType2")) {
			ivType2 = Integer.parseInt(value);
		} else if (name.equals("mIvMax2")) {
			ivMax2 = Integer.parseInt(value);
		} else if (name.equals("mSv1")) {
			subText = value;
		} else if (name.equals("isCharm")) {
			// Rely on type ????
		} else if (name.equals("numberOfItems")) {
			// Already known ????
		} else if (name.equals("mUseAbilityId")) {
			useAbilityId = Long.parseLong(value);

		} else if (name.equals("mHealingMod")) {
			healingMod = Integer.parseInt(value);
		} else if (name.equals("mBlockMod")) {
			blockMod = Integer.parseInt(value);
		} else if (name.equals("mMeleeHitMod")) {
			meleeHitMod = Integer.parseInt(value);
		} else if (name.equals("mMeleeCritMod")) {
			meleeCritMod = Integer.parseInt(value);
		} else if (name.equals("mRegenHealthMod")) {
			regenHealthMod = Integer.parseInt(value);
		} else if (name.equals("mCastSpeedMod")) {
			castSpeedMod = Integer.parseInt(value);
		} else if (name.equals("mAttackSpeedMod")) {
			attackSpeedMod = Integer.parseInt(value);
		} else if (name.equals("mParryMod")) {
			parryMod = Integer.parseInt(value);
		} else if (name.equals("mRunSpeedMod")) {
			runSpeedMod = Integer.parseInt(value);
		} else if (name.equals("mMagicHitMod")) {
			magicHitMod = Integer.parseInt(value);
		} else if (name.equals("mMagicCritMod")) {
			magicCritMod = Integer.parseInt(value);
		} else if (!name.equals("")) {
			LOG.info("TODO: Unhandled property " + name + " = " + value);
		}
	}

	public final float getHealingMod() {
		return healingMod;
	}

	public final void setHealingMod(float healingMod) {
		this.healingMod = healingMod;
	}

	public final float getMeleeHitMod() {
		return meleeHitMod;
	}

	public final void setMeleeHitMod(float meleeHitMod) {
		this.meleeHitMod = meleeHitMod;
	}

	public final float getMeleeCritMod() {
		return meleeCritMod;
	}

	public final void setMeleeCritMod(float meleeCritMod) {
		this.meleeCritMod = meleeCritMod;
	}

	public final float getRegenHealthMod() {
		return regenHealthMod;
	}

	public final void setRegenHealthMod(float regenHealthMod) {
		this.regenHealthMod = regenHealthMod;
	}

	public final float getCastSpeedMod() {
		return castSpeedMod;
	}

	public final void setCastSpeedMod(float castSpeedMod) {
		this.castSpeedMod = castSpeedMod;
	}

	public final float getAttackSpeedMod() {
		return attackSpeedMod;
	}

	public final void setAttackSpeedMod(float attackSpeedMod) {
		this.attackSpeedMod = attackSpeedMod;
	}

	public final float getBlockMod() {
		return blockMod;
	}

	public final void setBlockMod(float blockMod) {
		this.blockMod = blockMod;
	}

	public final float getParryMod() {
		return parryMod;
	}

	public final void setParryMod(float parryMod) {
		this.parryMod = parryMod;
	}

	public final float getRunSpeedMod() {
		return runSpeedMod;
	}

	public final void setRunSpeedMod(float runSpeedMod) {
		this.runSpeedMod = runSpeedMod;
	}

	public final float getMagicHitMod() {
		return magicHitMod;
	}

	public final void setMagicHitMod(float magicHitMod) {
		this.magicHitMod = magicHitMod;
	}

	public final float getMagicCritMod() {
		return magicCritMod;
	}

	public final void setMagicCritMod(float magicCritMod) {
		this.magicCritMod = magicCritMod;
	}

	public final SpecialItemType getSpecialItemType() {
		return specialItemType;
	}

	public final void setSpecialItemType(SpecialItemType specialItemType) {
		this.specialItemType = specialItemType;
	}

	public final boolean isCharm() {
		return ItemType.CHARM.equals(type);
	}

	public final int getIvMax2() {
		return ivMax2;
	}

	public final void setIvMax2(int ivMax2) {
		this.ivMax2 = ivMax2;
	}

	public final int getIvType2() {
		return ivType2;
	}

	public final void setIvType2(int ivType2) {
		this.ivType2 = ivType2;
	}

	public final long getUseAbilityId() {
		return useAbilityId;
	}

	public final void setUseAbilityId(long useAbilityId) {
		this.useAbilityId = useAbilityId;
	}

	public final WeaponType getWeaponType() {
		return weaponType;
	}

	public final void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public final int getValueType() {
		return valueType;
	}

	public final void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public final String getSubText() {
		return subText;
	}

	public final void setSubText(String sv1) {
		this.subText = sv1;
	}

	public final int getIvMax1() {
		return ivMax1;
	}

	public final void setIvMax1(int ivMax1) {
		this.ivMax1 = ivMax1;
	}

	public final int getIvType1() {
		return ivType1;
	}

	public final void setIvType1(int ivType1) {
		this.ivType1 = ivType1;
	}

	public final long getWeaponDamageMin() {
		return weaponDamageMin;
	}

	public final void setWeaponDamageMin(int weaponDamageMin) {
		this.weaponDamageMin = weaponDamageMin;
	}

	public final long getWeaponDamageMax() {
		return weaponDamageMax;
	}

	public final void setWeaponDamageMax(int weaponDamageMax) {
		this.weaponDamageMax = weaponDamageMax;
	}

	public final long getBonusPsyche() {
		return bonusPsyche;
	}

	public final void setBonusPsyche(int bonusPsyche) {
		this.bonusPsyche = bonusPsyche;
	}

	public final long getResultItemId() {
		return resultItemId;
	}

	public final void setResultItemId(long resultItemId) {
		this.resultItemId = resultItemId;
	}

	public final long getKeyComponentId() {
		return keyComponentId;
	}

	public final void setKeyComponentId(long keyComponentId) {
		this.keyComponentId = keyComponentId;
	}

	public final int getNumberOfItems() {
		return craftItemIds.size();
	}

	public final void setNumberOfItems(int numberOfItems) {
		throw new UnsupportedOperationException("Cannot set number of items, change the craft items Ids list");
	}

	public final List<Long> getCraftItemIds() {
		return craftItemIds;
	}

	public final void setCraftItemIds(List<Long> craftItemIds) {
		this.craftItemIds = craftItemIds;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public final String getIcon1() {
		return icon1;
	}

	public final void setIcon1(String icon1) {
		this.icon1 = icon1;
	}

	public final String getIcon2() {
		return icon2;
	}

	public final void setIcon2(String icon2) {
		this.icon2 = icon2;
	}

	public int getContainerSlots() {
		return containerSlots;
	}

	public void setContainerSlots(int containerSlots) {
		this.containerSlots = containerSlots;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public BindingType getBindingType() {
		return bindingType;
	}

	public void setBindingType(BindingType bindingType) {
		this.bindingType = bindingType;
	}

	public EquipType getEquipType() {
		return equipType;
	}

	public void setEquipType(EquipType equipType) {
		this.equipType = equipType;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public Quality getQuality() {
		return quality;
	}

	public void setQuality(Quality quality) {
		this.quality = quality;
	}

	public int getMinUseLevel() {
		return minUseLevel;
	}

	public void setMinUseLevel(int minUseLevel) {
		this.minUseLevel = minUseLevel;
	}

	public String getFlavorText() {
		return flavorText;
	}

	public void setFlavorText(String flavorText) {
		this.flavorText = flavorText;
	}

	public short getOwnershipRestriction() {
		return ownershipRestriction;
	}

	public void setOwnershipRestriction(short ownershipRestriction) {
		this.ownershipRestriction = ownershipRestriction;
	}

	public ArmourType getArmourType() {
		return armourType;
	}

	public void setArmourType(ArmourType armourType) {
		this.armourType = armourType;
	}

	public long getArmourResistMelee() {
		return armourResistMelee;
	}

	public void setArmourResistMelee(long armourResistMelee) {
		this.armourResistMelee = armourResistMelee;
	}

	public long getArmourResistFire() {
		return armourResistFire;
	}

	public void setArmourResistFire(long armourResistFire) {
		this.armourResistFire = armourResistFire;
	}

	public long getArmourResistFrost() {
		return armourResistFrost;
	}

	public void setArmourResistFrost(long armourResistFrost) {
		this.armourResistFrost = armourResistFrost;
	}

	public long getArmourResistMystic() {
		return armourResistMystic;
	}

	public void setArmourResistMystic(long armourResistMystic) {
		this.armourResistMystic = armourResistMystic;
	}

	public long getArmourResistDeath() {
		return armourResistDeath;
	}

	public void setArmourResistDeath(long armourResistDeath) {
		this.armourResistDeath = armourResistDeath;
	}

	public long getBonusSpirit() {
		return bonusSpirit;
	}

	public void setBonusSpirit(long bonusSpirit) {
		this.bonusSpirit = bonusSpirit;
	}

	public long getBonusConstitution() {
		return bonusConstitution;
	}

	public void setBonusConstitution(long bonusConstitution) {
		this.bonusConstitution = bonusConstitution;
	}

	public long getBonusDexterity() {
		return bonusDexterity;
	}

	public void setBonusDexterity(long bonusDexterity) {
		this.bonusDexterity = bonusDexterity;
	}

	public long getBonusStrength() {
		return bonusStrength;
	}

	public void setBonusStrength(int bonusStrength) {
		this.bonusStrength = bonusStrength;
	}

	public ItemAppearance getAppearance() {
		return appearance;
	}

	public void setAppearance(ItemAppearance appearance) {
		this.appearance = appearance;
	}

	@Override
	public String toString() {
		return Icelib.nonNull(displayName);
	}

	public boolean isAssetUsed(String asset) {
		if (appearance != null) {
			if (asset.equalsIgnoreCase(appearance.getClothingType())) {
				return true;
			}
			for (AttachmentItem a : appearance.getAttachments()) {
				if (asset.equalsIgnoreCase(a.getKey().getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public synchronized List<RGB> getItemColours(boolean rough) {
		if ((rough && roughItemColours == null) || (!rough && itemColours == null)) {
			ItemAppearance app = getAppearance();
			List<RGB> c = new ArrayList<RGB>();
			if (app != null) {
				for (AttachmentItem a : app.getAttachments()) {
					for (RGB x : a.getColors()) {
						if (rough) {
							x = Color.roughenHSV(x, COLOR_DIVISOR);
						}
						if (!c.contains(x)) {
							c.add(x);
						}
					}
				}

				if (app.getClothingColor() != null) {
					for (RGB x : app.getClothingColor()) {
						if (rough) {
							x = Color.roughenHSV(x, COLOR_DIVISOR);
						}
						if (!c.contains(x)) {
							c.add(x);
						}
					}
				}
			}
			Collections.sort(c);
			if (rough) {
				roughItemColours = c;
			} else {
				itemColours = c;
			}
		}
		return rough ? roughItemColours : itemColours;
	}
}
