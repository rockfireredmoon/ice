package org.icenet;

/**
 * Ok message sent from server to client (e.g. login success).
 */
public class LoginOkMessage extends SimulatorMessage {

    private final long value;

    public LoginOkMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        payload.rewind();
        value = readUnsignedInt();
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LoginOkMessage{" + "value=" + value + '}';
    }
}
