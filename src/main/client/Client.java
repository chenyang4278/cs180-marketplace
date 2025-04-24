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

import javax.swing.*;

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
    private boolean showErrors;

    public Client(String host, int port, boolean showErrors) throws IOException {
        socket = new Socket(host, port);
        oStream = socket.getOutputStream();
        iStream = socket.getInputStream();
        sessionToken = "";
        currentUserId = -1;
        this.showErrors = showErrors;

        // ensure tmp directory exists for image downloading
        new File("tmp").mkdir();
    }

    public User getUser() {
        if (currentUserId == -1) {
            return null;
        }

        try {
            ArrayList<PacketHeader> headers = new ArrayList<>();
            return sendObjectPacketRequest("/users/" + currentUserId, headers, User.class);
        } catch (Exception e) {
            System.out.println("User update failed: " + e.getMessage());
            showError(e.getMessage());
            return null;
        }
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
        if (headers == null) {
            headers = new ArrayList<>();
        }

        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        ObjectPacket<T> o = (ObjectPacket<T>) response;
        return o.getObj();
    }

    private <T extends Table> List<T> sendObjectListPacketRequest(String path,
            List<PacketHeader> headers, Class<T> type) throws IOException, PacketParsingException,
            ErrorPacketException {
        if (headers == null) {
            headers = new ArrayList<>();
        }

        Packet packet = new Packet(path, headers);
        packet.addHeader("Session-Token", sessionToken);
        packet.write(oStream);
        Packet response = Packet.read(iStream);
        ObjectListPacket<T> o = (ObjectListPacket<T>) response;
        return o.getObjList();
    }

    private SuccessPacket sendSuccessPacketRequest(String path, List<PacketHeader> headers)
            throws IOException, PacketParsingException, ErrorPacketException {
        if (headers == null) {
            headers = new ArrayList<>();
        }

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
            showError(e.getMessage());
            return false;
        }
    }

    public boolean createUser(String username, String password) {
        try {
            List<PacketHeader> headers = createHeaders("username", username, "password", password);
            User user = sendObjectPacketRequest("/users/create", headers, User.class);
            this.currentUserId = user.getId();
            return true;
        } catch (Exception e) {
            System.out.println("User creation failed: " + e.getMessage());
            showError(e.getMessage());
            return false;
        }
    }

    public Listing createListing(String title, String description, double price, String image) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "title", title,
                    "description", description,
                    "price", String.valueOf(price),
                    "image", image
            );
            return sendObjectPacketRequest("/listings/create", headers, Listing.class);
        } catch (Exception e) {
            System.out.println("Listing creation failed: " + e.getMessage());
            showError(e.getMessage());
            return null;
        }
    }

    public boolean buyListing(int listingId) {
        try {
            sendObjectPacketRequest("listings/" + listingId + "/buy", null, User.class);
            return true;
        } catch (Exception e) {
            System.out.println("Purchase failed: " + e.getMessage());
            showError(e.getMessage());
            return false;
        }
    }

    public boolean setUserBalance(double newBalance) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "attribute", "balance",
                    "attributeVal", String.valueOf(newBalance)
            );
            sendObjectPacketRequest("/users/" + currentUserId + "/edit", headers, User.class);
            return true;
        } catch (Exception e) {
            System.out.println("Balance update failed: " + e.getMessage());
            showError(e.getMessage());
            return false;
        }
    }

    public boolean deleteUser() {
        try {
            sendSuccessPacketRequest("/users/" + currentUserId + "/delete", null);
            this.currentUserId = -1;
            this.sessionToken = "";
            return true;
        } catch (Exception e) {
            System.out.println("Account deletion failed: " + e.getMessage());
            showError(e.getMessage());
            return false;
        }
    }

    public boolean deleteListing(int listingId) {
        try {
            sendSuccessPacketRequest("/listings/" + listingId + "/delete", null);
            return true;
        } catch (Exception e) {
            System.out.println("Listing deletion failed: " + e.getMessage());
            showError(e.getMessage());
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
            showError(e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean sendMessage(int toId, String body) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "receiverId", String.valueOf(toId),
                    "message", body
            );
            sendSuccessPacketRequest("/messages/create", headers);
            return true;
        } catch (Exception e) {
            System.out.println("Send message failed: " + e.getMessage());
            showError(e.getMessage());
            return false;
        }
    }

    public List<Message> getMessagesWithUser(int otherUserId) {
        try {
            List<PacketHeader> headers = createHeaders(
                    "otherUserId", String.valueOf(otherUserId)
            );
            return sendObjectListPacketRequest("/messages", headers, Message.class);
        } catch (Exception e) {
            System.out.println("Message retrieval failed: " + e.getMessage());
            showError(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns a hash that can be used to create a listing with an image
     */
    public String uploadImage(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            Packet packet = new Packet("/upload");
            packet.addHeader("Session-Token", sessionToken);
            packet.setBodyContinues(true);

            // write 1MiB at a time
            byte[] buf = new byte[1024 * 1024];
            int count;
            while ((count = stream.read(buf)) > 0) {
                if (count != buf.length) {
                    packet.setBody(Arrays.copyOfRange(buf, 0, count));
                } else {
                    packet.setBody(buf);
                }
                packet.write(oStream);

                packet = new Packet();
                packet.setBodyContinues(true);
            }

            // signal end
            packet.setBodyContinues(false);
            packet.write(oStream);

            Packet resp = Packet.read(iStream);
            return resp.getHeader("File-Hash").getValues().get(0);
        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
            return null;
        }
    }

    public File downloadImage(String hash) {
        File file = new File("tmp/" + hash);
        if (file.isFile()) {
            return file;
        }

        try {
            Packet packet = new Packet("/static/" + hash);
            packet.write(oStream);

            Packet dataPacket = Packet.read(iStream);

            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);

            while (true) {
                stream.write(dataPacket.getBody());

                if (!dataPacket.getBodyContinues()) {
                    break;
                }
                dataPacket = Packet.read(iStream);
            }

            return file;
        } catch (ErrorPacketException ignored) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
            return null;
        }
    }

    private void showError(String message) {
        if (showErrors) {
            JOptionPane.showMessageDialog(null, message, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public boolean isLoggedIn() {
        return !sessionToken.isEmpty();
    }
}
