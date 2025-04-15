package data;

public interface ISession {
    int getUserId();
    String getToken();
    void setUserId(String userId);
    void setToken(String token);
}
