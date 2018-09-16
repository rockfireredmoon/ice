package org.icenet;

import java.text.ParseException;

/**
 * Environment change, with 3 sub-types
 */
public class SetMapMessage extends SimulatorMessage {

	private int instanceId;
	private final int zoneDefId;
	private final String terrain;
	private final int pageSize;
	private String zoneId;
	private String environment;
	private String mapName;

	public final static int ZONE_CHANGE = 0;
	public final static int SET_MAP = 1;
	public final static int TIME_OF_DAY = 2;

	private short mask;

	public SetMapMessage(SimulatorMessage msg) throws ParseException {
		super(msg);
		setValidForProtocol(Simulator.ProtocolState.GAME);
		payload.rewind();

		mask = readUnsignedByte();

		// Some id's
		zoneId = readString();
		if (zoneId.length() > 0) {
			String ids = zoneId.substring(1, zoneId.length() - 1);
			String[] idArgs = ids.split("-");
			instanceId = Integer.parseInt(idArgs[0]);
		}

		zoneDefId = (int) readUnsignedInt();
		pageSize = readUnsignedShort();

		// Terrain / Environment name
		terrain = readString();
		environment = readString();
		
		if(mask != 2) {
			// Time update has no map name 
			mapName = readString();
		}

	}

	public short getMask() {
		return mask;
	}

	public String getMapName() {
		return mapName;
	}

	public String getEnvironment() {
		return environment;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getZoneIdString() {
		return zoneId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public int getZoneDefId() {
		return zoneDefId;
	}

	public String getTerrain() {
		return terrain;
	}

}
