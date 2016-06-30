package org.icenet;

public class NetworkException extends RuntimeException {

    public enum ErrorType {

        NOT_ENOUGH_INVENTORY_SPACE,
        INCORRECT_USERNAME_OR_PASSWORD,
        GENERAL_NETWORK_ERROR,
        OBJECT_NOT_FOUND,
        NAME_TAKEN, INCOMPATIBLE_ITEMS, INTERRUPTED_IO, TIMEOUT, FAILED_TO_CONNECT_TO_ROUTER, FAILED_TO_CONNECT_TO_SIMULATOR, PARSING_ERROR, SERVER_DISCONNECTED,
        AUTHENTICATION_SERICE_ERROR
    }
    private final ErrorType type;

    public NetworkException(ErrorType type) {
        this.type = type;
    }

    public NetworkException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    public NetworkException(ErrorType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public NetworkException(ErrorType type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public String getFullMessage() {
        return type + ":" + super.getMessage();
    }

    public ErrorType getType() {
        return type;
    }
}
