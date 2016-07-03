/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

public enum StatType {
	/**
	 * 
	 */
	APPEARANCE(0, String.class),
	/**
	* 
	*/
	EQ_APPEARANCE(1, String.class),
	/**
	* 
	*/
	LEVEL(2, Short.class),

	/**
	 * 
	 */
	DISPLAY_NAME(3, String.class),
	/**
	* 
	*/
	STRENGTH(4, Short.class),
	/**
	* 
	*/
	DEXTERITY(5, Short.class),
	/**
	* 
	*/
	CONSTITUTION(6, Short.class),
	/**
	* 
	*/
	PSYCHE(7, Short.class),
	/**
	* 
	*/
	SPIRIT(8, Short.class),
	/**
	* 
	*/
	ARMOR_RATING(30, Short.class),
	/**
	* 
	*/
	MELEE_ATTACK_SPEED(31, Short.class),
	/**
	* 
	*/
	MAGIC_ATTACK_SPEED(32, Short.class),
	/**
	* 
	*/
	BASE_DAMAGE_MELEE(33, Short.class),
	/**
	* 
	*/
	BASE_DAMAGE_FIRE(34, Short.class),
	/**
	* 
	*/
	BASE_DAMAGE_FROST(35, Short.class),
	/**
	* 
	*/
	BASE_DAMAGE_MYSTIC(36, Short.class),
	/**
	* 
	*/
	BASE_DAMAGE_DEATH(37, Short.class),
	/**
	* 
	*/
	DAMAGE_RESIST_MELEE(38, Short.class),

	/**
	* 
	*/
	DAMAGE_RESIST_FIRE(39, Short.class),
	/**
	* 
	*/
	DAMAGE_RESIST_FROST(40, Short.class),
	/**
	* 
	*/
	DAMAGE_RESIST_MYSTIC(41, Short.class),
	/**
	* 
	*/
	DAMAGE_RESIST_DEATH(42, Short.class),
	/**
	* 
	*/
	BASE_MOVEMENT(43, Short.class),
	/**
	* 
	*/
	BASE_LUCK(44, Short.class),
	/**
	* 
	*/
	BASE_HEALTH(45, Short.class),
	/**
	* 
	*/
	WILL_MAX(46, Short.class),
	/**
	* 
	*/
	WILL_REGEN(47, Float.class),
	/**
	* 
	*/
	MIGHT_MAX(48, Short.class),
	/**
	* 
	*/
	MIGHT_REGEN(49, Float.class),

	/**
	* 
	*/
	BASE_DOGE(50, Short.class),
	/**
	* 
	*/
	BASE_DEFLECT(51, Short.class),
	/**
	* 
	*/
	BASE_PARRY(52, Short.class),
	/**
	* 
	*/
	BASE_BLOCK(53, Short.class),
	/**
	* 
	*/
	BASE_MELEE_TO_HIT(54, Short.class),
	/**
	* 
	*/
	BASE_MELEE_CRITICAL(55, Short.class),
	/**
	* 
	*/
	BASE_MAGIC_SUCCESS(56, Short.class),
	/**
	* 
	*/
	BASE_MAGIC_CRITICAL(57, Short.class),
	/**
	* 
	*/
	OFFHAND_WEAPON_DAMAGE(58, Short.class),
	/**
	* 
	*/
	CASTING_SETBACK_CHANCE(59, Short.class),
	/**
	* 
	*/
	CHANNELING_BREAK_CHANCE(60, Short.class),
	/**
	* 
	*/
	BASE_HEALING(61, Short.class),
	/**
	* 
	*/
	AGGRO_RADIUS_MOD(62, Short.class),
	/**
	* 
	*/
	HATE_GAIN_RATE(63, Short.class),
	/**
	* 
	*/
	EXPERIENCE_GAIN_RATE(64, Short.class),
	/**
	* 
	*/
	COIN_GAIN_RATE(65, Short.class),
	/**
	* 
	*/
	MAGIC_LOOK_DROP_RATE(66, Short.class),
	/**
	* 
	*/
	INVENTORY_CAPACITY(67, Short.class),
	/**
	* 
	*/
	VIS_WEAPON(69, Short.class),

