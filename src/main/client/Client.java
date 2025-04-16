package client;

import packet.*;
import packet.response.*;

import java.io.*;
import java.net.*;
import java.util.*;
import data.Table;
import data.Listing;
import data.User;
import data.Message;

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
public class Client implements IClient {
    private Socket socket;
    private OutputStream oStream;
    private InputStream iStream;
    private int currentUserId;
    private String sessionToken;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        oStream = socket.getOutputStream();
        iStream = socket.getInputStream();
        sessionToken = "";
        currentUserId = -1;
    }

    public User getUser() {
        try {
            ArrayList<PacketHeader> headers = new ArrayList<>();
            return sendObjectPacketRequest("/user/" + currentUserId, headers, User.class);
        } catch (Exception e) {
            System.out.println("User update failed: " + e.getMessage());
            return null;
        }
    }

    public User sendLoginPacketRequest(List<PacketHeader> headers)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet("/login", headers);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        sessionToken = response.getHeaderValues("Session-Token")[0];
        ObjectPacket<User> o = (ObjectPacket<User>) response;
        return o.getObj();
    }

    public <T extends Table> T sendObjectPacketRequest(String path, List<PacketHeader> headers, Class<T> type)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        ObjectPacket<T> o = (ObjectPacket<T>) response;
        return o.getObj();
    }

    public <T extends Table> List<T> sendObjectListPacketRequest(String path, List<PacketHeader> headers, Class<T> type)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        ObjectListPacket<T> o = (ObjectListPacket<T>) response;
        return o.getObjList();
    }

    public SuccessPacket sendSuccessPacketRequest(String path, List<PacketHeader> headers)
            throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        return (SuccessPacket) response;
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

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int id) {
        this.currentUserId = id;
    }

    public boolean login(String username, String password) {
        try {
            List<PacketHeader> headers = createHeaders("username", username, "password", password);
            User user = sendLoginPacketRequest(headers);
            this.currentUserId = user.getId();
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
            this.currentUserId = user.getId();
            return true;
        } catch (Exception e) {
            System.out.println("User creation failed: " + e.getMessage());
            return false;
        }
    }

    public Listing createListing(String title, String description, double price, String image) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "username", getUser().getUsername(),
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
                    "buyingId", String.valueOf(currentUserId),
                    "listingId", String.valueOf(listingId)
            );
            sendObjectPacketRequest("/buy", headers, User.class);
            return true;
        } catch (Exception e) {
            System.out.println("Purchase failed: " + e.getMessage());
            return false;
        }
    }

    public boolean setUserBalance(double newBalance) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "userId", String.valueOf(currentUserId),
                    "attribute", "balance",
                    "attributeVal", String.valueOf(newBalance)
            );
            sendObjectPacketRequest("/user/edit", headers, User.class);
            return true;
        } catch (Exception e) {
            System.out.println("Balance update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser() {
        try {
            List<PacketHeader> headers = createHeaders("userId", String.valueOf(currentUserId));
            sendSuccessPacketRequest("/user/delete", headers);
            this.currentUserId = -1;
            this.sessionToken = "";
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
            List<PacketHeader> headers = createHeaders(
                    "attribute", key,
                    "attributeVal", value
            );
            return sendObjectListPacketRequest("/listings/attribute", headers, Listing.class);
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean sendMessage(int fromId, int toId, String body) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "senderId", String.valueOf(fromId),
                    "receiverId", String.valueOf(toId),
                    "message", body
            );
            sendSuccessPacketRequest("/message/create", headers);
            return true;
        } catch (Exception e) {
            System.out.println("Send message failed: " + e.getMessage());
            return false;
        }
    }

    public List<Message> getMessagesWithUser(int otherUserId) {
        try {
            List<PacketHeader> headersForSToR = createHeaders(
                    "senderId", String.valueOf(currentUserId),
                    "receiverId", String.valueOf(otherUserId)
            );
            List<PacketHeader> headersForRToS = createHeaders(
                    "senderId", String.valueOf(otherUserId),
                    "receiverId", String.valueOf(currentUserId)
            );
            /* Call database twice because we need to get messages sent by user1 to user 2
            * AND messages from user2 to user1. List of messages are appended together and returned, gui
            * can parse message object (contains receiver and sender ids, and time data) for display purposes.
             */
            List<Message> sToR=  (List<Message>) sendObjectListPacketRequest("/get/messages", headersForSToR, Message.class);
            List<Message> rToS=  (List<Message>) sendObjectListPacketRequest("/get/messages", headersForRToS, Message.class);
            sToR.addAll(rToS);
            return sToR;
        } catch (Exception e) {
            System.out.println("Message retrieval failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
