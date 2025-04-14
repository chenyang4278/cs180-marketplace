package data;

import java.util.ArrayList;
/**
 * IUser
 * <p>
 * interface for user class
 *
 * @author Chen Yang, section 24
 * @version 3/28/25
 */
public interface IUser {
    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    double getBalance();

    void setBalance(double balance);

    double getRating();

    void setRating(double rating);

    ArrayList<Listing> getListings();

    ArrayList<Message> getInbox();

    void setInbox(ArrayList<Message> inbox);

    void createListing(Listing item);

    void removeListing(Listing item);

    void sendMessage(String messageContent, int receiverId);

    void removeMessage(Message message);

    void updateBalance(double amount);

    void updateRating(double rating);

    void deleteAccount();
}