	/**
	* 
	*/
	SUB_NAME(70, String.class),
	/**
	* 
	*/
	ALTERNATIVE_IDLE_ANIM(71, String.class),
	/**
	* 
	*/
	SELECTIVE_EQ_OVERRIDE(72, String.class),

	/**
	* 
	*/
	HEALTH(80, "false".equals(System.getProperty("icenet.integerHealth")) ? Short.class : Integer.class),
	/**
	* 
	*/
	WILL(81, Short.class),
	/**
	* 
	*/
	WILL_CHARGES(82, Short.class),
	/**
	* 
	*/
	MIGHT(83, Short.class),
	/**
	* 
	*/
	MIGHT_CHARGES(84, Short.class),
	/**
	* 
	*/
	SSIZE(85, Short.class),
	/**
	* 
	*/
	PROFESSION(86, Short.class),

	/**
	* 
	*/
	WEAPON_DAMAGE_2H(87, Short.class),
	/**
	* 
	*/
	WEAPON_DAMAGE_1H(88, Short.class),
	/**
	* 
	*/
	WEAPON_DAMAGE_POLE(89, Short.class),
	/**
	* 
	*/
	WEAPON_DAMAGE_SMALL(90, Short.class),
	/**
	* 
	*/
	WEAPON_DAMAGE_BOW(91, Short.class),
	/**
	* 
	*/
	WEAPON_DAMAGE_THROWN(92, Short.class),
	/**
	* 
	*/
	WEAPON_DAMAGE_WAND(93, Short.class),
	/**
	* 
	*/
	EXTRA_DAMAGE_FIRE(94, Short.class), EXTRA_DAMAGE_FROST(95, Short.class),
	/**
	* 
	*/
	EXTRA_DAMAGE_MYSTIC(96, Short.class),
	/**
	* 
	*/
	EXTRA_DAMAGE_DEATH(97, Short.class),

	/**
	* 
	*/
	APPEARANCE_OVERRIDE(98, String.class),
	/**
	* 
	*/
	INVISIBILITY_DISTANCE(99, Float.class),
	/**
	* 
	*/
	TRANSLOCATE_DESTINATION(100, String.class),
	/**
	* 
	*/
	LOOTABLE_PLAYER_IDS(101, String.class),

	/**
	* 
	*/
	MASTER(102, Short.class),
	/**
	* 
	*/
	REZ_PENDING(103, Short.class),
	/**
	* 
	*/
	LOOT_SEEABLE_PLAYER_IDS(104, String.class),
	/**
	* 
	*/
	LOOT(105, String.class),
	/**
	* 
	*/
	BASE_STATS(107, String.class),
	/**
	* 
	*/
	HEALTH_MOD(108, Short.class),
	/**
	* 
	*/
	HEROISM(110, Short.class),
	/**
	* 
	*/
	EXPERIENCE(111, Integer.class),
	/**
	* 
	*/
	TOTAL_ABILITY_POINTS(112, Short.class),
	/**
	* 
	*/
	CURRENT_ABILITY_POINTS(113, Short.class),

	/**
	* 
	*/
	COPPER(114, Integer.class),
	/**
	* 
	*/
	CREDITS(115, Integer.class),

	/**
	* 
	*/
	AI_MODULE(120, String.class),
	/**
	* 
	*/
	CREATURE_CATEGORY(121, String.class),
	/**
	* 
	*/
	RARITY(122, Short.class),
	/**
	* 
	*/
	SPAWN_TABLE_POINTS(123, Short.class),
	/**
	* 
	*/
	AI_PACKAGE(124, String.class),
	/**
	* 
	*/
	BONUS_HEALTH(125, Short.class),
	/**
	* 
	*/
	DR_MOD_MELEE(126, Integer.class),
	/**
	* 
	*/
	DR_MOD_FIRE(127, Integer.class),
	/**
	* 
	*/
	DR_MOD_FROST(128, Integer.class),
	/**
	* 
	*/
	DR_MOD_MYSTIC(129, Integer.class),
	/**
	* 
	*/
	DR_MOD_DEATH(130, Integer.class),

