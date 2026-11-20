package reader.exception;

/**
 * Signals that no parser is available for the format of the given file.
 */
public class UnsupportedFormatException extends Exception {

    /**
     * Creates an exception with a message.
     *
     * @param message description of the problem
     */
    public UnsupportedFormatException(String message) {
        super(message);
    }
}
