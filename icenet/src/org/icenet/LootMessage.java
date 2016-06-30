package org.icenet;

/**
 * Creature Sub-message sent when the loot screen should appear
 */
public class LootMessage extends SimulatorMessage {

	private String reason;

	public LootMessage(CreatureEventReplyMessage msg) {
		super(msg);
		reason = readString();
	}

	public String getReason() {
		return reason;
	}
}
