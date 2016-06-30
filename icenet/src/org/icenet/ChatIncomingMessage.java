package org.icenet;

/**
 * Chat message
 */
public class ChatIncomingMessage extends SimulatorMessage {

    private final long value;
    private final String sender;
    private final String channel;
    private final String message;

    public ChatIncomingMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload.rewind();
        value = readUnsignedInt();
        sender = readString();
        channel = readString();
        message = readString();
    }

    public String getSender() {
        return sender;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "value=" + value + ", sender=" + sender + ", channel=" + channel + ", message=" + message + '}';
    }

}
