package org.icenet;

/**
 * Creature Sub-message sent when creature buys ability point
 */
public class AbilityPointBoughtMessage extends SimulatorMessage {

	public AbilityPointBoughtMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}
}
