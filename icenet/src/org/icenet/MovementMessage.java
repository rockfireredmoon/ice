package org.icenet;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Message sent to server when player moves. It encodes postion, rotation,
 * movement type (forward, backward, stop) and look direction (i think)
 */
public class MovementMessage extends SimulatorMessage {
	private static final Logger LOG = Logger.getLogger(MovementMessage.class.getName());

	/**
	 * Construct new movement message.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 * @param headingDeg
	 *            (in degrees)
	 * @param rotDeg
	 *            rotation (in degrees, 0 pointing south, anticlockwise)
	 * @param speed
	 *            movement speed
	 */
	public MovementMessage(int x, int y, int z, int headingDeg, int rotDeg, short speed) {
		super(MSG_MOVEMENT);
		if (speed > 255) {
			throw new IllegalArgumentException("Speed must be <= 255");
		}
		setValidForProtocol(Simulator.ProtocolState.GAME);
		payload = ByteBuffer.allocate(9);
		payload.putShort((short) x);
		payload.putShort((short) z);
		payload.putShort((short) y);
		final int moveAmt = (int) ((255f / 360f) * (float) headingDeg);
		payload.put((byte) (moveAmt));
		final int rotAmt = (int) ((255f / 360f) * (float) rotDeg);
		payload.put((byte) (rotAmt));
		payload.put((byte) speed);
		payload.flip();
	}

}
