package org.icenet;

/**
 * Creature Sub-message sent when a creature receives a heal
 */
public class HealMessage extends SimulatorMessage {

	private long amount;
	private long sourceId;

	public HealMessage(Simulator sim, CreatureEventReplyMessage msg) {
		super(msg);
		if (sim.getProtocolVersion() > 25) {
			sourceId = readUnsignedInt();
		}
		amount = readUnsignedInt();
	}

	public long getAmount() {
		return amount;
	}

	public long getSourceId() {
		return sourceId;
	}

}
