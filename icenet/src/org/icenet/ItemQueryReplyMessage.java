package org.icenet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.icelib.EquipType;
import org.icelib.Item.ArmourType;
import org.icelib.Item.BindingType;
import org.icelib.Item.WeaponType;
import org.icelib.ItemType;

/**
 * Item query reply (game mode)
 */
public class ItemQueryReplyMessage extends SimulatorMessage {

//    0000   47 ++ 00 de ++ 00 00 08 3b ++ 03 0b 54 77 69 6c 6c 20 54  G.....;..Twill T
//    0010   75 6e 69 63 3c 5b 7b 5b 22 63 22 5d 3d 7b 5b 22  unic<[{["c"]={["
//    0020   74 79 70 65 22 5d 3d 22 41 72 6d 6f 72 2d 43 43  type"]="Armor-CC
//    0030   2d 43 6c 6f 74 68 69 6e 67 31 22 2c 5b 22 63 6f  -Clothing1",["co
//    0040   6c 6f 72 73 22 5d 3d 5b 5d 7d 7d 2c 6e 75 6c 6c  lors"]=[]}},null
//    0050   5d 2f 49 63 6f 6e 2d 33 32 2d 4d 5f 41 72 6d 6f  ]/Icon-32-M_Armo
//    0060   72 2d 43 68 65 73 74 30 34 2e 70 6e 67 7c 49 63  r-Chest04.png|Ic
//    0070   6f 6e 2d 33 32 2d 42 47 2d 42 6c 75 65 2e 70 6e  on-32-BG-Blue.pn
//    0080   67 00 00 00 00 00 00 00 00 00 00 00 01 00 0b 00  g...............
//    0090   00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00  ................
//    00a0   02 00 00 00 02 00 00 00 02 00 00 00 02 00 00 00  ................
//    00b0   02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
//    00c0   00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00  ................
//    00d0   00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00  ................
//    00e0   01                                               .
    private static final Logger LOG = Logger.getLogger(ItemQueryReplyMessage.class.getName());
    private final long id;
    private final String icon1;
    private final String icon2;
    private final ItemType type;
    private final String appearance;
    private final String name;
	private short ivType1;
	private int ivMax1;
	private short ivType2;
	private int ivMax2;
	private String sv1;
	private long copper;
	private int containerSlots;
	private short autoTitleType;
	private int level;
	private BindingType bindingType;
	private EquipType equipType;
	private WeaponType weaponType;
	private long bonusStrength;
	private long bonusDexterity;
	private long bonusPsyche;
	private long bonusConstitution;
	private long bonusSpirit;
	private long bonusHealth;
	private long bonusWill;
	private long bonusSpirt;
	private long armorResistDeath;
	private long armorResistMystic;
	private long armorResistFrost;
	private long armorResistFire;
	private long armorResistMelee;
	private boolean charm;
	private float meleeHitMod;
	private float meleeCritMod;
	private float magicHitMod;
	private float magicCritMod;
	private float parryMod;
	private float blockMod;
	private float runSpeedMod;
	private float regenHealthMod;
	private float attackSpeedMod;
	private float castSpeedMod;
	private float healingMod;
	private short valueType;
	private long value;
	private long resultItemId;
	private long keyComponentItemId;
	private long numberOfItems;
	private List<Long> createItemDefId = new ArrayList<>();
	private String flavorText;
	private short specialItemType;
	private short ownershipRestriction;
	private short qualityLevel;
	private int minUseLevel;
	private long weaponDamageMin;
	private long weaponDamageMax;
	private short weaponExtraDamageRating;
	private short weaponExtraDamageType;
	private short speed;
	private long equipEffectId;
	private long useAbilityId;
	private long actionAbilityId;
	private ArmourType armorType;

