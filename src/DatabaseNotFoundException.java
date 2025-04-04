/** 
 * DatabaseNotFoundException Class. An exception that handles a missing database.
 * 
 * @author Karma Luitel, lab L24
 * @version 3/27/25
*/
public class DatabaseNotFoundException extends Exception {
    public DatabaseNotFoundException(String message) {
        super(message);
    }
}
