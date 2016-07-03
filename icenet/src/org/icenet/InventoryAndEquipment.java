package org.icenet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Item;
import org.icelib.ItemType;
import org.icelib.Persona;
import org.icelib.Slot;
import org.icenet.client.Client;
import org.icenet.client.ClientListener;
import org.icenet.client.ClientListenerAdapter;

/**
 * Manages players inventory and equipment. All modification and querying of an
 * inventory and equipment should usually come through this class. It is
 * responsible for querying and updating the server on inventory changes.
 */
public class InventoryAndEquipment {

	private static final Logger LOG = Logger.getLogger(InventoryAndEquipment.class.getName());
	private int equipped;
	private int nonBagEquipment;
	private int charms;
	private final ClientListener listener;

	public interface Listener {

		void rebuild(Persona persona, InventoryAndEquipment inv);

		void slotChanged(InventoryItem oldItem, InventoryItem newItem);
	}

	/**
	 * A single item that is equipped. Encapsulates the item itself and the
	 * actual slot it is in.
	 */
	public class EquipmentItem {

		private Item item;
		private Slot slot;

		public EquipmentItem(Item item, Slot slot) {
			this.item = item;
			this.slot = slot;
		}

		public void setItem(Item item) {
			this.item = item;
		}

		public Item getItem() {
			return item;
		}

