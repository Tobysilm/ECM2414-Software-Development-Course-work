
/**
 * Exception is thown when a card pack file is invalid,
 * this is a checked exeption the must be thrown.
 */

public class InvalidPackException extends Exception {

    /**
     * creates a InvalidPackExeption with the given message.
     * 
     * @param message is describing what was wrong about the pack 
     */

    public InvalidPackException(String message) {
        super(message);
    }

    // creates a InvalidPackExeption with the given message and cause. 

    public InvalidPackException(String message, Throwable cause) {
        super(message, cause);
    }
}