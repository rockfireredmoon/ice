package org.icenet;

/**
 * Creature Sub-message sent when a creature receives a heal
 */
public class CooldownMessage extends SimulatorMessage {

	private String category;

	public CooldownMessage(Simulator sim, CreatureEventReplyMessage msg) {
		super(msg);
		category = readString();
	}

	public String getCategoryt() {
		return category;
	}

}
