package org.icenet;

public class PingFromServerMessage extends SimulatorMessage {
    private final long value;

    public PingFromServerMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload.rewind();
        value = readUnsignedInt();
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PingFromServer{" + "value=" + value + '}';
    }
    
}
