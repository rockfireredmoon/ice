package org.icenet;

/**
 * Creature Sub-message sent when creature logs out
 */
public class LogoutMessage extends SimulatorMessage {

	public LogoutMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}
}
