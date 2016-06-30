package org.icenet;

import org.icenet.Simulator.ProtocolState;

/**
 * Message sent in reply to {@link SelectPersonaMessage}.
 */
public class ProtocolChangeReply extends SimulatorMessage {

	private ProtocolState newProtocol;

	public ProtocolChangeReply(SimulatorMessage msg) {
		super(msg);
		setValidForProtocol(Simulator.ProtocolState.LOBBY);
		payload.rewind();
		switch (readUnsignedByte()) {
		case 0:
			newProtocol = ProtocolState.LOBBY;
			break;
		case 1:
			newProtocol = ProtocolState.GAME;
			break;
		default:
			throw new IllegalArgumentException("Unsupported mode");
		}

	}

	public ProtocolState getNewProtocol() {
		return newProtocol;
	}
}