	/**
	* 
	*/
	DMG_MOD_MELEE(131, Integer.class),
	/**
	* 
	*/
	DMG_MOD_FIRE(132, Integer.class),
	/**
	* 
	*/
	DMG_MOD_FROST(133, Integer.class),
	/**
	* 
	*/
	DMG_MOD_MYSTIC(134, Integer.class),
	/**
	* 
	*/
	DMG_MOD_DEATH(135, Integer.class),

	/**
	* 
	*/
	PVP_TEAM(136, Short.class),

	/**
	* 
	*/
	PVP_KILLS(137, Integer.class), // Thought they were deleted in 88 but
									// apparently not
	/**
	* 
	*/
	PVP_DEATHS(138, Integer.class),

	// PVP_SCORE (138),
	/**
	* 
	*/
	PVP_STATE(139, String.class),
	/**
	* 
	*/
	PVP_FLAG_CAPTURES(140, Integer.class),

	/**
	* 
	*/
	MOD_MELEE_TO_HIT(141, Float.class),
	/**
	* 
	*/
	MOD_MELEE_TO_CRIT(142, Float.class),
	/**
	* 
	*/
	MOD_MAGIC_TO_HIT(143, Float.class),
	/**
	* 
	*/
	MOD_MAGIC_TO_CRIT(144, Float.class),
	/**
	* 
	*/
	MOD_PARRY(145, Float.class),
	/**
	* 
	*/
	MOD_BLOCK(146, Float.class),

	/**
	* 
	*/
	MOD_MOVEMENT(147, Integer.class),
	/**
	* 
	*/
	MOD_HEALTH_REGEN(148, Float.class),
	/**
	* 
	*/
	MOD_ATTACK_SPEED(149, Float.class),
	/**
	* 
	*/
	MOD_CASTING_SPEED(150, Float.class),
	/**
	* 
	*/
	MOD_HEALING(151, Float.class),

	/**
	* 
	*/
	TOTAL_SIZE(152, Float.class),
	/**
	* 
	*/
	AGGRO_PLAYERS(153, Short.class),

	// New ones found in 8.6), not sure about between versions
	/**
	* 
	*/
	MOD_LUCK(154, Float.class),
	/**
	* 
	*/
	HEALTH_REGEN(155, Short.class),
	/**
	* 
	*/
	BLEEDING(156, Short.class),
	/**
	* 
	*/
	DAMAGE_SHIELD(157, Integer.class),
	/**
	* 
	*/
	HIDE_NAMEBOARD(158, Short.class),

	// New ones found in 8.8), not sure about between versions
	/**
	* 
	*/
	HIDE_MINIMAP(159, Short.class),

	// ICEEE - Credit drops
	/**
	* 
	*/
	CREDIT_DROPS(160, Short.class),
	/**
	* 
	*/
	HEROISM_GAIN_RATE(161, Short.class),
	/**
	* 
	*/
	QUEST_EXP_GAIN_RATE(162, Short.class),
	/**
	* 
	*/
	DROP_GAIN_RATE(163, Short.class),
	/**
	* 
	*/
	TAGS(164, String.class);

	private int code;
	private Class<?> valueClass;

	// For quick lookup of code to enum constant
	private static StatType[] map = new StatType[256];
	static {
		for (StatType t : StatType.values()) {
			map[t.code] = t;
		}
	}

	StatType(int code, Class<?> valueClass) {
		this.code = code;
		this.valueClass = valueClass;
	}

	public static StatType fromCode(int code) {
		if (code >= map.length || map[code] == null) {
			throw new IllegalArgumentException("Unknown stat type " + code);
		}
		return map[code];
	}

	public int getCode() {
		return code;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

}
