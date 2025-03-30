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
    void createListing(Listing item);
    void removeListing(Listing item);
    void sendMessage(String messageContent, int receiverId)
    void deleteAccount();
}
