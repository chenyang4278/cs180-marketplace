package packet;

/**
 * ErrorPacketException
 * <p>
 * Thrown when an error packet is sent.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class ErrorPacketException extends Exception {
    public ErrorPacketException(String message) {
        super(message);
    }
}
