package client;

import packet.PacketHeader;
import packet.response.SuccessPacket;
import packet.ErrorPacketException;
import packet.PacketParsingException;
import data.Table;
import data.Listing;
import data.User;

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

    <T extends Table> T sendObjectPacketRequest(String path, List<PacketHeader> headers, Class<T> type)
            throws IOException, PacketParsingException, ErrorPacketException;

    <T extends Table> List<T> sendObjectListPacketRequest(String path, List<PacketHeader> headers, Class<T> type)
            throws IOException, PacketParsingException, ErrorPacketException;

    SuccessPacket sendSuccessPacketRequest(String path, List<PacketHeader> headers)
            throws IOException, PacketParsingException, ErrorPacketException;

    void close() throws IOException;

    static List<PacketHeader> createHeaders(String... keyValuePairs) {
        return Client.createHeaders(keyValuePairs);
    }

    User getCurrentUser();

    void setCurrentUser(User user);

    boolean login(String username, String password);

    boolean createUser(String username, String password);

    Listing createListing(String title, String description, double price, String image);

    boolean buyListing(int listingId);
}
