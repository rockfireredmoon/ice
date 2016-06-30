package org.icenet;

import java.nio.ByteBuffer;

import org.icelib.Hash;

public class LoginMessage extends SimulatorMessage {

    public LoginMessage(String username, char[] password, String salt) {
        super(MSG_LOGIN);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        setWantsReply(true);
        payload = ByteBuffer.allocate(255);
        payload.put((byte) 1); // Always 1?
        writeString(username);
        writeString(Hash.hash(new String(password), username, salt));
        payload.flip();
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof LoginOkMessage || msg instanceof LobbyErrorMessage;
    }
}
