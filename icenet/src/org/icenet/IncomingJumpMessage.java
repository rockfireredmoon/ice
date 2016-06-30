package org.icenet;

/**
 * Creature Sub-message sent when creature jumps
 */
public class IncomingJumpMessage extends SimulatorMessage {

	public IncomingJumpMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}
}
