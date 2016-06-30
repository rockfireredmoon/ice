package org.icenet;

import java.text.ParseException;
import java.util.logging.Logger;

public class JumpMessage extends SimulatorMessage {

    private static final Logger LOG = Logger.getLogger(JumpMessage.class.getName());
    private final long id;
    private final long val1;
    private final long val2;
    private final long val3;

    public JumpMessage(SimulatorMessage msg) throws ParseException {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload.rewind();
        id = readUnsignedInt();
        val1 = readUnsignedInt();
        val2 = readUnsignedInt();
        val3 = readUnsignedInt();
        if (payload.remaining() > 0) {
            LOG.warning(String.format("%d remaining bytes for %s", payload.remaining(), this));
        }
    }

    @Override
    public String toString() {
        return "JumpMessage{" + "id=" + id + ", val1=" + val1 + ", val2=" + val2 + ", val3=" + val3 + '}';
    }
}
