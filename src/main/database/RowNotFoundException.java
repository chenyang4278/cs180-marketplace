package main.database;

/**
 * RowNotFoundException
 * <p>
 * A class that handles a row not found error for the database.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class RowNotFoundException extends Exception {
    public RowNotFoundException(String message) {
        super(message);
    }
}
