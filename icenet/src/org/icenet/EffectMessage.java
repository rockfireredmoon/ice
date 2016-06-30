package org.icenet;

/**
 * Creature Sub-message sent when an effect should be applied to the creature
 */
public class EffectMessage extends SimulatorMessage {

	private String effect;

	public EffectMessage(CreatureEventReplyMessage msg) {
		super(msg);
		effect = readString();
	}

	public String getEffect() {
		return effect;
	}
}
