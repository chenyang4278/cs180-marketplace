import java.util.ArrayList;
public interface IUser {
    String getUsername();
    String getPassword();
    double getBalance();
    double getRating();
    ArrayList<Listing> getListings();
    ArrayList<Message> getInbox();

    void setPassword(String password);
    void setBalance(double balance);
    void setRating(double rating);
    void addListing(Listing item);
    void removeListing(Listing item);
    void sendMessage(Message message);
    void deleteAccount();
}
