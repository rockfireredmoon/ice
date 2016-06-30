package org.icenet;

/**
 * Creature Sub-message sent when a creature is disarmed
 */
public class DisarmedMessage extends SimulatorMessage {

	public DisarmedMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}

}
