package data;

public interface ISession {
    int getUserId();
    String getToken();
    void setUserId(int userId);
    void setToken(String token);
}
