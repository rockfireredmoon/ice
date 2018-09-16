package org.icenet;

import java.nio.ByteBuffer;

public class SimulatorMessage {
    //
    // S -> C
    //

    /**
     * Sent on initial connection. Hex 0x28, Dec 40
     */
    public final static byte MSG_HELO = 0x28;
    /**
     * Sent on error (e.g. invalid credentials). Hex 0x00, Dec 0
     */
    public final static byte MSG_LOBBY_ERROR = 0x00;
    /**
     * Sent on OK (e.g. valid credentials). Hex 0x32, Dec 50
     */
    public final static byte MSG_LOGIN_OK = 0x32;
    /**
     * Sent on query reply. Hex 0x01, Dec 1
     */
    public final static byte MSG_QUERY_REPLY = 0x01;
    /**
     * Sent on item data reply. Hex 0x04, Dec 4
     */
    public final static byte MSG_LOBBY_ITEM_REPLY = 0x04;
    /**
     * Sent on successful MSG_SELECT. Hex 0xff, Dec 255
     */
    public final static byte MSG_PROTOCOL_CHANGE = (byte) 0xff;
    /**
     * A ping from server?
     */
    public final static byte MSG_PING = 0x5a;
    //--------------------------------------------------------------------
    //
    // Server -> Client Game mode
    //
    //--------------------------------------------------------------------
    /**
     * Incoming chat message
     */
    public final static byte MSG_CHAT_IN = 0x32;
    /**
     * Seems to be some kind of system message
     */
    public final static byte MSG_SYSTEM = 0x00;
    /**
     * Seems to be just a string reply mesage in game mode
     */
    public final static byte MSG_CREATURE_EVENT = 0x04;
    /**
     * Spawn update Hex 0x05, Dec 5
     */
    public final static byte MSG_SPAWN_UPDATE = 0x05;
    /**
     * Jump
     */
    public final static byte MSG_JUMP = 0x3c;
    /**
     * Set Map Hex 0x2a, Dec 42. 
     */
    public final static byte MSG_SET_MAP = (byte) 0x2a;
    /**
     * Environment change. Hex 0x2b. Dec 43
     */
    public final static byte MSG_STATUS_UPDATE = 0x2b;
    /**
     * Ability query reply. Hex 0x47.
     */
    public final static byte MSG_ABILITY_QUERY_REPLY = 0x47;
    /**
     * Inventory query reply. Hex 0x46.
     */
    public final static byte MSG_INVENTORY_QUERY_REPLY = 0x46;
    /**
     * Scenery query reply. Hex 0x29.
     */
    public final static byte MSG_SCENERY_QUERY_REPLY = 0x29;
    /**
     * Modified client messages (added by Greth?). Hex 0x64.
     */
    public final static byte MSG_MOD = 0x64;
    
    //--------------------------------------------------------------------
    //
    // Client -> Server Lobby mode
    //
    //--------------------------------------------------------------------
    /**
     * Login. Initial authenticate message
     */
    public final static byte MSG_LOGIN = 0x01;
    /**
     * Select persona. Used when player clicks "Play".
     */
    public final static byte MSG_SELECT = 0x02;
    /**
     * Query. Generic queries
     */
    public final static byte MSG_LOBBY_QUERY = 0x03;
    /**
     * Get item appearance data.
     */
    public final static byte MSG_ITEM_APPEARANCE = 0x04;
    /**
     * Player Jump
     */
    public final static byte MSG_PLAYER_JUMP = 0x06;
    //--------------------------------------------------------------------
    //
    // Client -> Server Game mode
    //
    //-------------------------------------------------------------------- 
    /**
     * Movement message
     */
    public final static byte MSG_MOVEMENT = 0x01;
    /**
     * Chat message
     */
    public final static byte MSG_CHAT_OUT = 0x04;
    /**
     * Query. Generic queries
     */
    public final static byte MSG_GAME_QUERY = 0x02;
    /**
     * Item query
     */
    public final static byte MSG_ITEM_QUERY = 0x09;
    /**
     * Request spawn update?
     */
    public final static byte MSG_REQUEST_SPAWN_UPDATE = 0x05;
    /**
     * Reply to ping?
     */
    public final static byte MSG_PONG = 0x14;
    
    protected byte code;
    protected ByteBuffer payload;
    protected boolean wantsReply;
    protected long sendTime;
    protected long replyTimeout = NetConstants.DEFAULT_MESSAGE_TIMEOUT;
    protected Simulator.ProtocolState protocol = Simulator.ProtocolState.LOBBY;

    protected SimulatorMessage(SimulatorMessage msg) {
        this.code = msg == null ? 0 : msg.code;
        this.payload = msg == null ? null : msg.payload;
    }

    public Simulator.ProtocolState getValidForProtocol() {
        return protocol;
    }

    public void setValidForProtocol(Simulator.ProtocolState protocol) {
        this.protocol = protocol;
    }

    public long getReplyTimeout() {
        return replyTimeout;
    }

    public void setReplyTimeout(long replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    public SimulatorMessage(byte code) {
        this.code = code;
    }

    public SimulatorMessage(byte code, int len) {
        this.code = code;
        payload = ByteBuffer.allocate(len);
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isWantsReply() {
        return wantsReply;
    }

    public void setWantsReply(boolean wantsReply) {
        this.wantsReply = wantsReply;
    }

    public boolean isReply(SimulatorMessage msg) {
        return false;
    }

    protected boolean onReply(SimulatorMessage reply) {
        if(!isWaitForReply()) {
            throw new IllegalStateException("If isWaitForReply() is false you must override onReply() and NOT call super");
        }
        // Returning falls indicates normal processing should continue (i.e. the reply
        // will be handled using {@link Simulator#sendAndAwaitReplies}.
        return false;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public int getLen() {
        return payload == null ? 0 : payload.limit();
    }

    public ByteBuffer getPayload() {
        return payload;
    }

    public void setPayload(ByteBuffer payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" + "code=" + code + ", len=" + getLen() + '}';
    }

    protected void writeString(String str) {
        payload.put((byte) str.length());
        payload.put(str.getBytes());
    }
    
    protected float readFloat() {
        return payload.getFloat();
    }

    protected long readUnsignedInt() {
        return (long) payload.getInt() & 0xffffffff;
    }

    protected int readUnsignedShort() {
        return (int) payload.getShort() & 0xffff;
    }
    
    protected void skip(int bytes) {
        payload.get(new byte[bytes]);
    }

    protected short readUnsignedByte() {
        return (short) (payload.get() & 0xff);
    }

    protected boolean readBoolean() {
        return readUnsignedByte() > 0;
    }

    protected String readString() {
        int w = payload.get() & 0xff;
        byte[] arr;
        if (w == 0xff) {
            w = payload.getShort();
        }
        arr = new byte[w];
        payload.get(arr);
        return new String(arr);
    }

    public boolean isWaitForReply() {
        return isWantsReply();
    }
}