    public ItemQueryReplyMessage(Simulator.ProtocolState mode, Simulator sim, SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(mode);
        payload.rewind();
        id = readUnsignedInt();
        type = ItemType.fromCode(readUnsignedByte());

        // ???
        name = readString();
        appearance = readString();
        String icons = readString();
        String[] iconArr = icons.split("\\|");
        icon1 = iconArr.length > 0 ? iconArr[0] : null;
        icon2 = iconArr.length > 1 ? iconArr[1] : null;

        ivType1 = readUnsignedByte();
        ivMax1 = readUnsignedShort();
        ivType2 = readUnsignedByte();
        ivMax2 = readUnsignedShort();
        
        sv1 = readString(); 
        if(sim.getProtocolVersion() < 5) {
        	copper = readUnsignedInt();
        }
        
        containerSlots = readUnsignedShort();
        autoTitleType = readUnsignedByte();
        level = readUnsignedShort();
        bindingType = BindingType.fromCode(readUnsignedByte());
        equipType = EquipType.fromCode(readUnsignedByte());
        
        weaponType = WeaponType.fromCode(readUnsignedByte());
        if(weaponType != WeaponType.UNKNOWN) {
        	if(sim.getProtocolVersion() == 7) {
        		weaponDamageMin = readUnsignedByte();
        		weaponDamageMax = readUnsignedByte();
        		speed = readUnsignedByte();
        		weaponExtraDamageRating = readUnsignedByte();
        		weaponExtraDamageType = readUnsignedByte();
        	}
        	else {
        		weaponDamageMin = readUnsignedInt();
        		weaponDamageMax = readUnsignedInt();
        		weaponExtraDamageRating = readUnsignedByte();
        		weaponExtraDamageType = readUnsignedByte();
        		
        	}
        }
        
        equipEffectId = readUnsignedInt();
        useAbilityId = readUnsignedInt();
        actionAbilityId = readUnsignedInt();
        armorType = ArmourType.fromCode(readUnsignedByte());
        

        if(armorType != ArmourType.UNKNOWN) {
        	if(sim.getProtocolVersion() == 7) {
        		armorResistMelee = readUnsignedByte();
        		armorResistFire = readUnsignedByte();
        		armorResistFrost = readUnsignedByte();
        		armorResistMystic = readUnsignedByte();
        		armorResistDeath = readUnsignedByte();
        	}
        	else {
        		armorResistMelee = readUnsignedInt();
        		armorResistFire = readUnsignedInt();
        		armorResistFrost = readUnsignedInt();
        		armorResistMystic = readUnsignedInt();
        		armorResistDeath = readUnsignedInt();
        	}
        }
        
        if(sim.getProtocolVersion() == 7) {
        	bonusStrength = readUnsignedByte();
        	bonusDexterity= readUnsignedByte();
        	bonusConstitution  = readUnsignedByte();
        	bonusPsyche = readUnsignedByte();
        	bonusSpirt = readUnsignedByte();
        	bonusHealth = readUnsignedByte();
        	bonusWill = readUnsignedByte();
        }
        else {
        	bonusStrength = readUnsignedInt();
        	bonusDexterity= readUnsignedInt();
        	bonusConstitution  = readUnsignedInt();
        	bonusPsyche = readUnsignedInt();
        	bonusSpirit = readUnsignedInt();
        	if(sim.getProtocolVersion() < 32) {
            	bonusHealth = readUnsignedInt();
        	}
        	bonusWill = readUnsignedInt();
        }
        
        if(sim.getProtocolVersion() >= 4) {
        	charm = readBoolean();
        	if(charm) {
        		meleeHitMod = readFloat();
        		meleeCritMod = readFloat();
        		magicHitMod = readFloat();
        		magicCritMod = readFloat();
        		parryMod = readFloat();
        		blockMod = readFloat();
        		runSpeedMod = readFloat();
        		regenHealthMod = readFloat();
        		attackSpeedMod = readFloat();
        		castSpeedMod = readFloat();
        		healingMod = readFloat();
        	}
        }
        
        if(sim.getProtocolVersion() >= 5) {
        	value = readUnsignedInt();
        	valueType = readUnsignedByte();
        }
        
        if(sim.getProtocolVersion() >= 7) {
        	resultItemId = readUnsignedInt();
        	keyComponentItemId = readUnsignedInt();
        	numberOfItems = readUnsignedInt();
        	for(int i = 0 ; i < numberOfItems; i++) {
        		createItemDefId.add(readUnsignedInt());
        	}
        }

        if(sim.getProtocolVersion() >= 9) {
        	flavorText = readString();
        }

        if(sim.getProtocolVersion() >= 18) {
        	specialItemType = readUnsignedByte();
        }

        if(sim.getProtocolVersion() >= 30) {
        	ownershipRestriction = readUnsignedByte();
        }

        if(sim.getProtocolVersion() >= 31) {
        	qualityLevel = readUnsignedByte();
        	minUseLevel = readUnsignedShort();
        }
    }

