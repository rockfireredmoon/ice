package org.icenet;

/**
 * Creature Sub-message sent when creature parries
 */
public class ParryMessage extends SimulatorMessage {

	public ParryMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}
}
