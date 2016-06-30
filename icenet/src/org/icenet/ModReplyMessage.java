package org.icenet;

/**
 * Messages added by Greth for modded clients.
 */
public class ModReplyMessage extends SimulatorMessage {


	//const int MODMESSAGE_EVENT_SUPERCRIT = 1;
	//const int MODMESSAGE_EVENT_EMOTE = 2;
	//const int MODMESSAGE_EVENT_EMOTE_CONTROL = 3;
	//const int MODMESSAGE_EVENT_PING_START = 20;
	//const int MODMESSAGE_EVENT_PING_STOP = 21;
	//const int MODMESSAGE_EVENT_PING_QUERY = 22;
	//const int MODMESSAGE_EVENT_GENERIC_REQUEST = 23;
	//const int MODMESSAGE_EVENT_POPUP_MSG = 30;
	//const int MODMESSAGE_EVENT_STOP_SWIM = 40;
	
	public enum Op {
		SUPERCRIT(1), EMOTE(2), EMOTE_CONTROL(3), PING_START(20), PING_STOP(21), PING_QUERY(22), GENERIC(23), POPUP(30), STOP_SWIM(40);

		private int code;

		Op(int code) {
			this.code = code;
		}

		public int toCode() {
			return code;
		}

		public static Op fromCode(int code) {
			switch (code) {
			case 1:
				return SUPERCRIT;
			case 2:
				return EMOTE;
			case 3:
				return EMOTE_CONTROL;
			case 20:
				return PING_STOP;
			case 21:
				return PING_STOP;
			case 22:
				return PING_QUERY;
			case 23:
				return GENERIC;
			case 30:
				return POPUP;
			case 40:
				return STOP_SWIM;
			}
			throw new IllegalArgumentException("Unknown code " + code);
		}
	}

	private Op op;
	private String message;

	public ModReplyMessage(SimulatorMessage msg) {
		super(msg);
//		setValidForProtocol(Simulator.ProtocolState.LOBBY);
		payload.rewind();
		op = Op.fromCode(readUnsignedByte());
		switch(op) {
		case POPUP:
			message = readString();
			break;
		}
	}
	
	public String getMessage() {
		return message;
	}

	public Op getOp() {
		return op;
	}

	@Override
	public String toString() {
		return "ModReplyMessage [op=" + op + ", message=" + message + "]";
	}

}
