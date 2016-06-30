package org.icenet;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class RequestSpawnUpdateMessage extends SimulatorMessage {
    private static final Logger LOG = Logger.getLogger(RequestSpawnUpdateMessage.class.getName());
    private final long id;

    public RequestSpawnUpdateMessage(long id) {
        super(MSG_REQUEST_SPAWN_UPDATE);
//        setWantsReply(true);
        this.id = id;
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload = ByteBuffer.allocate(4);
        payload.putInt((int) id);
        payload.flip();
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof SpawnUpdateMessage && ((SpawnUpdateMessage) msg).getId() == id;
    }

    @Override
    public String toString() {
        return "RequestSpawnUpdateMessage{" + "id=" + id + '}';
    }


}
