package org.icenet;

/**
 * Creature Sub-message sent when creatures receives damage
 */
public class DamageMessage extends SimulatorMessage {

	private String damageString;
	private String abilityName;
	private boolean criticalHit;
	private long absorbedDamage;
	
	public DamageMessage(Simulator sim, CreatureEventReplyMessage msg) {
		super(msg);
		damageString = readString();
		abilityName = readString();
		criticalHit = readBoolean();
		if(sim.getProtocolVersion() >= 21) {
			absorbedDamage = readUnsignedInt();
		}
	}

	public String getDamageString() {
		return damageString;
	}

	public String getAbilityName() {
		return abilityName;
	}

	public boolean isCriticalHit() {
		return criticalHit;
	}

}
