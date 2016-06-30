package org.icenet;

/**
 * Creature Sub-message sent when creatures target changes.
 */
public class TargetMessage extends SimulatorMessage {

	private long targetId;

	public TargetMessage(CreatureEventReplyMessage msg) {
		super(msg);
		targetId = readUnsignedInt();
	}

	public long getTargetId() {
		return targetId;
	}
}
