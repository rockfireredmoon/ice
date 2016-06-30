package org.icenet;

/**
 * Creature Sub-message sent when creature changes in some way
 * 
 * TODO Not certain when this happens
 */
public class AvatarChangedMessage extends SimulatorMessage {

	public AvatarChangedMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}
}
