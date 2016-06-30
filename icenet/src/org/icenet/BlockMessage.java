package org.icenet;

/**
 * Creature Sub-message sent when a creature blocks
 */
public class BlockMessage extends SimulatorMessage {

	public BlockMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}

}
