package org.icenet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Item;
import org.icelib.ItemAppearance;

public class GameItem extends Item {
	final static Logger LOG = Logger.getLogger(GameItem.class.getName());

	public GameItem() {
		super();
	}

	/**
	 * Construct item from network message (game mode).
	 */
	public GameItem(ItemQueryReplyMessage irm) throws IOException {
		setEntityId(irm.getId());
		displayName = irm.getName();
		icon1 = irm.getIcon1();
		icon2 = irm.getIcon2();
		try {
			appearance = new ItemAppearance(irm.getAppearance());
		} catch (Exception e) {
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.WARNING, String.format("Could not parse item appearance '%s' for item %s (%d)", irm.getAppearance(),
						displayName, irm.getId()), e);
			else
				LOG.log(Level.WARNING, String.format("Could not parse item appearance '%s' for item %s (%d)", irm.getAppearance(),
						displayName, irm.getId()));
			appearance = new ItemAppearance();
		}
		type = irm.getType();
		containerSlots = irm.getContainerSlots();
		level = irm.getLevel();
		setEntityId(irm.getId());
		bindingType = irm.getBindingType();
		equipType = irm.getEquipType();
		quality = Quality.fromCode(irm.getQualityLevel());
		minUseLevel = irm.getMinUseLevel();
		ownershipRestriction = irm.getOwnershipRestriction();
		armourType = irm.getArmorType();
		armourResistMelee = irm.getArmorResistMelee();
		armourResistFire = irm.getArmorResistFire();
		armourResistFrost = irm.getArmorResistFrost();
		armourResistMystic = irm.getArmorResistMystic();
		armourResistDeath = irm.getArmorResistDeath();
		weaponType = irm.getWeaponType();
		specialItemType = irm.getSpecialItemType() > 0 ? SpecialItemType.fromCode(irm.getSpecialItemType()) : null;
		value = irm.getValue();
		valueType = irm.getValueType();
		flavorText = irm.getFlavorText();
		subText = irm.getSv1();
		ivMax1 = irm.getIvMax1();
		ivType1 = irm.getIvType1();
		ivMax2 = irm.getIvMax2();
		ivType2 = irm.getIvType2();
		resultItemId = irm.getResultItemId();
		keyComponentId = irm.getKeyComponentItemId();
		craftItemIds.addAll(irm.getCreateItemDefId());
		weaponDamageMin = irm.getWeaponDamageMin();
		weaponDamageMax = irm.getWeaponDamageMax();
		bonusDexterity = irm.getBonusDexterity();
		bonusStrength = irm.getBonusStrength();
		bonusPsyche = irm.getBonusPsyche();
		bonusSpirit = irm.getBonusSpirit();
		bonusConstitution = irm.getBonusConstitution();
		useAbilityId = irm.getUseAbilityId();
		// // Charm
		healingMod = irm.getHealingMod();
		meleeHitMod = irm.getMeleeHitMod();
		meleeCritMod = irm.getMeleeCritMod();
		regenHealthMod = irm.getRegenHealthMod();
		castSpeedMod = irm.getCastSpeedMod();
		attackSpeedMod = irm.getAttackSpeedMod();
		blockMod = irm.getBlockMod();
		parryMod = irm.getParryMod();
		runSpeedMod = irm.getRunSpeedMod();
		magicHitMod = irm.getMagicHitMod();
		magicCritMod = irm.getMagicCritMod();
		// private List<RGB> itemColours;
		// private List<RGB> roughItemColours;
		// private String normalisedName;
	}
}
