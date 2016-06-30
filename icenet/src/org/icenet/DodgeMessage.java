package org.icenet;

/**
 * Creature Sub-message sent when a creature dodges
 */
public class DodgeMessage extends SimulatorMessage {
	
	private long attackerId;

	public DodgeMessage(CreatureEventReplyMessage msg) {
		super(msg);
		attackerId = readUnsignedInt();
	}

	public long getAttackerId() {
		return attackerId;
	}

}
