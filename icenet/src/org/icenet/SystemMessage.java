package org.icenet;

/**
 */
public class SystemMessage extends SimulatorMessage {

    private final String message;

    public SystemMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload.rewind();
        message = readString();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SystemMessage{" + "message=" + message + '}';
    }
}
