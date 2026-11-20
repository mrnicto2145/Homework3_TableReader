package reader.exception;

/**
 * Signals that a directory file could not be read or has a broken structure.
 */
public class AddressParseException extends Exception {

    /**
     * Creates an exception with a message and the underlying cause.
     *
     * @param message description of the problem
     * @param cause   original exception
     */
    public AddressParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
