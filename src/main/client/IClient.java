package client;

import data.Listing;
import data.User;
import data.Message;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * IClient
 * <p>
 * Interface for the Client class.
 *
 * @author Chen
 * @version 4/13/25
 */
public interface IClient {

    User getUser();

    void close() throws IOException;

    boolean login(String username, String password);

    User createUser(String username, String password);

    Listing createListing(String title, String description, double price, String image);

    boolean buyListing(int listingId);

    boolean setUserBalance(double newBalance);

    boolean deleteUser();

    boolean deleteListing(int listingId);

    List<Listing> searchListingsByAttribute(String key, String value);

    boolean sendMessage(int toId, String body);

    List<Message> getMessagesWithUser(int otherUserId);

    List<User> getInboxUsers();

    String uploadImage(File file);

    File downloadImage(String hash);

    boolean isLoggedIn();
}
