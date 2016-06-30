package org.icenet;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message sent back from server when friend status changes.
 */
public class StatusUpdateMessage extends SimulatorMessage {

    public enum Type {

        UNKNOWN, LOGGED_IN, LOGGED_OUT, STATUS_CHANGED, SHARD_CHANGED, FRIEND_ADDED;

        public static Type fromCode(short code) {
            switch (code) {
                case 1:
                    return LOGGED_IN;
                case 2:
                    return LOGGED_OUT;
                case 3:
                	return STATUS_CHANGED;
                case 4:
                	return FRIEND_ADDED;
                case 15:
                    return SHARD_CHANGED;
            }
            throw new IllegalArgumentException(String.format("Unknown status update type %s.", code));
        }
    }
    private static final Logger LOG = Logger.getLogger(StatusUpdateMessage.class.getName());
    private Type type;
    private final String name;
    private String status;
    private final short typeCode;

    public StatusUpdateMessage(SimulatorMessage msg) throws ParseException {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.GAME);
        payload.rewind();
        typeCode = readUnsignedByte();
        try {
            type = Type.fromCode(typeCode);
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.SEVERE, "Failed to parse status update.", iae);
            type = Type.UNKNOWN;
        }
        if(type == Type.SHARD_CHANGED || type == Type.STATUS_CHANGED) {
        	name = readString();
        	status = readString();
        }
        else {
        	name = readString();
        }
    }
    
    public String getStatus() {
    	return status;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public short getTypeCode() {
        return typeCode;
    }

	@Override
	public String toString() {
		return "StatusUpdateMessage [type=" + type + ", name=" + name + ", status=" + status + ", typeCode=" + typeCode + "]";
	}

}
