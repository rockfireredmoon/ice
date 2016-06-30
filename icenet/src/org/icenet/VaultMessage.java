package org.icenet;

/**
 * Creature Sub-message sent when a creature opens their vault
 */
public class VaultMessage extends SimulatorMessage {

	public VaultMessage(CreatureEventReplyMessage msg) {
		super(msg);
	}

}
