/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class Persona extends AbstractCreature {

	public static class SlotContents {

		public SlotContents(long itemId, int qty) {
			this.itemId = itemId;
			this.qty = qty;
		}

		public long getItemId() {
			return itemId;
		}

		public int getQty() {
			return qty;
		}

		private long itemId;
		private int qty;
	}

	public enum EventType {

		PLAYER_MOVED
	}

	public class GameCharacterEvent extends EventObject {

		private final EventType eventType;

		public GameCharacterEvent(Persona source, EventType eventType) {
			super(source);
			this.eventType = eventType;
		}

		public Persona getCharacter() {
			return (Persona) getSource();
		}

		public EventType getType() {
			return eventType;
		}
	}

	private final static Logger LOG = Logger.getLogger(Persona.class.getName());
	private int order;
	private Long instance;
	private String statusText;
	private long secondsLogged;
	private int sessionsLogged;
	private long timeLogged;
	private long lastSession;
	private Date lastLogon;
	private Date lastLogoff;
	private int maxSideKicks;
	private Properties prefs = new Properties();
	private Map<Long, List<Long>> activeQuests = new HashMap();
	private List<Long> completeQuests = new ArrayList<>();
	private int baseLuck;
	private int visWeapon;
	private int will;
	private int might;
	private int healthMod;
	private String translocateDestination;
	private int basePsy;
	private int baseCon;
	private int baseStr;
	private int baseSpi;
	private int baseDex;
	private int heroism;
	private long exp;
	private int totalAbilityPoints;
	private int currentAbilityPoints;
	private long credits;
	private double modCastingSpeed;
	private int totalSize;
	private int health;
	private Appearance originalAppearance = new Appearance();
	//
	private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm aa");
	private Map<Integer, SlotContents> inventory;
	private Map<Slot, Long> equipment = new TreeMap<>();
	private List<Long> abilities = new ArrayList<>();
	private String shard;
	private Coin coin = new Coin(0);
	private boolean online;

	@Override
	public List<Long> getEquippedItems() {
		return Collections.unmodifiableList(new ArrayList<>(equipment.values()));
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public Persona() {
		inventory = new TreeMap<>();
	}

	public String getShard() {
		return shard;
	}

	public void setShard(String shard) {
		this.shard = shard;
	}

	public Appearance getOriginalAppearance() {
		return originalAppearance;
	}

	public void setOriginalAppearance(Appearance originalAppearance) {
		this.originalAppearance = originalAppearance;
	}

	public final List<Long> getCompleteQuests() {
		return completeQuests;
	}

	public final void setCompleteQuests(List<Long> completeQuests) {
		this.completeQuests = completeQuests;
	}

	public void clearInventory() {
		inventory.clear();
	}

	public final Map<Integer, SlotContents> getInventory() {
		return inventory;
	}

	public final void setInventory(Map<Integer, SlotContents> inventory) {
		this.inventory = inventory;
	}

	public final Map<Slot, Long> getEquipment() {
		return equipment;
	}

	public final void setEquipment(Map<Slot, Long> equipment) {
		this.equipment = equipment;
	}

	public final int getOrder() {
		return order;
	}

	public final void setOrder(int order) {
		this.order = order;
	}

	public final Long getInstance() {
		return instance;
	}

	public final void setInstance(Long instance) {
		this.instance = instance;
	}

	/**
	 * Call this when some external thing has changed the position of a
	 * character. For
	 * example, a warp was sent and the reposition message was received. This
	 * method will
	 * fire {@link GameCharacterEvent} events and be picked up by the various
	 * states to
	 * update themselves. DO NOT call this method to update the position locally
	 * (e.g. as
	 * the result of physics movement)
	 *
	 * @param location
	 *            new location
	 */
	// public final void warp(Location location) {
	// this.location = location;
	// LOG.info(String.format("Player warped to %s", location));
	// fireEvent(new GameCharacterEvent(this, EventType.PLAYER_MOVED));
	// }

	public final String getStatusText() {
		return statusText;
	}

	public final void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public final long getSecondsLogged() {
		return secondsLogged;
	}

	public final void setSecondsLogged(long secondsLogged) {
		this.secondsLogged = secondsLogged;
	}

	public final int getSessionsLogged() {
		return sessionsLogged;
	}

	public final void setSessionsLogged(int sessionsLogged) {
		this.sessionsLogged = sessionsLogged;
	}

	public final long getTimeLogged() {
		return timeLogged;
	}

	public final void setTimeLogged(long timeLogged) {
		this.timeLogged = timeLogged;
	}

	public final long getLastSession() {
		return lastSession;
	}

	public final void setLastSession(long lastSession) {
		this.lastSession = lastSession;
	}

	public final Date getLastLogon() {
		return lastLogon;
	}

	public final void setLastLogon(Date lastLogon) {
		this.lastLogon = lastLogon;
	}

	public final Date getLastLogoff() {
		return lastLogoff;
	}

	public final void setLastLogoff(Date lastLogoff) {
		this.lastLogoff = lastLogoff;
	}

	public final int getMaxSideKicks() {
		return maxSideKicks;
	}

	public final void setMaxSideKicks(int maxSideKicks) {
		this.maxSideKicks = maxSideKicks;
	}

	public final Properties getPrefs() {
		return prefs;
	}

	public final void setPrefs(Properties prefs) {
		this.prefs = prefs;
	}

	public final Map<Long, List<Long>> getActiveQuests() {
		return activeQuests;
	}

	public final void setActiveQuests(Map<Long, List<Long>> activeQuests) {
		this.activeQuests = activeQuests;
	}

	public final int getBaseLuck() {
		return baseLuck;
	}

	public final void setBaseLuck(int baseLuck) {
		this.baseLuck = baseLuck;
	}

	public final int getVisWeapon() {
		return visWeapon;
	}

	public final void setVisWeapon(int visWeapon) {
		this.visWeapon = visWeapon;
	}

	public final int getWill() {
		return will;
	}

	public final void setWill(int will) {
		this.will = will;
	}

	public final int getMight() {
		return might;
	}

	public final void setMight(int might) {
		this.might = might;
	}

	public final String getTranslocateDestination() {
		return translocateDestination;
	}

	public final void setTranslocateDestination(String translocateDestination) {
		this.translocateDestination = translocateDestination;
	}

	public int getBasePsy() {
		return basePsy;
	}

	public void setBasePsy(int basePsy) {
		this.basePsy = basePsy;
	}

	public int getBaseCon() {
		return baseCon;
	}

	public void setBaseCon(int baseCon) {
		this.baseCon = baseCon;
	}

	public int getBaseStr() {
		return baseStr;
	}

	public void setBaseStr(int baseStr) {
		this.baseStr = baseStr;
	}

	public int getBaseSpi() {
		return baseSpi;
	}

	public void setBaseSpi(int baseSpi) {
		this.baseSpi = baseSpi;
	}

	public int getBaseDex() {
		return baseDex;
	}

	public void setBaseDex(int baseDex) {
		this.baseDex = baseDex;
	}

	public final int getHeroism() {
		return heroism;
	}

	public final void setHeroism(int heroism) {
		this.heroism = heroism;
	}

	public final long getExp() {
		return exp;
	}

	public final void setExp(long exp) {
		this.exp = exp;
	}

	public final int getTotalAbilityPoints() {
		return totalAbilityPoints;
	}

	public final void setTotalAbilityPoints(int totalAbilityPoints) {
		this.totalAbilityPoints = totalAbilityPoints;
	}

	public final int getCurrentAbilityPoints() {
		return currentAbilityPoints;
	}

	public final void setCurrentAbilityPoints(int currentAbilityPoints) {
		this.currentAbilityPoints = currentAbilityPoints;
	}

	public final Coin getCoin() {
		return coin;
	}

	public final long getCredits() {
		return credits;
	}

	public final void setCredits(long credits) {
		this.credits = credits;
	}

	public final double getModCastingSpeed() {
		return modCastingSpeed;
	}

	public final void setModCastingSpeed(double modCastingSpeed) {
		this.modCastingSpeed = modCastingSpeed;
	}

	public final int getTotalSize() {
		return totalSize;
	}

	public final void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public final int getHealth() {
		return health;
	}

	public final void setHealth(int health) {
		this.health = health;
	}

	public final DateFormat getTimeFormat() {
		return timeFormat;
	}

	public final void setTimeFormat(DateFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

	public final DateFormat getDateTimeFormat() {
		return dateTimeFormat;
	}

	public final void setDateTimeFormat(DateFormat dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public int getOccupiedInventorySlots() {
		return inventory.size();
	}

	public Integer getNextFreeSlotNumber() {
		// TODO only check as many slots as available
		for (int i = 0; i < 1000; i++) {
			if (!inventory.containsKey(i)) {
				return i;
			}
		}
		return null;
	}

	public void equip(Slot slotToEquipTo, int inventorySlotToTakeFrom) {
		if (inventorySlotToTakeFrom < 0) {
			throw new IllegalArgumentException("Inventory slot must be >= 0");
		}
		Long item = null;
		if (equipment.containsKey(slotToEquipTo)) {
			item = equipment.get(slotToEquipTo);
		}
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Before equip, inventory has %d", inventory.size()));
		}
		equipment.put(slotToEquipTo, inventory.remove(inventorySlotToTakeFrom).itemId);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("After equip, inventory has %d", inventory.size()));
		}
		if (item != null) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Putting item %d back into inventory", item));
			}
			inventory.put(inventorySlotToTakeFrom, new SlotContents(item, 1));
		}
	}

	public boolean hasEquipment(Slot slot) {
		return equipment.containsKey(slot);
	}

	public void deequipAndDiscard(Slot slot) {
		Long id = equipment.get(slot);
		if (id == null) {
			throw new IllegalArgumentException("Nothing equipped in " + slot);
		}
		equipment.remove(slot);
	}

	public void deequip(int slotToReturnTo, Slot slot) {
		Long id = equipment.get(slot);
		if (id == null) {
			throw new IllegalArgumentException("Nothing equipped in " + slot);
		}

		// Determine where to return to
		if (slotToReturnTo == -1) {
			slotToReturnTo = getNextFreeSlotNumber();
		}
		if (slotToReturnTo > -1 && inventory.containsKey(slotToReturnTo)) {
			Integer newSlot = getNextFreeSlotNumber();
			if (newSlot == null) {
				throw new IllegalStateException("Not enough slots to de-equip");
			}
			slotToReturnTo = newSlot;
		}
		LOG.info(String.format("Before de-equip, there are %d items in equipment", equipment.size()));
		equipment.remove(slot);
		inventory.put(slotToReturnTo, new SlotContents(id, 1));
		LOG.info(String.format("After de-equip, there are %d items in equipment", equipment.size()));
		for (Map.Entry<Slot, Long> en : equipment.entrySet()) {
			LOG.info(String.format("    %s = %d", en.getKey(), en.getValue()));
		}
	}

	public void addToInventory(Long entityId) {
		Integer key = getNextFreeSlotNumber();
		if (key == null) {
			throw new IllegalStateException("Not enough room in inventory");
		}
		inventory.put(key, new SlotContents(entityId, 1));
	}

	public boolean isEquipped(Item item) {
		final Slot slot = item.getEquipType().toSlot();
		Long equipped = equipment.get(slot);
		return equipped != null && equipped.equals(item.getEntityId());
	}

	public void removeAllEquipment() {
		equipment.clear();
	}

	public void addToInventoryAndEquip(Item item) {
		addToInventory(item.getEntityId());
		equip(item);
	}

	public void equip(Item item) {
		assert item != null;
		final EquipType equipType = item.getEquipType();
		Slot slot = equipType.toSlot();
		Long equipped = equipment.get(slot);
		if (equipType.equals(EquipType.CONTAINER) && equipped != null) {
			slot = Slot.BAG_2;
			equipped = equipment.get(slot);
			if (equipped != null) {
				slot = Slot.BAG_3;
				equipped = equipment.get(slot);
				if (equipped != null) {
					slot = Slot.BAG_4;
					equipped = equipment.get(slot);
					if (equipped != null) {
						throw new IllegalStateException("All bag slots taken");
					}
				}
			}
		}
		equip(slot, item.getEntityId());
	}

	public void equip(Slot slot, long entityId) {
		Long equipped = equipment.get(slot);
		if (equipped != null) {
			if (equipped.equals(entityId)) {
				throw new IllegalStateException("Already equipped");
			}
			// Move to inventory
			Integer key = getNextFreeSlotNumber();
			if (key == null) {
				throw new IllegalStateException("Not enough room in inventory for current equipment in slot " + slot.toString());
			}
			inventory.put(key, new SlotContents(entityId, 1));
		}
		final int inventorySlot = getInventorySlot(entityId);
		if (inventorySlot == -1) {
			throw new IllegalStateException("Not in inventory.");
		}
		equip(slot, inventorySlot);
	}

	public boolean isItemInInventory(long entityId) {
		return inventory.values().contains(entityId);
	}

	public void swapInventory(int slot1, int slot2) {
		SlotContents l1 = inventory.get(slot1);
		SlotContents l2 = inventory.get(slot2);
		LOG.info(String.format("Swapping %d (%d) to %d (%d)", slot1, l1, slot2, l2));
		if (l1 != null) {
			inventory.put(slot2, l1);
		} else {
			inventory.remove(slot2);
		}
		if (l2 != null) {
			inventory.put(slot1, l2);
		} else {
			inventory.remove(slot1);
		}
		LOG.info(String.format("Now has %d in inventory", inventory.size()));
	}

	public int getInventorySlot(long entityId) {
		for (Map.Entry<Integer, SlotContents> en : inventory.entrySet()) {
			if (en.getValue().itemId == entityId) {
				return en.getKey();
			}
		}
		return -1;
	}

	public String getIcon() {
		return "cathead.png";
	}

	public void removeAllBags() {
		equipment.remove(Slot.BAG_1);
		equipment.remove(Slot.BAG_2);
		equipment.remove(Slot.BAG_3);
		equipment.remove(Slot.BAG_4);
	}

	public String getFirstName() {
		final String displayName = getDisplayName();
		if (Icelib.isNotNullOrEmpty(displayName)) {
			int idx = displayName.indexOf(' ');
			return idx == -1 ? displayName : displayName.substring(0, idx);
		}
		return "";
	}

	public String getLastName() {
		final String displayName = getDisplayName();
		if (Icelib.isNotNullOrEmpty(displayName)) {
			int idx = displayName.indexOf(' ');
			if (idx != -1) {
				return displayName.substring(idx + 1);
			}
		}
		return "";
	}
}
