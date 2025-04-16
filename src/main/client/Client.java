package client;

import packet.*;
import packet.response.*;

import java.io.*;
import java.net.*;
import java.util.*;
import data.Table;
import data.Listing;
import data.User;

//will add other functions tmr
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
    private String sessionToken;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        oStream = socket.getOutputStream();
        iStream = socket.getInputStream();
        sessionToken = "";
    }

    private User sendLoginPacketRequest(List<PacketHeader> headers)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet("/login", headers);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        sessionToken = response.getHeaderValues("Session-Token")[0];
        ObjectPacket<User> o = (ObjectPacket<User>) response;
        return o.getObj();
    }

    private <T extends Table> T sendObjectPacketRequest(String path, List<PacketHeader> headers, Class<T> type)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        ObjectPacket<T> o = (ObjectPacket<T>) response;
        return o.getObj();
    }

    private <T extends Table> List<T> sendObjectListPacketRequest(String path, List<PacketHeader> headers, Class<T> type)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        ObjectListPacket<T> o = (ObjectListPacket<T>) response;
        return o.getObjList();
    }

    private SuccessPacket sendSuccessPacketRequest(String path, List<PacketHeader> headers)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        return (SuccessPacket) response;
    }

    private void close() throws IOException {
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
            User user = sendLoginPacketRequest(headers);
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
            User user = sendObjectPacketRequest("/user/create", headers, User.class);
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
            return sendObjectPacketRequest("/listing/create", headers, Listing.class);
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
            User updatedUser = sendObjectPacketRequest("/buy", headers, User.class);
            this.currentUser = updatedUser;
            return true;
        } catch (Exception e) {
            System.out.println("Purchase failed: " + e.getMessage());
            return false;
        }
    }

    public boolean setUserBalance(double newBalance) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "userId", String.valueOf(currentUser.getId()),
                    "attribute", "balance",
                    "attributeVal", String.valueOf(newBalance)
            );
            User updatedUser = sendObjectPacketRequest("/user/edit", headers, User.class);
            this.currentUser = updatedUser;
            return true;
        } catch (Exception e) {
            System.out.println("Balance update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser() {
        try {
            ArrayList<PacketHeader> headers = new ArrayList<>();
            User updatedUser = sendObjectPacketRequest("/user/" + currentUser.getId(), headers, User.class);
            this.currentUser = updatedUser;
            return true;
        } catch (Exception e) {
            System.out.println("User update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser() {
        try {
            List<PacketHeader> headers = createHeaders("userId", String.valueOf(currentUser.getId()));
            sendSuccessPacketRequest("/user/delete", headers);
            this.currentUser = null;
            return true;
        } catch (Exception e) {
            System.out.println("Account deletion failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteListing(int listingId) {
        try {
            List<PacketHeader> headers = createHeaders("listingId", String.valueOf(listingId));
            sendSuccessPacketRequest("/listing/delete", headers);
            return true;
        } catch (Exception e) {
            System.out.println("Listing deletion failed: " + e.getMessage());
            return false;
        }
    }

    public List<Listing> searchListingsByAttribute(String key, String value) {
        try {
            List<PacketHeader> headers = createHeaders(key, value);
            return sendObjectListPacketRequest("/listing/search", headers, Listing.class);
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
