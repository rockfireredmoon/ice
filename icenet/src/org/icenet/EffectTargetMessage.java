package org.icenet;

/**
 * Creature Sub-message sent when an effect should be applied to a target
 */
public class EffectTargetMessage extends SimulatorMessage {

	private long targetId;
	private String effect;

	public EffectTargetMessage(CreatureEventReplyMessage msg) {
		super(msg);
		effect = readString();
		targetId = readUnsignedInt();
	}

	public long getTargetId() {
		return targetId;
	}

	public String getEffect() {
		return effect;
	}
}
