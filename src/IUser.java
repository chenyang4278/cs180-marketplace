import java.util.ArrayList;
public interface IUser {
    String getUsername();
    String getPassword();
    double getBalance();
    double getRating();
    ArrayList<String> getListings();
    ArrayList<String> getInbox();

    void setPassword(String password);
    void setBalance(double balance);
    void setRating(double rating);
    void addListing(String item);
    void removeListing(String item);
    void sendMessage(String message);
    void deleteAccount();
}
