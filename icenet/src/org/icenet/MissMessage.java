package org.icenet;

/**
 * Creature Sub-message sent when creature misses (no longer used)
 */
public class MissMessage extends SimulatorMessage {

	public MissMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}
}
