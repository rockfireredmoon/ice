package org.icenet;

/**
 * First message sent from server to client and contains the hash salt.
 */
public class HeloReplyMessage extends SimulatorMessage {

    private final String authData;

    public enum AuthenticationMode {

        EXTERNAL(0), DEV(1), SERVICE(2);
        private final long val;

        AuthenticationMode(long val) {
            this.val = val;
        }

        public static AuthenticationMode fromProtocolCode(long protocolCode) {
            for (AuthenticationMode a : values()) {
                if (a.val == protocolCode) {
                    return a;
                }
            }
            throw new IllegalArgumentException("Unknown protocol code.");
        }
    }
    private final AuthenticationMode authenticationMode;
    private long protocolVersion;

    public HeloReplyMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(Simulator.ProtocolState.LOBBY);
        payload.rewind();
        protocolVersion = payload.getInt() & 0xffff;
        authenticationMode = AuthenticationMode.fromProtocolCode(payload.getInt() & 0xffff);

        // The auth data
        authData = readString();

    }

    public AuthenticationMode getAuthenticationMode() {
        return authenticationMode;
    }

    public long getProtocolVersion() {
        return protocolVersion;
    }

    public String getAuthData() {
        return authData;
    }

    @Override
    public String toString() {
        return "HeloReplyMessage{" + "authSalt=" + authData + ", authenticationMode=" + authenticationMode + ", protocolVersion=" + protocolVersion + '}';
    }
}
