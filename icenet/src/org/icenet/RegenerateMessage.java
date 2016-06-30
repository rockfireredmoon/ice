package org.icenet;

/**
 * Creature Sub-message sent when creature regnerators
 */
public class RegenerateMessage extends SimulatorMessage {

	private long amountRegened;

	public RegenerateMessage(CreatureEventReplyMessage msg) {
		super(msg);
		amountRegened = readUnsignedInt();
	}

	public long getAmountRegened() {
		return amountRegened;
	}

}
