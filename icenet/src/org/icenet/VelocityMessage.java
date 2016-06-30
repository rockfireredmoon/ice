package org.icenet;

import org.icelib.Icelib;

/**
 * Creature Sub-message sent when creature velocity (or heading, or rotation)
 * changes
 */
public class VelocityMessage extends SimulatorMessage {

	private float heading;
	private float rotation;
	private int speed;

	public VelocityMessage(CreatureEventReplyMessage msg) {
		super(msg);
		heading = Icelib.rot2rad(readUnsignedByte());
		rotation = Icelib.rot2rad(readUnsignedByte());
		speed = readUnsignedByte() & 0xff;
	}

	public float getHeading() {
		return heading;
	}

	public float getRotation() {
		return rotation;
	}

	public int getSpeed() {
		return speed;
	}
}
