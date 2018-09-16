package org.icenet;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class ItemQueryMessage extends SimulatorMessage {
    private static final Logger LOG = Logger.getLogger(ItemQueryMessage.class.getName());
    private final long itemId;

    public ItemQueryMessage(long abilityId) {
        super(MSG_ITEM_QUERY);
        setWantsReply(true);
        this.itemId = abilityId;
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload = ByteBuffer.allocate(4);
        payload.putInt((int) abilityId);
        payload.flip();
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof ItemQueryReplyMessage && ((ItemQueryReplyMessage) msg).getId() == itemId;
    }

    @Override
    public String toString() {
        return "ItemQueryMessage{" + "abilityId=" + itemId + '}';
    }
}
