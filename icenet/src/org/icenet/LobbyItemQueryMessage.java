package org.icenet;

import java.nio.ByteBuffer;

public class LobbyItemQueryMessage extends SimulatorMessage {

    private final long itemId;

    public LobbyItemQueryMessage(long itemId) {
        super(MSG_ITEM_APPEARANCE);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        this.itemId = itemId;
        setWantsReply(true);
        payload = ByteBuffer.allocate(4);
        payload.putInt((int) itemId);
        payload.flip();
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof ItemQueryReplyMessage && ((ItemQueryReplyMessage) msg).getId() == itemId;
    }
}
