package org.icenet;

/**
 * Error message sent from server to client (e.g. login error).
 */
public class LobbyErrorMessage extends SimulatorMessage {

    private final String message;

    public LobbyErrorMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        payload.rewind();
        message = readString();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LobbyErrorMessage{" + "message=" + message + '}';
    }
}