    public long getWeaponDamageMin() {
		return weaponDamageMin;
	}

	public long getWeaponDamageMax() {
		return weaponDamageMax;
	}

	public short getWeaponExtraDamageRating() {
		return weaponExtraDamageRating;
	}

	public short getWeaponExtraDamageType() {
		return weaponExtraDamageType;
	}

	public short getSpeed() {
		return speed;
	}

	public long getEquipEffectId() {
		return equipEffectId;
	}

	public long getUseAbilityId() {
		return useAbilityId;
	}

	public long getActionAbilityId() {
		return actionAbilityId;
	}

	public ArmourType getArmorType() {
		return armorType;
	}

	public short getIvType1() {
		return ivType1;
	}

	public int getIvMax1() {
		return ivMax1;
	}

	public short getIvType2() {
		return ivType2;
	}

	public int getIvMax2() {
		return ivMax2;
	}

	public String getSv1() {
		return sv1;
	}

	public long getCopper() {
		return copper;
	}

	public int getContainerSlots() {
		return containerSlots;
	}

	public short getAutoTitleType() {
		return autoTitleType;
	}

	public int getLevel() {
		return level;
	}

	public BindingType getBindingType() {
		return bindingType;
	}

	public EquipType getEquipType() {
		return equipType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public long getBonusStrength() {
		return bonusStrength;
	}

	public long getBonusDexterity() {
		return bonusDexterity;
	}

	public long getBonusPsyche() {
		return bonusPsyche;
	}

	public long getBonusConstitution() {
		return bonusConstitution;
	}

	public long getBonusSpirit() {
		return bonusSpirit;
	}

	public long getBonusHealth() {
		return bonusHealth;
	}

	public long getBonusWill() {
		return bonusWill;
	}

	public long getBonusSpirt() {
		return bonusSpirt;
	}

	public long getArmorResistDeath() {
		return armorResistDeath;
	}

	public long getArmorResistMystic() {
		return armorResistMystic;
	}

	public long getArmorResistFrost() {
		return armorResistFrost;
	}

	public long getArmorResistFire() {
		return armorResistFire;
	}

	public long getArmorResistMelee() {
		return armorResistMelee;
	}

	public boolean isCharm() {
		return charm;
	}

	public float getMeleeHitMod() {
		return meleeHitMod;
	}

	public float getMeleeCritMod() {
		return meleeCritMod;
	}

	public float getMagicHitMod() {
		return magicHitMod;
	}

	public float getMagicCritMod() {
		return magicCritMod;
	}

	public float getParryMod() {
		return parryMod;
	}

	public float getBlockMod() {
		return blockMod;
	}

	public float getRunSpeedMod() {
		return runSpeedMod;
	}

	public float getRegenHealthMod() {
		return regenHealthMod;
	}

	public float getAttackSpeedMod() {
		return attackSpeedMod;
	}

	public float getCastSpeedMod() {
		return castSpeedMod;
	}

	public float getHealingMod() {
		return healingMod;
	}

	public short getValueType() {
		return valueType;
	}

	public long getValue() {
		return value;
	}

	public long getResultItemId() {
		return resultItemId;
	}

	public long getKeyComponentItemId() {
		return keyComponentItemId;
	}

	public long getNumberOfItems() {
		return numberOfItems;
	}

	public List<Long> getCreateItemDefId() {
		return createItemDefId;
	}

	public String getFlavorText() {
		return flavorText;
	}

	public short getSpecialItemType() {
		return specialItemType;
	}

	public short getOwnershipRestriction() {
		return ownershipRestriction;
	}

	public short getQualityLevel() {
		return qualityLevel;
	}

	public int getMinUseLevel() {
		return minUseLevel;
	}

	public ItemType getType() {
        return type;
    }

    public String getAppearance() {
        return appearance;
    }

    public String getName() {
        return name;
    }

    public String getIcon1() {
        return icon1;
    }

    public String getIcon2() {
        return icon2;
    }

    public long getId() {
        return id;
    }

	@Override
	public String toString() {
		return "ItemQueryReplyMessage [id=" + id + ", icon1=" + icon1 + ", icon2=" + icon2 + ", type=" + type + ", appearance="
				+ appearance + ", name=" + name + ", ivType1=" + ivType1 + ", ivMax1=" + ivMax1 + ", ivType2=" + ivType2
				+ ", ivMax2=" + ivMax2 + ", sv1=" + sv1 + ", copper=" + copper + ", containerSlots=" + containerSlots
				+ ", autoTitleType=" + autoTitleType + ", level=" + level + ", bindingType=" + bindingType + ", equipType="
				+ equipType + ", weaponType=" + weaponType + ", bonusStrength=" + bonusStrength + ", bonusDexterity="
				+ bonusDexterity + ", bonusPsyche=" + bonusPsyche + ", bonusConstitution=" + bonusConstitution + ", bonusSpirit="
				+ bonusSpirit + ", bonusHealth=" + bonusHealth + ", bonusWill=" + bonusWill + ", bonusSpirt=" + bonusSpirt
				+ ", armorResistDeath=" + armorResistDeath + ", armorResistMystic=" + armorResistMystic + ", armorResistFrost="
				+ armorResistFrost + ", armorResistFire=" + armorResistFire + ", armorResistMelee=" + armorResistMelee + ", charm="
				+ charm + ", meleeHitMod=" + meleeHitMod + ", meleeCritMod=" + meleeCritMod + ", magicHitMod=" + magicHitMod
				+ ", magicCritMod=" + magicCritMod + ", parryMod=" + parryMod + ", blockMod=" + blockMod + ", runSpeedMod="
				+ runSpeedMod + ", regenHealthMod=" + regenHealthMod + ", attackSpeedMod=" + attackSpeedMod + ", castSpeedMod="
				+ castSpeedMod + ", healingMod=" + healingMod + ", valueType=" + valueType + ", value=" + value + ", resultItemId="
				+ resultItemId + ", keyComponentItemId=" + keyComponentItemId + ", numberOfItems=" + numberOfItems
				+ ", createItemDefId=" + createItemDefId + ", flavorText=" + flavorText + ", specialItemType=" + specialItemType
				+ ", ownershipRestriction=" + ownershipRestriction + ", qualityLevel=" + qualityLevel + ", minUseLevel="
				+ minUseLevel + ", weaponDamageMin=" + weaponDamageMin + ", weaponDamageMax=" + weaponDamageMax
				+ ", weaponExtraDamageRating=" + weaponExtraDamageRating + ", weaponExtraDamageType=" + weaponExtraDamageType
				+ ", speed=" + speed + ", equipEffectId=" + equipEffectId + ", useAbilityId=" + useAbilityId + ", actionAbilityId="
				+ actionAbilityId + ", armorType=" + armorType + "]";
	}

}
