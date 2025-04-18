package client;

import packet.PacketHeader;
import packet.response.SuccessPacket;
import packet.ErrorPacketException;
import packet.PacketParsingException;
import data.Table;
import data.Listing;
import data.User;
import data.Message;

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

    int getCurrentUserId();

    void setCurrentUserId(int id);

    boolean login(String username, String password);

    boolean createUser(String username, String password);

    Listing createListing(String title, String description, double price, String image);

    boolean buyListing(int listingId);

    boolean setUserBalance(double newBalance);

    boolean deleteUser();

    boolean deleteListing(int listingId);

    List<Listing> searchListingsByAttribute(String key, String value);

    boolean sendMessage(int fromId, int toId, String body);

    List<Message> getMessagesWithUser(int otherUserId);
}
