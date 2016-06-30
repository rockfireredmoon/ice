package org.icenet;

import java.util.ArrayList;
import java.util.List;

/**
 * Creature Sub-message sent when the henge list should be displayed
 */
public class HengeListMessage extends SimulatorMessage {

	public static class Henge {
		private String name;
		private long cost;

		public Henge(String name, long cost) {
			this.name = name;
			this.cost = cost;
		}

		public String getName() {
			return name;
		}

		public long getCost() {
			return cost;
		}

	}

	private long sourceHengeId;
	private List<Henge> henges = new ArrayList<>();

	public HengeListMessage(CreatureEventReplyMessage msg) {
		super(msg);
		sourceHengeId = readUnsignedInt();
		int sz = readUnsignedByte();
		for (int i = 0; i < sz; i++) {
			henges.add(new Henge(readString(), readUnsignedInt()));
		}
	}

	public long getSourceHengeId() {
		return sourceHengeId;
	}

	public List<Henge> getHenges() {
		return henges;
	}

}
