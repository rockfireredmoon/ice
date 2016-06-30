package org.icenet;

/**
 * Creature Sub-message sent when a spell fails
 */
public class SpellFailMessage extends SimulatorMessage {

	public SpellFailMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}

}
