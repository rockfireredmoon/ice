package org.icenet;


/**
 * Not a real message, it is the first thing queued, so the 
 * client will expect a {@link HeloReplyMessage} in reply.
 *
 */
public class ConnectMessage extends SimulatorMessage {

    public ConnectMessage() {
        super(null);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        setWantsReply(true);
    }

	@Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof HeloReplyMessage;
    }
}