		public Slot getSlot() {
			return slot;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final EquipmentItem other = (EquipmentItem) obj;
			if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
				return false;
			}
			if (this.slot != other.slot) {
				return false;
			}
			return true;
		}
	}

	/**
	 * A single normal item in the inventory. Encapsults the item itself, the
	 * quantity the slot should hold and whether the item can be split or not.
	 */
	public class InventoryItem {

		private Item item;
		private int quantity;
		private boolean canSplit;
		private int slot;

		private InventoryItem(int slot) {
			this(slot, null, 0, false);
		}

		private InventoryItem(int slot, Item item, int quantity, boolean canSplit) {
			this.slot = slot;
			this.item = item;
			this.quantity = quantity;
			this.canSplit = canSplit;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			return hash;
		}

		public boolean isTaken() {
			return item != null;
		}

		public boolean isEmpty() {
			return item != null;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final InventoryItem other = (InventoryItem) obj;
			if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
				return false;
			}
			if (this.quantity != other.quantity) {
				return false;
			}
			if (this.slot != other.slot) {
				return false;
			}
			return true;
		}

		public int getSlot() {
			return slot;
		}

		public Item getItem() {
			return item;
		}

		public int getQuantity() {
			return quantity;
		}

		public boolean isCanSplit() {
			return canSplit;
		}

		@Override
		public String toString() {
			return "InventoryItem{" + "item=" + item + ", quantity=" + quantity + ", canSplit=" + canSplit + ", slot="
					+ slot + '}';
		}

		public void setItem(Item item) {
			this.item = item;
		}
	}

	private Persona player;
	private int freeSlots;
	private int totalBagSlots = -1;
	private final Client client;
	private Map<Long, Item> items = new HashMap<>();
	private List<InventoryItem> inventory = new ArrayList<>();
	private List<EquipmentItem> equipment = new ArrayList<>();
	private List<Listener> listeners = new ArrayList<>();
	private int strengthBonus;
	private int psycheBonus;
	private int dexterityBonus;
	private int spiritBonus;
	private int armourDefense;
	private int deathDefense;
	private int fireDefense;
	private int frostDefense;
	private int meleeDefense;
	private int mysticDefense;
	private int constitutionBonus;
	private int attackSpeedMod;
	private int blockMod;
	private int castSpeedMod;
	private int healingMod;
	private int magicCritMod;
	private int magicHitMod;
	private int meleeCritMod;
	private int meleeHitMod;
	private int parryMod;
	private int regenHealthMod;
	private int runSpeedMod;

	public InventoryAndEquipment(Client client, Persona player) {
		this.client = client;
		setPlayer(player);
		client.addListener(listener = new ClientListenerAdapter() {
			@Override
			public void inventoryUpdate() {
				new Thread("InventoryUpdate") {
					@Override
					public void run() {
						try {
							LOG.info("Inventory update from network");
							rebuild();
						} catch (NetworkException ex) {
							LOG.log(Level.SEVERE, "Failed to rebuild inventory.", ex);
						}
					}
				}.start();
			}
		});
	}

	public int getTotalBasicEquipment() {
		return nonBagEquipment;
	}

	/**
	 * Get all the equipment items that are containers.
	 *
	 * @return container items
	 */
	public List<EquipmentItem> getContainerItems() {
		List<EquipmentItem> containerItems = new ArrayList<>();
		for (EquipmentItem eq : equipment) {
			if (eq.slot.isContainer()) {
				containerItems.add(eq);
			}
		}
		return containerItems;
	}

	/**
	 * Get all inventory items of a particular type.
	 *
	 * @param type
	 *            type
	 * @return items
	 */
	public List<InventoryItem> getInventoryItemsOfType(ItemType type) {
		List<InventoryItem> reagents = new ArrayList<>();
		for (InventoryItem eq : inventory) {
			if (eq.isTaken() && eq.getItem().getType().equals(type)) {
				reagents.add(eq);
			}
		}
		return reagents;
	}

	/**
	 * Get all the equipment items that are charms.
	 *
	 * @return container items
	 */
	public List<EquipmentItem> getCharmItems() {
		List<EquipmentItem> containerItems = new ArrayList<>();
		for (EquipmentItem eq : equipment) {
			if (eq.slot.isCharm()) {
				containerItems.add(eq);
			}
		}
		return containerItems;
	}

	public int getTotalCharms() {
		return charms;
	}

	public int getTotalEquippedItems() {
		return equipped;
	}

	public int getStrengthBonus() {
		return strengthBonus;
	}

	public int getDexterityBonus() {
		return dexterityBonus;
	}

	public int getSpiritBonus() {
		return spiritBonus;
	}

	public int getArmourDefense() {
		return armourDefense;
	}

	public int getDeathDefense() {
		return deathDefense;
	}

	public int getFireDefense() {
		return fireDefense;
	}

	public int getFrostDefense() {
		return frostDefense;
	}

	public int getMeleeDefense() {
		return meleeDefense;
	}

	public int getMysticDefense() {
		return mysticDefense;
	}

	public int getConstitutionBonus() {
		return constitutionBonus;
	}

	public int getAttackSpeedMod() {
		return attackSpeedMod;
	}

	public int getBlockMod() {
		return blockMod;
	}

	public int getCastSpeedMod() {
		return castSpeedMod;
	}

	public int getHealingMod() {
		return healingMod;
	}

	public int getMagicCritMod() {
		return magicCritMod;
	}

	public int getMagicHitMod() {
		return magicHitMod;
	}

	public int getMeleeCritMod() {
		return meleeCritMod;
	}

	public int getMeleeHitMod() {
		return meleeHitMod;
	}

	public int getParryMod() {
		return parryMod;
	}

	public int getPsycheBonus() {
		return psycheBonus;
	}

	public int getRegenHealthMod() {
		return regenHealthMod;
	}

	public int getRunSpeedMod() {
		return runSpeedMod;
	}

	public void destroy() {
		client.removeListener(listener);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.add(listener);
	}

	public void deequip(int slotToReturnTo, Slot slot) throws NetworkException {
		client.deequip(slotToReturnTo, slot);
	}

	public void swap(InventoryItem invItem1, InventoryItem invItem2) throws NetworkException {
		int i1 = inventory.indexOf(invItem1);
		int i2 = inventory.indexOf(invItem2);
		client.swapInventoryItem(invItem1.slot, invItem2.slot);
	}

	public final void setPlayer(Persona player) {
		items.clear();
		totalBagSlots = -1;
		this.player = player;
	}

	public int getFreeSlots() {
		return freeSlots;
	}

	public int getTotalBagSlots() {
		return totalBagSlots;
	}

	public List<EquipmentItem> getEquipment() {
		return Collections.unmodifiableList(equipment);
	}

	public InventoryItem getInventoryItemById(long entityId) {
		for (InventoryItem i : inventory) {
			if (i.getItem() != null && i.getItem().getEntityId() == entityId) {
				return i;
			}
		}
		return null;
	}

	public List<InventoryItem> getInventory() {
		return Collections.unmodifiableList(inventory);
	}

	public void emote(long id, String sender, String emote) {
	}

	private Item getItem(long id) throws NetworkException {
		if (items.containsKey(id)) {
			return items.get(id);
		}
		Item item = client.getItem(id);
		items.put(id, item);
		return item;
	}

	private void fireRebuild() {
		for (Listener l : listeners) {
			l.rebuild(player, this);
		}
	}

	private void fireSlotChanged(InventoryItem oldItem, InventoryItem newItem) {
		for (Listener l : listeners) {
			l.slotChanged(oldItem, newItem);
		}
	}

	public void swapWithInventoryItem(int inventorySlot, Slot equipmentSlot) throws NetworkException {
		EquipmentItem eq = getEquipmentInSlot(equipmentSlot);
		if (eq == null) {
			throw new IllegalArgumentException("No equipment in slot " + equipmentSlot);
		}
		final Item equipmentItem = eq.getItem();
		InventoryItem item = inventory.get(inventorySlot);
		final Item inventoryItem = item.getItem();
		if (inventoryItem == null) {
			throw new IllegalArgumentException("No item in inventory slot " + inventorySlot);
		}
		if (!equipmentItem.getEquipType().equals(inventoryItem.getEquipType())) {
			throw new NetworkException(NetworkException.ErrorType.INCOMPATIBLE_ITEMS);
		}
		client.equip(equipmentSlot, inventorySlot);
	}

	public EquipmentItem getEquipmentInSlot(Slot slot) {
		for (EquipmentItem i : equipment) {
			if (i.getSlot().equals(slot)) {
				return i;
			}
		}
		return null;
	}

	public void rebuild() throws NetworkException {
		LOG.info("Rebuilding inventory and equipment");

		int newBagSlots = NetConstants.DEFAULT_INVENTORY_SLOTS;
		int newFreeSlots = 0;
		int newEquipped = 0;
		int newCharms = 0;
		int newNonBagEquipment = 0;
		List<EquipmentItem> newEquipment = new ArrayList<>();
		List<InventoryItem> newInventory = new ArrayList<>();

		if (player != null) {
			boolean eqChanged = false;
			boolean invChanged = false;

			// Gather all the items for those equipped
			for (Slot slot : Slot.values()) {

				try {
					Item eqIt = player.getEquipment().containsKey(slot) ? getItem(player.getEquipment().get(slot))
							: null;
					if (eqIt == null) {
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Nothing equipped in %s", slot));
						}
					} else {
						newEquipped++;
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Equipped with %s in %s", eqIt.getDisplayName(), slot));
						}
						if (slot.isContainer()) {
							newBagSlots += eqIt.getContainerSlots();
						} else if (slot.isCharm()) {
							newCharms++;
						} else {
							newNonBagEquipment++;
						}
					}
					EquipmentItem eqItem = new EquipmentItem(eqIt, slot);
					newEquipment.add(eqItem);
				} catch (IllegalArgumentException iae) {
					LOG.warning(String.format("Could not locate item for slot %s", slot));
				}
			}

			// Test if equipment has changed
			if (!newEquipment.equals(equipment)) {
				eqChanged = true;
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Equipment has changed");
				}
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Equipment is the same");
				}
			}

			// Gather new inventory
			int i = 0;
			while (i < newBagSlots) {
				Persona.SlotContents slot = player.getInventory().get(i);
				if (slot == null) {
					newFreeSlots++;
					newInventory.add(new InventoryItem(i));
					if (LOG.isLoggable(Level.FINE)) {
						LOG.fine(String.format("Adding empty slot at %d", i));
					}
					i++;
				} else {

					try {
						Item item = getItem(slot.getItemId());
						int maxPerSlot = Math.max(1, item.getIvMax1());
						int amount = 1;
						while (i < newBagSlots && amount > 0) {
							// TODO how do IvMax1 and IvMax2 and IvType1 and
							// IvType2
							int itemInSlot = Math.max(amount, maxPerSlot);
							if (LOG.isLoggable(Level.FINE)) {
								LOG.fine(String.format("Adding %d of %s to slot %d (max %d) leaving %d", itemInSlot,
										item.getDisplayName(), i, maxPerSlot, amount));
							}
							newInventory.add(new InventoryItem(i, item, itemInSlot, maxPerSlot > 1));
							amount -= itemInSlot;
							i++;
						}
					} catch (IllegalArgumentException iiae) {
						LOG.log(Level.SEVERE, String.format("Could not add item to inventory slot .", slot), iiae);
					}
				}
			}

			// If the number of slots has changed, then rebuild the whole lot
			if (newBagSlots != totalBagSlots) {
				eqChanged = true;
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("Equipment changed because bags change"));
				}
			}

			freeSlots = newFreeSlots;
			equipped = newEquipped;
			totalBagSlots = newBagSlots;
			equipment = newEquipment;
			nonBagEquipment = newNonBagEquipment;
			charms = newCharms;

			// If equipment has changed, just rebuild everything
			// (this may change to fire individual add/remove/update events)
			if (eqChanged) {
				inventory = newInventory;
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("Firing rebuild because requipment changed"));
				}
				fireRebuild();
			} else {
				// Otherwise fire events for the inventory slot changes
				Iterator<InventoryItem> it1 = inventory.iterator();
				Iterator<InventoryItem> it2 = newInventory.iterator();
				int index = 0;
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("Looking for changed slots"));
				}
				while (it1.hasNext()) {
					InventoryItem i1 = it1.next();
					InventoryItem i2 = it2.next();
					if (!i1.equals(i2)) {
						i2.slot = index;
						inventory.set(index, i2);
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Slot %d has changed (%s)", index, i2));
						}
						fireSlotChanged(i1, i2);
					} else {
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("%s is same as %s", i1, i2));
						}
					}
					index++;
				}
			}

			// Now total up bonuses
			psycheBonus = 0;
			strengthBonus = 0;
			spiritBonus = 0;
			dexterityBonus = 0;
			constitutionBonus = 0;
			armourDefense = 0;
			deathDefense = 0;
			fireDefense = 0;
			frostDefense = 0;
			mysticDefense = 0;
			attackSpeedMod = 0;
			blockMod = 0;
			castSpeedMod = 0;
			healingMod = 0;
			magicCritMod = 0;
			magicHitMod = 0;
			meleeCritMod = 0;
			parryMod = 0;
			regenHealthMod = 0;
			runSpeedMod = 0;

			for (EquipmentItem eq : equipment) {
				final Item item = eq.getItem();
				if (item != null) {
					// Stat bonuses
					psycheBonus += item.getBonusPsyche();
					strengthBonus += item.getBonusStrength();
					spiritBonus += item.getBonusSpirit();
					dexterityBonus += item.getBonusDexterity();
					constitutionBonus += item.getBonusConstitution();

					// Defense bonuses
					armourDefense += item.getArmourResistMelee();
					deathDefense += item.getArmourResistDeath();
					fireDefense += item.getArmourResistFire();
					frostDefense += item.getArmourResistFrost();
					mysticDefense += item.getArmourResistMystic();

					// Other
					attackSpeedMod += item.getAttackSpeedMod();
					blockMod += item.getBlockMod();
					castSpeedMod += item.getCastSpeedMod();
					healingMod += item.getHealingMod();
					magicCritMod += item.getMagicCritMod();
					magicHitMod += item.getMagicHitMod();
					meleeCritMod += item.getMeleeCritMod();
					meleeHitMod += item.getMeleeHitMod();
					parryMod += item.getParryMod();
					regenHealthMod += item.getRegenHealthMod();
					runSpeedMod += item.getRunSpeedMod();
				}
			}

		}
	}

	public void equip(Slot slot, InventoryItem invItem) throws NetworkException {
		client.equip(slot, invItem.slot);
	}
}
