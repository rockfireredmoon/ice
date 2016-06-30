package org.icenet;

/**
 * Creature Sub-message sent when creature reflects damage
 */
public class ReflectMessage extends SimulatorMessage {

	private long reflector;
	private String damageString;
	private short absorbedDamage;

	public ReflectMessage(CreatureEventReplyMessage msg) {
		super(msg);
		reflector = readUnsignedInt();
		damageString = readString();
		absorbedDamage = readUnsignedByte();
	}

	public long getReflector() {
		return reflector;
	}

	public String getDamageString() {
		return damageString;
	}

	public short getAbsorbedDamage() {
		return absorbedDamage;
	}

}
