package org.icenet;

import java.nio.ByteBuffer;

public class ChatOutgoingMessage extends SimulatorMessage {

    public ChatOutgoingMessage(String channel, String param, String message) {
        super(MSG_CHAT_OUT);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload = ByteBuffer.allocate(255);
        String chName = channel;
        if (param != null) {
            chName += param;
        }
        writeString(chName);
        writeString(message);
        payload.flip();
    }
}
