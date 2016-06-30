package org.icenet;

/**
 * Creature Sub-message sent when a countdown timer starts
 */
public class CountdownMessage extends SimulatorMessage {

	private float delay;
	private String name;

	public CountdownMessage(CreatureEventReplyMessage msg) {
		super(msg);
		name = readString();
		delay = readFloat();
	}

	public float getDelay() {
		return delay;
	}

	public String getName() {
		return name;
	}
}
