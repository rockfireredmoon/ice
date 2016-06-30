package org.icenet;

/**
 * Creature Sub-message sent when Xp is received
 */
public class XpMessage extends SimulatorMessage {

	private long amount;

	public XpMessage(CreatureEventReplyMessage msg) {
		super(msg);
		amount = readUnsignedInt();
	}

	public long getAmount() {
		return amount;
	}

}
