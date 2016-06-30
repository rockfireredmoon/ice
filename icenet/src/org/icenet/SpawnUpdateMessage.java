package org.icenet;

import java.nio.BufferUnderflowException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.icelib.StatType;
import org.icenet.SpawnUpdateMessage.Mods.Mod;

public class SpawnUpdateMessage extends SimulatorMessage {

	public final static int CREATURE_UPDATE_TYPE = 1;
	public final static int CREATURE_UPDATE_ZONE = 2;
	public final static int CREATURE_UPDATE_POSITION_INC = 4;
	public final static int CREATURE_UPDATE_VELOCITY = 8;
	public final static int CREATURE_UPDATE_ELEVATION = 16;
	public final static int CREATURE_UPDATE_STAT = 32;
	public final static int CREATURE_UPDATE_MOD = 64;
	public final static int CREATURE_UPDATE_COMBAT = 128;
	public final static int CREATURE_UPDATE_LOGIN_POSITION = 256; // From 0.8.8

	public final static Map<Integer, String> MASK_DEBUG_STRINGS = new HashMap<>();
	static {
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_TYPE, "CREATURE_UPDATE_TYPE");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_ZONE, "CREATURE_UPDATE_ZONE");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_POSITION_INC, "CREATURE_UPDATE_POSITION_INC");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_VELOCITY, "CREATURE_UPDATE_VELOCITY");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_ELEVATION, "CREATURE_UPDATE_ELEVATION");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_STAT, "CREATURE_UPDATE_STAT");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_MOD, "CREATURE_UPDATE_MOD");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_COMBAT, "CREATURE_UPDATE_COMBAT");
		MASK_DEBUG_STRINGS.put(CREATURE_UPDATE_LOGIN_POSITION, "CREATURE_UPDATE_LOGIN_POSITION");
	}

	private static final Logger LOG = Logger.getLogger(SpawnUpdateMessage.class.getName());
	private long id;
	private final int movementType;
	private List<SpawnUpdate> updates = new ArrayList<>();
	private int defHints;
	private String defHintsExtra;

	public interface SpawnUpdate {
		// Super interface for all update tpyes
	}

	/**
	 * Sub-data for "Type" message
	 *
	 * Dec: 001 Hex: 0x01
	 */
	public static class Type implements SpawnUpdate {
		private long creatureDefID;

		private Type(long readUnsignedInt) {
			this.creatureDefID = readUnsignedInt;
		}

		public long getCreatureDefID() {
			return creatureDefID;
		}

		@Override
		public String toString() {
			return "Type [creatureDefID=" + creatureDefID + "]";
		}
	}

	/**
	 * Sub-data for "Zone" message that is sent when a spawn appears or
	 * disappears in the zone. The first occurence announces arrival of new
	 * spawn, the next occurence announces it's departure. So this message only
	 * appears twice for each spawn.
	 *
	 * Dec: 002 Hex: 0x02
	 */
	public static class ZoneUpdate implements SpawnUpdate {

		private long x;
		private long z;
		private int instance;
		private int zone;
		private int shard; // ??

		public long getX() {
			return x;
		}

		public long getZ() {
			return z;
		}

		public int getInstance() {
			return instance;
		}

		public int getZone() {
			return zone;
		}

		public int getShard() {
			return shard;
		}

		@Override
		public String toString() {
			return "ZoneUpdate [x=" + x + ", z=" + z + ", instance=" + instance + ", zone=" + zone + ", shard=" + shard + "]";
		}
	}

	/**
	 * Sub-data for Elevation mesage.
	 *
	 * Dec: 16 Hex: 0x10
	 */
	public static class Elevation implements SpawnUpdate {
		private int elevation;

		private Elevation(int elevation) {
			this.elevation = elevation;
		}

		public int getElevation() {
			return elevation;
		}

		public void setElevation(int elevation) {
			this.elevation = elevation;
		}

		@Override
		public String toString() {
			return "Elevation [elevation=" + elevation + "]";
		}
	}

	/**
	 * Sub-data for Elevation mesage.
	 *
	 * Dec: 64 Hex: 0x40
	 */
	public static class Mods implements SpawnUpdate {

		public static class Mod {
			private int modStatID;
			private int abilityID;
			private long priority;
			private float clientAmount;

			public int getModStatID() {
				return modStatID;
			}

			public void setModStatID(int modStatID) {
				this.modStatID = modStatID;
			}

			public int getAbilityID() {
				return abilityID;
			}

			public void setAbilityID(int abilityID) {
				this.abilityID = abilityID;
			}

			public long getPriority() {
				return priority;
			}

			public void setPriority(long priority) {
				this.priority = priority;
			}

			public float getClientAmount() {
				return clientAmount;
			}

			public void setClientAmount(float clientAmount) {
				this.clientAmount = clientAmount;
			}

			@Override
			public String toString() {
				return "Mod [modStatID=" + modStatID + ", abilityID=" + abilityID + ", priority=" + priority + ", clientAmount="
						+ clientAmount + "]";
			}

		}

		private int modCount;
		private List<Mod> mods = new ArrayList<>();
		public long duration;
		public String description;
		private List<Integer> effects = new ArrayList<>();

		private Mods(int modCount) {
			this.modCount = modCount;
		}

		public int getModCount() {
			return modCount;
		}

		public List<Mod> getMods() {
			return mods;
		}

		public long getDuration() {
			return duration;
		}

		public String getDescription() {
			return description;
		}

		public List<Integer> getEffects() {
			return effects;
		}

		@Override
		public String toString() {
			return "Mods [modCount=" + modCount + ", mods=" + mods + ", duration=" + duration + ", description=" + description
					+ ", effects=" + effects + "]";
		}
	}

	/**
	 * Position Inc.
	 *
	 * Dec: 4 Hex: 0x04
	 */
	public class Position implements SpawnUpdate {
		private int x;
		private int z;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getZ() {
			return z;
		}

		public void setZ(int z) {
			this.z = z;
		}

		@Override
		public String toString() {
			return "Position [x=" + x + ", z=" + z + "]";
		}

	}

	/**
	 * Velocity.
	 *
	 * Dec: 8 Hex: 0x08
	 */
	public class Velocity implements SpawnUpdate {

		private short rotation;
		private short speed;
		private short heading;

		public short getHeading() {
			return heading;
		}

		public short getSpeed() {
			return speed;
		}

		public short getRotation() {
			return rotation;
		}

		@Override
		public String toString() {
			return "Velocity [rotation=" + rotation + ", speed=" + speed + ", heading=" + heading + "]";
		}

	}

	public class Stats {

		private Map<StatType, Object> stats = new HashMap<>();
	}

	public SpawnUpdateMessage(Simulator sim, SimulatorMessage msg) throws ParseException {
		super(msg);
		setValidForProtocol(Simulator.ProtocolState.GAME);
		payload.rewind();
		id = readUnsignedInt();

		if (sim.getProtocolVersion() < 22) {
			movementType = readUnsignedByte();
		} else {
			movementType = readUnsignedShort();
		}

		// TODO - I dont think this is ALWAYS done
		// Util.CreatureInstance(char *buffer, CreatureInstance *cInst) on
		// server for example
		// As far as I can make out it's only done when getting
		// CREATURE_UPDATE_STAT & CREATURE_UPDATE_TYPE (33)
		if (movementType == (CREATURE_UPDATE_STAT + CREATURE_UPDATE_TYPE)) {
			LOG.info("Reading DEFHINTS");
			defHints = readUnsignedShort();
			if (sim.getProtocolVersion() > 26) {
				try {
					defHintsExtra = readString();
				} catch (BufferUnderflowException ufe) {
					throw new RuntimeException("Failed to read def hints extra for " + id + " at " + movementType);
				}
			}
		}

		if ((movementType & CREATURE_UPDATE_TYPE) != 0) {
			updates.add(new Type(readUnsignedInt()));
		}

		if ((movementType & CREATURE_UPDATE_ZONE) != 0) {
			ZoneUpdate zone = new ZoneUpdate();
			final String zoneString = readString();
			String[] arr = zoneString.substring(1, zoneString.length() - 1).split("-");
			zone.instance = Integer.parseInt(arr[0]);
			zone.zone = Integer.parseInt(arr[1]);
			zone.x = new Long(readUnsignedInt());
			zone.z = new Long(readUnsignedInt());
			updates.add(zone);
		}

		if ((movementType & CREATURE_UPDATE_ELEVATION) != 0) {
			updates.add(new Elevation(readUnsignedShort()));
		}

		if ((movementType & CREATURE_UPDATE_POSITION_INC) != 0) {
			Position rot = new Position();
			rot.x = readUnsignedShort();
			rot.z = readUnsignedShort();
			updates.add(rot);
		}

		if ((movementType & CREATURE_UPDATE_VELOCITY) != 0) {
			Velocity rot = new Velocity();
			rot.heading = readUnsignedByte();

			// TODO the client of getRotation() should really be doing this
			rot.rotation = (short) (((float) readUnsignedByte() / 65536f) * 360f);
			rot.speed = readUnsignedByte();
			updates.add(rot);
		}

		if ((movementType & CREATURE_UPDATE_MOD) != 0) {
			Mods mods = new Mods(readUnsignedShort());
			for (int i = 0; i < mods.modCount; i++) {
				Mod mod = new Mod();
				if (sim.getProtocolVersion() > 15) {
					mod.priority = readUnsignedInt();
					mod.modStatID = readUnsignedShort();
					mod.abilityID = readUnsignedShort();
					if (mod.priority == 1) {
						mod.clientAmount = readFloat();
					} else {
						mod.clientAmount = readUnsignedShort();
					}
				} else {
					mod.modStatID = readUnsignedShort();
					mod.abilityID = readUnsignedShort();
					mod.clientAmount = readUnsignedShort();
				}
				mods.duration = readUnsignedInt();
				if (sim.getProtocolVersion() >= 24) {
					mods.description = readString();
				}
				mods.mods.add(mod);
			}
			int effects = readUnsignedShort();
			for (int i = 0; i < effects; i++) {
				mods.effects.add(readUnsignedShort());
			}
			updates.add(mods);
		}

		if ((movementType & CREATURE_UPDATE_STAT) != 0) {
			int statCount = readUnsignedShort();
			Stats stats = new Stats();
			for (int i = 0; i < statCount; i++) {

				try {
					StatType statType = StatType.fromCode(readUnsignedShort());
					Object value = null;
					if (statType.getValueClass().equals(String.class)) {
						value = readString();
					} else if (statType.getValueClass().equals(Integer.class)) {
						// Unsigned integer, so a long
						value = readUnsignedInt();
					} else if (statType.getValueClass().equals(Short.class)) {
						// Unsigned integer, so an integer
						value = readUnsignedShort();
					} else if (statType.getValueClass().equals(Byte.class)) {
						// Unsigned byte, so a short
						value = readUnsignedByte();
					} else if (statType.getValueClass().equals(Float.class)) {
						// Float
						value = readFloat();
					} else {
						throw new ParseException("Unknown type " + statType.getValueClass() + " for " + statType, 0);
					}
					stats.stats.put(statType, value);
				} catch (IllegalArgumentException iae) {
					ParseException parseException = new ParseException("Failed reading one of the " + statCount + " stats (" + i + ")", 0);
					parseException.initCause(iae);
					throw parseException;
				}
			}

		}

		if (payload.remaining() > 0) {
			LOG.warning(String.format("%d remaining bytes for %s", payload.remaining(), this));
		}
	}

	public long getId() {
		return id;
	}

	public int getMovementType() {
		return movementType;
	}

//	private Map<Slot, Long> getEquipmentObject() throws ParseException {
//		final String readString = readString();
//		ENotation.EObject eqs = new ENotation.EObject(readString);
//		Map<Slot, Long> eq = new HashMap<>();
//		for (Map.Entry<Object, Object> eqEn : eqs.entrySet()) {
//			final Long itemId = (Long) eqEn.getValue();
//			eq.put(Slot.fromCode(((Long) eqEn.getKey()).intValue()), itemId);
//		}
//		return eq;
//	}

	public List<SpawnUpdate> getUpdates() {
		return updates;
	}

	public boolean has(Class<? extends SpawnUpdate> clazz) {
		for (SpawnUpdate u : updates) {
			if (u.getClass().equals(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder bui = new StringBuilder();
		for (Map.Entry<Integer, String> en : MASK_DEBUG_STRINGS.entrySet()) {
			if ((movementType & en.getKey()) != 0) {
				if (bui.length() > 0) {
					bui.append(",");
				}
				bui.append(en.getValue());
			}
		}
		return "SpawnUpdateMessage [id=" + id + ", movementType=" + movementType + "(" + bui.toString() + "), updates=" + updates
				+ "]";
	}

}
