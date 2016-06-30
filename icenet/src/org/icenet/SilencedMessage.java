package org.icenet;

/**
 * Creature Sub-message sent when a creature is silenced
 */
public class SilencedMessage extends SimulatorMessage {

	public SilencedMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}

}
