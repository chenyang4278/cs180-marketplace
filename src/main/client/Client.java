package client;

import packet.*;
import packet.response.*;

import java.io.*;
import java.net.*;
import java.util.*;
import database.Table;
import database.Listing;
import database.User;


/**
 * Client
 * <p>
 * Handles sending and receiving packets to/from the server.
 * Does not store data locally; interacts with server for all data.
 *
 * @author Chen
 * @version 4/13/25
 */
public class Client {
    private Socket socket;
    private OutputStream oStream;
    private InputStream iStream;
    private User currentUser;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        oStream = socket.getOutputStream();
        iStream = socket.getInputStream();
    }

    public <T extends Table> T sendObjectPacketRequest(String path, List<PacketHeader> headers, Class<T> type) throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.write(oStream);
        ObjectPacket<T> response = Packet.read(iStream);
        return response.getObj();
    }

    public <T extends Table> List<T> sendObjectListPacketRequest(String path, List<PacketHeader> headers, Class<T> type) throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.write(oStream);
        ObjectListPacket<T> response = Packet.read(iStream);
        return response.getObjList();
    }

    public SuccessPacket sendSuccessPacketRequest(String path, List<PacketHeader> headers) throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.write(oStream);
        return Packet.read(iStream);
    }

    public void close() throws IOException {
        iStream.close();
        oStream.close();
        socket.close();
    }

    public static List<PacketHeader> createHeaders(String... keyValuePairs) {
        List<PacketHeader> headers = new ArrayList<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            headers.add(new PacketHeader(keyValuePairs[i], keyValuePairs[i + 1]));
        }
        return headers;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean login(String username, String password) {
        try {
            List<PacketHeader> headers = createHeaders("username", username, "password", password);
            User user = sendObjectPacketRequest("/login/", headers, User.class);
            this.currentUser = user;
            return true;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    public boolean createUser(String username, String password) {
        try {
            List<PacketHeader> headers = createHeaders("username", username, "password", password);
            User user = sendObjectPacketRequest("/usercreate/", headers, User.class);
            this.currentUser = user;
            return true;
        } catch (Exception e) {
            System.out.println("User creation failed: " + e.getMessage());
            return false;
        }
    }

    public Listing createListing(String title, String description, double price, String image) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "username", currentUser.getUsername(),
                    "title", title,
                    "description", description,
                    "price", String.valueOf(price),
                    "image", image
            );
            return sendObjectPacketRequest("/listingcreate/", headers, Listing.class);
        } catch (Exception e) {
            System.out.println("Listing creation failed: " + e.getMessage());
            return null;
        }
    }

    public boolean buyListing(int listingId) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "buyingId", String.valueOf(currentUser.getId()),
                    "listingId", String.valueOf(listingId)
            );
            User updatedUser = sendObjectPacketRequest("/buylisting/", headers, User.class);
            this.currentUser = updatedUser;
            return true;
        } catch (Exception e) {
            System.out.println("Purchase failed: " + e.getMessage());
            return false;
        }
    }
}
