package org.icenet;

import java.nio.ByteBuffer;

/**
 * Message sent indicating the player has started a jump.
 */
public class PlayerJumpMessage extends SimulatorMessage {

    public PlayerJumpMessage() {
        super(MSG_PLAYER_JUMP);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload = ByteBuffer.allocate(4);

        // TODO what does this mean?
        payload.putShort((short) 0x7fff);
        payload.putShort((short) 0);

        payload.flip();
    }
}
