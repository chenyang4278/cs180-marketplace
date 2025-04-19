package data;

/**
 * ISession
 * <p>
 * An interface for a Session class.
 *
 * @author Ayden Cline
 * @version 4/14/25
 */
public interface ISession {
    int getUserId();
    String getToken();
    void setUserId(int userId);
    void setToken(String token);
}
