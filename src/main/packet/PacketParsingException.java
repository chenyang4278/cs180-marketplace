package packet;

/**
 * PacketParsingException
 * Thrown when a packet is formatted incorrectly.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class PacketParsingException extends Exception {
    public PacketParsingException(String message) {
        super(message);
    }
}
