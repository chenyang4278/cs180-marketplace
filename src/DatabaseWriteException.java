/**
 * DatabaseWriteException
 * <p>
 * An exception that handles a problematic database write.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class DatabaseWriteException extends Exception {
    public DatabaseWriteException(String message) {
        super(message);
    }
}
