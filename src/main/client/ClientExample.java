package client;

import packet.ErrorPacketException;
import packet.PacketParsingException;

import java.io.IOException;

public class ClientExample {
    public static void main(String[] args) throws IOException, PacketParsingException, ErrorPacketException {
        Client c = new Client("localhost", 8080);
        c.createUser("karma4", "12345");
        c.login("karma4", "12345");
        c.createListing("apple watch", "a apple watch", 100.25, "null");

        Client c2 = new Client("localhost", 8080);
        c2.createUser("testuser1", "12345");
        c2.login("testuser1", "12345");

        Client c3 = new Client("localhost", 8080);
        c3.createUser("testuser1", "12345");
        c3.login("testuser1", "12345");


    }
}
