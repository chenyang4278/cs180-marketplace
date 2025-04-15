package data;

public class Session extends Table {
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
