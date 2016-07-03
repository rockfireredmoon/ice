package org.icenet;

import java.nio.ByteBuffer;

public class TokenLoginMessage extends SimulatorMessage {

	public TokenLoginMessage(String username, String token) {
		super(MSG_LOGIN);
		setValidForProtocol(Simulator.ProtocolState.LOBBY);
		setWantsReply(true);
		payload = ByteBuffer.allocate(255);
		payload.put((byte) 2); // Always 1?
		writeString(username);
		writeString(token);
		payload.flip();
	}

	@Override
	public boolean isReply(SimulatorMessage msg) {
		return msg instanceof LoginOkMessage || msg instanceof LobbyErrorMessage;
	}
}
