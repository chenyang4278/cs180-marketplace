package data;

/**
 * Session
 * <p>
 * A class that holds data about a session for a user that is logged in.
 * Tokens are used to verify packets are from a given user (checked with the userId field).
 * Just has getters and setters and is savable to the database.
 *
 * @author Ayden Cline
 * @version 4/14/25
 */
public class Session extends Table implements ISession {
    @TableField(field = "user_id", index = 1)
    private int userId;

    @TableField(field = "token", index = 2)
    private String token;

    // Required for Table
    public Session() {
    }

    public Session(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
