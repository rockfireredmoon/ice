package org.icenet;

import java.util.logging.Logger;

/**
 * Sent in reply to ping from server?
 */
public class PongMessage extends SimulatorMessage {
    private static final Logger LOG = Logger.getLogger(PongMessage.class.getName());

    public PongMessage() {
        super(SimulatorMessage.MSG_PONG);
    }

    @Override
    public String toString() {
        return "PongMessage{" + '}';
    }

}
