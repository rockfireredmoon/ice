package org.icenet;

/**
 * Creature Sub-message sent when a creature requests a portal
 */
public class PortalRequestMessage extends SimulatorMessage {

	private String teleporter;
	private String destination;

	public PortalRequestMessage(Simulator sim, CreatureEventReplyMessage msg) {
		super(msg);
		teleporter = readString();
		destination = readString();
	}

	public String getTeleporter() {
		return teleporter;
	}

	public String getDestination() {
		return destination;
	}

}
