package pl.chillcode.logs.exception;

public final class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(final String message) {
        super(message);
    }
}
