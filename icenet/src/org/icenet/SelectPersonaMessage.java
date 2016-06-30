package org.icenet;

import java.nio.ByteBuffer;

public class SelectPersonaMessage extends SimulatorMessage {

    public SelectPersonaMessage(Simulator sim, short index) {
        super(MSG_SELECT);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        payload = ByteBuffer.allocate(255);
        payload.putShort(index); 
        if(sim.getProtocolVersion() >= 38) {
        	// Unknown things (SimulatorThread :: handle_lobby_selectPersona)
        	payload.putInt(0);
        	payload.putInt(0);
        }
        payload.flip();
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof ProtocolChangeReply;
    }
}
