package org.icenet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Item query reply (game mode)
 */
public class SceneryQueryReplyMessage extends SimulatorMessage {

	public enum PropertyType {

		PROPERTY_INTEGER, PROPERTY_FLOAT, PROPERTY_STRING, PROPERTY_SCENERY, PROPERTY_NULL;
	}

	public enum LinkType {
		LOYALTY, PATH;
	}

	public static class Property {
		private PropertyType type;
		private Object value;
		private String name;

		public Property(String name, PropertyType type, Object value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}

		public PropertyType getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}

	public static class Link {

		private long propId;
		private LinkType type;

		public Link(long propId, LinkType type) {
			this.propId = propId;
			this.type = type;
		}

		public long getPropId() {
			return propId;
		}

		public LinkType getType() {
			return type;
		}

	}

	public final static long FLAG_LOCKED = 0x01;
	public final static long FLAG_PRIMARY = 0x02;

	private static final Logger LOG = Logger.getLogger(SceneryQueryReplyMessage.class.getName());

	public final static int SCENERY_UPDATE_ASSET = 1;
	public final static int SCENERY_UPDATE_LINKS = 2;
	public final static int SCENERY_UPDATE_POSITION = 4;
	public final static int SCENERY_UPDATE_ORIENTATION = 8;
	public final static int SCENERY_UPDATE_SCALE = 16;
	public final static int SCENERY_UPDATE_PROPERTIES = 32;
	public final static int SCENERY_UPDATE_FLAGS = 64;

	private long id;
	private short mask;
	private String asset;
	private String name;
	private float scaleX, scaleY, scaleZ;
	private float rotateX, rotateY, rotateZ, rotateW;
	private float x, y, z;
	private long flags;
	private boolean locked;
	private boolean primary;
	private String layer;
	private List<Link> links = null;
	private List<Property> properties = null;

	public SceneryQueryReplyMessage(Simulator sim, SimulatorMessage msg) {
		super(msg);
		setValidForProtocol(Simulator.ProtocolState.GAME);
		payload.rewind();

		id = readUnsignedInt();
		mask = readUnsignedByte();

		if ((mask & SCENERY_UPDATE_ASSET) != 0) {
			asset = readString();
			if (sim.getProtocolVersion() >= 23) {
				layer = readString();
			}
		}
		if ((mask & SCENERY_UPDATE_POSITION) != 0) {
			x = readFloat();
			y = readFloat();
			z = readFloat();

		}
		if ((mask & SCENERY_UPDATE_ORIENTATION) != 0) {
			rotateX = readFloat();
			rotateY = readFloat();
			rotateZ = readFloat();
			rotateW = readFloat();
		}
		if ((mask & SCENERY_UPDATE_SCALE) != 0) {
			scaleX = readFloat();
			scaleY = readFloat();
			scaleZ = readFloat();
		}
		if ((mask & SCENERY_UPDATE_FLAGS) != 0) {
			flags = readUnsignedInt();
		}
		if ((mask & SCENERY_UPDATE_LINKS) != 0) {
			int linkCount = readUnsignedShort();
			links = new ArrayList<>(linkCount);
			for (int i = 0; i < linkCount; i++) {
				links.add(new Link(readUnsignedInt(), LinkType.values()[readUnsignedByte()]));
			}
		}
		if ((mask & SCENERY_UPDATE_PROPERTIES) != 0) {
			name = readString();
			int count = (int) readUnsignedInt();
			properties = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				String name = readString();
				PropertyType type = PropertyType.values()[readUnsignedByte()];
				Object val = null;
				switch (type) {
				case PROPERTY_INTEGER:
					val = readUnsignedInt();
					break;
				case PROPERTY_FLOAT:
					val = readFloat();
					break;
				case PROPERTY_STRING:
				case PROPERTY_SCENERY:
					val = readString();
					break;
				}
				properties.add(new Property(name, type, val));
			}

			//
		}

		// flags derivd from bitmask of 'flags'
		locked = (flags & FLAG_LOCKED) != 0;
		primary = (flags & FLAG_PRIMARY) != 0;

		if (payload.remaining() > 0) {
			LOG.warning(String.format("%d remaining bytes for %s", payload.remaining(), this));
		}
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public boolean isLocked() {
		return locked;
	}

	public long getId() {
		return id;
	}

	public String getAsset() {
		return asset;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getScaleZ() {
		return scaleZ;
	}

	public float getRotateX() {
		return rotateX;
	}

	public float getRotateY() {
		return rotateY;
	}

	public float getRotateZ() {
		return rotateZ;
	}

	public float getRotateW() {
		return rotateW;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public long getFlags() {
		return flags;
	}

	public String getName() {
		return name;
	}

	public boolean isPrimary() {
		return primary;
	}

	public static long getFlagLocked() {
		return FLAG_LOCKED;
	}

	public static long getFlagPrimary() {
		return FLAG_PRIMARY;
	}

	public short getMask() {
		return mask;
	}

	public String getLayer() {
		return layer;
	}

	@Override
	public String toString() {
		return "SceneryQueryReplyMessage [id=" + id + ", mask=" + mask + ", asset=" + asset + ", name=" + name + ", scaleX="
				+ scaleX + ", scaleY=" + scaleY + ", scaleZ=" + scaleZ + ", rotateX=" + rotateX + ", rotateY=" + rotateY
				+ ", rotateZ=" + rotateZ + ", rotateW=" + rotateW + ", x=" + x + ", y=" + y + ", z=" + z + ", flags=" + flags
				+ ", locked=" + locked + ", primary=" + primary + ", layer=" + layer + ", links=" + links + ", properties="
				+ properties + "]";
	}

}
